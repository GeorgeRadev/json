package json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class JSONListenerImpl implements JSONListener {
	Stack objectStack;
	Map currentMap;
	List currentList;
	String key;

	public JSONListenerImpl() {
		reset();
	}
	
	public Object getObject() {
		if (currentMap != null) {
			return currentMap;
		}
		if (currentList != null) {
			return currentList;
		}
		return null;
	}

	public Map getMap() {
		return currentMap;
	}

	public List getList() {
		return currentList;
	}

	public void reset() {
		if (objectStack == null) {
			objectStack = new Stack();
		} else {
			objectStack.clear();
		}
		currentMap = null;
		currentList = null;
		key = null;
	}

	public void startObject() {
		Map newMap = new HashMap(32);
		if (objectStack.size() > 0) {
			if (key != null) {
				currentMap.put(key, newMap);
				key = null;
			} else {
				currentList.add(newMap);
			}
		}
		objectStack.push(currentMap = newMap);
	}
 
	public void endObject() {
		currentMap = (Map) objectStack.pop();

		if (objectStack.size() > 0) {
			Object top = objectStack.peek();
			if (top instanceof List) {
				currentList = (List) top;
				currentMap = null;
			} else if (top instanceof Map) {
				currentMap = (Map) top;
				currentList = null;
			}
		}
	}

	public void startArray() {
		List newArray = new ArrayList(32);
		if (objectStack.size() > 0) {
			if (key != null) {
				currentMap.put(key, newArray);
				key = null;
			} else {
				currentList.add(newArray);
			}
		}
		objectStack.push(currentList = newArray);
	}
 
	public void endArray() {
		currentList = (List) objectStack.pop();
		if (objectStack.size() > 0) {
			Object top = objectStack.peek();
			if (top instanceof List) {
				currentList = (List) top;
				currentMap = null;
			} else if (top instanceof Map) {
				currentMap = (Map) top;
				currentList = null;
			}
		}
	}

	public void key(String text) {
		key = text;
	}

	public void value(String value) {
		if (key != null) {
			currentMap.put(key, value);
			key = null;
		} else {
			currentList.add(value);
		}
	}

	public void value(double value) {
		if (key != null) {
			currentMap.put(key, new Double(value));
			key = null;
		} else {
			currentList.add(new Double(value));
		}
	}

	public void value(long value) {
		if (key != null) {
			currentMap.put(key, new Long(value));
			key = null;
		} else {
			currentList.add(new Long(value));
		}
	}

	public void value(boolean value) {
		if (key != null) {
			currentMap.put(key, Boolean.valueOf(value));
			key = null;
		} else {
			currentList.add(Boolean.valueOf(value));
		}
	}
}
