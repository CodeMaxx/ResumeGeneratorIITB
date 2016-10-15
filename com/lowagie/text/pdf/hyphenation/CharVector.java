/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.hyphenation;

import java.io.Serializable;

public class CharVector
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -4875768298308363544L;
    private static final int DEFAULT_BLOCK_SIZE = 2048;
    private int blockSize;
    private char[] array;
    private int n;

    public CharVector() {
        this(2048);
    }

    public CharVector(int n) {
        this.blockSize = n > 0 ? n : 2048;
        this.array = new char[this.blockSize];
        this.n = 0;
    }

    public CharVector(char[] arrc) {
        this.blockSize = 2048;
        this.array = arrc;
        this.n = arrc.length;
    }

    public CharVector(char[] arrc, int n) {
        this.blockSize = n > 0 ? n : 2048;
        this.array = arrc;
        this.n = arrc.length;
    }

    public void clear() {
        this.n = 0;
    }

    public Object clone() {
        CharVector charVector = new CharVector((char[])this.array.clone(), this.blockSize);
        charVector.n = this.n;
        return charVector;
    }

    public char[] getArray() {
        return this.array;
    }

    public int length() {
        return this.n;
    }

    public int capacity() {
        return this.array.length;
    }

    public void put(int n, char c) {
        this.array[n] = c;
    }

    public char get(int n) {
        return this.array[n];
    }

    public int alloc(int n) {
        int n2 = this.n;
        int n3 = this.array.length;
        if (this.n + n >= n3) {
            char[] arrc = new char[n3 + this.blockSize];
            System.arraycopy(this.array, 0, arrc, 0, n3);
            this.array = arrc;
        }
        this.n += n;
        return n2;
    }

    public void trimToSize() {
        if (this.n < this.array.length) {
            char[] arrc = new char[this.n];
            System.arraycopy(this.array, 0, arrc, 0, this.n);
            this.array = arrc;
        }
    }
}

