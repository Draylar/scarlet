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

import dev.draylar.scarlet.language.Environment;
import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.statement.BlockStatement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.List;

public class CountEntitySyntaxExpression extends ScarletPatternExpression {

    private static final RegistryFragment<EntityType<?>> ENTITY_FILTER = RegistryFragment.of(Registry.ENTITY_TYPE);
    private static final NumberFragment RADIUS = NumberFragment.of();
    private static final EnumFragment RADIUS_UNIT = EnumFragment.of(List.of("blocks", "chunks"));

    private static final ScarletSyntax COUNT_ENTITY = new ScarletSyntax(
            LiteralFragment.optional("all"),
            ENTITY_FILTER,
            LiteralFragment.optional("within"),
            RADIUS,
            RADIUS_UNIT
    );

    @Override
    public ScarletSyntax getPattern() {
        return COUNT_ENTITY;
    }

    @Override
    public Object apply(ScarletSyntax.Instance syntax, ScarletInterpreter interpreter, BlockStatement block) {
        EntityType<?> filter = syntax.get(ENTITY_FILTER);
        double radius = syntax.get(RADIUS);
        String unit = RADIUS_UNIT.getTokens().get(syntax.get(RADIUS_UNIT));

        Environment environment = interpreter.getEnvironment();
        Vec3d position = environment.getImplicitPosition();

        if(position != null) {
            return interpreter.getWorld().getEntitiesByClass(Entity.class, new Box(new BlockPos(position.getX(), position.getY(), position.getZ())).expand(radius), it -> {
                if(it.getType().equals(filter)) {
                    if(it.getPos().distanceTo(position) <= radius) {
                        return true;
                    }
                }

                return false;
            });
        }

        return Collections.emptyList();
    }
}
