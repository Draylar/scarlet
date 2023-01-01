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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ScarletGameEvent<Event> {

    private static final List<ScarletGameEvent<?>> allEvents = new ArrayList<>();

    private final List<Event> listeners = new ArrayList<>();
    private final List<Event> reloadableListeners = new ArrayList<>();

    private final Class<Event> eventClass;
    private final Function<List<Event>, Event> factory;
    private Event compiledEvent = null;

    public ScarletGameEvent(Class<Event> eventClass, Function<List<Event>, Event> factory) {
        this.eventClass = eventClass;
        this.factory = factory;
        recompile();
    }

    public static <T> ScarletGameEvent<T> create(Class<T> eventClass, Function<List<T>, T> event) {
        ScarletGameEvent<T> composed = new ScarletGameEvent<>(eventClass, event);
        //noinspection unchecked,ConstantConditions
        allEvents.add(composed);
        return composed;
    }

    public static <T, A> ScarletGameEventFactory<T, A> factory(Class<T> eventClass, Class<A> argumentClass, Function<A, Function<List<T>, T>> event) {
        return new ScarletGameEventFactory<>(eventClass, argumentClass, event);
    }

    public void register(Event event) {
        listeners.add(event);
    }

    public void registerReloadable(Event event) {
        reloadableListeners.add(event);
        recompile();
    }

    public void reload() {
        reloadableListeners.clear();
        recompile();
    }

    public Event invoker() {
        return compiledEvent;
    }

    private void recompile() {
        List<Event> allEvents = new ArrayList<>();
        allEvents.addAll(reloadableListeners);
        allEvents.addAll(listeners);
        compiledEvent = factory.apply(allEvents);
    }

    public static List<ScarletGameEvent<?>> getAllEvents() {
        return allEvents;
    }
}
