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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class Expression {

    @Nullable
    protected World world;

    public final <T> T evaluate(ScarletInterpreter interpreter, Class<T> expected) {
        Object result = evaluate(interpreter);
        try {
            return (T) result;
        } catch (ClassCastException exception) {
            throw new ScarletError(String.format("Expected to find type %s from %s, but found %s instead!", expected.getSimpleName(), result.toString(), result.getClass().getSimpleName()));
        }
    }

    public abstract Object evaluate(ScarletInterpreter interpreter);

    protected void checkNumberOperand(Token operator, Object operand) {
        if(!(operand instanceof Number)) {
            throw new RuntimeException("Operand mustbe a number!");
        }
    }

    protected void checkNumberOperand(Token operator, Object first, Object second) {
        if(!(first instanceof Number) && !(second instanceof Number)) {
            throw new RuntimeException("Operands must be numbers!");
        }
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
    }
}
