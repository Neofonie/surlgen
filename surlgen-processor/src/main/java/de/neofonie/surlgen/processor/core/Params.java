package de.neofonie.surlgen.processor.core;

import com.helger.jcodemodel.*;

import javax.lang.model.element.VariableElement;
import java.util.List;

public class Params {

    private final List<? extends VariableElement> parameters;

    public Params(List<? extends VariableElement> parameters) {
        this.parameters = parameters;
    }

    public void appendParams(JCodeModel codeModel, JMethod uriStringMethod, JInvocation invocation) {
        for (VariableElement variableElement : parameters) {
            TypeEnum typeEnum = TypeEnum.getType(variableElement);
            if (typeEnum.isRelevantForUrl()) {
                AbstractJType type = codeModel.parseType(variableElement.asType().toString());
                JVar param = uriStringMethod.param(type, variableElement.getSimpleName().toString());
                invocation.arg(param);
            }
        }
    }

    public JArray createVarArgArray(JCodeModel codeModel, JMethod urlMethod) {
        AbstractJType objectArray = codeModel.parseType("Object");
        JArray jArray = JExpr.newArray(objectArray);
        appendVarArgArray(codeModel, urlMethod, jArray);
        return jArray;
    }

    public void appendVarArgArray(JCodeModel codeModel, JMethod urlMethod, JArray jArray) {
        for (VariableElement variableElement : parameters) {
            TypeEnum typeEnum = TypeEnum.getType(variableElement);
            if (typeEnum.isRelevantForUrl()) {
                AbstractJType type = codeModel.parseType(variableElement.asType().toString());
                JVar param = urlMethod.param(type, variableElement.getSimpleName().toString());
                jArray.add(param);
            } else {
                jArray.add(JExpr._null());
            }
        }
    }
}
