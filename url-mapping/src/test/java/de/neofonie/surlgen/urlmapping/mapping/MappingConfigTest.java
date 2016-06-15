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

package de.neofonie.surlgen.urlmapping.mapping;

import java.util.HashMap;
import java.util.Map;

public class MappingConfigTest {

    public static MappingConfig createTestMappingConfig() {
        final MappingConfig mappingConfig = new MappingConfig();

        Map<String, String> map1 = new HashMap<>();
        map1.put("1", "foo");
        map1.put("2", "bar");
        map1.put("3", "foobar");
        mappingConfig.put("map1", new MappingImpl(map1));

        Map<String, String> map2 = new HashMap<>();
        map2.put("1", "foo");
        map2.put("2", "bar");
        map2.put("3", "foobar");
        mappingConfig.put("map2", new MappingImpl(map2));
        return mappingConfig;
    }
}