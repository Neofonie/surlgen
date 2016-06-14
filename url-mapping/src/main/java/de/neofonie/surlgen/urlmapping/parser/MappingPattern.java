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

import java.util.*;

class MappingPattern implements Matcher, UrlPattern {

    private final String name;
    private final String type;
    private final Mapping mapping;

    MappingPattern(String name, String type, MappingConfig mappingConfig) {
        this.name = name;
        this.type = type;
        mapping = mappingConfig.getMapping(type);
    }

    @Override
    public List<MatcherProcessingCommand> matches(MatcherProcessingCommand matcherProcessingCommand) {

        final Collection<Map.Entry<String, String>> matches = mapping.getMatches(matcherProcessingCommand.getString());
        if (matches == null) {
            return Collections.emptyList();
        }
        List<MatcherProcessingCommand> result = new ArrayList<>();
        for (Map.Entry<String, String> match : matches) {
            final MatcherProcessingCommand consume = matcherProcessingCommand.consume(match.getKey(), name, match.getValue());
            if (consume != null) {
                result.add(consume);
            }
        }
        return result;
    }

    @Override
    public String getParam() {
        return name;
    }

    @Override
    public List<List<Matcher>> getCompleteHierarchy() {
        return Collections.singletonList(Collections.singletonList(this));
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
