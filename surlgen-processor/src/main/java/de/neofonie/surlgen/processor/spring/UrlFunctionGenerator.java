package de.neofonie.surlgen.processor.spring;

import com.google.common.base.Preconditions;
import com.helger.jcodemodel.*;
import de.neofonie.surlgen.processor.core.*;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;

import javax.lang.model.element.ExecutableElement;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UrlFunctionGenerator extends AbstractGenerator {

    private static final Logger log = Logger.getLogger(UrlFunctionGenerator.class.getCanonicalName());
    private JDefinedClass definedClass;
    private JMethod serviceMethod;
    private final List<JMethod> methods = new ArrayList<>();

    @Override
    protected void handleElement(ExecutableElement elem) {
        String classname = Options.getValue(Options.OptionEnum.FunctionClassName);

        if (definedClass == null) {
            try {
                definedClass = ClassWriter.createClass(classname);
                definedClass.javadoc().add("Generated with " + UrlFunctionGenerator.class.getCanonicalName());
                serviceMethod = appendGetServiceMethod();

                Preconditions.checkNotNull(definedClass);
            } catch (JClassAlreadyExistsException e) {
                throw new IllegalStateException(e);
            }
        }
        UrlMethod urlMethod = new UrlMethod(elem);
        appendUriStringMethod(urlMethod);
    }

    private JMethod appendGetServiceMethod() {

//            private<T> T getService(Class<T> beanClass) {
        JMethod getServiceMethod = definedClass.method(JMod.PRIVATE | JMod.STATIC, Class.class, "getService");
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

    private void appendUriStringMethod(UrlMethod urlMethod) {

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

    @Override
    protected void finished(File outputDir) {
        super.finished(outputDir);
        try {
            String value = Options.getValue(Options.OptionEnum.TLD_FILE_NAME);
            if (value == null || value.isEmpty()) {
                log.info(String.format("No config for %s found - no TLD will be generated", Options.OptionEnum.TLD_FILE_NAME.getName()));
                return;
            }
            File outputFile = new File(value);
            if (!outputFile.isAbsolute()) {
                outputFile = new File(outputDir, value);
            }
            File directory = outputFile.getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Couldnt create " + directory.getAbsolutePath());
            }
            try (TldWriter tldWriter = new TldWriter(outputFile)) {
                tldWriter.write(methods);
            }
        } catch (IOException | XMLStreamException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
