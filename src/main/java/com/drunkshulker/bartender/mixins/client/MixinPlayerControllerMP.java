package com.drunkshulker.bartender.mixins.client;

import com.drunkshulker.bartender.Bartender;

import com.drunkshulker.bartender.util.salhack.events.blocks.EventGetBlockReachDistance;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerClickBlock;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerDamageBlock;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerDestroyBlock;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerResetBlockRemoving;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP
{
    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    public void getBlockReachDistance(CallbackInfoReturnable<Float> callback)
    {
        EventGetBlockReachDistance l_Event = new EventGetBlockReachDistance();
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.BlockReachDistance > 0.0f)
        {
            callback.setReturnValue(l_Event.BlockReachDistance);
            callback.cancel();
        }
    }

    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
    public void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> p_Info)
    {
        EventPlayerDamageBlock l_Event = new EventPlayerDamageBlock(posBlock, directionFacing);
        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
        {
            p_Info.setReturnValue(false);
            p_Info.cancel();
        }
    }
    
    @Inject(method = "resetBlockRemoving", at = @At("HEAD"), cancellable = true)
    public void resetBlockRemoving(CallbackInfo p_Info)
    {
        EventPlayerResetBlockRemoving l_Event = new EventPlayerResetBlockRemoving();

        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
        {
            p_Info.cancel();
        }
    }
    
    @Inject(method = "clickBlock", at = @At("HEAD"), cancellable = true)
    public void clickBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> callback)
    {
        EventPlayerClickBlock l_Event = new EventPlayerClickBlock(loc, face);

        Bartender.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
        {
            callback.setReturnValue(false);
            callback.cancel();
        }
    }

    @Inject(method = "onPlayerDestroyBlock", at = @At("HEAD"), cancellable = true)
    public void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info)
    {
        EventPlayerDestroyBlock l_Event = new EventPlayerDestroyBlock(pos);

        Bartender.EVENT_BUS.post(l_Event);

        if (l_Event.isCancelled())
        {
            info.setReturnValue(false);
            info.cancel();
        }
    }
}
