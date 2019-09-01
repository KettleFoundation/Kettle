package org.kettlemc.kettle.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.*;

public class KettleModConfig {
    private static final Gson gson = createGson();
    private static KettleModConfig instance;
    @SerializedName("enable_illegal_thread_access_warnings")
    public Boolean enableIllegalThreadAccessWarnings = true;
    @SerializedName("enable_kettle")
    public Boolean enableKettle = true;

    public static KettleModConfig instance() {
        return instance;
    }

    public static KettleModConfig loadConfig() {
        if (instance != null) {
            return instance;
        }

        File file = getConfigFile();
        KettleModConfig config;

        if (!file.exists()) {
            config = new KettleModConfig();
            config.saveConfig();
        } else {
            try (Reader reader = new FileReader(file)) {
                config = gson.fromJson(reader, KettleModConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to deserialize config from disk", e);
            }
        }

        instance = config;
        return config;
    }

    private static File getConfigDirectory() {
        return new File("config");
    }

    private static File getConfigFile() {
        return new File(getConfigDirectory(), "kettle.json");
    }

    private static Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    public void saveConfig() {
        File dir = getConfigDirectory();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Couldn't create configuration directory at '" + dir.getAbsolutePath() + "'");
            }
        } else if (!dir.isDirectory()) {
            throw new RuntimeException("Configuration directory at '" + dir.getAbsolutePath() + "' is not a directory");
        }

        try (Writer writer = new FileWriter(getConfigFile())) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
