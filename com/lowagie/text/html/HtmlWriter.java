/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Header;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.MarkedObject;
import com.lowagie.text.MarkedSection;
import com.lowagie.text.Meta;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Row;
import com.lowagie.text.Section;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.Table;
import com.lowagie.text.html.HtmlEncoder;
import com.lowagie.text.html.HtmlTags;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.OutputStreamCounter;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Stack;

public class HtmlWriter
extends DocWriter {
    public static final byte[] BEGINCOMMENT = HtmlWriter.getISOBytes("<!-- ");
    public static final byte[] ENDCOMMENT = HtmlWriter.getISOBytes(" -->");
    public static final String NBSP = "&nbsp;";
    protected Stack currentfont = new Stack();
    protected Font standardfont = new Font();
    protected String imagepath = null;
    protected int pageN = 0;
    protected HeaderFooter header = null;
    protected HeaderFooter footer = null;
    protected Properties markup = new Properties();

    protected HtmlWriter(Document document, OutputStream outputStream) {
        super(document, outputStream);
        this.document.addDocListener(this);
        this.pageN = this.document.getPageNumber();
        try {
            outputStream.write(60);
            outputStream.write(HtmlWriter.getISOBytes("html"));
            outputStream.write(62);
            outputStream.write(10);
            outputStream.write(9);
            outputStream.write(60);
            outputStream.write(HtmlWriter.getISOBytes("head"));
            outputStream.write(62);
        }
        catch (IOException var3_3) {
            throw new ExceptionConverter(var3_3);
        }
    }

    public static HtmlWriter getInstance(Document document, OutputStream outputStream) {
        return new HtmlWriter(document, outputStream);
    }

    public boolean newPage() {
        try {
            this.writeStart("div");
            this.write(" ");
            this.write("style");
            this.write("=\"");
            this.writeCssProperty("page-break-before", "always");
            this.write("\" /");
            this.os.write(62);
        }
        catch (IOException var1_1) {
            throw new ExceptionConverter(var1_1);
        }
        return true;
    }

    public boolean add(Element element) throws DocumentException {
        if (this.pause) {
            return false;
        }
        if (this.open && !element.isContent()) {
            throw new DocumentException("The document is open; you can only add Elements with content.");
        }
        try {
            switch (element.type()) {
                case 0: {
                    try {
                        Header header = (Header)element;
                        if ("stylesheet".equals(header.getName())) {
                            this.writeLink(header);
                        } else if ("JavaScript".equals(header.getName())) {
                            this.writeJavaScript(header);
                        } else {
                            this.writeHeader(header);
                        }
                    }
                    catch (ClassCastException var2_3) {
                        // empty catch block
                    }
                    return true;
                }
                case 2: 
                case 3: 
                case 4: {
                    Meta meta = (Meta)element;
                    this.writeHeader(meta);
                    return true;
                }
                case 1: {
                    this.addTabs(2);
                    this.writeStart("title");
                    this.os.write(62);
                    this.addTabs(3);
                    this.write(HtmlEncoder.encode(((Meta)element).getContent()));
                    this.addTabs(2);
                    this.writeEnd("title");
                    return true;
                }
                case 7: {
                    this.writeComment("Creator: " + HtmlEncoder.encode(((Meta)element).getContent()));
                    return true;
                }
                case 5: {
                    this.writeComment("Producer: " + HtmlEncoder.encode(((Meta)element).getContent()));
                    return true;
                }
                case 6: {
                    this.writeComment("Creationdate: " + HtmlEncoder.encode(((Meta)element).getContent()));
                    return true;
                }
                case 50: {
                    if (element instanceof MarkedSection) {
                        MarkedSection markedSection = (MarkedSection)element;
                        this.addTabs(1);
                        this.writeStart("div");
                        this.writeMarkupAttributes(markedSection.getMarkupAttributes());
                        this.os.write(62);
                        MarkedObject markedObject = ((MarkedSection)element).getTitle();
                        if (markedObject != null) {
                            this.markup = markedObject.getMarkupAttributes();
                            markedObject.process(this);
                        }
                        markedSection.process(this);
                        this.writeEnd("div");
                        return true;
                    }
                    MarkedObject markedObject = (MarkedObject)element;
                    this.markup = markedObject.getMarkupAttributes();
                    return markedObject.process(this);
                }
            }
            this.write(element, 2);
            return true;
        }
        catch (IOException var2_5) {
            throw new ExceptionConverter(var2_5);
        }
    }

    public void open() {
        super.open();
        try {
            this.writeComment(Document.getVersion());
            this.writeComment("CreationDate: " + new Date().toString());
            this.addTabs(1);
            this.writeEnd("head");
            this.addTabs(1);
            this.writeStart("body");
            if (this.document.leftMargin() > 0.0f) {
                this.write("leftmargin", String.valueOf(this.document.leftMargin()));
            }
            if (this.document.rightMargin() > 0.0f) {
                this.write("rightmargin", String.valueOf(this.document.rightMargin()));
            }
            if (this.document.topMargin() > 0.0f) {
                this.write("topmargin", String.valueOf(this.document.topMargin()));
            }
            if (this.document.bottomMargin() > 0.0f) {
                this.write("bottommargin", String.valueOf(this.document.bottomMargin()));
            }
            if (this.pageSize.getBackgroundColor() != null) {
                this.write("bgcolor", HtmlEncoder.encode(this.pageSize.getBackgroundColor()));
            }
            if (this.document.getJavaScript_onLoad() != null) {
                this.write("onLoad", HtmlEncoder.encode(this.document.getJavaScript_onLoad()));
            }
            if (this.document.getJavaScript_onUnLoad() != null) {
                this.write("onUnLoad", HtmlEncoder.encode(this.document.getJavaScript_onUnLoad()));
            }
            if (this.document.getHtmlStyleClass() != null) {
                this.write("class", this.document.getHtmlStyleClass());
            }
            this.os.write(62);
            this.initHeader();
        }
        catch (IOException var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    public void close() {
        try {
            this.initFooter();
            this.addTabs(1);
            this.writeEnd("body");
            this.os.write(10);
            this.writeEnd("html");
            super.close();
        }
        catch (IOException var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    protected void initHeader() {
        if (this.header != null) {
            try {
                this.add(this.header.paragraph());
            }
            catch (Exception var1_1) {
                throw new ExceptionConverter(var1_1);
            }
        }
    }

    protected void initFooter() {
        if (this.footer != null) {
            try {
                this.footer.setPageNumber(this.pageN + 1);
                this.add(this.footer.paragraph());
            }
            catch (Exception var1_1) {
                throw new ExceptionConverter(var1_1);
            }
        }
    }

    protected void writeHeader(Meta meta) throws IOException {
        this.addTabs(2);
        this.writeStart("meta");
        switch (meta.type()) {
            case 0: {
                this.write("name", ((Header)meta).getName());
                break;
            }
            case 2: {
                this.write("name", "subject");
                break;
            }
            case 3: {
                this.write("name", "keywords");
                break;
            }
            case 4: {
                this.write("name", "author");
            }
        }
        this.write("content", HtmlEncoder.encode(meta.getContent()));
        this.writeEnd();
    }

    protected void writeLink(Header header) throws IOException {
        this.addTabs(2);
        this.writeStart("link");
        this.write("rel", header.getName());
        this.write("type", "text/css");
        this.write("href", header.getContent());
        this.writeEnd();
    }

    protected void writeJavaScript(Header header) throws IOException {
        this.addTabs(2);
        this.writeStart("script");
        this.write("language", "JavaScript");
        if (this.markup.size() > 0) {
            this.writeMarkupAttributes(this.markup);
            this.os.write(62);
            this.writeEnd("script");
        } else {
            this.write("type", "text/javascript");
            this.os.write(62);
            this.addTabs(2);
            this.write(new String(BEGINCOMMENT) + "\n");
            this.write(header.getContent());
            this.addTabs(2);
            this.write("//" + new String(ENDCOMMENT));
            this.addTabs(2);
            this.writeEnd("script");
        }
    }

    protected void writeComment(String string) throws IOException {
        this.addTabs(2);
        this.os.write(BEGINCOMMENT);
        this.write(string);
        this.os.write(ENDCOMMENT);
    }

    public void setStandardFont(Font font) {
        this.standardfont = font;
    }

    public boolean isOtherFont(Font font) {
        try {
            Font font2 = (Font)this.currentfont.peek();
            if (font2.compareTo(font) == 0) {
                return false;
            }
            return true;
        }
        catch (EmptyStackException var2_3) {
            if (this.standardfont.compareTo(font) == 0) {
                return false;
            }
            return true;
        }
    }

    public void setImagepath(String string) {
        this.imagepath = string;
    }

    public void resetImagepath() {
        this.imagepath = null;
    }

    public void setHeader(HeaderFooter headerFooter) {
        this.header = headerFooter;
    }

    public void setFooter(HeaderFooter headerFooter) {
        this.footer = headerFooter;
    }

    public boolean add(String string) {
        if (this.pause) {
            return false;
        }
        try {
            this.write(string);
            return true;
        }
        catch (IOException var2_2) {
            throw new ExceptionConverter(var2_2);
        }
    }

    protected void write(Element element, int n) throws IOException {
        Properties properties = null;
        switch (element.type()) {
            case 50: {
                try {
                    this.add(element);
                }
                catch (DocumentException var4_4) {
                    var4_4.printStackTrace();
                }
                return;
            }
            case 10: {
                boolean bl;
                Chunk chunk = (Chunk)element;
                Image image = chunk.getImage();
                if (image != null) {
                    this.write(image, n);
                    return;
                }
                if (chunk.isEmpty()) {
                    return;
                }
                HashMap hashMap = chunk.getAttributes();
                if (hashMap != null && hashMap.get("NEWPAGE") != null) {
                    return;
                }
                boolean bl2 = bl = this.isOtherFont(chunk.getFont()) || this.markup.size() > 0;
                if (bl) {
                    this.addTabs(n);
                    this.writeStart("span");
                    if (this.isOtherFont(chunk.getFont())) {
                        this.write(chunk.getFont(), null);
                    }
                    this.writeMarkupAttributes(this.markup);
                    this.os.write(62);
                }
                if (hashMap != null && hashMap.get("SUBSUPSCRIPT") != null) {
                    if (((Float)hashMap.get("SUBSUPSCRIPT")).floatValue() > 0.0f) {
                        this.writeStart("sup");
                    } else {
                        this.writeStart("sub");
                    }
                    this.os.write(62);
                }
                this.write(HtmlEncoder.encode(chunk.getContent()));
                if (hashMap != null && hashMap.get("SUBSUPSCRIPT") != null) {
                    this.os.write(60);
                    this.os.write(47);
                    if (((Float)hashMap.get("SUBSUPSCRIPT")).floatValue() > 0.0f) {
                        this.write("sup");
                    } else {
                        this.write("sub");
                    }
                    this.os.write(62);
                }
                if (bl) {
                    this.writeEnd("span");
                }
                return;
            }
            case 11: {
                Phrase phrase = (Phrase)element;
                properties = new Properties();
                if (phrase.hasLeading()) {
                    properties.setProperty("line-height", "" + phrase.getLeading() + "pt");
                }
                this.addTabs(n);
                this.writeStart("span");
                this.writeMarkupAttributes(this.markup);
                this.write(phrase.getFont(), properties);
                this.os.write(62);
                this.currentfont.push(phrase.getFont());
                Iterator iterator = phrase.iterator();
                while (iterator.hasNext()) {
                    this.write((Element)iterator.next(), n + 1);
                }
                this.addTabs(n);
                this.writeEnd("span");
                this.currentfont.pop();
                return;
            }
            case 17: {
                Anchor anchor = (Anchor)element;
                properties = new Properties();
                if (anchor.hasLeading()) {
                    properties.setProperty("line-height", "" + anchor.getLeading() + "pt");
                }
                this.addTabs(n);
                this.writeStart("a");
                if (anchor.getName() != null) {
                    this.write("name", anchor.getName());
                }
                if (anchor.getReference() != null) {
                    this.write("href", anchor.getReference());
                }
                this.writeMarkupAttributes(this.markup);
                this.write(anchor.getFont(), properties);
                this.os.write(62);
                this.currentfont.push(anchor.getFont());
                Iterator iterator = anchor.iterator();
                while (iterator.hasNext()) {
                    this.write((Element)iterator.next(), n + 1);
                }
                this.addTabs(n);
                this.writeEnd("a");
                this.currentfont.pop();
                return;
            }
            case 12: {
                Paragraph paragraph = (Paragraph)element;
                properties = new Properties();
                if (paragraph.hasLeading()) {
                    properties.setProperty("line-height", "" + paragraph.getTotalLeading() + "pt");
                }
                this.addTabs(n);
                this.writeStart("div");
                this.writeMarkupAttributes(this.markup);
                String string = HtmlEncoder.getAlignment(paragraph.getAlignment());
                if (!"".equals(string)) {
                    this.write("align", string);
                }
                this.write(paragraph.getFont(), properties);
                this.os.write(62);
                this.currentfont.push(paragraph.getFont());
                Iterator iterator = paragraph.iterator();
                while (iterator.hasNext()) {
                    this.write((Element)iterator.next(), n + 1);
                }
                this.addTabs(n);
                this.writeEnd("div");
                this.currentfont.pop();
                return;
            }
            case 13: 
            case 16: {
                this.writeSection((Section)element, n);
                return;
            }
            case 14: {
                List list = (List)element;
                this.addTabs(n);
                if (list.isNumbered()) {
                    this.writeStart("ol");
                } else {
                    this.writeStart("ul");
                }
                this.writeMarkupAttributes(this.markup);
                this.os.write(62);
                Iterator iterator = list.getItems().iterator();
                while (iterator.hasNext()) {
                    this.write((Element)iterator.next(), n + 1);
                }
                this.addTabs(n);
                if (list.isNumbered()) {
                    this.writeEnd("ol");
                } else {
                    this.writeEnd("ul");
                }
                return;
            }
            case 15: {
                ListItem listItem = (ListItem)element;
                properties = new Properties();
                if (listItem.hasLeading()) {
                    properties.setProperty("line-height", "" + listItem.getTotalLeading() + "pt");
                }
                this.addTabs(n);
                this.writeStart("li");
                this.writeMarkupAttributes(this.markup);
                this.write(listItem.getFont(), properties);
                this.os.write(62);
                this.currentfont.push(listItem.getFont());
                Iterator iterator = listItem.iterator();
                while (iterator.hasNext()) {
                    this.write((Element)iterator.next(), n + 1);
                }
                this.addTabs(n);
                this.writeEnd("li");
                this.currentfont.pop();
                return;
            }
            case 20: {
                String string;
                Cell cell = (Cell)element;
                this.addTabs(n);
                if (cell.isHeader()) {
                    this.writeStart("th");
                } else {
                    this.writeStart("td");
                }
                this.writeMarkupAttributes(this.markup);
                if (cell.getBorderWidth() != -1.0f) {
                    this.write("border", String.valueOf(cell.getBorderWidth()));
                }
                if (cell.getBorderColor() != null) {
                    this.write("bordercolor", HtmlEncoder.encode(cell.getBorderColor()));
                }
                if (cell.getBackgroundColor() != null) {
                    this.write("bgcolor", HtmlEncoder.encode(cell.getBackgroundColor()));
                }
                if (!"".equals(string = HtmlEncoder.getAlignment(cell.getHorizontalAlignment()))) {
                    this.write("align", string);
                }
                if (!"".equals(string = HtmlEncoder.getAlignment(cell.getVerticalAlignment()))) {
                    this.write("valign", string);
                }
                if (cell.getWidthAsString() != null) {
                    this.write("width", cell.getWidthAsString());
                }
                if (cell.getColspan() != 1) {
                    this.write("colspan", String.valueOf(cell.getColspan()));
                }
                if (cell.getRowspan() != 1) {
                    this.write("rowspan", String.valueOf(cell.getRowspan()));
                }
                if (cell.getMaxLines() == 1) {
                    this.write("style", "white-space: nowrap;");
                }
                this.os.write(62);
                if (cell.isEmpty()) {
                    this.write("&nbsp;");
                } else {
                    Iterator iterator = cell.getElements();
                    while (iterator.hasNext()) {
                        this.write((Element)iterator.next(), n + 1);
                    }
                }
                this.addTabs(n);
                if (cell.isHeader()) {
                    this.writeEnd("th");
                } else {
                    this.writeEnd("td");
                }
                return;
            }
            case 21: {
                Row row = (Row)element;
                this.addTabs(n);
                this.writeStart("tr");
                this.writeMarkupAttributes(this.markup);
                this.os.write(62);
                for (int i = 0; i < row.getColumns(); ++i) {
                    Element element2 = (Element)row.getCell(i);
                    if (element2 == null) continue;
                    this.write(element2, n + 1);
                }
                this.addTabs(n);
                this.writeEnd("tr");
                return;
            }
            case 22: {
                Table table;
                try {
                    table = (Table)element;
                }
                catch (ClassCastException var5_24) {
                    try {
                        table = ((SimpleTable)element).createTable();
                    }
                    catch (BadElementException var6_31) {
                        throw new ExceptionConverter(var6_31);
                    }
                }
                table.complete();
                this.addTabs(n);
                this.writeStart("table");
                this.writeMarkupAttributes(this.markup);
                this.os.write(32);
                this.write("width");
                this.os.write(61);
                this.os.write(34);
                this.write(String.valueOf(table.getWidth()));
                if (!table.isLocked()) {
                    this.write("%");
                }
                this.os.write(34);
                String string = HtmlEncoder.getAlignment(table.getAlignment());
                if (!"".equals(string)) {
                    this.write("align", string);
                }
                this.write("cellpadding", String.valueOf(table.getPadding()));
                this.write("cellspacing", String.valueOf(table.getSpacing()));
                if (table.getBorderWidth() != -1.0f) {
                    this.write("border", String.valueOf(table.getBorderWidth()));
                }
                if (table.getBorderColor() != null) {
                    this.write("bordercolor", HtmlEncoder.encode(table.getBorderColor()));
                }
                if (table.getBackgroundColor() != null) {
                    this.write("bgcolor", HtmlEncoder.encode(table.getBackgroundColor()));
                }
                this.os.write(62);
                Iterator iterator = table.iterator();
                while (iterator.hasNext()) {
                    Row row = (Row)iterator.next();
                    this.write(row, n + 1);
                }
                this.addTabs(n);
                this.writeEnd("table");
                return;
            }
            case 29: {
                Annotation annotation = (Annotation)element;
                this.writeComment(annotation.title() + ": " + annotation.content());
                return;
            }
            case 32: 
            case 33: 
            case 34: 
            case 35: {
                Image image = (Image)element;
                if (image.getUrl() == null) {
                    return;
                }
                this.addTabs(n);
                this.writeStart("img");
                String string = image.getUrl().toString();
                if (this.imagepath != null) {
                    string = string.indexOf(47) > 0 ? this.imagepath + string.substring(string.lastIndexOf(47) + 1) : this.imagepath + string;
                }
                this.write("src", string);
                if ((image.getAlignment() & 2) > 0) {
                    this.write("align", "Right");
                } else if ((image.getAlignment() & 1) > 0) {
                    this.write("align", "Middle");
                } else {
                    this.write("align", "Left");
                }
                if (image.getAlt() != null) {
                    this.write("alt", image.getAlt());
                }
                this.write("width", String.valueOf(image.getScaledWidth()));
                this.write("height", String.valueOf(image.getScaledHeight()));
                this.writeMarkupAttributes(this.markup);
                this.writeEnd();
                return;
            }
        }
    }

    protected void writeSection(Section section, int n) throws IOException {
        if (section.getTitle() != null) {
            int n2 = section.getDepth() - 1;
            if (n2 > 5) {
                n2 = 5;
            }
            Properties properties = new Properties();
            if (section.getTitle().hasLeading()) {
                properties.setProperty("line-height", "" + section.getTitle().getTotalLeading() + "pt");
            }
            this.addTabs(n);
            this.writeStart(HtmlTags.H[n2]);
            this.write(section.getTitle().getFont(), properties);
            String string = HtmlEncoder.getAlignment(section.getTitle().getAlignment());
            if (!"".equals(string)) {
                this.write("align", string);
            }
            this.writeMarkupAttributes(this.markup);
            this.os.write(62);
            this.currentfont.push(section.getTitle().getFont());
            Iterator iterator = section.getTitle().iterator();
            while (iterator.hasNext()) {
                this.write((Element)iterator.next(), n + 1);
            }
            this.addTabs(n);
            this.writeEnd(HtmlTags.H[n2]);
            this.currentfont.pop();
        }
        Iterator iterator = section.iterator();
        while (iterator.hasNext()) {
            this.write((Element)iterator.next(), n);
        }
    }

    protected void write(Font font, Properties properties) throws IOException {
        Object object;
        if (font == null || !this.isOtherFont(font)) {
            return;
        }
        this.write(" ");
        this.write("style");
        this.write("=\"");
        if (properties != null) {
            object = properties.propertyNames();
            while (object.hasMoreElements()) {
                String string = (String)object.nextElement();
                this.writeCssProperty(string, properties.getProperty(string));
            }
        }
        if (this.isOtherFont(font)) {
            this.writeCssProperty("font-family", font.getFamilyname());
            if (font.getSize() != -1.0f) {
                this.writeCssProperty("font-size", "" + font.getSize() + "pt");
            }
            if (font.getColor() != null) {
                this.writeCssProperty("color", HtmlEncoder.encode(font.getColor()));
            }
            int n = font.getStyle();
            object = font.getBaseFont();
            if (object != null) {
                String string = object.getPostscriptFontName().toLowerCase();
                if (string.indexOf("bold") >= 0) {
                    if (n == -1) {
                        n = 0;
                    }
                    n |= 1;
                }
                if (string.indexOf("italic") >= 0 || string.indexOf("oblique") >= 0) {
                    if (n == -1) {
                        n = 0;
                    }
                    n |= 2;
                }
            }
            if (n != -1 && n != 0) {
                switch (n & 3) {
                    case 1: {
                        this.writeCssProperty("font-weight", "bold");
                        break;
                    }
                    case 2: {
                        this.writeCssProperty("font-style", "italic");
                        break;
                    }
                    case 3: {
                        this.writeCssProperty("font-weight", "bold");
                        this.writeCssProperty("font-style", "italic");
                    }
                }
                if ((n & 4) > 0) {
                    this.writeCssProperty("text-decoration", "underline");
                }
                if ((n & 8) > 0) {
                    this.writeCssProperty("text-decoration", "line-through");
                }
            }
        }
        this.write("\"");
    }

    protected void writeCssProperty(String string, String string2) throws IOException {
        this.write(string + ": " + string2 + "; ");
    }
}

