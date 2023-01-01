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
import dev.draylar.scarlet.language.ScarletParser;
import dev.draylar.scarlet.language.ScarletLexer;
import dev.draylar.scarlet.language.statement.Statement;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ScopeTests {

    @Test
    public void event_scope_ordered() {
        ScarletLexer tokenizer = new ScarletLexer("""
                let foo = 0
                
                on test_event test_argument_1 {
                    foo = foo + 1
                    print foo
                }
                """);

        ScarletParser parser = new ScarletParser(tokenizer.parseTokens());
        ScarletInterpreter interpreter = new ScarletInterpreter();
        List<Statement> statements = parser.parse();
        interpreter.evaluate(statements);
    }

    @Test
    public void scope_print_outputs() {
        ScarletLexer tokenizer = new ScarletLexer("""
                let foo = 0
                                
                {
                    foo = foo + 1
                    print foo
                }
                """);

        ScarletParser parser = new ScarletParser(tokenizer.parseTokens());
        ScarletInterpreter interpreter = new ScarletInterpreter();
        List<Statement> statements = parser.parse();
        interpreter.evaluate(statements);
    }

    @Test
    public void scope_doubleNested() {
        ScarletLexer tokenizer = new ScarletLexer("""
                let foo = 0
                print foo
                                
                {
                  // First block
                  print foo
                  let foo = 3
                  print foo
                  
                  {
                    // Second block
                    print foo
                    let foo = 5
                    print foo
                  }
                }
                
                print foo
                """);

        ScarletParser parser = new ScarletParser(tokenizer.parseTokens());
        ScarletInterpreter interpreter = new ScarletInterpreter();
        List<Statement> statements = parser.parse();
        interpreter.evaluate(statements);
    }
}
