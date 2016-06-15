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

import de.neofonie.surlgen.urlmapping.generator.Generator;
import de.neofonie.surlgen.urlmapping.mapping.MappingConfig;
import de.neofonie.surlgen.urlmapping.mapping.MappingConfigTest;
import de.neofonie.surlgen.urlmapping.mapping.MappingImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class GeneratorTest {

    @Test
    public void testCompareTo() throws Exception {
        final MappingConfig mappingConfig = new MappingConfig();
        mappingConfig.put("map", new MappingImpl(new HashMap<>()));

        MappingPattern a = new MappingPattern("a", "map", mappingConfig);
        MappingPattern b = new MappingPattern("b", "map", mappingConfig);
        Generator generator = new Generator(Arrays.asList(a, a));
        Generator generator2 = new Generator(Arrays.asList(a, b));
        Generator generator3 = new Generator(Arrays.asList(a));
        Generator generator4 = new Generator(Arrays.asList(b));

        ArrayList<Generator> generators = new ArrayList<>(Arrays.asList(generator, generator2, generator3, generator4));
        Collections.sort(generators);
        assertEquals(generator2, generators.get(0));
        //Not defined
//        assertEquals(generator, generators.get(1));
//        assertEquals(generator, generators.get(2));
//        assertEquals(generator, generators.get(3));
//        assertEquals(generator, generators.get(4));
    }

    @Test
    public void testGenerateUrl() throws Exception {
        final MappingConfig mappingConfig = MappingConfigTest.createTestMappingConfig();

        MappingPattern a = new MappingPattern("a", "map1", mappingConfig);
        MappingPattern b = new MappingPattern("b", "map2", mappingConfig);
        Generator generator = new Generator(Arrays.asList(a, b));

        assertEquals(null, generator.generateUrl(new Params()));
        assertEquals(null, generator.generateUrl(new Params().add("a", "1")));
        assertEquals("foobar", generator.generateUrl(new Params().add("a", "1").add("b", "2")));
        assertEquals("foobar?b=2", generator.generateUrl(new Params().add("a", "1").add("b", "2").add("b", "2")));
    }
}