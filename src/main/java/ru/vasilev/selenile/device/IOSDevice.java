package ru.vasilev.selenile.device;

import ru.vasilev.selenile.config.NodeConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOSDevice extends Device {

    /**
     * Creating iOS emulator using node configuration.
     *
     * @param nodeConfig Configuration.
     * @throws MalformedURLException Throws when string connection to Selenium Grid Hub can't be parsed to {@link URL}.
     */
    public IOSDevice(NodeConfig nodeConfig) throws MalformedURLException {
        super(nodeConfig);
    }


    /**
     * Gets Device ID.
     *
     * @return Device ID.
     */
    @Override
    protected String getId() {
        String deviceId = macProcessXcrunSimctl("list", line -> {
            Pattern pattern = Pattern.compile(".*" + deviceName + " *\\(([^)]*).*");
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return "";
        });
        if (deviceId.equals("")) {
            throw new IllegalStateException(
                    "Can't find device {} in devices list." + deviceId);
        }
        LOGGER.info("Emulator ID {}:{}", deviceName, deviceId);
        return deviceId;
    }

    /**
     * Stops emulator.
     */
    @Override
    public void stopEmulator() {
        macProcessXcrunSimctl("shutdown", line -> {
            LOGGER.info(line);
            return "";
        });
        eraseIosEmulator();
    }

    /**
     * Erase changes in ios emulator.
     */
    private void eraseIosEmulator() {
        macProcessXcrunSimctl("erase", line -> {
            LOGGER.info(line);
            return "";
        });
    }

    /**
     * Runs emulator.
     */
    @Override
    public void startEmulator() {
        macProcessXcrunSimctl("boot", line -> {
            LOGGER.info(line);
            return "";
        });
    }

    /**
     * Executes {@code xcrun simctl [command]} in terminal on OSX.
     *
     * @param command       Command to run with {@code xcrun simctl}.
     * @param processString String handler for command result.
     * @return String handler result.
     */
    private String macProcessXcrunSimctl(String command, IOSProcess processString) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("xcrun", "simctl", command);
        String deviceId = "";
        try {
            Process process = processBuilder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(deviceName)) {
                    deviceId = processString.processBuffer(line);
                }
            }
            int exitCode = process.waitFor();
            LOGGER.info("Exited with error code : {}", exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return deviceId;
    }
}
