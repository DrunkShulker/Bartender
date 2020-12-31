package com.drunkshulker.bartender.client.module;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiPanel;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.util.Config;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Nick {

    enum Mode {
        OFF,
        CLASSIC,
        PRIDE
    }

    static Mode state = Mode.OFF;
    static boolean custom = false;
    static String color = "0";

    static boolean bold = false;
    static boolean strike = false;
    static boolean underline = false;
    static int interval = 6000;
    static long lastUpdateStamp = System.currentTimeMillis();
    public static String customNick = "Gay";
    static int lastPrideIndex = 0;

    public static void clickAction(String title) {
        if (title.equals("clear")) {
            execute("off");
            while ((state != Mode.OFF)) {
                ClickGuiSetting.handleClick(ClickGuiSetting.fromString("nick->state"), false);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (state != Mode.PRIDE) return;
        if (Bartender.MC.player == null) return;
        if (Bartender.MC.isSingleplayer()) return;

        
        if (System.currentTimeMillis() - lastUpdateStamp > interval) {
            lastUpdateStamp = System.currentTimeMillis();
            update();
        }
    }

    public static void update() {
        if (state == Mode.OFF) return;
        if (Bartender.MC.player == null) return;
        if (Bartender.MC.isSingleplayer()) return;

        if (state == Mode.CLASSIC) {
            String playerName = ((custom) ? customNick : Bartender.MC.player.getDisplayNameString());
            if (bold) playerName = "&l" + playerName;
            if (strike) playerName = "&m" + playerName;
            if (underline) playerName = "&n" + playerName;
            playerName = "&" + color + playerName;
            execute(playerName);

        } else if (state == Mode.PRIDE) {

            String[] pridePattern = new String[]{"0",
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9",
                    "a",
                    "b",
                    "c",
                    "d",
                    "e",
                    "f"};
            String[] msg = ((custom) ? customNick.split("") : Bartender.MC.player.getDisplayNameString().split(""));
            String playerName = "";
            for (int i = 0, y = lastPrideIndex; i < msg.length; i++) {
                playerName += "&" + pridePattern[y] + msg[i];

                if (y >= pridePattern.length - 1) y = -1;
                y++;
            }
            lastPrideIndex++;
            if (lastPrideIndex >= pridePattern.length - 1) lastPrideIndex = 0;
            String effects = "";
            if (bold) effects = "&l" + effects;
            if (strike) effects = "&m" + effects;
            if (underline) effects = "&n" + effects;

            execute(effects + playerName);
        }
    }

    public static void setCustom(String c) {
        customNick = c;
        Bartender.msg("Local nick set: " + customNick + ". To use it, make sure u set nick->SOURCE->custom");
        Config.save();
    }

    public static void execute(String val) {
        Bartender.MC.player.sendChatMessage("/nick " + val);
    }

    public static void applyPreferences(ClickGuiSetting[] contents) {
        for (ClickGuiSetting setting : contents) {
            if (setting.title.equals("state")) {
                if (setting.value == 0) state = Mode.OFF;
                else if (setting.value == 1) state = Mode.CLASSIC;
                else if (setting.value == 2) state = Mode.PRIDE;
            } else if (setting.title.equals("source")) {
                custom = setting.value == 1;
            } else if (setting.title.equals("color")) {
                color = setting.values.get(setting.value).getAsString();
            } else if (setting.title.equals("bold")) {
                bold = setting.value == 1;
            } else if (setting.title.equals("strike")) {
                strike = setting.value == 1;
            } else if (setting.title.equals("underline")) {
                underline = setting.value == 1;
            } else if (setting.title.equals("interval")) {
                interval = Integer.parseInt(setting.values.get(setting.value).getAsString());
            }
        }
        update();
    }
}
