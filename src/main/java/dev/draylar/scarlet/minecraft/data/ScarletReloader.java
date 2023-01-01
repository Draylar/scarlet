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
package dev.draylar.scarlet.minecraft.data;

import dev.draylar.scarlet.Scarlet;
import dev.draylar.scarlet.script.ScarletScript;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScarletReloader {

    @NotNull
    public static List<ScarletScript> loadScripts(Profiler profiler) {
        Scarlet.listeners.clear();

        profiler.push("Scarlet Reload");
        List<ScarletScript> scripts = new ArrayList<>();

        try {
            Files.list(FabricLoader.getInstance().getGameDir().resolve("scarlet"))
                    .filter(file -> file.getFileName().toString().endsWith(".scarlet"))
                    .forEach(file -> {
                        try {
                            String contents = Files.readString(file);
                            scripts.add(new ScarletScript(file.getFileName().toString(), contents));
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    });

            if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
                Files.list(Paths.get("").toAbsolutePath().getParent().resolve("example_scripts"))
                        .filter(file -> file.getFileName().toString().endsWith(".scarlet"))
                        .forEach(file -> {
                            try {
                                String contents = Files.readString(file);
                                scripts.add(new ScarletScript(file.getFileName().toString(), contents));
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                        });
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        profiler.pop();
        return scripts;
    }

    public static long applyReload(MinecraftServer server, List<ScarletScript> data, ServerPlayerEntity trigger) {
        long start = System.nanoTime();
        Scarlet.ACTIVE.clear();
        Scarlet.ACTIVE.addAll(data);
        Scarlet.reload(server, trigger);

        // Listeners
        Scarlet.listeners.getListeners().forEach((event, listeners) -> {
            for (Object listener : listeners) {
                registerListener(event, listener);
            }
        });

        Scarlet.LOGGER.info(String.format("Loaded %d Scarlet Scripts.", data.size()));
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }

    private static <T> void registerListener(Event event, T listener) {
        event.register(listener);
    }
}
