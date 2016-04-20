package de.neofonie.surlgen.processor.spring;

import com.helger.jcodemodel.JClassAlreadyExistsException;
import de.neofonie.surlgen.processor.core.AbstractGenerator;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.io.IOException;

public class UrlFactoryServiceGenerator extends AbstractGenerator {

    @Override
    protected void handleElement(Element elem) {
        Element clazz = elem.getEnclosingElement();
        FactoryModel factoryModel = FactoryModel.create(clazz.toString(), getOptions());
        factoryModel.appendMethod((ExecutableElement) elem);

    }

    @Override
    protected void finished() {
        try {
            FactoryModel.writeSourceCodes(getOutputDir());
        } catch (IOException | JClassAlreadyExistsException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
