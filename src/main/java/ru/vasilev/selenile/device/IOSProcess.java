package ru.vasilev.selenile.device;

public interface IOSProcess {
    /**
     * Handle string to other string.
     *
     * @param line String for handling.
     * @return String after handling.
     */
    String processBuffer(String line);
}
