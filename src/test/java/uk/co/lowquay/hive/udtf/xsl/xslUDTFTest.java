package uk.co.lowquay.hive.udtf.xsl;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.Collector;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

import org.apache.hadoop.io.Text;
import org.junit.Test;

import org.apache.hive.com.esotericsoftware.kryo.Kryo;
import org.apache.hive.com.esotericsoftware.kryo.io.Input;
import org.apache.hive.com.esotericsoftware.kryo.io.Output;

public class xslUDTFTest {

    private static List<Object> evaluate(final GenericUDTF udtf, final Object... ins) throws HiveException {
        final List<Object> out = new ArrayList<>();
        udtf.setCollector(new Collector() {
            @Override
            public void collect(Object input) throws HiveException {
                out.add(input);
            }
        });
        for (Object in : ins)
            udtf.process(new Object[] { in });
        return out;
    }

    private static ConstantObjectInspector toConstantOI(final String text) {
        return PrimitiveObjectInspectorFactory.getPrimitiveWritableConstantObjectInspector(TypeInfoFactory.stringTypeInfo, new Text(text));
    }

    private static Object toObject(final String text) {
        return PrimitiveObjectInspectorFactory.writableStringObjectInspector.create(text);
    }

    private String getResoucePath(final String fileName) {
        URL url = this.getClass().getResource(fileName);
        File tmpFile = new File(url.getFile());
        return tmpFile.getAbsolutePath();
    }

    private String getFileContent(final String fileName) {
        URL url = this.getClass().getResource(fileName);
        File tmpFile = new File(url.getFile());
        String fileContent = "";

        try {
            fileContent = new String(Files.readAllBytes(tmpFile.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    public static byte[] serializeObjectByKryo(Object obj) {
        Kryo kryo = new Kryo();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        kryo.writeObject(output, obj);
        output.close();
        return bos.toByteArray();
    }

    public static Object deserializeObjectByKryo(byte[] in, Class clazz) {
        Kryo kryo = new Kryo();
        Input inp = new Input(in);
        Object obj = kryo.readObject(inp, clazz);
        inp.close();
        return obj;
    }


    /*
    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }
    */



    @Test
    public void testSingleColumnNoHeaderXML() throws HiveException {

        final xslUDTF sut = new xslUDTF();
        String TEST_XML_FILE = "/xml/single_record.xml";
        String TEST_XSLT_FILE = "/xsl/tocsv_noheader.xsl";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        /*
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI("c:/tools/xsl/tocsv_noheader.xsl"),
                toConstantOI("COL_SEP=\"skippy\""), toConstantOI("ROW_SEP=\"flipper\"")
        });
         */

        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH)
        });

        assertEquals("struct<col1:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

        final List<Object> rows = evaluate(sut, toObject(TEST_XML));
        assertEquals(1, rows.size());
        assertEquals("123", new HivePath(oi, ".col1").extract(rows.get(0)).asString());
    }

    @Test
    public void testMultiRow() throws HiveException {
        final xslUDTF sut = new xslUDTF();

        String TEST_XML_FILE = "/xml/multirow.xml";
        String TEST_XSLT_FILE = "/xsl/tocsv.xsl";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);


        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                //toConstantOI("COL_SEP=\"skippy\""),
        });
        assertEquals("struct<intfield:int,stringfield:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

        final List<Object> results = evaluate(sut, toObject(TEST_XML));
        assertEquals(3, results.size());

        final HivePath namePath = new HivePath(oi, ".stringfield");
        final HivePath offsetPath = new HivePath(oi, ".intfield");

        assertEquals("hello", namePath.extract(results.get(0)).asString());
        assertEquals(123, offsetPath.extract(results.get(0)).asInt());

        assertEquals("goodbye", namePath.extract(results.get(1)).asString());
        assertEquals(456, offsetPath.extract(results.get(1)).asInt());

        assertEquals("really, goodbye", namePath.extract(results.get(2)).asString());
        assertEquals(789, offsetPath.extract(results.get(2)).asInt());
    }

