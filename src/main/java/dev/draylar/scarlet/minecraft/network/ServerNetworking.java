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
package dev.draylar.scarlet.minecraft.network;

import dev.draylar.scarlet.Scarlet;
import dev.draylar.scarlet.minecraft.data.ScarletReloader;
import dev.draylar.scarlet.script.ScarletScript;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class ServerNetworking {

    public static final Identifier SCARLET_RELOAD = Scarlet.id("reload_scripts");

    public static void initialize() {
        ServerPlayNetworking.registerGlobalReceiver(SCARLET_RELOAD, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if(player.hasPermissionLevel(2)) {
                    long start = System.nanoTime();
                    List<ScarletScript> scripts = ScarletReloader.loadScripts(server.getProfiler());
                    ScarletReloader.applyReload(server, scripts, player);

                    // Console Log
                    long time = System.nanoTime() - start;
                    Scarlet.LOGGER.info(String.format("\u001b[31mReloaded Scarlet in %.2fms", time / 1_000_000.0));

                    // Game Chat
                    MutableText scarlet = new LiteralText("<Scarlet> ").formatted(Formatting.RED);
                    player.sendMessage(scarlet.append(new LiteralText(String.format("Reloaded in %.2fms", time / 1_000_000.0)).formatted(Formatting.GRAY)), false);
                }
            });
        });
    }
}
