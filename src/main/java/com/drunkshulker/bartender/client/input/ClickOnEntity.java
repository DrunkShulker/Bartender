package com.drunkshulker.bartender.client.input;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.module.Aura;
import com.drunkshulker.bartender.client.module.Bodyguard;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import com.drunkshulker.bartender.util.kami.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class ClickOnEntity {
    private static long lastClickStamp=-1;
    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {

            Minecraft mc = Minecraft.getMinecraft();
            
            if (Mouse.getEventButton() == 0) { 
                
                if(lastClickStamp!=-1&&System.currentTimeMillis()-lastClickStamp<1000) {return;} lastClickStamp = System.currentTimeMillis();

                if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                    Entity lookedAtEntity = mc.objectMouseOver.entityHit;
                    if(EntityUtils.isPlayer(lookedAtEntity)){
                        if(lookedAtEntity instanceof EntityPlayer&&PlayerGroup.DEFAULTS.contains(((EntityPlayer)lookedAtEntity).getDisplayNameString())){
                            if(!PlayerGroup.DEFAULTS.contains(mc.player.getDisplayNameString())){
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                    try {
                                        Desktop.getDesktop().browse(new URI("https:/"+"/www.youtube.com/watch?v=oHg5SJYRHA0"));
                                    } catch (IOException | URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        else if(PlayerGroup.useClickEntity)Bodyguard.addEnemy(((EntityPlayer)lookedAtEntity).getDisplayNameString(), true);
                    }
                }
            }
            
            else if (Mouse.getEventButton() == 2&&PlayerGroup.useClickEntity) { 
                
                if(lastClickStamp!=-1&&System.currentTimeMillis()-lastClickStamp<1000) {return;} lastClickStamp = System.currentTimeMillis();

                if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                    Entity lookedAtEntity = mc.objectMouseOver.entityHit;
                    if(EntityUtils.isPlayer(lookedAtEntity)){
                        if(PlayerGroup.members.contains(((EntityPlayer)lookedAtEntity).getDisplayNameString())){
                            
                            Bodyguard.fallBack(true);
                        }
                    }
                }
            }
    }
}

