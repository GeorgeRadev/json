package json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class TestJSONSAXParser extends TestCase {

	JSONListener listener = new JSONListenerImpl();

	public void testDouble() throws IOException, Exception {
		{
			String json = "{\"id\": -42.13, \"id2\": -42.}";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertTrue(JSONPath.getObject(obj, "id") instanceof Double);
				assertEquals(JSONPath.getObject(obj, "id").toString(), "-42.13");
				assertEquals(JSONPath.getObject(obj, "id2").toString(), "-42.0");
			} else {
				fail();
			}
		}
		{
			String json = "{\"id\": 143.04528 }";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertTrue(JSONPath.getObject(obj, "id") instanceof Double);
			} else {
				fail();
			}
		}
		{
			String json = "{\"id\": 143e-8 }";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertTrue(JSONPath.getObject(obj, "id") instanceof Double);
			} else {
				fail();
			}
		}
	}

	public void testList() throws IOException, Exception {
		{
			String json = "{\"tags\": [ \"Bar\", \"Eek\"  ] }";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertTrue(JSONPath.getObject(obj, "tags") instanceof List);
				assertEquals("Bar", JSONPath.getString(obj, "tags.0"));
				assertEquals("Eek", JSONPath.getString(obj, "tags.1"));
			} else {
				fail();
			}
		}
		{
			String json = "{\"success\":true,\"records\":[],\"recordCount\":0,\"pageCount\":0}";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertEquals(JSONPath.getString(obj, "success"), "true");
				assertEquals(JSONPath.getString(obj, "recordCount"), "0");
				assertTrue(JSONPath.getList(obj, "records") != null);
				assertTrue(JSONPath.getList(obj, "records").size() == 0);
			} else {
				fail();
			}
		}
		{
			String json = "{'tags': [ 'Bar', 'Eek'] }";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertTrue(JSONPath.getObject(obj, "tags") instanceof List);
				assertEquals("Bar", JSONPath.getString(obj, "tags.0"));
				assertEquals("Eek", JSONPath.getString(obj, "tags.1"));
			} else {
				fail();
			}
		}
		{
			String json = "['tags',  'Bar', 'Eek' ]";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object _list = listener.getObject();
			if (_list != null && _list instanceof List) {
				List list = (List) _list;
				assertEquals("tags", list.get(0));
				assertEquals("Bar", list.get(1));
				assertEquals("Eek", list.get(2));
			} else {
				fail();
			}
		}
		{
			String json = "['tags','Bar','Eek']";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object _list = listener.getObject();
			if (_list != null && _list instanceof List) {
				List list = (List) _list;
				assertEquals("tags", list.get(0));
				assertEquals("Bar", list.get(1));
				assertEquals("Eek", list.get(2));
			} else {
				fail();
			}
		}
	}

	public void testMap() throws Exception {
		{
			String json = "{\"success\":true,\"records\":{},\"recordCount\":0,\"pageCount\":0}";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertEquals(JSONPath.getString(obj, "success"), "true");
				assertEquals(JSONPath.getString(obj, "recordCount"), "0");
				assertTrue(JSONPath.getMap(obj, "records") != null);
				assertTrue(JSONPath.getMap(obj, "records").size() == 0);
			} else {
				fail();
			}
		}
		{
			String json = "{\"success\":true,\"records\":[],\"recordCount\":0      ,     \"pageCount\"      :0}      ";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertEquals(JSONPath.getString(obj, "success"), "true");
				assertEquals(JSONPath.getString(obj, "recordCount"), "0");
				assertTrue(JSONPath.getList(obj, "records") != null);
				assertTrue(JSONPath.getList(obj, "records").size() == 0);
			} else {
				fail();
			}
		}
		{
			String json = "{\"stock\": {\"warehouse\": 300, \"retail\": 20 } }";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertTrue(JSONPath.getObject(obj, "stock") instanceof Map);
				assertEquals("300", JSONPath.getString(obj, "stock.warehouse"));
				assertEquals("20", JSONPath.getString(obj, "stock.retail"));
			} else {
				fail();
			}
		}
		{
			String json = "{'stock':  {    'warehouse': 300   , 'retail': 20 }     }   ";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertTrue(JSONPath.getObject(obj, "stock") instanceof Map);
				assertEquals("300", JSONPath.getString(obj, "stock.warehouse"));
				assertEquals("20", JSONPath.getString(obj, "stock.retail"));
			} else {
				fail();
			}
		}
		{
			String json = "{ stock: { warehouse : 300,  retail      \t : 20 } }";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertTrue(JSONPath.getObject(obj, "stock") instanceof Map);
				assertEquals("300", JSONPath.getString(obj, "stock.warehouse"));
				assertEquals("20", JSONPath.getString(obj, "stock.retail"));
			} else {
				fail();
			}
		}
		{
			String json = "{ stock: [{ warehouse : 300 },{ warehouse : 400 }], size:20, len: 50 }";
			listener.reset();
			String error = JSONParser.parse(json, listener);
			if (error != null) {
				fail(error);
			}
			Object obj = listener.getObject();
			if (obj != null) {
				assertTrue(JSONPath.getObject(obj, "stock") instanceof List);
				assertEquals("300", JSONPath.getString(obj, "stock.0.warehouse"));
				assertEquals("400", JSONPath.getString(obj, "stock.1.warehouse"));
				assertEquals("20", JSONPath.getString(obj, "size"));
				assertEquals("50", JSONPath.getString(obj, "len"));
			} else {
				fail();
			}
		}
	}

	public void testFile() throws Exception {
		File file = new File("./test/ui.json");
		long length = file.length();
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), 1026 * 32);
		char[] content = new char[(int) length];
		in.read(content);
		in.close();

		listener.reset();
		JSONParser.parse("", listener);
		Object obj = listener.getObject();
		if (obj != null) {
			fail();
		}
	}

	public void testBorders() throws IOException, Exception {
		{
			listener.reset();
			JSONParser.parse((String) null, listener);
			Object obj = listener.getObject();
			if (obj != null) {
				fail();
			}
		}
		{
			listener.reset();
			JSONParser.parse("", listener);
			Object obj = listener.getObject();
			if (obj != null) {
				fail();
			}
		}
		{
			listener.reset();
			JSONParser.parse("test", null);
			Object obj = listener.getObject();
			if (obj != null) {
				fail();
			}
		}
	}

	public void testString() throws IOException, Exception {
		String json = "{\"name\": \"\\u0434\\u0430\\n \\u0433\\u043E \\u0435\\u0431\\u0430\" }";
		listener.reset();
		String error = JSONParser.parse(json, listener);
		if (error != null) {
			fail(error);
		}
		assertNull(error);
		Object obj = listener.getObject();
		if (obj != null) {
			assertTrue(JSONPath.getObject(obj, "name") instanceof String);
			assertEquals("\u0434\u0430\n \u0433\u043E \u0435\u0431\u0430", JSONPath.getString(obj, "name"));
		} else {
			fail();
		}

		json = "{\"name\": \"\\/\" }";
		listener.reset();
		error = JSONParser.parse(json, listener);
		if (error != null) {
			fail(error);
		}
		assertNull(error);
		obj = listener.getObject();
		if (obj != null) {
			assertTrue(JSONPath.getObject(obj, "name") instanceof String);
			assertEquals("/", ((Map) obj).get("name"));
		} else {
			fail();
		}
	}

	public void testLong() throws IOException, Exception {
		String json = "{\"price\": 1234}";
		listener.reset();
		String error = JSONParser.parse(json, listener);
		if (error != null) {
			fail(error);
		}
		Object obj = listener.getObject();
		if (obj != null) {
			assertTrue(JSONPath.getObject(obj, "price") instanceof Long);
		} else {
			fail();
		}
	}

	public void testBoolean() throws IOException, Exception {
		String json = "{\"true\": true, \"false\": false}";
		listener.reset();
		String error = JSONParser.parse(json, listener);
		if (error != null) {
			fail(error);
		}
		Object obj = listener.getObject();
		if (obj != null) {
			assertTrue(JSONPath.getObject(obj, "true") instanceof Boolean);
			assertTrue(JSONPath.getObject(obj, "false") instanceof Boolean);
		} else {
			fail();
		}
	}

	public void testNull() throws IOException, Exception {
		String json = "{\"null\": null }";
		listener.reset();
		String error = JSONParser.parse(json, listener);
		if (error != null) {
			fail(error);
		}
		Object obj = listener.getObject();
		if (obj != null) {
			assertTrue(JSONPath.getObject(obj, "null") == null);
		} else {
			fail();
		}
	}
}
