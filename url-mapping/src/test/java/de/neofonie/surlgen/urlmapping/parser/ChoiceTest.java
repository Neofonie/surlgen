/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Neofonie GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.neofonie.surlgen.urlmapping.parser;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChoiceTest {
    @Test
    public void testCompleteChoice() throws Exception {
        final Choice parse = (Choice) UrlMappingParser.parse(null, "[/fooo/asdf]");
        assertEquals("Choice{choice=StaticUrlPattern{string='/fooo/asdf'}}", parse.toString());
        assertTrue(parse.matches("/fooo/asdf"));
        assertFalse(parse.matches("/fooo/asdfD"));
        assertTrue(parse.matches(""));
        assertFalse(parse.matches("/foo"));
        assertFalse(parse.matches("/foooo"));
        assertFalse(parse.matches("/fooO"));
    }

    @Test
    public void testMixed() throws Exception {
        final PatternList parse = (PatternList) UrlMappingParser.parse(null, "/fooo[/asdf]");
        assertEquals("PatternList{list=[StaticUrlPattern{string='/fooo'}, Choice{choice=StaticUrlPattern{string='/asdf'}}]}",
                parse.toString());

        assertTrue(parse.matches("/fooo/asdf"));
        assertTrue(parse.matches("/fooo"));
        assertFalse(parse.matches(""));
        assertFalse(parse.matches("/fooo/asd"));
        assertFalse(parse.matches("/fooo/asdf/"));
    }

    @Test
    public void testComplex() throws Exception {
        final PatternList parse = (PatternList) UrlMappingParser.parse(null, "/fooo[[/asdf]/fooo[/asdf]]");
        assertEquals("PatternList{list=[" +
                        "StaticUrlPattern{string='/fooo'}, " +
                        "Choice{choice=PatternList{list=[Choice{choice=StaticUrlPattern{string='/asdf'}}, " +
                        "StaticUrlPattern{string='/fooo'}, " +
                        "Choice{choice=StaticUrlPattern{string='/asdf'}}]}}]}",
                parse.toString());

        assertFalse(parse.matches("/fooo/asdf"));
        assertTrue(parse.matches("/fooo"));
        assertTrue(parse.matches("/fooo/fooo"));
        assertTrue(parse.matches("/fooo/fooo"));
        assertTrue(parse.matches("/fooo/fooo/asdf"));
        assertTrue(parse.matches("/fooo/asdf/fooo"));
        assertTrue(parse.matches("/fooo/asdf/fooo/asdf"));
        assertFalse(parse.matches("/fooo/asdf2/fooo/asdf"));
        assertFalse(parse.matches("/fooo/asdf/asdf"));
        assertFalse(parse.matches("/fooo/asdf/asdf"));
    }

    @Test
    public void testMultipleChoices() throws Exception {
        final PatternList parse = (PatternList) UrlMappingParser.parse(null, "[/a][/b][/c][/d]");
        assertEquals("PatternList{list=[Choice{choice=StaticUrlPattern{string='/a'}}, " +
                        "Choice{choice=StaticUrlPattern{string='/b'}}, " +
                        "Choice{choice=StaticUrlPattern{string='/c'}}, " +
                        "Choice{choice=StaticUrlPattern{string='/d'}}]}",
                parse.toString());

        assertTrue(parse.matches("/a/b/c/d"));
        assertTrue(parse.matches("/a/b/c"));
        assertTrue(parse.matches("/a/b/d"));
        assertTrue(parse.matches("/a/b"));
        assertTrue(parse.matches("/a/c/d"));
        assertTrue(parse.matches("/a/c"));
        assertTrue(parse.matches("/a/d"));
        assertTrue(parse.matches("/a"));
        assertTrue(parse.matches("/b/c/d"));
        assertTrue(parse.matches("/b/c"));
        assertTrue(parse.matches("/b/d"));
        assertTrue(parse.matches("/b"));
        assertTrue(parse.matches("/c/d"));
        assertTrue(parse.matches("/c"));
        assertTrue(parse.matches("/d"));
        assertTrue(parse.matches(""));
    }

    @Test
    public void testGreedy() throws Exception {
        final PatternList parse = (PatternList) UrlMappingParser.parse(null, "[/a[/b]][/a[/b/c]]");
        assertEquals("PatternList{list=[Choice{choice=PatternList{list=[StaticUrlPattern{string='/a'}, Choice{choice=StaticUrlPattern{string='/b'}}]}}, Choice{choice=PatternList{list=[StaticUrlPattern{string='/a'}, Choice{choice=StaticUrlPattern{string='/b/c'}}]}}]}",
                parse.toString());

        assertFalse(parse.matches("/a/b/c/d"));

        //TODO: IS WRONG!!!
        assertFalse(parse.matches("/a/b/c"));
        assertTrue(parse.matches("/a/b"));
        assertFalse(parse.matches("/a/c"));
        assertTrue(parse.matches("/a"));
        assertFalse(parse.matches("/b/c"));
        assertFalse(parse.matches("/b"));
        assertFalse(parse.matches("/c"));
        assertTrue(parse.matches(""));

        assertTrue(parse.matches("/a/a/b/c"));
        assertTrue(parse.matches("/a/b/a/b/c"));
    }

    @Test
    public void testComplex2() throws Exception {
        final PatternList parse = (PatternList) UrlMappingParser.parse(null, "/fooo[/bar[-asdf]]");
        assertEquals("PatternList{list=[StaticUrlPattern{string='/fooo'}, Choice{choice=PatternList{list=[StaticUrlPattern{string='/bar'}, Choice{choice=StaticUrlPattern{string='-asdf'}}]}}]}",
                parse.toString());

        assertFalse(parse.matches("/fooo/asdf"));
        assertTrue(parse.matches("/fooo"));
        assertTrue(parse.matches("/fooo/bar"));
        assertTrue(parse.matches("/fooo/bar-asdf"));
    }

}