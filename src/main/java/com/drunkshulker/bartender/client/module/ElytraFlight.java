package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.kami.BlockUtils;
import com.drunkshulker.bartender.util.kami.EntityUtils;
import com.drunkshulker.bartender.util.salhack.MathUtil;
import com.drunkshulker.bartender.util.salhack.PlayerUtil;
import com.drunkshulker.bartender.util.salhack.Timer;

import com.drunkshulker.bartender.util.salhack.events.network.EventNetworkPacketEvent;
import com.drunkshulker.bartender.util.salhack.events.player.EventPlayerTravel;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listenable;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer;

import static com.drunkshulker.bartender.util.kami.BlockUtils.getGroundPosY;


public class ElytraFlight implements Listenable {
    public static boolean enabled = false;
    public static Mode mode = Mode.CONTROL;
    public static float speed = 1.82f;
    public static float boost = 1.82f;
    public static boolean easyTakeoff = false;
    public static boolean slowDown = false;
    
    
    
    public final boolean Accelerate = true;
    public static boolean spacePressed = false;
    public final int vAccelerationTimer = 1000;
    public final float RotationPitch = 0.0f;
    public final boolean CancelInWater = false;
    
    public final boolean PitchSpoof = false;
    private static boolean wasInLiquid = false;
    private Timer PacketTimer = new Timer();
    private Timer AccelerationTimer = new Timer();
    private Timer AccelerationResetTimer = new Timer();
    private Timer InstantFlyTimer = new Timer();
    private boolean SendMessage = false;
    
    static Minecraft mc = Minecraft.getMinecraft();

    public ElytraFlight() {
        setMode(mode);
    }

    public static float useSpeed = speed;

    public static void setMode(Mode newMode) {
        mode = newMode;
        if (mode == Mode.BOOST) useSpeed = boost;
        else useSpeed = speed;
    }

    public static void clickAction(String title) {
        if (title.equals("lock/unlock")) {
            if (mode == Mode.CONTROL) setMode(Mode.BOOST);
            else setMode(Mode.CONTROL);
        }
    }

    public enum Mode {
        BOOST, Tarzan, Superior, Packet, CONTROL
    }

    boolean spamPacket = false;

