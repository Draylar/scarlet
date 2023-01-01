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
package dev.draylar.scarlet.function;

import dev.draylar.scarlet.Testing;
import dev.draylar.scarlet.language.ScarletInterpreter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FunctionTests {

    private static final String SIMPLE_FUNCTION = """
            let global = 0
            
            function mutateGlobal() {
                global = 5
            }
            
            mutateGlobal()
            """;

    private static final String ARGUMENT_FUNCTION = """
            let global = 0
           
            function mutateGlobal(value) {
                global = value
            }
            
            mutateGlobal(5)
            """;


    private static final String RETURN_FUNCTION = """            
            function value() {
                return 5
            }
            
            let global = value()
            """;

    private static final String RETURN_ARGUMENT_FUNCTION = """            
            function value(argument) {
                return argument
            }
            
            let global = value(5)
            """;

    @Test
    public void interpretFunction() {
        ScarletInterpreter interpreter = Testing.interpret(SIMPLE_FUNCTION);
        Assertions.assertEquals(5, Testing.fetchGlobal(interpreter, "global"));
    }

    @Test
    public void interpretArgumentFunction() {
        ScarletInterpreter interpreter = Testing.interpret(ARGUMENT_FUNCTION);
        Assertions.assertEquals(5, Testing.fetchGlobal(interpreter, "global"));
    }

    @Test
    public void interpretReturnFunction() {
        ScarletInterpreter interpreter = Testing.interpret(RETURN_FUNCTION);
        Assertions.assertEquals(5, Testing.fetchGlobal(interpreter, "global"));
    }

    @Test
    public void interpretReturnArgumentFunction() {
        ScarletInterpreter interpreter = Testing.interpret(RETURN_ARGUMENT_FUNCTION);
        Assertions.assertEquals(5, Testing.fetchGlobal(interpreter, "global"));
    }
}
