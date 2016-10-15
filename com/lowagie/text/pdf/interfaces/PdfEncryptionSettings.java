/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.DocumentException;
import java.security.cert.Certificate;

public interface PdfEncryptionSettings {
    public void setEncryption(byte[] var1, byte[] var2, int var3, int var4) throws DocumentException;

    public void setEncryption(Certificate[] var1, int[] var2, int var3) throws DocumentException;
}

