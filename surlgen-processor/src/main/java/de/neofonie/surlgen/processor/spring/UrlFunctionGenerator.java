package de.neofonie.surlgen.processor.spring;

import com.google.common.base.Preconditions;
import com.helger.jcodemodel.*;
import de.neofonie.surlgen.processor.core.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.lang.model.element.ExecutableElement;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class UrlFunctionGenerator extends AbstractGenerator {

    private final Map<String, FunctionModel> map = new HashMap<>();

    @Override
    protected void handleElement(ExecutableElement elem) {
//        name = elem.getEnclosingElement().toString();
        String classname = Options.getValue(Options.OptionEnum.FunctionClassName);

        FunctionModel functionModel = map
                .computeIfAbsent(classname, key -> new FunctionModel(classname));
        UrlMethod urlMethod = new UrlMethod(elem);
        functionModel.appendUriStringMethod(urlMethod);
    }

    private static class FunctionModel {
        //        private final String name;
        private final JDefinedClass definedClass;
        private final JMethod serviceMethod;

        private FunctionModel(String classname) {

            try {
                definedClass = ClassWriter.createClass(classname);
                definedClass.javadoc().add("Generated with " + UrlFunctionGenerator.class.getCanonicalName());
                serviceMethod = appendGetServiceMethod();

                Preconditions.checkNotNull(definedClass);
            } catch (JClassAlreadyExistsException e) {
                throw new IllegalStateException(e);
            }
        }

        private JMethod appendGetServiceMethod() {

//            private<T> T getService(Class<T> beanClass) {
//            AbstractJClass serviceClass = ClassWriter.ref(name + Options.getValue(Options.OptionEnum.ServiceClassName));
//            Preconditions.checkNotNull(definedClass);
//            Preconditions.checkNotNull(serviceClass);
            JMethod getServiceMethod = definedClass.method(JMod.PRIVATE, Class.class, "getService");
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

            JMethod uriStringMethod = definedClass.method(JMod.PUBLIC, String.class,
                    CamelCaseUtils.firstCharLowerCased(urlMethod.getClazz().getSimpleName().toString()) +
                            CamelCaseUtils.firstCharUpperCased(urlMethod.getMethodName()));

            AbstractJClass serviceClass = ClassWriter.ref(urlMethod.getClazz().toString() + Options.getValue(Options.OptionEnum.ServiceClassName));

            JVar service = uriStringMethod.body().decl(serviceClass, "service")
                    .init(JExpr.invoke(serviceMethod).arg(serviceClass.dotclass()));
            JInvocation invocation = service.invoke(urlMethod.getMethodName() + "UriString");

            uriStringMethod.body()._return(invocation);
            urlMethod.appendParams(uriStringMethod, invocation);
        }
    }
}
