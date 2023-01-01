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

import com.google.common.collect.ImmutableMap;
import dev.draylar.scarlet.language.exception.ScarletError;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.draylar.scarlet.language.TokenType.*;

public class ScarletLexer {

    private static final Map<String, TokenType> RESERVED_KEYWORDS = ImmutableMap.<String, TokenType>builder()
            .put("and", AND)
            .put("class", CLASS)
            .put("else", ELSE)
            .put("false", FALSE)
            .put("for", FOR)
            .put("function", FUNCTION)
            .put("if", IF)
            .put("nil", NIL)
            .put("or", OR)
            .put("print", PRINT)
            .put("return", RETURN)
            .put("super", SUPER)
            .put("this", THIS)
            .put("true", TRUE)
            .put("var", VAR)
            .put("while", WHILE)
            .put("in", IN)
            .put("let", LET)
            .put("on", ON)
            .put("as", AS)
            .build();

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 0;
    private int lineStart = 0;
    private String[] lines;

    public ScarletLexer(String input) {
        this.source = input;
        lines = input.split("\n");
    }

    public List<Token> parseTokens() throws ScarletError {
        while (!isAtEnd()) {
            start = current;
            scanCurrent();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line, 0, start, lines));
        return tokens;
    }

    private void scanCurrent() throws ScarletError {
        char c = advance();

        switch (c) {
            // Strings
            case '"' -> string();

            case '(' -> addToken(LEFT_PARENTHESIS);
            case ')' -> addToken(RIGHT_PARENTHESIS);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);
            case '{' -> addToken(OPEN_SCOPE);
            case '}' -> addToken(CLOSE_SCOPE);

            // Special double types
            case '!' -> addToken(next('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            case '>' -> addToken(next('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            case '<' -> addToken(next('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
            case '=' -> addToken(next('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);

            case '&' -> {
                if(next('&')) {
                    addToken(AND);
                }
            }

            case '|' -> {
                if(next('|')) {
                    addToken(OR);
                }
            }

            // Comments
            case '/' -> {
                // If we're parsing a double // (comments), skip all characters until the end of the line.
                if(next('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(TokenType.SLASH);
                }
            }

            // Ignore whitespace
            case ' ', '\t', '\r' -> {
            }

            // Newlines
            case '\n' -> {
                line++;
                lineStart = current;
            }

            default -> {
                // Numbers
                if(Character.isDigit(c)) {
                    number();
                    break;
                }

                // Identifiers
                if(Character.isAlphabetic(c)) {
                    identifier();
                    break;
                }

                throw new ScarletError(String.format("Encountered an unexpected character '%s'", c));
            }
        }
    }

    private void identifier() {
        while (true) {
            char peek = peek();
            if(!(Character.isAlphabetic(peek) || Character.isDigit(peek) || peek == '_')) break;
            advance();
        }

        TokenType type = IDENTIFIER;
        String identifier = source.substring(start, current);
        @Nullable TokenType keyword = RESERVED_KEYWORDS.get(identifier);
        if(keyword != null) {
            type = keyword;
        }

        addToken(type);
    }

    private void number() {
        // Progress while the current character is a number.
        while (Character.isDigit(peek())) {
            advance();
        }

        // Handle decimal place
        if(peek() == '.' && Character.isDigit(peekNext())) {
            advance();
            while (Character.isDigit(peek())) {
                advance();
            }
        }

        String value = source.substring(start, current);
        try {
            int asInt = Integer.parseInt(value);
            addToken(NUMBER, asInt);
            return;
        } catch (Exception ignored) {

        }

        addToken(NUMBER, Double.parseDouble(value));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void string() throws ScarletError {
        // Parse until we each the end of the file or an end quote.
        boolean closed = false;
        while (peek() != '"' && !isAtEnd()) {
            advance();
            closed = peek() == '"';
        }

        if(!closed && isAtEnd()) {
            throw new ScarletError(String.format("Encountered unterminated string on line %d!", line));
        }

        advance();

        // Get rid of quotes from string
        String stringValue = source.substring(start + 1, current - 1);
        addToken(STRING, stringValue);
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }

        return source.charAt(current);
    }

    private char peekNext() {
        if(current + 1 >= source.length()) {
            return '\0';
        }

        return source.charAt(current + 1);
    }

    private boolean next(char input) {
        if(isAtEnd()) {
            return false;
        }

        if(source.charAt(current) != input) {
            return false;
        }

        current++;
        return true;
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object value) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, value, line, lineStart, start, lines));
    }
}
