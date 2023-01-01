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
package dev.draylar.scarlet.expression;

import dev.draylar.scarlet.Testing;
import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.ScarletParser;
import dev.draylar.scarlet.language.statement.Statement;
import dev.draylar.scarlet.language.statement.VariableStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SizeExpressionTests {

    private static final String SIZE_TEST = """
            let foo = size of range(0, 5)
            """;

    @Test
    public void parseSizeOfRange() {
        ScarletParser parser = Testing.parser(SIZE_TEST);
        List<Statement> statements = parser.parse();
        Testing.assertStatementsMatch(statements, VariableStatement.class);
    }

    @Test
    public void interpretSizeOfRange() {
        ScarletInterpreter result = Testing.interpret(SIZE_TEST);
        Assertions.assertEquals(5, Testing.fetchGlobal(result, "foo"));
    }
}
