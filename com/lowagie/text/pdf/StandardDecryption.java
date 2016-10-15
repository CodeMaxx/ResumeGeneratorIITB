/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.crypto.AESCipher;
import com.lowagie.text.pdf.crypto.ARCFOUREncryption;

public class StandardDecryption {
    protected ARCFOUREncryption arcfour;
    protected AESCipher cipher;
    private byte[] key;
    private static final int AES_128 = 4;
    private boolean aes;
    private boolean initiated;
    private byte[] iv = new byte[16];
    private int ivptr;

    public StandardDecryption(byte[] arrby, int n, int n2, int n3) {
        boolean bl = this.aes = n3 == 4;
        if (this.aes) {
            this.key = new byte[n2];
            System.arraycopy(arrby, n, this.key, 0, n2);
        } else {
            this.arcfour = new ARCFOUREncryption();
            this.arcfour.prepareARCFOURKey(arrby, n, n2);
        }
    }

    public byte[] update(byte[] arrby, int n, int n2) {
        if (this.aes) {
            if (this.initiated) {
                return this.cipher.update(arrby, n, n2);
            }
            int n3 = Math.min(this.iv.length - this.ivptr, n2);
            System.arraycopy(arrby, n, this.iv, this.ivptr, n3);
            n += n3;
            n2 -= n3;
            this.ivptr += n3;
            if (this.ivptr == this.iv.length) {
                this.cipher = new AESCipher(false, this.key, this.iv);
                this.initiated = true;
                if (n2 > 0) {
                    return this.cipher.update(arrby, n, n2);
                }
            }
            return null;
        }
        byte[] arrby2 = new byte[n2];
        this.arcfour.encryptARCFOUR(arrby, n, n2, arrby2, 0);
        return arrby2;
    }

    public byte[] finish() {
        if (this.aes) {
            return this.cipher.doFinal();
        }
        return null;
    }
}

