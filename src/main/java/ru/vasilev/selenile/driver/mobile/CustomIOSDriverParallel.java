package ru.vasilev.selenile.driver.mobile;

import ru.vasilev.selenile.device.IOSDevice;

public class CustomIOSDriverParallel extends MobileDriverParallel<IOSDevice> {

    /**
     * Creates ios driver using device.
     *
     * @param device iOS device whose parameters will use for connecting.
     */
    public CustomIOSDriverParallel(IOSDevice device) {
        super(device);
    }
}
