package com.drunkshulker.bartender.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import com.drunkshulker.bartender.Bartender;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

public class AssetLoader {
	public File extractSchematic(String filename) throws IOException {
		
		if(!new File(Bartender.MINECRAFT_DIR + "/Bartender/schematics").exists()) {
			new File(Bartender.MINECRAFT_DIR+"/Bartender/schematics").mkdir();
		}
		try {
			File file = new File(Bartender.BARTENDER_DIR+"/schematics/"+filename);
            copyInputStreamToFile(Objects.requireNonNull(getClass().getClassLoader()
					.getResourceAsStream("assets/schematics/" + filename)), file);
			return file;
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean copyFileAndOverwrite(File from, File to){
		try{
			Path from1 = from.toPath();
			Path to1 = to.toPath();
			Files.copy(from1, to1, StandardCopyOption.REPLACE_EXISTING);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	
    private static void copyInputStreamToFile(InputStream inputStream, File file)
		throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        }

    }
	
	public JsonObject loadJson(String filename) {
		
		BufferedReader reader;
		StringBuilder builder = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader()
					.getResourceAsStream("assets/json/" + filename)), "UTF-8"));
		
			
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	builder.append(line);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    return new JsonParser().parse(builder.toString()).getAsJsonObject();
	}

}
