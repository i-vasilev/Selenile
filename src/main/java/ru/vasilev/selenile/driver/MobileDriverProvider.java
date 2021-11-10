package ru.vasilev.selenile.driver;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import ru.vasilev.selenile.DevicePool;
import ru.vasilev.selenile.DevicesQueue;
import ru.vasilev.selenile.config.MobileSystemPlatform;
import ru.vasilev.selenile.device.AndroidDevice;
import ru.vasilev.selenile.device.Device;
import ru.vasilev.selenile.device.IOSDevice;
import ru.vasilev.selenile.driver.mobile.AndroidDriverParallel;
import ru.vasilev.selenile.driver.mobile.CustomIOSDriverParallel;
import ru.vasilev.selenile.driver.mobile.MobileDriverParallel;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Objects;

public class MobileDriverProvider {
    /**
     * Multi thread driver logger.
     */
    protected static final Logger LOGGER = LogManager.getLogger("MobileDriverParallel");

    /**
     * Mobile platform type.
     */
    private static MobileSystemPlatform type = null;

    /**
     * Creates driver for device controlling.
     *
     * @param capabilities Device connection parameters.
     * @return AppiumDriver got from pool.
     */
    @CheckReturnValue
    @Nonnull
    public AppiumDriver createDriver(DesiredCapabilities capabilities) {
        DevicesQueue.addDevice(DevicePool.deviceRequest());
        LocalDateTime getDeviceTime = LocalDateTime.now();
        int count = 0;
        while (DevicesQueue.getDevice() == null) {
            if (getDeviceTime.plusMinutes(10)
                             .isBefore(LocalDateTime.now())) {
                throw new WebDriverException("Device loading during 10 minutes failed!");
            }
            LOGGER.error("Can't find available device. Trying №{}. Next try after 10 seconds.", count++);
            sleep(10000);
            DevicesQueue.addDevice(DevicePool.deviceRequest());
        }
        final Device device = DevicesQueue.getDevice();
        LOGGER.info("Got device {}", device.getDeviceName());
        LOGGER.info("Work with {}  from hub {}", device.getDeviceName(), device.getUrlHub());
        return Objects.requireNonNull(createAppiumDriver(device));
    }

    /**
     * Creates new {@link MobileDriverParallel}.
     *
     * @param device Device whose parameters gives to new driver.
     * @return New {@link MobileDriverParallel}.
     */
    private synchronized <D extends Device> MobileDriverParallel<D> createAppiumDriver(D device) {
        LOGGER.info("Trying create driver for {}", device.getDeviceName());
        int count = 0;
        while (count < 5) {
            try {
                MobileDriverParallel driver;
                switch (type) {
                    case IOS:
                        driver = new CustomIOSDriverParallel((IOSDevice) device);
                        break;
                    case ANDROID:
                        driver = new AndroidDriverParallel((AndroidDevice) device);
                        break;
                    default:
                        throw new IllegalArgumentException("Wrong mobile platform type");
                }
                LOGGER.info("Got driver for {}", device.getDeviceName());
                return driver;
            } catch (WebDriverException e) {
                if (++count == 5) {
                    throw e;
                }
                device.restartEmulator();
            }
        }
        throw new IllegalArgumentException("Can't create mobile driver.");
    }

    /**
     * Stops thread for {@code ms} milliseconds.
     *
     * @param ms Count milliseconds to stop thread.
     */
    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets mobile platform name from system property and returns appropriates {@link MobileSystemPlatform}.
     * If type has defined yet, just returns {@code type} value.
     *
     * @return Mobile platform type.
     */
    public static MobileSystemPlatform getMobileSystemType() {
        if (type == null) {
            String customProperty = System.getProperty("mobilesystem")
                                          .toUpperCase();
            try {
                type = MobileSystemPlatform.valueOf(customProperty);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Wrong mobile platform type");
            }
        }
        return type;
    }
}
