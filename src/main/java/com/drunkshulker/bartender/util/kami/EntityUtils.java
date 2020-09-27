package com.drunkshulker.bartender.util.kami;

import com.drunkshulker.bartender.client.social.PlayerFriends;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;



public class EntityUtils {
    private static Minecraft mc = Minecraft.getMinecraft();


    public static boolean mobTypeSettings(Entity e, boolean mobs, boolean passive, boolean neutral, boolean hostile) {
        return mobs && (passive && isPassiveMob(e) || neutral && isCurrentlyNeutral(e) || hostile && isMobAggressive(e));
    }


    public static boolean isPassiveMob(Entity e) { 
        return e instanceof EntityAnimal || e instanceof EntityAgeable || e instanceof EntityTameable || e instanceof EntityAmbientCreature || e instanceof EntitySquid;
    }


    public static boolean isLiving(Entity e){
        return e instanceof EntityLivingBase;
    }


    public static boolean isFakeLocalPlayer(Entity entity){
        return entity != null && entity.getEntityId() == -100 && Minecraft.getMinecraft().player != entity;
    }


    public static Vec3d getInterpolatedAmount(Entity entity, float ticks){
        return new Vec3d(
                (entity.posX - entity.lastTickPosX) * ticks,
                (entity.posY - entity.lastTickPosY) * ticks,
                (entity.posZ - entity.lastTickPosZ) * ticks
        );
    }

    public static boolean isMobAggressive(Entity entity) {
        if (entity instanceof EntityPigZombie) {
            
            if (((EntityZombie) entity).isArmsRaised() || ((EntityPigZombie) entity).isAngry()) {
                return true;
            }
        } else if (entity instanceof EntityWolf) {
            return ((EntityWolf) entity).isAngry() &&
                    Minecraft.getMinecraft().player != ((EntityWolf) entity).getOwner();
        } else if (entity instanceof EntityEnderman) {
            return ((EntityEnderman) entity).isScreaming();
        } else if (entity instanceof EntityIronGolem) {
            return ((EntityIronGolem) entity).getRevengeTarget() == null;
        }
        return isHostileMob(entity);
    }

 
    public static boolean isCurrentlyNeutral(Entity entity){
        return isNeutralMob(entity) && !isMobAggressive(entity);
    }

 

    public static boolean isNeutralMob(Entity entity){
        return entity instanceof EntityPigZombie ||
                entity instanceof EntityWolf ||
                entity instanceof EntityEnderman ||
                entity instanceof EntityIronGolem;
    }

   

    public static boolean isFriendlyMob(Entity entity){
        return entity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(entity) ||
                entity.isCreatureType(EnumCreatureType.AMBIENT, false) ||
                entity instanceof EntityVillager;
    }

 

