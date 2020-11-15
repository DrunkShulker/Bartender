package org.newdawn.slick.svg;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.ShapeRenderer;
import org.newdawn.slick.geom.TexCoordGenerator;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;


public class SimpleDiagramRenderer {
	
	protected static SGL GL = Renderer.get();
	
	
	public Diagram diagram;
	
	public int list = -1;
	
	
	public SimpleDiagramRenderer(Diagram diagram) {
		this.diagram = diagram;
	}
	
	
	public void render(Graphics g) {
		
		if (list == -1) {
			list = GL.glGenLists(1);
			GL.glNewList(list, SGL.GL_COMPILE);
				render(g, diagram);
			GL.glEndList();
		}
		
		GL.glCallList(list);
		
		TextureImpl.bindNone();
	}
	
	
	public static void render(Graphics g, Diagram diagram) {
		for (int i=0;i<diagram.getFigureCount();i++) {
			Figure figure = diagram.getFigure(i);

			if (figure.getData().isFilled()) {
				if (figure.getData().isColor(NonGeometricData.FILL)) {
					g.setColor(figure.getData().getAsColor(NonGeometricData.FILL));
					g.fill(diagram.getFigure(i).getShape());
					g.setAntiAlias(true);
					g.draw(diagram.getFigure(i).getShape());
					g.setAntiAlias(false);
				}
				
				String fill = figure.getData().getAsReference(NonGeometricData.FILL);
				if (diagram.getPatternDef(fill) != null){
					System.out.println("PATTERN");
				}
				if (diagram.getGradient(fill) != null) {
					Gradient gradient = diagram.getGradient(fill);
					Shape shape = diagram.getFigure(i).getShape();
					TexCoordGenerator fg = null;
					if (gradient.isRadial()) {
						fg = new RadialGradientFill(shape, diagram.getFigure(i).getTransform(), gradient);	
					} else {
						fg = new LinearGradientFill(shape, diagram.getFigure(i).getTransform(), gradient);
					}
					
			        Color.white.bind();
					ShapeRenderer.texture(shape, gradient.getImage(), fg);
				}
			}
			
			if (figure.getData().isStroked()) {
				if (figure.getData().isColor(NonGeometricData.STROKE)) {
					g.setColor(figure.getData().getAsColor(NonGeometricData.STROKE));
					g.setLineWidth(figure.getData().getAsFloat(NonGeometricData.STROKE_WIDTH));
					g.setAntiAlias(true);
					g.draw(diagram.getFigure(i).getShape());
					g.setAntiAlias(false);
					g.resetLineWidth();
				}
			}
	
			




		}
	}
}
