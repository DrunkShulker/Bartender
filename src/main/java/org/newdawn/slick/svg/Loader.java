package org.newdawn.slick.svg;

import org.newdawn.slick.geom.Transform;
import org.w3c.dom.Element;


public interface Loader {
	
	public void loadChildren(Element element, Transform t) throws ParsingException;
}
