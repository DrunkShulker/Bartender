package com.drunkshulker.bartender.client.module;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;


import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Tracers {

	final float portalColorRed = 0.788f;
	final float portalColorGreen = 0.176f;
	final float portalColorBlue = 0.901f;
	final float portalColorAlpha = 0.9f;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void drawAll(DrawBlockHighlightEvent event){
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.player==null) return;

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

		double d0 = mc.renderManager.renderPosX;
		double d1 = mc.renderManager.renderPosY;
		double d2 = mc.renderManager.renderPosZ;
		Vec3d pos1 = new Vec3d(d0, d1, d2);

		
	    Vec3d eyes = new Vec3d(0.0, 0.0, 1.0)
				.rotatePitch((float) -Math.toRadians(mc.player.rotationPitch)).rotateYaw((float)-Math
						.toRadians(mc.player.rotationYaw)).add(pos1);


			double yawRad = Math.toRadians(mc.player.rotationYaw);
			double pitchRad = Math.toRadians(mc.player.rotationPitch);
			double distance = -(mc.player.distanceWalkedModified + (mc.player.distanceWalkedModified - mc.player.prevDistanceWalkedModified) * pTicks());
			double cameraYaw = mc.player.prevCameraYaw + (mc.player.cameraYaw - mc.player.prevCameraYaw) * pTicks();
			double cameraPitch = mc.player.prevCameraPitch + (mc.player.cameraPitch - mc.player.prevCameraPitch) * pTicks();
			double xOffsetScreen = Math.sin(distance * Math.PI) * cameraYaw * 0.5;
			double yOffsetScreen = (((Math.abs(Math.cos(distance * Math.PI - 0.2) * cameraYaw) * 5.0) + cameraPitch) * Math.PI / 180.0) - Math.abs(Math.cos(distance * Math.PI) * cameraYaw);
			double xOffset = (-Math.cos(yawRad) * xOffsetScreen) + (-Math.sin(yawRad) * Math.sin(pitchRad) * yOffsetScreen);
			double yOffset = Math.cos(pitchRad) * yOffsetScreen;
			double zOffset = (-Math.sin(yawRad) * xOffsetScreen) + (Math.cos(yawRad) * Math.sin(pitchRad) * yOffsetScreen);
			eyes = eyes.subtract(xOffset, yOffset, zOffset);
		}

		GL11.glTranslated(-pos1.x, -pos1.y, -pos1.z);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST); 
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(3f);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		
		for (Entity e:EntityRadar.nearbyMobs()) {
			if(e!=null){
				Vec3d targetPos = e.getPositionVector();
				drawLineWithGL(
						eyes,
						targetPos,
						portalColorRed, portalColorGreen, portalColorBlue, portalColorAlpha
				);
			}
		}

		




		

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private static float pTicks() {
		if(Minecraft.getMinecraft().isGamePaused) return Minecraft.getMinecraft().renderPartialTicksPaused;
		else return Minecraft.getMinecraft().getRenderPartialTicks();
	}

	private void drawLineWithGL(Vec3d pos1, Vec3d pos2, float r, float g, float b, float a) {
		GL11.glColor4f(r, g, b, a);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		
		GL11.glVertex3d(pos1.x, pos1.y, pos1.z);
		GL11.glVertex3d(pos2.x, pos2.y, pos2.z);
		GL11.glEnd();
	}
}
