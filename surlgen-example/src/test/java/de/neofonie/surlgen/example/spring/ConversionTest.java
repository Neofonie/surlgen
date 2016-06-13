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

package de.neofonie.surlgen.example.spring;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-test-config.xml"})

public class ConversionTest {

    @Autowired
    private ConversionService conversionService;

    @Test
    @Ignore
    public void testLocalDate() throws Exception {
        //TODO: Handle different locales

        LocalDate date = LocalDate.of(2012, 1, 31);
        assertEquals("31.01.12", conversionService.convert(date, String.class));
        assertEquals(date, conversionService.convert("31.01.12", LocalDate.class));

        assertEquals(date, conversionService.convert(conversionService.convert(date, String.class), LocalDate.class));
    }

    @Test
    public void testLong() {
        assertEquals(Long.valueOf(123L), conversionService.convert("123", Long.class));
        assertEquals("123", conversionService.convert(123, String.class));
    }

    @Test
    @Ignore
    public void testDateConvert() {
        //TODO: Handle different locales

        Date date = new Date(12313123L);
        assertEquals("Thu Jan 01 04:25:13 CET 1970", conversionService.convert(date, String.class));
        try {
            //Doesnt work
            assertEquals(date, conversionService.convert("Thu Jan 01 04:25:13 CET 1970", Date.class));
            fail();
        } catch (ConversionFailedException e) {
        }
    }
}