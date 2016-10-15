/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.xml.XmlDomWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class XfaForm {
    private Xml2SomTemplate templateSom;
    private Node templateNode;
    private Xml2SomDatasets datasetsSom;
    private Node datasetsNode;
    private AcroFieldsSearch acroFieldsSom;
    private PdfReader reader;
    private boolean xfaPresent;
    private Document domDocument;
    private boolean changed;
    public static final String XFA_DATA_SCHEMA = "http://www.xfa.org/schema/xfa-data/1.0/";

    public XfaForm() {
    }

    public static PdfObject getXfaObject(PdfReader pdfReader) {
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfReader.getCatalog().get(PdfName.ACROFORM));
        if (pdfDictionary == null) {
            return null;
        }
        return PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.XFA));
    }

    public XfaForm(PdfReader pdfReader) throws IOException, ParserConfigurationException, SAXException {
        Object object;
        Object object2;
        Object object3;
        this.reader = pdfReader;
        PdfObject pdfObject = XfaForm.getXfaObject(pdfReader);
        if (pdfObject == null) {
            this.xfaPresent = false;
            return;
        }
        this.xfaPresent = true;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (pdfObject.isArray()) {
            object = ((PdfArray)pdfObject).getArrayList();
            for (int i = 1; i < object.size(); i += 2) {
                object2 = PdfReader.getPdfObject((PdfObject)object.get(i));
                if (!(object2 instanceof PRStream)) continue;
                object3 = PdfReader.getStreamBytes((PRStream)object2);
                byteArrayOutputStream.write((byte[])object3);
            }
        } else if (pdfObject instanceof PRStream) {
            object = PdfReader.getStreamBytes((PRStream)pdfObject);
            byteArrayOutputStream.write((byte[])object);
        }
        byteArrayOutputStream.close();
        object = DocumentBuilderFactory.newInstance();
        object.setNamespaceAware(true);
        DocumentBuilder documentBuilder = object.newDocumentBuilder();
        this.domDocument = documentBuilder.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        object2 = this.domDocument.getFirstChild();
        while (object2.getChildNodes().getLength() == 0) {
            object2 = object2.getNextSibling();
        }
        for (object2 = object2.getFirstChild(); object2 != null; object2 = object2.getNextSibling()) {
            if (object2.getNodeType() != 1) continue;
            object3 = object2.getLocalName();
            if (object3.equals("template")) {
                this.templateNode = object2;
                this.templateSom = new Xml2SomTemplate((Node)object2);
                continue;
            }
            if (!object3.equals("datasets")) continue;
            this.datasetsNode = object2;
            this.datasetsSom = new Xml2SomDatasets(object2.getFirstChild());
        }
    }

    public static void setXfa(XfaForm xfaForm, PdfReader pdfReader, PdfWriter pdfWriter) throws IOException {
        Object object;
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfReader.getCatalog().get(PdfName.ACROFORM));
        if (pdfDictionary == null) {
            return;
        }
        PdfObject pdfObject = XfaForm.getXfaObject(pdfReader);
        if (pdfObject.isArray()) {
            object = ((PdfArray)pdfObject).getArrayList();
            int n = -1;
            int n2 = -1;
            for (int i = 0; i < object.size(); i += 2) {
                PdfString pdfString = (PdfString)object.get(i);
                if ("template".equals(pdfString.toString())) {
                    n = i + 1;
                }
                if (!"datasets".equals(pdfString.toString())) continue;
                n2 = i + 1;
            }
            if (n > -1 && n2 > -1) {
                pdfReader.killXref((PdfIndirectReference)object.get(n));
                pdfReader.killXref((PdfIndirectReference)object.get(n2));
                PdfStream pdfStream = new PdfStream(XfaForm.serializeDoc(xfaForm.templateNode));
                pdfStream.flateCompress(pdfWriter.getCompressionLevel());
                object.set(n, pdfWriter.addToBody(pdfStream).getIndirectReference());
                PdfStream pdfStream2 = new PdfStream(XfaForm.serializeDoc(xfaForm.datasetsNode));
                pdfStream2.flateCompress(pdfWriter.getCompressionLevel());
                object.set(n2, pdfWriter.addToBody(pdfStream2).getIndirectReference());
                pdfDictionary.put(PdfName.XFA, new PdfArray((ArrayList)object));
                return;
            }
        }
        pdfReader.killXref(pdfDictionary.get(PdfName.XFA));
        object = new PdfStream(XfaForm.serializeDoc(xfaForm.domDocument));
        object.flateCompress(pdfWriter.getCompressionLevel());
        PdfIndirectReference pdfIndirectReference = pdfWriter.addToBody((PdfObject)object).getIndirectReference();
        pdfDictionary.put(PdfName.XFA, pdfIndirectReference);
    }

    public void setXfa(PdfWriter pdfWriter) throws IOException {
        XfaForm.setXfa(this, this.reader, pdfWriter);
    }

    public static byte[] serializeDoc(Node node) throws IOException {
        XmlDomWriter xmlDomWriter = new XmlDomWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        xmlDomWriter.setOutput(byteArrayOutputStream, null);
        xmlDomWriter.setCanonical(false);
        xmlDomWriter.write(node);
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public boolean isXfaPresent() {
        return this.xfaPresent;
    }

    public Document getDomDocument() {
        return this.domDocument;
    }

    public String findFieldName(String string, AcroFields acroFields) {
        HashMap hashMap = acroFields.getFields();
        if (hashMap.containsKey(string)) {
            return string;
        }
        if (this.acroFieldsSom == null) {
            this.acroFieldsSom = new AcroFieldsSearch(hashMap.keySet());
        }
        if (this.acroFieldsSom.getAcroShort2LongName().containsKey(string)) {
            return (String)this.acroFieldsSom.getAcroShort2LongName().get(string);
        }
        return this.acroFieldsSom.inverseSearchGlobal(Xml2Som.splitParts(string));
    }

    public String findDatasetsName(String string) {
        if (this.datasetsSom.getName2Node().containsKey(string)) {
            return string;
        }
        return this.datasetsSom.inverseSearchGlobal(Xml2Som.splitParts(string));
    }

    public Node findDatasetsNode(String string) {
        if (string == null) {
            return null;
        }
        if ((string = this.findDatasetsName(string)) == null) {
            return null;
        }
        return (Node)this.datasetsSom.getName2Node().get(string);
    }

    public static String getNodeText(Node node) {
        if (node == null) {
            return "";
        }
        return XfaForm.getNodeText(node, "");
    }

    private static String getNodeText(Node node, String string) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == 1) {
                string = XfaForm.getNodeText(node2, string);
                continue;
            }
            if (node2.getNodeType() != 3) continue;
            string = string + node2.getNodeValue();
        }
        return string;
    }

    public void setNodeText(Node node, String string) {
        if (node == null) {
            return;
        }
        Node node2 = null;
        while ((node2 = node.getFirstChild()) != null) {
            node.removeChild(node2);
        }
        if (node.getAttributes().getNamedItemNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode") != null) {
            node.getAttributes().removeNamedItemNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode");
        }
        node.appendChild(this.domDocument.createTextNode(string));
        this.changed = true;
    }

    public void setXfaPresent(boolean bl) {
        this.xfaPresent = bl;
    }

    public void setDomDocument(Document document) {
        this.domDocument = document;
    }

    public PdfReader getReader() {
        return this.reader;
    }

    public void setReader(PdfReader pdfReader) {
        this.reader = pdfReader;
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void setChanged(boolean bl) {
        this.changed = bl;
    }

    public Xml2SomTemplate getTemplateSom() {
        return this.templateSom;
    }

    public void setTemplateSom(Xml2SomTemplate xml2SomTemplate) {
        this.templateSom = xml2SomTemplate;
    }

    public Xml2SomDatasets getDatasetsSom() {
        return this.datasetsSom;
    }

    public void setDatasetsSom(Xml2SomDatasets xml2SomDatasets) {
        this.datasetsSom = xml2SomDatasets;
    }

    public AcroFieldsSearch getAcroFieldsSom() {
        return this.acroFieldsSom;
    }

    public void setAcroFieldsSom(AcroFieldsSearch acroFieldsSearch) {
        this.acroFieldsSom = acroFieldsSearch;
    }

    public Node getDatasetsNode() {
        return this.datasetsNode;
    }

    public static class Xml2SomTemplate
    extends Xml2Som {
        private boolean dynamicForm;
        private int templateLevel;

        public Xml2SomTemplate(Node node) {
            this.order = new ArrayList();
            this.name2Node = new HashMap();
            this.stack = new Stack2();
            this.anform = 0;
            this.templateLevel = 0;
            this.inverseSearch = new HashMap();
            this.processTemplate(node, null);
        }

        public String getFieldType(String string) {
            Node node;
            Node node2 = (Node)this.name2Node.get(string);
            if (node2 == null) {
                return null;
            }
            if (node2.getLocalName().equals("exclGroup")) {
                return "exclGroup";
            }
            for (node = node2.getFirstChild(); !(node == null || node.getNodeType() == 1 && node.getLocalName().equals("ui")); node = node.getNextSibling()) {
            }
            if (node == null) {
                return null;
            }
            for (Node node3 = node.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                if (node3.getNodeType() != 1 || node3.getLocalName().equals("extras") && node3.getLocalName().equals("picture")) continue;
                return node3.getLocalName();
            }
            return null;
        }

        private void processTemplate(Node node, HashMap hashMap) {
            if (hashMap == null) {
                hashMap = new HashMap<String, Integer>();
            }
            HashMap<String, Integer> hashMap2 = new HashMap<String, Integer>();
            for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                Node node3;
                String string;
                Object object;
                int n;
                if (node2.getNodeType() != 1) continue;
                String string2 = node2.getLocalName();
                if (string2.equals("subform")) {
                    node3 = node2.getAttributes().getNamedItem("name");
                    string = "#subform";
                    n = 1;
                    if (node3 != null) {
                        string = Xml2SomTemplate.escapeSom(node3.getNodeValue());
                        n = 0;
                    }
                    if (n != 0) {
                        object = new Integer(this.anform);
                        ++this.anform;
                    } else {
                        object = (Integer)hashMap2.get(string);
                        object = object == null ? new Integer(0) : new Integer(object.intValue() + 1);
                        hashMap2.put(string, (Integer)object);
                    }
                    this.stack.push(string + "[" + object.toString() + "]");
                    ++this.templateLevel;
                    if (n != 0) {
                        this.processTemplate(node2, hashMap);
                    } else {
                        this.processTemplate(node2, null);
                    }
                    --this.templateLevel;
                    this.stack.pop();
                    continue;
                }
                if (string2.equals("field") || string2.equals("exclGroup")) {
                    node3 = node2.getAttributes().getNamedItem("name");
                    if (node3 == null) continue;
                    string = Xml2SomTemplate.escapeSom(node3.getNodeValue());
                    Integer n2 = (Integer)hashMap.get(string);
                    n2 = n2 == null ? new Integer(0) : new Integer(n2 + 1);
                    hashMap.put(string, n2);
                    this.stack.push(string + "[" + n2.toString() + "]");
                    object = this.printStack();
                    this.order.add(object);
                    this.inverseSearchAdd((String)object);
                    this.name2Node.put(object, node2);
                    this.stack.pop();
                    continue;
                }
                if (this.dynamicForm || this.templateLevel <= 0 || !string2.equals("occur")) continue;
                int n3 = 1;
                int n4 = 1;
                n = 1;
                object = node2.getAttributes().getNamedItem("initial");
                if (object != null) {
                    try {
                        n3 = Integer.parseInt(object.getNodeValue().trim());
                    }
                    catch (Exception var10_14) {
                        // empty catch block
                    }
                }
                if ((object = node2.getAttributes().getNamedItem("min")) != null) {
                    try {
                        n4 = Integer.parseInt(object.getNodeValue().trim());
                    }
                    catch (Exception var10_15) {
                        // empty catch block
                    }
                }
                if ((object = node2.getAttributes().getNamedItem("max")) != null) {
                    try {
                        n = Integer.parseInt(object.getNodeValue().trim());
                    }
                    catch (Exception var10_16) {
                        // empty catch block
                    }
                }
                if (n3 == n4 && n4 == n) continue;
                this.dynamicForm = true;
            }
        }

        public boolean isDynamicForm() {
            return this.dynamicForm;
        }

        public void setDynamicForm(boolean bl) {
            this.dynamicForm = bl;
        }
    }

    public static class AcroFieldsSearch
    extends Xml2Som {
        private HashMap acroShort2LongName;

        public AcroFieldsSearch(Collection collection) {
            this.inverseSearch = new HashMap();
            this.acroShort2LongName = new HashMap();
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                String string = (String)iterator.next();
                String string2 = AcroFieldsSearch.getShortName(string);
                this.acroShort2LongName.put(string2, string);
                AcroFieldsSearch.inverseSearchAdd(this.inverseSearch, AcroFieldsSearch.splitParts(string2), string);
            }
        }

        public HashMap getAcroShort2LongName() {
            return this.acroShort2LongName;
        }

        public void setAcroShort2LongName(HashMap hashMap) {
            this.acroShort2LongName = hashMap;
        }
    }

    public static class Xml2SomDatasets
    extends Xml2Som {
        public Xml2SomDatasets(Node node) {
            this.order = new ArrayList();
            this.name2Node = new HashMap();
            this.stack = new Stack2();
            this.anform = 0;
            this.inverseSearch = new HashMap();
            this.processDatasetsInternal(node);
        }

        public Node insertNode(Node node, String string) {
            Stack2 stack2 = Xml2SomDatasets.splitParts(string);
            Document document = node.getOwnerDocument();
            Node node2 = null;
            node = node.getFirstChild();
            for (int i = 0; i < stack2.size(); ++i) {
                Object object;
                String string2 = (String)stack2.get(i);
                int n = string2.lastIndexOf(91);
                String string3 = string2.substring(0, n);
                n = Integer.parseInt(string2.substring(n + 1, string2.length() - 1));
                int n2 = -1;
                for (node2 = node.getFirstChild(); !(node2 == null || node2.getNodeType() == 1 && (object = Xml2SomDatasets.escapeSom(node2.getLocalName())).equals(string3) && ++n2 == n); node2 = node2.getNextSibling()) {
                }
                while (n2 < n) {
                    node2 = document.createElementNS(null, string3);
                    node2 = node.appendChild(node2);
                    object = document.createAttributeNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode");
                    object.setNodeValue("dataGroup");
                    node2.getAttributes().setNamedItemNS((Node)object);
                    ++n2;
                }
                node = node2;
            }
            Xml2SomDatasets.inverseSearchAdd(this.inverseSearch, stack2, string);
            this.name2Node.put(string, node2);
            this.order.add(string);
            return node2;
        }

        private static boolean hasChildren(Node node) {
            Object object;
            Node node2 = node.getAttributes().getNamedItemNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode");
            if (node2 != null) {
                object = node2.getNodeValue();
                if ("dataGroup".equals(object)) {
                    return true;
                }
                if ("dataValue".equals(object)) {
                    return false;
                }
            }
            if (!node.hasChildNodes()) {
                return false;
            }
            for (object = node.getFirstChild(); object != null; object = object.getNextSibling()) {
                if (object.getNodeType() != 1) continue;
                return true;
            }
            return false;
        }

        private void processDatasetsInternal(Node node) {
            HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
            for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (node2.getNodeType() != 1) continue;
                String string = Xml2SomDatasets.escapeSom(node2.getLocalName());
                Integer n = (Integer)hashMap.get(string);
                n = n == null ? new Integer(0) : new Integer(n + 1);
                hashMap.put(string, n);
                if (Xml2SomDatasets.hasChildren(node2)) {
                    this.stack.push(string + "[" + n.toString() + "]");
                    this.processDatasetsInternal(node2);
                    this.stack.pop();
                    continue;
                }
                this.stack.push(string + "[" + n.toString() + "]");
                String string2 = this.printStack();
                this.order.add(string2);
                this.inverseSearchAdd(string2);
                this.name2Node.put(string2, node2);
                this.stack.pop();
            }
        }
    }

    public static class Xml2Som {
        protected ArrayList order;
        protected HashMap name2Node;
        protected HashMap inverseSearch;
        protected Stack2 stack;
        protected int anform;

        public static String escapeSom(String string) {
            int n = string.indexOf(46);
            if (n < 0) {
                return string;
            }
            StringBuffer stringBuffer = new StringBuffer();
            int n2 = 0;
            while (n >= 0) {
                stringBuffer.append(string.substring(n2, n));
                stringBuffer.append('\\');
                n2 = n;
                n = string.indexOf(46, n + 1);
            }
            stringBuffer.append(string.substring(n2));
            return stringBuffer.toString();
        }

        public static String unescapeSom(String string) {
            int n = string.indexOf(92);
            if (n < 0) {
                return string;
            }
            StringBuffer stringBuffer = new StringBuffer();
            int n2 = 0;
            while (n >= 0) {
                stringBuffer.append(string.substring(n2, n));
                n2 = n + 1;
                n = string.indexOf(92, n + 1);
            }
            stringBuffer.append(string.substring(n2));
            return stringBuffer.toString();
        }

        protected String printStack() {
            if (this.stack.empty()) {
                return "";
            }
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < this.stack.size(); ++i) {
                stringBuffer.append('.').append((String)this.stack.get(i));
            }
            return stringBuffer.substring(1);
        }

        public static String getShortName(String string) {
            int n = string.indexOf(".#subform[");
            if (n < 0) {
                return string;
            }
            int n2 = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while (n >= 0) {
                stringBuffer.append(string.substring(n2, n));
                n = string.indexOf("]", n + 10);
                if (n < 0) {
                    return stringBuffer.toString();
                }
                n2 = n + 1;
                n = string.indexOf(".#subform[", n2);
            }
            stringBuffer.append(string.substring(n2));
            return stringBuffer.toString();
        }

        public void inverseSearchAdd(String string) {
            Xml2Som.inverseSearchAdd(this.inverseSearch, this.stack, string);
        }

        public static void inverseSearchAdd(HashMap hashMap, Stack2 stack2, String string) {
            String string2 = (String)stack2.peek();
            InverseStore inverseStore = (InverseStore)hashMap.get(string2);
            if (inverseStore == null) {
                inverseStore = new InverseStore();
                hashMap.put(string2, inverseStore);
            }
            for (int i = stack2.size() - 2; i >= 0; --i) {
                InverseStore inverseStore2;
                string2 = (String)stack2.get(i);
                int n = inverseStore.part.indexOf(string2);
                if (n < 0) {
                    inverseStore.part.add(string2);
                    inverseStore2 = new InverseStore();
                    inverseStore.follow.add(inverseStore2);
                } else {
                    inverseStore2 = (InverseStore)inverseStore.follow.get(n);
                }
                inverseStore = inverseStore2;
            }
            inverseStore.part.add("");
            inverseStore.follow.add(string);
        }

        public String inverseSearchGlobal(ArrayList arrayList) {
            if (arrayList.isEmpty()) {
                return null;
            }
            InverseStore inverseStore = (InverseStore)this.inverseSearch.get(arrayList.get(arrayList.size() - 1));
            if (inverseStore == null) {
                return null;
            }
            for (int i = arrayList.size() - 2; i >= 0; --i) {
                String string = (String)arrayList.get(i);
                int n = inverseStore.part.indexOf(string);
                if (n < 0) {
                    if (inverseStore.isSimilar(string)) {
                        return null;
                    }
                    return inverseStore.getDefaultName();
                }
                inverseStore = (InverseStore)inverseStore.follow.get(n);
            }
            return inverseStore.getDefaultName();
        }

        public static Stack2 splitParts(String string) {
            String string2;
            while (string.startsWith(".")) {
                string = string.substring(1);
            }
            Stack2 stack2 = new Stack2();
            int n = 0;
            int n2 = 0;
            do {
                n2 = n;
                while ((n2 = string.indexOf(46, n2)) >= 0 && string.charAt(n2 - 1) == '\\') {
                    ++n2;
                }
                if (n2 < 0) break;
                string2 = string.substring(n, n2);
                if (!string2.endsWith("]")) {
                    string2 = string2 + "[0]";
                }
                stack2.add(string2);
                n = n2 + 1;
            } while (true);
            string2 = string.substring(n);
            if (!string2.endsWith("]")) {
                string2 = string2 + "[0]";
            }
            stack2.add(string2);
            return stack2;
        }

        public ArrayList getOrder() {
            return this.order;
        }

        public void setOrder(ArrayList arrayList) {
            this.order = arrayList;
        }

        public HashMap getName2Node() {
            return this.name2Node;
        }

        public void setName2Node(HashMap hashMap) {
            this.name2Node = hashMap;
        }

        public HashMap getInverseSearch() {
            return this.inverseSearch;
        }

        public void setInverseSearch(HashMap hashMap) {
            this.inverseSearch = hashMap;
        }
    }

    public static class Stack2
    extends ArrayList {
        private static final long serialVersionUID = -7451476576174095212L;

        public Object peek() {
            if (this.size() == 0) {
                throw new EmptyStackException();
            }
            return this.get(this.size() - 1);
        }

        public Object pop() {
            if (this.size() == 0) {
                throw new EmptyStackException();
            }
            Object e = this.get(this.size() - 1);
            this.remove(this.size() - 1);
            return e;
        }

        public Object push(Object object) {
            this.add(object);
            return object;
        }

        public boolean empty() {
            return this.size() == 0;
        }
    }

    public static class InverseStore {
        protected ArrayList part = new ArrayList();
        protected ArrayList follow = new ArrayList();

        public String getDefaultName() {
            InverseStore inverseStore = this;
            Object e;
            while (!((e = inverseStore.follow.get(0)) instanceof String)) {
                inverseStore = (InverseStore)e;
            }
            return (String)e;
        }

        public boolean isSimilar(String string) {
            int n = string.indexOf(91);
            string = string.substring(0, n + 1);
            for (int i = 0; i < this.part.size(); ++i) {
                if (!((String)this.part.get(i)).startsWith(string)) continue;
                return true;
            }
            return false;
        }
    }

}

