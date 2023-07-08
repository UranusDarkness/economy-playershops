package uranus.economyplayershop.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Paths;

public class ConfigManager {
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        public static boolean loadConfig(){
            boolean success;
            try {
                File configDir = Paths.get("", "config").toFile();
                File configFile = new File(configDir, "economy-playershop.json");

                ConfigData configData = configFile.exists() ? GSON.fromJson(new InputStreamReader(new FileInputStream(configFile), "UTF-8"), ConfigData.class) : new ConfigData();

                {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8"));
                    writer.write(GSON.toJson(configData));
                    writer.close();
                }

                success = true;

            } catch (IOException e){
                success = false;
            }

            return success;
        }
}
