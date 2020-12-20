package org.newdawn.slick.svg.inkscape;

import java.util.StringTokenizer;

import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.svg.NonGeometricData;
import org.newdawn.slick.svg.ParsingException;
import org.w3c.dom.Element;


public class Util {
	
	public static final String INKSCAPE = "http:/"+"/www.inkscape.org/namespaces/inkscape";
	
	public static final String SODIPODI = "http:/"+"/sodipodi.sourceforge.net/DTD/sodipodi-0.dtd";
	
	public static final String XLINK = "http:/"+"/www.w3.org/1999/xlink";

	
	static NonGeometricData getNonGeometricData(Element element) {
		String meta = getMetaData(element);
		
		NonGeometricData data = new InkscapeNonGeometricData(meta, element);
		data.addAttribute(NonGeometricData.ID, element.getAttribute("id"));
		data.addAttribute(NonGeometricData.FILL, getStyle(element, NonGeometricData.FILL));
		data.addAttribute(NonGeometricData.STROKE, getStyle(element, NonGeometricData.STROKE));
		data.addAttribute(NonGeometricData.OPACITY, getStyle(element, NonGeometricData.OPACITY));
		data.addAttribute(NonGeometricData.STROKE_DASHARRAY, getStyle(element, NonGeometricData.STROKE_DASHARRAY));
		data.addAttribute(NonGeometricData.STROKE_DASHOFFSET, getStyle(element, NonGeometricData.STROKE_DASHOFFSET));
		data.addAttribute(NonGeometricData.STROKE_MITERLIMIT, getStyle(element, NonGeometricData.STROKE_MITERLIMIT));
		data.addAttribute(NonGeometricData.STROKE_OPACITY, getStyle(element, NonGeometricData.STROKE_OPACITY));
		data.addAttribute(NonGeometricData.STROKE_WIDTH, getStyle(element, NonGeometricData.STROKE_WIDTH));
		
		return data;
	}
	
	
	static String getMetaData(Element element) {
		String label = element.getAttributeNS(INKSCAPE, "label");
		if ((label != null) && (!label.equals(""))) {
			return label;
		}
		
		return element.getAttribute("id");
	}
	
	
	static String getStyle(Element element, String styleName) {
		String value = element.getAttribute(styleName);
		
		if ((value != null) && (value.length() > 0)) {
			return value;
		}
		
		String style = element.getAttribute("style");
		return extractStyle(style, styleName);
	}
	
	
	static String extractStyle(String style, String attribute) {
		if (style == null) {
			return "";
		}
		
		StringTokenizer tokens = new StringTokenizer(style,";");
		
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			String key = token.substring(0,token.indexOf(':'));
			if (key.equals(attribute)) {
				return token.substring(token.indexOf(':')+1);
			}
		}
		
		return "";
	}

	
	static Transform getTransform(Element element) {
		return getTransform(element, "transform");
	}
	
	
	static Transform getTransform(Element element, String attribute) {
		String str = element.getAttribute(attribute);
		if (str == null) {
			return new Transform();
		}
		
		if (str.equals("")) {
			return new Transform();
		} else if (str.startsWith("translate")) {
			str = str.substring(0, str.length()-1);
			str = str.substring("translate(".length());
			StringTokenizer tokens = new StringTokenizer(str, ", ");
			float x = Float.parseFloat(tokens.nextToken());
			float y = Float.parseFloat(tokens.nextToken());
			
			return Transform.createTranslateTransform(x,y);
		} else if (str.startsWith("matrix")) {
			float[] pose = new float[6];
			str = str.substring(0, str.length()-1);
			str = str.substring("matrix(".length());
			StringTokenizer tokens = new StringTokenizer(str, ", ");
			float[] tr = new float[6];
			for (int j=0;j<tr.length;j++) {
				tr[j] = Float.parseFloat(tokens.nextToken());
			}
			
			pose[0] = tr[0];
			pose[1] = tr[2];
			pose[2] = tr[4];
			pose[3] = tr[1];
			pose[4] = tr[3];
			pose[5] = tr[5];
			
			return new Transform(pose);
		}
		
		return new Transform();
	}
	
	
	static float getFloatAttribute(Element element, String attr) throws ParsingException {
		String cx = element.getAttribute(attr);
		if ((cx == null) || (cx.equals(""))) {
			cx = element.getAttributeNS(SODIPODI, attr);
		}
		
		try {
			return Float.parseFloat(cx);
		} catch (NumberFormatException e) {
			throw new ParsingException(element, "Invalid value for: "+attr, e);
		}
	}
	
	
	public static String getAsReference(String value) {
		if (value.length() < 2) {
			return "";
		}
		
		value = value.substring(1, value.length());
		
		return value;
	}
}
