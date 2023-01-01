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

import dev.draylar.scarlet.ScarletLanguage;
import dev.draylar.scarlet.language.exception.ScarletError;
import dev.draylar.scarlet.language.expression.*;
import dev.draylar.scarlet.language.expression.minecraft.BreakBlockExpression;
import dev.draylar.scarlet.language.expression.minecraft.ParticleExpression;
import dev.draylar.scarlet.language.expression.minecraft.TeleportPlayerExpression;
import dev.draylar.scarlet.language.expression.syntax.FragmentMatchResult;
import dev.draylar.scarlet.language.expression.syntax.ScarletPatternExpression;
import dev.draylar.scarlet.language.expression.syntax.ScarletSyntax;
import dev.draylar.scarlet.language.statement.*;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

import static dev.draylar.scarlet.language.TokenType.*;

public class ScarletParser {

    private final List<Token> tokens;
    private final ServerPlayerEntity trigger;
    private int current = 0;

    public ScarletParser(List<Token> tokens) {
        this.tokens = tokens;
        this.trigger = null;
    }

    public ScarletParser(List<Token> tokens, ServerPlayerEntity trigger) {
        this.tokens = tokens;
        this.trigger = trigger;
    }

    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Statement declaration() {
        if(match(LET)) {
            return variableDeclaration();
        }

        if(match(ON)) {
            return eventDeclaration();
        }

        if(match(FUNCTION)) {
            return function();
        }

        // Parse non-token identifiers
        Token next = peek();

        // TODO: how do we handle "after = 1"?
        // TODO: this is bad, also where should we put these
        if(next.type() == IDENTIFIER) {
            switch (next.lexeme()) {
                case "after": {
                    advance();
                    return after();
                }

                case "break": {
                    Token nextx2 = peekNext();
                    if(nextx2 != null && nextx2.lexeme().equals("block")) {
                        advance();
                        advance();
                        return new ExpressionStatement(breakBlock());
                    }
                }

                case "spawn": {
                    Token nextx2 = peekNext();
                    if(nextx2 != null && nextx2.lexeme().equals("particle")) {
                        advance();
                        advance();
                        return new ExpressionStatement(spawnParticle());
                    }
                }

                case "teleport": {
                    advance();
                    return new ExpressionStatement(teleport());
                }
            }
        }

        return statement();
    }

    private Statement function() {
        Token name = consume(IDENTIFIER, "Expected to find function name after 'function' keyword!");
        consume(LEFT_PARENTHESIS, "Expected to find opening parenthesis after function name!");

        // Consume function arguments.
        List<String> arguments = new ArrayList<>();
        while(match(IDENTIFIER)) {
            arguments.add(previous().lexeme());
            match(COMMA);
        }

        consume(RIGHT_PARENTHESIS, "Expected to find closing parenthesis after function name!");
        consume(OPEN_SCOPE, "Expected to find opening scope after function declaration!");
        List<Statement> expression = block();
        return new FunctionStatement(name.lexeme(), expression, arguments);
    }

    private Expression teleport() {
        Expression target = expression();
        consume(TokenType.TO, "Expected to after teleport");
        Expression x = expression();
        consume(COMMA, "Expected comma after X coordinate!");
        Expression y = expression();
        consume(COMMA, "Expected comma after Y coordinate!");
        Expression z = expression();
        return new TeleportPlayerExpression(target, x, y, z);
    }

    private Statement after() {

        // Time value.
        Expression time = expression();

        // Time unit.
        Token token = matchIdentifier("ticks", "Expected to find a time unit after wait declaration!");

        if(match(OPEN_SCOPE)) {
            List<Statement> block = block();
            return new AfterStatement(time, token, new BlockStatement(block));
        }

        throw error(peek(), "Expected to find opening scope after wait declaration!");
    }

    private boolean matchIdentifier(String identifier) {
        if(peek().type() == IDENTIFIER) {
            return Objects.equals(peek().lexeme(), identifier);
        }

        return false;
    }

    private Token matchIdentifier(String identifier, String message) {
        if(isAtEnd()) {
            throw error(peek(), message);
        }

        if(peek().type() == IDENTIFIER) {
            if(Objects.equals(peek().lexeme(), identifier)) {
                return advance();
            }
        }

        throw error(peek(), message);
    }

