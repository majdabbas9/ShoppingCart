package com.example.ShoppingCart;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A simple singleton logger with color support and function call destination
 * tracking.
 */
public class Logger {

    // ANSI Escape Codes for Colors
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    private static final String LOG_FILE_PATH = "logs/app.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Static instance for Singleton
    private static final Logger INSTANCE = new Logger();

    private Logger() {
        // Ensure log directory exists
        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    public static Logger getInstance() {
        return INSTANCE;
    }

    /**
     * Logs an INFO message with green color.
     */
    public void info(String message) {
        log(GREEN, "INFO", message);
    }

    /**
     * Logs a WARNING message with yellow color.
     */
    public void warn(String message) {
        log(YELLOW, "WARN", message);
    }

    /**
     * Logs an ERROR message with red color.
     */
    public void error(String message) {
        log(RED, "ERROR", message);
    }

    /**
     * Logs a DEBUG message with cyan color.
     */
    public void debug(String message) {
        log(CYAN, "DEBUG", message);
    }

    private synchronized void log(String color, String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String destination = getCallerInfo();

        // Print to console with colors
        /*
         * System.out.printf("%s[%s]%s %s%-5s%s %s[%s]%s %s%n",
         * CYAN, timestamp, RESET,
         * color, level, RESET,
         * PURPLE, destination, RESET,
         * message);
         */

        // Save to file (plain text)
        saveToFile(timestamp, level, destination, message);
    }

    private void saveToFile(String timestamp, String level, String destination, String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            out.printf("[%s] %-5s [%s] %s%n", timestamp, level, destination, message);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    /**
     * Retrieves the caller's class and method name using StackWalker (Java 9+).
     */
    private String getCallerInfo() {
        return StackWalker.getInstance()
                .walk(frames -> frames
                        .skip(3) // Skip getCallerInfo, log, and the log level method (info/warn/etc.)
                        .findFirst()
                        .map(frame -> frame.getClassName() + "." + frame.getMethodName())
                        .orElse("Unknown Destination"));
    }
}
