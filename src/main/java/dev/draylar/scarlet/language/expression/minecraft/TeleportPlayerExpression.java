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
package dev.draylar.scarlet.language.expression.minecraft;

import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.expression.Expression;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class TeleportPlayerExpression extends Expression {

    private final Expression player;
    private final Expression x;
    private final Expression y;
    private final Expression z;

    public TeleportPlayerExpression(Expression player, Expression x, Expression y, Expression z) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Object evaluate(ScarletInterpreter interpreter) {
        if(world instanceof ServerWorld serverWorld) {
            double xVal = x.evaluate(interpreter, double.class);
            double yVal = y.evaluate(interpreter, double.class);
            double zVal = z.evaluate(interpreter, double.class);
            String name = player.evaluate(interpreter, String.class);

            ServerPlayerEntity target = world.getServer().getPlayerManager().getPlayer(name);
            if(target != null) {
                target.teleport(serverWorld, xVal, yVal, zVal, target.getYaw(), target.getPitch());
                return true;
            }
        }

        return false;
    }
}
