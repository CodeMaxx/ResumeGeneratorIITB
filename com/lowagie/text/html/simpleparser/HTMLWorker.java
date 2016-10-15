/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html.simpleparser;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactoryImp;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.html.Markup;
import com.lowagie.text.html.simpleparser.ALink;
import com.lowagie.text.html.simpleparser.ChainedProperties;
import com.lowagie.text.html.simpleparser.FactoryProperties;
import com.lowagie.text.html.simpleparser.ImageProvider;
import com.lowagie.text.html.simpleparser.Img;
import com.lowagie.text.html.simpleparser.IncCell;
import com.lowagie.text.html.simpleparser.IncTable;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

public class HTMLWorker
implements SimpleXMLDocHandler,
DocListener {
    protected ArrayList objectList;
    protected DocListener document;
    private Paragraph currentParagraph;
    private ChainedProperties cprops = new ChainedProperties();
    private Stack stack = new Stack();
    private boolean pendingTR = false;
    private boolean pendingTD = false;
    private boolean pendingLI = false;
    private StyleSheet style = new StyleSheet();
    private boolean isPRE = false;
    private Stack tableState = new Stack();
    private boolean skipText = false;
    private HashMap interfaceProps;
    private FactoryProperties factoryProperties = new FactoryProperties();
    public static final String tagsSupportedString = "ol ul li a pre font span br p div body table td th tr i b u sub sup em strong s strike h1 h2 h3 h4 h5 h6 img";
    public static final HashMap tagsSupported = new HashMap();

    public HTMLWorker(DocListener docListener) {
        this.document = docListener;
    }

    public void setStyleSheet(StyleSheet styleSheet) {
        this.style = styleSheet;
    }

    public StyleSheet getStyleSheet() {
        return this.style;
    }

    public void setInterfaceProps(HashMap hashMap) {
        this.interfaceProps = hashMap;
        FontFactoryImp fontFactoryImp = null;
        if (hashMap != null) {
            fontFactoryImp = (FontFactoryImp)hashMap.get("font_factory");
        }
        if (fontFactoryImp != null) {
            this.factoryProperties.setFontImp(fontFactoryImp);
        }
    }

    public HashMap getInterfaceProps() {
        return this.interfaceProps;
    }

    public void parse(Reader reader) throws IOException {
        SimpleXMLParser.parse(this, null, reader, true);
    }

    public static ArrayList parseToList(Reader reader, StyleSheet styleSheet) throws IOException {
        return HTMLWorker.parseToList(reader, styleSheet, null);
    }

    public static ArrayList parseToList(Reader reader, StyleSheet styleSheet, HashMap hashMap) throws IOException {
        HTMLWorker hTMLWorker = new HTMLWorker(null);
        if (styleSheet != null) {
            hTMLWorker.style = styleSheet;
        }
        hTMLWorker.document = hTMLWorker;
        hTMLWorker.setInterfaceProps(hashMap);
        hTMLWorker.objectList = new ArrayList();
        hTMLWorker.parse(reader);
        return hTMLWorker.objectList;
    }

    public void endDocument() {
        try {
            for (int i = 0; i < this.stack.size(); ++i) {
                this.document.add((Element)this.stack.elementAt(i));
            }
            if (this.currentParagraph != null) {
                this.document.add(this.currentParagraph);
            }
            this.currentParagraph = null;
        }
        catch (Exception var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    public void startDocument() {
        HashMap hashMap = new HashMap();
        this.style.applyStyle("body", hashMap);
        this.cprops.addToChain("body", hashMap);
    }

    public void startElement(String string, HashMap hashMap) {
        if (!tagsSupported.containsKey(string)) {
            return;
        }
        try {
            this.style.applyStyle(string, hashMap);
            String string2 = (String)FactoryProperties.followTags.get(string);
            if (string2 != null) {
                HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
                hashMap2.put(string2, null);
                this.cprops.addToChain(string2, hashMap2);
                return;
            }
            FactoryProperties.insertStyle(hashMap, this.cprops);
            if (string.equals("a")) {
                this.cprops.addToChain(string, hashMap);
                if (this.currentParagraph == null) {
                    this.currentParagraph = new Paragraph();
                }
                this.stack.push(this.currentParagraph);
                this.currentParagraph = new Paragraph();
                return;
            }
            if (string.equals("br")) {
                if (this.currentParagraph == null) {
                    this.currentParagraph = new Paragraph();
                }
                this.currentParagraph.add(this.factoryProperties.createChunk("\n", this.cprops));
                return;
            }
            if (string.equals("font") || string.equals("span")) {
                this.cprops.addToChain(string, hashMap);
                return;
            }
            if (string.equals("img")) {
                Object object;
                Object object2;
                Object object3;
                float f;
                String string3 = (String)hashMap.get("src");
                if (string3 == null) {
                    return;
                }
                this.cprops.addToChain(string, hashMap);
                Image image = null;
                if (this.interfaceProps != null) {
                    object3 = (ImageProvider)this.interfaceProps.get("img_provider");
                    if (object3 != null) {
                        image = object3.getImage(string3, hashMap, this.cprops, this.document);
                    }
                    if (image == null) {
                        object2 = (HashMap)this.interfaceProps.get("img_static");
                        if (object2 != null) {
                            object = (Image)object2.get(string3);
                            if (object != null) {
                                image = Image.getInstance((Image)object);
                            }
                        } else if (!string3.startsWith("http") && (object = (String)this.interfaceProps.get("img_baseurl")) != null) {
                            string3 = (String)object + string3;
                            image = Image.getInstance(string3);
                        }
                    }
                }
                if (image == null) {
                    if (!string3.startsWith("http")) {
                        object3 = this.cprops.getProperty("image_path");
                        if (object3 == null) {
                            object3 = "";
                        }
                        string3 = new File((String)object3, string3).getPath();
                    }
                    image = Image.getInstance(string3);
                }
                object3 = (String)hashMap.get("align");
                object2 = (String)hashMap.get("width");
                object = (String)hashMap.get("height");
                String string4 = this.cprops.getProperty("before");
                String string5 = this.cprops.getProperty("after");
                if (string4 != null) {
                    image.setSpacingBefore(Float.parseFloat(string4));
                }
                if (string5 != null) {
                    image.setSpacingAfter(Float.parseFloat(string5));
                }
                if ((f = Markup.parseLength(this.cprops.getProperty("size"), 12.0f)) <= 0.0f) {
                    f = 12.0f;
                }
                float f2 = Markup.parseLength((String)object2, f);
                float f3 = Markup.parseLength((String)object, f);
                if (f2 > 0.0f && f3 > 0.0f) {
                    image.scaleAbsolute(f2, f3);
                } else if (f2 > 0.0f) {
                    f3 = image.getHeight() * f2 / image.getWidth();
                    image.scaleAbsolute(f2, f3);
                } else if (f3 > 0.0f) {
                    f2 = image.getWidth() * f3 / image.getHeight();
                    image.scaleAbsolute(f2, f3);
                }
                image.setWidthPercentage(0.0f);
                if (object3 != null) {
                    this.endElement("p");
                    int n = 1;
                    if (object3.equalsIgnoreCase("left")) {
                        n = 0;
                    } else if (object3.equalsIgnoreCase("right")) {
                        n = 2;
                    }
                    image.setAlignment(n);
                    Img img = null;
                    boolean bl = false;
                    if (this.interfaceProps != null && (img = (Img)this.interfaceProps.get("img_interface")) != null) {
                        bl = img.process(image, hashMap, this.cprops, this.document);
                    }
                    if (!bl) {
                        this.document.add(image);
                    }
                    this.cprops.removeChain(string);
                } else {
                    this.cprops.removeChain(string);
                    if (this.currentParagraph == null) {
                        this.currentParagraph = FactoryProperties.createParagraph(this.cprops);
                    }
                    this.currentParagraph.add(new Chunk(image, 0.0f, 0.0f));
                }
                return;
            }
            this.endElement("p");
            if (string.equals("h1") || string.equals("h2") || string.equals("h3") || string.equals("h4") || string.equals("h5") || string.equals("h6")) {
                if (!hashMap.containsKey("size")) {
                    int n = 7 - Integer.parseInt(string.substring(1));
                    hashMap.put("size", Integer.toString(n));
                }
                this.cprops.addToChain(string, hashMap);
                return;
            }
            if (string.equals("ul")) {
                if (this.pendingLI) {
                    this.endElement("li");
                }
                this.skipText = true;
                this.cprops.addToChain(string, hashMap);
                List list = new List(false, 10.0f);
                list.setListSymbol("\u2022");
                this.stack.push(list);
                return;
            }
            if (string.equals("ol")) {
                if (this.pendingLI) {
                    this.endElement("li");
                }
                this.skipText = true;
                this.cprops.addToChain(string, hashMap);
                List list = new List(true, 10.0f);
                this.stack.push(list);
                return;
            }
            if (string.equals("li")) {
                if (this.pendingLI) {
                    this.endElement("li");
                }
                this.skipText = false;
                this.pendingLI = true;
                this.cprops.addToChain(string, hashMap);
                ListItem listItem = FactoryProperties.createListItem(this.cprops);
                this.stack.push(listItem);
                return;
            }
            if (string.equals("div") || string.equals("body")) {
                this.cprops.addToChain(string, hashMap);
                return;
            }
            if (string.equals("pre")) {
                if (!hashMap.containsKey("face")) {
                    hashMap.put("face", "Courier");
                }
                this.cprops.addToChain(string, hashMap);
                this.isPRE = true;
                return;
            }
            if (string.equals("p")) {
                this.cprops.addToChain(string, hashMap);
                this.currentParagraph = FactoryProperties.createParagraph(hashMap);
                return;
            }
            if (string.equals("tr")) {
                if (this.pendingTR) {
                    this.endElement("tr");
                }
                this.skipText = true;
                this.pendingTR = true;
                this.cprops.addToChain("tr", hashMap);
                return;
            }
            if (string.equals("td") || string.equals("th")) {
                if (this.pendingTD) {
                    this.endElement(string);
                }
                this.skipText = false;
                this.pendingTD = true;
                this.cprops.addToChain("td", hashMap);
                this.stack.push(new IncCell(string, this.cprops));
                return;
            }
            if (string.equals("table")) {
                this.cprops.addToChain("table", hashMap);
                IncTable incTable = new IncTable(hashMap);
                this.stack.push(incTable);
                this.tableState.push(new boolean[]{this.pendingTR, this.pendingTD});
                this.pendingTD = false;
                this.pendingTR = false;
                this.skipText = true;
                return;
            }
        }
        catch (Exception var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    public void endElement(String string) {
        if (!tagsSupported.containsKey(string)) {
            return;
        }
        try {
            Object object;
            String string2 = (String)FactoryProperties.followTags.get(string);
            if (string2 != null) {
                this.cprops.removeChain(string2);
                return;
            }
            if (string.equals("font") || string.equals("span")) {
                this.cprops.removeChain(string);
                return;
            }
            if (string.equals("a")) {
                Object object2;
                ArrayList arrayList;
                if (this.currentParagraph == null) {
                    this.currentParagraph = new Paragraph();
                }
                boolean bl = false;
                if (this.interfaceProps != null && (object2 = (ALink)this.interfaceProps.get("alink_interface")) != null) {
                    bl = object2.process(this.currentParagraph, this.cprops);
                }
                if (!bl && (object2 = this.cprops.getProperty("href")) != null) {
                    arrayList = this.currentParagraph.getChunks();
                    int n = arrayList.size();
                    for (int i = 0; i < n; ++i) {
                        Chunk chunk = (Chunk)arrayList.get(i);
                        chunk.setAnchor((String)object2);
                    }
                }
                object2 = (Paragraph)this.stack.pop();
                arrayList = new Phrase();
                arrayList.add(this.currentParagraph);
                object2.add(arrayList);
                this.currentParagraph = object2;
                this.cprops.removeChain("a");
                return;
            }
            if (string.equals("br")) {
                return;
            }
            if (this.currentParagraph != null) {
                if (this.stack.empty()) {
                    this.document.add(this.currentParagraph);
                } else {
                    Object arrayList = this.stack.pop();
                    if (arrayList instanceof TextElementArray) {
                        object = (TextElementArray)arrayList;
                        object.add(this.currentParagraph);
                    }
                    this.stack.push(arrayList);
                }
            }
            this.currentParagraph = null;
            if (string.equals("ul") || string.equals("ol")) {
                if (this.pendingLI) {
                    this.endElement("li");
                }
                this.skipText = false;
                this.cprops.removeChain(string);
                if (this.stack.empty()) {
                    return;
                }
                Object e = this.stack.pop();
                if (!(e instanceof List)) {
                    this.stack.push(e);
                    return;
                }
                if (this.stack.empty()) {
                    this.document.add((Element)e);
                } else {
                    ((TextElementArray)this.stack.peek()).add(e);
                }
                return;
            }
            if (string.equals("li")) {
                this.pendingLI = false;
                this.skipText = true;
                this.cprops.removeChain(string);
                if (this.stack.empty()) {
                    return;
                }
                Object e = this.stack.pop();
                if (!(e instanceof ListItem)) {
                    this.stack.push(e);
                    return;
                }
                if (this.stack.empty()) {
                    this.document.add((Element)e);
                    return;
                }
                object = this.stack.pop();
                if (!(object instanceof List)) {
                    this.stack.push(object);
                    return;
                }
                ListItem listItem = (ListItem)e;
                ((List)object).add(listItem);
                ArrayList arrayList = listItem.getChunks();
                if (!arrayList.isEmpty()) {
                    listItem.getListSymbol().setFont(((Chunk)arrayList.get(0)).getFont());
                }
                this.stack.push(object);
                return;
            }
            if (string.equals("div") || string.equals("body")) {
                this.cprops.removeChain(string);
                return;
            }
            if (string.equals("pre")) {
                this.cprops.removeChain(string);
                this.isPRE = false;
                return;
            }
            if (string.equals("p")) {
                this.cprops.removeChain(string);
                return;
            }
            if (string.equals("h1") || string.equals("h2") || string.equals("h3") || string.equals("h4") || string.equals("h5") || string.equals("h6")) {
                this.cprops.removeChain(string);
                return;
            }
            if (string.equals("table")) {
                if (this.pendingTR) {
                    this.endElement("tr");
                }
                this.cprops.removeChain("table");
                IncTable incTable = (IncTable)this.stack.pop();
                object = incTable.buildTable();
                object.setSplitRows(true);
                if (this.stack.empty()) {
                    this.document.add((Element)object);
                } else {
                    ((TextElementArray)this.stack.peek()).add(object);
                }
                boolean[] arrbl = (boolean[])this.tableState.pop();
                this.pendingTR = arrbl[0];
                this.pendingTD = arrbl[1];
                this.skipText = false;
                return;
            }
            if (string.equals("tr")) {
                Object e;
                if (this.pendingTD) {
                    this.endElement("td");
                }
                this.pendingTR = false;
                this.cprops.removeChain("tr");
                ArrayList<PdfPCell> arrayList = new ArrayList<PdfPCell>();
                object = null;
                do {
                    if (!((e = this.stack.pop()) instanceof IncCell)) continue;
                    arrayList.add(((IncCell)e).getCell());
                } while (!(e instanceof IncTable));
                object = (IncTable)e;
                object.addCols(arrayList);
                object.endRow();
                this.stack.push(object);
                this.skipText = true;
                return;
            }
            if (string.equals("td") || string.equals("th")) {
                this.pendingTD = false;
                this.cprops.removeChain("td");
                this.skipText = true;
                return;
            }
        }
        catch (Exception var2_3) {
            throw new ExceptionConverter(var2_3);
        }
    }

    public void text(String string) {
        if (this.skipText) {
            return;
        }
        String string2 = string;
        if (this.isPRE) {
            if (this.currentParagraph == null) {
                this.currentParagraph = FactoryProperties.createParagraph(this.cprops);
            }
            Chunk chunk = this.factoryProperties.createChunk(string2, this.cprops);
            this.currentParagraph.add(chunk);
            return;
        }
        if (string2.trim().length() == 0 && string2.indexOf(32) < 0) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        int n = string2.length();
        boolean bl = false;
        block6 : for (int i = 0; i < n; ++i) {
            char c = string2.charAt(i);
            switch (c) {
                case ' ': {
                    if (bl) continue block6;
                    stringBuffer.append(c);
                    continue block6;
                }
                case '\n': {
                    if (i <= 0) continue block6;
                    bl = true;
                    stringBuffer.append(' ');
                    continue block6;
                }
                case '\r': {
                    continue block6;
                }
                case '\t': {
                    continue block6;
                }
                default: {
                    bl = false;
                    stringBuffer.append(c);
                }
            }
        }
        if (this.currentParagraph == null) {
            this.currentParagraph = FactoryProperties.createParagraph(this.cprops);
        }
        Chunk chunk = this.factoryProperties.createChunk(stringBuffer.toString(), this.cprops);
        this.currentParagraph.add(chunk);
    }

    public boolean add(Element element) throws DocumentException {
        this.objectList.add(element);
        return true;
    }

    public void clearTextWrap() throws DocumentException {
    }

    public void close() {
    }

    public boolean newPage() {
        return true;
    }

    public void open() {
    }

    public void resetFooter() {
    }

    public void resetHeader() {
    }

    public void resetPageCount() {
    }

    public void setFooter(HeaderFooter headerFooter) {
    }

    public void setHeader(HeaderFooter headerFooter) {
    }

    public boolean setMarginMirroring(boolean bl) {
        return true;
    }

    public boolean setMargins(float f, float f2, float f3, float f4) {
        return true;
    }

    public void setPageCount(int n) {
    }

    public boolean setPageSize(Rectangle rectangle) {
        return true;
    }

    private static float lengthParse(String string, int n) {
        if (string == null) {
            return -1.0f;
        }
        if (string.endsWith("%")) {
            float f = Float.parseFloat(string.substring(0, string.length() - 1));
            return f;
        }
        if (string.endsWith("px")) {
            float f = Float.parseFloat(string.substring(0, string.length() - 2));
            return f;
        }
        int n2 = Integer.parseInt(string);
        return (float)n2 / (float)n * 100.0f;
    }

    static {
        StringTokenizer stringTokenizer = new StringTokenizer("ol ul li a pre font span br p div body table td th tr i b u sub sup em strong s strike h1 h2 h3 h4 h5 h6 img");
        while (stringTokenizer.hasMoreTokens()) {
            tagsSupported.put(stringTokenizer.nextToken(), null);
        }
    }
}

