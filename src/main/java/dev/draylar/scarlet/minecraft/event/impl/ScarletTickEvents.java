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
package dev.draylar.scarlet.minecraft.event.impl;

import dev.draylar.scarlet.minecraft.event.ScarletGameEvent;
import dev.draylar.scarlet.minecraft.event.ScarletGameEventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;

public class ScarletTickEvents {

    public static final ScarletGameEvent<GameTick> SERVER_TICK = ScarletGameEvent.create(GameTick.class, listeners -> server -> {
        for (GameTick listener : listeners) {
            listener.tick(server);
        }
    });

    public static final ScarletGameEventFactory<EntityTick, EntityType> ENTITY_TICK = ScarletGameEvent.factory(EntityTick.class, EntityType.class, type -> listeners -> entity -> {
        for (EntityTick listener : listeners) {
            listener.tick(entity);
        }
    });

    @FunctionalInterface
    public interface GameTick {

        void tick(MinecraftServer server);
    }

    @FunctionalInterface
    public interface EntityTick<T extends Entity> {

        void tick(T entity);
    }
}
