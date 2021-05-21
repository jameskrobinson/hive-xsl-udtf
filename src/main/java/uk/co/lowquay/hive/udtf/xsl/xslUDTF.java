package uk.co.lowquay.hive.udtf.xsl;

import uk.co.lowquay.hive.udtf.xsl.internal.ObjectInspectors;
import uk.co.lowquay.hive.udtf.xsl.internal.Pair;
import uk.co.lowquay.hive.udtf.xsl.internal.ResultObjectMarshaller;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.*;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableConstantStringObjectInspector;
import org.apache.hadoop.io.Text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Description(name = "xsltable", value = "_FUNC_(XML, 'XSLT FILE', 'ARGS')")

public class xslUDTF extends GenericUDTF {

	static String HIVE_HEADER_START = "<!--HiveHeaders:";
	static String HIVE_HEADER_END = "-->";
	static String DEFAULT_COLUMN = "col1:string";
	private String ROW_SEP = "\n";
	private String ROW_SEP_PARAM = "ROW_SEP";
	private String COL_SEP = "\t";
	private String COL_SEP_PARAM = "COL_SEP";
	private String XSD_TO_XSL_TEMPLATE = "xsd_to_xsl.xsl";
	private String OUTPUT_METHOD_PARAM = "OUTPUT_METHOD";

	private boolean isDefault = false;

	private static Log log = LogFactory.getLog(xslUDTF.class);

	private transient StringObjectInspector in;
	private transient ResultObjectMarshaller marshaller;
	private transient Configuration config;
	private transient Processor processor;
	private transient XsltExecutable exp;
	private transient XsltCompiler compiler;
	private transient Serializer out;
	private transient StringWriter sw;
	private transient Xslt30Transformer trans;

	private transient boolean saxonInitComplete = false;
	private transient boolean transformParamsComplete = false;
	private transient boolean xsdInitComplete = false;

	private String stylesheet = "";
	private List<String> stylesheetParams = null;
	private String xsltFileName = "";

	private StructObjectInspector initialize(final ObjectInspector xmlArg, final ObjectInspector xsltFileArg, final List<ObjectInspector> xslParamArg) throws UDFArgumentException {

		this.in = Arguments.asString(xmlArg, "XML");
		xsltFileName = Arguments.asConstantNonNullString(xsltFileArg, "XSLT FILE");

		if (xslParamArg != null)
			stylesheetParams = Arguments.asConstantNonNullStrings(xslParamArg, "STYLESHEET PARAMETERS");

		//if the caller has supplied an XSD file instead of an XSL one, we have to pre-process this.
		if (xsltFileName.toLowerCase(Locale.ROOT).contains(".xsd")) {
			processXSDFile();
		}
		else {
			//normal initialization - get the contents of the supplied stylesheet file
			try {
				stylesheet = new String(Files.readAllBytes(Paths.get(xsltFileName)));
			} catch (java.io.IOException e) {
				throw new UDFArgumentException("Error reading XSLT file: " + xsltFileName + " : " + e.getMessage());
			}

			//Saxon setup:
			try {
				initSaxon();
			}
			catch (final SaxonApiException e) {
				throw new UDFArgumentException("Error initializing Saxon: " + e.getMessage() + " for xsl template file: " + xsltFileName);
			}
		}

		//Output column as types setup:
		List<String> hiveHeaderList = initHiveHeaderList();
		this.marshaller = ResultObjectMarshallers.create(hiveHeaderList);
		return marshaller.objectInspector();
	}

	private void initSaxon() throws SaxonApiException
	{
		if (saxonInitComplete == false) {
			log.info("Starting Saxon init");
			processor = new Processor(false);
			compiler = processor.newXsltCompiler();
			exp = compiler.compile(new StreamSource(new StringReader(stylesheet)));
			out = processor.newSerializer();
			sw = new StringWriter();
			out.setOutputWriter(sw);
			out.setOutputProperty(Serializer.Property.METHOD, "text");
			trans = exp.load30();

			trans.setErrorListener(new ErrorListener() {
				@Override
				public void warning(TransformerException exception) throws TransformerException {
					System.out.println(exception.getMessage());
					throw exception;
				}

				@Override
				public void fatalError(TransformerException exception) throws TransformerException {
					System.out.println(exception.getMessage());
					throw exception;
				}

				@Override
				public void error(TransformerException exception) throws TransformerException {
					System.out.println(exception.getMessage());
					throw exception;
				}
			});

			saxonInitComplete = true;
			log.info("Completed Saxon init");
		}
	}

