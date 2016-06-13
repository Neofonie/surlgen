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

        assertTrue(parse.resolve("/fooo/asdf"));
        assertFalse(parse.resolve("/fooo/asdfD"));
        assertTrue(parse.resolve(""));
        assertFalse(parse.resolve("/foo"));
        assertFalse(parse.resolve("/foooo"));
        assertFalse(parse.resolve("/fooO"));
    }

    @Test
    public void testMixed() throws Exception {
        final UrlPattern urlPattern = UrlMappingParser.parse(null, "/fooo[/asdf]");
        final MappingTree<String> parse = (MappingTree) createMappingTree(urlPattern);
        assertEquals("PatternList{list=[StaticUrlPattern{string='/fooo'}, Choice{choice=StaticUrlPattern{string='/asdf'}}]}",
                urlPattern.toString());

        assertTrue(parse.resolve("/fooo/asdf"));
        assertTrue(parse.resolve("/fooo"));
        assertFalse(parse.resolve(""));
        assertFalse(parse.resolve("/fooo/asd"));
        assertFalse(parse.resolve("/fooo/asdf/"));
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

        assertFalse(parse.resolve("/fooo/asdf"));
        assertTrue(parse.resolve("/fooo"));
        assertTrue(parse.resolve("/fooo/fooo"));
        assertTrue(parse.resolve("/fooo/fooo"));
        assertTrue(parse.resolve("/fooo/fooo/asdf"));
        assertTrue(parse.resolve("/fooo/asdf/fooo"));
        assertTrue(parse.resolve("/fooo/asdf/fooo/asdf"));
        assertFalse(parse.resolve("/fooo/asdf2/fooo/asdf"));
        assertFalse(parse.resolve("/fooo/asdf/asdf"));
        assertFalse(parse.resolve("/fooo/asdf/asdf"));
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

        assertTrue(parse.resolve("/a/b/c/d"));
        assertTrue(parse.resolve("/a/b/c"));
        assertTrue(parse.resolve("/a/b/d"));
        assertTrue(parse.resolve("/a/b"));
        assertTrue(parse.resolve("/a/c/d"));
        assertTrue(parse.resolve("/a/c"));
        assertTrue(parse.resolve("/a/d"));
        assertTrue(parse.resolve("/a"));
        assertTrue(parse.resolve("/b/c/d"));
        assertTrue(parse.resolve("/b/c"));
        assertTrue(parse.resolve("/b/d"));
        assertTrue(parse.resolve("/b"));
        assertTrue(parse.resolve("/c/d"));
        assertTrue(parse.resolve("/c"));
        assertTrue(parse.resolve("/d"));
        assertTrue(parse.resolve(""));
    }

    @Test
    public void testGreedy() throws Exception {
        final UrlPattern urlPattern = UrlMappingParser.parse(null, "[/a/b[/f]][/a[/b/c]]");
        final MappingTree<String> parse = createMappingTree(urlPattern);
        assertEquals("PatternList{list=[Choice{choice=PatternList{list=[StaticUrlPattern{string='/a/b'}, Choice{choice=StaticUrlPattern{string='/f'}}]}}, Choice{choice=PatternList{list=[StaticUrlPattern{string='/a'}, Choice{choice=StaticUrlPattern{string='/b/c'}}]}}]}",
                urlPattern.toString());

        assertFalse(parse.resolve("/a/b/c/d"));

        //TODO: IS WRONG!!!
        assertTrue(parse.resolve("/a/b/c"));
        assertTrue(parse.resolve("/a/b"));
        assertFalse(parse.resolve("/a/c"));
        assertTrue(parse.resolve("/a"));
        assertFalse(parse.resolve("/b/c"));
        assertFalse(parse.resolve("/b"));
        assertFalse(parse.resolve("/c"));
        assertTrue(parse.resolve(""));

        assertTrue(parse.resolve("/a/b/a/b/c"));
        assertTrue(parse.resolve("/a/b/a/b/c"));
    }

    @Test
    public void testComplex2() throws Exception {
        final UrlPattern urlPattern = UrlMappingParser.parse(null, "/fooo[/bar[-asdf]]");
        final MappingTree<String> parse = (MappingTree) createMappingTree(urlPattern);
        assertEquals("PatternList{list=[StaticUrlPattern{string='/fooo'}, Choice{choice=PatternList{list=[StaticUrlPattern{string='/bar'}, Choice{choice=StaticUrlPattern{string='-asdf'}}]}}]}",
                urlPattern.toString());

        assertFalse(parse.resolve("/fooo/asdf"));
        assertTrue(parse.resolve("/fooo"));
        assertTrue(parse.resolve("/fooo/bar"));
        assertTrue(parse.resolve("/fooo/bar-asdf"));
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