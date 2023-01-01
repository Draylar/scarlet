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
package dev.draylar.scarlet.language.expression;

import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.Token;
import dev.draylar.scarlet.language.TokenType;

import static dev.draylar.scarlet.language.expression.UnaryExpression.isTruthy;

public class LogicalExpression extends Expression {

    private final Expression expression;
    private final Token token;
    private final Expression right;

    public LogicalExpression(Expression expression, Token token, Expression right) {
        this.expression = expression;
        this.token = token;
        this.right = right;
    }

    @Override
    public Object evaluate(ScarletInterpreter interpreter) {
        Object left = expression.evaluate(interpreter);

        if(TokenType.OR.equals(token.type())) {
            if(isTruthy(left)) return left;
        } else if(TokenType.AND.equals(token.type())) {
            if(!isTruthy(left)) return left;
        }

        return right.evaluate(interpreter);
    }
}
