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
import dev.draylar.scarlet.language.ScarletLexer;
import dev.draylar.scarlet.language.TokenType;
import dev.draylar.scarlet.language.exception.ScarletError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScarletTests {

    private static final ScarletInterpreter INTERPRETER = new ScarletInterpreter();

    private String input = """
            // This evaluates when the script is first loaded.
            // Implicit global scope.
            set foo to 5
                        
            on player join:
                print "Welcome ${it.name} to the server!"
                        
                        
            """;

    @Test
    public void interpret_validTokens_valid() {
        ScarletLexer parser = new ScarletLexer("""
                5 * 5
                """);

        Assertions.assertDoesNotThrow(parser::parseTokens);
    }

    @Test
    public void interpret_singleConstant_numericType() throws ScarletError {
        ScarletLexer parser = new ScarletLexer("""
                5 * 5
                """);

        Assertions.assertEquals(TokenType.NUMBER, parser.parseTokens().get(0).type());
    }
}
