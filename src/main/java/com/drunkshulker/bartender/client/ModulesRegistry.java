package com.drunkshulker.bartender.client;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.input.ClickOnEntity;
import com.drunkshulker.bartender.client.ipc.IPCHandler;
import com.drunkshulker.bartender.client.module.*;
import com.drunkshulker.bartender.util.Update;

import com.drunkshulker.bartender.util.kami.TpsCalculator;
import net.minecraftforge.common.MinecraftForge;

public class ModulesRegistry {
	
	public static void registerAll(){	
        MinecraftForge.EVENT_BUS.register(new Update());
        MinecraftForge.EVENT_BUS.register(new SafeTotemSwap());
        MinecraftForge.EVENT_BUS.register(new Search());
        MinecraftForge.EVENT_BUS.register(new Aura());
        MinecraftForge.EVENT_BUS.register(new PlayerParticles());
        MinecraftForge.EVENT_BUS.register(new BaseFinder());
        MinecraftForge.EVENT_BUS.register(new Bodyguard());
        MinecraftForge.EVENT_BUS.register(new FlightBot());
        MinecraftForge.EVENT_BUS.register(new Dupe());
        MinecraftForge.EVENT_BUS.register(new AutoEat());
        MinecraftForge.EVENT_BUS.register(new ClickOnEntity());
        MinecraftForge.EVENT_BUS.register(new IPCHandler());
        MinecraftForge.EVENT_BUS.register(new AutoWalk());
        MinecraftForge.EVENT_BUS.register(new AutoFire());
        MinecraftForge.EVENT_BUS.register(new Scaffold());
        MinecraftForge.EVENT_BUS.register(new AutoSpawn());
        MinecraftForge.EVENT_BUS.register(new AutoRefill());
        Bartender.EVENT_BUS.subscribe(new Nametags());
        Bartender.EVENT_BUS.subscribe(new StorageESP());
        Bartender.EVENT_BUS.subscribe(new ElytraFlight());
        Bartender.EVENT_BUS.subscribe(new NewChunks());
        Bartender.EVENT_BUS.subscribe(new Tracers());
        Bartender.EVENT_BUS.subscribe(new Sprint());
        Bartender.EVENT_BUS.subscribe(new AntiOverlay());
        Bartender.EVENT_BUS.subscribe(new Freecam());
        Bartender.EVENT_BUS.subscribe(new Velocity());
        Bartender.EVENT_BUS.subscribe(new NoSlow());
        Bartender.EVENT_BUS.subscribe(new AutoTool());
        Bartender.EVENT_BUS.subscribe(new AntiHunger());
        Bartender.EVENT_BUS.subscribe(new HideArmor());
        Bartender.EVENT_BUS.subscribe(new FullBright());
        Bartender.EVENT_BUS.subscribe(new PeekPreview());
        Bartender.EVENT_BUS.subscribe(new TpsCalculator());
	}

}
