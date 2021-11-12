package ru.vasilev.selenile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.vasilev.selenile.config.NodeConfig;
import ru.vasilev.selenile.deserializer.CapabilityDeserializer;
import ru.vasilev.selenile.device.AndroidDevice;
import ru.vasilev.selenile.device.Device;
import ru.vasilev.selenile.device.IOSDevice;
import ru.vasilev.selenile.driver.MobileDriverProvider;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DevicePool {
    /**
     * Multi thread driver logger.
     */
    protected static final Logger LOGGER = LogManager.getLogger("MobileDriverParallel");

    /**
     * Default path to nodes configuration.
     */
    private static final String NODES_CONFIG_PATH = ".\\src\\test\\resources\\node-config\\";

    /**
     * Devices pool with availability flags.
     */
    private static final ConcurrentHashMap<Device, Boolean> POOL = new ConcurrentHashMap<>();

    /**
     * Devices allowed for using.
     */
    private static HashSet<String> emulatorForUse = new HashSet<>();

    /**
     * Is pool ready.
     */
    private static boolean poolIsReady = false;

    /**
     * Private constructor
     */
    private DevicePool() {
    }

    /**
     * Prepares devices pool. Reads all configurations and creates {@link Device}s.
     */
    public static synchronized void prepareDevicesPool() {
        if (poolIsReady) {
            return;
        }
        getUsedEmulatorsList();
        String filePath = getNodeFilesPath();
        File[] nodeConfigFiles = (new File(filePath)).listFiles((dir, name) -> name.toLowerCase()
                                                                                   .endsWith(".json"));
        for (File nodeConfigFile : Objects.requireNonNull(nodeConfigFiles)) {
            LOGGER.info("Node config file processing:{}{}", filePath, nodeConfigFile.getAbsolutePath());
            try (Reader nodeConfigFileReader = new FileReader(nodeConfigFile)) {
                final Gson gson = new GsonBuilder().registerTypeAdapter(DesiredCapabilities.class,
                                                                        new CapabilityDeserializer())
                                                   .create();
                NodeConfig nodeConfig = gson.fromJson(nodeConfigFileReader, NodeConfig.class);
                LOGGER.info("Node configuration:{}", nodeConfig);
                LOGGER.info("Selenium grid hub: {}", nodeConfig.getHubConfiguration());
                Device device;
                switch (MobileDriverProvider.getMobileSystemType()) {
                    case IOS:
                        device = new IOSDevice(nodeConfig);
                        break;
                    case ANDROID:
                        device = new AndroidDevice(nodeConfig);
                        break;
                    default:
                        continue;
                }
                if (emulatorForUse.isEmpty() || emulatorForUse.contains(device.getDeviceName())) {
                    LOGGER.info("Found device: {}", device);
                    addDevice(device);
                }

            } catch (IOException | JsonSyntaxException | JsonIOException e) {
                LOGGER.error("Reading configuration error <{}> {}", nodeConfigFile.getName(), e.getMessage());
                e.printStackTrace();
            }
        }
        if (POOL.size() == 0) {
            throw new IllegalStateException("Pool is empty");
        }
        LOGGER.info("Pool is ready to work. {}", POOL);
        poolIsReady = true;
    }

    /**
     * Gets free device from pool.
     *
     * @return Free device from pool.
     */
    public static synchronized Device deviceRequest() {
        LOGGER.info("Getting available device from pool: {}", POOL);
        for (Map.Entry<Device, Boolean> entry : POOL.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                LOGGER.info("Available device is found {}.", entry.getKey().getDeviceName());
                POOL.replace(entry.getKey(), false);
                LOGGER.info("Device {} is unavailable", entry.getKey().getDeviceName());
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Releases device in pool.
     *
     * @param usedDevice Device for releasing in pool.
     */
    public static synchronized void freeDevice(Device usedDevice) {
        LOGGER.info("Device {} releasing", usedDevice.getDeviceName());
        POOL.replace(usedDevice, false, true);
        LOGGER.info("Device {} released", usedDevice.getDeviceName());
    }

    /**
     * Marks device as unavailable.
     *
     * @param device Device for marking.
     */
    public static synchronized void addDevice(Device device) {
        LOGGER.info("Adding device {} into pool.", device);
        if (Objects.isNull(POOL.putIfAbsent(device, true))) {
            LOGGER.info("Device {} added into pool ", device.getDeviceName());
        } else {
            LOGGER.info("Pool has device {} yet", device.getDeviceName());
        }
    }

    /**
     * Gets node configurations files path
     *
     * @return Node configurations files path
     */
    private static String getNodeFilesPath() {
        return NODES_CONFIG_PATH + MobileDriverProvider.getMobileSystemType()
                                                                    .toString()
                                                                    .toLowerCase();
    }

    /**
     * Defines devices for use.
     */
    public static void getUsedEmulatorsList() {
        String emulatorsProperty;
        try {
            emulatorsProperty = System.getProperty("emulators");
            List<String> emulators = Arrays.asList(emulatorsProperty.split(","));
            emulators.forEach(s -> s = s.trim());
            emulatorForUse = new HashSet<>(emulators);
            LOGGER.info("Devices allowed for using {}.", emulatorForUse);
        } catch (NullPointerException e) {
            LOGGER.info("All available devices is used.");
        }
    }
}
