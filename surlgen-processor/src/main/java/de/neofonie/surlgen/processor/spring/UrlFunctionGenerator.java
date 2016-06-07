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

package de.neofonie.surlgen.processor.spring;

import com.helger.jcodemodel.JClassAlreadyExistsException;
import de.neofonie.surlgen.processor.classwriter.FunctionClassWriter;
import de.neofonie.surlgen.processor.core.AbstractGenerator;
import de.neofonie.surlgen.processor.core.Options;
import de.neofonie.surlgen.processor.core.TldWriter;
import de.neofonie.surlgen.processor.core.UrlMethod;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class UrlFunctionGenerator extends AbstractGenerator {

    private static final Logger log = Logger.getLogger(UrlFunctionGenerator.class.getCanonicalName());
    private FunctionClassWriter functionClassWriter;

    @Override
    protected void handleElement(UrlMethod urlMethod) {

        try {
            functionClassWriter = FunctionClassWriter.createFunctionClassWriter();
        } catch (JClassAlreadyExistsException e) {
            throw new IllegalStateException(e);
        }

        functionClassWriter.appendUriStringMethod(urlMethod);
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
                throw new IllegalArgumentException("Couldnt create " + directory.getAbsolutePath());
            }
            try (TldWriter tldWriter = new TldWriter(outputFile)) {
                tldWriter.write(functionClassWriter.getMethods());
            }
        } catch (IOException | XMLStreamException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
