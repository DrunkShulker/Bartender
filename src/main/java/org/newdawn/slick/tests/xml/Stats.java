package org.newdawn.slick.tests.xml;


public class Stats {
	
	private int hp;
	
	private int mp;
	
	private float age;
	
	private int exp;

	
	public void dump(String prefix) {
		System.out.println(prefix+"Stats "+hp+","+mp+","+age+","+exp);
	}
}
