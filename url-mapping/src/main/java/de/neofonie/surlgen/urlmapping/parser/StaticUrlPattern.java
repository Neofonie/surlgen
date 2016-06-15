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

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.List;

class StaticUrlPattern implements UrlPattern, Matcher {

    private final String string;

    StaticUrlPattern(String string) {
        Preconditions.checkNotNull(string);
        Preconditions.checkArgument(!string.isEmpty());
        this.string = string;
    }

    @Override
    public String toString() {
        return String.format("StaticUrlPattern{%s}", string);
    }

    @Override
    public List<MatcherProcessingCommand> matches(MatcherProcessingCommand matcherProcessingCommand) {
        final String remaining = matcherProcessingCommand.getString();
        if (!remaining.startsWith(string)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(matcherProcessingCommand.consume(string));
    }

    @Override
    public String getParam() {
        return null;
    }

    @Override
    public String generateUrl(Params params) {
        return string;
    }

    @Override
    public List<List<Matcher>> getCompleteHierarchy() {
        return Collections.singletonList(Collections.singletonList(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StaticUrlPattern that = (StaticUrlPattern) o;

        return string.equals(that.string);

    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }
}
