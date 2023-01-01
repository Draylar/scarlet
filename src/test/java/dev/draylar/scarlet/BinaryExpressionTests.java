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
import dev.draylar.scarlet.language.Token;
import dev.draylar.scarlet.language.TokenType;
import dev.draylar.scarlet.language.expression.BinaryExpression;
import dev.draylar.scarlet.language.expression.LiteralExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BinaryExpressionTests {

    @Test
    public void addition_integers_resultsInInteger() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5), new Token(TokenType.PLUS), new LiteralExpression(5));
        Assertions.assertEquals(10, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void addition_mixedNumberTypes_resultsInDouble() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5.0), new Token(TokenType.PLUS), new LiteralExpression(5));
        Assertions.assertEquals(10.0, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void addition_doubles_resultsInDouble() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5.0), new Token(TokenType.PLUS), new LiteralExpression(5.0));
        Assertions.assertEquals(10.0, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void subtraction_doubleNegation_resultsInDoubleZero() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5.0), new Token(TokenType.MINUS), new LiteralExpression(5.0));
        Assertions.assertEquals(0.0, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void subtraction_integerNegation_resultsInIntegerZero() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5), new Token(TokenType.MINUS), new LiteralExpression(5));
        Assertions.assertEquals(0, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void division_same_resultsInOne() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5.0), new Token(TokenType.SLASH), new LiteralExpression(5.0));
        Assertions.assertEquals(1.0, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void division_duplicatedIntegers_resultsInIntegerOne() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5), new Token(TokenType.SLASH), new LiteralExpression(5));
        Assertions.assertEquals(1, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void multiplication_doubles_resultsInDouble() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5.0), new Token(TokenType.STAR), new LiteralExpression(5.0));
        Assertions.assertEquals(25.0, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void multiplication_integers_resultsInInteger() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5), new Token(TokenType.STAR), new LiteralExpression(5));
        Assertions.assertEquals(25, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void greater_double_success() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(6), new Token(TokenType.GREATER), new LiteralExpression(5));
        Assertions.assertEquals(true, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void greater_double_failure() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(4), new Token(TokenType.GREATER), new LiteralExpression(5));
        Assertions.assertEquals(false, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void greaterEqual_integerEqual_success() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5), new Token(TokenType.GREATER_EQUAL), new LiteralExpression(5));
        Assertions.assertEquals(true, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void greaterEqual_integerGreater_success() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(6), new Token(TokenType.GREATER_EQUAL), new LiteralExpression(5));
        Assertions.assertEquals(true, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void equality_integer_success() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5), new Token(TokenType.EQUAL_EQUAL), new LiteralExpression(5));
        Assertions.assertEquals(true, addition.evaluate(new ScarletInterpreter()));
    }

    @Test
    public void equality_double_success() {
        BinaryExpression addition = new BinaryExpression(new LiteralExpression(5.0), new Token(TokenType.EQUAL_EQUAL), new LiteralExpression(5.0));
        Assertions.assertEquals(true, addition.evaluate(new ScarletInterpreter()));
    }
}
