package json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class TestJSONParser extends TestCase {

	public void testFile() throws Exception {
		File file = new File("./test/ui.json");
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), 1026 * 32);

		Object json = JSONParser.parse(in);
		if (json == null) {
			fail();
		}
	}

	public void testBorders() {
		Object json = null;
		try {
			json = JSONParser.parse((String) null);
			fail();
		} catch (Exception e) {
		}
		if (json != null) {
			fail();
		}

		try {
			json = JSONParser.parse((String) null);
			fail();
		} catch (Exception e) {
		}
		if (json != null) {
			fail();
		}

		try {
			json = JSONParser.parse("");
			fail();
		} catch (Exception e) {
		}
		if (json != null) {
			fail();
		}
	}

	public void testMap1() throws Exception {
		String json = "{\"stock\": {\"warehouse\": 300, \"retail\": 20 } }";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(((Map) jsonObject).get("stock") instanceof Map);
			assertEquals("300", ((Map) (((Map) jsonObject).get("stock"))).get("warehouse").toString());
			assertEquals("20", ((Map) (((Map) jsonObject).get("stock"))).get("retail").toString());

			assertTrue(JSONPath.getMap(jsonObject, "stock") instanceof Map);
			assertEquals(JSONPath.getString(jsonObject, "stock.warehouse"), "300");
			assertEquals(JSONPath.getString(jsonObject, "stock.retail"), "20");
		} else {
			fail();
		}
	}

	public void testMap2() throws Exception {
		String json = "{'stock': {'warehouse': 300, 'retail': 20 } }";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(((Map) jsonObject).get("stock") instanceof Map);
			assertEquals("300", ((Map) (((Map) jsonObject).get("stock"))).get("warehouse").toString());
			assertEquals("20", ((Map) (((Map) jsonObject).get("stock"))).get("retail").toString());

			assertTrue(JSONPath.getMap(jsonObject, "stock") instanceof Map);
			assertEquals(JSONPath.getString(jsonObject, "stock.warehouse"), "300");
			assertEquals(JSONPath.getString(jsonObject, "stock.retail"), "20");
		} else {
			fail();
		}
	}

	public void testMap3() throws Exception {
		String json = "{ stock : { warehouse : 300,  retail   : 20 } }";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(((Map) jsonObject).get("stock") instanceof Map);
			assertEquals("300", ((Map) (((Map) jsonObject).get("stock"))).get("warehouse").toString());
			assertEquals("20", ((Map) (((Map) jsonObject).get("stock"))).get("retail").toString());

			assertTrue(JSONPath.getMap(jsonObject, "stock") instanceof Map);
			assertEquals(JSONPath.getString(jsonObject, "stock.warehouse"), "300");
			assertEquals(JSONPath.getString(jsonObject, "stock.retail"), "20");
		} else {
			fail();
		}
	}

	public void testMap4() throws IOException, Exception {
		String json = "{ stock: [{ warehouse : 300 },{ warehouse : -400 }], size:20, len: 50 }";
		Object jsonObject = JSONParser.parse(json);
		Map map = (Map) jsonObject;
		if (map != null) {
			assertTrue(map.get("stock") instanceof List);
			assertEquals(JSONPath.getString(jsonObject, "stock.0.warehouse"), "300");
			assertEquals(JSONPath.getString(jsonObject, "stock.1.warehouse"), "-400");
			assertEquals(JSONPath.getString(jsonObject, "size"), "20");
			assertEquals(JSONPath.getString(jsonObject, "len"), "50");
		} else {
			fail();
		}
	}

	public void testMap5() throws Exception {
		String json = "{a: -300, b: -20.4 }";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(jsonObject instanceof Map);
			assertEquals("-300", ((Map) jsonObject).get("a").toString());
			assertEquals("-20.4", ((Map) jsonObject).get("b").toString());

			assertEquals(JSONPath.getString(jsonObject, "a"), "-300");
			assertEquals(JSONPath.getString(jsonObject, "b"), "-20.4");
		} else {
			fail();
		}
	}

	public void testList1() throws Exception {
		String json = "{\"tags\": [ \"Bar\", \"Eek\"] }";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(((Map) jsonObject).get("tags") instanceof List);
			assertEquals("Bar", ((List) (((Map) jsonObject).get("tags"))).get(0).toString());
			assertEquals("Eek", ((List) (((Map) jsonObject).get("tags"))).get(1).toString());

			assertTrue(JSONPath.getList(jsonObject, "tags") instanceof List);
			assertEquals(JSONPath.getString(jsonObject, "tags.0"), "Bar");
			assertEquals(JSONPath.getString(jsonObject, "tags.1"), "Eek");
		} else {
			fail();
		}
	}

	public void testList2() throws Exception {
		String json = "{'tags': [ 'Bar', 'Eek'] }";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(((Map) jsonObject).get("tags") instanceof List);
			assertEquals("Bar", ((List) (((Map) jsonObject).get("tags"))).get(0).toString());
			assertEquals("Eek", ((List) (((Map) jsonObject).get("tags"))).get(1).toString());

			assertTrue(JSONPath.getList(jsonObject, "tags") instanceof List);
			assertEquals(JSONPath.getString(jsonObject, "tags.0"), "Bar");
			assertEquals(JSONPath.getString(jsonObject, "tags.1"), "Eek");
		} else {
			fail();
		}
	}

	public void testList3() throws Exception {
		String json = "{\"success\":true,\"records\":[],\"recordCount\":0,\"pageCount\":0}";

		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertEquals(JSONPath.getString(jsonObject, "success"), "true");
			assertEquals(JSONPath.getString(jsonObject, "recordCount"), "0");
			assertTrue(JSONPath.getList(jsonObject, "records") != null);
			assertTrue(JSONPath.getList(jsonObject, "records").size() == 0);
		} else {
			fail();
		}
	}

	public void testString() throws Exception {
		String json = "{\"name\": \"\\u0434\\u0430\\n \\u0433\\u043E \\u0435\\u0431\\u0430\" }";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(((Map) jsonObject).get("name") instanceof String);
		} else {
			fail();
		}

		json = "{\"name\": \"\\/\" }";
		jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertEquals("/", ((Map) jsonObject).get("name"));
		} else {
			fail();
		}
	}

	public void testLong() throws Exception {
		String json = "{\"price\": 1234}";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(((Map) jsonObject).get("price") instanceof Long);

			assertEquals(JSONPath.getInteger(jsonObject, "price"), 1234);
			assertEquals(JSONPath.getLong(jsonObject, "price"), 1234L);
		} else {
			fail();
		}
	}

	public void testDouble() throws Exception {
		String json = "{\"id\": 143.04528 }";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(((Map) jsonObject).get("id") instanceof Double);
			assertEquals(JSONPath.getDouble(jsonObject, "id"), 143.04528d, 0.0001d);
		} else {
			fail();
		}

		json = "{\"id\": 143e-8 }";
		jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(((Map) jsonObject).get("id") instanceof Double);
			JSONPath.getDouble(jsonObject, "id");
		} else {
			fail();
		}
	}

	public void testBoolean() throws Exception {
		String json = "{\"true\": true, \"false\": false }";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(((Map) jsonObject).get("true") instanceof Boolean);
			assertTrue(((Map) jsonObject).get("false") instanceof Boolean);

			assertTrue(JSONPath.getBoolean(jsonObject, "true"));
			assertFalse(JSONPath.getBoolean(jsonObject, "false"));
		} else {
			fail();
		}
	}

	public void testNull() throws Exception {
		String json = "{\"null\": null }";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertTrue(((Map) jsonObject).get("null") == null);
			try {

				JSONPath.getObject(jsonObject, "null");
				// ok null is actually null
			} catch (Exception e) {
				fail();
			}
		} else {
			fail();
		}
	}

	public void testPath1() throws Exception {
		String json = "{a:{b:{c:{d:'abcd'}}}}";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertEquals(JSONPath.getString(jsonObject, "a.b.c.d"), "abcd");
		} else {
			fail();
		}
	}

	public void testPath2() throws Exception {
		String json = "{a:{b:[{c:{d:'abcd'}},{e:{f:'abef'}}]}}";
		Object jsonObject = JSONParser.parse(json);
		if (jsonObject != null) {
			assertEquals(JSONPath.getString(jsonObject, "a.b.0.c.d"), "abcd");
			assertEquals(JSONPath.getString(jsonObject, "a.b.1.e.f"), "abef");
		} else {
			fail();
		}
	}
}
