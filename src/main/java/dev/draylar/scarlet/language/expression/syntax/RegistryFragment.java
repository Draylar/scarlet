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

import dev.draylar.scarlet.language.ParserAccess;
import dev.draylar.scarlet.language.Token;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryFragment<T> extends ArgumentFragment<T> {

    private final Registry<T> registry;

    public RegistryFragment(Registry<T> registry) {
        this.registry = registry;
    }

    public static <T> RegistryFragment<T> of(Registry<T> registry) {
        return new RegistryFragment<>(registry);
    }

    @Override
    public FragmentMatchResult match(Token token) {
        // try/catch for Identifier parsing
        try {
            boolean contains = registry.containsId(new Identifier(token.lexeme()));
            if(contains) {
                return FragmentMatchResult.success();
            }

            return FragmentMatchResult.issue("Value '%s' is not present in registry %s".formatted(token.lexeme(), registry.getKey().toString()));
        } catch (Throwable any) {
            return FragmentMatchResult.issue("Found invalid identifier value '%s'".formatted(token.lexeme()));
        }
    }

    @Override
    public T get(ParserAccess access) {
        if(access.getRead().get(this) instanceof Token token) {
            return registry.get(new Identifier(token.lexeme()));
        }

        return null;
    }
}
