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
package dev.draylar.scarlet.minecraft.api;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.*;

public class ScarletEventManager {

    private static final Identifier PHASE = new Identifier("scarlet", "phase");
    private static final Set<Object> REGISTERED_LISTENER = new HashSet<>();
    private final Map<Event<?>, List> listeners = new HashMap<>();

    private static Field _phasesField;
    private static Field _orderedPhasesField;

    public <T> List<T> get(Event<T> event) {
        return (List<T>) listeners.get(event);
    }

    public <T> void add(Event<T> event, T listener) {
        event.register(PHASE, listener);
        listeners.computeIfAbsent(event, key -> new ArrayList<>());
        listeners.get(event).add(listener);
        REGISTERED_LISTENER.add(listener);

        if(_phasesField == null) {
            try {
                _phasesField = event.getClass().getDeclaredField("phases");
                _orderedPhasesField = event.getClass().getDeclaredField("sortedPhases");
            } catch (NoSuchFieldException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void clear() {
        listeners.forEach((event, list) -> {
            if(_phasesField != null) {
                _phasesField.setAccessible(true);
                _orderedPhasesField.setAccessible(true);

                try {
                    Map<Identifier, Object> objects = (Map<Identifier, Object>) _phasesField.get(event);
                    Object value = objects.remove(PHASE);
                    if(value != null) {
                        List<Object> t = (List<Object>) _orderedPhasesField.get(event);
                        t.remove(value);
                    }
                } catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                }
            }
        });

        listeners.clear();
        REGISTERED_LISTENER.clear();
    }

    public Map<Event<?>, List> getListeners() {
        return listeners;
    }
}
