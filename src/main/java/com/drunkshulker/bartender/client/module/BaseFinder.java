package com.drunkshulker.bartender.client.module;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.gui.overlaygui.OverlayGui;
import com.drunkshulker.bartender.util.Config;
import com.drunkshulker.bartender.util.TimeUtils;
import com.drunkshulker.bartender.util.kami.MathsUtils;
import com.drunkshulker.bartender.util.kami.MathsUtils.Cardinal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class BaseFinder {
	
	public static int flightLevel = 210; 
	public final int WORLD_BORDER = 30000000;
	public final int TARGET_TRIGGER_RADIUS = 2;
	
	public static boolean enabled = false;
	public static boolean orbit = false, customTarget = false;
	public static boolean screenshot = true;
	public static boolean saveLog = true;
	public static boolean pauseOnFind = true;
	public static boolean disconnectOnGoal = false;
	public static boolean disconnectOnFind = false;

	public static boolean avoidSpawn = false;
	public static boolean pauseOnGoal = true;
	public static boolean disconnectOnMeetPlayer = true;
	private static boolean autoDisconnectSent = false;
	private static BlockPos orbitOrigin;

	public static int orbitDensity = 75;
	private static Quadron currentQuadron, lastQuadron;
	private static boolean visitedOtherQuadron = false;
	public static int totalHits = 0;
	public static BlockPos customTargetGoal = new BlockPos(0,0,0);

	private static long lastPointReachedStamp = System.currentTimeMillis();
	private static long lastTravelTargetSetStamp = System.currentTimeMillis();

	enum Quadron {
		XMIN_ZMIN,
		X_Z,
		XMIN_Z,
		X_ZMIN
	}

	public enum FinderTask {
		EXPLORE,
		GOTO_TARGET,
		CAPTURE_TARGET,
		PAUSE
	}
	
	private static BlockPos travelTarget, tempTarget;
	public static FinderTask currentTask = FinderTask.EXPLORE;
	private static ArrayList<String> loggedChunks = new ArrayList<>();

	@SubscribeEvent
    public void onPlayerLoggedIn(ClientConnectedToServerEvent event) {
		
    	GameSettings settings = Minecraft.getMinecraft().gameSettings;
		settings.viewBobbing = false;
		settings.pauseOnLostFocus = false;
		settings.saveOptions();

		
		OverlayGui.targetGuiActive=false;

		
		autoDisconnectSent = false;
	}
	
	@SubscribeEvent
	public void playerTick(InputUpdateEvent event)
	{
		if(!enabled) defaultState();
		if (enabled) {
			Minecraft mc = Minecraft.getMinecraft();
			
			
			if(mc.player==null) return;

			
			ArrayList<String> ps = EntityRadar.nearbyPlayersNoGroup();
			if(disconnectOnMeetPlayer&&(!ps.isEmpty())) {
				writeToLog("encounter | "+ ps.toString());
				logOut(ps.toString());
			}

			
			if(currentTask==FinderTask.PAUSE) {
				return;
			}

			if(currentTask==FinderTask.CAPTURE_TARGET) {
				
				event.getMovementInput().moveForward = 0f;
				lookAt(mc.player.posX,-1000,mc.player.posZ, mc.player, false);
				if(screenshot) {
					
					if(!new File(Bartender.MINECRAFT_DIR + "/Bartender/base-finder-logs").exists()) {
			    		new File(Bartender.MINECRAFT_DIR+"/Bartender/base-finder-logs").mkdir();
			    	}
					
					SimpleDateFormat fileNameFormatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");  
					Date date = new Date();
					String filename = fileNameFormatter.format(date)+".png";
					
					saveScreenshot(new File(Bartender.MINECRAFT_DIR+"/Bartender/base-finder-logs"), filename, 1920, 1080, Minecraft.getMinecraft().getFramebuffer());
				}

				
				if(pauseOnFind&&currentTask!=FinderTask.PAUSE) {
					setTask(FinderTask.PAUSE);
				}
				else {
					setTask(FinderTask.EXPLORE);
				}
				
				
				if(disconnectOnFind) {
					logOut("target blocks found");
				}
				tempTarget=null;
			}

			
			if(!mc.player.isElytraFlying()) {
				if(currentTask!=FinderTask.PAUSE) setTask(FinderTask.PAUSE);
				return;
			}
			
			
			if(travelTarget==null) setTravelTarget();
			if(travelTarget==null) return;
			getCurrentQuadron();
			
			if(avoidSpawn) {
				if(Math.abs(mc.player.getPosition().getX())<20000&&Math.abs(mc.player.getPosition().getZ())<20000)
					logOut("entered too close to spawn (20k)");
			}
			
			
			if(mc.player.posY<flightLevel) {
				if(ElytraFlight.mode!=ElytraFlight.Mode.CONTROL) ElytraFlight.mode= ElytraFlight.Mode.CONTROL;
				EntityPlayerSP p = mc.player;
				lookAt(p.posX, 1000, p.posZ, p, false);
				event.getMovementInput().moveForward = 1f;
			}else if(ElytraFlight.mode!=ElytraFlight.Mode.BOOST) ElytraFlight.mode= ElytraFlight.Mode.BOOST;
			
			
			else {
				if(currentTask==FinderTask.EXPLORE) {
					lookAt(travelTarget.getX(), 0, travelTarget.getZ(), mc.player, true);
					event.getMovementInput().moveForward = 1f;
					checkExploreTravelTargetReached();
				}
				else if(currentTask==FinderTask.GOTO_TARGET) {
					EntityPlayerSP p = Minecraft.getMinecraft().player;
					if(tempTarget==null) {
						Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> BaseFinder tempTarget is null!"));
						setTask(FinderTask.EXPLORE);
						return;
					} else {
						lookAt(tempTarget.getX(), 0, tempTarget.getZ(), p, true);
						event.getMovementInput().moveForward = 1f;
						
						
						if(distance2D(p.getPosition(), tempTarget)<TARGET_TRIGGER_RADIUS) {
							
							Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> BaseFinder reached temp target."));
							setTask(FinderTask.CAPTURE_TARGET);
						}
					}			
				}

			}
			
		}
	}

	private void getCurrentQuadron() {
		BlockPos pp = Minecraft.getMinecraft().player.getPosition();
		boolean xMinus = pp.getX()<0;
		boolean zMinus = pp.getZ()<0;
		
		if(xMinus&&zMinus) {
			currentQuadron = Quadron.XMIN_ZMIN;
		}
		if(!xMinus&&!zMinus) {
			currentQuadron = Quadron.X_Z;
		}
		if(xMinus&&!zMinus) {
			currentQuadron = Quadron.XMIN_Z;
		}
		if(!xMinus&&zMinus) {
			currentQuadron = Quadron.X_ZMIN;
		}

		
		if(lastQuadron==null) lastQuadron=currentQuadron;
		else if(lastQuadron!=currentQuadron) {
			visitedOtherQuadron=true;
			lastQuadron = currentQuadron;
		}
	}

	private void checkExploreTravelTargetReached() {
		EntityPlayerSP p = Minecraft.getMinecraft().player;
		if(orbit) {
			if(System.currentTimeMillis()-lastPointReachedStamp<3000){
				return;
			}
			
			if(distance2D(p.getPosition(), travelTarget)<TARGET_TRIGGER_RADIUS) {
				lastPointReachedStamp = System.currentTimeMillis();
				
				if(distance2D(p.getPosition(), orbitOrigin)<TARGET_TRIGGER_RADIUS) {
					if(visitedOtherQuadron) {
						
						Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> "+"orbit complete. next orbit is "+orbitDensity+" up | player pos: " + p.getPosition().toString()));

						writeToLog("orbit complete. next orbit is "+orbitDensity+" up | player pos: " + p.getPosition().toString());
						visitedOtherQuadron = false;
						
						if(currentQuadron==Quadron.X_Z) orbitOrigin=orbitOrigin.add(new Vec3i(orbitDensity,0,orbitDensity));
						else if(currentQuadron==Quadron.X_ZMIN)orbitOrigin= orbitOrigin.add(new Vec3i(orbitDensity,0,-orbitDensity));
						else if(currentQuadron==Quadron.XMIN_Z)orbitOrigin= orbitOrigin.add(new Vec3i(-orbitDensity,0,orbitDensity));
						else if(currentQuadron==Quadron.XMIN_ZMIN)orbitOrigin= orbitOrigin.add(new Vec3i(-orbitDensity,0,-orbitDensity));
						else Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> should never happen"));

						travelTarget = orbitOrigin;
					}else {
						
						Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> arrived to new orbit origin at " + p.getPosition().toString()));
						setTravelTarget();
					}
				}
				else if(visitedOtherQuadron){
					
					Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> orbit point reached at " + p.getPosition().toString()));
					
					setTravelTarget();
				}
			}
		}
		else {
			if(Math.abs(p.getPosition().getX())>WORLD_BORDER-17||Math.abs(p.getPosition().getZ())>WORLD_BORDER-17) {
				writeToLog("world border reached at " + p.getPosition().toString());
				if(pauseOnGoal||customTarget) {
					setTask(FinderTask.PAUSE);
				} 
				else if(disconnectOnGoal) logOut("goal reached");
				else {
					Random r = new Random();
					int randomX = r.nextInt(WORLD_BORDER)*((r.nextBoolean())?1:-1);
					int randomZ = r.nextInt(WORLD_BORDER)*((r.nextBoolean())?1:-1);
					writeToLog("choosing a random travel destination "+randomX+","+randomZ+" at " + p.getPosition().toString());
					lookAt(randomX, 0, randomZ, p, true);
					setTravelTarget();
				}
			}
			else if(distance2D(p.getPosition(), travelTarget)<TARGET_TRIGGER_RADIUS){
				if(disconnectOnGoal) logOut("logout on goal reached");
				else if(pauseOnGoal&&currentTask!=FinderTask.PAUSE) setTask(FinderTask.PAUSE);
			}
		}
	}

	
	public static int distance2D(BlockPos pos1, BlockPos pos2) {
		return (int) Math.sqrt(
				(pos2.getX() - pos1.getX()) * (pos2.getX() - pos1.getX()) +
				(pos2.getZ() - pos1.getZ()) * (pos2.getZ() - pos1.getZ())		
		);
	}

	public static double distance2Ddouble(Vec3d pos1, Vec3d pos2) {
		return Math.sqrt(
				(pos2.x - pos1.x) * (pos2.x - pos1.x) +
						(pos2.z - pos1.z) * (pos2.z - pos1.z)
		);
	}

	private void defaultState() {
		currentTask = FinderTask.EXPLORE;
		travelTarget = null;
		tempTarget = null;
		orbitOrigin = null;
		visitedOtherQuadron = false;
		totalHits = 0;
		if(loggedChunks!=null)loggedChunks.clear();
	}

	private void setTravelTarget() {

		if(System.currentTimeMillis()-lastTravelTargetSetStamp<1000){
			return;
		}
		lastTravelTargetSetStamp = System.currentTimeMillis();

		if(customTarget) {
			travelTarget = customTargetGoal;
			return;
		}
		
		Cardinal c = MathsUtils.getPlayerCardinal(Minecraft.getMinecraft());
		BlockPos playerPos = Minecraft.getMinecraft().player.getPosition();
		
		if(orbit) {
			if(orbitOrigin==null) {
				orbitOrigin = playerPos;
			}
			
			if(currentQuadron==null) getCurrentQuadron();
			
			int x,z;
			switch (currentQuadron) {
			case X_Z:
				x = Math.abs(orbitOrigin.getX());
				z = Math.abs(orbitOrigin.getZ())*-1;
				
				break;
			case X_ZMIN:
				x = Math.abs(orbitOrigin.getX())*-1;
				z = Math.abs(orbitOrigin.getZ())*-1;		
				break;
			case XMIN_Z:
				x = Math.abs(orbitOrigin.getX());
				z = Math.abs(orbitOrigin.getZ());
				break;
			case XMIN_ZMIN:
				x = Math.abs(orbitOrigin.getX())*-1;
				z = Math.abs(orbitOrigin.getZ());
				break;
			default:
				x = orbitOrigin.getX();
				z = orbitOrigin.getZ();
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> Error on getting the next orbit origin!"));
				break;
			}
			
			travelTarget = new BlockPos(x,0,z);
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> New travelTarget is "+travelTarget.toString()));
		} 
		
		else {		
			switch (c) {
				case POS_Z:
					travelTarget = playerPos.add(new Vec3i(0,0,WORLD_BORDER));
					break;
				case  NEG_X_POS_Z:
					travelTarget = playerPos.add(new Vec3i(-WORLD_BORDER,0,WORLD_BORDER));
					break;
				case  NEG_X:
					travelTarget = playerPos.add(new Vec3i(-WORLD_BORDER,0,0));
					break;
				case  NEG_X_NEG_Z:
					travelTarget = playerPos.add(new Vec3i(-WORLD_BORDER,0,-WORLD_BORDER));
					break;
				case  NEG_Z:
					travelTarget = playerPos.add(new Vec3i(0,0,-WORLD_BORDER));
					break;
				case  POS_X_NEG_Z:
					travelTarget = playerPos.add(new Vec3i(WORLD_BORDER,0,-WORLD_BORDER));
					break;
				case  POS_X:
					travelTarget = playerPos.add(new Vec3i(WORLD_BORDER,0,0));
					break;
				case  POS_X_POS_Z:
					travelTarget = playerPos.add(new Vec3i(WORLD_BORDER,0,WORLD_BORDER));
					break;
				default:
					Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> BaseFinder failed to get current facing direction."));
					break;
			}
		}
	}

	public static void logOut(String ps){
		if(autoDisconnectSent) return;
		autoDisconnectSent = true;
		writeToLog("force disconnect | "+Minecraft.getMinecraft().player.getPosition().toString() + " | " + ps);
		Minecraft.getMinecraft().world.sendQuittingDisconnectingPacket();
	}	
	
	public static void lookAt(double px, double py, double pz , EntityPlayer me, boolean lookDown){
	    double dirx = me.getPosition().getX() - px;
	    double diry = me.getPosition().getY() - py;
	    double dirz = me.getPosition().getZ() - pz;

	    double len = Math.sqrt(dirx*dirx + diry*diry + dirz*dirz);

	    dirx /= len;
	    diry /= len;
	    dirz /= len;

	    double pitch = Math.asin(diry);
	    double yaw = Math.atan2(dirz, dirx);
	    
	    
	    pitch = pitch * 180.0 / Math.PI;
	    if(lookDown) pitch = 79;
	    yaw = yaw * 180.0 / Math.PI;

	    yaw += 90f;
	    me.rotationPitch = (float)pitch;
	    me.rotationYaw = (float)yaw;
	}
	
	public static void saveScreenshot(File dir, String screenshotName, int width, int height, Framebuffer buffer) {
	  try {
	    BufferedImage bufferedimage = ScreenShotHelper.createScreenshot(width, height, buffer);
	    File file2 = new File(dir, screenshotName);
	    net.minecraftforge.client.ForgeHooksClient.onScreenshot(bufferedimage, file2);
	    ImageIO.write(bufferedimage, "png", file2);
	    writeToLog("saved screenshot: "+screenshotName);
	  } catch(Exception exception) {writeToLog("failed to save screenshot: "+screenshotName);}
	}

	public static void applyPreferences(ClickGuiSetting[] contents) {
		for (ClickGuiSetting setting : contents) {
			switch (setting.title) {
			case "state":
				if(enabled&&setting.value==0) ElytraFlight.setMode(ElytraFlight.Mode.CONTROL);
				enabled = setting.value == 0;
				break;
			case "travel":
				if(orbit!=(setting.value == 0)
						||customTarget != (setting.value ==2)) {
					if(enabled)
					setTask(FinderTask.PAUSE);
				}
				orbit = setting.value == 0;
				customTarget = setting.value ==2;
				break;
			case "save log":
				saveLog = setting.value == 0;
				break;
			case "screenshot":
				screenshot = setting.value == 0;
				break;
			case "disconnect":
				disconnectOnGoal = setting.value == 1;
				disconnectOnFind = setting.value == 2;
				break;
			case "avoid spawn":
				avoidSpawn = setting.value == 0;
				break;
			case "pause":
				pauseOnFind = setting.value == 0;
				pauseOnGoal = setting.value == 1;
				break;
			case "anti encounter":
				disconnectOnMeetPlayer = setting.value == 0;
				break;
			case "orbit density":
				orbitDensity = Integer.parseInt(setting.values.get(setting.value).getAsString());
				break;
			case "flight level":
				flightLevel = Integer.parseInt(setting.values.get(setting.value).getAsString());
				break;
			default:
				break;
			}
		}	
	}

	public static void targetFound(int count, Chunk chunk) {
		if(!enabled) return;
		
		if(!loggedChunks.contains(chunk.x+","+chunk.z)){
			loggedChunks.add(chunk.x+","+chunk.z);
			totalHits += count;
			if(saveLog) {
				String logMsg = count+" targets at chunk "+chunk.x+","+chunk.z + " | player pos " + Minecraft.getMinecraft().player.getPosition().toString() ;
				writeToLog(logMsg);
			}
			if(currentTask == FinderTask.EXPLORE) {
				tempTarget = new BlockPos(chunk.x*16, 0, chunk.z*16);
				setTask(FinderTask.GOTO_TARGET);
			}
		}
	}

	public static void setTask(FinderTask task) {
		
		currentTask = task;
		if(task == FinderTask.PAUSE) {
			writeToLog("pause");
		}
		else if(task==FinderTask.CAPTURE_TARGET) {
		}
		else if(task==FinderTask.EXPLORE){
			totalHits = 0;
		}
	}
	
	private static void writeToLog(String logMsg) {
		if(!saveLog) return;
		Date date = new Date();
		SimpleDateFormat fileNameFormatter = new SimpleDateFormat("dd-MM-yyyy");  	
		String filename = fileNameFormatter.format(date)+".txt";
		SimpleDateFormat timestampFormatter = new SimpleDateFormat("HH:mm:ss");  	
		String timestamp = timestampFormatter.format(date);
		logMsg = timestamp + " | " + logMsg;
		
		
		if(!new File(Bartender.MINECRAFT_DIR + "/Bartender/base-finder-logs").exists()) {
    		new File(Bartender.MINECRAFT_DIR+"/Bartender/base-finder-logs").mkdir();
    	}
		
		final String path = Bartender.MINECRAFT_DIR+"/Bartender/base-finder-logs/"+filename;
		
		if(new File(path).exists()) {
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(path, true);
				fos.write(System.getProperty("line.separator").getBytes());
			    fos.write(logMsg.getBytes());
			    fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
		
		else {
			try {
				Config.writeFile(path, logMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static String getStatusString() {
		if(currentTask==FinderTask.PAUSE) return totalHits + " | "+currentTask.toString();
		else return totalHits + " | "+currentTask.toString();
	}

	public static void clickAction(String action) {
		EntityPlayerSP p = Minecraft.getMinecraft().player;
		if(p==null) return;
		if ("pause/continue".equals(action)) {
			pauseOrContinue();
		}
	}

	public static void pauseOrContinue() {
		if(enabled){
			if(currentTask==FinderTask.PAUSE){
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> BaseFinder continues exploration!"));
				setTask(FinderTask.EXPLORE);
			}
			else{
				setTask(FinderTask.PAUSE);
			}
		}else {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> BaseFinder is not enabled!"));
		}
	}

	public static void setCustomGoal(int x, int z) {
		customTargetGoal = new BlockPos(x,0,z);
		Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> BaseFinder custom goal set to "+customTargetGoal.toString()+". Set 'travel: custom' and 'state: ON' to start you journey."));
		Config.save();
	}

	public static void prepareTpa() {
		if(enabled) {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<Bartender> BaseFinder pausing for group TPA"));
			setTask(FinderTask.PAUSE);
		}
	}
}
