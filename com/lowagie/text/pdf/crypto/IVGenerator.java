/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.crypto;

import com.lowagie.text.pdf.crypto.ARCFOUREncryption;

public final class IVGenerator {
    private static ARCFOUREncryption arcfour = new ARCFOUREncryption();

    private IVGenerator() {
    }

    public static byte[] getIV() {
        return IVGenerator.getIV(16);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] getIV(int n) {
        byte[] arrby = new byte[n];
        ARCFOUREncryption aRCFOUREncryption = arcfour;
        synchronized (aRCFOUREncryption) {
            arcfour.encryptARCFOUR(arrby);
        }
        return arrby;
    }

    static {
        long l = System.currentTimeMillis();
        long l2 = Runtime.getRuntime().freeMemory();
        String string = "" + l + "+" + l2;
        arcfour.prepareARCFOURKey(string.getBytes());
    }
}

