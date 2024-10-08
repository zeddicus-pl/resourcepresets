package com.github.zeddicuspl.util;

import com.github.zeddicuspl.model.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigUtil {
    public static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    public static File jsonConfig = new File("config/resourcepresets.json");

    public static Config read() {
        try {
            if (!jsonConfig.exists() && jsonConfig.createNewFile()) {
                Map<String, List<String>> defaultMap = new HashMap<>();
                String json = gson.toJson(defaultMap, Config.class);
                FileWriter writer = new FileWriter(jsonConfig);
                writer.write(json);
                writer.close();
            }
            return gson.fromJson(new FileReader(jsonConfig), Config.class);
        } catch (IOException e) {
            System.out.println("Error reading configuration.");
        }
        return new Config();
    }

    public static void save(Config config) {
        try {
            FileWriter writer = new FileWriter(jsonConfig);
            writer.write(gson.toJson(config, new TypeToken<Config>(){}.getType()));
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving configuration.");
        }
    }
}
