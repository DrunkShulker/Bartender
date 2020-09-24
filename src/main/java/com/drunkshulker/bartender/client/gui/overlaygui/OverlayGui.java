package com.drunkshulker.bartender.client.gui.overlaygui;

import com.drunkshulker.bartender.client.input.Keybinds;
import com.drunkshulker.bartender.client.module.EntityRadar;
import com.drunkshulker.bartender.util.Config;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.GuiHandler;
import com.drunkshulker.bartender.client.gui.clickgui.BeveledBox;
import com.drunkshulker.bartender.client.input.ChatObserver;
import com.drunkshulker.bartender.client.module.BaseFinder;
import com.drunkshulker.bartender.client.module.Bodyguard;
import com.drunkshulker.bartender.client.module.SafeTotemSwap;
import com.drunkshulker.bartender.client.social.PlayerGroup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;

import java.util.ArrayList;

public class OverlayGui extends Gui
{
    public static int groupListBottom = 4+48;
	public static boolean targetGuiActive = false;
	public static ArrayList<String> availableTargets = new ArrayList<>();
	public static int currentSelectedTargetIndex = 0;
	static long lastPopupMessage = 0;
	static String lastTargetedEnemy = "";

    
    
    

	public static String lastGuiAction = "";
	public static long lastGuiActionStamp = System.currentTimeMillis();

