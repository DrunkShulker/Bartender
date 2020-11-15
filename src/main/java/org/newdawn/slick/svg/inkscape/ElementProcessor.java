package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Loader;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Element;


public interface ElementProcessor {
	
	public void process(Loader loader, Element element, Diagram diagram, Transform transform) throws ParsingException;

	
	public boolean handles(Element element);
}
