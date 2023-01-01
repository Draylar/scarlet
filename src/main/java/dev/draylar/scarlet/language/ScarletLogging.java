/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Draylar, Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.draylar.scarlet.language;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

public class ScarletLogging extends Formatter {

    private static final Map<Level, String> COLOR_BY_LEVELS = new HashMap<>();

    public static final Logger LOGGER = Logger.getLogger("Scarlet");
    public static final String BLACK = "\u001b[30m";
    public static final String RED = "\u001b[31m";
    public static final String GREEN = "\u001b[32m";
    public static final String YELLOW = "\u001b[33m";
    public static final String BLUE = "\u001b[34m";
    public static final String MAGENTA = "\u001b[35m";
    public static final String CYAN = "\u001b[36m";
    public static final String WHITE = "\u001b[37m";
    public static final String RESET = "\u001b[0m";

    static {
        COLOR_BY_LEVELS.put(Level.WARNING, RED);
        COLOR_BY_LEVELS.put(Level.SEVERE, RED);
        COLOR_BY_LEVELS.put(Level.INFO, BLUE);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new ScarletLogging());

        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(handler);
    }

    @Override
    public String format(LogRecord record) {
        return String.format("%s[Scarlet] %s[%s]: %s%s",
                RED,
                COLOR_BY_LEVELS.getOrDefault(record.getLevel(), WHITE),
                record.getLevel().toString().replace("WARNING", "Language Error"),
                WHITE,
                record.getMessage()
        ) + System.lineSeparator() + RESET;
    }
}
