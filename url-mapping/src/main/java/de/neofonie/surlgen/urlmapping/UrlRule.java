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

import com.google.common.base.Preconditions;
import de.neofonie.surlgen.urlmapping.mapping.MappingConfig;
import de.neofonie.surlgen.urlmapping.parser.ParseException;
import de.neofonie.surlgen.urlmapping.parser.UrlMappingParser;
import de.neofonie.surlgen.urlmapping.parser.UrlPattern;

public class UrlRule {

    private final ActionEnum action;
    private final UrlPattern urlPattern;
    private final String internalUrl;

    public UrlRule(UrlPattern urlPattern, String internalUrl, ActionEnum action) {
        Preconditions.checkNotNull(urlPattern);
        Preconditions.checkNotNull(internalUrl);
        Preconditions.checkNotNull(action);
        this.action = action;
        this.urlPattern = urlPattern;
        this.internalUrl = internalUrl;
    }

    public UrlRule(String urlPattern, MappingConfig mappingConfig, ActionEnum action, String internalUrl) throws ParseException {
        Preconditions.checkNotNull(urlPattern);
        Preconditions.checkNotNull(internalUrl);
        Preconditions.checkNotNull(action);
        Preconditions.checkNotNull(mappingConfig);

        this.action = action;
        this.internalUrl = internalUrl;
        this.urlPattern = UrlMappingParser.parse(mappingConfig, urlPattern);
    }

    public UrlPattern getUrlPattern() {
        return urlPattern;
    }

    public String getInternalUrl() {
        return internalUrl;
    }

    public ActionEnum getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "UrlRule{" +
                "urlPattern=" + urlPattern +
                ", internalUrl='" + internalUrl + '\'' +
                '}';
    }
}
