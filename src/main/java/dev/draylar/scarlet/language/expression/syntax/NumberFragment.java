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
package dev.draylar.scarlet.language.expression.syntax;

import dev.draylar.scarlet.language.ParserAccess;
import dev.draylar.scarlet.language.Token;

public class NumberFragment extends ArgumentFragment<Double> {

    public NumberFragment() {

    }

    public static NumberFragment of() {
        return new NumberFragment();
    }

    @Override
    public FragmentMatchResult match(Token token) {
        try {
            Double.parseDouble(token.lexeme());
            return FragmentMatchResult.success();
        } catch (Exception any) {
            return FragmentMatchResult.failure();
        }
    }

    @Override
    public Double get(ParserAccess access) {
        if(access.getRead().get(this) instanceof Token token) {
            return Double.parseDouble(token.lexeme());
        }

        return null;
    }
}

