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

package de.neofonie.surlgen.urlmapping.generator;

import de.neofonie.surlgen.urlmapping.ActionEnum;
import de.neofonie.surlgen.urlmapping.UrlRule;
import de.neofonie.surlgen.urlmapping.mapping.MappingConfig;
import de.neofonie.surlgen.urlmapping.mapping.MappingConfigTest;
import de.neofonie.surlgen.urlmapping.parser.Matcher;
import de.neofonie.surlgen.urlmapping.parser.Params;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class GeneratorMapTest {

    @Test
    public void testGenerateUrl() throws Exception {
        GeneratorMap generatorMap = new GeneratorMap();

        final MappingConfig mappingConfig = MappingConfigTest.createTestMappingConfig();

        addRule(generatorMap, new UrlRule("/asaa", mappingConfig, ActionEnum.FORWARD, "/intern"));
        addRule(generatorMap, new UrlRule("/{key1:map1}", mappingConfig, ActionEnum.FORWARD, "/intern"));

        assertEquals("/asaa", generatorMap.generateUrl("/intern", new Params()));
        assertEquals("/foo", generatorMap.generateUrl("/intern", new Params().add("key1", "1")));
        assertEquals("/foo?foo=bar", generatorMap.generateUrl("/intern", new Params().add("key1", "1").add("foo", "bar")));
        assertEquals("/asaa?key1=4", generatorMap.generateUrl("/intern", new Params().add("key1", "4")));
        assertEquals("/fooo?key1=4", generatorMap.generateUrl("/fooo", new Params().add("key1", "4")));
    }

    private void addRule(GeneratorMap generatorMap, UrlRule urlRule) {
        final List<List<Matcher>> completeHierarchy = urlRule.getUrlPattern().getCompleteHierarchy();
        for (List<Matcher> matcherList : completeHierarchy) {
            generatorMap.add(urlRule, matcherList);
        }
    }
}