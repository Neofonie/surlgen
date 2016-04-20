package de.neofonie.surlgen.processor.core;

import com.helger.jcodemodel.*;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

public class UrlMethod {

    private final List<? extends VariableElement> parameters;
    private final ExecutableElement method;
    private final ClassWriter classWriter;

    public UrlMethod(ExecutableElement method, ClassWriter classWriter) {
        this.method = method;
        this.classWriter = classWriter;
        this.parameters = method.getParameters();
    }

    public String getMethodName() {
        return method.getSimpleName().toString();
    }

    public void appendParams(JMethod uriStringMethod, JInvocation invocation) {
        for (VariableElement variableElement : parameters) {
            TypeEnum typeEnum = TypeEnum.getType(variableElement);
            if (typeEnum.isRelevantForUrl()) {
                AbstractJType type = classWriter.parseType(variableElement.asType().toString());
                JVar param = uriStringMethod.param(type, variableElement.getSimpleName().toString());
                invocation.arg(param);
            }
        }
    }

    public JArray createVarArgArray(JMethod urlMethod) {
        AbstractJType objectArray = classWriter.parseType("Object");
        JArray jArray = JExpr.newArray(objectArray);
        appendVarArgArray(urlMethod, jArray);
        return jArray;
    }

    public void appendVarArgArray(JMethod urlMethod, JArray jArray) {
        for (VariableElement variableElement : parameters) {
            TypeEnum typeEnum = TypeEnum.getType(variableElement);
            if (typeEnum.isRelevantForUrl()) {
                AbstractJType type = classWriter.parseType(variableElement.asType().toString());
                JVar param = urlMethod.param(type, variableElement.getSimpleName().toString());
                jArray.add(param);
            } else {
                jArray.add(JExpr._null());
            }
        }
    }
}
