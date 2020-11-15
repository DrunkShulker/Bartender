package com.drunkshulker.bartender.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.social.PlayerGroup;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class Update {

    Timer timer;

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.isSingleplayer()) return;
        if (timer == null) return;
        timer.cancel();
        timer.purge();
    }

    @SubscribeEvent
    public void onWorldReady(ClientConnectedToServerEvent event) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Minecraft mc = Minecraft.getMinecraft();

                if (!mc.isSingleplayer()) {
                    if (Minecraft.getMinecraft().player != null && mc.playerController.getCurrentGameType() != GameType.SPECTATOR) {

                        
                        if (!Bartender.MAPPED_BUS_INITIALIZED) Minecraft.getMinecraft()
                                .player.sendMessage(new TextComponentString("<" + Bartender.NAME + "> Failed to initialize IPC! Bodyguard will be unable to communicate with the group."));

                        
                        
                        
                        if (!PlayerGroup.members.contains(mc.player.getDisplayNameString()))
                            PlayerGroup.members.add(mc.player.getDisplayNameString());
                        
                        
                        if (PlayerGroup.DEFAULTS.contains(mc.player.getDisplayNameString())) {
                            PlayerGroup.mainAccount = PlayerGroup.DEFAULTS.get(0);
                            for (String dMember : PlayerGroup.DEFAULTS) {
                                if (!PlayerGroup.members.contains(dMember)) {
                                    PlayerGroup.members.add(dMember);
                                }
                            }
                        }
                        

						
						if(Bartender.OFFER_IMPORTS){
							if(Minecraft.getMinecraft().player!=null)
								Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<" + Bartender.NAME + "> Use '/gui import' to import your settings from previous version. Note that this only works on settings that were not edited in the update."));

						}

                        
                        if (!Bartender.UPDATES_CHECKED) {
                            Bartender.UPDATES_CHECKED = true;
                            Timer timerr = new Timer();
                            timerr.schedule(new TimerTask() {
                                public void run() {
                                    try {
                                        URL obj = new URL("https:/" + "" + "/g3fh-h56f-x9da-0asd.firebaseio.com/version.json");
                                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                                        con.setRequestMethod("GET");
                                        con.setRequestProperty("User-Agent", "Mozilla/5.0");
                                        int responseCode = con.getResponseCode();
                                        System.out.println("GET Response Code :: " + responseCode);
                                        if (responseCode == HttpURLConnection.HTTP_OK) { 
                                            BufferedReader in = new BufferedReader(new InputStreamReader(
                                                    con.getInputStream()));
                                            String inputLine;
                                            StringBuffer response = new StringBuffer();

                                            while ((inputLine = in.readLine()) != null) {
                                                response.append(inputLine);
                                            }
                                            in.close();

                                            
                                            if (Minecraft.getMinecraft().player != null) {
                                                JsonObject responseJson = new JsonParser().parse(response.toString()).getAsJsonObject();
                                                if (!responseJson.get("code").getAsString().equals(Bartender.VERSION)) {
                                                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<" + Bartender.NAME + "> A new version is available:"));
                                                    for (JsonElement line : responseJson.get("notes").getAsJsonArray()) {
                                                        String ll = line.getAsString();
                                                        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(" - " + ll));
                                                    }

                                                    System.out.println("A new version of Bartender available (" + response.toString().replaceAll("\"", "") + ")");
                                                } else System.out.println("Bartender is up-to-date.");
                                            } else {
                                                System.out.println("Could not display updates because player is null.");
                                            }
                                        } else {
                                            System.out.println("Failed to get update information.");
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Failed to get update information. Exception.");
                                    }
                                    timerr.cancel();
                                    
                                }
                            }, 40, 40);
                        }
                        timer.cancel();
                    }
                }
            }
        }, 4000, 4000);
    }
}
