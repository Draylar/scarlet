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
package dev.draylar.scarlet;

import dev.draylar.scarlet.language.ScarletInterpreter;
import dev.draylar.scarlet.language.ScarletLexer;
import dev.draylar.scarlet.language.ScarletParser;
import dev.draylar.scarlet.language.expression.minecraft.ClosestEntityExpression;
import dev.draylar.scarlet.language.expression.minecraft.event.*;
import dev.draylar.scarlet.language.expression.syntax.CountEntitySyntaxExpression;
import dev.draylar.scarlet.language.statement.Statement;
import dev.draylar.scarlet.minecraft.api.ScarletEventManager;
import dev.draylar.scarlet.minecraft.command.ScarletCommand;
import dev.draylar.scarlet.minecraft.data.ScarletScriptLoader;
import dev.draylar.scarlet.minecraft.event.ScarletGameEvent;
import dev.draylar.scarlet.minecraft.event.impl.ScarletInteractionEvents;
import dev.draylar.scarlet.minecraft.event.impl.ScarletItemEvents;
import dev.draylar.scarlet.minecraft.network.ServerNetworking;
import dev.draylar.scarlet.script.ScarletScript;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Scarlet implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Scarlet");
    public static final List<ScarletScript> ACTIVE = new ArrayList<>();
    public static MinecraftServer SERVER_CONTEXT = null;
    public static ScarletEventManager listeners = new ScarletEventManager();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(new ScarletCommand());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ScarletScriptLoader());
        ServerNetworking.initialize();

        ScarletLanguage.registerEvent(new ScarletLanguageEntityTickEvent());
        ScarletLanguage.registerEvent(new ScarletLanguageProjectileLandEvent());
        ScarletLanguage.registerEvent(new ScarletLanguageCommandEvent());
        ScarletLanguage.registerEvent(new ScarletLanguageInteractEvent());
        ScarletLanguage.registerEvent(new ScarletLanguageUseEvent());
        ScarletLanguage.registerEvent(new ScarletLanguageDeathEvent());
        ScarletLanguage.registerEvent(new ScarletLanguageJoinEvent());

        ScarletLanguage.registerExpression(new CountEntitySyntaxExpression());
        ScarletLanguage.registerExpression(new ClosestEntityExpression());

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER_CONTEXT = server;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if(!world.isClient && hand == Hand.MAIN_HAND) {
                ScarletInteractionEvents.INTERACTION.invoker().interact(player, world, entity);
            }

            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            if(!world.isClient) {
                ScarletItemEvents.USE_ITEM.invoker().run(player, world, stack);
            }

            return TypedActionResult.pass(stack);
        });
    }

    public static Identifier id(String name) {
        return new Identifier("scarlet", name);
    }

    public static void reload(MinecraftServer server, ServerPlayerEntity trigger) {
        // Clear Scarlet event listeners
        for (ScarletGameEvent<?> event : ScarletGameEvent.getAllEvents()) {
            event.reload();
        }

        // Re-load Scarlet Scripts
        for (ScarletScript script : ACTIVE) {
            try {
                ScarletLexer tokenizer = new ScarletLexer(script.contents());
                ScarletParser parser = new ScarletParser(tokenizer.parseTokens(), trigger);
                ScarletInterpreter interpreter = new ScarletInterpreter(server.getOverworld());
                List<Statement> statements = parser.parse();
                interpreter.evaluate(statements);
            } catch (Throwable any) {
                System.out.printf(any.getMessage());

                if(trigger != null) {
                    trigger.sendMessage(LiteralText.EMPTY, false);
//                    LiteralText error = new LiteralText(String.format("%sScarlet error: %sencountered an error while loading script %s:", Formatting.RED, Formatting.RED, script.id()));
//                    trigger.sendMessage(error, false);
//                    trigger.sendMessage(new LiteralText(message.replace("\r", "")), false);

//                    LiteralText error = new LiteralText("Scarlet Error: %s".formatted(message))
                }
            }
        }
    }
}

