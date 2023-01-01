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
package dev.draylar.scarlet.language.expression.minecraft;

import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.expression.Expression;
import net.minecraft.util.math.BlockPos;

public class BreakBlockExpression extends Expression {

    private final Expression x;
    private final Expression y;
    private final Expression z;

    public BreakBlockExpression(Expression x, Expression y, Expression z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Object evaluate(ScarletInterpreter interpreter) {
        if(world == null || world.isClient) {
            return false;
        }

        double x = this.x.evaluate(interpreter, double.class);
        double y = this.y.evaluate(interpreter, double.class);
        double z = this.z.evaluate(interpreter, double.class);
        return world.breakBlock(new BlockPos(x, y, z), false);
    }
}
