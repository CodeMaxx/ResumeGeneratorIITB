/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.crypto;

public class ARCFOUREncryption {
    private byte[] state = new byte[256];
    private int x;
    private int y;

    public void prepareARCFOURKey(byte[] arrby) {
        this.prepareARCFOURKey(arrby, 0, arrby.length);
    }

    public void prepareARCFOURKey(byte[] arrby, int n, int n2) {
        int n3;
        int n4 = 0;
        int n5 = 0;
        for (n3 = 0; n3 < 256; ++n3) {
            this.state[n3] = (byte)n3;
        }
        this.x = 0;
        this.y = 0;
        for (int i = 0; i < 256; ++i) {
            n5 = arrby[n4 + n] + this.state[i] + n5 & 255;
            n3 = this.state[i];
            this.state[i] = this.state[n5];
            this.state[n5] = n3;
            n4 = (n4 + 1) % n2;
        }
    }

    public void encryptARCFOUR(byte[] arrby, int n, int n2, byte[] arrby2, int n3) {
        int n4 = n2 + n;
        for (int i = n; i < n4; ++i) {
            this.x = this.x + 1 & 255;
            this.y = this.state[this.x] + this.y & 255;
            byte by = this.state[this.x];
            this.state[this.x] = this.state[this.y];
            this.state[this.y] = by;
            arrby2[i - n + n3] = (byte)(arrby[i] ^ this.state[this.state[this.x] + this.state[this.y] & 255]);
        }
    }

    public void encryptARCFOUR(byte[] arrby, int n, int n2) {
        this.encryptARCFOUR(arrby, n, n2, arrby, n);
    }

    public void encryptARCFOUR(byte[] arrby, byte[] arrby2) {
        this.encryptARCFOUR(arrby, 0, arrby.length, arrby2, 0);
    }

    public void encryptARCFOUR(byte[] arrby) {
        this.encryptARCFOUR(arrby, 0, arrby.length, arrby, 0);
    }
}

