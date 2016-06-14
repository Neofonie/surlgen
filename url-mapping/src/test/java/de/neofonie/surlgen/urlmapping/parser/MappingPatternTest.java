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
import de.neofonie.surlgen.urlmapping.mapping.MappingImpl;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.*;

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
        final MappingTree<String> mappingTree = createMappingTree(urlPattern);
        assertEquals("Mapping{name='foo', type='int'}<SUCCESS>\n", mappingTree.toStringHierarchy());

        EasyMock.replay(mapping);
        assertEquals(mappingTree.resolve("fooo/asdf"), null);
        EasyMock.verify(mapping);
        EasyMock.reset(mapping);

        EasyMock.expect(mapping.getMatches("fooo")).andReturn(Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo", "12")
        ));
        EasyMock.replay(mapping);
        assertTrue("Params{params={foo=[12]}}", mappingTree.resolve("fooo"));

        EasyMock.verify(mapping);
        EasyMock.reset(mapping);

        EasyMock.expect(mapping.getMatches("fooo/bar")).andReturn(Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo", "12"),
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo/bar", "13")
        ));
        EasyMock.replay(mapping);
        assertTrue("Params{params={foo=[13]}}", mappingTree.resolve("fooo/bar"));
        EasyMock.verify(mapping);
        EasyMock.reset(mapping);

        EasyMock.expect(mapping.getMatches("fooo/bar-asdf")).andReturn(Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo/bar-asdf", "14"),
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo/bar", "15")
        ));
        EasyMock.replay(mapping);
        assertTrue("Params{params={foo=[14]}}", mappingTree.resolve("fooo/bar-asdf"));
        EasyMock.verify(mapping);
    }

    @Test
    public void testMapping_Complex() throws Exception {
        final MappingConfig mappingConfig = new MappingConfig();
        final Mapping map1 = EasyMock.createMock(Mapping.class);
        final Mapping map2 = EasyMock.createMock(Mapping.class);
        mappingConfig.put("map1", map1);
        mappingConfig.put("map2", map2);

        final UrlPattern urlPattern = UrlMappingParser.parse(mappingConfig, "{key1:map1}[/{key2:map2}]");
        assertNotNull(urlPattern);
        assertEquals("[Mapping{name='key1', type='map1'}, Choice{[StaticUrlPattern{/}, Mapping{name='key2', type='map2'}]}]",
                urlPattern.toString());

        EasyMock.expect(map1.getMatches("fooobb")).andReturn(Collections.emptyList());
        EasyMock.expect(map1.getMatches("fooo")).andReturn(Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo", "1")
        ));
        EasyMock.expect(map1.getMatches("fooo/bar")).andReturn(Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo", "2"),
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo/bar", "3")
        ));
        EasyMock.expect(map1.getMatches("fooo/bar/bar")).andReturn(Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo", "2"),
                new AbstractMap.SimpleImmutableEntry<String, String>("fooo/bar", "3")
        ));
        EasyMock.expect(map2.getMatches("bar")).andReturn(Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<String, String>("", "4"),
                new AbstractMap.SimpleImmutableEntry<String, String>("bar", "5")
        )).atLeastOnce();
        EasyMock.expect(map2.getMatches("bar/bar")).andReturn(Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<String, String>("", "4"),
                new AbstractMap.SimpleImmutableEntry<String, String>("bar", "5")
        ));

        final MappingTree<String> mappingTree = createMappingTree(urlPattern);
        assertEquals("Mapping{name='key1', type='map1'}<SUCCESS>\n" +
                "    StaticUrlPattern{/}Mapping{name='key2', type='map2'}<SUCCESS>\n", mappingTree.toStringHierarchy());

        EasyMock.replay(map1, map2);
        assertEquals(mappingTree.resolve("fooobb"), null);
        assertTrue("Params{params={key1=[1]}}", mappingTree.resolve("fooo"));
        assertTrue("Params{params={key1=[2], key2=[5]}}", mappingTree.resolve("fooo/bar"));
        assertTrue("Params{params={key1=[3], key2=[5]}}", mappingTree.resolve("fooo/bar/bar"));

        EasyMock.verify(map1, map2);
    }

    @Test
    public void testMapping_Complex2() throws Exception {
        final MappingConfig mappingConfig = new MappingConfig();

        Map<String, String> map1 = new HashMap<>();
        map1.put("1", "fooo");
        map1.put("3", "fooo/bar");
        mappingConfig.put("map1", new MappingImpl(map1));

        Map<String, String> map2 = new HashMap<>();
//        map2.put("", "4");
        map2.put("5", "bar");
        mappingConfig.put("map2", new MappingImpl(map2));

        final UrlPattern urlPattern = UrlMappingParser.parse(mappingConfig, "{key1:map1}[/{key2:map2}]");
        assertNotNull(urlPattern);
        assertEquals("[Mapping{name='key1', type='map1'}, Choice{[StaticUrlPattern{/}, Mapping{name='key2', type='map2'}]}]",
                urlPattern.toString());

        final MappingTree<String> mappingTree = createMappingTree(urlPattern);
        assertEquals("Mapping{name='key1', type='map1'}<SUCCESS>\n" +
                "    StaticUrlPattern{/}Mapping{name='key2', type='map2'}<SUCCESS>\n", mappingTree.toStringHierarchy());

        assertEquals(mappingTree.resolve("fooobb"), null);
        assertTrue("Params{params={key1=[1]}}", mappingTree.resolve("fooo"));
        assertTrue("Params{params={key1=[1], key2=[5]}}", mappingTree.resolve("fooo/bar"));
        assertTrue("Params{params={key1=[3], key2=[5]}}", mappingTree.resolve("fooo/bar/bar"));
    }

    public static void assertTrue(String params, MatcherResult<String> pattern) throws ParseException {
        assertEquals("SUCCESS", pattern.getValue());
        assertEquals(params, pattern.getParams().toString());
    }

    private static MappingTree<String> createMappingTree(UrlPattern urlPattern) {
        MappingTree<String> mappingTree = new MappingTree<>();
        mappingTree.addEntry(urlPattern, "SUCCESS");
        return mappingTree;
    }
}