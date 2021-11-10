package ru.vasilev.selenile.device;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.vasilev.selenile.config.NodeConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public abstract class Device {
    /**
     * Multi thread driver logger.
     */
    protected static final Logger LOGGER = LogManager.getLogger("MobileDriverParallel");

    /**
     * Device name.
     */
    protected final String deviceName;

    /**
     * Selenium Grid Hub URL.
     */
    protected final URL urlHub;

    /**
     * Device capability. Use for connection to device.
     */
    protected final DesiredCapabilities capabilities;

    /**
     * Creating device using configuration.
     *
     * @param nodeConfig Device configuration.
     * @throws MalformedURLException Throws when string connection to Selenium Grid Hub can't be parsed to {@link URL}.
     */
    protected Device(NodeConfig nodeConfig) throws MalformedURLException {
        capabilities = nodeConfig.getCapabilities();
        urlHub = nodeConfig.getHubConfiguration();
        deviceName = capabilities.getCapability("deviceName")
                                 .toString();
    }

    /**
     * Compare is {@code this} object is equal to {@code o}.
     * Result is true if deviceName and urlHub is equals.
     *
     * @param o Object for comparing.
     * @return Is {@code o} equals to {@code this} object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Device device = (Device) o;
        return deviceName.equals(device.deviceName) &&
                urlHub.equals(device.urlHub);
    }

    /**
     * Generates hashcode using {@code deviceName} and connection URL {@code urlHub}..
     *
     * @return Generated hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(deviceName + urlHub);
    }

    /**
     * Gets string of the simulator parameters.
     *
     * @return String of the simulator parameters
     */
    @Override
    public String toString() {
        return String.format("Device{deviceName='%s', hub='%s'}", deviceName, urlHub);
    }

    /**
     * Gets device name.
     *
     * @return {@code deviceName}.
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Gets Selenium Grid Hub URL.
     *
     * @return {@code urlHun}.
     */
    public URL getUrlHub() {
        return urlHub;
    }

    /**
     * Gets device capability.
     *
     * @return {@code capabilities}.
     */
    public DesiredCapabilities getCapabilities() {
        return capabilities;
    }

    /**
     * Restarts device.
     */
    public void restartEmulator() {
        String emulatorName = getId();
        LOGGER.info("Device {}:{} restarting", getDeviceName(), emulatorName);
        stopEmulator();
        startEmulator();
        LOGGER.info("Device {}:{} restarted ", getDeviceName(), emulatorName);
    }


    /**
     * Stops emulator.
     */
    public abstract void stopEmulator();

    /**
     * Starts emulator.
     */
    public abstract void startEmulator();

    /**
     * Gets device ID.
     *
     * @return Device ID.
     */
    protected abstract String getId();
}
