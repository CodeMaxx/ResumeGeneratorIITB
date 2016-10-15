package com.lowagie.tools;

import com.lowagie.text.pdf.PdfEncryptor;
import com.lowagie.text.pdf.PdfReader;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class EncryptPdf {
    private static final int INPUT_FILE = 0;
    private static final int OUTPUT_FILE = 1;
    private static final int USER_PASSWORD = 2;
    private static final int OWNER_PASSWORD = 3;
    private static final int PERMISSIONS = 4;
    private static final int STRENGTH = 5;
    private static final int MOREINFO = 6;
    private static final int[] permit = new int[]{2052, 8, 16, 32, 256, 512, 1024, 4};

    private static void usage() {
        System.out.println("usage: input_file output_file user_password owner_password permissions 128|40 [new info string pairs]");
        System.out.println("permissions is 8 digit long 0 or 1. Each digit has a particular security function:");
        System.out.println();
        System.out.println("AllowPrinting");
        System.out.println("AllowModifyContents");
        System.out.println("AllowCopy");
        System.out.println("AllowModifyAnnotations");
        System.out.println("AllowFillIn (128 bit only)");
        System.out.println("AllowScreenReaders (128 bit only)");
        System.out.println("AllowAssembly (128 bit only)");
        System.out.println("AllowDegradedPrinting (128 bit only)");
        System.out.println("Example permissions to copy and print would be: 10100000");
    }

    public static void main(String[] arrstring) {
        System.out.println("PDF document encryptor");
        if (arrstring.length <= 5 || arrstring[4].length() != 8) {
            EncryptPdf.usage();
            return;
        }
        try {
            int n = 0;
            String string = arrstring[4];
            for (int i = 0; i < string.length(); ++i) {
                n |= string.charAt(i) == '0' ? 0 : permit[i];
            }
            System.out.println("Reading " + arrstring[0]);
            PdfReader pdfReader = new PdfReader(arrstring[0]);
            System.out.println("Writing " + arrstring[1]);
            HashMap<String, String> hashMap = new HashMap<String, String>();
            for (int j = 6; j < arrstring.length - 1; j += 2) {
                hashMap.put(arrstring[j], arrstring[j + 1]);
            }
            PdfEncryptor.encrypt(pdfReader, (OutputStream)new FileOutputStream(arrstring[1]), arrstring[2].getBytes(), arrstring[3].getBytes(), n, arrstring[5].equals("128"), hashMap);
            System.out.println("Done.");
        }
        catch (Exception var1_2) {
            var1_2.printStackTrace();
        }
    }
}