    /*
     * Declares an event using the 'on' keyword. The syntax is as follows:
     *
     * on <event name>:
     *     // scoped logic
     *
     * To determine whether an event name is valid, we iterate over all types in ScarletEventType
     * and search for a matching name.
     */
    private Statement eventDeclaration() {
        // Keep track of syntax issues that occur while matching language fragments.
        Map<ScarletPatternExpression, FragmentMatchResult> issues = new HashMap<>();

        // Check each language fragment for matches.
        for (ScarletPatternExpression pattern : ScarletLanguage.EVENTS) {
            ParserAccess access = new ParserAccess(this::peek, this::expression, this::advance);
            int restore = current;
            FragmentMatchResult result = pattern.getPattern().match(access);
            if(result.okay()) {
                ScarletSyntax.Instance instance = new ScarletSyntax.Instance(pattern.getPattern(), access);
                consume(OPEN_SCOPE, "give me open scope");
                return new ExpressionStatement(new PatternExpression(pattern, instance, new BlockStatement(block())));
            } else {
                if(result.message() != null) {
                    issues.put(pattern, result);
                }
            }

            // Pattern did not match. Move back to the starting index from before we started consuming tokens.
            current = restore;
        }

        // todo: sort by depth of erroring token
        // Print out any possible issues to clue the user into why the event is failing.
        // Example: 'on death of creeper_' is invalid due to the registry fragment, but may still match if fixed.
        if(!issues.isEmpty()) {
            var entries = new ArrayList<>(issues.entrySet());
            var first = entries.get(0);
            String more = entries.size() == 1 ? "" : " ( %d more)".formatted(entries.size() - 1);
            throw error(tokens.get(current + first.getValue().getOffset()), first.getValue().message() + more);
        }

        throw error(peek(), "Expected to find an event, but no registered syntax matched!");
    }

    private Statement variableDeclaration() {
        Token variableName = consume(IDENTIFIER, "Expected a variable name!");

        Expression initializer = null;
        if(match(EQUAL)) {
            initializer = expression();
        }

        return new VariableStatement(variableName, initializer);
    }

    private Statement statement() {
        if(match(FOR)) {
            return forStatement();
        }

        if(match(IF)) {
            return ifStatement();
        }

        if(match(PRINT)) {
            return printStatement();
        }

        if(match(RETURN)) {
            return returnStatement();
        }

        if(matchIdentifier("sort")) {
            advance();
            return new SortStatement(expression());
        }

        if(matchIdentifier("chat")) {
            advance();
            return new ChatStatement(expression());
        }

        if(match(OPEN_SCOPE)) {
            return new BlockStatement(block());
        }

        return expressionStatement();
    }

    private Statement returnStatement() {
        Token previous = previous();
        if(check(SEMICOLON)) {
            // Return early
            return new ReturnStatement(previous, null);
        } else {
            return new ReturnStatement(previous, expression());
        }
    }

    private Statement forStatement() {
        Token variable = consume(IDENTIFIER, "Expected variable name after for-loop 'for' declaration!");
        consume(IN, "Expected 'in' after for-loop identifier!");

        Statement range;
        if(match(VAR)) {
            range = variableDeclaration();
        } else {
            range = expressionStatement();
        }

        Statement body = statement();
        return new ForStatement(variable, range, body);
    }

    private Statement ifStatement() {
        Expression condition = expression();
        Statement statement = statement();

        Map<Expression, Statement> elseIfBranches = null;
        Statement elseStatement = null;

        // Else
        while (match(ELSE)) {
            if(match(IF)) {
                if(elseIfBranches == null) {
                    elseIfBranches = new HashMap<>();
                }

                Expression elseIfCondition = expression();
                Statement elseIfStatement = statement();
                elseIfBranches.put(elseIfCondition, elseIfStatement);
                continue;
            }

            // Ending with else
            elseStatement = statement();
            break;
        }

        return new IfStatement(condition, statement, elseIfBranches, elseStatement);
    }

    private Statement loopStatement() {


        return null;
    }

