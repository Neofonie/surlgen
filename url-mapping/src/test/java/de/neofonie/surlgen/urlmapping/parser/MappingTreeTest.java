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

import de.neofonie.surlgen.urlmapping.ActionEnum;
import de.neofonie.surlgen.urlmapping.UrlRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MappingTreeTest {

    @Test
    public void testSimple() throws Exception {
        MappingTree<UrlRule> mappingTree = new MappingTree<>();

        final UrlRule A = new UrlRule(UrlMappingParser.parse(null, "/fooo"), "A", ActionEnum.FORWARD);
        final UrlRule B = new UrlRule(UrlMappingParser.parse(null, "/fooo"), "B", ActionEnum.FORWARD);
        final UrlRule C = new UrlRule(UrlMappingParser.parse(null, "/fooo"), "C", ActionEnum.FORWARD);
        mappingTree.addEntry(UrlMappingParser.parse(null, "/fooo"), A);
        assertEquals("StaticUrlPattern{/fooo}<UrlRule{urlPattern=StaticUrlPattern{/fooo}, internalUrl='A'}>\n", mappingTree.toStringHierarchy());

        mappingTree.addEntry(UrlMappingParser.parse(null, "/fooo/bar"), B);
        assertEquals("StaticUrlPattern{/fooo}<UrlRule{urlPattern=StaticUrlPattern{/fooo}, internalUrl='A'}>\n" +
                "StaticUrlPattern{/fooo/bar}<UrlRule{urlPattern=StaticUrlPattern{/fooo}, internalUrl='B'}>\n", mappingTree.toStringHierarchy());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicate() throws Exception {
        final UrlRule A = new UrlRule(UrlMappingParser.parse(null, "/fooo"), "A", ActionEnum.FORWARD);
        final UrlRule B = new UrlRule(UrlMappingParser.parse(null, "/fooo"), "B", ActionEnum.FORWARD);
        final UrlRule C = new UrlRule(UrlMappingParser.parse(null, "/fooo"), "C", ActionEnum.FORWARD);

        MappingTree<UrlRule> mappingTree = new MappingTree<>();
        mappingTree.addEntry(UrlMappingParser.parse(null, "/fooo"), A);
        mappingTree.addEntry(UrlMappingParser.parse(null, "[/fooo]"), C);
    }

    @Test
    public void testChoice() throws Exception {
        final UrlRule A = new UrlRule(UrlMappingParser.parse(null, "/fooo"), "A", ActionEnum.FORWARD);
        final UrlRule B = new UrlRule(UrlMappingParser.parse(null, "/fooo"), "B", ActionEnum.FORWARD);
        final UrlRule C = new UrlRule(UrlMappingParser.parse(null, "/fooo"), "C", ActionEnum.FORWARD);

        MappingTree<UrlRule> mappingTree = new MappingTree<>();

        mappingTree.addEntry(UrlMappingParser.parse(null, "/fooo[/bar]"), C);
//        assertEquals("StaticUrlPattern{/fooo}<C>\n" +
//                "    StaticUrlPattern{/bar}<C>\n", mappingTree.toStringHierarchy());

        mappingTree.addEntry(UrlMappingParser.parse(null, "[/a][/b][/c][/d]"), C);
        assertEquals(("<C>\n" +
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
                        "StaticUrlPattern{/d}<C>\n")
                        .replace("<C>", "<UrlRule{urlPattern=StaticUrlPattern{/fooo}, internalUrl='C'}>"),
                mappingTree.toStringHierarchy());
        mappingTree.resolve("asdfas");
    }
}