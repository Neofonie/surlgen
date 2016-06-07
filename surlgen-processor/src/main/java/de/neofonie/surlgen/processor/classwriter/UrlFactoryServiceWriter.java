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

package de.neofonie.surlgen.processor.classwriter;

import com.helger.jcodemodel.*;
import de.neofonie.surlgen.processor.core.Options;
import de.neofonie.surlgen.processor.core.UrlMethod;
import de.neofonie.surlgen.processor.spring.UrlFactoryServiceGenerator;
import de.neofonie.surlgen.processor.util.NamedInstances;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.lang.model.element.ExecutableElement;
import java.util.function.Function;

public class UrlFactoryServiceWriter extends ClassWriter {

    private static final NamedInstances<UrlFactoryServiceWriter> map = new NamedInstances<>(new Function<String, UrlFactoryServiceWriter>() {
        @Override
        public UrlFactoryServiceWriter apply(String t) {
            return createHelper(t);
        }
    });

    private final JMethod baseMvcUriComponentsMethod;
    private final String name;

    private UrlFactoryServiceWriter(String name, JDefinedClass definedClass) {
        super(definedClass);
        this.name = name;
        baseMvcUriComponentsMethod = appendBaseMvcUriComponentsBuilderMethod();

    }

    public static UrlFactoryServiceWriter create(String name) {
        return map.getInstance(name);
    }

    private static UrlFactoryServiceWriter createHelper(String name) {
        try {
            final JDefinedClass definedClass = ClassWriter.createClass(name + Options.getValue(Options.OptionEnum.ServiceClassName));
            definedClass.annotate(Service.class);
            definedClass.javadoc().add("Generated with " + UrlFactoryServiceGenerator.class.getCanonicalName());

            return new UrlFactoryServiceWriter(name, definedClass);
        } catch (JClassAlreadyExistsException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private JMethod appendBaseMvcUriComponentsBuilderMethod() {
        try {
            MvcUriComponentsBuilder.class.getMethod("fromMethodName", UriComponentsBuilder.class, Class.class, String.class, Object[].class);

            JMethod getBaseMvcUriComponentsBuilder = definedClass.method(JMod.PROTECTED, UriComponentsBuilder.class, "getBaseUriComponentsBuilder");
            getBaseMvcUriComponentsBuilder.body()._return(JExpr._null());
            getBaseMvcUriComponentsBuilder.javadoc().add("Extension-Point to use another Base-UriComponentsBuilder than the default one");
            return getBaseMvcUriComponentsBuilder;
        } catch (NoSuchMethodException e) {
// prior to Spring 4.2
            return null;
        }
    }

    public void appendMethod(ExecutableElement method) {
        UrlMethod parameters = new UrlMethod(method);

        JMethod mvcUriComponentsBuilderMethod = appendMvcUriComponentsBuilderMethod(parameters.getMethodName(), parameters);
        appendUriStringMethod(parameters.getMethodName(), parameters, mvcUriComponentsBuilderMethod);
    }

    private void appendUriStringMethod(String methodName, UrlMethod parameters, JMethod mvcUriComponentsBuilderMethod) {
        JMethod uriStringMethod = definedClass.method(JMod.PUBLIC, String.class, methodName + "UriString");

        JInvocation invocation = JExpr.invoke(mvcUriComponentsBuilderMethod);
        uriStringMethod.body()._return(invocation.invoke("toUriString"));
        parameters.appendParams(uriStringMethod, invocation);
    }

    private JMethod appendMvcUriComponentsBuilderMethod(String methodName, UrlMethod parameters) {
        JMethod urlMethod = definedClass.method(JMod.PUBLIC, UriComponentsBuilder.class, methodName);
        JBlock body = urlMethod.body();
        JArray varArgArray = JExpr.newArray(ClassWriter.parseType("Object"));
        JVar uriComponentsBuilder = body.decl(ClassWriter.ref(UriComponentsBuilder.class), "uriComponentsBuilder")
                .init(createMvcUriComponentsBuilderInvocation(methodName, varArgArray));

        parameters.handleUriComponentsInvocation(urlMethod, varArgArray, uriComponentsBuilder);

        body._return(uriComponentsBuilder);
        return urlMethod;
    }

    private JInvocation createMvcUriComponentsBuilderInvocation(String methodName, JArray varArgArray) {
        AbstractJClass mvcUriComponentsBuilder = ClassWriter.ref(MvcUriComponentsBuilder.class);
        JInvocation fromMethodName = mvcUriComponentsBuilder.staticInvoke("fromMethodName");
        if (baseMvcUriComponentsMethod != null) {
            fromMethodName.arg(JExpr.invoke(baseMvcUriComponentsMethod));
        }
        fromMethodName.arg(ClassWriter.ref(name).dotclass());
        fromMethodName.arg(methodName);
        fromMethodName.arg(varArgArray);
        return fromMethodName;
    }
}
