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
import dev.draylar.scarlet.language.Token;
import dev.draylar.scarlet.language.statement.ForStatement;
import dev.draylar.scarlet.language.statement.Statement;
import dev.draylar.scarlet.language.statement.VariableStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.draylar.scarlet.Testing.interpret;
import static dev.draylar.scarlet.language.TokenType.*;

public class LoopTests {

    private static final String LOOP = """
            let foo = 0
                            
            for i in range(0, 5) {
                foo = foo + i
            }
            """;

    @Test
    public void tokenizeLoop() {
        ScarletLexer tokenizer = new ScarletLexer(LOOP);
        List<Token> tokens = tokenizer.parseTokens();
        Testing.assertTokensMatch(tokens,

                // Variable Declaration
                LET, IDENTIFIER, EQUAL, NUMBER,

                // Loop
                FOR, IDENTIFIER, IN, IDENTIFIER, LEFT_PARENTHESIS, NUMBER, COMMA, NUMBER, RIGHT_PARENTHESIS, OPEN_SCOPE,

                // Inner Loop
                IDENTIFIER, EQUAL, IDENTIFIER, PLUS, IDENTIFIER,

                CLOSE_SCOPE
        );
    }

    @Test
    public void parseLoop() {
        ScarletLexer tokenizer = new ScarletLexer(LOOP);
        ScarletParser parser = new ScarletParser(tokenizer.parseTokens());
        List<Statement> statements = parser.parse();
        Testing.assertStatementsMatch(statements,
                VariableStatement.class,
                ForStatement.class
        );
    }

    @Test
    public void evaluateLoop() {
        ScarletInterpreter interpreter = interpret(LOOP);
        Assertions.assertEquals(1 + 2 + 3 + 4, Testing.fetchGlobal(interpreter, "foo"));
    }
}
