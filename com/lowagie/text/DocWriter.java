/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.DocListener;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.OutputStreamCounter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public abstract class DocWriter
implements DocListener {
    public static final byte NEWLINE = 10;
    public static final byte TAB = 9;
    public static final byte LT = 60;
    public static final byte SPACE = 32;
    public static final byte EQUALS = 61;
    public static final byte QUOTE = 34;
    public static final byte GT = 62;
    public static final byte FORWARD = 47;
    protected Rectangle pageSize;
    protected Document document;
    protected OutputStreamCounter os;
    protected boolean open = false;
    protected boolean pause = false;
    protected boolean closeStream = true;

    protected DocWriter() {
    }

    protected DocWriter(Document document, OutputStream outputStream) {
        this.document = document;
        this.os = new OutputStreamCounter(new BufferedOutputStream(outputStream));
    }

    public boolean add(Element element) throws DocumentException {
        return false;
    }

    public void open() {
        this.open = true;
    }

    public boolean setPageSize(Rectangle rectangle) {
        this.pageSize = rectangle;
        return true;
    }

    public boolean setMargins(float f, float f2, float f3, float f4) {
        return false;
    }

    public boolean newPage() {
        if (!this.open) {
            return false;
        }
        return true;
    }

    public void setHeader(HeaderFooter headerFooter) {
    }

    public void resetHeader() {
    }

    public void setFooter(HeaderFooter headerFooter) {
    }

    public void resetFooter() {
    }

    public void resetPageCount() {
    }

    public void setPageCount(int n) {
    }

    public void close() {
        this.open = false;
        try {
            this.os.flush();
            if (this.closeStream) {
                this.os.close();
            }
        }
        catch (IOException var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    public static final byte[] getISOBytes(String string) {
        if (string == null) {
            return null;
        }
        int n = string.length();
        byte[] arrby = new byte[n];
        for (int i = 0; i < n; ++i) {
            arrby[i] = (byte)string.charAt(i);
        }
        return arrby;
    }

    public void pause() {
        this.pause = true;
    }

    public boolean isPaused() {
        return this.pause;
    }

    public void resume() {
        this.pause = false;
    }

    public void flush() {
        try {
            this.os.flush();
        }
        catch (IOException var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    protected void write(String string) throws IOException {
        this.os.write(DocWriter.getISOBytes(string));
    }

    protected void addTabs(int n) throws IOException {
        this.os.write(10);
        for (int i = 0; i < n; ++i) {
            this.os.write(9);
        }
    }

    protected void write(String string, String string2) throws IOException {
        this.os.write(32);
        this.write(string);
        this.os.write(61);
        this.os.write(34);
        this.write(string2);
        this.os.write(34);
    }

    protected void writeStart(String string) throws IOException {
        this.os.write(60);
        this.write(string);
    }

    protected void writeEnd(String string) throws IOException {
        this.os.write(60);
        this.os.write(47);
        this.write(string);
        this.os.write(62);
    }

    protected void writeEnd() throws IOException {
        this.os.write(32);
        this.os.write(47);
        this.os.write(62);
    }

    protected boolean writeMarkupAttributes(Properties properties) throws IOException {
        if (properties == null) {
            return false;
        }
        Iterator iterator = properties.keySet().iterator();
        while (iterator.hasNext()) {
            String string = String.valueOf(iterator.next());
            this.write(string, properties.getProperty(string));
        }
        properties.clear();
        return true;
    }

    public boolean isCloseStream() {
        return this.closeStream;
    }

    public void setCloseStream(boolean bl) {
        this.closeStream = bl;
    }

    public boolean setMarginMirroring(boolean bl) {
        return false;
    }
}