    @EventHandler
    private Listener<EventPlayerTravel> OnTravel = new Listener<>(event ->
    {
        if (!enabled) return;
        if (mc.player == null) return;
        
        if(mc.player.isSneaking()&&!mc.player.isElytraFlying()) {
            return;
        }
        if(spacePressed&&mc.player.isSneaking()&&!mc.player.isElytraFlying()) {
            return;
        }
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA
            ||mc.player.isCreative()||mc.player.isSpectator()) {
            return;
        }
        if (!mc.player.isElytraFlying() && mc.player.isSneaking()&&(EntityUtils.isInWater(mc.player)||EntityUtils.isAboveWater(mc.player))) {
            return;
        }
        if (!mc.player.isElytraFlying() && !mc.player.isSneaking()) {
            if (spamPacket) {
                if (!InstantFlyTimer.passed(300)) return;
                InstantFlyTimer.reset();
                spamPacket = false;
                
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_FALL_FLYING));
            } else if (!mc.player.onGround && easyTakeoff) {
                spamPacket = true;
                takeoff(event);
            }
            return;
        } else reset(false);

        switch (mode) {
            case BOOST:
            case Tarzan:
            case Packet:
                HandleNormalModeElytra(event);
                break;
            case Superior:
                HandleImmediateModeElytra(event);
                break;
            case CONTROL:
                HandleControlMode(event);
                break;
            default:
                break;
        }
    });

    public void HandleNormalModeElytra(EventPlayerTravel p_Travel) {
        double l_YHeight = mc.player.posY;

        boolean l_IsMoveKeyDown = mc.player.movementInput.moveForward > 0 || mc.player.movementInput.moveStrafe > 0;

        boolean l_CancelInWater = !mc.player.isInWater() && !mc.player.isInLava() && CancelInWater;

        if (mc.player.movementInput.jump) {
            p_Travel.cancel();
            Accelerate();
            return;
        }

        if (!l_IsMoveKeyDown) {
            AccelerationTimer.resetTimeSkipTo(-vAccelerationTimer);
        } else if ((mc.player.rotationPitch <= RotationPitch || mode == Mode.Tarzan) && l_CancelInWater) {
            if (Accelerate) {
                if (AccelerationTimer.passed(vAccelerationTimer)) {
                    Accelerate();
                    return;
                }
            }
            return;
        }

        p_Travel.cancel();
        Accelerate();
    }

    public void HandleImmediateModeElytra(EventPlayerTravel p_Travel) {
        if (mc.player.movementInput.jump) {
            double l_MotionSq = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);

            if (l_MotionSq > 1.0) {
                return;
            } else {
                double[] dir = MathUtil.directionSpeedNoForward(getSpeed());

                mc.player.motionX = dir[0];
                mc.player.motionY = -(getSpeed() / 10000f);
                mc.player.motionZ = dir[1];
            }

            p_Travel.cancel();
            return;
        }

        mc.player.setVelocity(0, 0, 0);

        p_Travel.cancel();

        double[] dir = MathUtil.directionSpeed(getSpeed());

        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX = dir[0];
            mc.player.motionY = -(getSpeed() / 10000f);
            mc.player.motionZ = dir[1];
        }

        if (mc.player.movementInput.sneak)
            mc.player.motionY = -getSpeed();

        mc.player.prevLimbSwingAmount = 0;
        mc.player.limbSwingAmount = 0;
        mc.player.limbSwing = 0;
    }

    
    static void takeoff(EventPlayerTravel event) {
        boolean highPingOptimize = false, easyTakeOff = true, timerControl = true;
        float minTakeoffHeight = 0.5f;
        float timerSpeed;
        double hoverTarget = -1.0;

        if (highPingOptimize) timerSpeed = 400.0f;
        else timerSpeed = 200.0f;
        double height;
        if (highPingOptimize) height = 0.0f;
        else height = minTakeoffHeight;
        boolean closeToGround = mc.player.posY <= getGroundPosY(false) + height && !wasInLiquid && !mc.integratedServerIsRunning;
        if (!easyTakeOff || mc.player.onGround) {
            if (mc.player.posY - getGroundPosY(false) > 4.0f) holdPlayer(event);
            reset(mc.player.onGround);
            return;
        }
        if (mc.player.motionY < 0 && !highPingOptimize || mc.player.motionY < -0.02) {
            if (closeToGround) {
                mc.timer.tickLength = 25.0f;
                return;
            }
            if (!highPingOptimize && !wasInLiquid && !mc.integratedServerIsRunning) {
                event.cancel();
                mc.player.setVelocity(0.0, -0.02, 0.0);
            }
            if (timerControl && !mc.integratedServerIsRunning) mc.timer.tickLength = timerSpeed * 2.0f;
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            hoverTarget = mc.player.posY + 0.2;
        } else if (highPingOptimize && !closeToGround) {
            mc.timer.tickLength = timerSpeed;
        }
    }

    public static void reset(boolean cancelFlying) {
        wasInLiquid = false;
        if (mc.player != null) {
            mc.timer.tickLength = 50.0f;
            mc.player.capabilities.setFlySpeed(0.05f);
            if (cancelFlying) mc.player.capabilities.isFlying = false;
        }
    }

    
    private static void holdPlayer(EventPlayerTravel event) {
        event.cancel();
        mc.timer.tickLength = 50.0f;
        mc.player.setVelocity(0.0, -0.01, 0.0);
    }

    public void Accelerate() {
        if (AccelerationResetTimer.passed(vAccelerationTimer)) {
            AccelerationResetTimer.reset();
            AccelerationTimer.reset();
            SendMessage = false;
        }

        float l_Speed = getSpeed();

        final double[] dir = MathUtil.directionSpeed(l_Speed);

        mc.player.motionY = -(getSpeed() / 10000f);

        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        if (mc.player.movementInput.sneak)
            mc.player.motionY = -getSpeed();

        mc.player.prevLimbSwingAmount = 0;
        mc.player.limbSwingAmount = 0;
        mc.player.limbSwing = 0;
    }

    private float getSpeed() {
        return useSpeed;
    }

    private void HandleControlMode(EventPlayerTravel p_Event) {
        final double[] dir = MathUtil.directionSpeed(getSpeed());

        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];

            mc.player.motionX -= (mc.player.motionX * (Math.abs(mc.player.rotationPitch) + 90) / 90) - mc.player.motionX;
            mc.player.motionZ -= (mc.player.motionZ * (Math.abs(mc.player.rotationPitch) + 90) / 90) - mc.player.motionZ;
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }
        if(!Freecam.enabled){
            if (mc.player.isSneaking()) mc.player.motionY = -1;
            else if (spacePressed) mc.player.motionY = 1;
            else mc.player.motionY = (-MathUtil.degToRad(mc.player.rotationPitch)) * mc.player.movementInput.moveForward;
        }
        mc.player.prevLimbSwingAmount = 0;
        mc.player.limbSwingAmount = 0;
        mc.player.limbSwing = 0;
        p_Event.cancel();
    }

    @EventHandler
    private Listener<EventNetworkPacketEvent> PacketEvent = new Listener<>(p_Event ->
    {
        if (!enabled) return;
        if (p_Event.getPacket() instanceof CPacketPlayer && PitchSpoof) {

            if(!mc.player.isElytraFlying()) return;
            if(!mc.player.onGround&&mc.player.isSneaking()&&!mc.player.isElytraFlying()) return;

            if (p_Event.getPacket() instanceof CPacketPlayer.PositionRotation && PitchSpoof) {
                CPacketPlayer.PositionRotation rotation = (CPacketPlayer.PositionRotation) p_Event.getPacket();

                mc.getConnection().sendPacket(new CPacketPlayer.Position(rotation.x, rotation.y, rotation.z, rotation.onGround));
                p_Event.cancel();
            } else if (p_Event.getPacket() instanceof CPacketPlayer.Rotation && PitchSpoof) {
                p_Event.cancel();
            }
        }
    });

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            switch (setting.title) {
                case "state":
                    enabled = setting.value == 1;
                    break;
                case "speed":
                    speed = setting.values.get(setting.value).getAsFloat();
                    setMode(mode);
                    break;
                case "boost":
                    boost = setting.values.get(setting.value).getAsFloat();
                    setMode(mode);
                    break;
                default:
                    break;
            }
        }
    }
}