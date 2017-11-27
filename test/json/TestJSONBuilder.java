package json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

public class TestJSONBuilder extends TestCase {
	public void testBuilderObject() {
		{
			Map map = new HashMap();
			List list = new ArrayList();
			list.add("foo");
			list.add("bar");
			map.put("list", list);
			String json = JSONBuilder.objectToJSON(map);
			assertEquals("{\"list\":[\"foo\",\"bar\"]}", json);
		}
		{
			Map map = new LinkedHashMap();
			List list = new ArrayList();
			list.add("foo");
			list.add("bar");
			map.put("list1", list);
			map.put("list2", null);
			String json = JSONBuilder.objectToJSON(map);
			assertEquals("{\"list1\":[\"foo\",\"bar\"],\"list2\":null}", json);
		}
	}

	public void testBuilder() {
		{
			JSONBuilder builder = new JSONBuilder();
			assertEquals("", builder.toString());
		}
		{
			JSONBuilder builder = new JSONBuilder();
			builder.startObject().endObject();
			assertEquals("{}", builder.toString());
		}
		{
			JSONBuilder builder = new JSONBuilder();
			builder.startArray().endArray();
			assertEquals("[]", builder.toString());
		}
		{
			JSONBuilder builder = new JSONBuilder();
			builder.startObject().addKeyValue("key", "value").endObject();
			assertEquals("{\"key\":\"value\"}", builder.toString());
		}
		{
			JSONBuilder builder = new JSONBuilder();
			builder.startObject().addKeyValue("key", "N/A").endObject();
			assertEquals("{\"key\":\"N\\/A\"}", builder.toString());
		}
		{
			JSONBuilder builder = new JSONBuilder();
			builder.startObject().addKeyValue("key", "value").addKeyValue("for", "while").addKeyValue("int", new Integer(5))
					.endObject();
			assertEquals("{\"key\":\"value\",\"for\":\"while\",\"int\":5}", builder.toString());
		}
		{
			JSONBuilder builder = new JSONBuilder();
			builder.startArray().addValue("for").addValue("value").addValue(10).endArray();
			assertEquals("[\"for\",\"value\",10]", builder.toString());
		}
		{
			JSONBuilder builder = new JSONBuilder();
			builder.startObject();
			builder.addKeyValue("key", "value").addKeyValue("for", "while").addKey("array");
			builder.startArray().addValue("for").addValue("value").addValue(10).endArray();
			builder.endObject();
			assertEquals("{\"key\":\"value\",\"for\":\"while\",\"array\":[\"for\",\"value\",10]}", builder.toString());
		}

		{
			JSONBuilder builder = new JSONBuilder();
			Map aMap = new TreeMap();
			aMap.put("string", "string");
			List list = new ArrayList();
			list.add(new Integer(1));
			list.add(new Integer(2));
			list.add(new Integer(3));
			aMap.put("list", list);
			Map submap = new TreeMap();
			submap.put("a", Boolean.TRUE);
			submap.put("b", Boolean.FALSE);
			aMap.put("map", submap);
			builder.startObject();
			builder.addKeyValue("map", aMap);
			builder.endObject();
			assertEquals("{\"map\":{\"list\":[1,2,3],\"map\":{\"a\":true,\"b\":false},\"string\":\"string\"}}",
					builder.toString());
		}
		{// serialize a single map as content
			JSONBuilder builder = new JSONBuilder();
			Map map = new TreeMap();
			map.put("a", Boolean.TRUE);
			map.put("b", Boolean.FALSE);

			builder.addValue(map);
			assertEquals("{\"a\":true,\"b\":false}", builder.toString());
		}
		{// serialize a single list as content
			JSONBuilder builder = new JSONBuilder();
			List list = new ArrayList();
			list.add(new Integer(1));
			list.add(new Integer(2));
			list.add(new Integer(3));
			builder.addValue(list);
			assertEquals("[1,2,3]", builder.toString());
		}

		{// serialize a single map and null
			JSONBuilder builder = new JSONBuilder();
			Map map = new TreeMap();
			map.put("a", Boolean.TRUE);
			map.put("b", Boolean.FALSE);
			map.put("c", null);

			builder.addValue(map);
			assertEquals("{\"a\":true,\"b\":false,\"c\":null}", builder.toString());
		}
		{// serialize a single list as content
			JSONBuilder builder = new JSONBuilder();
			List list = new ArrayList();
			list.add(new Integer(1));
			list.add(new Integer(2));
			list.add(new Integer(3));
			list.add(null);
			builder.addValue(list);
			assertEquals("[1,2,3,null]", builder.toString());
		}
	}
}
