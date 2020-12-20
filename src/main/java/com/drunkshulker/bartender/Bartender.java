package com.drunkshulker.bartender;

import java.io.File;

import com.drunkshulker.bartender.client.cape.Capes;
import com.drunkshulker.bartender.client.gui.overlaygui.OverlayGui;
import com.drunkshulker.bartender.util.forge.ForgeEventProcessor;
import com.drunkshulker.bartender.util.salhack.FontManager;
import com.drunkshulker.bartender.util.salhack.TickRateManager;
import com.mojang.realmsclient.gui.ChatFormatting;
import io.mappedbus.MappedBusReader;
import io.mappedbus.MappedBusWriter;

import me.zero.alpine.fork.bus.EventBus;
import me.zero.alpine.fork.bus.EventManager;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.SystemUtils;
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
import com.drunkshulker.bartender.util.forge.ForgeModsHelper;
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
public class Bartender {
    public static final String MOD_ID = "bartender";
    public static final String NAME = "Bartender";
    public static final String VERSION = "1.3.1";
    public static final String ACCEPTED_VERSIONS = "(1.12.2)";
    public static final String CLIENT_PROXY = "com.drunkshulker.bartender.proxy.ClientProxy";
    public static final String COMMON_PROXY = "com.drunkshulker.bartender.proxy.CommonProxy";

    public static final InventoryUtils INVENTORY_UTILS = new InventoryUtils();
    public static final Minecraft MC = Minecraft.getMinecraft();
    public static String MINECRAFT_DIR, BARTENDER_DIR;
    public static boolean IMPACT_INSTALLED, KAMI_INSTALLED;
    public static boolean UPDATES_CHECKED = false, OFFER_IMPORTS = false;

    
    public static boolean MAPPED_BUS_INITIALIZED;
    public static MappedBusReader IPC_READER;
    public static MappedBusWriter IPC_WRITER;

    
    public static final EventBus EVENT_BUS = new EventManager();
    private static TickRateManager TICK_RATE_MANAGER = new TickRateManager();
    public static final FontManager FONT_MANAGER = new FontManager();

    @Instance
    public static Bartender instance;

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        
        try {
            
            if (!new File("/tmp").exists()) {
                if (!new File("/tmp").mkdir()) {
                    System.out.println("tmp create failed (win)");
                }
            }

            
            File deleteTarget = new File(Bartender.MINECRAFT_DIR + "/bartender-gui-backup.json");
            if (deleteTarget.exists()) {
                if (!deleteTarget.delete()) {
                    System.out.println("backup delete failed");
                }
            }
            
            File baritoneFolder = new File(Bartender.MINECRAFT_DIR + "/baritone");
            if (baritoneFolder.exists() && baritoneFolder.isDirectory()) {
                if (!baritoneFolder.delete()) {
                    System.out.println("baritone cache clear failed");
                }
            }
            String ipcPath = "/tmp/ipc";

            
            File f = new File(ipcPath);
            if (!f.delete()) {
                System.out.println("tmp delete failed");
            }
            
            IPC_READER = new MappedBusReader(ipcPath, 2000000L, 32); 
            IPC_READER.open();
            
            IPC_WRITER = new MappedBusWriter(ipcPath, 2000000L, 32); 
            IPC_WRITER.open();
            
            MAPPED_BUS_INITIALIZED = true;
        } catch (Exception exception) {
            MAPPED_BUS_INITIALIZED = false;
        }
        System.out.println(NAME + " ipc init result: " + MAPPED_BUS_INITIALIZED);

        
        MINECRAFT_DIR = event.getModConfigurationDirectory().getParentFile().getAbsolutePath();
        if (!new File(Bartender.MINECRAFT_DIR + "/Bartender").exists()) {
            if (!new File(MINECRAFT_DIR + "/Bartender").mkdir()) {
                System.out.println("bartender dir creation fail");
            }
        }
        BARTENDER_DIR = Bartender.MINECRAFT_DIR + "/Bartender";

        
        Config.load();
        GuiConfig.load();
        Preferences.apply();

        
        IMPACT_INSTALLED = ForgeModsHelper.impactInstalled();
        KAMI_INSTALLED = ForgeModsHelper.kamiBlueInstalled();

        
        PlayerFriends.loadImpactFriends();

        
        setCustomTitle();

        
        Keybinds.register();
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new ChatObserver());

        MinecraftForge.EVENT_BUS.register(new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new ForgeEventProcessor());

        CommandsRegistry.registerAll();
        ModulesRegistry.registerAll();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        FONT_MANAGER.Load();
        MinecraftForge.EVENT_BUS.register(new Capes());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        setCustomTitle();
        OverlayGui.loadLayout();
    }

    
    private void setCustomTitle() {
        String title = Bartender.NAME;
        if (IMPACT_INSTALLED) title = "Impact + " + title;
        if (KAMI_INSTALLED) title = "KAMI Blue + " + title;
        if (MC.getSession() != null)
            if (MC.getSession().getUsername() != null)
                title = title + " | Logged as " + MC.getSession().getUsername();
        Display.setTitle(title);
    }

    public static void msg(String text) {
        if (MC.player == null) {
            System.out.println(text);
        } else {
            MC.player.sendMessage(new TextComponentString(ChatFormatting.DARK_PURPLE.toString() + ChatFormatting.BOLD.toString() + "<" + NAME + "> " + ChatFormatting.RESET.toString() + text));
        }
    }

    public static TickRateManager GetTickRateManager() {
        return TICK_RATE_MANAGER;
    }
}
