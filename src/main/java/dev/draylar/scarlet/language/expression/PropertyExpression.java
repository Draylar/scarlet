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
package dev.draylar.scarlet.language.expression;

import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.Token;
import dev.draylar.scarlet.language.exception.ScarletError;

import java.lang.reflect.Method;
import java.util.Collections;

public class PropertyExpression extends Expression {

    private final Expression expression;
    private final Token name;

    public PropertyExpression(Expression expression, Token name) {
        this.expression = expression;
        this.name = name;
    }

    @Override
    public Object evaluate(ScarletInterpreter interpreter) {
        Object callee = expression.evaluate(interpreter);
        String lexeme = name.lexeme();

        // check Scarlet objects with first priority

        // check fields / properties

        // If the Expression (what we are evaluating from) is a Java type, use reflection to return that method or field.
        // if expression is ScarletInstance...
        // else...
        // TODO: Must cache this.
        // Check methods
        {
            for (Method method : callee.getClass().getDeclaredMethods()) {
                if(method.getName().equals(lexeme)) {
                    return new InstanceMethod(callee, method);
                }
            }
        }

        // todo: boil this down into a different Expression? Simplification stage?
        // check shorthand method getter access
        // foo.x -> foo.getX()
        // TODO: cache callee class + method found. If callee classes changes, re-cache method.
        for (Method method : callee.getClass().getMethods()) {
            if(method.getName().equals("get" + String.valueOf(lexeme.charAt(0)).toUpperCase() + (lexeme.length() > 1 ? lexeme.substring(1) : ""))) {
                return CallExpression.invoke(Collections.emptyList(), new InstanceMethod(callee, method));
            }
        }

        throw new ScarletError("Could not evaluate property %s on Expression %s!".formatted(lexeme, expression));
    }
}
