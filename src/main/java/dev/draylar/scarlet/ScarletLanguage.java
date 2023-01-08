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

import dev.draylar.scarlet.language.expression.minecraft.KillExpression;
import dev.draylar.scarlet.language.expression.minecraft.SummonExpression;
import dev.draylar.scarlet.language.expression.minecraft.event.ScarletLanguageDebugSyntaxExpression;
import dev.draylar.scarlet.language.expression.minecraft.event.ScarletLanguageDebugTestEvent;
import dev.draylar.scarlet.language.expression.minecraft.event.ScarletLanguageServerTickEvent;
import dev.draylar.scarlet.language.expression.syntax.ScarletPatternExpression;
import dev.draylar.scarlet.language.expression.syntax.SizeSyntaxExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScarletLanguage {

    public static final List<ScarletPatternExpression> EVENTS = new ArrayList<>(Arrays.asList(
            new ScarletLanguageDebugTestEvent(),
            new ScarletLanguageServerTickEvent()
    ));

    public static final List<ScarletPatternExpression> PATTERN_EXPRESSIONS = new ArrayList<>(Arrays.asList(
            new ScarletLanguageDebugSyntaxExpression(),
            new SizeSyntaxExpression(),
            new KillExpression(),
            new SummonExpression()
    ));

    public static void registerEvent(ScarletPatternExpression event) {
        EVENTS.add(event);
    }

    public static void registerExpression(ScarletPatternExpression event) {
        PATTERN_EXPRESSIONS.add(event);
    }
}
