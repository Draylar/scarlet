package dev.draylar.scarlet.language.expression.minecraft;

import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.exception.ScarletError;
import dev.draylar.scarlet.language.expression.Expression;
import dev.draylar.scarlet.language.expression.syntax.*;
import dev.draylar.scarlet.language.statement.BlockStatement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class SummonExpression extends ScarletPatternExpression {

    private static final RegistryFragment<EntityType<?>> SUMMON_TYPE = RegistryFragment.of(Registry.ENTITY_TYPE);
    private static final ExpressionFragment LOCATION = ExpressionFragment.of();
    private static final ScarletSyntax SYNTAX = new ScarletSyntax(
            LiteralFragment.of("summon"),
            SUMMON_TYPE,
            LiteralFragment.of("at"),
            LOCATION
    );

    @Override
    public ScarletSyntax getPattern() {
        return SYNTAX;
    }

    @Override
    public Object apply(ScarletSyntax.Instance syntax, ScarletInterpreter interpreter, BlockStatement block) {
        World world = interpreter.getWorld();

        EntityType<?> type = syntax.get(SUMMON_TYPE);
        Expression locationExpression = syntax.get(LOCATION);
        Object posExpression = locationExpression.evaluate(interpreter);

        // position can be an Entity or Vec3d
        // TODO: position-like interface injection?
        Vec3d position;
        if(posExpression instanceof Entity entity) {
            position = entity.getPos();
        } else if(posExpression instanceof Vec3d vector) {
            position = vector;
        } else {
            throw new ScarletError("Could not extract position from evaluated %s (from Expression '%s')!".formatted(posExpression.getClass(), locationExpression.getClass()));
        }

        Entity created = type.create(world);
        if(created != null) {
            created.updatePosition(position.getX(), position.getY(), position.getZ());
            world.spawnEntity(created);
            return created;
        } else {
            return null;
        }
    }
}
