package ru.vasilev.selenile.device;

import ru.vasilev.selenile.config.NodeConfig;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class AndroidDevice extends Device {

    /**
     * Creating android emulator using node configuration.
     *
     * @param nodeConfig Configuration.
     * @throws MalformedURLException Throws when string connection to Selenium Grid Hub can't be parsed to {@link URL}.
     */
    public AndroidDevice(NodeConfig nodeConfig) throws MalformedURLException {
        super(nodeConfig);
    }

    /**
     * Gets Device ID.
     *
     * @return Device ID.
     */
    @Override
    protected String getId() {
        return getCapabilities()
                .getCapability("udid")
                .toString();
    }

    /**
     * Gets port that use device.
     *
     * @return Device port.
     */
    protected String getPort() {
        return getId().replace("emulator-", "");
    }

    /**
     * Executes a command in Windows command prompt.
     *
     * @param command Command for executing.
     */
    protected static void sendCommand(String command) {
        try {
            Runtime.getRuntime()
                   .exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops emulator.
     */
    @Override
    public void stopEmulator() {
        String shutdownCommand = "adb -s %emulator emu kill";
        sendCommand(shutdownCommand.replace("%emulator", getPort()));
        killProcessOnPort(getPort());
    }

    /**
     * Runs emulator.
     */
    @Override
    public void startEmulator() {
        String bootCommand = "cmd /c %LOCALAPPDATA%\\Android\\Sdk\\emulator\\emulator -avd"
                + " %name -port %port -netfast -no-audio -no-snapshot-load ^>^> %file_restart.log";
        sendCommand(bootCommand.replace("%name", deviceName)
                               .replace("%port", getPort())
                               .replace("%file", deviceName.toLowerCase()));
    }

    /**
     * Closes processes that use port.
     *
     * @param port Port that use processes.
     */
    private static void killProcessOnPort(String port) {
        String killProcessCommand =
                "cmd /c for /f \"tokens=5\" %%a in ('netstat -aon ^| find \":%port\""
                        + " ^| find \"LISTENING\"') do taskkill /f /t /pid %%a";
        sendCommand(killProcessCommand.replace("%port", port));
    }
}
