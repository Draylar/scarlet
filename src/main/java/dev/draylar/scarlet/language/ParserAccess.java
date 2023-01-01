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
package dev.draylar.scarlet.language;

import dev.draylar.scarlet.language.expression.Expression;
import dev.draylar.scarlet.language.expression.syntax.Fragment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ParserAccess {

    private final Supplier<Token> peek;
    private final Supplier<Expression> expression;
    private final Supplier<Token> advance;
    private final Map<Fragment, Object> read = new HashMap<>();
    private Fragment fragment;

    public ParserAccess(Supplier<Token> peek, Supplier<Expression> expression, Supplier<Token> advance) {
        this.peek = peek;
        this.expression = expression;
        this.advance = advance;
    }

    public Token peek() {
        return peek.get();
    }

    public Expression expression() {
        Expression expression = this.expression.get();
        read.put(fragment, expression);
        return expression;
    }

    public Token advance() {
        Token token = advance.get();
        read.put(fragment, token);
        return token;
    }

    public void active(Fragment fragment) {
        this.fragment = fragment;
    }

    public Map<Fragment, Object> getRead() {
        return read;
    }
}
