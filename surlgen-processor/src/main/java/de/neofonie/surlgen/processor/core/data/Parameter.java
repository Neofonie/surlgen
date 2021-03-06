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

package de.neofonie.surlgen.processor.core.data;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JArray;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;
import de.neofonie.surlgen.processor.classwriter.ClassWriter;
import de.neofonie.surlgen.processor.classwriter.UrlFactoryServiceWriter;
import de.neofonie.surlgen.processor.util.LangModelUtil;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class Parameter {

    private static final Logger log = Logger.getLogger(Parameter.class.getCanonicalName());
    protected final VariableElement variableElement;
    protected final LangModelUtil langModelUtil;

    protected Parameter(VariableElement variableElement, LangModelUtil langModelUtil) {
        this.variableElement = variableElement;
        this.langModelUtil = langModelUtil;
    }

    public static ArrayList<Parameter> createParameters(List<? extends VariableElement> parameters, LangModelUtil langModelUtil) {
        final ArrayList<Parameter> result = new ArrayList<>();
        for (VariableElement variableElement : parameters) {
            result.add(createParameters(variableElement, langModelUtil));
        }
        return result;
    }

    public static Parameter createParameters(VariableElement variableElement, LangModelUtil langModelUtil) {
        if (variableElement.getAnnotation(RequestParam.class) != null) {
            return new UrlRelevantParameter(variableElement, langModelUtil);
        }
        if (variableElement.getAnnotation(PathVariable.class) != null) {
            return new UrlRelevantParameter(variableElement, langModelUtil);
        }
        //ModelAttribute is currently not implemented in MvcUriComponentsBuilder - so we cant support this
        if (variableElement.getAnnotation(ModelAttribute.class) != null) {
            return new ModelAttributeParameter(variableElement, langModelUtil);
        }
        //MatrixVariable is currently not implemented in MvcUriComponentsBuilder - so we cant support this
        if (variableElement.getAnnotation(MatrixVariable.class) != null) {
            log.info("MatrixVariable currently isnt supported in MvcUriComponentsBuilder");
            return new NotSupportedParameter(variableElement, langModelUtil);
        }
        return new OtherParameter(variableElement, langModelUtil);
    }

    public abstract boolean isRelevantForUrl();

    public abstract void handleUriComponentsInvocation(JMethod urlMethod, JArray varArgArray, JVar uriComponentsBuilder, UrlFactoryServiceWriter urlFactoryServiceWriter);

    public final JVar appendParamToMethod(JMethod method) {
        AbstractJType type = ClassWriter.parseType(variableElement.asType().toString());
        return method.param(type, variableElement.getSimpleName().toString());
    }

}
