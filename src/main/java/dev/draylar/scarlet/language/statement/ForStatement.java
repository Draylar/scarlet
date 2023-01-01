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
package dev.draylar.scarlet.language.statement;

import dev.draylar.scarlet.language.Environment;
import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.Token;
import dev.draylar.scarlet.language.exception.ScarletError;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ForStatement extends Statement {

    private final Token variable;
    private final Statement range;
    private final Statement body;

    public ForStatement(Token variable, Statement range, Statement body) {
        this.variable = variable;
        this.range = range;
        this.body = body;
    }

    @Override
    public void evaluate(ScarletInterpreter interpreter) {
        Environment scopedEventContext = new Environment(interpreter.getEnvironment());
        scopedEventContext.define(variable.lexeme(), null);
        Environment previous = interpreter.getEnvironment();
        interpreter.setEnvironment(scopedEventContext);

        // Range should return a collection we can iterate over.
        Object value = null;
        if(range instanceof ExpressionStatement expression) {
            value = expression.getExpression().evaluate(interpreter);
        } else if(range instanceof VariableStatement variable) {
            value = interpreter.getEnvironment().get(variable.getVariableName());
        }

        if(value instanceof Iterable<?> iterable) {
            for (Object entry : iterable) {
                scopedEventContext.define(variable.lexeme(), entry);
                body.evaluate(interpreter);
            }
        } else {
            throw new ScarletError(String.format("Expected an iterable for loop, but found instead %s!", value));
        }

        interpreter.setEnvironment(previous);
    }

    @Override
    public void setWorld(@Nullable World world) {
        super.setWorld(world);
        body.setWorld(world);
    }
}
