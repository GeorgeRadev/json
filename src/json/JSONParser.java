package json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class JSONParser {
	private Reader reader;
	private int parserChar;
	private int parserLineNr;
	private int parserLinePos;
	private final JSONListener listener;
	private final StringBuffer stringbuffer;

	/**
	 * no instances available use the static parse(...) methods
	 */
	protected JSONParser(JSONListener listener) {
		this.listener = listener;
		parserChar = 0;
		parserLineNr = 0;
		parserLinePos = 0;
		stringbuffer = new StringBuffer(4096);
	}
	

	public static Object parse(String json) throws Exception {
		if (json == null || json.length() <= 0) {
			throw new Exception("Nothing to parse!");
		}
		JSONListenerImpl listener = new JSONListenerImpl();
		JSONParser.parseEx(json, listener);
		return listener.getObject();
	}

	public static Object parse(Reader json) throws Exception {
		if (json == null) {
			throw new Exception("Nothing to parse!");
		}
		JSONListenerImpl listener = new JSONListenerImpl();
		JSONParser.parseEx(json, listener);
		return listener.getObject();
	}

	/**
	 * Parse JSON string presentation and return non-null error text if any
	 * 
	 * @param json
	 *            JSON string presentation
	 * @param listener
	 *            JSONSAXListener implementation
	 * @return error text, otherwise null
	 */
	public static String parse(String json, JSONListener listener) {
		if (json == null || json.length() <= 0) {
			return null;
		}
		return parse(new StringReader(json), listener);
	}

	/**
	 * Parse JSON string presentation and return non-null error text if any
	 * 
	 * @param json
	 *            Reader for JSON presentation
	 * @param listener
	 *            JSONSAXListener implementation
	 * @return error text, otherwise null
	 */
	public static String parse(Reader reader, JSONListener listener) {
		if (listener != null) {
			try {
				parseEx(reader, listener);
			} catch (Exception exception) {
				return exception.getMessage();
			}
		}
		return null;
	}

	/**
	 * Parse JSON string presentation
	 * 
	 * @param json
	 *            JSON string presentation
	 * @param listener
	 *            JSONSAXListener implementation
	 * @throws exception
	 *             on error
	 */
	public static void parseEx(String json, JSONListener listener) throws Exception {
		parseEx(new StringReader(json), listener);
	}

	/**
	 * Parse JSON string presentation
	 * 
	 * @param json
	 *            Reader for JSON presentation
	 * @param listener
	 *            JSONSAXListener implementation
	 * @throws exception
	 *             on error
	 */
	public static void parseEx(Reader reader, JSONListener listener) throws Exception {
		JSONParser parser = new JSONParser(listener);
		parser.parseFromReader(reader);
	}

	protected void parseFromReader(Reader reader) throws IOException, Exception {
		this.reader = reader;
		readChar();
		if (parserChar != -1) {
			scanElement();
		} else {
			// empty Reader has been passed
		}
	}

	protected void scanElement() throws IOException, Exception {
		// recognizes boolean, long, double, string, array, object
		skipWhitespace();

		if (parserChar == '{') {
			// new object
			scanObject();
			skipWhitespace();

		} else if (parserChar == '[') {
			// new array
			scanArray();
			skipWhitespace();

		} else if (parserChar == '+' || parserChar == '-' || (parserChar >= '0' && parserChar <= '9')) {
			// long or double
			stringbuffer.setLength(0);
			if (parserChar == '+' || parserChar == '-') {
				stringbuffer.append((char) parserChar);
				readChar();// eat sign
			}
			addDigits();

			boolean decimal = false;
			if (parserChar == '.') {
				decimal = true;
				stringbuffer.append((char) parserChar);
				readChar();// eat .
				addDigits();
			}

			if ((parserChar == 'e') || (parserChar == 'E')) {
				decimal = true;
				stringbuffer.append((char) parserChar);
				readChar(); // eat e
				if (parserChar == '+' || parserChar == '-') {
					stringbuffer.append((char) parserChar);
					readChar(); // eat sign
				}

				addDigits();
			}

			if (decimal) {
				int l = stringbuffer.length();
				if (l > 0 && stringbuffer.charAt(l - 1) == '.') {
					stringbuffer.setLength(l - 1);
				}
				listener.value(toDouble(stringbuffer.toString()));
			} else {
				listener.value(toLong(stringbuffer.toString()));
			}

		} else if (parserChar == '\'' || parserChar == '\"') {
			// string
			scanString();
			listener.value(stringbuffer.toString());

		} else {
			scanLiteral();

			final String constantString = stringbuffer.toString();
			if ("true".equals(constantString)) {
				listener.value(true);
			} else if ("false".equals(constantString)) {
				listener.value(false);
			} else if ("null".equals(constantString)) {
				listener.value(null);
			} else {
				listener.value(constantString);
			}
		}
	}

	protected final void readChar() throws IOException, Exception {
		parserChar = reader.read();
		parserLinePos++;
		if (parserChar == 10) {
			parserLineNr++;
		}
	}

	protected final void skipWhitespace() throws IOException, Exception {
		while (parserChar != -1 && parserChar <= ' ') {
			readChar();
		}
	}

	protected String scanLiteral() throws IOException, Exception {
		stringbuffer.setLength(0);
		while (true) {
			if (parserChar == -1) {
				throw syntaxError("Unexpected end of stream.");
			} else if (!Character.isLetter((char) parserChar)) {
				break;
			} else {
				stringbuffer.append((char) parserChar);
			}
			readChar();
		}
		return stringbuffer.toString();
	}

	protected String scanString() throws IOException, Exception {
		stringbuffer.setLength(0);
		final char terminationChar = (char) parserChar;
		do {
			if (parserChar == -1) {
				throw expectedInput(terminationChar + " to end the string.");
			}
			readChar();
			char c = (char) parserChar;
			if (c == terminationChar) {
				readChar();
				return stringbuffer.toString();
			} else if (c == '\\') {
				// escaping
				readChar();
				if (parserChar == -1) {
					throw syntaxError("Not finished escaping");
				}
				c = (char) parserChar;

				if (c == '\'') {
					stringbuffer.append(c);
				} else if (c == '\"') {
					stringbuffer.append(c);
				} else if (c == '\\') {
					stringbuffer.append(c);
				} else if (c == '/') {
					stringbuffer.append(c);
				} else if (c == 'n') {
					stringbuffer.append('\n');
				} else if (c == 'r') {
					stringbuffer.append('\r');
				} else if (c == 't') {
					stringbuffer.append('\t');
				} else if (c == 'f') {
					stringbuffer.append('\f');
				} else if (c == 'b') {
					stringbuffer.append('\b');
				} else if (c == '0') {
					stringbuffer.append('\0');
				} else if (c == 'u') {
					// read next 4 hex values for UNICODE string
					int value = 0;
					for (int x = 0; x < 4; x++) {
						readChar();
						if (parserChar == -1) {
							throw syntaxError("Not finished escaping");
						}
						int v = toHex((char) parserChar);
						if (v == -1) {
							throw syntaxError("Malformed \\uxxxx encoding.");
						}
						value = (value << 4) + v;
					}

					stringbuffer.append((char) value);
				} else {
					throw syntaxError("Unrecognized \\escaping.");
				}
			} else {
				stringbuffer.append(c);
			}
		} while (true);

	}

	protected void scanArray() throws IOException, Exception {
		listener.startArray();
		// eat [
		readChar();
		while (true) {
			skipWhitespace();
			if (parserChar == -1) {
				throw expectedInput("]");
			} else if (parserChar == ']') {
				readChar();
				break;
			} else {
				scanElement();
				while (parserChar != -1 && parserChar <= ' ') {
					skipWhitespace();
				}
				if (parserChar == ']') {
					readChar();
					break;
				} else if (parserChar == ',') {
					readChar();
					continue;
				} else {
					throw syntaxError("Expecting ']' or ','");
				}
			}
		}
		listener.endArray();
	}

	protected void scanObject() throws IOException, Exception {
		listener.startObject();
		// eat {
		readChar();
		while (true) {
			skipWhitespace();
			if (parserChar == -1) {
				throw expectedInput("}");
			} else if (parserChar == '}') {
				readChar();
				break;
			} else if (parserChar == '\"' || parserChar == '\'') {
				scanString();
			} else {
				// read literal
				scanLiteral();
			}

			{
				String key = stringbuffer.toString();
				listener.key(key);
			}
			// check if : was already found, or skip to it
			skipWhitespace();
			if (parserChar != ':') {
				throw syntaxError("Expecting ':'");
			} else {
				// eat :
				readChar();
				scanElement();
				skipWhitespace();
				if (parserChar == '}') {
					readChar();
					break;
				} else if (parserChar == ',') {
					readChar();
					continue;
				} else {
					throw syntaxError("Expecting '}' or ','");
				}
			}
		}
		listener.endObject();
	}

	private final int addDigits() throws IOException, Exception {
		while (true) {
			if (parserChar >= '0' && parserChar <= '9') {
				stringbuffer.append((char) parserChar);
			} else {
				return parserChar;
			}
			readChar();
		}
	}

	public static long toLong(String s) {
		return toLong(s, 0);
	}

	public static long toLong(String str, int i) {
		if (str == null)
			return i;
		try {
			return Long.parseLong(str, 10);
		} catch (NumberFormatException numberformatexception) {
			return i;
		}
	}

	public static double toDouble(String s) {
		return toDouble(s, 0.0D);
	}

	public static double toDouble(String s, double d) {
		if (s == null)
			return d;
		try {
			return Double.valueOf(s).doubleValue();
		} catch (NumberFormatException numberformatexception) {
			return d;
		}
	}

	public static boolean toBoolean(String s) {
		if (s == null) {
			return false;
		}
		return Boolean.valueOf(s).booleanValue();
	}

	protected Exception syntaxError(String s) {
		return new Exception("line " + parserLineNr + ":" + parserLinePos + " " + s);
	}

	protected Exception expectedInput(String s) {
		return new Exception("line " + parserLineNr + ":" + parserLinePos + " " + s);
	}

	public final static int toHex(char c) {
		switch (c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return c - '0';

		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
			return 0x0A + (c - 'a');

		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
			return 0x0A + (c - 'A');
		case 58: // ':'
		case 59: // ';'
		case 60: // '<'
		case 61: // '='
		case 62: // '>'
		case 63: // '?'
		case 64: // '@'
		case 71: // 'G'
		case 72: // 'H'
		case 73: // 'I'
		case 74: // 'J'
		case 75: // 'K'
		case 76: // 'L'
		case 77: // 'M'
		case 78: // 'N'
		case 79: // 'O'
		case 80: // 'P'
		case 81: // 'Q'
		case 82: // 'R'
		case 83: // 'S'
		case 84: // 'T'
		case 85: // 'U'
		case 86: // 'V'
		case 87: // 'W'
		case 88: // 'X'
		case 89: // 'Y'
		case 90: // 'Z'
		case 91: // '['
		case 92: // '\\'
		case 93: // ']'
		case 94: // '^'
		case 95: // '_'
		case 96: // '`'
		default:
			return -1;
		}
	}
}
