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

import dev.draylar.scarlet.language.expression.CallExpression;
import dev.draylar.scarlet.language.exception.ScarletError;
import dev.draylar.scarlet.language.function.ReadFileFunction;
import dev.draylar.scarlet.language.statement.Statement;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScarletInterpreter {

    private World world = null;
    private final Random random = new Random();
    private final Environment global = new Environment();

    private Environment environment = global;

    {
        global.define(new ReadFileFunction());

        global.define("range", new ScarletFunction() {
            @Override
            public Object call(CallExpression parent, List<Object> arguments) {
                Object lower = arguments.get(0);
                Object upper = arguments.get(1);

                if(lower instanceof Number lowerInt && upper instanceof Number upperInt) {
                    List<Integer> values = new ArrayList<>();
                    for (int i = lowerInt.intValue(); i < upperInt.intValue(); i++) {
                        values.add(i);
                    }

                    return values;
                }

                throw new ScarletError("Arguments to range() must be numbers!");
            }

            @Override
            public int arity() {
                return 2;
            }

            @Override
            public String name() {
                return "range";
            }
        });
    }

    public ScarletInterpreter() {

    }

    public ScarletInterpreter(World world) {
        this.world = world;
    }

    public final void interpret(String input) {
        evaluate(new ScarletParser(new ScarletLexer(input).parseTokens()).parse());
    }

    public final void evaluate(List<Statement> statements) {
        evaluate(statements.toArray(new Statement[0]));
    }

    public void evaluate(Statement... statements) {
        for (Statement statement : statements) {
            statement.setWorld(world);
            statement.evaluate(this);
        }
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment freezeEnvironment() {
        // Gather all the assignments from the environment tree, and place them into a single environment.
        Environment frozen = new Environment();
        frozen.merge(environment);
        return frozen;
    }

    public World getWorld() {
        return world;
    }

    public Environment pushEnvironment() {
        Environment newEnvironment = new Environment(getEnvironment());
        setEnvironment(newEnvironment);
        return newEnvironment;
    }

    public void popEnvironment() {
        setEnvironment(getEnvironment().getParent());
    }
}
