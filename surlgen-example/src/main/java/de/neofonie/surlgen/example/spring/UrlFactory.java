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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.Arrays;
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

        HelloWorldCommand command = new HelloWorldCommand();
        command.setId(25);
        result.add(helloWorldControllerUrlFactory.doWithModelUriString(command));
        command.setCaption("foobar");
//        result.add(MvcUriComponentsBuilder.fromMethodName(HelloWorldController.class, "doWithModel", new Object[]{command}).toUriString());
        result.add(helloWorldControllerUrlFactory.doWithModelUriString(command));
        command.setFooo(Arrays.asList("foo", "bar", "25"));
        result.add(helloWorldControllerUrlFactory.doWithModelUriString(command));

        result.add(helloWorldControllerUrlFactory.doWithDateUriString(new Date(111, 1, 1)));
//        result.add(helloWorldControllerUrlFactory.doWithDateUriString(LocalDate.of(2011, 1, 1)));
//        result.add(helloWorldControllerUrlFactory.doWithDateUriString(LocalDate.now()));
        return result;
    }
}
