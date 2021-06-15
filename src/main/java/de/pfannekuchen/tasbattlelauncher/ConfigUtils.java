package de.pfannekuchen.tasbattlelauncher;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtils {

    public static Properties props = new Properties();
    private static File configuration;

    public static void setString(String category, String key, String value) {
        props.setProperty(category + ":" + key, value);
    }

    public static void setInt(String category, String key, int value) {
        props.setProperty(category + ":" + key, value + "");
    }

    public static void setBoolean(String category, String key, boolean value) {
        props.setProperty(category + ":" + key, Boolean.toString(value));
    }

    public static boolean getBoolean(String category, String key) {
        return Boolean.valueOf(props.getProperty(category + ":" + key, "false"));
    }

    public static String getString(String category, String key) {
        return props.getProperty(category + ":" + key, "null");
    }

    public static int getInt(String category, String key) {
        return Integer.valueOf(props.getProperty(category + ":" + key, "-1"));
    }

    public static void save() {
        try {
            FileWriter writer = new FileWriter(configuration);
            props.store(writer, "TAS Battle Launcher Configuration File");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init(File configuration) throws IOException {
        ConfigUtils.configuration = configuration;
        if (!configuration.exists()) configuration.createNewFile();
        props = new Properties();
        FileReader reader = new FileReader(configuration);
        props.load(reader);
        reader.close();
    }
}