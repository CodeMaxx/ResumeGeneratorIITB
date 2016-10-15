/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public final class PdfEncryptor {
    private PdfEncryptor() {
    }

    public static void encrypt(PdfReader pdfReader, OutputStream outputStream, byte[] arrby, byte[] arrby2, int n, boolean bl) throws DocumentException, IOException {
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        pdfStamper.setEncryption(arrby, arrby2, n, bl);
        pdfStamper.close();
    }

    public static void encrypt(PdfReader pdfReader, OutputStream outputStream, byte[] arrby, byte[] arrby2, int n, boolean bl, HashMap hashMap) throws DocumentException, IOException {
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        pdfStamper.setEncryption(arrby, arrby2, n, bl);
        pdfStamper.setMoreInfo(hashMap);
        pdfStamper.close();
    }

    public static void encrypt(PdfReader pdfReader, OutputStream outputStream, boolean bl, String string, String string2, int n) throws DocumentException, IOException {
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        pdfStamper.setEncryption(bl, string, string2, n);
        pdfStamper.close();
    }

    public static void encrypt(PdfReader pdfReader, OutputStream outputStream, boolean bl, String string, String string2, int n, HashMap hashMap) throws DocumentException, IOException {
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        pdfStamper.setEncryption(bl, string, string2, n);
        pdfStamper.setMoreInfo(hashMap);
        pdfStamper.close();
    }

    public static void encrypt(PdfReader pdfReader, OutputStream outputStream, int n, String string, String string2, int n2, HashMap hashMap) throws DocumentException, IOException {
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        pdfStamper.setEncryption(n, string, string2, n2);
        pdfStamper.setMoreInfo(hashMap);
        pdfStamper.close();
    }

    public static void encrypt(PdfReader pdfReader, OutputStream outputStream, int n, String string, String string2, int n2) throws DocumentException, IOException {
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        pdfStamper.setEncryption(n, string, string2, n2);
        pdfStamper.close();
    }

    public static String getPermissionsVerbose(int n) {
        StringBuffer stringBuffer = new StringBuffer("Allowed:");
        if ((2052 & n) == 2052) {
            stringBuffer.append(" Printing");
        }
        if ((8 & n) == 8) {
            stringBuffer.append(" Modify contents");
        }
        if ((16 & n) == 16) {
            stringBuffer.append(" Copy");
        }
        if ((32 & n) == 32) {
            stringBuffer.append(" Modify annotations");
        }
        if ((256 & n) == 256) {
            stringBuffer.append(" Fill in");
        }
        if ((512 & n) == 512) {
            stringBuffer.append(" Screen readers");
        }
        if ((1024 & n) == 1024) {
            stringBuffer.append(" Assembly");
        }
        if ((4 & n) == 4) {
            stringBuffer.append(" Degraded printing");
        }
        return stringBuffer.toString();
    }

    public static boolean isPrintingAllowed(int n) {
        return (2052 & n) == 2052;
    }

    public static boolean isModifyContentsAllowed(int n) {
        return (8 & n) == 8;
    }

    public static boolean isCopyAllowed(int n) {
        return (16 & n) == 16;
    }

    public static boolean isModifyAnnotationsAllowed(int n) {
        return (32 & n) == 32;
    }

    public static boolean isFillInAllowed(int n) {
        return (256 & n) == 256;
    }

    public static boolean isScreenReadersAllowed(int n) {
        return (512 & n) == 512;
    }

    public static boolean isAssemblyAllowed(int n) {
        return (1024 & n) == 1024;
    }

    public static boolean isDegradedPrintingAllowed(int n) {
        return (4 & n) == 4;
    }
}

