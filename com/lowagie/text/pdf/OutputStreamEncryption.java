/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.crypto.AESCipher;
import com.lowagie.text.pdf.crypto.ARCFOUREncryption;
import com.lowagie.text.pdf.crypto.IVGenerator;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamEncryption
extends OutputStream {
    protected OutputStream out;
    protected ARCFOUREncryption arcfour;
    protected AESCipher cipher;
    private byte[] sb = new byte[1];
    private static final int AES_128 = 4;
    private boolean aes;
    private boolean finished;

    public OutputStreamEncryption(OutputStream outputStream, byte[] arrby, int n, int n2, int n3) {
        try {
            this.out = outputStream;
            boolean bl = this.aes = n3 == 4;
            if (this.aes) {
                byte[] arrby2 = IVGenerator.getIV();
                byte[] arrby3 = new byte[n2];
                System.arraycopy(arrby, n, arrby3, 0, n2);
                this.cipher = new AESCipher(true, arrby3, arrby2);
                this.write(arrby2);
            } else {
                this.arcfour = new ARCFOUREncryption();
                this.arcfour.prepareARCFOURKey(arrby, n, n2);
            }
        }
        catch (Exception var6_7) {
            throw new ExceptionConverter(var6_7);
        }
    }

    public OutputStreamEncryption(OutputStream outputStream, byte[] arrby, int n) {
        this(outputStream, arrby, 0, arrby.length, n);
    }

    public void close() throws IOException {
        this.finish();
        this.out.close();
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public void write(byte[] arrby) throws IOException {
        this.write(arrby, 0, arrby.length);
    }

    public void write(int n) throws IOException {
        this.sb[0] = (byte)n;
        this.write(this.sb, 0, 1);
    }

    public void write(byte[] arrby, int n, int n2) throws IOException {
        if (this.aes) {
            byte[] arrby2 = this.cipher.update(arrby, n, n2);
            if (arrby2 == null || arrby2.length == 0) {
                return;
            }
            this.out.write(arrby2, 0, arrby2.length);
        } else {
            byte[] arrby3 = new byte[Math.min(n2, 4192)];
            while (n2 > 0) {
                int n3 = Math.min(n2, arrby3.length);
                this.arcfour.encryptARCFOUR(arrby, n, n3, arrby3, 0);
                this.out.write(arrby3, 0, n3);
                n2 -= n3;
                n += n3;
            }
        }
    }

    public void finish() throws IOException {
        if (!this.finished) {
            this.finished = true;
            if (this.aes) {
                byte[] arrby;
                try {
                    arrby = this.cipher.doFinal();
                }
                catch (Exception var2_2) {
                    throw new ExceptionConverter(var2_2);
                }
                this.out.write(arrby, 0, arrby.length);
            }
        }
    }
}

