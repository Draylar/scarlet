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
import dev.draylar.scarlet.language.exception.ScarletReturn;
import net.minecraft.world.World;

import java.util.List;

public class BlockStatement extends Statement {

    private final List<Statement> statements;

    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public void evaluate(ScarletInterpreter interpreter) throws ScarletReturn {
        Environment scoped = new Environment(interpreter.getEnvironment());
        Environment previous = interpreter.getEnvironment();

        try {
            interpreter.setEnvironment(scoped);
            for (Statement statement : statements) {
                statement.evaluate(interpreter);
            }
        } catch(ScarletReturn scarletReturn) {
            throw scarletReturn;
        } catch (Throwable any) {
            any.printStackTrace();
        } finally {
            interpreter.setEnvironment(previous);
        }
    }

    // TODO: BIG risk of thread safety here, could go off mid-interpretation somewhere else
    public void evaluate(ScarletInterpreter interpreter, Environment environment) {
        Environment previous = interpreter.getEnvironment();

        try {
            interpreter.setEnvironment(environment);
            for (Statement statement : statements) {
                statement.evaluate(interpreter);
            }
        } catch (Throwable any) {
            any.printStackTrace();
        } finally {
            interpreter.setEnvironment(previous);
        }
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        for (Statement statement : statements) {
            statement.setWorld(world);
        }
    }
}
