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

import dev.draylar.scarlet.language.Environment;
import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.Token;
import dev.draylar.scarlet.language.exception.ScarletError;
import dev.draylar.scarlet.language.exception.ScarletReturn;
import dev.draylar.scarlet.language.statement.BlockStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LambdaExpression extends Expression {

    private final Expression source;
    private final Token operation;
    private final BlockStatement code;

    public LambdaExpression(Expression source, Token operation, BlockStatement code) {
        this.source = source;
        this.operation = operation;
        this.code = code;
    }

    @Override
    public Object evaluate(ScarletInterpreter interpreter) {
        switch(operation.lexeme()) {
            case "map" -> {
                Object target = source.evaluate(interpreter);
                if(target.getClass().isArray()) {
                    return map(interpreter, Arrays.asList((Object[]) target));
                } else if(target instanceof Iterable<?> iterator) {
                    return map(interpreter, iterator);
                }
            }
        }

        throw new ScarletError("Could not match Lambda operation %s!".formatted(operation.lexeme()));
    }

    @NotNull
    private List<Object> map(ScarletInterpreter interpreter, Iterable<?> iterator) {
        List<Object> mapped = new ArrayList<>();
        Environment previous = interpreter.getEnvironment();
        for (Object value : iterator) {
            try {
                Environment scoped = new Environment(interpreter.getEnvironment());
                interpreter.setEnvironment(scoped);
                scoped.define("it", value);
                code.evaluate(interpreter);
                interpreter.setEnvironment(previous);
            } catch (ScarletReturn exception) {
                mapped.add(exception.getValue());
                continue;
            }

            interpreter.setEnvironment(previous);
            throw new ScarletError("Expected map lambda to return a value!");
        }

        interpreter.setEnvironment(previous);
        return mapped;
    }
}
