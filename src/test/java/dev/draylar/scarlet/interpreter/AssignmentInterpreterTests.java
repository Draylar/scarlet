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
package dev.draylar.scarlet.interpreter;

import dev.draylar.scarlet.language.ScarletInterpreter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static dev.draylar.scarlet.Testing.interpret;

public class AssignmentInterpreterTests {

    @Test
    public void assignment_integer() {
        ScarletInterpreter interpreter = interpret("""
                let foo = 5
                """);

        Assertions.assertEquals(5, interpreter.getEnvironment().get("foo"));
    }

    @Test
    public void assignment_double() {
        ScarletInterpreter interpreter = interpret("""
                let foo = 5.0
                """);

        Assertions.assertEquals(5.0, interpreter.getEnvironment().get("foo"));
    }

    @Test
    public void assignment_string() {
        ScarletInterpreter interpreter = interpret("""
                let foo = "hello, world!"
                """);

        Assertions.assertEquals("hello, world!", interpreter.getEnvironment().get("foo"));
    }

    @Test
    public void assignment_term() {
        ScarletInterpreter interpreter = interpret("""
                let foo = 5 + 5
                """);

        Assertions.assertEquals(10, interpreter.getEnvironment().get("foo"));
    }

    @Test
    public void assignment_factor() {
        ScarletInterpreter interpreter = interpret("""
                let foo = 5 * 5
                """);

        Assertions.assertEquals(25, interpreter.getEnvironment().get("foo"));
    }
}
