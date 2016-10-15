package com.lowagie.tools;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class SplitPdf {
    public static void main(String[] arrstring) {
        if (arrstring.length != 4) {
            System.err.println("arguments: srcfile destfile1 destfile2 pagenumber");
        } else {
            try {
                PdfImportedPage pdfImportedPage;
                int n;
                int n2 = Integer.parseInt(arrstring[3]);
                PdfReader pdfReader = new PdfReader(arrstring[0]);
                int n3 = pdfReader.getNumberOfPages();
                System.out.println("There are " + n3 + " pages in the original file.");
                if (n2 < 2 || n2 > n3) {
                    throw new DocumentException("You can't split this document at page " + n2 + "; there is no such page.");
                }
                Document document = new Document(pdfReader.getPageSizeWithRotation(1));
                Document document2 = new Document(pdfReader.getPageSizeWithRotation(n2));
                PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(arrstring[1]));
                PdfWriter pdfWriter2 = PdfWriter.getInstance(document2, new FileOutputStream(arrstring[2]));
                document.open();
                PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
                document2.open();
                PdfContentByte pdfContentByte2 = pdfWriter2.getDirectContent();
                int n4 = 0;
                while (n4 < n2 - 1) {
                    document.setPageSize(pdfReader.getPageSizeWithRotation(++n4));
                    document.newPage();
                    pdfImportedPage = pdfWriter.getImportedPage(pdfReader, n4);
                    n = pdfReader.getPageRotation(n4);
                    if (n == 90 || n == 270) {
                        pdfContentByte.addTemplate(pdfImportedPage, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, pdfReader.getPageSizeWithRotation(n4).getHeight());
                        continue;
                    }
                    pdfContentByte.addTemplate(pdfImportedPage, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f);
                }
                while (n4 < n3) {
                    document2.setPageSize(pdfReader.getPageSizeWithRotation(++n4));
                    document2.newPage();
                    pdfImportedPage = pdfWriter2.getImportedPage(pdfReader, n4);
                    n = pdfReader.getPageRotation(n4);
                    if (n == 90 || n == 270) {
                        pdfContentByte2.addTemplate(pdfImportedPage, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, pdfReader.getPageSizeWithRotation(n4).getHeight());
                    } else {
                        pdfContentByte2.addTemplate(pdfImportedPage, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f);
                    }
                    System.out.println("Processed page " + n4);
                }
                document.close();
                document2.close();
            }
            catch (Exception var1_2) {
                var1_2.printStackTrace();
            }
        }
    }
}

