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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.draylar.scarlet.language.Environment;
import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.expression.syntax.ScarletSyntax;
import dev.draylar.scarlet.language.expression.syntax.ScarletPatternExpression;
import dev.draylar.scarlet.language.expression.syntax.LiteralFragment;
import dev.draylar.scarlet.language.expression.syntax.StringArgumentFragment;
import dev.draylar.scarlet.language.statement.BlockStatement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ScarletLanguageCommandEvent extends ScarletPatternExpression {

    public static final Set<LiteralArgumentBuilder<ServerCommandSource>> ALL_REGISTERED_NODES = new HashSet<>();

    private static final StringArgumentFragment COMMAND_ROOT = StringArgumentFragment.of();
    private static final ScarletSyntax PATTERN = new ScarletSyntax(
            LiteralFragment.of("command"),
            COMMAND_ROOT
    );

    @Override
    public ScarletSyntax getPattern() {
        return PATTERN;
    }

    @Override
    public void reload(MinecraftServer server) {
        for (LiteralArgumentBuilder<ServerCommandSource> node : ALL_REGISTERED_NODES) {
            // Reload commands
        }
    }

    @Override
    public Object apply(ScarletSyntax.Instance syntax, ScarletInterpreter interpreter, BlockStatement block) {
        if(interpreter.getWorld().isClient || interpreter.getWorld().getServer() == null) {
            return false;
        }

        String root = syntax.get(COMMAND_ROOT).replace("/", "");
        upsertCommand(interpreter.getWorld().getServer(), () -> CommandManager.literal(root).executes(context -> {
            try {
                Environment environment = new Environment(interpreter.getEnvironment());
                environment.setImplicitPosition(context.getSource().getPosition());
                interpreter.setEnvironment(environment);
                block.evaluate(interpreter);
                interpreter.setEnvironment(environment);
            } catch (Throwable any) {
                any.printStackTrace();
            }

            return 1;
        }));

        return true;
    }

    public static void upsertCommand(MinecraftServer server, Supplier<LiteralArgumentBuilder<ServerCommandSource>> nodeSupplier) {
        LiteralArgumentBuilder<ServerCommandSource> root = nodeSupplier.get();
        ALL_REGISTERED_NODES.add(root);
        server.getCommandManager().getDispatcher().register(root);

        // Update clients
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            server.getCommandManager().sendCommandTree(player);
        }
    }
}
