package com.drunkshulker.bartender.util.kami;


import com.drunkshulker.bartender.client.module.BaseFinder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CoordUtil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    public static final String coordsLogFilename = "KAMIBlueCoords.json";
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static Minecraft mc = Minecraft.getMinecraft();

    public static Coordinate getCurrentCoord() {
        Minecraft mc = Minecraft.getMinecraft();
        return new Coordinate((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ);
    }

    public static String[] getCurrentCoordsDesc() {
        if (mc.player == null) return new String[]{"", ""};
        String[] coords = new String[2];
        BlockPos pos = mc.player.getPosition();
        
        coords[0] = "" + String.format("%,d",pos.getX()) + ", " + String.format("%,d",pos.getY()) + ", " + String.format("%,d",pos.getZ());
        coords[1] = MathsUtils.getPlayerCardinal(mc).cardinalName;

        if (mc.player.dimension == -1) { 
            coords[1] = coords[1] + " | (" + String.format("%,d",pos.getX() * 8) + ", " + String.format("%,d",pos.getY() * 8) + ", " + String.format("%,d",pos.getZ() * 8) + ")";
        } else if (mc.player.dimension == 0) { 
            coords[1] = coords[1] + " | (" + String.format("%,d",pos.getX() / 8) + ", " + String.format("%,d",pos.getY() / 8) + ", " + String.format("%,d",pos.getZ() / 8) + ")";
        }

        return coords;
    }

    public static ArrayList<CoordinateInfo> readCoords(String filename) {
        try {
            ArrayList<CoordinateInfo> coords;
            coords = gson.fromJson(new FileReader(filename), new TypeToken<ArrayList<CoordinateInfo>>() {
            }.getType());
            if (coords != null) {
                return coords;
            } else {
                return new ArrayList<>();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                File file = new File(filename);
                file.createNewFile();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return new ArrayList<>();
        }
    }
}