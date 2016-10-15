/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNameTree;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.xml.simpleparser.IanaEncodings;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public final class SimpleNamedDestination
implements SimpleXMLDocHandler {
    private HashMap xmlNames;
    private HashMap xmlLast;

    private SimpleNamedDestination() {
    }

    public static HashMap getNamedDestination(PdfReader pdfReader, boolean bl) {
        IntHashtable intHashtable = new IntHashtable();
        int n = pdfReader.getNumberOfPages();
        for (int i = 1; i <= n; ++i) {
            intHashtable.put(pdfReader.getPageOrigRef(i).getNumber(), i);
        }
        HashMap hashMap = bl ? pdfReader.getNamedDestinationFromNames() : pdfReader.getNamedDestinationFromStrings();
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            ArrayList arrayList = ((PdfArray)entry.getValue()).getArrayList();
            StringBuffer stringBuffer = new StringBuffer();
            try {
                stringBuffer.append(intHashtable.get(((PdfIndirectReference)arrayList.get(0)).getNumber()));
                stringBuffer.append(' ').append(arrayList.get(1).toString().substring(1));
                for (int j = 2; j < arrayList.size(); ++j) {
                    stringBuffer.append(' ').append(arrayList.get(j).toString());
                }
                entry.setValue(stringBuffer.toString());
            }
            catch (Exception var9_11) {
                iterator.remove();
            }
        }
        return hashMap;
    }

    public static void exportToXML(HashMap hashMap, OutputStream outputStream, String string, boolean bl) throws IOException {
        String string2 = IanaEncodings.getJavaEncoding(string);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, string2));
        SimpleNamedDestination.exportToXML(hashMap, bufferedWriter, string, bl);
    }

    public static void exportToXML(HashMap hashMap, Writer writer, String string, boolean bl) throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"");
        writer.write(SimpleXMLParser.escapeXML(string, bl));
        writer.write("\"?>\n<Destination>\n");
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String string2 = (String)entry.getKey();
            String string3 = (String)entry.getValue();
            writer.write("  <Name Page=\"");
            writer.write(SimpleXMLParser.escapeXML(string3, bl));
            writer.write("\">");
            writer.write(SimpleXMLParser.escapeXML(SimpleNamedDestination.escapeBinaryString(string2), bl));
            writer.write("</Name>\n");
        }
        writer.write("</Destination>\n");
        writer.flush();
    }

    public static HashMap importFromXML(InputStream inputStream) throws IOException {
        SimpleNamedDestination simpleNamedDestination = new SimpleNamedDestination();
        SimpleXMLParser.parse((SimpleXMLDocHandler)simpleNamedDestination, inputStream);
        return simpleNamedDestination.xmlNames;
    }

    public static HashMap importFromXML(Reader reader) throws IOException {
        SimpleNamedDestination simpleNamedDestination = new SimpleNamedDestination();
        SimpleXMLParser.parse((SimpleXMLDocHandler)simpleNamedDestination, reader);
        return simpleNamedDestination.xmlNames;
    }

    static PdfArray createDestinationArray(String string, PdfWriter pdfWriter) {
        PdfArray pdfArray = new PdfArray();
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        int n = Integer.parseInt(stringTokenizer.nextToken());
        pdfArray.add(pdfWriter.getPageReference(n));
        if (!stringTokenizer.hasMoreTokens()) {
            pdfArray.add(PdfName.XYZ);
            pdfArray.add(new float[]{0.0f, 10000.0f, 0.0f});
        } else {
            String string2 = stringTokenizer.nextToken();
            if (string2.startsWith("/")) {
                string2 = string2.substring(1);
            }
            pdfArray.add(new PdfName(string2));
            for (int i = 0; i < 4 && stringTokenizer.hasMoreTokens(); ++i) {
                string2 = stringTokenizer.nextToken();
                if (string2.equals("null")) {
                    pdfArray.add(PdfNull.PDFNULL);
                    continue;
                }
                pdfArray.add(new PdfNumber(string2));
            }
        }
        return pdfArray;
    }

    public static PdfDictionary outputNamedDestinationAsNames(HashMap hashMap, PdfWriter pdfWriter) {
        PdfDictionary pdfDictionary = new PdfDictionary();
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            try {
                String string = (String)entry.getKey();
                String string2 = (String)entry.getValue();
                PdfArray pdfArray = SimpleNamedDestination.createDestinationArray(string2, pdfWriter);
                PdfName pdfName = new PdfName(string);
                pdfDictionary.put(pdfName, pdfArray);
            }
            catch (Exception var5_6) {}
        }
        return pdfDictionary;
    }

    public static PdfDictionary outputNamedDestinationAsStrings(HashMap hashMap, PdfWriter pdfWriter) throws IOException {
        HashMap hashMap2 = new HashMap(hashMap);
        Iterator iterator = hashMap2.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            try {
                String string = (String)entry.getValue();
                PdfArray pdfArray = SimpleNamedDestination.createDestinationArray(string, pdfWriter);
                entry.setValue(pdfWriter.addToBody(pdfArray).getIndirectReference());
            }
            catch (Exception var5_6) {
                iterator.remove();
            }
        }
        return PdfNameTree.writeTree(hashMap2, pdfWriter);
    }

    public static String escapeBinaryString(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        char[] arrc = string.toCharArray();
        int n = arrc.length;
        for (int i = 0; i < n; ++i) {
            char c = arrc[i];
            if (c < ' ') {
                stringBuffer.append('\\');
                String string2 = "00" + Integer.toOctalString(c);
                stringBuffer.append(string2.substring(string2.length() - 3));
                continue;
            }
            if (c == '\\') {
                stringBuffer.append("\\\\");
                continue;
            }
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    public static String unEscapeBinaryString(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        char[] arrc = string.toCharArray();
        int n = arrc.length;
        for (int i = 0; i < n; ++i) {
            char c = arrc[i];
            if (c == '\\') {
                if (++i >= n) {
                    stringBuffer.append('\\');
                    break;
                }
                c = arrc[i];
                if (c >= '0' && c <= '7') {
                    int n2 = c - 48;
                    ++i;
                    for (int j = 0; j < 2 && i < n && (c = arrc[i]) >= '0' && c <= '7'; ++i, ++j) {
                        n2 = n2 * 8 + c - 48;
                    }
                    --i;
                    stringBuffer.append((char)n2);
                    continue;
                }
                stringBuffer.append(c);
                continue;
            }
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    public void endDocument() {
    }

    public void endElement(String string) {
        if (string.equals("Destination")) {
            if (this.xmlLast == null && this.xmlNames != null) {
                return;
            }
            throw new RuntimeException("Destination end tag out of place.");
        }
        if (!string.equals("Name")) {
            throw new RuntimeException("Invalid end tag - " + string);
        }
        if (this.xmlLast == null || this.xmlNames == null) {
            throw new RuntimeException("Name end tag out of place.");
        }
        if (!this.xmlLast.containsKey("Page")) {
            throw new RuntimeException("Page attribute missing.");
        }
        this.xmlNames.put(SimpleNamedDestination.unEscapeBinaryString((String)this.xmlLast.get("Name")), this.xmlLast.get("Page"));
        this.xmlLast = null;
    }

    public void startDocument() {
    }

    public void startElement(String string, HashMap hashMap) {
        if (this.xmlNames == null) {
            if (string.equals("Destination")) {
                this.xmlNames = new HashMap();
                return;
            }
            throw new RuntimeException("Root element is not Destination.");
        }
        if (!string.equals("Name")) {
            throw new RuntimeException("Tag " + string + " not allowed.");
        }
        if (this.xmlLast != null) {
            throw new RuntimeException("Nested tags are not allowed.");
        }
        this.xmlLast = new HashMap(hashMap);
        this.xmlLast.put("Name", "");
    }

    public void text(String string) {
        if (this.xmlLast == null) {
            return;
        }
        String string2 = (String)this.xmlLast.get("Name");
        string2 = string2 + string;
        this.xmlLast.put("Name", string2);
    }
}

