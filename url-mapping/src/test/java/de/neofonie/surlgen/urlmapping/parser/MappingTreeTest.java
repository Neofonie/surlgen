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

public class MappingTreeTest {

    @Test
    public void testSimple() throws Exception {
        MappingTree<String> mappingTree = new MappingTree<>();

        mappingTree.addEntry(UrlMappingParser.parse(null, "/fooo"), "A");
        assertEquals("StaticUrlPattern{/fooo}<A>\n", mappingTree.toStringHierarchy());

        mappingTree.addEntry(UrlMappingParser.parse(null, "/fooo/bar"), "B");
        assertEquals("StaticUrlPattern{/fooo}<A>\n" +
                "StaticUrlPattern{/fooo/bar}<B>\n", mappingTree.toStringHierarchy());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicate() throws Exception {
        MappingTree<String> mappingTree = new MappingTree<>();
        mappingTree.addEntry(UrlMappingParser.parse(null, "/fooo"), "A");
        mappingTree.addEntry(UrlMappingParser.parse(null, "[/fooo]"), "C");
    }

    @Test
    public void testChoice() throws Exception {
        MappingTree<String> mappingTree = new MappingTree<>();

        mappingTree.addEntry(UrlMappingParser.parse(null, "/fooo[/bar]"), "C");
//        assertEquals("StaticUrlPattern{/fooo}<C>\n" +
//                "    StaticUrlPattern{/bar}<C>\n", mappingTree.toStringHierarchy());

        mappingTree.addEntry(UrlMappingParser.parse(null, "[/a][/b][/c][/d]"), "C");
        assertEquals("<C>\n" +
                "StaticUrlPattern{/fooo}<C>\n" +
                "    StaticUrlPattern{/bar}<C>\n" +
                "StaticUrlPattern{/a}<C>\n" +
                "    StaticUrlPattern{/b}<C>\n" +
                "        StaticUrlPattern{/c}<C>\n" +
                "            StaticUrlPattern{/d}<C>\n" +
                "        StaticUrlPattern{/d}<C>\n" +
                "    StaticUrlPattern{/c}<C>\n" +
                "        StaticUrlPattern{/d}<C>\n" +
                "    StaticUrlPattern{/d}<C>\n" +
                "StaticUrlPattern{/b}<C>\n" +
                "    StaticUrlPattern{/c}<C>\n" +
                "        StaticUrlPattern{/d}<C>\n" +
                "    StaticUrlPattern{/d}<C>\n" +
                "StaticUrlPattern{/c}<C>\n" +
                "    StaticUrlPattern{/d}<C>\n" +
                "StaticUrlPattern{/d}<C>\n", mappingTree.toStringHierarchy());
        mappingTree.resolve("asdfas");
    }
}