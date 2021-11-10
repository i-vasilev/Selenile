package ru.vasilev.selenile;

import ru.vasilev.selenile.device.Device;

import java.util.concurrent.ConcurrentHashMap;

public abstract class DevicesQueue {
    /**
     * Devices list with thread ids.
     */
    private static final ConcurrentHashMap<Long, Device> DEVICES = new ConcurrentHashMap<>();

    /**
     * Private constructor.
     */
    private DevicesQueue() {
    }

    /**
     * Gets device for current thread.
     *
     * @return Current thread.
     */
    public static synchronized Device getDevice() {
        return DEVICES.get(Thread.currentThread()
                                 .getId());
    }

    /**
     * Adds device for current thread.
     *
     * @param device Device for current thread.
     */
    public static synchronized void addDevice(Device device) {
        DEVICES.put(Thread.currentThread()
                          .getId(), device);
    }
}
