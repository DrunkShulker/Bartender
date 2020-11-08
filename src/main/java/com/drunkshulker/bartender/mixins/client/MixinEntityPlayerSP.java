package com.drunkshulker.bartender.mixins.client;


import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.util.salhack.events.MinecraftEvent;
import com.drunkshulker.bartender.util.salhack.events.player.*;
import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.EnumHand;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.IInteractionObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends MixinAbstractClientPlayer
{
    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;closeScreen()V"))
    public void closeScreen(EntityPlayerSP entityPlayerSP)
    {
        
        
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    public void closeScreen(Minecraft minecraft, GuiScreen screen)
    {
        
        
    }

    
    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo p_Info)
    {
        EventPlayerMove event = new EventPlayerMove(type, x, y, z);
        Bartender.EVENT_BUS.post(event);
        if (event.isCancelled())
        {
            super.move(type, event.X, event.Y, event.Z);
            p_Info.cancel();
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    public void OnPreUpdateWalkingPlayer(CallbackInfo p_Info)
    {
        EventPlayerMotionUpdate l_Event = new EventPlayerMotionUpdate(MinecraftEvent.Era.PRE);
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            p_Info.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"), cancellable = true)
    public void OnPostUpdateWalkingPlayer(CallbackInfo p_Info)
    {
        EventPlayerMotionUpdate l_Event = new EventPlayerMotionUpdate(MinecraftEvent.Era.POST);
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            p_Info.cancel();
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    public void onUpdate(CallbackInfo p_Info)
    {
        EventPlayerUpdate l_Event = new EventPlayerUpdate();
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            p_Info.cancel();
    }

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    public void swingArm(EnumHand p_Hand, CallbackInfo p_Info)
    {
        EventPlayerSwingArm l_Event = new EventPlayerSwingArm(p_Hand);
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            p_Info.cancel();
    }

    @Inject(method = "pushOutOfBlocks(DDD)Z", at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> callbackInfo)
    {
        EventPlayerPushOutOfBlocks l_Event = new EventPlayerPushOutOfBlocks(x, y, z);
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            callbackInfo.setReturnValue(false);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void swingArm(String p_Message, CallbackInfo p_Info)
    {
        EventPlayerSendChatMessage l_Event = new EventPlayerSendChatMessage(p_Message);
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
            p_Info.cancel();
    }

    @Override
    public void jump()
    {
        try
        {
            EventPlayerJump l_Event = new EventPlayerJump(motionX, motionZ);

            Bartender.EVENT_BUS.post(l_Event);
            
            if (!l_Event.isCancelled())
                super.jump();
        }
        catch (Exception v3)
        {
            v3.printStackTrace();
        }
    }

    
}
