package net.kalbskinder.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public class ConfigManager {
    private static final String CONFIG_PATH = "server.yml";
    private volatile Map<String, Object> config;

    public ConfigManager() {
        reload();
    }

    /**
     * Reloads server.yml from classpath resources.
     * Note: when packaged into a jar, editing the on-disk yml won't change the embedded resource.
     */
    public final void reload() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_PATH)) {
            if (inputStream == null) {
                throw new RuntimeException("server.yml not found");
            }

            Yaml yaml = new Yaml();
            Map<String, Object> loaded = yaml.load(inputStream);
            this.config = loaded != null ? loaded : Collections.emptyMap();
        } catch (Exception e) {
            System.err.println("Could not load " + CONFIG_PATH + ": " + e.getMessage());
            this.config = Collections.emptyMap();
        }
    }

    public String getString(String key, String defaultValue) {
        Object value = config != null ? config.get(key) : null;
        return value != null ? String.valueOf(value) : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        Object value = config != null ? config.get(key) : null;
        if (value == null) return defaultValue;
        if (value instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Backwards-compatible accessor.
     */
    public String get(String key) {
        Object value = config != null ? config.get(key) : null;
        return value != null ? String.valueOf(value) : null;
    }
}
