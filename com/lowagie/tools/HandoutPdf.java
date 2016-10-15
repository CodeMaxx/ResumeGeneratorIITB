package com.lowagie.tools;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class HandoutPdf {
    public static void main(String[] arrstring) {
        if (arrstring.length != 3) {
            System.err.println("arguments: srcfile destfile pages");
        } else {
            try {
                int n = Integer.parseInt(arrstring[2]);
                if (n < 2 || n > 8) {
                    throw new DocumentException("You can't have " + n + " pages on one page (minimum 2; maximum 8).");
                }
                float f = 30.0f;
                float f2 = 280.0f;
                float f3 = 320.0f;
                float f4 = 565.0f;
                float[] arrf = new float[n];
                float[] arrf2 = new float[n];
                float f5 = (778.0f - 20.0f * (float)(n - 1)) / (float)n;
                arrf[0] = 812.0f;
                arrf2[0] = 812.0f - f5;
                for (int i = 1; i < n; ++i) {
                    arrf[i] = arrf2[i - 1] - 20.0f;
                    arrf2[i] = arrf[i] - f5;
                }
                PdfReader pdfReader = new PdfReader(arrstring[0]);
                int n2 = pdfReader.getNumberOfPages();
                System.out.println("There are " + n2 + " pages in the original file.");
                Document document = new Document(PageSize.A4);
                PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(arrstring[1]));
                document.open();
                PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
                int n3 = 0;
                int n4 = 0;
                while (n3 < n2) {
                    float f6;
                    Rectangle rectangle;
                    float f7;
                    float f8 = (f7 = (f2 - f) / (rectangle = pdfReader.getPageSizeWithRotation(++n3)).getWidth()) < (f6 = (arrf[n4] - arrf2[n4]) / rectangle.getHeight()) ? f7 : f6;
                    float f9 = f7 == f8 ? 0.0f : (f2 - f - rectangle.getWidth() * f8) / 2.0f;
                    float f10 = f6 == f8 ? 0.0f : (arrf[n4] - arrf2[n4] - rectangle.getHeight() * f8) / 2.0f;
                    PdfImportedPage pdfImportedPage = pdfWriter.getImportedPage(pdfReader, n3);
                    int n5 = pdfReader.getPageRotation(n3);
                    if (n5 == 90 || n5 == 270) {
                        pdfContentByte.addTemplate(pdfImportedPage, 0.0f, - f8, f8, 0.0f, f + f9, arrf2[n4] + f10 + rectangle.getHeight() * f8);
                    } else {
                        pdfContentByte.addTemplate(pdfImportedPage, f8, 0.0f, 0.0f, f8, f + f9, arrf2[n4] + f10);
                    }
                    pdfContentByte.setRGBColorStroke(192, 192, 192);
                    pdfContentByte.rectangle(f3 - 5.0f, arrf2[n4] - 5.0f, f4 - f3 + 10.0f, arrf[n4] - arrf2[n4] + 10.0f);
                    for (float f11 = arrf[n4] - 19.0f; f11 > arrf2[n4]; f11 -= 16.0f) {
                        pdfContentByte.moveTo(f3, f11);
                        pdfContentByte.lineTo(f4, f11);
                    }
                    pdfContentByte.rectangle(f + f9, arrf2[n4] + f10, rectangle.getWidth() * f8, rectangle.getHeight() * f8);
                    pdfContentByte.stroke();
                    System.out.println("Processed page " + n3);
                    if (++n4 != n) continue;
                    n4 = 0;
                    document.newPage();
                }
                document.close();
            }
            catch (Exception var1_2) {
                System.err.println(var1_2.getClass().getName() + ": " + var1_2.getMessage());
            }
        }
    }
}

