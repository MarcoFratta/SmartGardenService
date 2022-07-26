package server;

import logger.SingletonLogger;
import logger.Type;
import model.*;
import serial.SerialManager;
import view.ShowApplication;

import java.util.List;

public class Launcher {
    public static void main(final String... args) {
        final var garden = new SmartGardenImpl(
                List.of(new OnOffObjectImpl("Lamp 1"), new OnOffObjectImpl("Lamp 2")),
                List.of(new IntensityObjectImpl(0, 4, "Lamp 3"),
                        new IntensityObjectImpl(0, 4, "Lamp 4")),
                new IntensityObjectImpl(0, 4, "Irrigation"), GardenState.AUTO);
        final int port = 8080;
        int rate = 9600;
        String serialPort = "COM5";
        if (args.length >= 1) {
            serialPort = args[0];
        }
        if (args.length >= 2) {
            try {
                rate = Integer.parseInt(args[1]);
            } catch (final Exception e) {
                e.printStackTrace();
                SingletonLogger.getLogger().log(Type.ERROR_INFO, "LAUNCHER", e.getMessage());
            }
        }


        final Service<SmartGarden> service = RunService.start(garden, port);
        try {
            SerialManager.start(serialPort, rate, service);
        } catch (final Exception e) {
            SingletonLogger.getLogger().log(Type.ERROR_INFO, "LAUNCHER", e.getMessage());
        }
        ShowApplication.main(args);
    }
}
