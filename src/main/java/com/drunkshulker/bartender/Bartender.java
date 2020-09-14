package com.drunkshulker.bartender;

import java.io.File;

import io.mappedbus.MappedBusReader;
import io.mappedbus.MappedBusWriter;
import org.lwjgl.opengl.Display;

import com.drunkshulker.bartender.client.CommandsRegistry;
import com.drunkshulker.bartender.client.ModulesRegistry;
import com.drunkshulker.bartender.client.gui.GuiConfig;
import com.drunkshulker.bartender.client.gui.GuiHandler;
import com.drunkshulker.bartender.client.input.ChatObserver;
import com.drunkshulker.bartender.client.input.KeyInputHandler;
import com.drunkshulker.bartender.client.input.Keybinds;
import com.drunkshulker.bartender.client.social.PlayerFriends;
import com.drunkshulker.bartender.proxy.CommonProxy;
import com.drunkshulker.bartender.util.Config;
import com.drunkshulker.bartender.util.Preferences;
import com.drunkshulker.bartender.util.forge.ForgeLoadingScreen;
import com.drunkshulker.bartender.util.forge.ModsHelper;
import com.drunkshulker.bartender.util.kami.InventoryUtils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Bartender.MOD_ID, name = Bartender.NAME, version = Bartender.VERSION)
public class Bartender
{
    public static final String MOD_ID = "bartender";
    public static final String NAME = "Bartender";
    public static final String VERSION = "1.1.5";
    public static final String ACCEPTED_VERSIONS = "(1.12.2)";
    public static final String CLIENT_PROXY = "com.drunkshulker.bartender.proxy.ClientProxy";
    public static final String COMMON_PROXY = "com.drunkshulker.bartender.proxy.CommonProxy";
    
    public static final InventoryUtils INVENTORY_UTILS = new InventoryUtils();

    public static String MINECRAFT_DIR, BARTENDER_DIR;
    public static boolean IMPACT_INSTALLED, KAMI_INSTALLED;
    public static boolean UPDATES_CHECKED = false;

    
    public static boolean MAPPED_BUS_INITIALIZED;
    public static MappedBusReader IPC_READER;
    public static MappedBusWriter IPC_WRITER;

    @Instance
	public static Bartender instance;
    
    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        
        try {
            
            if(!new File("/tmp").exists()) {
                new File("/tmp").mkdir();
            }

            final String ipcPath = "/tmp/ipc";
            
            File f= new File(ipcPath);
 }
            
            IPC_READER = new MappedBusReader(ipcPath, 2000000L, 32); 
            IPC_READER.open();
            
            IPC_WRITER = new MappedBusWriter(ipcPath, 2000000L, 32); 
            IPC_WRITER.open();
            
            MAPPED_BUS_INITIALIZED=true;
        }catch (Exception exception){
            MAPPED_BUS_INITIALIZED = false;
        }
        System.out.println(NAME+" ipc init result: "+MAPPED_BUS_INITIALIZED);

    	
    	MINECRAFT_DIR = event.getModConfigurationDirectory().getParentFile().getAbsolutePath();
    	if(!new File(Bartender.MINECRAFT_DIR + "/Bartender").exists()) {
    		new File(MINECRAFT_DIR+"/Bartender").mkdir();
    	}
    	BARTENDER_DIR = Bartender.MINECRAFT_DIR+"/Bartender";
    	
    	
    	ForgeLoadingScreen.modify();
    	Config.load();
    	GuiConfig.load();
    	Preferences.apply();
    	
    	
    	IMPACT_INSTALLED = ModsHelper.impactInstalled();
    	KAMI_INSTALLED = ModsHelper.kamiBlueInstalled();
    	
    	
    	PlayerFriends.loadImpactFriends();
    	
    	
    	setCustomTitle();
    	
    	
    	Keybinds.register();
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new ChatObserver());
        
        
        MinecraftForge.EVENT_BUS.register(new GuiHandler());
        
        
        CommandsRegistry.registerAll();

        
        ModulesRegistry.registerAll();
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
    	
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
    	setCustomTitle();
    }
    
    
	private void setCustomTitle() {
    	String title = Bartender.NAME;
    	if(IMPACT_INSTALLED) title = "Impact + "+title;
    	if(KAMI_INSTALLED)title = "KAMI Blue + "+title;
    	if(Minecraft.getMinecraft().getSession()!=null)
    		if(Minecraft.getMinecraft().getSession().getUsername()!=null)
    			title = title + " | Logged as "+Minecraft.getMinecraft().getSession().getUsername();
        Display.setTitle(title);
	}
}
