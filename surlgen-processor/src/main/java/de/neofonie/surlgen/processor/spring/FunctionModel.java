package de.neofonie.surlgen.processor.spring;

import com.google.common.base.Preconditions;
import com.helger.jcodemodel.*;
import de.neofonie.surlgen.processor.core.ClassWriter;
import de.neofonie.surlgen.processor.core.Options;
import de.neofonie.surlgen.processor.core.UrlMethod;
import org.springframework.stereotype.Service;
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

class FunctionModel {

    private static final Map<String, FunctionModel> map = new HashMap<>();
    private final String name;
    private final ClassWriter classWriter;
    //        private final JDefinedClass serviceClass;
    private final JDefinedClass definedClass;
    private JMethod serviceMethod;
    private final Options options;

    public static void write(ExecutableElement elem, Options options, ClassWriter classWriter) {
        FunctionModel functionModel = FunctionModel.create(elem, options, classWriter);
        functionModel.appendMethod((ExecutableElement) elem);

//        FunctionModel functionModel = new FunctionModel(elem, options, classWriter);
//        functionModel.appendMethod(elem);
    }

    public static FunctionModel create(ExecutableElement elem, Options options, ClassWriter classWriter) {
        return map.computeIfAbsent(elem.getEnclosingElement().toString(), key -> new FunctionModel(elem, options, classWriter));
    }

    FunctionModel(ExecutableElement elem, Options options, ClassWriter classWriter) {
        this.name = elem.getEnclosingElement().toString();
        this.classWriter = classWriter;
//            classWriter = new JCodeModel();
        try {
            this.options = options;
//                serviceClass = classWriter._class(name + options.getValue(Options.OptionEnum.ServiceClassName));
            definedClass = this.classWriter.createClass(name + options.getValue(Options.OptionEnum.FunctionClassName));

            definedClass.annotate(Service.class);
            definedClass.javadoc().add("Generated with " + UrlFunctionGenerator.class.getCanonicalName());
            serviceMethod = appendGetServiceMethod();

            Preconditions.checkNotNull(definedClass);
        } catch (JClassAlreadyExistsException e) {
            throw new IllegalStateException(e);
        }
    }

    private JMethod appendGetServiceMethod() {

//            private<T> T getService(Class<T> beanClass) {
        AbstractJClass serviceClass = classWriter.ref(name + options.getValue(Options.OptionEnum.ServiceClassName));
        Preconditions.checkNotNull(definedClass);
        Preconditions.checkNotNull(serviceClass);
        JMethod getServiceMethod = definedClass.method(JMod.PRIVATE, serviceClass, "getService");
        JTypeVar t = getServiceMethod.generify("T");
        getServiceMethod.type(t);

        AbstractJClass beanClass2 = classWriter.ref(Class.class).narrow(t);
        JVar beanClass = getServiceMethod.param(beanClass2, "beanClass");

//            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        JBlock body = getServiceMethod.body();
        JVar requestAttributes = body
                .decl(classWriter.ref(RequestAttributes.class), "requestAttributes")
                .init(classWriter.ref(RequestContextHolder.class).staticInvoke("getRequestAttributes"));

//                HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        JVar servletRequest = body
                .decl(classWriter.ref(HttpServletRequest.class), "servletRequest")
                .init(JExpr.cast(classWriter.ref(ServletRequestAttributes.class), requestAttributes)
                        .invoke("getRequest"));

//            ServletContext servletContext = servletRequest.getServletContext();
        JVar servletContext = body
                .decl(classWriter.ref(ServletContext.class), "servletContext")
                .init(servletRequest.invoke("getServletContext"));

//            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        JVar webApplicationContext = body
                .decl(classWriter.ref(WebApplicationContext.class), "webApplicationContext")
                .init(classWriter
                        .ref(WebApplicationContextUtils.class)
                        .staticInvoke("getRequiredWebApplicationContext")
                        .arg(servletContext));

//            return requiredWebApplicationContext.getBean(Foo.class);
        body._return(webApplicationContext.invoke("getBean").arg(beanClass));
//            getServiceMethod.javadoc().add("Extension-Point to use another Base-UriComponentsBuilder than the default one");
        return getServiceMethod;
    }

    void appendMethod(ExecutableElement method) {
//            String methodName = method.getSimpleName().toString();
        UrlMethod urlMethod = new UrlMethod(method, classWriter);

//            JMethod mvcUriComponentsBuilderMethod = appendMvcUriComponentsBuilderMethod(methodName, urlMethod);
        appendUriStringMethod(urlMethod);
    }

    //
    private void appendUriStringMethod(UrlMethod urlMethod) {
        String methodName = urlMethod.getMethodName();
        JMethod uriStringMethod = definedClass.method(JMod.PUBLIC, String.class, methodName);
//            definedClass.
//        checkSupport(urlMethod, uriStringMethod);

        AbstractJClass serviceClass = classWriter.ref(name + options.getValue(Options.OptionEnum.ServiceClassName));

        JVar service = uriStringMethod.body().decl(serviceClass, "service")
                .init(JExpr.invoke(serviceMethod).arg(serviceClass.dotclass()));
        JInvocation invocation = service.invoke(methodName + "UriString");

//            JInvocation invocation = JExpr.invoke(mvcUriComponentsBuilderMethod);
        uriStringMethod.body()._return(invocation);
        urlMethod.appendParams(uriStringMethod, invocation);
    }

//
//        private JMethod appendMvcUriComponentsBuilderMethod(String methodName, Params parameters) {
//            JMethod urlMethod = definedClass.method(JMod.PUBLIC, UriComponentsBuilder.class, methodName);
////        checkSupport(parameters, urlMethod);
//            JBlock body = urlMethod.body();
//            JInvocation fromMethodName = createMvcUriComponentsBuilderInvocation(parameters, methodName, urlMethod);
//            body._return(fromMethodName);
//            return urlMethod;
//        }
//
//        private JInvocation createMvcUriComponentsBuilderInvocation(Params parameters, String methodName, JMethod urlMethod) {
//            AbstractJClass mvcUriComponentsBuilder = classWriter.ref(MvcUriComponentsBuilder.class);
//            JInvocation fromMethodName = mvcUriComponentsBuilder.staticInvoke("fromMethodName");
//            fromMethodName.arg(JExpr.invoke(baseMvcUriComponentsMethod));
//            fromMethodName.arg(classWriter.ref(name).dotclass());
//            fromMethodName.arg(methodName);
//            fromMethodName.arg(parameters.createVarArgArray(classWriter, urlMethod));
//            return fromMethodName;
//        }
}
