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

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class UrlMappingPatternFilterTest {

    private final UrlMappingFilter urlMappingFilter = new UrlMappingFilter();
    private final UrlMappingService urlMappingService = EasyMock.createMock(UrlMappingService.class);
    private final ServletResponse response = new MockHttpServletResponse();
    private final FilterChain filterChain = EasyMock.createMock(FilterChain.class);
    private final HttpServletRequest httpServletRequest = EasyMock.createMock(HttpServletRequest.class);

    @Before
    public void setUp() throws Exception {
        urlMappingFilter.setUrlMappingService(urlMappingService);
        EasyMock.reset(urlMappingService, filterChain, httpServletRequest);
    }

    @Test
    public void testDoFilter_NoMapping() throws Exception {
        EasyMock.expect(httpServletRequest.getRequestURI()).andReturn("/requestUri");

        filterChain.doFilter(httpServletRequest, response);
        EasyMock.expect(urlMappingService.resolve("/requestUri")).andReturn(null);

        EasyMock.replay(urlMappingService, filterChain, httpServletRequest);
        urlMappingFilter.doFilter(httpServletRequest, response, filterChain);
        EasyMock.verify(urlMappingService, filterChain, httpServletRequest);
    }

    @Test
    public void testDoFilter_Mapping() throws Exception {

        EasyMock.expect(httpServletRequest.getRequestURI()).andReturn("/requestUri");
        RequestDispatcher requestDispatcher = EasyMock.createMock(RequestDispatcher.class);
        EasyMock.expect(urlMappingService.resolve("/requestUri")).andReturn("/newMapping");
        EasyMock.expect(httpServletRequest.getRequestDispatcher("/newMapping")).andReturn(requestDispatcher);
        requestDispatcher.include(httpServletRequest, response);

        EasyMock.replay(urlMappingService, filterChain, httpServletRequest, requestDispatcher);
        urlMappingFilter.doFilter(httpServletRequest, response, filterChain);
        EasyMock.verify(urlMappingService, filterChain, httpServletRequest, requestDispatcher);
    }
}