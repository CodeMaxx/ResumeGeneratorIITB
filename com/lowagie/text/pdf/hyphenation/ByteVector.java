/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.hyphenation;

import java.io.Serializable;

public class ByteVector
implements Serializable {
    private static final long serialVersionUID = -1096301185375029343L;
    private static final int DEFAULT_BLOCK_SIZE = 2048;
    private int blockSize;
    private byte[] array;
    private int n;

    public ByteVector() {
        this(2048);
    }

    public ByteVector(int n) {
        this.blockSize = n > 0 ? n : 2048;
        this.array = new byte[this.blockSize];
        this.n = 0;
    }

    public ByteVector(byte[] arrby) {
        this.blockSize = 2048;
        this.array = arrby;
        this.n = 0;
    }

    public ByteVector(byte[] arrby, int n) {
        this.blockSize = n > 0 ? n : 2048;
        this.array = arrby;
        this.n = 0;
    }

    public byte[] getArray() {
        return this.array;
    }

    public int length() {
        return this.n;
    }

    public int capacity() {
        return this.array.length;
    }

    public void put(int n, byte by) {
        this.array[n] = by;
    }

    public byte get(int n) {
        return this.array[n];
    }

    public int alloc(int n) {
        int n2 = this.n;
        int n3 = this.array.length;
        if (this.n + n >= n3) {
            byte[] arrby = new byte[n3 + this.blockSize];
            System.arraycopy(this.array, 0, arrby, 0, n3);
            this.array = arrby;
        }
        this.n += n;
        return n2;
    }

    public void trimToSize() {
        if (this.n < this.array.length) {
            byte[] arrby = new byte[this.n];
            System.arraycopy(this.array, 0, arrby, 0, this.n);
            this.array = arrby;
        }
    }
}

