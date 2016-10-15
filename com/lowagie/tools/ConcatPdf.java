package com.lowagie.tools;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.SimpleBookmark;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConcatPdf {
    public static void main(String[] arrstring) {
        if (arrstring.length < 2) {
            System.err.println("arguments: file1 [file2 ...] destfile");
        } else {
            try {
                int n = 0;
                ArrayList arrayList = new ArrayList();
                String string = arrstring[arrstring.length - 1];
                Document document = null;
                PdfWriter pdfWriter = null;
                for (int i = 0; i < arrstring.length - 1; ++i) {
                    PdfReader pdfReader = new PdfReader(arrstring[i]);
                    pdfReader.consolidateNamedDestinations();
                    int n2 = pdfReader.getNumberOfPages();
                    List list = SimpleBookmark.getBookmark(pdfReader);
                    if (list != null) {
                        if (n != 0) {
                            SimpleBookmark.shiftPageNumbers(list, n, null);
                        }
                        arrayList.addAll(list);
                    }
                    n += n2;
                    System.out.println("There are " + n2 + " pages in " + arrstring[i]);
                    if (i == 0) {
                        document = new Document(pdfReader.getPageSizeWithRotation(1));
                        pdfWriter = new PdfCopy(document, new FileOutputStream(string));
                        document.open();
                    }
                    int n3 = 0;
                    while (n3 < n2) {
                        PdfImportedPage pdfImportedPage = pdfWriter.getImportedPage(pdfReader, ++n3);
                        pdfWriter.addPage(pdfImportedPage);
                        System.out.println("Processed page " + n3);
                    }
                    pdfWriter.freeReader(pdfReader);
                }
                if (!arrayList.isEmpty()) {
                    pdfWriter.setOutlines(arrayList);
                }
                document.close();
            }
            catch (Exception var1_2) {
                var1_2.printStackTrace();
            }
        }
    }
}

