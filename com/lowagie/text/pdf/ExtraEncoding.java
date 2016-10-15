/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

public interface ExtraEncoding {
    public byte[] charToByte(String var1, String var2);

    public byte[] charToByte(char var1, String var2);

    public String byteToChar(byte[] var1, String var2);
}