	void processXSDFile () throws UDFArgumentException {

		if (!xsdInitComplete){

			String xsdFileContents = "";
			Path xsdFilePath = Paths.get(xsltFileName);
			Path xsdToXslFilePath = Paths.get(xsdFilePath.getParent().toString() + "/" + XSD_TO_XSL_TEMPLATE);

			try {
				xsdFileContents = new String(Files.readAllBytes(Paths.get(xsltFileName)));
				stylesheet = new String(Files.readAllBytes(xsdToXslFilePath));
			}
			catch (java.io.IOException e)
			{
				throw new UDFArgumentException("Error reading XSLT file: "+ xsltFileName + " : " + e.getMessage());
			}

			//init Saxon with this temporary XSL file
			try {
				initSaxon();
			}
			catch (final SaxonApiException e) {
				throw new UDFArgumentException("Error initializing Saxon: " + e.getMessage() + " for xsl template file: " + xsltFileName);
			}

			//the user must specify a NODE_LEVEL parameter, this puts that parameter in the Saxon params list...
			try {
				initTranformParams();
			}
			catch (final SaxonApiException | HiveException e) {
				throw new UDFArgumentException("Error setting transformation parameters : " + e.getMessage() + " for xsl template file: " + xsltFileName);
			}

			//get the contents of the XSD
			try {

				trans.transform(new StreamSource(new StringReader(xsdFileContents)), out);

				// and now assign this to be the actual stylesheet and re-initialize Saxon
				stylesheet = sw.toString();
				exp = compiler.compile(new StreamSource(new StringReader(stylesheet)));

				reInitOutputStream();
				trans = exp.load30();
				try
				{
					initTranformParams();
				}
				catch (final SaxonApiException | HiveException e) {
					throw new UDFArgumentException("Error setting transformation parameters : " + e.getMessage() + " for xsl template file: " + xsltFileName);
				}

			}
			catch (final SaxonApiException e) {
				throw new UDFArgumentException("Error initializing Saxon a second time: " + e.getMessage() + " for xsd template file: " + xsltFileName + " xsd to xsl template file: " + xsdToXslFilePath.toString());
			}
			xsdInitComplete = true;
		}
	}

	private void reInitOutputStream (){
		try {
			out.close();
		} catch (SaxonApiException e) {
			e.printStackTrace();
		}

		out = processor.newSerializer();
		sw = new StringWriter();
		out.setOutputWriter(sw);
		out.setOutputProperty(Serializer.Property.METHOD, "text");
	}

	private List<String> initHiveHeaderList() throws UDFArgumentException {

		//Pull column info - names and types - from the XSLT. This is expected to be in a comment that looks like this:
		// <!--HiveHeaders:intField:int, stringField:string -->
		// allowed types are int, bigint, float, double, boolean, string
		// collections, lists, etc are not supported
		// TODO - think about datetime fields - this must be possible somehow...?
		// Note, if no HiveHeader string is found, we assume a single column called col1 of type string

		String hiveHeader = "";
		List<String> hiveHeaderList = new ArrayList<>();

		int hiveHeaderStartPos = stylesheet.indexOf(HIVE_HEADER_START);
		if (hiveHeaderStartPos > -1)
		{
			int hiveHeaderEndPos = stylesheet.indexOf(HIVE_HEADER_END, hiveHeaderStartPos + 1);
			if (hiveHeaderEndPos > -1)
			{
				hiveHeader = stylesheet.substring(hiveHeaderStartPos + HIVE_HEADER_START.length(),hiveHeaderEndPos);
				String[] hiveHeaderArray = hiveHeader.replace(" ", "").split(",");
				hiveHeaderList = Arrays.asList(hiveHeaderArray);
			}
			else
			{
				throw new UDFArgumentException("Hive header in XSLT is malformed - can't find end tag");
			}
		}
		else
		{
			//no header found, default
			//when this happens, no delimiting of columns will take place!
			hiveHeaderList.add(DEFAULT_COLUMN);
			this.isDefault = true;
		}
		return hiveHeaderList;
	}

	@Override
	public StructObjectInspector initialize(ObjectInspector[] args) throws UDFArgumentException {
		if (args.length < 2)
			throw new UDFArgumentException("xsltable() takes at least two arguments.");
		else if (args.length == 2)
			return initialize(args[0], args[1], null);
		else
			return initialize(args[0], args[1], Arrays.asList(Arrays.copyOfRange(args, 2, args.length)));
	}

	@Override
	public void process(Object[] o) throws HiveException {
		final String xmlText = in.getPrimitiveJavaObject(o[0]);

		if (saxonInitComplete == false)
		{
			try {
				initSaxon();
			}
			catch (final SaxonApiException e) {
				throw new UDFArgumentException("Error initializing Saxon: " + e.getMessage() + " for xsl template file: " + xsltFileName);
			}
		}

		if (transformParamsComplete == false)
		{
			try {
				initTranformParams();
			}
			catch (final SaxonApiException e) {
				throw new UDFArgumentException("Error setting transformation parameters : " + e.getMessage() + " for xsl template file: " + xsltFileName);
			}
		}

		if (xmlText.isEmpty() == false) {
			String results = "";
			try {
				results = transformRow(xmlText);
			} catch (final SaxonApiException e) {
				throw new HiveException("Error transforming XML with Saxon: " + e.getMessage());
			}

			String rows[] = results.split(ROW_SEP);

			for (final String row : rows) {
				forward(marshaller.marshal(row, COL_SEP, this.isDefault));
			}
		}
		reInitOutputStream();
	}

