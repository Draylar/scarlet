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
import dev.draylar.scarlet.language.expression.VariableExpression;
import dev.draylar.scarlet.language.exception.ScarletError;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleExpression extends Expression {

    public final Expression particle;
    public final Expression x;
    public final Expression y;
    public final Expression z;

    public ParticleExpression(Expression particle, Expression x, Expression y, Expression z) {
        this.particle = particle;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Object evaluate(ScarletInterpreter interpreter) {
        if(world instanceof ServerWorld serverWorld) {
            DefaultParticleType type = parseParticleType(particle, interpreter);
            if(type != null) {
                return serverWorld.spawnParticles(
                        type,
                        (Double) x.evaluate(interpreter),
                        (Double) y.evaluate(interpreter),
                        (Double) z.evaluate(interpreter),
                        1,
                        0,
                        0,
                        0,
                        0
                );
            } else {
                // TODO: ScarletTypeError?
                throw new ScarletError(String.format("Expected to find type 'string' for particle, but found %s instead!", particle.toString()));
            }
        }

        return 0;
    }

    private DefaultParticleType parseParticleType(Expression expression, ScarletInterpreter interpreter) {

        // First off - to avoid having to surround types with quotation, we check if the variable can be directly
        // converted into a particle type. If so, we return that immediately.
        if(expression instanceof VariableExpression variable) {
            Identifier byLexeme = new Identifier(variable.getToken().lexeme());
            if(Registry.PARTICLE_TYPE.containsId(byLexeme)) {
                return (DefaultParticleType) Registry.PARTICLE_TYPE.get(byLexeme);
            }
        }

        // Fall back to evaluating the variable.
        Object id = particle.evaluate(interpreter);
        if(id instanceof String str) {
            Identifier byStr = new Identifier(str);
            if(Registry.PARTICLE_TYPE.containsId(byStr)) {
                return (DefaultParticleType) Registry.PARTICLE_TYPE.get(byStr);
            }
        }

        return null;
    }
}
