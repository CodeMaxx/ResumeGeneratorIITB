/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamCounter
extends OutputStream {
    protected OutputStream out;
    protected int counter = 0;

    public OutputStreamCounter(OutputStream outputStream) {
        this.out = outputStream;
    }

    public void close() throws IOException {
        this.out.close();
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public void write(byte[] arrby) throws IOException {
        this.counter += arrby.length;
        this.out.write(arrby);
    }

    public void write(int n) throws IOException {
        ++this.counter;
        this.out.write(n);
    }

    public void write(byte[] arrby, int n, int n2) throws IOException {
        this.counter += n2;
        this.out.write(arrby, n, n2);
    }

    public int getCounter() {
        return this.counter;
    }

    public void resetCounter() {
        this.counter = 0;
    }
}

