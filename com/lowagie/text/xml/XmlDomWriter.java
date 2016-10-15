/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XmlDomWriter {
    protected PrintWriter fOut;
    protected boolean fCanonical;
    protected boolean fXML11;

    public XmlDomWriter() {
    }

    public XmlDomWriter(boolean bl) {
        this.fCanonical = bl;
    }

    public void setCanonical(boolean bl) {
        this.fCanonical = bl;
    }

    public void setOutput(OutputStream outputStream, String string) throws UnsupportedEncodingException {
        if (string == null) {
            string = "UTF8";
        }
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, string);
        this.fOut = new PrintWriter(outputStreamWriter);
    }

    public void setOutput(Writer writer) {
        this.fOut = writer instanceof PrintWriter ? (PrintWriter)writer : new PrintWriter(writer);
    }

    public void write(Node node) {
        if (node == null) {
            return;
        }
        short s = node.getNodeType();
        switch (s) {
            case 9: {
                Document document = (Document)node;
                this.fXML11 = false;
                if (!this.fCanonical) {
                    if (this.fXML11) {
                        this.fOut.println("<?xml version=\"1.1\" encoding=\"UTF-8\"?>");
                    } else {
                        this.fOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    }
                    this.fOut.flush();
                    this.write(document.getDoctype());
                }
                this.write(document.getDocumentElement());
                break;
            }
            case 10: {
                DocumentType documentType = (DocumentType)node;
                this.fOut.print("<!DOCTYPE ");
                this.fOut.print(documentType.getName());
                String string = documentType.getPublicId();
                String string2 = documentType.getSystemId();
                if (string != null) {
                    this.fOut.print(" PUBLIC '");
                    this.fOut.print(string);
                    this.fOut.print("' '");
                    this.fOut.print(string2);
                    this.fOut.print('\'');
                } else if (string2 != null) {
                    this.fOut.print(" SYSTEM '");
                    this.fOut.print(string2);
                    this.fOut.print('\'');
                }
                String string3 = documentType.getInternalSubset();
                if (string3 != null) {
                    this.fOut.println(" [");
                    this.fOut.print(string3);
                    this.fOut.print(']');
                }
                this.fOut.println('>');
                break;
            }
            case 1: {
                this.fOut.print('<');
                this.fOut.print(node.getNodeName());
                Attr[] arrattr = this.sortAttributes(node.getAttributes());
                for (int i = 0; i < arrattr.length; ++i) {
                    Attr attr = arrattr[i];
                    this.fOut.print(' ');
                    this.fOut.print(attr.getNodeName());
                    this.fOut.print("=\"");
                    this.normalizeAndPrint(attr.getNodeValue(), true);
                    this.fOut.print('\"');
                }
                this.fOut.print('>');
                this.fOut.flush();
                for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                    this.write(node2);
                }
                break;
            }
            case 5: {
                if (this.fCanonical) {
                    for (Node node3 = node.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                        this.write(node3);
                    }
                    break;
                }
                this.fOut.print('&');
                this.fOut.print(node.getNodeName());
                this.fOut.print(';');
                this.fOut.flush();
                break;
            }
            case 4: {
                if (this.fCanonical) {
                    this.normalizeAndPrint(node.getNodeValue(), false);
                } else {
                    this.fOut.print("<![CDATA[");
                    this.fOut.print(node.getNodeValue());
                    this.fOut.print("]]>");
                }
                this.fOut.flush();
                break;
            }
            case 3: {
                this.normalizeAndPrint(node.getNodeValue(), false);
                this.fOut.flush();
                break;
            }
            case 7: {
                this.fOut.print("<?");
                this.fOut.print(node.getNodeName());
                String string = node.getNodeValue();
                if (string != null && string.length() > 0) {
                    this.fOut.print(' ');
                    this.fOut.print(string);
                }
                this.fOut.print("?>");
                this.fOut.flush();
                break;
            }
            case 8: {
                if (this.fCanonical) break;
                this.fOut.print("<!--");
                String string = node.getNodeValue();
                if (string != null && string.length() > 0) {
                    this.fOut.print(string);
                }
                this.fOut.print("-->");
                this.fOut.flush();
            }
        }
        if (s == 1) {
            this.fOut.print("</");
            this.fOut.print(node.getNodeName());
            this.fOut.print('>');
            this.fOut.flush();
        }
    }

    protected Attr[] sortAttributes(NamedNodeMap namedNodeMap) {
        int n;
        int n2 = namedNodeMap != null ? namedNodeMap.getLength() : 0;
        Attr[] arrattr = new Attr[n2];
        for (n = 0; n < n2; ++n) {
            arrattr[n] = (Attr)namedNodeMap.item(n);
        }
        for (n = 0; n < n2 - 1; ++n) {
            String string = arrattr[n].getNodeName();
            int n3 = n;
            for (int i = n + 1; i < n2; ++i) {
                String string2 = arrattr[i].getNodeName();
                if (string2.compareTo(string) >= 0) continue;
                string = string2;
                n3 = i;
            }
            if (n3 == n) continue;
            Attr attr = arrattr[n];
            arrattr[n] = arrattr[n3];
            arrattr[n3] = attr;
        }
        return arrattr;
    }

    protected void normalizeAndPrint(String string, boolean bl) {
        int n = string != null ? string.length() : 0;
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            this.normalizeAndPrint(c, bl);
        }
    }

    protected void normalizeAndPrint(char c, boolean bl) {
        switch (c) {
            case '<': {
                this.fOut.print("&lt;");
                break;
            }
            case '>': {
                this.fOut.print("&gt;");
                break;
            }
            case '&': {
                this.fOut.print("&amp;");
                break;
            }
            case '\"': {
                if (bl) {
                    this.fOut.print("&quot;");
                    break;
                }
                this.fOut.print("\"");
                break;
            }
            case '\r': {
                this.fOut.print("&#xD;");
                break;
            }
            case '\n': {
                if (this.fCanonical) {
                    this.fOut.print("&#xA;");
                    break;
                }
            }
            default: {
                if (this.fXML11 && (c >= '\u0001' && c <= '\u001f' && c != '\t' && c != '\n' || c >= '' && c <= '\u009f' || c == '\u2028') || bl && (c == '\t' || c == '\n')) {
                    this.fOut.print("&#x");
                    this.fOut.print(Integer.toHexString(c).toUpperCase());
                    this.fOut.print(";");
                    break;
                }
                this.fOut.print(c);
            }
        }
    }
}

