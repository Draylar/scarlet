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

import java.util.Objects;

public class BinaryExpression extends Expression {

    private final Expression left;
    private final Token operator;
    private final Expression right;

    public BinaryExpression(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public Token getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public Object evaluate(ScarletInterpreter interpreter) {
        Object left = this.left.evaluate(interpreter);
        Object right = this.right.evaluate(interpreter);

        if(operator.type() == TokenType.PLUS) {
            if(left instanceof Integer a && right instanceof Integer b) {
                return a + b;
            }

            if(left instanceof Number a && right instanceof Number b) {
                return a.doubleValue() + b.doubleValue();
            }

            if(left instanceof String a && right instanceof String b) {
                return a + b;
            }

            if(left instanceof Number a && right instanceof String b) {
                return a + b;
            }

            if(left instanceof String a && right instanceof Number b) {
                return a + b;
            }

            throw new RuntimeException("Operands must be strings or numbers!");
        }

        // If both numbers are integers, we can attempt to cast backwards from double to int after doing calculations.
        boolean tryIntCast = left instanceof Integer && right instanceof Integer;
        Number leftNumber = (Number) left;
        Number rightNumber = (Number) right;

        Object value = switch (operator.type()) {
            case MINUS -> {
                checkNumberOperand(operator, left, right);
                yield leftNumber.doubleValue() - rightNumber.doubleValue();
            }

            case SLASH -> {
                checkNumberOperand(operator, left, right);
                yield leftNumber.doubleValue() / rightNumber.doubleValue();
            }

            case STAR -> {
                checkNumberOperand(operator, left, right);
                yield leftNumber.doubleValue() * rightNumber.doubleValue();
            }

            case GREATER -> {
                checkNumberOperand(operator, left, right);
                yield leftNumber.doubleValue() > rightNumber.doubleValue();
            }

            case GREATER_EQUAL -> {
                checkNumberOperand(operator, left, right);
                yield leftNumber.doubleValue() >= rightNumber.doubleValue();
            }

            case LESS -> {
                checkNumberOperand(operator, left, right);
                yield leftNumber.doubleValue() < rightNumber.doubleValue();
            }

            case LESS_EQUAL -> {
                checkNumberOperand(operator, left, right);
                yield leftNumber.doubleValue() <= rightNumber.doubleValue();
            }

            case EQUAL_EQUAL -> isEqual(left, right);
            case BANG_EQUAL -> !isEqual(left, right);
            default -> null;
        };

        if(value instanceof Double d && tryIntCast) {
            if(((int) d.doubleValue()) == d) {
                return d.intValue();
            }
        }

        return value;
    }

    private boolean isEqual(Object a, Object b) {
        if(a == null && b == null) return true;
        return Objects.equals(a, b);
    }
}
