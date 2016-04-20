package de.neofonie.surlgen.processor.spring;

import de.neofonie.surlgen.processor.core.AbstractGenerator;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class UrlFactoryServiceGenerator extends AbstractGenerator {

    @Override
    protected void handleElement(Element elem) {
        Element clazz = elem.getEnclosingElement();
        FactoryModel factoryModel = FactoryModel.create(clazz.toString());
        factoryModel.appendMethod((ExecutableElement) elem);

    }
}
