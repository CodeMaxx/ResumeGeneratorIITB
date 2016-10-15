/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chapter;
import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Meta;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.factories.ElementFactory;
import com.lowagie.text.html.HtmlTagMap;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.lowagie.text.xml.simpleparser.EntitiesToSymbol;
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SAXiTextHandler
extends DefaultHandler {
    protected DocListener document;
    protected Stack stack;
    protected int chapters = 0;
    protected Chunk currentChunk = null;
    protected boolean ignore = false;
    protected boolean controlOpenClose = true;
    float topMargin = 36.0f;
    float rightMargin = 36.0f;
    float leftMargin = 36.0f;
    float bottomMargin = 36.0f;
    protected HashMap myTags;
    private BaseFont bf = null;
    static /* synthetic */ Class class$com$lowagie$text$PageSize;

    public SAXiTextHandler(DocListener docListener) {
        this.document = docListener;
        this.stack = new Stack();
    }

    public SAXiTextHandler(DocListener docListener, HtmlTagMap htmlTagMap) {
        this(docListener);
        this.myTags = htmlTagMap;
    }

    public SAXiTextHandler(DocListener docListener, HtmlTagMap htmlTagMap, BaseFont baseFont) {
        this(docListener, htmlTagMap);
        this.bf = baseFont;
    }

    public SAXiTextHandler(DocListener docListener, HashMap hashMap) {
        this(docListener);
        this.myTags = hashMap;
    }

    public void setControlOpenClose(boolean bl) {
        this.controlOpenClose = bl;
    }

    public void startElement(String string, String string2, String string3, Attributes attributes) {
        Properties properties = new Properties();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String string4 = attributes.getQName(i);
                properties.setProperty(string4, attributes.getValue(i));
            }
        }
        this.handleStartingTags(string3, properties);
    }

    public void handleStartingTags(String string, Properties properties) {
        Object object;
        if (this.ignore || "ignore".equals(string)) {
            this.ignore = true;
            return;
        }
        if (this.currentChunk != null) {
            try {
                object = (TextElementArray)this.stack.pop();
            }
            catch (EmptyStackException var4_5) {
                object = this.bf == null ? new Paragraph("", new Font()) : new Paragraph("", new Font(this.bf));
            }
            object.add(this.currentChunk);
            this.stack.push(object);
            this.currentChunk = null;
        }
        if ("chunk".equals(string)) {
            this.currentChunk = ElementFactory.getChunk(properties);
            if (this.bf != null) {
                this.currentChunk.setFont(new Font(this.bf));
            }
            return;
        }
        if ("entity".equals(string)) {
            object = new Font();
            if (this.currentChunk != null) {
                this.handleEndingTags("chunk");
                object = this.currentChunk.getFont();
            }
            this.currentChunk = EntitiesToSymbol.get(properties.getProperty("id"), (Font)object);
            return;
        }
        if ("phrase".equals(string)) {
            this.stack.push(ElementFactory.getPhrase(properties));
            return;
        }
        if ("anchor".equals(string)) {
            this.stack.push(ElementFactory.getAnchor(properties));
            return;
        }
        if ("paragraph".equals(string) || "title".equals(string)) {
            this.stack.push(ElementFactory.getParagraph(properties));
            return;
        }
        if ("list".equals(string)) {
            this.stack.push(ElementFactory.getList(properties));
            return;
        }
        if ("listitem".equals(string)) {
            this.stack.push(ElementFactory.getListItem(properties));
            return;
        }
        if ("cell".equals(string)) {
            this.stack.push(ElementFactory.getCell(properties));
            return;
        }
        if ("table".equals(string)) {
            object = ElementFactory.getTable(properties);
            float[] arrf = object.getProportionalWidths();
            for (int i = 0; i < arrf.length; ++i) {
                if (arrf[i] != 0.0f) continue;
                arrf[i] = 100.0f / (float)arrf.length;
            }
            try {
                object.setWidths(arrf);
            }
            catch (BadElementException var5_16) {
                throw new ExceptionConverter(var5_16);
            }
            this.stack.push(object);
            return;
        }
        if ("section".equals(string)) {
            Section section;
            object = (Element)this.stack.pop();
            try {
                section = ElementFactory.getSection((Section)object, properties);
            }
            catch (ClassCastException var5_17) {
                throw new ExceptionConverter(var5_17);
            }
            this.stack.push(object);
            this.stack.push(section);
            return;
        }
        if ("chapter".equals(string)) {
            this.stack.push(ElementFactory.getChapter(properties));
            return;
        }
        if ("image".equals(string)) {
            try {
                object = ElementFactory.getImage(properties);
                try {
                    this.addImage((Image)object);
                    return;
                }
                catch (EmptyStackException var4_8) {
                    try {
                        this.document.add((Element)object);
                    }
                    catch (DocumentException var5_18) {
                        throw new ExceptionConverter(var5_18);
                    }
                    return;
                }
            }
            catch (Exception var3_4) {
                throw new ExceptionConverter(var3_4);
            }
        }
        if ("annotation".equals(string)) {
            object = ElementFactory.getAnnotation(properties);
            try {
                try {
                    TextElementArray textElementArray = (TextElementArray)this.stack.pop();
                    try {
                        textElementArray.add(object);
                    }
                    catch (Exception var5_19) {
                        this.document.add((Element)object);
                    }
                    this.stack.push(textElementArray);
                }
                catch (EmptyStackException var5_20) {
                    this.document.add((Element)object);
                }
                return;
            }
            catch (DocumentException var5_21) {
                throw new ExceptionConverter(var5_21);
            }
        }
        if (this.isNewline(string)) {
            try {
                object = (TextElementArray)this.stack.pop();
                object.add(Chunk.NEWLINE);
                this.stack.push(object);
            }
            catch (EmptyStackException var4_10) {
                if (this.currentChunk == null) {
                    try {
                        this.document.add(Chunk.NEWLINE);
                    }
                    catch (DocumentException var5_22) {
                        throw new ExceptionConverter(var5_22);
                    }
                }
                this.currentChunk.append("\n");
            }
            return;
        }
        if (this.isNewpage(string)) {
            try {
                object = (TextElementArray)this.stack.pop();
                Chunk chunk = new Chunk("");
                chunk.setNewPage();
                if (this.bf != null) {
                    chunk.setFont(new Font(this.bf));
                }
                object.add(chunk);
                this.stack.push(object);
            }
            catch (EmptyStackException var4_12) {
                this.document.newPage();
            }
            return;
        }
        if ("horizontalrule".equals(string)) {
            LineSeparator lineSeparator = new LineSeparator(1.0f, 100.0f, null, 1, 0.0f);
            try {
                object = (TextElementArray)this.stack.pop();
                object.add(lineSeparator);
                this.stack.push(object);
            }
            catch (EmptyStackException var5_23) {
                try {
                    this.document.add(lineSeparator);
                }
                catch (DocumentException var6_25) {
                    throw new ExceptionConverter(var6_25);
                }
            }
            return;
        }
        if (this.isDocumentRoot(string)) {
            Rectangle rectangle = null;
            String string2 = null;
            Iterator iterator = properties.keySet().iterator();
            while (iterator.hasNext()) {
                object = (String)iterator.next();
                String string3 = properties.getProperty((String)object);
                try {
                    if ("left".equalsIgnoreCase((String)object)) {
                        this.leftMargin = Float.parseFloat(string3 + "f");
                    }
                    if ("right".equalsIgnoreCase((String)object)) {
                        this.rightMargin = Float.parseFloat(string3 + "f");
                    }
                    if ("top".equalsIgnoreCase((String)object)) {
                        this.topMargin = Float.parseFloat(string3 + "f");
                    }
                    if ("bottom".equalsIgnoreCase((String)object)) {
                        this.bottomMargin = Float.parseFloat(string3 + "f");
                    }
                }
                catch (Exception var8_29) {
                    throw new ExceptionConverter(var8_29);
                }
                if ("pagesize".equals(object)) {
                    try {
                        String string4 = string3;
                        Field field = (class$com$lowagie$text$PageSize == null ? SAXiTextHandler.class$("com.lowagie.text.PageSize") : class$com$lowagie$text$PageSize).getField(string4);
                        rectangle = (Rectangle)field.get(null);
                        continue;
                    }
                    catch (Exception var8_30) {
                        throw new ExceptionConverter(var8_30);
                    }
                }
                if ("orientation".equals(object)) {
                    try {
                        if (!"landscape".equals(string3)) continue;
                        string2 = "landscape";
                        continue;
                    }
                    catch (Exception var8_31) {
                        throw new ExceptionConverter(var8_31);
                    }
                }
                try {
                    this.document.add(new Meta((String)object, string3));
                    continue;
                }
                catch (DocumentException var8_32) {
                    throw new ExceptionConverter(var8_32);
                }
            }
            if (rectangle != null) {
                if ("landscape".equals(string2)) {
                    rectangle = rectangle.rotate();
                }
                this.document.setPageSize(rectangle);
            }
            this.document.setMargins(this.leftMargin, this.rightMargin, this.topMargin, this.bottomMargin);
            if (this.controlOpenClose) {
                this.document.open();
            }
        }
    }

    protected void addImage(Image image) throws EmptyStackException {
        Object e = this.stack.pop();
        if (e instanceof Chapter || e instanceof Section || e instanceof Cell) {
            ((TextElementArray)e).add(image);
            this.stack.push(e);
            return;
        }
        Stack stack = new Stack();
        while (!(e instanceof Chapter || e instanceof Section || e instanceof Cell)) {
            stack.push(e);
            if (e instanceof Anchor) {
                image.setAnnotation(new Annotation(0.0f, 0.0f, 0.0f, 0.0f, ((Anchor)e).getReference()));
            }
            e = this.stack.pop();
        }
        ((TextElementArray)e).add(image);
        this.stack.push(e);
        while (!stack.empty()) {
            this.stack.push(stack.pop());
        }
    }

    public void ignorableWhitespace(char[] arrc, int n, int n2) {
        this.characters(arrc, n, n2);
    }

    public void characters(char[] arrc, int n, int n2) {
        if (this.ignore) {
            return;
        }
        String string = new String(arrc, n, n2);
        if (string.trim().length() == 0 && string.indexOf(32) < 0) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        int n3 = string.length();
        boolean bl = false;
        block6 : for (int i = 0; i < n3; ++i) {
            char c = string.charAt(i);
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
        if (this.currentChunk == null) {
            this.currentChunk = this.bf == null ? new Chunk(stringBuffer.toString()) : new Chunk(stringBuffer.toString(), new Font(this.bf));
        } else {
            this.currentChunk.append(stringBuffer.toString());
        }
    }

    public void setBaseFont(BaseFont baseFont) {
        this.bf = baseFont;
    }

    public void endElement(String string, String string2, String string3) {
        this.handleEndingTags(string3);
    }

    public void handleEndingTags(String string) {
        if ("ignore".equals(string)) {
            this.ignore = false;
            return;
        }
        if (this.ignore) {
            return;
        }
        if (this.isNewpage(string) || "annotation".equals(string) || "image".equals(string) || this.isNewline(string)) {
            return;
        }
        try {
            TextElementArray textElementArray;
            Object object;
            TextElementArray textElementArray2;
            if ("title".equals(string)) {
                Paragraph paragraph = (Paragraph)this.stack.pop();
                if (this.currentChunk != null) {
                    paragraph.add(this.currentChunk);
                    this.currentChunk = null;
                }
                textElementArray2 = (Section)this.stack.pop();
                textElementArray2.setTitle(paragraph);
                this.stack.push(textElementArray2);
                return;
            }
            if (this.currentChunk != null) {
                try {
                    object = (TextElementArray)this.stack.pop();
                }
                catch (EmptyStackException var3_7) {
                    object = new Paragraph();
                }
                object.add(this.currentChunk);
                this.stack.push(object);
                this.currentChunk = null;
            }
            if ("chunk".equals(string)) {
                return;
            }
            if ("phrase".equals(string) || "anchor".equals(string) || "list".equals(string) || "paragraph".equals(string)) {
                object = (Element)this.stack.pop();
                try {
                    TextElementArray textElementArray3 = (TextElementArray)this.stack.pop();
                    textElementArray3.add(object);
                    this.stack.push(textElementArray3);
                }
                catch (EmptyStackException var3_9) {
                    this.document.add((Element)object);
                }
                return;
            }
            if ("listitem".equals(string)) {
                object = (ListItem)this.stack.pop();
                textElementArray = (List)this.stack.pop();
                textElementArray.add(object);
                this.stack.push(textElementArray);
            }
            if ("table".equals(string)) {
                object = (Table)this.stack.pop();
                try {
                    textElementArray = (TextElementArray)this.stack.pop();
                    textElementArray.add(object);
                    this.stack.push(textElementArray);
                }
                catch (EmptyStackException var3_11) {
                    this.document.add((Element)object);
                }
                return;
            }
            if ("row".equals(string)) {
                Object object2;
                Cell cell;
                object = new ArrayList();
                int n = 0;
                while ((object2 = (Element)this.stack.pop()).type() == 20) {
                    cell = (Cell)object2;
                    n += cell.getColspan();
                    object.add(cell);
                }
                Table table = (Table)object2;
                if (table.getColumns() < n) {
                    table.addColumns(n - table.getColumns());
                }
                Collections.reverse(object);
                float[] arrf = new float[n];
                boolean[] arrbl = new boolean[n];
                for (int i = 0; i < n; ++i) {
                    arrf[i] = 0.0f;
                    arrbl[i] = true;
                }
                float f = 0.0f;
                int n2 = 0;
                float[] arrf2 = object.iterator();
                while (arrf2.hasNext()) {
                    cell = (Cell)arrf2.next();
                    object2 = cell.getWidthAsString();
                    if (cell.getWidth() == 0.0f) {
                        if (cell.getColspan() == 1 && arrf[n2] == 0.0f) {
                            try {
                                arrf[n2] = 100.0f / (float)n;
                                f += arrf[n2];
                            }
                            catch (Exception var12_24) {}
                        } else if (cell.getColspan() == 1) {
                            arrbl[n2] = false;
                        }
                    } else if (cell.getColspan() == 1 && object2.endsWith("%")) {
                        try {
                            arrf[n2] = Float.parseFloat(object2.substring(0, object2.length() - 1) + "f");
                            f += arrf[n2];
                        }
                        catch (Exception var12_25) {
                            // empty catch block
                        }
                    }
                    n2 += cell.getColspan();
                    table.addCell(cell);
                }
                arrf2 = table.getProportionalWidths();
                if (arrf2.length == n) {
                    int n3;
                    float f2 = 0.0f;
                    for (n3 = 0; n3 < n; ++n3) {
                        if (!arrbl[n3] || arrf2[n3] == 0.0f) continue;
                        f2 += arrf2[n3];
                        arrf[n3] = arrf2[n3];
                    }
                    if (100.0 >= (double)f) {
                        for (n3 = 0; n3 < arrf2.length; ++n3) {
                            if (arrf[n3] != 0.0f || arrf2[n3] == 0.0f) continue;
                            arrf[n3] = arrf2[n3] / f2 * (100.0f - f);
                        }
                    }
                    table.setWidths(arrf);
                }
                this.stack.push(table);
            }
            if ("cell".equals(string)) {
                return;
            }
            if ("section".equals(string)) {
                this.stack.pop();
                return;
            }
            if ("chapter".equals(string)) {
                this.document.add((Element)this.stack.pop());
                return;
            }
            if (this.isDocumentRoot(string)) {
                try {
                    do {
                        object = (Element)this.stack.pop();
                        try {
                            textElementArray2 = (TextElementArray)this.stack.pop();
                            textElementArray2.add(object);
                            this.stack.push(textElementArray2);
                        }
                        catch (EmptyStackException var3_13) {
                            this.document.add((Element)object);
                        }
                    } while (true);
                }
                catch (EmptyStackException var2_4) {
                    if (this.controlOpenClose) {
                        this.document.close();
                    }
                    return;
                }
            }
        }
        catch (DocumentException var2_5) {
            throw new ExceptionConverter(var2_5);
        }
    }

    private boolean isNewpage(String string) {
        return "newpage".equals(string);
    }

    private boolean isNewline(String string) {
        return "newline".equals(string);
    }

    protected boolean isDocumentRoot(String string) {
        return "itext".equals(string);
    }
}

