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
package dev.draylar.scarlet.event;

import dev.draylar.scarlet.language.ScarletLexer;
import dev.draylar.scarlet.language.Token;
import dev.draylar.scarlet.language.expression.syntax.ScarletPatternExpression;
import dev.draylar.scarlet.language.expression.minecraft.event.ScarletLanguageDebugTestEvent;
import dev.draylar.scarlet.language.exception.ScarletError;
import dev.draylar.scarlet.language.expression.Expression;
import dev.draylar.scarlet.language.statement.ExpressionStatement;
import dev.draylar.scarlet.language.statement.PatternExpression;
import dev.draylar.scarlet.language.statement.Statement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.draylar.scarlet.Testing.*;
import static dev.draylar.scarlet.language.TokenType.*;

public class TickEntityTests {

    private static final String INPUT = """
            on test_event test_argument_1 {
                chat "hi"
            }
            """;

    private static final String INVALID = """
            on test_event blah {
                chat "hi"
            }
            """;

    @Test
    public void tokenizeTickEntity() {
        ScarletLexer tokenizer = new ScarletLexer(INPUT);
        List<Token> tokens = tokenizer.parseTokens();

        assertTokensMatch(
                tokens,
                ON, IDENTIFIER, IDENTIFIER, OPEN_SCOPE,
                IDENTIFIER, STRING,
                CLOSE_SCOPE
        );
    }

    @Test
    public void parseTickEntity() {
        List<Statement> parsed = parser(INPUT).parse();

        assertStatementsMatch(
                parsed,
                ExpressionStatement.class
        );

        Statement statement = parsed.get(0);
        Assertions.assertInstanceOf(ExpressionStatement.class, statement);

        if(statement instanceof ExpressionStatement event) {
            Expression expression = event.getExpression();
            if(expression instanceof PatternExpression pattern) {
                ScarletPatternExpression langEvent = pattern.getEvent();
                Assertions.assertInstanceOf(ScarletLanguageDebugTestEvent.class, langEvent);
            }
        }
    }

    @Test
    public void failInvalidEventParsing() {
        Assertions.assertThrows(ScarletError.class, () -> parser(INVALID).parse());
    }
}
