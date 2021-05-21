package uk.co.lowquay.hive.udtf.xsl.internal;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.*;

public class ResultObjectMarshaller {
	private final StructObjectInspector oi;

	public ResultObjectMarshaller(final StructObjectInspector oi) {
		this.oi = oi;
	}

	public StructObjectInspector objectInspector() {
		return oi;
	}

	public Object marshal(final String row, final String columnSep, final boolean isDefault) throws HiveException {
		String colArray[] = ((isDefault == false) ? row.split(columnSep, -1) : new String[]{row});

		final StandardStructObjectInspector structInspector = (StandardStructObjectInspector) oi;
		final Object out = structInspector.create();
		int i = 0;
		int errorCount = 0;
		String errorNarrative = "";

		try {
			for (final StructField field : structInspector.getAllStructFieldRefs()) {

				final ObjectInspector iface = field.getFieldObjectInspector();

				if (field.getFieldName() == "udtfrowstatus") {
					final WritableIntObjectInspector inspector = (WritableIntObjectInspector) iface;
					structInspector.setStructFieldData(out, field, inspector.create(errorCount));
				}
				else if (field.getFieldName() == "udtfrownarrative") {
					final WritableStringObjectInspector inspector = (WritableStringObjectInspector) iface;
					structInspector.setStructFieldData(out, field, inspector.create(errorNarrative));
				}
				else {
					try {
						if (iface instanceof WritableStringObjectInspector) {
							final WritableStringObjectInspector inspector = (WritableStringObjectInspector) iface;
							structInspector.setStructFieldData(out, field, inspector.create(colArray[i]));
						} else if (iface instanceof WritableIntObjectInspector) {
							final WritableIntObjectInspector inspector = (WritableIntObjectInspector) iface;
							structInspector.setStructFieldData(out, field, inspector.create(Integer.parseInt(colArray[i])));
						} else if (iface instanceof WritableFloatObjectInspector) {
							final WritableFloatObjectInspector inspector = (WritableFloatObjectInspector) iface;
							structInspector.setStructFieldData(out, field, inspector.create(Float.parseFloat(colArray[i])));
						} else if (iface instanceof WritableDoubleObjectInspector) {
							final WritableDoubleObjectInspector inspector = (WritableDoubleObjectInspector) iface;
							structInspector.setStructFieldData(out, field, inspector.create(Double.parseDouble(colArray[i])));
						} else if (iface instanceof WritableLongObjectInspector) {
							final WritableLongObjectInspector inspector = (WritableLongObjectInspector) iface;
							structInspector.setStructFieldData(out, field, inspector.create(Long.parseLong(colArray[i])));
						} else if (iface instanceof WritableBooleanObjectInspector) {
							final WritableBooleanObjectInspector inspector = (WritableBooleanObjectInspector) iface;
							structInspector.setStructFieldData(out, field, inspector.create(Boolean.parseBoolean(colArray[i])));
						} else {
							throw new IllegalArgumentException("unsupported inspector: " + iface.getTypeName());
						}
					} catch (Exception e) {
						//repeat the above, but defaulting things and keeping track of issues...
						if (iface instanceof WritableStringObjectInspector) {
							final WritableStringObjectInspector inspector = (WritableStringObjectInspector) iface;
							structInspector.setStructFieldData(out, field, "");
						} else if (iface instanceof WritableIntObjectInspector) {
							final WritableIntObjectInspector inspector = (WritableIntObjectInspector) iface;
							structInspector.setStructFieldData(out, field, inspector.create(Integer.parseInt("0")));
						} else if (iface instanceof WritableFloatObjectInspector) {
							final WritableFloatObjectInspector inspector = (WritableFloatObjectInspector) iface;
							structInspector.setStructFieldData(out, field, inspector.create(Float.parseFloat("0.0")));
						} else if (iface instanceof WritableDoubleObjectInspector) {
							final WritableDoubleObjectInspector inspector = (WritableDoubleObjectInspector) iface;
							structInspector.setStructFieldData(out, field, inspector.create(Double.parseDouble("0.0")));
						} else if (iface instanceof WritableLongObjectInspector) {
							final WritableLongObjectInspector inspector = (WritableLongObjectInspector) iface;
							structInspector.setStructFieldData(out, field, inspector.create(Long.parseLong("0")));
						} else if (iface instanceof WritableBooleanObjectInspector) {
							final WritableBooleanObjectInspector inspector = (WritableBooleanObjectInspector) iface;
							structInspector.setStructFieldData(out, field, inspector.create(Boolean.parseBoolean("false")));
						}

						errorCount++;
						errorNarrative += (errorCount + ". Field: " + field.getFieldName() + " Value: " + colArray[i] + " Error: " + e.getMessage() + " ");
					}
				}
				i++;
			}
		}
		catch (Exception e)
		{
			throw new HiveException("Error allocating results to columns - check that column headers and results are in sync and data types match. Row: " + 	row + " Error info: " + e.getMessage());
		}
		return out;
	}

}