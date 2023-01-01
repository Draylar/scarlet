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

import dev.draylar.scarlet.language.ScarletFunction;
import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.Token;
import dev.draylar.scarlet.language.exception.ScarletError;
import dev.draylar.scarlet.language.exception.ScarletReturn;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CallExpression extends Expression {

    private final Expression expression;
    private final Token paren;
    private final List<Expression> arguments;

    public CallExpression(Expression expression, Token paren, List<Expression> arguments) {
        this.expression = expression;
        this.paren = paren;
        this.arguments = arguments;
    }

    @Override
    public Object evaluate(ScarletInterpreter interpreter) {
        Object caller = expression.evaluate(interpreter);
        List<Object> evaluatedArgs = arguments.stream().map(it -> it.evaluate(interpreter)).toList();

        if(caller instanceof InstanceMethod method) {
            return invoke(evaluatedArgs, method);
        }

        if(caller instanceof ScarletFunction function) {
            if(arguments.size() != function.arity()) {
                throw new ScarletError(String.format("""
                        Function %s was called, which expects %d arguments, but %d were provided instead.
                        """, function.name(), function.arity(), arguments.size()));
            }

            try {
                return function.call(this, evaluatedArgs);
            } catch (ScarletReturn returnException) {
                return returnException.getValue();
            }
        }

        throw new ScarletError(String.format("Tried to call function on invalid caller %s!", caller));
    }

    public static Object invoke(List<Object> evaluatedArgs, InstanceMethod method) {
        try {
            Object[] args = evaluatedArgs.toArray();
            return method.method().invoke(method.instance(), args);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }
}
