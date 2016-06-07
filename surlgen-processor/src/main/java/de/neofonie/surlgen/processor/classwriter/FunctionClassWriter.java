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
import de.neofonie.surlgen.processor.core.CamelCaseUtils;
import de.neofonie.surlgen.processor.core.Options;
import de.neofonie.surlgen.processor.core.UrlMethod;
import de.neofonie.surlgen.processor.spring.UrlFunctionGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionClassWriter extends ClassWriter {

    private static FunctionClassWriter instance = null;
    private final JMethod serviceMethod;
    private final List<JMethod> methods = new ArrayList<>();

    private FunctionClassWriter(JDefinedClass definedClass) {
        super(definedClass);
        serviceMethod = appendGetServiceMethod();
    }

    public static synchronized FunctionClassWriter createFunctionClassWriter() throws JClassAlreadyExistsException {
        if (instance != null) {
            return instance;
        }
        JDefinedClass definedClass = ClassWriter.createClass(Options.getValue(Options.OptionEnum.FunctionClassName));
        definedClass.javadoc().add("Generated with " + UrlFunctionGenerator.class.getCanonicalName());
        instance = new FunctionClassWriter(definedClass);

        return instance;
    }

    private JMethod appendGetServiceMethod() {

//            private<T> T getService(Class<T> beanClass) {
        JMethod getServiceMethod = getDefinedClass().method(JMod.PRIVATE | JMod.STATIC, Class.class, "getService");
        JTypeVar t = getServiceMethod.generify("T");
        getServiceMethod.type(t);

        AbstractJClass beanClass2 = ClassWriter.ref(Class.class).narrow(t);
        JVar beanClass = getServiceMethod.param(beanClass2, "beanClass");

//            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        JBlock body = getServiceMethod.body();
        JVar requestAttributes = body
                .decl(ClassWriter.ref(RequestAttributes.class), "requestAttributes")
                .init(ClassWriter.ref(RequestContextHolder.class).staticInvoke("getRequestAttributes"));

//                HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        JVar servletRequest = body
                .decl(ClassWriter.ref(HttpServletRequest.class), "servletRequest")
                .init(JExpr.cast(ClassWriter.ref(ServletRequestAttributes.class), requestAttributes)
                        .invoke("getRequest"));

//        Object attribute = servletRequest.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        JVar applicationContext = body
                .decl(ClassWriter.ref(ApplicationContext.class), "applicationContext")
                .init(JExpr.cast(ClassWriter.ref(ApplicationContext.class), servletRequest
                                .invoke("getAttribute")
                                .arg(ClassWriter.ref(DispatcherServlet.class).staticRef("WEB_APPLICATION_CONTEXT_ATTRIBUTE")))
                );

        body._if(applicationContext.neNull())
                ._then()
                ._return(applicationContext.invoke("getBean").arg(beanClass));

//            ServletContext servletContext = servletRequest.getServletContext();
        JVar servletContext = body
                .decl(ClassWriter.ref(ServletContext.class), "servletContext")
                .init(servletRequest.invoke("getServletContext"));

//            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        JVar webApplicationContext = body
                .decl(ClassWriter.ref(WebApplicationContext.class), "webApplicationContext")
                .init(ClassWriter
                        .ref(WebApplicationContextUtils.class)
                        .staticInvoke("getRequiredWebApplicationContext")
                        .arg(servletContext));

//            return requiredWebApplicationContext.getBean(Foo.class);
        body._return(webApplicationContext.invoke("getBean").arg(beanClass));
//            getServiceMethod.javadoc().add("Extension-Point to use another Base-UriComponentsBuilder than the default one");
        return getServiceMethod;
    }

    public void appendUriStringMethod(UrlMethod urlMethod) {

        String name = CamelCaseUtils.firstCharLowerCased(urlMethod.getClazz().getSimpleName().toString())
                + CamelCaseUtils.firstCharUpperCased(urlMethod.getMethodName());
        JMethod uriStringMethod = definedClass.method(JMod.PUBLIC | JMod.STATIC, String.class,
                name);

        AbstractJClass serviceClass = ClassWriter.ref(urlMethod.getClazz().toString() + Options.getValue(Options.OptionEnum.ServiceClassName));

        JVar service = uriStringMethod.body().decl(serviceClass, "service")
                .init(JExpr.invoke(serviceMethod).arg(serviceClass.dotclass()));
        JInvocation invocation = service.invoke(urlMethod.getMethodName() + "UriString");

        uriStringMethod.body()._return(invocation);
        urlMethod.appendParams(uriStringMethod, invocation);

        methods.add(uriStringMethod);
    }

    public List<JMethod> getMethods() {
        return Collections.unmodifiableList(methods);
    }
}
