package at.pavlov.internal;

import lombok.Getter;

import java.util.logging.Logger;

public class CannonLogger {
    @Getter
    private static Logger logger = null;

    private CannonLogger() {}

    public static void instantiate(Logger logger) {
        if (CannonLogger.logger == null) {
            CannonLogger.logger = logger;
        }
    }
}
