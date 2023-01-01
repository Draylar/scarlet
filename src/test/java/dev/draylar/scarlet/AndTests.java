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
package dev.draylar.scarlet;

import dev.draylar.scarlet.language.ScarletInterpreter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static dev.draylar.scarlet.Testing.fetchGlobal;
import static dev.draylar.scarlet.Testing.interpret;

public class AndTests {

    private static final String AND_OPERATOR = """
            let foo = true && false
            let banana = (5 == 5) && (10 == 7)
            let truth = true && true
            let secondTruth = true and true
            let lie = false or false
            let paren = (true and false)
            """;

    @Test
    public void evaluateAnd() {
        ScarletInterpreter interpreter = interpret(AND_OPERATOR);
        Assertions.assertEquals(false, fetchGlobal(interpreter, "foo"));
        Assertions.assertEquals(false, fetchGlobal(interpreter, "banana"));
        Assertions.assertEquals(true, fetchGlobal(interpreter, "truth"));
        Assertions.assertEquals(true, fetchGlobal(interpreter, "secondTruth"));
        Assertions.assertEquals(false, fetchGlobal(interpreter, "lie"));
        Assertions.assertEquals(false, fetchGlobal(interpreter, "paren"));
    }
}
