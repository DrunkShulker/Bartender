package org.newdawn.slick.tests.xml;



public class Item {
	
	protected String name;
	
	protected int condition; 

	
	public void dump(String prefix) {
		System.out.println(prefix+"Item "+name+","+condition);
	}
}
