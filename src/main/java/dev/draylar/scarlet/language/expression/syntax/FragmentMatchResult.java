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

public final class FragmentMatchResult {

    private static final FragmentMatchResult SUCCESS = new FragmentMatchResult(true, null);
    private static final FragmentMatchResult FAILURE = new FragmentMatchResult(false, null);
    private final boolean okay;
    private final String message;
    private int offset = 0;

    public FragmentMatchResult(boolean okay, String message) {
        this.okay = okay;
        this.message = message;
    }

    public static FragmentMatchResult success() {
        return SUCCESS;
    }

    public static FragmentMatchResult failure() {
        return FAILURE;
    }

    public static FragmentMatchResult issue(String reason) {
        return new FragmentMatchResult(false, reason);
    }

    public static FragmentMatchResult from(boolean value) {
        return value ? SUCCESS : FAILURE;
    }

    public boolean okay() {
        return okay;
    }

    public String message() {
        return message;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
