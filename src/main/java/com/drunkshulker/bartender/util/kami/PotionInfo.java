package com.drunkshulker.bartender.util.kami;

import net.minecraft.potion.PotionEffect;

import java.util.concurrent.TimeUnit;

public class PotionInfo {
    public PotionEffect potionEffect;
    String name;
    int amplifier;

    public PotionInfo(PotionEffect potionEffect, String name, int amplifier) {
        this.amplifier = amplifier;
        this.name = name;
        this.potionEffect = potionEffect;
    }

    public String formattedTimeLeft() {
        long compensatedDuration = (long) (potionEffect.getDuration() / TpsCalculator.tickRate());
        long min = TimeUnit.SECONDS.toMinutes(compensatedDuration);
        long secs = TimeUnit.SECONDS.toSeconds(compensatedDuration) - min * 60;
        return String.format("(%d:%02d)", min, secs);
    }

    public String formattedName() {
        return name + " " + numberToRoman(amplifier + 1);
    }

    String numberToRoman(int number) {
        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return number + "" +
                        "";
        }
    }
}