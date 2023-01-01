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
package dev.draylar.scarlet.error;

import dev.draylar.scarlet.language.ScarletInterpreter;
import org.junit.jupiter.api.Assertions;

import static dev.draylar.scarlet.Testing.interpret;

public class ErrorTests {

    private static final String NO_VARIABLE = """
            prin "hi"
            """;

    private static final String NO_EXPRESSION = """
            print
            """;

    public void errorText() {
        try {
            ScarletInterpreter interpreter = interpret(NO_VARIABLE);
        } catch (Throwable exception) {
            Assertions.assertEquals("""
                Scarlet script at line 1
                    prin "hi"
                    ^
                
                Variable prin was not defined
                """, exception.getMessage());
        }
    }

    public void errorExpression() {
        try {
            ScarletInterpreter interpreter = interpret(NO_EXPRESSION);
        } catch (Throwable exception) {
            Assertions.assertEquals("""
                Scarlet script at line 1
                    print
                         ^
                
                Expected an expression to appear after your print statement
                """, exception.getMessage());
        }
    }

    public void errorEvent() {
        try {
            interpret("""
                    on test_event:
                        chat "hi"
                    end
                    """);
        } catch (Throwable exception) {
            Assertions.assertEquals("""
                Scarlet script at line 1
                    on test_event:
                       ^
                
                Expected to find an event, but no registered syntax matched!
                """, exception.getMessage());
        }
    }
}
