/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Header;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.LargeElement;
import com.lowagie.text.Meta;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Document
implements DocListener {
    private static final String ITEXT_VERSION = "iText 2.1.3 (by lowagie.com)";
    public static boolean compress = true;
    public static boolean plainRandomAccess = false;
    public static float wmfFontCorrection = 0.86f;
    private ArrayList listeners = new ArrayList();
    protected boolean open;
    protected boolean close;
    protected Rectangle pageSize;
    protected float marginLeft = 0.0f;
    protected float marginRight = 0.0f;
    protected float marginTop = 0.0f;
    protected float marginBottom = 0.0f;
    protected boolean marginMirroring = false;
    protected String javaScript_onLoad = null;
    protected String javaScript_onUnLoad = null;
    protected String htmlStyleClass = null;
    protected int pageN = 0;
    protected HeaderFooter header = null;
    protected HeaderFooter footer = null;
    protected int chapternumber = 0;

    public Document() {
        this(PageSize.A4);
    }

    public Document(Rectangle rectangle) {
        this(rectangle, 36.0f, 36.0f, 36.0f, 36.0f);
    }

    public Document(Rectangle rectangle, float f, float f2, float f3, float f4) {
        this.pageSize = rectangle;
        this.marginLeft = f;
        this.marginRight = f2;
        this.marginTop = f3;
        this.marginBottom = f4;
    }

    public void addDocListener(DocListener docListener) {
        this.listeners.add(docListener);
    }

    public void removeDocListener(DocListener docListener) {
        this.listeners.remove(docListener);
    }

    public boolean add(Element element) throws DocumentException {
        if (this.close) {
            throw new DocumentException("The document has been closed. You can't add any Elements.");
        }
        if (!this.open && element.isContent()) {
            throw new DocumentException("The document is not open yet; you can only add Meta information.");
        }
        boolean bl = false;
        if (element instanceof ChapterAutoNumber) {
            ++this.chapternumber;
            ((ChapterAutoNumber)element).setChapterNumber(this.chapternumber);
        }
        Object object = this.listeners.iterator();
        while (object.hasNext()) {
            DocListener docListener = (DocListener)object.next();
            bl |= docListener.add(element);
        }
        if (element instanceof LargeElement && !(object = (LargeElement)element).isComplete()) {
            object.flushContent();
        }
        return bl;
    }

    public void open() {
        if (!this.close) {
            this.open = true;
        }
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.setPageSize(this.pageSize);
            docListener.setMargins(this.marginLeft, this.marginRight, this.marginTop, this.marginBottom);
            docListener.open();
        }
    }

    public boolean setPageSize(Rectangle rectangle) {
        this.pageSize = rectangle;
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.setPageSize(rectangle);
        }
        return true;
    }

    public boolean setMargins(float f, float f2, float f3, float f4) {
        this.marginLeft = f;
        this.marginRight = f2;
        this.marginTop = f3;
        this.marginBottom = f4;
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.setMargins(f, f2, f3, f4);
        }
        return true;
    }

    public boolean newPage() {
        if (!this.open || this.close) {
            return false;
        }
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.newPage();
        }
        return true;
    }

    public void setHeader(HeaderFooter headerFooter) {
        this.header = headerFooter;
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.setHeader(headerFooter);
        }
    }

    public void resetHeader() {
        this.header = null;
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.resetHeader();
        }
    }

    public void setFooter(HeaderFooter headerFooter) {
        this.footer = headerFooter;
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.setFooter(headerFooter);
        }
    }

    public void resetFooter() {
        this.footer = null;
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.resetFooter();
        }
    }

    public void resetPageCount() {
        this.pageN = 0;
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.resetPageCount();
        }
    }

    public void setPageCount(int n) {
        this.pageN = n;
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.setPageCount(n);
        }
    }

    public int getPageNumber() {
        return this.pageN;
    }

    public void close() {
        if (!this.close) {
            this.open = false;
            this.close = true;
        }
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.close();
        }
    }

    public boolean addHeader(String string, String string2) {
        try {
            return this.add(new Header(string, string2));
        }
        catch (DocumentException var3_3) {
            throw new ExceptionConverter(var3_3);
        }
    }

    public boolean addTitle(String string) {
        try {
            return this.add(new Meta(1, string));
        }
        catch (DocumentException var2_2) {
            throw new ExceptionConverter(var2_2);
        }
    }

    public boolean addSubject(String string) {
        try {
            return this.add(new Meta(2, string));
        }
        catch (DocumentException var2_2) {
            throw new ExceptionConverter(var2_2);
        }
    }

    public boolean addKeywords(String string) {
        try {
            return this.add(new Meta(3, string));
        }
        catch (DocumentException var2_2) {
            throw new ExceptionConverter(var2_2);
        }
    }

    public boolean addAuthor(String string) {
        try {
            return this.add(new Meta(4, string));
        }
        catch (DocumentException var2_2) {
            throw new ExceptionConverter(var2_2);
        }
    }

    public boolean addCreator(String string) {
        try {
            return this.add(new Meta(7, string));
        }
        catch (DocumentException var2_2) {
            throw new ExceptionConverter(var2_2);
        }
    }

    public boolean addProducer() {
        try {
            return this.add(new Meta(5, "iText by lowagie.com"));
        }
        catch (DocumentException var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    public boolean addCreationDate() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            return this.add(new Meta(6, simpleDateFormat.format(new Date())));
        }
        catch (DocumentException var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    public float leftMargin() {
        return this.marginLeft;
    }

    public float rightMargin() {
        return this.marginRight;
    }

    public float topMargin() {
        return this.marginTop;
    }

    public float bottomMargin() {
        return this.marginBottom;
    }

    public float left() {
        return this.pageSize.getLeft(this.marginLeft);
    }

    public float right() {
        return this.pageSize.getRight(this.marginRight);
    }

    public float top() {
        return this.pageSize.getTop(this.marginTop);
    }

    public float bottom() {
        return this.pageSize.getBottom(this.marginBottom);
    }

    public float left(float f) {
        return this.pageSize.getLeft(this.marginLeft + f);
    }

    public float right(float f) {
        return this.pageSize.getRight(this.marginRight + f);
    }

    public float top(float f) {
        return this.pageSize.getTop(this.marginTop + f);
    }

    public float bottom(float f) {
        return this.pageSize.getBottom(this.marginBottom + f);
    }

    public Rectangle getPageSize() {
        return this.pageSize;
    }

    public boolean isOpen() {
        return this.open;
    }

    public static final String getVersion() {
        return "iText 2.1.3 (by lowagie.com)";
    }

    public void setJavaScript_onLoad(String string) {
        this.javaScript_onLoad = string;
    }

    public String getJavaScript_onLoad() {
        return this.javaScript_onLoad;
    }

    public void setJavaScript_onUnLoad(String string) {
        this.javaScript_onUnLoad = string;
    }

    public String getJavaScript_onUnLoad() {
        return this.javaScript_onUnLoad;
    }

    public void setHtmlStyleClass(String string) {
        this.htmlStyleClass = string;
    }

    public String getHtmlStyleClass() {
        return this.htmlStyleClass;
    }

    public boolean setMarginMirroring(boolean bl) {
        this.marginMirroring = bl;
        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext()) {
            DocListener docListener = (DocListener)iterator.next();
            docListener.setMarginMirroring(bl);
        }
        return true;
    }

    public boolean isMarginMirroring() {
        return this.marginMirroring;
    }
}