    private List<Statement> block() {
        List<Statement> statements = new ArrayList<>();

        while (!check(CLOSE_SCOPE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(CLOSE_SCOPE, "Scope was not closed with 'end' clause!");
        return statements;
    }

    private Statement printStatement() {
        Expression value = null;

        try {
            value = expression();
        } catch (ScarletError exception) {
            throw error(peek(), "Expected an expression to appear after your print statement");
        }

//        consume(TokenType.SEMICOLON, "Expected a ';' after the value!");
        return new PrintStatement(value);
    }

    private Statement expressionStatement() {
        Expression value = expression();
        return new ExpressionStatement(value);
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if(previous().type() == SEMICOLON) {
                return;
            }

            switch (peek().type()) {
                case CLASS, FUNCTION, VAR, FOR, IF, WHILE, PRINT, RETURN:
                    return;
            }

            advance();
        }
    }

    private Expression expression() {

        // Check against all pattern expressions to see if any of them match the current and forward tokens.
        // To check if a pattern is valid, we allow the ScarletPatternExpression's ScarletSyntax to process forward in
        // this parser, consuming tokens as needed to determine whether it is valid.
        // If the syntax was valid, the consumed tokens are not returned, and are fed into whatever expression was validated.
        // If the syntax was invalid at any point, we drop all read tokens & restore back to the starting index at the top of the loop.
        for (ScarletPatternExpression pattern : ScarletLanguage.PATTERN_EXPRESSIONS) {
            ParserAccess access = new ParserAccess(this::peek, this::expression, this::advance);
            int restore = current;
            if(pattern.getPattern().match(access).okay()) {
                ScarletSyntax.Instance instance = new ScarletSyntax.Instance(pattern.getPattern(), access);
                return new PatternExpression(pattern, instance);
            }

            // Pattern did not match. Move back to the starting index from before we started consuming tokens.
            current = restore;
        }

        return assignment();
    }

    private Expression assignment() {
        Expression expression = or();

        if(match(EQUAL)) {
            Token equals = previous();
            Expression value = assignment();

            if(expression instanceof VariableExpression variableExpression) {
                Token name = variableExpression.getToken();
                return new AssignExpression(name, value);
            }

            throw error(equals, "Invalid assignment target!");
        }

        return expression;
    }

    private Expression or() {
        Expression expression = and();

        while (match(OR)) {
            Token operator = previous();
            Expression right = and();
            expression = new LogicalExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression and() {
        Expression expression = equality();

        while (match(AND)) {
            Token op = previous();
            Expression right = equality();
            expression = new LogicalExpression(expression, op, right);
        }

        return expression;
    }

    private Expression equality() {
        Expression expression = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression comparison() {
        Expression expression = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression term() {
        Expression expression = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expression right = factor();
            expression = new BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression factor() {
        Expression expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expression right = unary();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression unary() {
        if(match(BANG, MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new UnaryExpression(operator, right);
        }

        return call();
    }

    private Expression call() {
        Expression expression = coerce();

        while (true) {
            if(match(LEFT_PARENTHESIS)) {
                expression = closeCall(expression);
            } else if(match(DOT)) {
                Token name = consume(IDENTIFIER, "Expected to find a property name after '.'!");

                // Lambda function
                if(match(OPEN_SCOPE)) {
                    expression = new LambdaExpression(expression, name, new BlockStatement(block()));
                } else {
                    expression = new PropertyExpression(expression, name);
                }
            } else {
                break;
            }
        }

        return expression;
    }

    private Expression closeCall(Expression expression) {
        List<Expression> arguments = new ArrayList<>();

        // While we have yet to hit a right-parenthesis (closing the function call)...
        if(!check(RIGHT_PARENTHESIS)) {
            // Add new expression, skipping comma separators
            do {
                arguments.add(expression());
            } while (match(COMMA));
        }

        Token paren = consume(RIGHT_PARENTHESIS, "Expected function call to close with a ')' character!");
        return new CallExpression(expression, paren, arguments);
    }

    private Expression coerce() {
        Expression primary = primary();

        if(match(AS)) {
            Token type = consume(IDENTIFIER, "Expected to find type after 'as' keyword!");
            return new CoercionExpression(primary, type);
        }

        return primary;
    }

    private Expression primary() {
        if(match(FALSE)) {
            return new LiteralExpression(false);
        }

        if(match(TRUE)) {
            return new LiteralExpression(true);
        }

        if(match(NIL)) {
            return new LiteralExpression(null);
        }

        if(match(NUMBER, STRING)) {
            return new LiteralExpression(previous().literal());
        }

        if(match(LEFT_PARENTHESIS)) {
            Expression expression = expression();
            consume(RIGHT_PARENTHESIS, "Expect ')' after expression.");
            return new GroupingExpression(expression);
        }

        if(match(IDENTIFIER)) {
            return new VariableExpression(previous());
        }

        throw error(peek(), "Expected to find an expression");
    }

    private Expression breakBlock() {
        matchIdentifier("at", "Expected location after block break!");
        Expression x = expression();
        consume(COMMA, "Expected comma after X coordinate!");
        Expression y = expression();
        consume(COMMA, "Expected comma after Y coordinate!");
        Expression z = expression();
        return new BreakBlockExpression(x, y, z);
    }

    private Expression spawnParticle() {
        Expression particle = expression();
        matchIdentifier("at", "Expected location after block break!");
        Expression x = expression();
        consume(COMMA, "Expected comma after X coordinate!");
        Expression y = expression();
        consume(COMMA, "Expected comma after Y coordinate!");
        Expression z = expression();
        return new ParticleExpression(particle, x, y, z);
    }

    private Token consume(TokenType type, String message) {
        if(check(type)) {
            return advance();
        }

        throw error(peek(), message);
    }

    private ScarletError error(Token token, String message) {
        if(token.type() == EOF) {
            report(token.line(), "at end", message);
        } else {
            report(token.line(), "at '" + token.lexeme() + "'", message);
        }

        int line = token.type() == EOF ? token.line() - 1 : token.line();
        return new ScarletError(
                message,
                token.lines()[line],
                line,
                token);
    }

    private void report(int line, String prefix, String message) {

    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if(check((type))) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if(isAtEnd()) {
            return false;
        }

        return peek().type() == type;
    }

    private Token advance() {
        if(!isAtEnd()) {
            current++;
        }

        return previous();
    }

    private boolean isAtEnd() {
        return peek().type() == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token peekNext() {
        if(isAtEnd()) {
            return null;
        }

        return tokens.get(current + 1);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
