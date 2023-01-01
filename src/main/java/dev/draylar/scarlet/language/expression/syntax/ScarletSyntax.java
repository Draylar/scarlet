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
import dev.draylar.scarlet.language.TokenType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows for the construction of non-native language statements based off pattern matching.
 */
public record ScarletSyntax(Fragment... fragments) {

    public FragmentMatchResult match(ParserAccess queue) {
        for (int i = 0; i < fragments.length; i++) {
            Fragment fragment = fragments[i];

            queue.active(fragment);

            // Peek ahead at the next Token.
            // If it is not available or the end of the file, it is not possible for us to match - return false.
            Token next = queue.peek();
            if(next == null || next.type() == TokenType.EOF) {
                return FragmentMatchResult.failure();
            }

            // If the next Fragment is an Expression, consume the Expression and continue.
            // If we throw any exception (Expression could not be read), abort so the calling parser can resume.
            if(fragment instanceof ExpressionFragment) {
                try {
                    queue.expression();
                    continue;
                } catch (Throwable any) {
                    return FragmentMatchResult.failure();
                }
            }

            // Check if the next fragment matches, consuming the token if it does.
            FragmentMatchResult result = fragment.match(next);
            if(!result.okay()) {

                // Only consider issues (through messages) when this is not the first token after 'on'.
                if(i != 0) {
                    result.setOffset(i);
                    return result;
                } else {
                    return FragmentMatchResult.failure();
                }
            }

            queue.advance();
        }

        return FragmentMatchResult.success();
    }

    public static class Instance {

        // TODO: cache? Add canCache method to Fragment?
        private final ScarletSyntax pattern;
        private final ParserAccess tokens;
        private final List<Fragment> fragments;
        private final Map<ArgumentFragment<?>, Integer> fragmentByIndex = new HashMap<>();

        public Instance(ScarletSyntax pattern, ParserAccess eventConstructionTokens) {
            this.pattern = pattern;
            this.tokens = eventConstructionTokens;
            this.fragments = Arrays.asList(pattern.fragments);

            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if(fragment instanceof ArgumentFragment<?> argument) {
                    fragmentByIndex.put(argument, i);
                }
            }
        }

        public <T> T get(ArgumentFragment<T> argument) {
            return argument.get(tokens);
        }
    }
}
