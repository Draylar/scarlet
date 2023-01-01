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
package dev.draylar.scarlet.language.exception;

import dev.draylar.scarlet.language.Token;

import static dev.draylar.scarlet.language.ScarletLogging.*;

public class ScarletError extends Error {

    private final String line;
    private final int lineIndex;
    private final int characterIndex;
    private final Token token;
    private final String issue;

    public ScarletError(String message) {
        super(message);
        this.issue = null;
        this.line = "";
        this.lineIndex = 0;
        this.characterIndex = 0;
        this.token = null;
    }

    public ScarletError(String issue, String line, int lineIndex, Token token) {
        super();
        this.issue = issue;
        this.line = line;
        this.lineIndex = lineIndex;
        this.characterIndex = token.start() - token.lineStart();
        this.token = token;
    }

    @Override
    public String getMessage() {
        if(issue == null) {
            return super.getMessage();
        }

        var leadingWhitespace = line.indexOf(line.trim());
        var pipeOffset = String.valueOf(lineIndex + 1).length() - 1;
        var arrow = " ".repeat(characterIndex - leadingWhitespace) + "^".repeat(token == null ? 1 : token.lexeme().length());
        var offsetPipe = CYAN + " ".repeat(pipeOffset) + "|" + RED;
        var pipe = CYAN + "|" + RED;

        return String.format("""
                %sScarlet error%s: %s
                   %s
                   %s   %s
                 %d %s   %s
                   %s
                """,
                RED,
                RESET,
                issue,

                offsetPipe,
                offsetPipe,
                line.trim(),
                lineIndex + 1,
                pipe,
                arrow,
                offsetPipe);
    }

    @Override
    public void printStackTrace() {
        if(issue != null) {
            return;
        }

        super.printStackTrace();
    }
}
