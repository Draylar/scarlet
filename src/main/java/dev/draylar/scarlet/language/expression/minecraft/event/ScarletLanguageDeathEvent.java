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
package dev.draylar.scarlet.language.expression.minecraft.event;

import dev.draylar.scarlet.language.Environment;
import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.expression.syntax.LiteralFragment;
import dev.draylar.scarlet.language.expression.syntax.RegistryFragment;
import dev.draylar.scarlet.language.expression.syntax.ScarletPatternExpression;
import dev.draylar.scarlet.language.expression.syntax.ScarletSyntax;
import dev.draylar.scarlet.language.statement.BlockStatement;
import dev.draylar.scarlet.minecraft.event.impl.ScarletEntityEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

public class ScarletLanguageDeathEvent extends ScarletPatternExpression {

    private static final RegistryFragment<EntityType<?>> ENTITY_TYPE = RegistryFragment.of(Registry.ENTITY_TYPE);

    private static final ScarletSyntax SYNTAX = new ScarletSyntax(
            LiteralFragment.of("death"),
            LiteralFragment.of("of"),
            ENTITY_TYPE
    );

    @Override
    public ScarletSyntax getPattern() {
        return SYNTAX;
    }

    @Override
    public Object apply(ScarletSyntax.Instance syntax, ScarletInterpreter interpreter, BlockStatement block) {
        EntityType<?> type = syntax.get(ENTITY_TYPE);

        ScarletEntityEvents.DEATH.registerReloadable((entity, source) -> {
            if(entity.world.isClient) {
                return;
            }

            if(type == null || entity.getType() == type) {
                // TODO: This isn't going to work, because the interpreter environment will have changed when we run this code in the future.
                Environment env = interpreter.pushEnvironment();
                env.bind(entity);
                env.define("source", source);
                block.evaluate(interpreter);
                interpreter.popEnvironment();
            }
        });

        return null;
    }
}