    public OverlayGui(Minecraft mc)
    {
        ScaledResolution scaled = new ScaledResolution(mc);
        int width = scaled.getScaledWidth();
        int height = scaled.getScaledHeight();

        
		if (mc.currentScreen==null&&GuiHandler.showBindInfo&&System.currentTimeMillis()-lastGuiActionStamp<1700) drawCenteredString(mc.fontRenderer, lastGuiAction, width / 2, (height / 2) + 36, Integer.parseInt("FFFFFF", 16));

		
		if(Bodyguard.enabled&&BaseFinder.enabled){
			drawCenteredString(mc.fontRenderer, "ERROR: YOU CANNOT HAVE BASEFINDER AND BODYGUARD ENABLED AT THE SAME TIME", width / 2, (height / 2) - 25, Integer.parseInt("FF0000", 16));
		}
        else if(ChatObserver.partyTPA)
        	drawCenteredString(mc.fontRenderer, "Party TPA enabled", width / 2, (height / 2) - 25, Integer.parseInt("FF0000", 16));
        else if(Bodyguard.enabled) {
        	drawCenteredString(mc.fontRenderer, Bodyguard.getStatusString(), width / 2, (height / 2) - 25, Integer.parseInt("FFAA00", 16));
        }
        else if(BaseFinder.enabled) {
        	drawCenteredString(mc.fontRenderer, BaseFinder.getStatusString(), width / 2, (height / 2) - 25, Integer.parseInt("FFAA00", 16));
        }

        
		if(targetGuiActive) {
			drawCenteredString(mc.fontRenderer, "Select target", width / 2, (height / 2) + 25, Integer.parseInt("62A2C4", 16));
		}else{
			
			if (System.currentTimeMillis()-lastPopupMessage<2500){
				drawCenteredString(mc.fontRenderer, "Eliminate " + lastTargetedEnemy, width / 2, (height / 2) + 25, Integer.parseInt("FF0000", 16));
			}
		}

		
        if(GuiHandler.ingameWaterMark)
		drawString(mc.fontRenderer,
				(((Bartender.IMPACT_INSTALLED)?"               + ":"")+Bartender.NAME +" "+Bartender.VERSION),
				4, 4, Integer.parseInt("AAAAAA", 16));
		
		
		if(!mc.isSingleplayer()&&GuiHandler.showGroup&&PlayerGroup.members.size()>0) {
			int x = 4;
			drawString(mc.fontRenderer,
					("Group: " +((PlayerGroup.groupAcceptTpa)?"/tpa ":"")+((PlayerGroup.groupAcceptTpaHere)?"/tpahere":"")), 
					x, 38, 
					Integer.parseInt((PlayerGroup.groupAcceptTpa)
							?"FF0000"
							:((PlayerGroup.groupAcceptTpaHere)
							?"FFD800"
							:"62A2C4"), 16));
			
			for (int i = 0; i < PlayerGroup.members.size(); i++) {
				String selfSelector = "> ";
				String mainDetector = " <";

				
				if(mc.player.getDisplayNameString().equals(PlayerGroup.members.get(i))) {
					drawString(mc.fontRenderer,selfSelector+PlayerGroup.members.get(i)+((PlayerGroup.mainAccount.equals(PlayerGroup.members.get(i)))?mainDetector:""), x, 4+48+(i*10),
							Integer.parseInt((!PlayerGroup.isPlayerOnline(PlayerGroup.members.get(i)))?"AAAAAA":"FFFFFF", 16));
				}
				
				else {
					String outOfRange = " ?";
					if(Bodyguard.enabled){
						if(Bodyguard.nearbyGroup.contains(PlayerGroup.members.get(i))) outOfRange = "";
					}else {
						if(EntityRadar.nearbyGroupMembers().contains(PlayerGroup.members.get(i))) outOfRange = "";
					}

					drawString(mc.fontRenderer,"  "+PlayerGroup.members.get(i)+((PlayerGroup.mainAccount.equals(PlayerGroup.members.get(i)))?mainDetector:"")+outOfRange, x+1, 4+48+(i*10),
							Integer.parseInt((!PlayerGroup.isPlayerOnline(PlayerGroup.members.get(i)))?"AAAAAA":"FFFFFF", 16));
				}

				groupListBottom = 4+48+(i*10);
			}
		}

		
		if(GuiHandler.showTargetListing&&!mc.isSingleplayer()){
			
			int x = 4;
			drawString(mc.fontRenderer,
					"Hostile: "+Bodyguard.currentEnemies.size(),
					x, groupListBottom+13,
					Integer.parseInt((Bodyguard.currentEnemies.isEmpty())?"62A2C4":"FF0000", 16));

			
			if(targetGuiActive){
				for (int i = 0; i < availableTargets.size(); i++) {
					String prefix = "  ", postFix ="";
					int extraPixel=1;
					if(i==currentSelectedTargetIndex) {
						prefix="> ";
						extraPixel=0;
					}

					if(EntityRadar.getEntityPlayer(availableTargets.get(i))==null){
						postFix = " ?";
					}

					drawString(mc.fontRenderer, prefix+availableTargets.get(i)+postFix,
							extraPixel+4, groupListBottom+27+(i*10),
							Integer.parseInt((i==currentSelectedTargetIndex)?"FFFFFF":((Bodyguard.currentEnemies.contains(availableTargets.get(i)))?"FF0000":"AAAAAA"), 16));
				}
			}
			
			else{
				
				ArrayList<String> combined = new ArrayList<>(EntityRadar.nearbyPotentialEnemiesToBodyGuard());
				for (String enemy:Bodyguard.currentEnemies) {
					if(!combined.contains(enemy)) combined.add(enemy);
				}
				for (int i = 0; i < combined.size(); i++) {
					String postFix ="";
					if(EntityRadar.getEntityPlayer(combined.get(i))==null){
						postFix = " ?";
					}

					drawString(mc.fontRenderer, "  "+combined.get(i)+postFix,
							5, groupListBottom+27+(i*10),
							Integer.parseInt((Bodyguard.currentEnemies.contains(combined.get(i)))?"FF0000":"AAAAAA", 16));
				}

			}
		}
		
		boolean survivalMode = mc.playerController.getCurrentGameType()!=GameType.CREATIVE&&mc.playerController.getCurrentGameType()!=GameType.SPECTATOR;
		
		
		if(SafeTotemSwap.enabled&&GuiHandler.showTots&&survivalMode&&SafeTotemSwap.totalCount>0) {
			final int ss = 20;
			int color = 0xFFFF0000;
			if(SafeTotemSwap.totemsReadyToSwitch) color = 0xFF00FF00; 
			BeveledBox.drawBeveledBox(width/2-(4*ss)-(ss/2), height-ss-1, width/2-(3*ss)-(ss/2), height-1, 2, color, color, 0x44B200FF);
			
			
			
			if(SafeTotemSwap.totalCount-SafeTotemSwap.totalUselessCount==SafeTotemSwap.totalCount){
				drawCenteredString(mc.fontRenderer, SafeTotemSwap.totalCount+"", (width/2)-109, height-33, Integer.parseInt("FFFFFF", 16));

			}else{
				drawCenteredString(mc.fontRenderer, SafeTotemSwap.totalCount-SafeTotemSwap.totalUselessCount+"", (width/2)-109, height-43, Integer.parseInt("FFFFFF", 16));
				drawCenteredString(mc.fontRenderer, "("+SafeTotemSwap.totalCount+")", (width/2)-109, height-33, Integer.parseInt("FFFFFF", 16));
			}
		}
		
		
		if(GuiHandler.txtHpAndFood&&survivalMode) {
			int food = mc.player.getFoodStats().getFoodLevel();
			float hp = mc.player.getHealth();
			
			drawString(mc.fontRenderer, hp+" hp", (width/2)-90, height-50, Integer.parseInt("FFFFFF", 16));
			drawString(mc.fontRenderer, food+" food", (width/2)-90, height-39, Integer.parseInt("FFFFFF", 16));
		}
    }
    
    

