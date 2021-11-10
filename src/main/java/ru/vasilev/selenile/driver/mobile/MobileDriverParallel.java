package ru.vasilev.selenile.driver.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import ru.vasilev.selenile.DevicePool;
import ru.vasilev.selenile.device.Device;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MobileDriverParallel<D extends Device> extends AppiumDriver<MobileElement> {

    /**
     *
     */
    private final AtomicBoolean hasQuit = new AtomicBoolean(false);

    /**
     * Device whose parameters use the driver.
     */
    protected D device;

    /**
     * Creates driver for device control.
     *
     * @param device Object whose pararmeters use for connection to device.
     */
    protected MobileDriverParallel(D device) {
        super(device.getUrlHub(), device.getCapabilities());
        this.device = device;
    }

    /**
     * Closes session. Release device.
     */
    @Override
    public void quit() {
        if (!hasQuit.get() && Objects.nonNull(device)) {
            DevicePool.freeDevice(device);
            hasQuit.set(true);
        }
        super.quit();
    }
}
