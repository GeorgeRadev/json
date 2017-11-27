package json;

public interface JSONListener {

	/**
	 * in case of reuse of the listener - this is the called before parsing
	 * starts
	 */
	void reset();
	
	public Object getObject();
	
	void startObject();

	void endObject();

	void startArray();

	void endArray();

	/** called for each key of an object */
	void key(String text);

	/** called for each value of object or Array */
	void value(String value);

	void value(double value);

	void value(long value);

	void value(boolean value);

}
