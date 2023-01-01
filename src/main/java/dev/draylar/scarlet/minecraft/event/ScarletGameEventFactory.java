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
package dev.draylar.scarlet.minecraft.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ScarletGameEventFactory<Event, FactoryType> {

    private final Class<Event> eventClass;
    private final Class<FactoryType> argumentClass;
    private final Function<FactoryType, Function<List<Event>, Event>> event;
    private final Map<FactoryType, ScarletGameEvent<Event>> cache = new HashMap<>();

    public ScarletGameEventFactory(Class<Event> eventClass, Class<FactoryType> argumentClass, Function<FactoryType, Function<List<Event>, Event>> event) {
        this.eventClass = eventClass;
        this.argumentClass = argumentClass;
        this.event = event;
    }

    public ScarletGameEvent<Event> apply(FactoryType type) {
        if(cache.containsKey(type)) {
            return cache.get(type);
        }

        Function<List<Event>, Event> function = event.apply(type);
        ScarletGameEvent<Event> event = ScarletGameEvent.create(eventClass, function);
        cache.put(type, event);
        return event;
    }
}
