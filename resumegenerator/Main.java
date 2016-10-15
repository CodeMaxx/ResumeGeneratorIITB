package resumegenerator;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        String filepre = "";
        System.out.println("ResumeGenerator v 1.0\nAuthor: Aniruddha Maru (aniruddhamaru@gmail.com)\n");
        System.out.print("Reading and generating pdf into cpiaddedresume.pdf ...");
        if (args.length < 9) {
            System.out.println("\n\nInsufficient arguments\nUsage:");
            System.out.println("java -jar \"ResumeGenerator.jar\" NAME ROLLNO DEPT PROGRAM INSTITUTE SEX SPECIALIZATION DOB \\\n[phd;PHD UNIVERSITY;PHD INSTITUTE;PHD YEAR;PHD CPI] \\\n[Post Graduation;PG UNIVERSITY;PG INSTITUTE;PG YEAR;PG CPI UNDERGRADUATE SPECIALIZATION] \\\nGraduation;UG UNIVERSITY;UG INSTITUTE;UG YEAR;UG CPI \\\n[Intermediate/+2;12TH UNIVERSITY;12TH INSTITUTE;12TH YEAR;12TH %] \\\n[Matriculation;10TH UNIVERSITY;10TH INSTITUTE;10TH YEAR;10TH %]\n\nNOTE:------------------------------------------------\nAll students should include 10th/12th and IIT Bombay in institutes. \nPG Students should include UG as well. \nPhD students should include both UG and PG.\n\n See README.TXT for more details!");
            System.exit(1);
        }
        Document document = new Document(PageSize.A4, 54.0f, 54.0f, 54.0f, 54.0f);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File(String.valueOf(filepre) + "cpiaddedresume.pdf")));
            PdfReader pdfReader = new PdfReader(new FileInputStream(String.valueOf(filepre) + "resume.pdf"));
            document.open();
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100.0f);
            table.setTotalWidth(100.0f);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setBorderWidth(0.0f);
            table.setWidths(new float[]{20.0f, 80.0f});
            Image logo = Image.getInstance(Main.class.getResource("logo.jpg"));
            logo.scaleAbsolute(64.0f, 61.0f);
            PdfPCell cell = new PdfPCell(logo);
            cell.setBorder(0);
            cell.setBorderWidth(0.0f);
            table.addCell(cell);
            PdfPTable nestedTable = new PdfPTable(2);
            nestedTable.setWidthPercentage(100.0f);
            nestedTable.setTotalWidth(100.0f);
            nestedTable.getDefaultCell().setBorder(0);
            nestedTable.getDefaultCell().setBorderWidth(0.0f);
            nestedTable.setWidths(new float[]{50.0f, 30.0f});
            int i = 0;
            while (i < 8) {
                nestedTable.addCell(new Phrase(i < 6 ? args[i] : (i == 6 ? (args[i].equals("") ? "" : "Specialization: " + args[i]) : "DOB: " + args[i]), new Font(2, 10.0f, 1)));
                ++i;
            }
            table.addCell(nestedTable);
            document.add(table);
            table = new PdfPTable(5);
            table.setSpacingBefore(10.0f);
            table.setSpacingAfter(10.0f);
            table.setTotalWidth(100.0f);
            table.setWidthPercentage(100.0f);
            int max1 = Integer.MIN_VALUE;
            int max2 = Integer.MIN_VALUE;
            int i2 = 8;
            while (i2 < args.length) {
                if (args[i2].indexOf(";") > max1) {
                    max1 = args[i2].indexOf(";");
                }
                if (args[i2].indexOf(";", args[i2].indexOf(";") + 1) - args[i2].indexOf(";") > max2) {
                    max2 = args[i2].indexOf(";", args[i2].indexOf(";") + 1) - args[i2].indexOf(";");
                }
                ++i2;
            }
            table.setWidths(new float[]{16.5f, 30.0f, 37.0f, 7.5f, 9.0f});
            cell = table.getDefaultCell();
            cell.setBorder(0);
            cell.setBorderWidth(0.0f);
            cell.setBorderWidthTop(1.0f);
            cell.setBorderWidthBottom(1.0f);
            cell.setVerticalAlignment(5);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setUseBorderPadding(true);
            cell.setPaddingTop(3.0f);
            cell.setPaddingBottom(3.0f);
            table.addCell(new Phrase("Examination", new Font(2, 10.0f, 1)));
            table.addCell(new Phrase("University", new Font(2, 10.0f, 1)));
            table.addCell(new Phrase("Institute", new Font(2, 10.0f, 1)));
            table.addCell(new Phrase("Year", new Font(2, 10.0f, 1)));
            table.addCell(new Phrase("CPI / %", new Font(2, 10.0f, 1)));
            StringTokenizer st = null;
            int n = -1;
            int i3 = 8;
            while (i3 < args.length) {
                st = new StringTokenizer(args[i3], ";");
                n = st.countTokens();
                while (st.hasMoreTokens()) {
                    cell = new PdfPCell(new Phrase(st.nextToken(), new Font(2, 10.0f, 0)));
                    if (i3 == 8) {
                        cell.setPaddingTop(5.0f);
                    }
                    if (n == 1) {
                        cell.setColspan(5);
                    }
                    cell.setBorderWidth(0.0f);
                    cell.setBorder(0);
                    cell.setVerticalAlignment(5);
                    cell.setUseAscender(true);
                    cell.setUseDescender(true);
                    cell.setUseBorderPadding(true);
                    if (i3 == args.length - 1) {
                        cell.setBorderWidthBottom(1.0f);
                    }
                    table.addCell(cell);
                }
                ++i3;
            }
            document.add(table);
            PdfContentByte cb = writer.getDirectContent();
            int i4 = 0;
            while (i4 < pdfReader.getNumberOfPages()) {
                cb.addTemplate(writer.getImportedPage(pdfReader, i4 + 1), 0.0f, 0.0f);
                document.newPage();
                ++i4;
            }
            System.out.println(" Done!");
        }
        catch (DocumentException de) {
            System.out.println("Exception occured while writing to pdf file! Stacktrace coming up:\n");
            de.printStackTrace();
        }
        catch (IOException ioe2) {
            System.out.println("IOException occured! Unable to read from or write to file! Make sure you have a file resume.pdf with read permissions and write permissions for the current folder. Stacktrace coming up:\n");
            ioe2.printStackTrace();
        }
        try {
            document.close();
        }
        catch (Exception ioe2) {
            // empty catch block
        }
    }
}