    public static boolean isHostileMob(Entity entity) {
        return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity);
    }

   

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedRenderPos(Entity entity, float ticks){
        return getInterpolatedPos(entity, ticks).subtract(Minecraft.getMinecraft().getRenderManager().renderPosX, Minecraft.getMinecraft().getRenderManager().renderPosY, Minecraft.getMinecraft().getRenderManager().renderPosZ);
    }



    public static boolean isDrivenByPlayer(Entity entityIn) {
        return Minecraft.getMinecraft().player != null && entityIn != null && entityIn == Minecraft.getMinecraft().player.getRidingEntity();
    }




    public static double[] calculateLookAt(double px, double  py, double  pz, EntityPlayer me) {
    	double dirx = me.posX - px;
    	double diry = me.posY - py;
    	double dirz = me.posZ - pz;
    	double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        
        pitch = Math.toDegrees(pitch);
        yaw = Math.toDegrees(yaw) + 90.0;
        return new double[] {yaw, pitch};
    }

    public static boolean isPlayer(Entity entity){
        return entity instanceof EntityPlayer;
    }

    public static double getRelativeX(float yaw){
        return Math.sin(Math.toRadians(-yaw));
    }

    public static double getRelativeZ(float yaw) {
        return Math.cos(Math.toRadians(yaw));
    }

    double[] getRotationFromVec3d(Vec3d vec3d) {
    	double x = vec3d.x;
    	double y = vec3d.y;
    	double z = vec3d.z;
    	double speed = Math.sqrt(x * x + y * y + z * z);

        x /= speed;
        y /= speed;
        z /= speed;

        double yaw = Math.toDegrees(Math.atan2(z, x)) - 90.0;
        double pitch = Math.toDegrees(-Math.asin(y));

        return new double[] {yaw, pitch};
    }

    double[] getRotationFromBlockPos(BlockPos posFrom, BlockPos posTo) {
    	double[] delta = new double[] {(posFrom.getX() - posTo.getX()), (posFrom.getY() - posTo.getY()), (posFrom.getZ() - posTo.getZ())};
        double yaw = Math.toDegrees(Math.atan2(delta[0], -delta[2]));
        double dist =  Math.sqrt(delta[0] * delta[0] + delta[2] * delta[2]);
        double pitch =  Math.toDegrees(Math.atan2(delta[1], dist));
        return new double[] {yaw, pitch};
    }

    void resetHSpeed(float speed, EntityPlayer player) {
    	Vec3d vec3d = new Vec3d(player.motionX, player.motionY, player.motionZ);
        float yaw = (float) Math.toRadians(getRotationFromVec3d(vec3d)[0]);
        player.motionX = Math.sin(-yaw) * speed;
        player.motionZ = Math.cos(yaw) * speed;
    }

    float getSpeed(Entity entity) {
        return (float) Math.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ);
    }

 


    public enum EntityPriority {
        DISTANCE, HEALTH
    }

    public static Entity getPrioritizedTarget(ArrayList<Entity> targetList,EntityPriority priority) {
    	if(targetList==null||targetList.isEmpty()) return null;
        Entity entity = targetList.get(0);

        switch (priority) {
		case DISTANCE:
			float distance = mc.player.getDistance(targetList.get(0));
            for (int j = 0; j < targetList.size(); j++) {
            	float currentDistance = mc.player.getDistance(targetList.get(j));
                if (currentDistance < distance) {
                    distance = currentDistance;
                    entity = targetList.get(j);
                }
			}
			break;
		case HEALTH:
			float health = ((EntityLivingBase)targetList.get(0)).getHealth();
            for (int j = 0; j < targetList.size(); j++) {
            	float currentHealth = ((EntityLivingBase)targetList.get(j)).getHealth();
                if (currentHealth < health) {
                    health = currentHealth;
                    entity = targetList.get(j);
                }
			}
			break;
		default:
			break;
		}
        return entity;
    }

    public static ArrayList<Entity> getTargetList(boolean[] player, boolean[] mobs, boolean ignoreWalls, boolean invisible, float range) {
        if (mc.world==null||mc.world.loadedEntityList == null) return new ArrayList<Entity>();
        ArrayList<Entity> entityList = new ArrayList<Entity>();
        for (Entity entity : mc.world.loadedEntityList) {

            if (!isLiving(entity)) continue;
            if (entity == mc.player) continue;
            if (entity instanceof EntityPlayer) {
                if (!player[0]) continue;
                if (PlayerGroup.members.contains((((EntityPlayer) entity).getDisplayNameString()))) continue; 
                if (PlayerFriends.friends.contains((((EntityPlayer) entity).getDisplayNameString()))) continue; 
                if (PlayerFriends.impactFriends.contains((((EntityPlayer) entity).getDisplayNameString()))) continue; 
                if (!player[2] && ((EntityLivingBase) entity).isPlayerSleeping()) continue;
            } else if (!mobTypeSettings(entity, mobs[0], mobs[1], mobs[2], mobs[3])) continue;

            if (mc.player.isRiding() && entity == mc.player.getRidingEntity()) continue; 
            if (mc.player.getDistance(entity) > range) continue; 
            if (((EntityLivingBase) entity).getHealth() <= 0) continue; 
            if (!ignoreWalls && !mc.player.canEntityBeSeen(entity) && !canEntityFeetBeSeen(entity)) continue;  
            if (!invisible && entity.isInvisible()) continue;
            entityList.add(entity);
        }
        return entityList;
    }

   


    boolean canEntityFeetBeSeen(Entity entityIn) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.eyeHeight, mc.player.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null;
    }

    float[] getFaceEntityRotation(Entity entity) {
        double diffX = entity.posX - mc.player.posX;
        double diffZ = entity.posZ - mc.player.posZ;
        double diffY = (entity.getEntityBoundingBox().getCenter().y) - (mc.player.posY + mc.player.getEyeHeight());

        double xz = Math.sqrt(diffX * diffX + diffZ * diffZ);
        double yaw = MathsUtils.normalizeAngle(Math.atan2(diffZ, diffX) * 180.0 / Math.PI - 90.0f);
        double pitch = MathsUtils.normalizeAngle(-Math.atan2(diffY, xz) * 180.0 / Math.PI);

        return new float[] {(float) yaw, (float) pitch};
    }

    void faceEntity(Entity entity) {
        float[] rotation = getFaceEntityRotation(entity);

        mc.player.rotationYaw = rotation[0];
        mc.player.rotationPitch = rotation[1];
    }






}