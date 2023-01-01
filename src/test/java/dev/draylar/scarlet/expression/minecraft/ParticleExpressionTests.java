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
package dev.draylar.scarlet.expression.minecraft;

import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.ScarletLexer;
import dev.draylar.scarlet.language.TokenType;
import dev.draylar.scarlet.language.expression.minecraft.ParticleExpression;
import dev.draylar.scarlet.language.statement.ExpressionStatement;
import dev.draylar.scarlet.language.statement.Statement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.draylar.scarlet.Testing.*;

public class ParticleExpressionTests {

    private static final String SPAWN_PARTICLE = """
            spawn particle end_rod at 5, 0, 0
            """;

    @Test
    public void tokenizeParticle() {
        ScarletLexer tokenizer = new ScarletLexer(SPAWN_PARTICLE);
        assertTokensMatch(tokenizer.parseTokens(),
                TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.IDENTIFIER,
                TokenType.NUMBER, TokenType.COMMA,
                TokenType.NUMBER, TokenType.COMMA,
                TokenType.NUMBER
        );
    }

    @Test
    public void parseParticle() {
        List<Statement> parsed = parser(SPAWN_PARTICLE).parse();
        assertStatementsMatch(parsed, ExpressionStatement.class);

        if(parsed.get(0) instanceof ExpressionStatement statement) {
            Assertions.assertInstanceOf(ParticleExpression.class, statement.getExpression());
            if(statement.getExpression() instanceof ParticleExpression expression) {
                Assertions.assertEquals(5, expression.x.evaluate(new ScarletInterpreter()));
            }
        }
    }
}
