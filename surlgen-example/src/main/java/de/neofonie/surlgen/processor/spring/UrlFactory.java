package de.neofonie.surlgen.processor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class UrlFactory {

    @Autowired
    private HelloWorldControllerUrlFactoryGenerated helloWorldControllerUrlFactory;
    @Autowired
    private HelloWorldController2UrlFactoryGenerated helloWorldController2UrlFactoryGenerated;

    @ModelAttribute(value = "urls")
    public List<String> urls() {
        List<String> result = new ArrayList<>();
        result.add(helloWorldControllerUrlFactory.indexUriString());
        result.add(helloWorldControllerUrlFactory.fooUriString());
        result.add(helloWorldControllerUrlFactory.fooUriString(1L, 2L, "bla", 3, 4));
        result.add(helloWorldControllerUrlFactory.fooUriString(1L, 2L, 3, "bla"));
        result.add(helloWorldController2UrlFactoryGenerated.getBookingUriString("foooo"));
        result.add(helloWorldController2UrlFactoryGenerated.findPetUriString("foo", "bar"));
        result.add(helloWorldControllerUrlFactory.doWithDateUriString(new Date()));

        HelloWorldCommand command = new HelloWorldCommand();
        command.setId(25);
        command.setCaption("foobar");
        result.add(MvcUriComponentsBuilder.fromMethodName(HelloWorldController.class, "doWithModel", new Object[]{command}).toUriString());
        return result;
    }
}
