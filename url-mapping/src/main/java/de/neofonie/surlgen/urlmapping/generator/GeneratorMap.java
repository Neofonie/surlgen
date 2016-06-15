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

import de.neofonie.surlgen.urlmapping.UrlRule;
import de.neofonie.surlgen.urlmapping.parser.Matcher;
import de.neofonie.surlgen.urlmapping.parser.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GeneratorMap {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorMap.class);
    //    private final Collection<Generator> generators = new TreeSet<>();
    private final Map<String, Collection<Generator>> map = new HashMap<>();

    public void add(UrlRule value, List<Matcher> matcherList) {
        Generator generator = new Generator(matcherList);
        Collection<Generator> generators = map.computeIfAbsent(value.getInternalUrl(), t -> new TreeSet<Generator>());
        generators.add(generator);
    }

    public String generateUrl(String internalRequestURI, Params params) {
        Set<String> paramNames = params.keySet();

        Collection<Generator> generators = map.get(internalRequestURI);
        if (generators == null) {
            return internalRequestURI + params.createQueryString();
        }
        for (Generator generator : generators) {
            if (generator.containsAllParams(paramNames)) {
                Params copy = params.copy();
                String result = generator.generateUrl(copy);
                if (result != null) {
                    return result;
                }
            }
        }
        return internalRequestURI + params.createQueryString();
    }
}
