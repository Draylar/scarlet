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
import dev.draylar.scarlet.language.expression.Expression;
import dev.draylar.scarlet.language.exception.ScarletError;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class AfterStatement extends Statement {

    private final Expression time;
    private final Token type;
    private final BlockStatement blockStatement;

    public AfterStatement(Expression time, Token type, BlockStatement blockStatement) {
        this.time = time;
        this.type = type;
        this.blockStatement = blockStatement;
    }

    @Override
    public void evaluate(ScarletInterpreter interpreter) {
        long milliseconds = 0;

        if(type.lexeme().equals("ticks")) {
            milliseconds = (long) ((time.evaluate(interpreter, Number.class).doubleValue() / 20d) * 1_000);
        } else {
            throw new ScarletError(String.format("Expected to find a valid time unit, but found %s instead!", type.toString()));
        }

        Environment environment = interpreter.freezeEnvironment();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(world == null || world.getServer() == null) {
                    return;
                }

                world.getServer().execute(() -> {
                    blockStatement.evaluate(interpreter, environment);
                });
            }
        }, milliseconds);
    }

    @Override
    public void setWorld(@Nullable World world) {
        super.setWorld(world);
        blockStatement.setWorld(world);
    }
}
