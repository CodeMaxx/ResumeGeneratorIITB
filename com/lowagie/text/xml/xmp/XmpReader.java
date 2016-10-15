/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.xml.XmlDomWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class XmpReader {
    private Document domDocument;

    public XmpReader(byte[] arrby) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
            this.domDocument = documentBuilder.parse(byteArrayInputStream);
        }
        catch (SAXException var2_3) {
            throw new ExceptionConverter(var2_3);
        }
        catch (IOException var2_4) {
            throw new ExceptionConverter(var2_4);
        }
        catch (ParserConfigurationException var2_5) {
            throw new ExceptionConverter(var2_5);
        }
    }

    public void replace(String string, String string2, String string3) {
        NodeList nodeList = this.domDocument.getElementsByTagNameNS(string, string2);
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            this.setNodeText(this.domDocument, node, string3);
        }
    }

    public boolean setNodeText(Document document, Node node, String string) {
        if (node == null) {
            return false;
        }
        Node node2 = null;
        while ((node2 = node.getFirstChild()) != null) {
            node.removeChild(node2);
        }
        node.appendChild(document.createTextNode(string));
        return true;
    }

    public byte[] serializeDoc() throws IOException {
        XmlDomWriter xmlDomWriter = new XmlDomWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        xmlDomWriter.setOutput(byteArrayOutputStream, null);
        xmlDomWriter.setCanonical(false);
        xmlDomWriter.write(this.domDocument);
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}

