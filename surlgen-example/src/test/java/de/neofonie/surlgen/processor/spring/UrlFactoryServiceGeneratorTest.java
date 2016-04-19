package de.neofonie.surlgen.processor.spring;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-test-config.xml"})
public class UrlFactoryServiceGeneratorTest {

    @Autowired
    private HelloWorldControllerUrlFactoryGenerated testExampleControllerUrlFactory;
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
        assertNotNull("No TestExampleControllerUrlFactory found", testExampleControllerUrlFactory);
        assertEquals("http://localhost/", testExampleControllerUrlFactory.indexUriString());
        assertEquals("http://localhost/foo", testExampleControllerUrlFactory.fooUriString());
        assertEquals("http://localhost/fooiii?pp=1&a=2&b=bla&c=3&d=4",
                testExampleControllerUrlFactory.fooUriString(1L, 2L, "bla", 3, 4L));
        assertEquals("http://localhost/fooB?g=1&h=2&i=3&message=bla",
                testExampleControllerUrlFactory.fooUriString(1L, 2L, 3, "bla"));
        assertEquals("http://localhost/hotels/%7Bhotel%7D/bookings/ownerId",
                helloWorldController2UrlFactoryGenerated.getBookingUriString("ownerId"));
        assertEquals("http://localhost/hotels/ownerId/pets/petId",
                helloWorldController2UrlFactoryGenerated.findPetUriString("ownerId", "petId"));
        assertEquals("http://localhost/hotels/%7Bhotel%7D/pets/petId",
                helloWorldController2UrlFactoryGenerated.findPet2UriString("petId"));

        //Not supported
        HelloWorldCommand command = new HelloWorldCommand();
        command.setId(25);
        command.setCaption("foobar");
        assertEquals("http://localhost/doWithModel",
                testExampleControllerUrlFactory.doWithModelUriString());
        assertEquals("http://localhost/doWithModel",
                MvcUriComponentsBuilder.fromMethodName(HelloWorldController.class, "doWithModel", new Object[]{command}).toUriString());

        Account account = new Account();
        account.setValue(123);

        assertEquals("http://localhost/accounts/%7Baccount%7D",
                testExampleControllerUrlFactory.saveUriString());
        assertEquals("http://localhost/accounts/%7Baccount%7D",
                MvcUriComponentsBuilder.fromMethodName(HelloWorldController.class, "save", new Object[]{account}).toUriString());

        assertEquals("http://localhost/hotels/%7Bhotel%7D/pets/petId",
                MvcUriComponentsBuilder.fromMethodName(HelloWorldController2.class, "findPet2", new Object[]{"petId", 2}).toUriString());
    }
}