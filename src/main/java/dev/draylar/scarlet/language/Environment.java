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

import dev.draylar.scarlet.language.exception.ScarletError;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Environment parent;
    private final Map<String, Object> variables = new HashMap<>();
    private Vec3d implicitPosition = null;

    public Environment() {
        this(null);
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public void define(ScarletFunction function) {
        define(function.name(), function);
    }

    public void define(String name, Object value) {
        variables.put(name, value);
    }

    public Object get(Token name) {
        try {
            return get(name.lexeme());
        } catch (ScarletError error) {
            throw new ScarletError(
                    String.format("Variable %s was not defined", name),
                    name.lines()[name.line()],
                    name.line(),
                    name);
        }
    }

    public Object get(String name) {
        if(variables.containsKey(name)) {
            return variables.get(name);
        }

        // Delegate to parent for variable scoping.
        if(parent != null) {
            return parent.get(name);
        }

        throw new ScarletError(String.format("Variable %s was not defined", name));
    }

    public void assign(Token token, Object value) {
        if(variables.containsKey(token.lexeme())) {
            variables.put(token.lexeme(), value);
            return;
        }

        if(parent != null) {
            parent.assign(token, value);
            return;
        }

        throw new ScarletError(
                String.format("Variable %s was not defined", token.lexeme()),
                token.lines()[token.line()],
                token.line(),
                token
        );
    }

    public void bind(Entity entity) {
        define("x", entity.getX());
        define("y", entity.getY());
        define("z", entity.getZ());
        define("name", entity.getEntityName());

        if(entity instanceof PlayerEntity player) {
            define("player", player);
        } else {
            define("entity", entity);
        }
    }

    public void setImplicitPosition(Vec3d implicitPosition) {
        this.implicitPosition = implicitPosition;
    }

    public Vec3d getImplicitPosition() {
        if(implicitPosition == null) {
            return parent == null ? null : parent.getImplicitPosition();
        }

        return implicitPosition;
    }

    public Environment getParent() {
        return parent;
    }

    public void merge(Environment environment) {
        environment.variables.forEach((key, obj) -> {
            if(!variables.containsKey(key)) {
                variables.put(key, obj);
            }
        });

        if(environment.parent != null) {
            merge(environment.parent);
        }
    }
}
