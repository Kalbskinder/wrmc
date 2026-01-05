package net.kalbskinder.config;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class ConfigManager {
    private static final String CONFIG_PATH = "server.yml";
    private Map<String, Object> config;

    public ConfigManager() {
        loadConfig();
    }

    private void loadConfig() {
        try (InputStream inputStream =
                     getClass().getClassLoader().getResourceAsStream(CONFIG_PATH)) {

            if (inputStream == null) {
                throw new RuntimeException("server.yml not found");
            }

            Yaml yaml = new Yaml();
            this.config = yaml.load(inputStream);

        } catch (Exception e) {
            System.err.println("Could not load " + CONFIG_PATH + ": " + e.getMessage());
            this.config = Map.of();
        }
    }

    public String get(String key) {
        if (config == null || !config.containsKey(key)) {
            return null;
        }
        return String.valueOf(config.get(key));
    }
}
