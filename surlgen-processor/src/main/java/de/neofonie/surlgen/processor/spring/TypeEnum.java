package de.neofonie.surlgen.processor.spring;

import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.logging.Logger;

enum TypeEnum {
    URL_RELEVANT,
    NOT_SUPPORTED,
    OTHER;

    private static final Logger log = Logger.getLogger(TypeEnum.class.getCanonicalName());

    static TypeEnum getType(VariableElement variableElement) {
        if (variableElement.getAnnotation(RequestParam.class) != null) {
            return URL_RELEVANT;
        }
        if (variableElement.getAnnotation(PathVariable.class) != null) {
            return URL_RELEVANT;
        }
        //MatrixVariable is currently not implemented in MvcUriComponentsBuilder - so we cant support this
        if (variableElement.getAnnotation(ModelAttribute.class) != null) {
            log.info("MatrixVariable currently isnt supported in MvcUriComponentsBuilder");
            return NOT_SUPPORTED;
        }
        //MatrixVariable is currently not implemented in MvcUriComponentsBuilder - so we cant support this
        if (variableElement.getAnnotation(MatrixVariable.class) != null) {
            log.info("MatrixVariable currently isnt supported in MvcUriComponentsBuilder");
            return NOT_SUPPORTED;
        }
        return OTHER;
    }

    static boolean isNotSupported(List<? extends VariableElement> parameters) {
        for (VariableElement variableElement : parameters) {
            if (getType(variableElement) == NOT_SUPPORTED) {
                return true;
            }
        }
        return false;
    }

    public boolean isRelevantForUrl() {
        return this == URL_RELEVANT;
    }
}
