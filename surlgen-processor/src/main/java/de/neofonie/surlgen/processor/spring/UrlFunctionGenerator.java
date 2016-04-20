package de.neofonie.surlgen.processor.spring;

import de.neofonie.surlgen.processor.core.AbstractGenerator;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class UrlFunctionGenerator extends AbstractGenerator {

    @Override
    protected void handleElement(Element elem) {
        FunctionModel.write((ExecutableElement) elem, getOptions());
    }
}
