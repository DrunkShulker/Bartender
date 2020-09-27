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


public class ClickOnEntity {
    private static long lastClickStamp=-1;
    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
            if(!PlayerGroup.useClickEntity) return;
            Minecraft mc = Minecraft.getMinecraft();
            
            if (Mouse.getEventButton() == 0) { 
                
                if(lastClickStamp!=-1&&System.currentTimeMillis()-lastClickStamp<1000) {return;} lastClickStamp = System.currentTimeMillis();

                if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                    Entity lookedAtEntity = mc.objectMouseOver.entityHit;
                    if(EntityUtils.isPlayer(lookedAtEntity)){
                        
                        Bodyguard.addEnemy(((EntityPlayer)lookedAtEntity).getDisplayNameString(), true);
                    }
                }
            }
            
            else if (Mouse.getEventButton() == 2) { 
                
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

