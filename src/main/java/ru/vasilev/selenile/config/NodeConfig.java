package ru.vasilev.selenile.config;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class NodeConfig {
    /**
     * Desired capabilities.
     */
    private final DesiredCapabilities capabilities;

    /**
     * Configuration connecting to Selenium Grid Hub.
     */
    private final HashMap<String, Object> configuration;

    /**
     * Configuration constructor.
     *
     * @param capabilities  Device capability.
     * @param configuration Configuration connecting to Selenium Grid Hub.
     */
    public NodeConfig(DesiredCapabilities capabilities, HashMap<String, Object> configuration) {
        this.capabilities = capabilities;
        this.configuration = configuration;
    }

    /**
     * Gets device's desired capabilities.
     *
     * @return Device's desired capabilities.
     */
    public DesiredCapabilities getCapabilities() {
        return capabilities;
    }

    /**
     * Gets configuration value on {@code key}.
     *
     * @param key Key whose value need to get.
     * @return Key value.
     */
    public Object getValueOf(String key) {
        return configuration.get(key);
    }

    /**
     * Converts object to string.
     *
     * @return Object presentation.
     */
    @Override
    public String toString() {
        return String.format("NodeConfig{capabilities=%s, configuration=%s}",
                             capabilities,
                             configuration);
    }

    /**
     * Gets URL connection to Selenium Grid Hub.
     *
     * @return URL connection to Selenium Grid Hub.
     */
    public URL getHubConfiguration() throws MalformedURLException {
        return new URL(String.format("%s://%s:%s/wd/hub",
                                     getValueOf("hubProtocol"),
                                     getValueOf("hubHost"),
                                     getValueOf("hubPort").toString()
                                                          .replace(".0", "")));
    }
}
