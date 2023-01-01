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

import dev.draylar.scarlet.language.*;
import dev.draylar.scarlet.language.statement.Statement;

import java.util.List;

import static dev.draylar.scarlet.language.TokenType.IDENTIFIER;

public class Testing {

    public static ScarletInterpreter interpret(String input) {
        ScarletInterpreter interpreter = new ScarletInterpreter();
        interpreter.interpret(input);
        return interpreter;
    }

    public static void assertTokensMatch(List<Token> tokens, TokenType... types) {
        for (int i = 0; i < types.length; i++) {
            if(tokens.get(i).type() != types[i]) {
                throw new IllegalStateException(String.format("Token at index %d did not match expected type %s (found %s)!\nFull token stack: %s", i, types[i], tokens.get(i).type(), String.join(", ", tokens.stream().map(Token::type).map(Enum::name).toList())));
            }
        }
    }

    @SafeVarargs
    public static void assertStatementsMatch(List<Statement> tokens, Class<? extends Statement>... statements) {
        for (int i = 0; i < statements.length; i++) {
            if(tokens.size() <= i) {
                throw new IllegalStateException(String.format("Statement at index %d did not match expected type %s (found nothing!)!", i, statements[i]));
            } else if(tokens.get(i).getClass() != statements[i]) {
                throw new IllegalStateException(String.format("Statement at index %d did not match expected type %s (found %s!)!", i, statements[i], tokens.get(i).getClass()));
            }
        }
    }

    public static Object fetchGlobal(ScarletInterpreter interpreter, String name) {
        return interpreter.getEnvironment().get(new Token(IDENTIFIER, name, name, 0, 0, 0, new String[0]));
    }

    public static ScarletParser parser(String input) {
        ScarletLexer tokenizer = new ScarletLexer(input);
        return new ScarletParser(tokenizer.parseTokens());
    }
}