    public static void renderCustomTexture(int x, int y, int u, int v, int width, int height, ResourceLocation resourceLocation, float scale){
      Minecraft mc = Minecraft.getMinecraft();
      
      
      
      GL11.glPushMatrix();
      
      GL11.glScalef(scale, scale, scale);
      
      if(resourceLocation != null)
        mc.getTextureManager().bindTexture(resourceLocation);
      
      mc.ingameGUI.drawTexturedModalRect(x, y, u, v, width, height);
      
      GL11.glPopMatrix();
    }

    
	public static void targetConfirm() {
		if(!allowTargetInput()) return;
		if(!targetGuiActive) return;
		if(availableTargets.isEmpty()) return;

		String targetName = availableTargets.get(currentSelectedTargetIndex);
		if(Bodyguard.currentEnemies.contains(targetName)){
			
			Bodyguard.removeEnemy(targetName, true);
		}else{
			
			Bodyguard.addEnemy(targetName, true);
			lastTargetedEnemy = targetName;
			lastPopupMessage = System.currentTimeMillis();
		}
		
		targetGUIToggle();
	}

	public static void targetSelect(int direction) {
		if(!allowTargetInput()) return;
		if(!targetGuiActive) return;
		currentSelectedTargetIndex = currentSelectedTargetIndex + direction;
		if(currentSelectedTargetIndex<0) {
			currentSelectedTargetIndex = availableTargets.size()-1;
		} else if(currentSelectedTargetIndex>availableTargets.size()-1){
			currentSelectedTargetIndex=0;
		}
	}

	public static void targetGUIToggle() {
		if(!allowTargetInput()) return;
		availableTargets.clear();
		currentSelectedTargetIndex = 0;
    	targetGuiActive = !targetGuiActive;
    	if(targetGuiActive){
			
			availableTargets = new ArrayList<>(EntityRadar.nearbyPotentialEnemiesToBodyGuard());
			for (String enemy:Bodyguard.currentEnemies) {
				if(!availableTargets.contains(enemy)) availableTargets.add(enemy);
			}
		}
	}

	private static boolean allowTargetInput(){
		Minecraft mc = Minecraft.getMinecraft();
    	if(mc.player==null) return false;
    	if(mc.isSingleplayer()) return false;
    	if(!GuiHandler.showTargetListing) return false;
    	return true;
	}
}
