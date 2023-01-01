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

import dev.draylar.scarlet.Testing;
import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.ScarletParser;
import dev.draylar.scarlet.language.expression.Expression;
import dev.draylar.scarlet.language.expression.minecraft.event.ScarletLanguageDebugSyntaxExpression;
import dev.draylar.scarlet.language.expression.syntax.ScarletPatternExpression;
import dev.draylar.scarlet.language.statement.PatternExpression;
import dev.draylar.scarlet.language.statement.Statement;
import dev.draylar.scarlet.language.statement.VariableStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SyntaxExpressionArgumentTests {

    private static final String ROOT = """
            let foo = expression_argument 5 + 5
            """;

    @Test
    public void parseExpressionSyntaxArgument() {
        ScarletParser parse = Testing.parser(ROOT);
        List<Statement> statements = parse.parse();
        Testing.assertStatementsMatch(statements, VariableStatement.class);

        Statement statement = statements.get(0);
        if(statement instanceof VariableStatement variableStatement) {
            Expression expression = variableStatement.getInitializer();
            Assertions.assertInstanceOf(PatternExpression.class, expression);
            if(expression instanceof PatternExpression patternExpression) {
                ScarletPatternExpression event = patternExpression.getEvent();
                Assertions.assertInstanceOf(ScarletLanguageDebugSyntaxExpression.class, event);
            }
        }
    }

    @Test
    public void interpretExpressionSyntaxArgument() {
        ScarletInterpreter interpreter = Testing.interpret(ROOT);
        Assertions.assertEquals(10, Testing.fetchGlobal(interpreter, "foo"));
    }
}
