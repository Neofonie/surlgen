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

import de.neofonie.surlgen.urlmapping.mapping.Mapping;
import de.neofonie.surlgen.urlmapping.mapping.MappingConfig;
import de.neofonie.surlgen.urlmapping.tree.MappingTree;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MappingPatternTest {
    @Test
    public void testMapping_Simple() throws Exception {
        final MappingConfig mappingConfig = new MappingConfig();

        final Mapping mapping = EasyMock.createMock(Mapping.class);

        mappingConfig.put("int", mapping);

        final UrlPattern urlPattern = UrlMappingParser.parse(mappingConfig, "{foo:int}");
        assertNotNull(urlPattern);
        assertEquals("Mapping{name='foo', type='int'}",
                urlPattern.toString());

        EasyMock.expect(mapping.getMatches("fooo/asdf")).andReturn(Collections.emptyList());
        EasyMock.expect(mapping.getMatches("fooo")).andReturn(Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo", "12")
        ));
        EasyMock.expect(mapping.getMatches("fooo/bar")).andReturn(Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo", "12"),
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo/bar", "13")
        ));
        EasyMock.expect(mapping.getMatches("fooo/bar-asdf")).andReturn(Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo/bar-asdf", "12"),
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo/bar", "13")
        ));
//        EasyMock.expect(mapping.getMatches("/fooo/asdf")).andReturn(Arrays.asList(new AbstractMap.SimpleImmutableEntry<String, String>()));
//        EasyMock.expect(mapping.getMatches("/fooo/asdf")).andReturn(Collections.emptyList());

        final MappingTree<String> mappingTree = createMappingTree(urlPattern);
        assertEquals("Mapping{name='foo', type='int'}<SUCCESS>\n", mappingTree.toStringHierarchy());

        EasyMock.replay(mapping);
        assertEquals(mappingTree.matches("fooo/asdf"), null);
        assertEquals(mappingTree.matches("fooo"), "SUCCESS");
        //TODO: YES - GREEDY
        assertEquals(mappingTree.matches("fooo/bar"), null);
        assertEquals(mappingTree.matches("fooo/bar-asdf"), "SUCCESS");
        EasyMock.verify(mapping);
    }

    private static MappingTree<String> createMappingTree(UrlPattern urlPattern) {
        MappingTree<String> mappingTree = new MappingTree<>();
        mappingTree.addEntry(urlPattern, "SUCCESS");
        return mappingTree;
    }
}