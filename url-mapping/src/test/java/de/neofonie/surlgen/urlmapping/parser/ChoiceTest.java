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

import static org.junit.Assert.assertEquals;

public class ChoiceTest {

    @Test
    public void testCompleteChoice() throws Exception {
        final UrlPattern urlPattern = UrlMappingParser.parse(null, "[/fooo/asdf]");
        assertEquals("Choice{choice=StaticUrlPattern{string='/fooo/asdf'}}", urlPattern.toString());

        final MappingTree<String> parse = createMappingTree(urlPattern);
        assertEquals("<SUCCESS>\n" +
                "StaticUrlPattern{string='/fooo/asdf'}<SUCCESS>\n", parse.toStringHierarchy());

        assertTrue(parse.matches("/fooo/asdf"));
        assertFalse(parse.matches("/fooo/asdfD"));
        assertTrue(parse.matches(""));
        assertFalse(parse.matches("/foo"));
        assertFalse(parse.matches("/foooo"));
        assertFalse(parse.matches("/fooO"));
    }

    @Test
    public void testMixed() throws Exception {
        final UrlPattern urlPattern = UrlMappingParser.parse(null, "/fooo[/asdf]");
        final MappingTree<String> parse = (MappingTree) createMappingTree(urlPattern);
        assertEquals("PatternList{list=[StaticUrlPattern{string='/fooo'}, Choice{choice=StaticUrlPattern{string='/asdf'}}]}",
                urlPattern.toString());

        assertTrue(parse.matches("/fooo/asdf"));
        assertTrue(parse.matches("/fooo"));
        assertFalse(parse.matches(""));
        assertFalse(parse.matches("/fooo/asd"));
        assertFalse(parse.matches("/fooo/asdf/"));
    }

    @Test
    public void testComplex() throws Exception {
        final UrlPattern urlPattern = UrlMappingParser.parse(null, "/fooo[[/asdf]/fooo[/asdf]]");
        final MappingTree<String> parse = (MappingTree) createMappingTree(urlPattern);
        assertEquals("PatternList{list=[" +
                        "StaticUrlPattern{string='/fooo'}, " +
                        "Choice{choice=PatternList{list=[Choice{choice=StaticUrlPattern{string='/asdf'}}, " +
                        "StaticUrlPattern{string='/fooo'}, " +
                        "Choice{choice=StaticUrlPattern{string='/asdf'}}]}}]}",
                urlPattern.toString());

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
        final UrlPattern urlPattern = UrlMappingParser.parse(null, "[/a][/b][/c][/d]");
        final MappingTree<String> parse = (MappingTree) createMappingTree(urlPattern);
        assertEquals("PatternList{list=[Choice{choice=StaticUrlPattern{string='/a'}}, " +
                        "Choice{choice=StaticUrlPattern{string='/b'}}, " +
                        "Choice{choice=StaticUrlPattern{string='/c'}}, " +
                        "Choice{choice=StaticUrlPattern{string='/d'}}]}",
                urlPattern.toString());

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
        final UrlPattern urlPattern = UrlMappingParser.parse(null, "[/a/b[/f]][/a[/b/c]]");
        final MappingTree<String> parse = createMappingTree(urlPattern);
        assertEquals("PatternList{list=[Choice{choice=PatternList{list=[StaticUrlPattern{string='/a/b'}, Choice{choice=StaticUrlPattern{string='/f'}}]}}, Choice{choice=PatternList{list=[StaticUrlPattern{string='/a'}, Choice{choice=StaticUrlPattern{string='/b/c'}}]}}]}",
                urlPattern.toString());

        assertFalse(parse.matches("/a/b/c/d"));

        //TODO: IS WRONG!!!
        assertTrue(parse.matches("/a/b/c"));
        assertTrue(parse.matches("/a/b"));
        assertFalse(parse.matches("/a/c"));
        assertTrue(parse.matches("/a"));
        assertFalse(parse.matches("/b/c"));
        assertFalse(parse.matches("/b"));
        assertFalse(parse.matches("/c"));
        assertTrue(parse.matches(""));

        assertTrue(parse.matches("/a/b/a/b/c"));
        assertTrue(parse.matches("/a/b/a/b/c"));
    }

    @Test
    public void testComplex2() throws Exception {
        final UrlPattern urlPattern = UrlMappingParser.parse(null, "/fooo[/bar[-asdf]]");
        final MappingTree<String> parse = (MappingTree) createMappingTree(urlPattern);
        assertEquals("PatternList{list=[StaticUrlPattern{string='/fooo'}, Choice{choice=PatternList{list=[StaticUrlPattern{string='/bar'}, Choice{choice=StaticUrlPattern{string='-asdf'}}]}}]}",
                urlPattern.toString());

        assertFalse(parse.matches("/fooo/asdf"));
        assertTrue(parse.matches("/fooo"));
        assertTrue(parse.matches("/fooo/bar"));
        assertTrue(parse.matches("/fooo/bar-asdf"));
    }

    public static void assertTrue(MatcherResult<String> pattern) throws ParseException {
        assertEquals("SUCCESS", pattern.getValue());
        assertEquals("Params{params={}}", pattern.getParams().toString());
    }

    public static void assertFalse(MatcherResult<String> pattern) throws ParseException {
        assertEquals(null, pattern);
    }

    private static MappingTree createMappingTree(UrlPattern urlPattern) {
        MappingTree mappingTree = new MappingTree();
        mappingTree.addEntry(urlPattern, "SUCCESS");
        return mappingTree;
    }
}