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

package de.neofonie.surlgen.processor.core;

import com.google.common.base.Preconditions;
import com.helger.jcodemodel.JMethod;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TldWriter implements Closeable {

    private XMLStreamWriter writer;
    private final StringWriter stringWriter;
    private final File outputFile;
    private final Set<String> writtenNames = new HashSet<>();

    public TldWriter(File outputFile) throws XMLStreamException {
        stringWriter = new StringWriter();
        writer = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);
        writer.writeStartDocument();
        writer.writeStartElement("taglib");
        writer.writeAttribute("xmlns", "http://java.sun.com/xml/ns/javaee");
        writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        writer.writeAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-jsptaglibrary_2_1.xsd");
        writer.writeAttribute("version", "2.1");

        writeTag("tlib-version", "1.1");
        writeTag("short-name", "urlFunctions");
        writeTag("uri", Options.getValue(Options.OptionEnum.TLD_URI));
        this.outputFile = outputFile;
    }

    public void write(List<JMethod> methods) {
        for (JMethod jMethod : methods) {
            write(jMethod);
        }
    }

    private void write(JMethod method) {
        Preconditions.checkArgument(method.mods().isStatic());
        try {
            writer.writeStartElement("function");
            String name = getName(method);
            writeTag("name", name);
            writeTag("function-class", method.owningClass().fullName());

            StringBuilder functionSignature = new StringBuilder();
            String params = method
                    .params()
                    .stream()
                    .map(p -> p.type().fullName())
                    .collect(Collectors.joining(","));

            functionSignature
                    .append(method.type().fullName())
                    .append(' ')
                    .append(method.name())
                    .append('(')
                    .append(params)
                    .append(')');
            writeTag("function-signature", functionSignature.toString());
            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String getName(JMethod method) {
        String name = method.name();
        int i = 1;
        while (writtenNames.contains(name)) {
            name = method.name() + i;
            i++;
        }
        writtenNames.add(name);
        return name;
    }

    private void writeTag(String name, String content) throws XMLStreamException {
        writer.writeStartElement(name);
        writer.writeCharacters(content);
        writer.writeEndElement();
    }

    @Override
    public void close() throws IOException {

        try {
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                StreamResult result = new StreamResult(outputStream);
                Source source = new StreamSource(new StringReader(stringWriter.toString()));
                transformer.transform(source, result);
            } catch (TransformerException e) {
                throw new IOException(e);
            }

        } catch (XMLStreamException | TransformerConfigurationException e) {
            throw new IOException(e);
        } finally {
            writer = null;
        }
    }
}
