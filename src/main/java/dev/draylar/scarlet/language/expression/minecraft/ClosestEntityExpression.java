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
import dev.draylar.scarlet.language.exception.ScarletError;
import dev.draylar.scarlet.language.expression.syntax.LiteralFragment;
import dev.draylar.scarlet.language.expression.syntax.ScarletPatternExpression;
import dev.draylar.scarlet.language.expression.syntax.ScarletSyntax;
import dev.draylar.scarlet.language.statement.BlockStatement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ClosestEntityExpression extends ScarletPatternExpression {

    private static final ScarletSyntax SYNTAX = new ScarletSyntax(LiteralFragment.of("closest"), LiteralFragment.of("entity"));

    @Override
    public ScarletSyntax getPattern() {
        return SYNTAX;
    }

    @Override
    public Object apply(ScarletSyntax.Instance syntax, ScarletInterpreter interpreter, BlockStatement block) {
        World world = interpreter.getWorld();

        if(world instanceof ServerWorld serverWorld) {
//            GameContext context = interpreter.getGameContext();
//            if(context.getEntity() != null) {
//                Vec3d position = context.getPosition() == null ? context.getEntity().getPos() : context.getPosition();
//                return serverWorld.getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, null, position.x, position.y, position.z, new Box(new BlockPos(position)).expand(64));
//            }
        }

        throw new ScarletError("");
    }
}
