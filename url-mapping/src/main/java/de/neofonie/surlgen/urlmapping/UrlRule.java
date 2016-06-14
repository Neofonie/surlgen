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
import de.neofonie.surlgen.urlmapping.parser.ParseException;
import de.neofonie.surlgen.urlmapping.parser.UrlMappingParser;
import de.neofonie.surlgen.urlmapping.parser.UrlPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlRule {

    private static final Logger logger = LoggerFactory.getLogger(UrlRule.class);
    private final UrlPattern urlPattern;
    private final String internalUrl;

    public UrlRule(UrlPattern urlPattern, String internalUrl) {
        this.urlPattern = urlPattern;
        this.internalUrl = internalUrl;
    }

    public UrlRule(String urlPattern, MappingConfig mappingConfig, String internalUrl) throws ParseException {
        this.internalUrl = internalUrl;
        this.urlPattern = UrlMappingParser.parse(mappingConfig, urlPattern);
    }

    public UrlPattern getUrlPattern() {
        return urlPattern;
    }

    public String getInternalUrl() {
        return internalUrl;
    }

    @Override
    public String toString() {
        return "UrlRule{" +
                "urlPattern=" + urlPattern +
                ", internalUrl='" + internalUrl + '\'' +
                '}';
    }
}
