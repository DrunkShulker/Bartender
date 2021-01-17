package com.drunkshulker.bartender.client.commands.helper;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import com.drunkshulker.bartender.Bartender;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraftforge.client.ClientCommandHandler;

public class CommandsHelper {

    
    

    public static JsonArray scripts = null;

    static public void onJoinMp() {
        try {
            for (JsonElement s:scripts) {
                JsonObject obj = s.getAsJsonObject();
                if(obj.get("runWhenJoinServer").getAsBoolean()){
                    int totalDelay = obj.get("delayMillis").getAsInt() * obj.get("commands").getAsJsonArray().size();
                    runScript(obj);
                    try {
                        Thread.sleep(totalDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e){
            Bartender.msg("Script error when joining world. Check your scripts for typos and restart the game.");
        }
    }

    static void runScript(JsonObject script){
        try {
            Thread thread = new Thread(){
                public void run(){
                    JsonArray rows = script.get("commands").getAsJsonArray();
                    for (int i = 0; i < rows.size(); i++) {
                        try {
                            Thread.sleep(script.get("delayMillis").getAsInt());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                        if(rows.get(i).getAsString().charAt(0)=='#') {
                            BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute(rows.get(i).getAsString().replaceFirst("#",""));
                        }
                        
                        else
                            ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().player, rows.get(i).getAsString());
                    }

                }
            };
            thread.start();
        } catch (Exception e){
            e.printStackTrace();
            Bartender.msg("Script error. Check your scripts for typos and restart the game.");
        }
    }

    public static void findScript(String arg) {
        try {
            for (JsonElement s:scripts) {
                JsonObject obj = s.getAsJsonObject();
                if(obj.get("name").getAsString().equals(arg)){
                    runScript(obj);
                    return;
                }
            }
            Bartender.msg("Script not found.");
        } catch (Exception e){
            Bartender.msg("Script search error. Check your scripts for typos and restart the game.");
        }
    }
}
