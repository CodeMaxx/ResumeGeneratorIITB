/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.Utilities;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

public class InputMeta {
    InputStream in;
    int length;

    public InputMeta(InputStream inputStream) {
        this.in = inputStream;
    }

    public int readWord() throws IOException {
        this.length += 2;
        int n = this.in.read();
        if (n < 0) {
            return 0;
        }
        return n + (this.in.read() << 8) & 65535;
    }

    public int readShort() throws IOException {
        int n = this.readWord();
        if (n > 32767) {
            n -= 65536;
        }
        return n;
    }

    public int readInt() throws IOException {
        this.length += 4;
        int n = this.in.read();
        if (n < 0) {
            return 0;
        }
        int n2 = this.in.read() << 8;
        int n3 = this.in.read() << 16;
        return n + n2 + n3 + (this.in.read() << 24);
    }

    public int readByte() throws IOException {
        ++this.length;
        return this.in.read() & 255;
    }

    public void skip(int n) throws IOException {
        this.length += n;
        Utilities.skip(this.in, n);
    }

    public int getLength() {
        return this.length;
    }

    public Color readColor() throws IOException {
        int n = this.readByte();
        int n2 = this.readByte();
        int n3 = this.readByte();
        this.readByte();
        return new Color(n, n2, n3);
    }
}

