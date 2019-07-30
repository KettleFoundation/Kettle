package org.bukkit.craftbukkit.util;

import com.mojang.util.QueueLogAppender;
import jline.console.ConsoleReader;
import org.bukkit.craftbukkit.Main;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TerminalConsoleWriterThread implements Runnable {
    private static final byte[] RESET_LINE = String.valueOf('\r').getBytes();
    final private ConsoleReader reader;
    final private OutputStream output;

    public TerminalConsoleWriterThread(OutputStream output, ConsoleReader reader) {
        this.output = output;
        this.reader = reader;
    }

    @Override
    public void run() {
        String message;

        // Using name from log4j config in vanilla jar
        while (true) {
            message = QueueLogAppender.getNextLogEvent("TerminalConsole");
            if (message == null) {
                continue;
            }

            try {
                if (Main.useJline) {
                    this.output.write(RESET_LINE);
                    this.output.write(ColouredConsoleSender.toAnsiStr(message).getBytes());
                    this.output.flush();

                    try {
                        reader.drawLine();
                    } catch (Throwable ex) {
                        reader.getCursorBuffer().clear();
                    }
                    reader.flush();
                } else {
                    output.write(message.getBytes());
                    output.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(TerminalConsoleWriterThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
