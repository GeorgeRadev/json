package json;

import junit.framework.TestCase;

// http://seriot.ch/json/parsing.html
public class testJSONSeriot extends TestCase {

	public void testOpenArrayObject() throws Exception {
		StringBuffer json = new StringBuffer();
		for (int i = 0; i < 1000; i++) {
			json.append("[{\"\":");
		}
		try {
			JSONParser.parse(json.toString());
			fail();
		} catch (Exception e) {
			// OK
			e.printStackTrace();
		}
	}

	public void testOpeningArray() throws Exception {
		StringBuffer json = new StringBuffer();
		for (int i = 0; i < 1000; i++) {
			json.append("[");
		}
		try {
			JSONParser.parse(json.toString());
			fail();
		} catch (Exception e) {
			// OK
			e.printStackTrace();
		}
	}

	public void testNoData() throws Exception {
		try {
			JSONParser.parse("");
			fail();
		} catch (Exception e) {
			// OK
			e.printStackTrace();
		}
	}

	public void testMissingObjectValue() throws Exception {
		try {
			JSONParser.parse("{\"a\":");
			fail();
		} catch (Exception e) {
			// OK
			e.printStackTrace();
		}
		try {
			JSONParser.parse("{\"\":");
			fail();
		} catch (Exception e) {
			// OK
			e.printStackTrace();
		}
	}

	public void testEscapeUnclosed() throws Exception {
		try {
			JSONParser.parse("[\"\\");
			fail();
		} catch (Exception e) {
			// OK
			e.printStackTrace();
		}
	}

	public void testNewLine() throws Exception {
		try {
			JSONParser.parse("['a',\n4\n,1,");
			fail();
		} catch (Exception e) {
			// OK
			e.printStackTrace();
		}
	}

	public void testHugeNumber() throws Exception {
		try {
			JSONParser.parse("[0.4e00669999999999999999999999999999999999999999999999999999999999999999999999");
			fail();
		} catch (Exception e) {
			// OK
			e.printStackTrace();
		}
	}

	public void testNegativeHugeNumber() throws Exception {
		try {
			Object result = JSONParser.parse("{a:[-1e+9999]}");
			assertNotNull(result);
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
	}
}
