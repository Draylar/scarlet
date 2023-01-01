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

import dev.draylar.scarlet.Scarlet;
import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.expression.Expression;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class ChatStatement extends Statement {

    private final Expression value;

    public ChatStatement(Expression value) {
        this.value = value;
    }

    @Override
    public void evaluate(ScarletInterpreter interpreter) {
        String message = String.valueOf(value.evaluate(interpreter));
        for (ServerPlayerEntity player : Scarlet.SERVER_CONTEXT.getPlayerManager().getPlayerList()) {
            player.sendMessage(new LiteralText(message), false);
        }
    }
}