	private void initTranformParams() throws HiveException, SaxonApiException {

		if (this.stylesheetParams != null) {

			//set any additional parameters (such as field / row separators, function names, etc)
			Map<QName, XdmValue> styleParams = new HashMap<QName, XdmValue>();

			for (final String param : this.stylesheetParams) {
				final String[] splitParams = param.split("=");
				if (splitParams.length != 2)
					throw new HiveException("Invalid parameter string - expected format is PARAM=VALUE: " + param);

				QName name = new QName(splitParams[0]);
				XdmValue value = XdmValue.makeValue(splitParams[1]);
				styleParams.put(name, value);

				//special cases - if we override the col sep and the row sep in the stylesheet
				//we override them here too, to allow the splitting to work/
				//if we want a different separator, and don't want the code to try to split the results,
				//use another parameter in your XSLT eg MY_COL_SEP

				//Likewise if we want to use an output method other than text (eg xml), then
				//cater for this here:

				if (splitParams[0].equals(COL_SEP_PARAM))
					COL_SEP = splitParams[1];
				else if (splitParams[0].equals(ROW_SEP_PARAM))
					ROW_SEP = splitParams[1];
				else if (splitParams[0].equals(OUTPUT_METHOD_PARAM))
					out.setOutputProperty(Serializer.Property.METHOD, splitParams[1]);
			}
			trans.setStylesheetParameters(styleParams);
		}
		transformParamsComplete = true;
	}


	private String transformRow(String xmlText) throws SaxonApiException{
		trans.transform(new StreamSource(new StringReader(xmlText)), out);
		return sw.toString();
	}

	@Override
	public String toString() {
		return "xsltable";
	}

	@Override
	public void close() throws HiveException {}

	private static class Arguments {
		public static String asConstantNonNullString(final ObjectInspector oi, final String name) throws UDFArgumentException {
			if (!(oi instanceof WritableConstantStringObjectInspector))
				throw new UDFArgumentException(name + " must be a constant string.");
			final Text text = ((WritableConstantStringObjectInspector) oi).getWritableConstantValue();
			if (text == null)
				throw new UDFArgumentException(name + " must not be NULL.");
			return text.toString();
		}

		public static List<String> asConstantNonNullStrings(final List<ObjectInspector> ois, final String name) throws UDFArgumentException {
			final List<String> strs = new ArrayList<>();
			for (final ObjectInspector oi : ois)
				strs.add(asConstantNonNullString(oi, name));
			return strs;
		}

		public static StringObjectInspector asString(final ObjectInspector oi, final String name) throws UDFArgumentException {
			if (!(oi instanceof StringObjectInspector))
				throw new UDFArgumentException(name + " must be of string type.");
			return (StringObjectInspector) oi;
		}
	}

	private static class ResultObjectMarshallers {
		private static final Pattern NAME_AND_TYPE_PATTERN = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*):[a-zA-Z_].*");

		private static Pair<String, ObjectInspector> parseNameAndType(final String nameAndType, final boolean requireName) throws UDFArgumentException {
			try {
				final Matcher m = NAME_AND_TYPE_PATTERN.matcher(nameAndType);
				if (m.matches()) {
					final String name = m.group(1);
					final ObjectInspector oi = ObjectInspectors.newObjectInspectorFromHiveType(nameAndType.substring(name.length() + 1));
					return Pair.of(name, oi);
				}
				if (requireName)
					throw new UDFArgumentException("Can't parse name:type from \"" + nameAndType + "\". NAME is required.");
				return Pair.of(null, ObjectInspectors.newObjectInspectorFromHiveType(nameAndType));
			} catch (final Exception e) {
				throw new UDFArgumentException("Can't parse name:type or TYPE from \"" + nameAndType + "\". " + e.getMessage());
			}
		}

		public static ResultObjectMarshaller create(final List<String> nameAndTypeArgs) throws UDFArgumentException {
			final List<String> columns = new ArrayList<>(nameAndTypeArgs.size());
			final List<ObjectInspector> inspectors = new ArrayList<>(nameAndTypeArgs.size());

			for (int i = 0; i < nameAndTypeArgs.size(); ++i) {
				final Pair<String, ObjectInspector> nameAndType = parseNameAndType(nameAndTypeArgs.get(i), i > 0);
				columns.add(nameAndType._1);
				inspectors.add(nameAndType._2);
			}

			//we always add these two columns...
			columns.add("udtfrowstatus");
			inspectors.add(ObjectInspectors.newObjectInspectorFromHiveType("int"));
			columns.add("udtfrownarrative");
			inspectors.add(ObjectInspectors.newObjectInspectorFromHiveType("string"));

			return new ResultObjectMarshaller(ObjectInspectorFactory.getStandardStructObjectInspector(columns, inspectors));
		}
	}
}
