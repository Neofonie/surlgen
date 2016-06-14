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

package de.neofonie.surlgen.urlmapping;

import de.neofonie.surlgen.urlmapping.mapping.MappingConfig;
import de.neofonie.surlgen.urlmapping.mapping.MappingImpl;
import de.neofonie.surlgen.urlmapping.parser.MatcherResult;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UrlMappingServiceTest {

    @Test
    public void testGetInternalRequestURI() throws Exception {
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

//        mappingConfig.put()
        UrlRule urlRule3 = new UrlRule("/asaa", mappingConfig, "/intern2");
        UrlMappingService urlMappingService = new UrlMappingService(Arrays.asList(
                new UrlRule("/asdf", mappingConfig, "/intern1"),
                new UrlRule("/asaa", mappingConfig, "/intern2"),
                new UrlRule("/foo[/{m:map1}]", mappingConfig, "/intern3"),
                new UrlRule("/foo[/{m:map1}][/{m:map2}]", mappingConfig, "/intern4")
        ));
        assertEquals(null, resolve(urlMappingService, "/unknown"));
        assertEquals("/intern1", resolve(urlMappingService, "/asdf"));
        assertEquals("/intern3?m=1", resolve(urlMappingService, "/foo/foo"));
        assertEquals("/intern3", resolve(urlMappingService, "/foo"));
    }

    private String resolve(UrlMappingService urlMappingService, String string) {
        MatcherResult<UrlRule> resolve = urlMappingService.resolve(string);
        if (resolve == null) {
            return null;
        }

        return resolve.getValue().getInternalUrl() + resolve.getParams().createQueryString();
    }

    @Test
    public void testGetExternalRequestURI() throws Exception {

    }
}