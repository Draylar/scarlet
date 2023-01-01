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
package dev.draylar.scarlet.language.statement;

import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.expression.Expression;
import dev.draylar.scarlet.language.expression.UnaryExpression;

import java.util.Map;

public class IfStatement extends Statement {

    private final Expression condition;
    private final Statement block;
    private final Map<Expression, Statement> elseIfBranches;
    private final Statement elseStatement;

    public IfStatement(Expression condition, Statement block) {
        this(condition, block, null, null);
    }

    public IfStatement(Expression condition, Statement block, Map<Expression, Statement> elseIfBranches, Statement elseStatement) {
        this.condition = condition;
        this.block = block;
        this.elseIfBranches = elseIfBranches;
        this.elseStatement = elseStatement;
    }

    @Override
    public void evaluate(ScarletInterpreter interpreter) {
        if(UnaryExpression.isTruthy(condition.evaluate(interpreter))) {
            block.evaluate(interpreter);
            return;
        } else {

            // else if branches
            if(elseIfBranches != null) {
                var branches = elseIfBranches.entrySet();
                for (var branch : branches) {
                    if(UnaryExpression.isTruthy(branch.getKey().evaluate(interpreter))) {
                        branch.getValue().evaluate(interpreter);
                        return;
                    }
                }
            }

            // else branch
            if(elseStatement != null) {
                elseStatement.evaluate(interpreter);
            }
        }
    }
}
