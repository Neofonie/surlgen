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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-test-config.xml"})
public class UrlFactoryServiceGeneratorTest {

    @Autowired
    private HelloWorldControllerUrlFactoryGenerated helloWorldControllerUrlFactoryGenerated;
    @Autowired
    private HelloWorldController2UrlFactoryGenerated helloWorldController2UrlFactoryGenerated;

    @Before
    public void setUp() throws Exception {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @After
    public void tearDown() throws Exception {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void testUrl() throws Exception {
        assertNotNull("No TestExampleControllerUrlFactory found", helloWorldControllerUrlFactoryGenerated);
        assertEquals("http://localhost/", helloWorldControllerUrlFactoryGenerated.indexUriString());
        assertEquals("http://localhost/foo", helloWorldControllerUrlFactoryGenerated.fooUriString());
        assertEquals("http://localhost/fooiii?pp=1&a=2&b=bla&c=3&d=4",
                helloWorldControllerUrlFactoryGenerated.fooUriString(1L, 2L, "bla", 3, 4L));
        assertEquals("http://localhost/fooB?g=1&h=2&i=3&message=bla",
                helloWorldControllerUrlFactoryGenerated.fooUriString(1L, 2L, 3, "bla"));
        assertEquals("http://localhost/hotels/%7Bhotel%7D/bookings/ownerId",
                helloWorldController2UrlFactoryGenerated.getBookingUriString("ownerId"));
        assertEquals("http://localhost/hotels/ownerId/pets/petId",
                helloWorldController2UrlFactoryGenerated.findPetUriString("ownerId", "petId"));
        assertEquals("http://localhost/hotels/%7Bhotel%7D/pets/petId",
                helloWorldController2UrlFactoryGenerated.findPet2UriString("petId"));
    }

    @Test
    public void testModelAttribute() throws Exception {

        assertEquals("http://localhost/doWithModel", helloWorldControllerUrlFactoryGenerated.doWithModelUriString(null));
        HelloWorldCommand command = new HelloWorldCommand();
        assertEquals("http://localhost/doWithModel?id=0&halla=halla", helloWorldControllerUrlFactoryGenerated.doWithModelUriString(command));
        command.setId(25);
        assertEquals("http://localhost/doWithModel?id=25&halla=halla", helloWorldControllerUrlFactoryGenerated.doWithModelUriString(command));
        command.setCaption("foobar");
        assertEquals("http://localhost/doWithModel?id=25&caption=foobar&halla=halla", helloWorldControllerUrlFactoryGenerated.doWithModelUriString(command));

        Account account = new Account();
        account.setValue(123);

        assertEquals("http://localhost/accounts/%7Baccount%7D?value=123",
                helloWorldControllerUrlFactoryGenerated.saveUriString(account));
        assertEquals("http://localhost/accounts/%7Baccount%7D",
                MvcUriComponentsBuilder.fromMethodName(HelloWorldController.class, "save", new Object[]{account}).toUriString());

        assertEquals("http://localhost/hotels/%7Bhotel%7D/pets/petId",
                MvcUriComponentsBuilder.fromMethodName(HelloWorldController2.class, "findPet2", new Object[]{"petId", 2}).toUriString());
    }

    @Test
    public void testCollections() throws Exception {

        HelloWorldCommand command = new HelloWorldCommand();
        command.setFooo(Arrays.asList("foo", "bar"));
        assertEquals("http://localhost/doWithModel?id=0&fooo=foo&fooo=bar&halla=halla",
                helloWorldControllerUrlFactoryGenerated.doWithModelUriString(command));

        UriComponentsBuilder doWithModel = MvcUriComponentsBuilder.fromMethodName(HelloWorldController.class, "doWithModel", new Object[]{command, null});
        assertEquals("http://localhost/doWithModel", doWithModel.toUriString());
        doWithModel.queryParam("fooo", "bar", "blub");
        assertEquals("http://localhost/doWithModel?fooo=bar&fooo=blub", doWithModel.toUriString());

        assertEquals("http://localhost/collection?stringList=a&stringList=b",
                helloWorldControllerUrlFactoryGenerated.collectionUriString(Arrays.asList("a", "b")));

    }

    @Test
    public void testArrays() throws Exception {

        HelloWorldCommand command = new HelloWorldCommand();
        command.setStringArray(new String[]{"oink", "bla"});
        assertEquals("http://localhost/doWithModel?id=0&stringArray=oink&stringArray=bla&halla=halla",
                helloWorldControllerUrlFactoryGenerated.doWithModelUriString(command));
    }

    @Test
    public void testDate() throws Exception {

//        assertEquals("http://localhost/date?date=01.01.11",
//                helloWorldControllerUrlFactoryGenerated.doWithDateUriString(new Date(2011, 1, 1)));

    }
}