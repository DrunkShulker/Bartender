package org.newdawn.slick.svg.inkscape;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Figure;
import org.newdawn.slick.svg.Loader;
import org.newdawn.slick.svg.NonGeometricData;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Element;


public class UseProcessor implements ElementProcessor {

	
	public boolean handles(Element element) {
		return element.getNodeName().equals("use");
	}

	
	public void process(Loader loader, Element element, Diagram diagram,
			Transform transform) throws ParsingException {

		String ref = element.getAttributeNS("http:
		String href = Util.getAsReference(ref);
		
		Figure referenced = diagram.getFigureByID(href);
		if (referenced == null) {
			throw new ParsingException(element, "Unable to locate referenced element: "+href);
		}
		
		Transform local = Util.getTransform(element);
		Transform trans = local.concatenate(referenced.getTransform());
		
		NonGeometricData data = Util.getNonGeometricData(element);
		Shape shape = referenced.getShape().transform(trans);
		data.addAttribute(NonGeometricData.FILL, referenced.getData().getAttribute(NonGeometricData.FILL));
		data.addAttribute(NonGeometricData.STROKE, referenced.getData().getAttribute(NonGeometricData.STROKE));
		data.addAttribute(NonGeometricData.OPACITY, referenced.getData().getAttribute(NonGeometricData.OPACITY));
		data.addAttribute(NonGeometricData.STROKE_WIDTH, referenced.getData().getAttribute(NonGeometricData.STROKE_WIDTH));
		
		Figure figure = new Figure(referenced.getType(), shape, data, trans);
		diagram.addFigure(figure);
	}

}
