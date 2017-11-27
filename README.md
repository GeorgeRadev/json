# JSON
JSON format parser and builder in Java 1.4.
Requires JDK 1.4 or higher and junit.jar for tests

### Examples

#### JSON parsing
```java
String json = "{\"stock\": {\"warehouse\": 300, \"retail\": 20 } }";
Object jsonObject = JSONParser.parse(json);

System.out.println(JSONPath.getString(jsonObject, "stock.warehouse")); //300
System.out.println(JSONPath.getString(jsonObject, "stock.retail")); //20
```

#### JSON creation:
```java
JSONBuilder builder = new JSONBuilder();
builder.startObject();
builder.addKeyValue("key", "value").addKeyValue("for", "while").addKey("array");
builder.startArray().addValue("for").addValue("value").addValue(10).endArray();
builder.endObject();

System.out.println(builder.toString());
```

Outputs: ```{"key":"value","for":"while","array":["for","value",10]}```