    @Test
    public void testMultiRowCustomSep() throws HiveException {
        final xslUDTF sut = new xslUDTF();

        String TEST_XML_FILE = "/xml/multirow.xml";
        String TEST_XSLT_FILE = "/xsl/tocsv.xsl";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);


        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                toConstantOI("COL_SEP=\"skippy\""),
        });
        assertEquals("struct<intfield:int,stringfield:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

        final List<Object> results = evaluate(sut, toObject(TEST_XML));
        assertEquals(3, results.size());

        final HivePath namePath = new HivePath(oi, ".stringfield");
        final HivePath offsetPath = new HivePath(oi, ".intfield");

        assertEquals("hello", namePath.extract(results.get(0)).asString());
        assertEquals(123, offsetPath.extract(results.get(0)).asInt());

        assertEquals("goodbye", namePath.extract(results.get(1)).asString());
        assertEquals(456, offsetPath.extract(results.get(1)).asInt());

        assertEquals("really, goodbye", namePath.extract(results.get(2)).asString());
        assertEquals(789, offsetPath.extract(results.get(2)).asInt());
    }


    @Test
    public void testFPMLCashFlow() throws HiveException, IOException {

        //tests the extraction of multiple rows from a single XML file
        String TEST_XML_FILE = "/xml/ird-ex02-stub-amort-swap.xml";
        String TEST_XSLT_FILE = "/xsl/cashflow_to_tabulated.xsl";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                //toConstantOI("COL_SEP=\":\""),
        });
        assertEquals("struct<payerparty:string,eventdate:string,notional:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

        final List<Object> results = evaluate(sut, toObject(TEST_XML));
        assertEquals(15, results.size());

        final HivePath payerPath = new HivePath(oi, ".payerparty");
        final HivePath datePath = new HivePath(oi, ".eventdate");
        final HivePath notionalPath = new HivePath(oi, ".notional");

        assertEquals("party1", payerPath.extract(results.get(0)).asString());
        assertEquals("1995-06-14", datePath.extract(results.get(0)).asString());
        assertEquals("50000000.00", notionalPath.extract(results.get(0)).asString());

        assertEquals("party2", payerPath.extract(results.get(14)).asString());
        assertEquals("1999-12-14", datePath.extract(results.get(14)).asString());
        assertEquals("10000000.00", notionalPath.extract(results.get(14)).asString());
    }

    @Test
    public void testFPMLNullValue() throws HiveException, IOException {

        //tests the extraction of multiple rows from a single XML file. The final row has a null value

        String TEST_XML_FILE = "/xml/ird-ex02-stub-amort-swap-empty-value.xml";
        String TEST_XSLT_FILE = "/xsl/cashflow_to_tabulated.xsl";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                //toConstantOI("COL_SEP=\":\""),
        });
        assertEquals("struct<payerparty:string,eventdate:string,notional:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

        final List<Object> results = evaluate(sut, toObject(TEST_XML));
        assertEquals(15, results.size());

        final HivePath payerPath = new HivePath(oi, ".payerparty");
        final HivePath datePath = new HivePath(oi, ".eventdate");
        final HivePath notionalPath = new HivePath(oi, ".notional");

        assertEquals("party1", payerPath.extract(results.get(0)).asString());
        assertEquals("1995-06-14", datePath.extract(results.get(0)).asString());
        assertEquals("50000000.00", notionalPath.extract(results.get(0)).asString());

        assertEquals("party2", payerPath.extract(results.get(14)).asString());
        assertEquals("1999-12-14", datePath.extract(results.get(14)).asString());
        assertEquals("", notionalPath.extract(results.get(14)).asString());

    }

    @Test
    public void testTypes() throws HiveException {

        String TEST_XML_FILE = "/xml/multirow_multitype.xml";
        String TEST_XSLT_FILE = "/xsl/tocsv_types.xsl";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                //toConstantOI("COL_SEP=\":\""),
        });

        final List<Object> results = evaluate(sut, toObject(TEST_XML));

        assertEquals(3, results.size());

        final Object obj = results.get(0);
        assertEquals(12345, new HivePath(oi, ".intfield").extract(obj).asInt());
        assertEquals("hello", new HivePath(oi, ".stringfield").extract(obj).asString());
        assertEquals(0, Float.compare(0.1f, new HivePath(oi, ".floatfield").extract(obj).asFloat()));
        assertEquals(0, Double.compare(0.3, new HivePath(oi, ".doublefield").extract(obj).asDouble()));
        assertEquals(9223372036854775807L, new HivePath(oi, ".bigintfield").extract(obj).asLong());
        assertEquals(true, new HivePath(oi, ".booleanfield").extract(obj).asBoolean());
        assertEquals(0, new HivePath(oi, ".udtfrowstatus").extract(obj).asInt()); //this field shows there are no errors
        assertEquals("", new HivePath(oi, ".udtfrownarrative").extract(obj).asString()); //this field tells us what the error is, if any


    }

    @Test
    public void testNullPayload() throws HiveException {

        String TEST_XSLT_FILE = "/xsl/tocsv_types.xsl";
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                //toConstantOI("COL_SEP=\":\""),
        });

        final List<Object> nullResults = evaluate(sut, toObject(null));
        assertEquals(0, nullResults.size());

        final List<Object> emptyResults = evaluate(sut, toObject(null));
        assertEquals(0, emptyResults.size());
    }


    @Test
    public void testTypeConversionFailure() throws HiveException {

        String TEST_XML_FILE = "/xml/multirow_multitype_error.xml";
        String TEST_XSLT_FILE = "/xsl/tocsv_types.xsl";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                //toConstantOI("COL_SEP=\":\""),
        });

        final List<Object> results = evaluate(sut, toObject(TEST_XML));

        assertEquals(3, results.size());

        Object obj = results.get(0);
        assertEquals(12345, new HivePath(oi, ".intfield").extract(obj).asInt());
        assertEquals("hello", new HivePath(oi, ".stringfield").extract(obj).asString());
        assertEquals(0, Float.compare(0.0f, new HivePath(oi, ".floatfield").extract(obj).asFloat())); //error here
        assertEquals(0, Double.compare(0.3, new HivePath(oi, ".doublefield").extract(obj).asDouble()));
        assertEquals(9223372036854775807L, new HivePath(oi, ".bigintfield").extract(obj).asLong());
        assertEquals(false, new HivePath(oi, ".booleanfield").extract(obj).asBoolean());
        assertEquals(1, new HivePath(oi, ".udtfrowstatus").extract(obj).asInt()); //this field shows there an error
        assertEquals("1. Field: floatfield Value: eee Error: For input string: \"eee\" ", new HivePath(oi, ".udtfrownarrative").extract(obj).asString()); //this field tells us what the error is

        obj = results.get(1);
        assertEquals(45678, new HivePath(oi, ".intfield").extract(obj).asInt());
        assertEquals("goodbye", new HivePath(oi, ".stringfield").extract(obj).asString());
        assertEquals(0, Float.compare(0.0f, new HivePath(oi, ".floatfield").extract(obj).asFloat())); //error here
        assertEquals(0, Double.compare(0, new HivePath(oi, ".doublefield").extract(obj).asDouble())); //error here
        assertEquals(9223372036854775801L, new HivePath(oi, ".bigintfield").extract(obj).asLong());
        assertEquals(true, new HivePath(oi, ".booleanfield").extract(obj).asBoolean());
        assertEquals(2, new HivePath(oi, ".udtfrowstatus").extract(obj).asInt()); //this field shows there an error
        assertEquals("1. Field: floatfield Value: gotcha Error: For input string: \"gotcha\" 2. Field: doublefield Value: squiggle Error: For input string: \"squiggle\" ", new HivePath(oi, ".udtfrownarrative").extract(obj).asString()); //this field tells us what the error is

    }


    @Test
    public void testTypeConversionNull() throws HiveException {

        String TEST_XML_FILE = "/xml/multirow_multitype_null.xml";
        String TEST_XSLT_FILE = "/xsl/tocsv_types.xsl";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                //toConstantOI("COL_SEP=\":\""),
        });

        final List<Object> results = evaluate(sut, toObject(TEST_XML));

        assertEquals(3, results.size());

        final Object obj = results.get(0);
        assertEquals(12345, new HivePath(oi, ".intfield").extract(obj).asInt());
        assertEquals("hello", new HivePath(oi, ".stringfield").extract(obj).asString());
        assertEquals(0, Float.compare(0.0f, new HivePath(oi, ".floatfield").extract(obj).asFloat())); //null field, has been defaulted to zero
        assertEquals(0, Double.compare(0.3, new HivePath(oi, ".doublefield").extract(obj).asDouble()));
        assertEquals(9223372036854775807L, new HivePath(oi, ".bigintfield").extract(obj).asLong());
        assertEquals(true, new HivePath(oi, ".booleanfield").extract(obj).asBoolean());
        assertEquals(1, new HivePath(oi, ".udtfrowstatus").extract(obj).asInt()); //this field shows there an error
        assertEquals("1. Field: floatfield Value:  Error: empty String ", new HivePath(oi, ".udtfrownarrative").extract(obj).asString()); //this field tells us what the error is

    }
    //@Test - not a test for now, until I understand kryo a bit better
    public void testSerialize() throws HiveException {

        //same as testMultiRow above, but with a step to serialize / de-serialize.
        //This is because Hive will serialize the objects when farming them out to Tez (or whereever)

        try {

            final xslUDTF sut = new xslUDTF();

            String TEST_XML_FILE = "/xml/multirow.xml";
            String TEST_XSLT_FILE = "/xsl/tocsv.xsl";

            String TEST_XML = getFileContent(TEST_XML_FILE);
            String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

            final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                    PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                    toConstantOI(XSLT_FILE_PATH),
                    //toConstantOI("COL_SEP=\"skippy\""),
            });

            byte[] serialized = serializeObjectByKryo(sut);
            Object obj = deserializeObjectByKryo(serialized, sut.getClass());

            assertEquals("struct<intfield:int,stringfield:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

            final List<Object> results = evaluate(sut, toObject(TEST_XML));

            //byte[] serialized = serializeObjectByKryo(sut);
            //Object obj = deserializeObjectByKryo(serialized, sut.getClass());


            assertEquals(3, results.size());

            final HivePath namePath = new HivePath(oi, ".stringfield");
            final HivePath offsetPath = new HivePath(oi, ".intfield");

            assertEquals("hello", namePath.extract(results.get(0)).asString());
            assertEquals(123, offsetPath.extract(results.get(0)).asInt());

            assertEquals("goodbye", namePath.extract(results.get(1)).asString());
            assertEquals(456, offsetPath.extract(results.get(1)).asInt());

            assertEquals("really, goodbye", namePath.extract(results.get(2)).asString());
            assertEquals(789, offsetPath.extract(results.get(2)).asInt());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testXSDToXSL() throws HiveException, IOException {

        //Test that we can convert an XSD into an XSL and then use the resulting XSL to pull back data from XML!
        //Note that the xsd_to_xsl.xsl file must be in the same directory as the XSD

        String TEST_XML_FILE = "/xml/Summit/summit-swap-with-events.xml";
        String TEST_XSLT_FILE = "/xsd/Summit/swap-with-flows.xsd";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                toConstantOI("NODE_LEVEL=EVENT"),
        });
        assertEquals("struct<date:string,adate:string,linkev:string,type:string,ccy:string,amount:string,riskid:string,evstyle:string,bdate:string,cdate:string,eventcal:string,eventbdrule:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

        final List<Object> results = evaluate(sut, toObject(TEST_XML));
        assertEquals(58, results.size());

        final List<Object> results2 = evaluate(sut, toObject(TEST_XML));
        assertEquals(58, results2.size()); //do this again, just to make sure everything cleared down okay

    }

    @Test
    public void testXSDToXSLDeepCopy() throws HiveException, IOException {

        String TEST_XML_FILE = "/xml/Summit/summit-swap-with-events.xml";
        String TEST_XSLT_FILE = "/xsd/summit/swap-with-flows.xsd";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                toConstantOI("NODE_LEVEL=ASSET"),
                toConstantOI("DEEP_COPY=YES"),
        });
        assertEquals("struct<dmassetid:string,dmownertable:string,tradeid:string,schedevents:string,type:string,subtype:string,rc:string,book:string,pors:string,effdate:string,matdate:string,notional:string,notexp:string,ccy:string,stub_date1:string,stub_rate1:string,stub_date2:string,stub_rate2:string,stub_stubtype:string,stub_decomp:string,stub_compmode:string,stub_rounding:string,stub_interpstyle:string,stub_startterm1:string,stub_startterm2:string,stub_endterm1:string,stub_endterm2:string,resetinstub:string,interest_indexform:string,interest_yldfreq:string,interest_fixfloat:string,interest_rate:string,interest_basis:string,interest_dmindex:string,interest_ccy:string,int_ref_orgsource:string,int_ref_source:string,int_ref_nnearby:string,interest_spread:string,interest_term:string,int_avg_type:string,int_avg_freq:string,int_avg_compound:string,int_avg_annday:string,int_avg_rule:string,int_avg_spread:string,int_avg_time:string,int_avg_cutoffdays:string,int_avg_corbgap:string,interest_dtype:string,interest_round:string,interest_rounddp:string,int_res_rule:string,int_res_time:string,int_res_round:string,int_res_rounddp:string,int_res_ntl_ref_orgsource:string,int_res_ntl_ref_source:string,int_res_ntl_ref_nnearby:string,int_res_ntl_cal:string,int_res_ntl_term:string,int_res_fx_ref_orgsource:string,int_res_fx_ref_source:string,int_res_fx_ref_nnearby:string,int_res_fx_cal:string,int_res_fx_term:string,int_res_fxdomain:string,int_acc_acccalc:string,int_acc_method:string,int_acc_forl:string,int_acc_basis:string,interest_fwddecomp:string,int_div_ratio:string,int_div_reinvest:string,int_div_divmethod:string,interest_nrate:string,interest_noncontig:string,interest_underindex:string,sched_pay_freq:string,sched_pay_annday:string,sched_pay_rule:string,sched_pay_intrule:string,sched_pay_time:string,sched_pay_cal:string,sched_pay_drule:string,sched_reset_freq:string,sched_reset_annday:string,sched_reset_rule:string,sched_reset_intrule:string,sched_reset_time:string,sched_reset_cal:string,sched_reset_drule:string,comp_compfreq:string,comp_mode:string,amort_type:string,amort_amount:string,amort_start:string,amort_end:string,amort_freq:string,custom:string,cross_ccy_notexch:string,cross_ccy_forexrate:string,optiondata:string,pmodel:string,style:string,opform:string,subindex:string,termdate:string,roll_rollnature:string,roll_noticedays:string,roll_reinvestamt:string,fstcpeffect:string,nextfixing:string,riskid:string,int_avg_conversion:string,int_res_refixmode:string,int_avg_cutoffmeth:string,audit_version:string,amort_ntlform:string,amort_sched_freq:string,amort_sched_annday:string,amort_sched_rule:string,amort_sched_intrule:string,amort_sched_time:string,amort_sched_cal:string,amort_sched_druleid:string,interest_fundindex:string,interest_fundsprd:string,mmtype:string,legrole:string,groupnum:string,numeve:string,formula:string,events:string,cmdtyquotetype:string,int_div_paydividend:string,int_div_hedgedivratio:string,int_res_auxdomain:string,sched_pay_gapdrule:string,sched_reset_gapdrule:string,amort_sched_gapdruleid:string,amort_druleid:string,int_avg_gap:string,int_res_ntl_gap:string,int_res_fx_gap:string,sched_pay_gap:string,sched_reset_gap:string,amort_sched_gap:string,int_div_gap:string,productname:string,accrualfactors:string,stub_startcal:string,stub_endcal:string,stub_startsource1:string,stub_startsource2:string,stub_endsource1:string,stub_endsource2:string,int_interimround:string,int_interimrounddp:string,int_res_convround:string,int_res_convrounddp:string,int_res_paymentreset:string,int_res_cutoffdays:string,int_res_ntl_paymentreset:string,int_res_ntl_cutoffdays:string,int_res_fx_paymentreset:string,int_res_fx_cutoffdays:string,interest_orgindex:string,interest_orgccy:string,interest_orgsource:string,origmatdate:string,gapdateroll:string,int_acc_useproj:string,finntlexchcal:string,fincalatpay:string,deliverable:string,int_res_dorf:string,paymethod:string,redemformula:string,redemproductname:string,cfround:string,cfrounddp:string,interest_method:string,denom_type:string,denom_amount:string,denom_round:string,denom_rounddp:string,int_acc_ratetype:string,loanintrule:string,loanbasis:string,loanrate:string,loanstubtype:string,loanntlamorttype:string,payevents:string,adjfstxnl:string,freqdecomptype:string,freqdecompfreq:string,stubfreqdecomptype:string,stubfreqdecompfreq:string,stub_ignorehalfperiodrule:string,busmatdate:string,interest_intratetype:string,roll_rolloverstatus:string,siasset:string,notionaldaysgap:string,notionalgapdruleid:string,useintpaygaprules:string,proddata:string,redemproddata:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

        final List<Object> results = evaluate(sut, toObject(TEST_XML));
        assertEquals(2, results.size());

        final HivePath eventsPath = new HivePath(oi, ".events");
        assertEquals("<Events TYPE=", eventsPath.extract(results.get(0)).asString().toString().substring(0,13));

    }

    @Test
    public void testXSLDeepCopy() throws HiveException, IOException {

        String TEST_XML_FILE = "/xml/Summit/summit-swap-with-events.xml";
        String TEST_XSLT_FILE = "/xsl/ASSET_to_csv_auto_real.xsl";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                toConstantOI("DEEP_COPY=YES"),
                //toConstantOI("DEEP_COPY='NO'"),
        });
        assertEquals("struct<dmassetid:string,dmownertable:string,tradeid:string,schedevents:string,type:string,subtype:string,rc:string,book:string,pors:string,effdate:string,matdate:string,notional:string,notexp:string,ccy:string,stub_date1:string,stub_rate1:string,stub_date2:string,stub_rate2:string,stub_stubtype:string,stub_decomp:string,stub_compmode:string,stub_rounding:string,stub_interpstyle:string,stub_startterm1:string,stub_startterm2:string,stub_endterm1:string,stub_endterm2:string,resetinstub:string,interest_indexform:string,interest_yldfreq:string,interest_fixfloat:string,interest_rate:string,interest_basis:string,interest_dmindex:string,interest_ccy:string,int_ref_orgsource:string,int_ref_source:string,int_ref_nnearby:string,interest_spread:string,interest_term:string,int_avg_type:string,int_avg_freq:string,int_avg_compound:string,int_avg_annday:string,int_avg_rule:string,int_avg_spread:string,int_avg_time:string,int_avg_cutoffdays:string,int_avg_corbgap:string,interest_dtype:string,interest_round:string,interest_rounddp:string,int_res_rule:string,int_res_time:string,int_res_round:string,int_res_rounddp:string,int_res_ntl_ref_orgsource:string,int_res_ntl_ref_source:string,int_res_ntl_ref_nnearby:string,int_res_ntl_cal:string,int_res_ntl_term:string,int_res_fx_ref_orgsource:string,int_res_fx_ref_source:string,int_res_fx_ref_nnearby:string,int_res_fx_cal:string,int_res_fx_term:string,int_res_fxdomain:string,int_acc_acccalc:string,int_acc_method:string,int_acc_forl:string,int_acc_basis:string,interest_fwddecomp:string,int_div_ratio:string,int_div_reinvest:string,int_div_divmethod:string,interest_nrate:string,interest_noncontig:string,interest_underindex:string,sched_pay_freq:string,sched_pay_annday:string,sched_pay_rule:string,sched_pay_intrule:string,sched_pay_time:string,sched_pay_cal:string,sched_pay_drule:string,sched_reset_freq:string,sched_reset_annday:string,sched_reset_rule:string,sched_reset_intrule:string,sched_reset_time:string,sched_reset_cal:string,sched_reset_drule:string,comp_compfreq:string,comp_mode:string,amort_type:string,amort_amount:string,amort_start:string,amort_end:string,amort_freq:string,custom:string,cross_ccy_notexch:string,cross_ccy_forexrate:string,optiondata:string,pmodel:string,style:string,opform:string,subindex:string,termdate:string,roll_rollnature:string,roll_noticedays:string,roll_reinvestamt:string,fstcpeffect:string,nextfixing:string,riskid:string,int_avg_conversion:string,int_res_refixmode:string,int_avg_cutoffmeth:string,audit_version:string,amort_ntlform:string,amort_sched_freq:string,amort_sched_annday:string,amort_sched_rule:string,amort_sched_intrule:string,amort_sched_time:string,amort_sched_cal:string,amort_sched_druleid:string,interest_fundindex:string,interest_fundsprd:string,mmtype:string,legrole:string,groupnum:string,numeve:string,formula:string,events:string,cmdtyquotetype:string,int_div_paydividend:string,int_div_hedgedivratio:string,int_res_auxdomain:string,sched_pay_gapdrule:string,sched_reset_gapdrule:string,amort_sched_gapdruleid:string,amort_druleid:string,int_avg_gap:string,int_res_ntl_gap:string,int_res_fx_gap:string,sched_pay_gap:string,sched_reset_gap:string,amort_sched_gap:string,int_div_gap:string,productname:string,accrualfactors:string,stub_startcal:string,stub_endcal:string,stub_startsource1:string,stub_startsource2:string,stub_endsource1:string,stub_endsource2:string,int_interimround:string,int_interimrounddp:string,int_res_convround:string,int_res_convrounddp:string,int_res_paymentreset:string,int_res_cutoffdays:string,int_res_ntl_paymentreset:string,int_res_ntl_cutoffdays:string,int_res_fx_paymentreset:string,int_res_fx_cutoffdays:string,interest_orgindex:string,interest_orgccy:string,interest_orgsource:string,origmatdate:string,gapdateroll:string,int_acc_useproj:string,finntlexchcal:string,fincalatpay:string,deliverable:string,int_res_dorf:string,paymethod:string,redemformula:string,redemproductname:string,cfround:string,cfrounddp:string,interest_method:string,denom_type:string,denom_amount:string,denom_round:string,denom_rounddp:string,int_acc_ratetype:string,loanintrule:string,loanbasis:string,loanrate:string,loanstubtype:string,loanntlamorttype:string,payevents:string,adjfstxnl:string,freqdecomptype:string,freqdecompfreq:string,stubfreqdecomptype:string,stubfreqdecompfreq:string,stub_ignorehalfperiodrule:string,busmatdate:string,interest_intratetype:string,roll_rolloverstatus:string,siasset:string,notionaldaysgap:string,notionalgapdruleid:string,useintpaygaprules:string,cotherasset:string,cenv:string,cback:string,cbreaks:string,proddata:string,redemproddata:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

        final List<Object> results = evaluate(sut, toObject(TEST_XML));
        assertEquals(2, results.size());

        final HivePath eventsPath = new HivePath(oi, ".events");
        assertEquals("<Events TYPE=", eventsPath.extract(results.get(0)).asString().toString().substring(0,13));

        final List<Object> results2 = evaluate(sut, toObject(TEST_XML));
        assertEquals(2, results2.size()); //do this again, just to make sure everything cleared down okay

    }

    @Test
    public void testXSLNoDeepCopy() throws HiveException, IOException {

        String TEST_XML_FILE = "/xml/Summit/summit-swap-with-events.xml";
        String TEST_XSLT_FILE = "/xsl/ASSET_to_csv_auto_real.xsl";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                toConstantOI("DEEP_COPY=NO"),
        });
        assertEquals("struct<dmassetid:string,dmownertable:string,tradeid:string,schedevents:string,type:string,subtype:string,rc:string,book:string,pors:string,effdate:string,matdate:string,notional:string,notexp:string,ccy:string,stub_date1:string,stub_rate1:string,stub_date2:string,stub_rate2:string,stub_stubtype:string,stub_decomp:string,stub_compmode:string,stub_rounding:string,stub_interpstyle:string,stub_startterm1:string,stub_startterm2:string,stub_endterm1:string,stub_endterm2:string,resetinstub:string,interest_indexform:string,interest_yldfreq:string,interest_fixfloat:string,interest_rate:string,interest_basis:string,interest_dmindex:string,interest_ccy:string,int_ref_orgsource:string,int_ref_source:string,int_ref_nnearby:string,interest_spread:string,interest_term:string,int_avg_type:string,int_avg_freq:string,int_avg_compound:string,int_avg_annday:string,int_avg_rule:string,int_avg_spread:string,int_avg_time:string,int_avg_cutoffdays:string,int_avg_corbgap:string,interest_dtype:string,interest_round:string,interest_rounddp:string,int_res_rule:string,int_res_time:string,int_res_round:string,int_res_rounddp:string,int_res_ntl_ref_orgsource:string,int_res_ntl_ref_source:string,int_res_ntl_ref_nnearby:string,int_res_ntl_cal:string,int_res_ntl_term:string,int_res_fx_ref_orgsource:string,int_res_fx_ref_source:string,int_res_fx_ref_nnearby:string,int_res_fx_cal:string,int_res_fx_term:string,int_res_fxdomain:string,int_acc_acccalc:string,int_acc_method:string,int_acc_forl:string,int_acc_basis:string,interest_fwddecomp:string,int_div_ratio:string,int_div_reinvest:string,int_div_divmethod:string,interest_nrate:string,interest_noncontig:string,interest_underindex:string,sched_pay_freq:string,sched_pay_annday:string,sched_pay_rule:string,sched_pay_intrule:string,sched_pay_time:string,sched_pay_cal:string,sched_pay_drule:string,sched_reset_freq:string,sched_reset_annday:string,sched_reset_rule:string,sched_reset_intrule:string,sched_reset_time:string,sched_reset_cal:string,sched_reset_drule:string,comp_compfreq:string,comp_mode:string,amort_type:string,amort_amount:string,amort_start:string,amort_end:string,amort_freq:string,custom:string,cross_ccy_notexch:string,cross_ccy_forexrate:string,optiondata:string,pmodel:string,style:string,opform:string,subindex:string,termdate:string,roll_rollnature:string,roll_noticedays:string,roll_reinvestamt:string,fstcpeffect:string,nextfixing:string,riskid:string,int_avg_conversion:string,int_res_refixmode:string,int_avg_cutoffmeth:string,audit_version:string,amort_ntlform:string,amort_sched_freq:string,amort_sched_annday:string,amort_sched_rule:string,amort_sched_intrule:string,amort_sched_time:string,amort_sched_cal:string,amort_sched_druleid:string,interest_fundindex:string,interest_fundsprd:string,mmtype:string,legrole:string,groupnum:string,numeve:string,formula:string,events:string,cmdtyquotetype:string,int_div_paydividend:string,int_div_hedgedivratio:string,int_res_auxdomain:string,sched_pay_gapdrule:string,sched_reset_gapdrule:string,amort_sched_gapdruleid:string,amort_druleid:string,int_avg_gap:string,int_res_ntl_gap:string,int_res_fx_gap:string,sched_pay_gap:string,sched_reset_gap:string,amort_sched_gap:string,int_div_gap:string,productname:string,accrualfactors:string,stub_startcal:string,stub_endcal:string,stub_startsource1:string,stub_startsource2:string,stub_endsource1:string,stub_endsource2:string,int_interimround:string,int_interimrounddp:string,int_res_convround:string,int_res_convrounddp:string,int_res_paymentreset:string,int_res_cutoffdays:string,int_res_ntl_paymentreset:string,int_res_ntl_cutoffdays:string,int_res_fx_paymentreset:string,int_res_fx_cutoffdays:string,interest_orgindex:string,interest_orgccy:string,interest_orgsource:string,origmatdate:string,gapdateroll:string,int_acc_useproj:string,finntlexchcal:string,fincalatpay:string,deliverable:string,int_res_dorf:string,paymethod:string,redemformula:string,redemproductname:string,cfround:string,cfrounddp:string,interest_method:string,denom_type:string,denom_amount:string,denom_round:string,denom_rounddp:string,int_acc_ratetype:string,loanintrule:string,loanbasis:string,loanrate:string,loanstubtype:string,loanntlamorttype:string,payevents:string,adjfstxnl:string,freqdecomptype:string,freqdecompfreq:string,stubfreqdecomptype:string,stubfreqdecompfreq:string,stub_ignorehalfperiodrule:string,busmatdate:string,interest_intratetype:string,roll_rolloverstatus:string,siasset:string,notionaldaysgap:string,notionalgapdruleid:string,useintpaygaprules:string,cotherasset:string,cenv:string,cback:string,cbreaks:string,proddata:string,redemproddata:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

        final List<Object> results = evaluate(sut, toObject(TEST_XML));
        assertEquals(2, results.size());

        final HivePath eventsPath = new HivePath(oi, ".events");
        assertEquals("", eventsPath.extract(results.get(0)).asString());

    }

    @Test
    public void testXSDToXSLFPML() throws HiveException, IOException {

        String TEST_XML_FILE = "/xml/ird-ex02-stub-amort-swap.xml";
        String TEST_XSLT_FILE = "/xsd/fpml/fpml_ird_complete.xsd";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                toConstantOI("NODE_LEVEL=swapStream"),
                toConstantOI("NODE_TYPE=InterestRateStream"),
                toConstantOI("DEEP_COPY=YES"),
        });
        assertEquals("struct<payerpartyreference_href:string,payeraccountreference_href:string,receiverpartyreference_href:string,receiveraccountreference_href:string,calculationperioddates_effectivedate_unadjusteddate_unadjusteddate:string,calculationperioddates_effectivedate_unadjusteddate_id:string,calculationperioddates_effectivedate_dateadjustments_businessdayconvention:string,calculationperioddates_effectivedate_dateadjustments_businesscentersreference_href:string,calculationperioddates_effectivedate_dateadjustments_businesscenters_businesscenter:string,calculationperioddates_effectivedate_adjusteddate_adjusteddate:string,calculationperioddates_effectivedate_adjusteddate_id:string,calculationperioddates_relativeeffectivedate_relativedateadjustments_businessdayconvention:string,calculationperioddates_relativeeffectivedate_relativedateadjustments_businesscentersreference_href:string,calculationperioddates_relativeeffectivedate_relativedateadjustments_businesscenters_businesscenter:string,calculationperioddates_terminationdate_unadjusteddate_unadjusteddate:string,calculationperioddates_terminationdate_unadjusteddate_id:string,calculationperioddates_terminationdate_dateadjustments_businessdayconvention:string,calculationperioddates_terminationdate_dateadjustments_businesscentersreference_href:string,calculationperioddates_terminationdate_dateadjustments_businesscenters_businesscenter:string,calculationperioddates_terminationdate_adjusteddate_adjusteddate:string,calculationperioddates_terminationdate_adjusteddate_id:string,calculationperioddates_relativeterminationdate_businessdayconvention:string,calculationperioddates_relativeterminationdate_daterelativeto_href:string,calculationperioddates_relativeterminationdate_adjusteddate_adjusteddate:string,calculationperioddates_relativeterminationdate_adjusteddate_id:string,calculationperioddates_relativeterminationdate_businesscentersreference_href:string,calculationperioddates_relativeterminationdate_businesscenters_businesscenter:string,calculationperioddates_calculationperioddatesadjustments_businessdayconvention:string,calculationperioddates_calculationperioddatesadjustments_businesscentersreference_href:string,calculationperioddates_calculationperioddatesadjustments_businesscenters_businesscenter:string,calculationperioddates_firstperiodstartdate_unadjusteddate_unadjusteddate:string,calculationperioddates_firstperiodstartdate_unadjusteddate_id:string,calculationperioddates_firstperiodstartdate_dateadjustments_businessdayconvention:string,calculationperioddates_firstperiodstartdate_dateadjustments_businesscentersreference_href:string,calculationperioddates_firstperiodstartdate_dateadjustments_businesscenters_businesscenter:string,calculationperioddates_firstperiodstartdate_adjusteddate_adjusteddate:string,calculationperioddates_firstperiodstartdate_adjusteddate_id:string,calculationperioddates_firstregularperiodstartdate:string,calculationperioddates_firstcompoundingperiodenddate:string,calculationperioddates_lastregularperiodenddate:string,calculationperioddates_stubperiodtype:string,calculationperioddates_calculationperiodfrequency_rollconvention:string,paymentdates_calculationperioddatesreference_href:string,paymentdates_resetdatesreference_href:string,paymentdates_valuationdatesreference_href:string,paymentdates_paymentfrequency_periodmultiplier:string,paymentdates_paymentfrequency_period:string,paymentdates_firstpaymentdate:string,paymentdates_lastregularpaymentdate:string,paymentdates_payrelativeto:string,paymentdates_paymentdaysoffset_daytype:string,paymentdates_paymentdatesadjustments_businessdayconvention:string,paymentdates_paymentdatesadjustments_businesscentersreference_href:string,paymentdates_paymentdatesadjustments_businesscenters_businesscenter:string,resetdates_calculationperioddatesreference_href:string,resetdates_resetrelativeto:string,resetdates_initialfixingdate_businessdayconvention:string,resetdates_initialfixingdate_daterelativeto_href:string,resetdates_initialfixingdate_adjusteddate_adjusteddate:string,resetdates_initialfixingdate_adjusteddate_id:string,resetdates_initialfixingdate_businesscentersreference_href:string,resetdates_initialfixingdate_businesscenters_businesscenter:string,resetdates_fixingdates_businessdayconvention:string,resetdates_fixingdates_daterelativeto_href:string,resetdates_fixingdates_adjusteddate_adjusteddate:string,resetdates_fixingdates_adjusteddate_id:string,resetdates_fixingdates_businesscentersreference_href:string,resetdates_fixingdates_businesscenters_businesscenter:string,resetdates_ratecutoffdaysoffset_daytype:string,resetdates_resetfrequency_weeklyrollconvention:string,resetdates_resetdatesadjustments_businessdayconvention:string,resetdates_resetdatesadjustments_businesscentersreference_href:string,resetdates_resetdatesadjustments_businesscenters_businesscenter:string,calculationperiodamount_calculation_notionalschedule_notionalstepschedule_currency_currency:string,calculationperiodamount_calculation_notionalschedule_notionalstepschedule_currency_currencyscheme:string,calculationperiodamount_calculation_notionalschedule_notionalstepparameters_calculationperioddatesreference_href:string,calculationperiodamount_calculation_notionalschedule_notionalstepparameters_stepfrequency_periodmultiplier:string,calculationperiodamount_calculation_notionalschedule_notionalstepparameters_stepfrequency_period:string,calculationperiodamount_calculation_notionalschedule_notionalstepparameters_firstnotionalstepdate:string,calculationperiodamount_calculation_notionalschedule_notionalstepparameters_lastnotionalstepdate:string,calculationperiodamount_calculation_notionalschedule_notionalstepparameters_notionalstepamount:string,calculationperiodamount_calculation_notionalschedule_notionalstepparameters_notionalsteprate:string,calculationperiodamount_calculation_notionalschedule_notionalstepparameters_steprelativeto:string,calculationperiodamount_calculation_fxlinkednotionalschedule_constantnotionalschedulereference_href:string,calculationperiodamount_calculation_fxlinkednotionalschedule_initialvalue:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalcurrency_varyingnotionalcurrency:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalcurrency_currencyscheme:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalfixingdates_businessdayconvention:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalfixingdates_daterelativeto_href:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalfixingdates_adjusteddate_adjusteddate:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalfixingdates_adjusteddate_id:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalfixingdates_businesscentersreference_href:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalfixingdates_businesscenters_businesscenter:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_primaryratesource_ratesource_ratesource:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_primaryratesource_ratesource_informationproviderscheme:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_primaryratesource_ratesourcepage_ratesourcepage:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_primaryratesource_ratesourcepage_ratesourcepagescheme:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_primaryratesource_ratesourcepageheading:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_secondaryratesource_ratesource_ratesource:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_secondaryratesource_ratesource_informationproviderscheme:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_secondaryratesource_ratesourcepage_ratesourcepage:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_secondaryratesource_ratesourcepage_ratesourcepagescheme:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_secondaryratesource_ratesourcepageheading:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_fixingtime_hourminutetime:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_fixingtime_businesscenter_businesscenter:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_fixingtime_businesscenter_businesscenterscheme:string,calculationperiodamount_calculation_fxlinkednotionalschedule_fxspotratesource_fixingtime_businesscenter_id:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalinterimexchangepaymentdates_businessdayconvention:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalinterimexchangepaymentdates_daterelativeto_href:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalinterimexchangepaymentdates_adjusteddate_adjusteddate:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalinterimexchangepaymentdates_adjusteddate_id:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalinterimexchangepaymentdates_businesscentersreference_href:string,calculationperiodamount_calculation_fxlinkednotionalschedule_varyingnotionalinterimexchangepaymentdates_businesscenters_businesscenter:string,calculationperiodamount_calculation_fixedrateschedule_initialvalue:string,calculationperiodamount_calculation_fixedrateschedule_step:string,calculationperiodamount_calculation_futurevaluenotional_calculationperiodnumberofdays:string,calculationperiodamount_calculation_futurevaluenotional_valuedate:string,calculationperiodamount_calculation_daycountfraction_daycountfraction:string,calculationperiodamount_calculation_daycountfraction_daycountfractionscheme:string,calculationperiodamount_calculation_discounting_discountingtype:string,calculationperiodamount_calculation_discounting_discountrate:string,calculationperiodamount_calculation_discounting_discountratedaycountfraction_discountratedaycountfraction:string,calculationperiodamount_calculation_discounting_discountratedaycountfraction_daycountfractionscheme:string,calculationperiodamount_calculation_compoundingmethod:string,calculationperiodamount_knownamountschedule_currency_currency:string,calculationperiodamount_knownamountschedule_currency_currencyscheme:string,stubcalculationperiodamount_calculationperioddatesreference_href:string,stubcalculationperiodamount_initialstub_floatingrate_floatingratemultiplierschedule_initialvalue:string,stubcalculationperiodamount_initialstub_floatingrate_floatingratemultiplierschedule_step:string,stubcalculationperiodamount_initialstub_floatingrate_spreadschedule:string,stubcalculationperiodamount_initialstub_floatingrate_ratetreatment:string,stubcalculationperiodamount_initialstub_floatingrate_caprateschedule:string,stubcalculationperiodamount_initialstub_floatingrate_floorrateschedule:string,stubcalculationperiodamount_initialstub_floatingrate_capfloorstraddle:string,stubcalculationperiodamount_initialstub_floatingrate_floatingrateindex_floatingrateindex:string,stubcalculationperiodamount_initialstub_floatingrate_floatingrateindex_floatingrateindexscheme:string,stubcalculationperiodamount_initialstub_floatingrate_indextenor_periodmultiplier:string,stubcalculationperiodamount_initialstub_floatingrate_indextenor_period:string,stubcalculationperiodamount_initialstub_stubrate:string,stubcalculationperiodamount_initialstub_stubamount_amount:string,stubcalculationperiodamount_finalstub_floatingrate_floatingratemultiplierschedule_initialvalue:string,stubcalculationperiodamount_finalstub_floatingrate_floatingratemultiplierschedule_step:string,stubcalculationperiodamount_finalstub_floatingrate_spreadschedule:string,stubcalculationperiodamount_finalstub_floatingrate_ratetreatment:string,stubcalculationperiodamount_finalstub_floatingrate_caprateschedule:string,stubcalculationperiodamount_finalstub_floatingrate_floorrateschedule:string,stubcalculationperiodamount_finalstub_floatingrate_capfloorstraddle:string,stubcalculationperiodamount_finalstub_floatingrate_floatingrateindex_floatingrateindex:string,stubcalculationperiodamount_finalstub_floatingrate_floatingrateindex_floatingrateindexscheme:string,stubcalculationperiodamount_finalstub_floatingrate_indextenor_periodmultiplier:string,stubcalculationperiodamount_finalstub_floatingrate_indextenor_period:string,stubcalculationperiodamount_finalstub_stubrate:string,stubcalculationperiodamount_finalstub_stubamount_amount:string,principalexchanges_initialexchange:string,principalexchanges_finalexchange:string,principalexchanges_intermediateexchange:string,cashflows_cashflowsmatchparameters:string,cashflows_principalexchange:string,cashflows_paymentcalculationperiod:string,settlementprovision_settlementcurrency_settlementcurrency:string,settlementprovision_settlementcurrency_currencyscheme:string,settlementprovision_nondeliverablesettlement_referencecurrency_referencecurrency:string,settlementprovision_nondeliverablesettlement_referencecurrency_currencyscheme:string,settlementprovision_nondeliverablesettlement_fxfixingdate_businessdayconvention:string,settlementprovision_nondeliverablesettlement_fxfixingdate_daterelativetopaymentdates_paymentdatesreference:string,settlementprovision_nondeliverablesettlement_fxfixingdate_daterelativetocalculationperioddates_calculationperioddatesreference:string,settlementprovision_nondeliverablesettlement_fxfixingdate_businesscentersreference_href:string,settlementprovision_nondeliverablesettlement_fxfixingdate_businesscenters_businesscenter:string,settlementprovision_nondeliverablesettlement_fxfixingschedule_unadjusteddate:string,settlementprovision_nondeliverablesettlement_fxfixingschedule_dateadjustments_businessdayconvention:string,settlementprovision_nondeliverablesettlement_fxfixingschedule_dateadjustments_businesscentersreference_href:string,settlementprovision_nondeliverablesettlement_fxfixingschedule_dateadjustments_businesscenters_businesscenter:string,settlementprovision_nondeliverablesettlement_fxfixingschedule_adjusteddate:string,settlementprovision_nondeliverablesettlement_settlementrateoption_settlementrateoption:string,settlementprovision_nondeliverablesettlement_settlementrateoption_settlementrateoptionscheme:string,settlementprovision_nondeliverablesettlement_pricesourcedisruption_fallbackreferenceprice_valuationpostponement_maximumdaysofpostponement:string,settlementprovision_nondeliverablesettlement_pricesourcedisruption_fallbackreferenceprice_fallbacksettlementrateoption:string,settlementprovision_nondeliverablesettlement_pricesourcedisruption_fallbackreferenceprice_calculationagentdetermination_calculationagentpartyreference:string,settlementprovision_nondeliverablesettlement_pricesourcedisruption_fallbackreferenceprice_calculationagentdetermination_calculationagentparty:string,formula_formuladescription:string,formula_formulacomponent:string,dummycol:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

        final List<Object> results = evaluate(sut, toObject(TEST_XML));
        assertEquals(2, results.size());

        final HivePath eventsPath = new HivePath(oi, ".calculationPeriodDates_terminationDate_dateAdjustments_businessCenters_businessCenter");
        assertEquals("<businessCent", eventsPath.extract(results.get(0)).asString().toString().substring(0,13));

    }

    @Test
    public void testXSDToXSLISO() throws HiveException, IOException {

        String TEST_XML_FILE = "/xml/camt.053_P_CH0309000000250090342_380000000_0_2016053100163801.xml";
        String TEST_XSLT_FILE = "/xsd/iso20022/camt.053.001.02.xsd";

        String TEST_XML = getFileContent(TEST_XML_FILE);
        String XSLT_FILE_PATH = getResoucePath(TEST_XSLT_FILE);

        final xslUDTF sut = new xslUDTF();
        final StructObjectInspector oi = sut.initialize(new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.writableStringObjectInspector,
                toConstantOI(XSLT_FILE_PATH),
                toConstantOI("NODE_LEVEL=Ntry"),
                toConstantOI("NODE_TYPE=ReportEntry2"),
                toConstantOI("DEEP_COPY=YES"),
        });
        assertEquals("struct<ntryref:string,amt:string,amt_ccy:string,cdtdbtind:string,rvslind:string,sts:string,bookgdt_dt:string,bookgdt_dttm:string,valdt_dt:string,valdt_dttm:string,acctsvcrref:string,avlbty:string,bktxcd_domn_cd:string,bktxcd_domn_fmly_cd:string,bktxcd_domn_fmly_subfmlycd:string,bktxcd_prtry_cd:string,bktxcd_prtry_issr:string,comssnwvrind:string,addtlinfind_msgnmid:string,addtlinfind_msgid:string,amtdtls_instdamt_amt:string,amtdtls_instdamt_amt_ccy:string,amtdtls_instdamt_ccyxchg_srcccy:string,amtdtls_instdamt_ccyxchg_trgtccy:string,amtdtls_instdamt_ccyxchg_unitccy:string,amtdtls_instdamt_ccyxchg_xchgrate:string,amtdtls_instdamt_ccyxchg_ctrctid:string,amtdtls_instdamt_ccyxchg_qtndt:string,amtdtls_txamt_amt:string,amtdtls_txamt_amt_ccy:string,amtdtls_txamt_ccyxchg_srcccy:string,amtdtls_txamt_ccyxchg_trgtccy:string,amtdtls_txamt_ccyxchg_unitccy:string,amtdtls_txamt_ccyxchg_xchgrate:string,amtdtls_txamt_ccyxchg_ctrctid:string,amtdtls_txamt_ccyxchg_qtndt:string,amtdtls_cntrvalamt_amt:string,amtdtls_cntrvalamt_amt_ccy:string,amtdtls_cntrvalamt_ccyxchg_srcccy:string,amtdtls_cntrvalamt_ccyxchg_trgtccy:string,amtdtls_cntrvalamt_ccyxchg_unitccy:string,amtdtls_cntrvalamt_ccyxchg_xchgrate:string,amtdtls_cntrvalamt_ccyxchg_ctrctid:string,amtdtls_cntrvalamt_ccyxchg_qtndt:string,amtdtls_anncdpstngamt_amt:string,amtdtls_anncdpstngamt_amt_ccy:string,amtdtls_anncdpstngamt_ccyxchg_srcccy:string,amtdtls_anncdpstngamt_ccyxchg_trgtccy:string,amtdtls_anncdpstngamt_ccyxchg_unitccy:string,amtdtls_anncdpstngamt_ccyxchg_xchgrate:string,amtdtls_anncdpstngamt_ccyxchg_ctrctid:string,amtdtls_anncdpstngamt_ccyxchg_qtndt:string,amtdtls_prtryamt:string,chrgs:string,techinptchanl_cd:string,techinptchanl_prtry:string,intrst:string,ntrydtls:string,addtlntryinf:string,dummycol:string,udtfrowstatus:int,udtfrownarrative:string>", oi.getTypeName());

        final List<Object> results = evaluate(sut, toObject(TEST_XML));
        assertEquals(13, results.size());

        final HivePath eventsPath = new HivePath(oi, ".acctsvcrref");
        assertEquals("20160530007602769939022000000012", eventsPath.extract(results.get(6)).asString());

    }



}


