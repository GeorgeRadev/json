package json;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Builds JSON presentation in a chain calls manner:<br/>
 * example: <br/>
 * 
 * <pre>
 * {@code
 * 	JSONBuilder builder = new JSONBuilder();
 * 	builder.startObject();
 * 	builder.addKeyValue("key", "value").addKeyValue("for", "while").addKey("array");
 * 	builder.startArray().addValue("for").addValue("value").addValue(10).endArray();
 * 	builder.endObject();
 * 	
 * 	String json = builder.toString());
 * 	}
 * </pre>
 */
public class JSONBuilder {
	final StringBuffer builder;

	private static String EMPTY = "";

	public JSONBuilder() {
		builder = new StringBuffer();
	}

	public JSONBuilder(int size) {
		builder = new StringBuffer(size);
	}

	public final JSONBuilder reset() {
		builder.setLength(0);
		return this;
	}

	public final JSONBuilder startObject() {
		builder.append('{');
		return this;
	}

	public final JSONBuilder put(String name, String value) {
		addKeyValue(name, value);
		return this;
	}

	public final JSONBuilder put(String name, Object value) {
		addKeyValue(name, value);
		return this;
	}

	public final JSONBuilder addKeyValue(String name, String value) {
		addKey(name);
		addValue(value);
		return this;
	}

	public final JSONBuilder addKeyValue(String name, Object value) {
		addKey(name);
		addValue(value);
		return this;
	}

	public final JSONBuilder addKey(String name) {
		escapeAsString(builder, name);
		builder.append(':');
		return this;
	}

	public final JSONBuilder addValue(String str) {
		escapeAsString(builder, str);
		builder.append(',');
		return this;
	}

	public final JSONBuilder addValue(long value) {
		builder.append(value);
		builder.append(',');
		return this;
	}

	public final JSONBuilder addValue(int value) {
		builder.append(value);
		builder.append(',');
		return this;
	}

	public final JSONBuilder addValue(Object value) {
		if (value == null) {
			builder.append("null");
		} else if (value instanceof Number || value instanceof Boolean) {
			builder.append(value.toString());
		} else if (value instanceof List) {
			List theList = (List) value;
			startArray();
			for (int i = 0, l = theList.size(); i < l; ++i) {
				addValue(theList.get(i));
			}
			endArray();
			removeComma();

		} else if (value instanceof Map) {
			Map theMap = (Map) value;
			startObject();
			for (Iterator i = theMap.entrySet().iterator(); i.hasNext();) {
				Map.Entry element = (Map.Entry) i.next();
				Object key = element.getKey();
				if (key == null) {
					continue;
				}
				addKey(String.valueOf(element.getKey()));
				addValue(element.getValue());
			}
			endObject();
			removeComma();

		} else {
			escapeAsString(builder, String.valueOf(value));
		}
		builder.append(',');
		return this;
	}

	public final JSONBuilder endObject() {
		removeComma();
		builder.append('}');
		builder.append(',');
		return this;
	}

	public final JSONBuilder startArray() {
		builder.append('[');
		return this;
	}

	public final JSONBuilder endArray() {
		removeComma();
		builder.append(']');
		builder.append(',');
		return this;
	}

	private void removeComma() {
		final int len = builder.length() - 1;
		if (builder.charAt(len) == ',') {
			builder.setLength(len);
		}
	}

	public String toString() {
		if (builder.length() <= 0) {
			return EMPTY;
		}
		final int len = builder.length() - 1;
		if (builder.charAt(len) == ',') {
			return builder.substring(0, len);
		} else {
			return builder.toString();
		}
	}

	static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	void escapeAsString(StringBuffer builder, String str) {
		if (str == null) {
			builder.append("null");
			return;
		}
		builder.append('"');
		for (int i = 0, l = str.length(); i < l; i++) {
			final char c = str.charAt(i);
			switch (c) {
			case '"':
				builder.append("\\\"");
				break;
			case '\\':
				builder.append("\\\\");
				break;
			case '/':
				builder.append("\\/");
				break;
			case '\b':
				builder.append("\\b");
				break;
			case '\f':
				builder.append("\\f");
				break;
			case '\n':
				builder.append("\\n");
				break;
			case '\r':
				builder.append("\\r");
				break;
			case '\t':
				builder.append("\\t");
				break;
			default:
				if ((c < 0x0020) || (c > 0x007e)) {
					builder.append("\\u");
					builder.append(hexDigit[((c >> 12) & 0xF)]);
					builder.append(hexDigit[((c >> 8) & 0xF)]);
					builder.append(hexDigit[((c >> 4) & 0xF)]);
					builder.append(hexDigit[(c & 0xF)]);
				} else {
					builder.append(c);
				}
				break;
			}
		}
		builder.append('"');
	}

	public static final String objectToJSON(Object obj) {
		JSONBuilder builder = new JSONBuilder();
		objectToJSON(builder, obj);
		return builder.toString();
	}

	private static final void objectToJSON(JSONBuilder builder, Object obj) {
		if (obj == null) {
			builder.addValue(null);
		} else if (obj instanceof Map) {
			builder.startObject();
			for (Iterator i = ((Map) obj).entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				Object key = entry.getKey();
				if (key == null) {
					continue;
				}
				builder.addKey(key.toString());
				objectToJSON(builder, entry.getValue());
			}
			builder.endObject();
		} else if (obj instanceof List) {
			builder.startArray();
			List theList = ((List) obj);
			for (int i = 0, l = theList.size(); i < l; ++i) {
				objectToJSON(builder, theList.get(i));
			}
			builder.endArray();
		} else if (obj instanceof Date) {
			builder.addValue(((Date) obj).getTime());
		} else if (obj instanceof Calendar) {
			builder.addValue(((Calendar) obj).getTimeInMillis());
		} else {
			builder.addValue(String.valueOf(obj));
		}
	}
}
