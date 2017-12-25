package apps;

public class ScalarSymbol {
	
	/**
	 * Name, sequence of letters
	 */
	public String name;
	
	/**
	 * Integer value
	 */
	public int value;

	/**
	 * Initializes this symbol with given name, and zero value
	 * 
	 * @param name Variable name
	 */
	public ScalarSymbol(String name) {
		this.name = name; 
		value = 0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name + "=" + value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ScalarSymbol)) {
			return false;
		}
		ScalarSymbol ss = (ScalarSymbol)o;
		return name.equals(ss.name);
	}
}

