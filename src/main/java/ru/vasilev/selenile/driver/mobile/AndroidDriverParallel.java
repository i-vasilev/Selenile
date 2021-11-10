package ru.vasilev.selenile.driver.mobile;

import ru.vasilev.selenile.device.AndroidDevice;

public class AndroidDriverParallel extends MobileDriverParallel<AndroidDevice> {

    /**
     * Creates android driver using device.
     *
     * @param device Android device whose parameters will use for connecting.
     */
    public AndroidDriverParallel(AndroidDevice device) {
        super(device);
    }
}
