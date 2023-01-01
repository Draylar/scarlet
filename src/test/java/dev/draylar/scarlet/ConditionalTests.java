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
import dev.draylar.scarlet.language.ScarletParser;
import dev.draylar.scarlet.language.ScarletLexer;
import dev.draylar.scarlet.language.statement.IfStatement;
import dev.draylar.scarlet.language.statement.VariableStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static dev.draylar.scarlet.Testing.*;
import static dev.draylar.scarlet.language.TokenType.*;

public class ConditionalTests {

    private static final String CONDITIONAL = """
            let variable = 0
                        
            if variable == 0 {
                variable = 5
            }
            """;

    @Test
    public void tokenizeConditional() {
        ScarletLexer tokenizer = new ScarletLexer(CONDITIONAL);
        assertTokensMatch(tokenizer.parseTokens(),
                LET, IDENTIFIER, EQUAL, NUMBER,
                IF, IDENTIFIER, EQUAL_EQUAL, NUMBER, OPEN_SCOPE,
                IDENTIFIER, EQUAL, NUMBER,
                CLOSE_SCOPE
        );
    }

    @Test
    public void parseConditional() {
        ScarletLexer tokenizer = new ScarletLexer(CONDITIONAL);
        ScarletParser parser = new ScarletParser(tokenizer.parseTokens());
        assertStatementsMatch(parser.parse(),
                VariableStatement.class,
                IfStatement.class
        );
    }

    @Test
    public void evaluateConditional() {
        ScarletInterpreter interpreter = interpret(CONDITIONAL);
        Assertions.assertEquals(5, fetchGlobal(interpreter, "variable"));
    }
}
