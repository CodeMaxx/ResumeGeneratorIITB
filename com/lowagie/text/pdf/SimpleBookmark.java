/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.SimpleNamedDestination;
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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

public final class SimpleBookmark
implements SimpleXMLDocHandler {
    private ArrayList topList;
    private Stack attr = new Stack();

    private SimpleBookmark() {
    }

    private static List bookmarkDepth(PdfReader pdfReader, PdfDictionary pdfDictionary, IntHashtable intHashtable) {
        ArrayList arrayList = new ArrayList();
        while (pdfDictionary != null) {
            Object object;
            Object object2;
            Object object3;
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            PdfString pdfString = (PdfString)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.TITLE));
            hashMap.put("Title", pdfString.toUnicodeString());
            PdfArray pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.C));
            if (pdfArray != null && pdfArray.getArrayList().size() == 3) {
                object2 = new ByteBuffer();
                object3 = pdfArray.getArrayList();
                object2.append(((PdfNumber)object3.get(0)).floatValue()).append(' ');
                object2.append(((PdfNumber)object3.get(1)).floatValue()).append(' ');
                object2.append(((PdfNumber)object3.get(2)).floatValue());
                hashMap.put("Color", PdfEncodings.convertToString(object2.toByteArray(), null));
            }
            if ((object2 = (PdfNumber)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.F))) != null) {
                int n = object2.intValue();
                object = "";
                if ((n & 1) != 0) {
                    object = (String)object + "italic ";
                }
                if ((n & 2) != 0) {
                    object = (String)object + "bold ";
                }
                if ((object = object.trim()).length() != 0) {
                    hashMap.put("Style", object);
                }
            }
            if ((object3 = (PdfNumber)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.COUNT))) != null && object3.intValue() < 0) {
                hashMap.put("Open", "false");
            }
            try {
                object = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.DEST));
                if (object != null) {
                    SimpleBookmark.mapGotoBookmark(hashMap, (PdfObject)object, intHashtable);
                } else {
                    PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.A));
                    if (pdfDictionary2 != null) {
                        Object object4;
                        if (PdfName.GOTO.equals(PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.S)))) {
                            object = PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.D));
                            if (object != null) {
                                SimpleBookmark.mapGotoBookmark(hashMap, (PdfObject)object, intHashtable);
                            }
                        } else if (PdfName.URI.equals(PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.S)))) {
                            hashMap.put("Action", "URI");
                            hashMap.put("URI", ((PdfString)PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.URI))).toUnicodeString());
                        } else if (PdfName.GOTOR.equals(PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.S)))) {
                            Object object5;
                            object = PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.D));
                            if (object != null) {
                                if (object.isString()) {
                                    hashMap.put("Named", object.toString());
                                } else if (object.isName()) {
                                    hashMap.put("NamedN", PdfName.decodeName(object.toString()));
                                } else if (object.isArray()) {
                                    object4 = ((PdfArray)object).getArrayList();
                                    object5 = new StringBuffer();
                                    object5.append(object4.get(0).toString());
                                    object5.append(' ').append(object4.get(1).toString());
                                    for (int i = 2; i < object4.size(); ++i) {
                                        object5.append(' ').append(object4.get(i).toString());
                                    }
                                    hashMap.put("Page", object5.toString());
                                }
                            }
                            hashMap.put("Action", "GoToR");
                            object4 = PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.F));
                            if (object4 != null) {
                                if (object4.isString()) {
                                    hashMap.put("File", ((PdfString)object4).toUnicodeString());
                                } else if (object4.isDictionary() && (object4 = PdfReader.getPdfObject(((PdfDictionary)object4).get(PdfName.F))).isString()) {
                                    hashMap.put("File", ((PdfString)object4).toUnicodeString());
                                }
                            }
                            if ((object5 = PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.NEWWINDOW))) != null) {
                                hashMap.put("NewWindow", object5.toString());
                            }
                        } else if (PdfName.LAUNCH.equals(PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.S)))) {
                            hashMap.put("Action", "Launch");
                            object4 = PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.F));
                            if (object4 == null) {
                                object4 = PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.WIN));
                            }
                            if (object4 != null) {
                                if (object4.isString()) {
                                    hashMap.put("File", ((PdfString)object4).toUnicodeString());
                                } else if (object4.isDictionary() && (object4 = PdfReader.getPdfObjectRelease(((PdfDictionary)object4).get(PdfName.F))).isString()) {
                                    hashMap.put("File", ((PdfString)object4).toUnicodeString());
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception var9_11) {
                // empty catch block
            }
            object = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.FIRST));
            if (object != null) {
                hashMap.put("Kids", SimpleBookmark.bookmarkDepth(pdfReader, (PdfDictionary)object, intHashtable));
            }
            arrayList.add(hashMap);
            pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.NEXT));
        }
        return arrayList;
    }

    private static void mapGotoBookmark(HashMap hashMap, PdfObject pdfObject, IntHashtable intHashtable) {
        if (pdfObject.isString()) {
            hashMap.put("Named", pdfObject.toString());
        } else if (pdfObject.isName()) {
            hashMap.put("Named", PdfName.decodeName(pdfObject.toString()));
        } else if (pdfObject.isArray()) {
            hashMap.put("Page", SimpleBookmark.makeBookmarkParam((PdfArray)pdfObject, intHashtable));
        }
        hashMap.put("Action", "GoTo");
    }

    private static String makeBookmarkParam(PdfArray pdfArray, IntHashtable intHashtable) {
        ArrayList arrayList = pdfArray.getArrayList();
        StringBuffer stringBuffer = new StringBuffer();
        if (((PdfObject)arrayList.get(0)).isNumber()) {
            stringBuffer.append(((PdfNumber)arrayList.get(0)).intValue() + 1);
        } else {
            stringBuffer.append(intHashtable.get(SimpleBookmark.getNumber((PdfIndirectReference)arrayList.get(0))));
        }
        stringBuffer.append(' ').append(arrayList.get(1).toString().substring(1));
        for (int i = 2; i < arrayList.size(); ++i) {
            stringBuffer.append(' ').append(arrayList.get(i).toString());
        }
        return stringBuffer.toString();
    }

    private static int getNumber(PdfIndirectReference pdfIndirectReference) {
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(pdfIndirectReference);
        if (pdfDictionary.contains(PdfName.TYPE) && pdfDictionary.get(PdfName.TYPE).equals(PdfName.PAGES) && pdfDictionary.contains(PdfName.KIDS)) {
            PdfArray pdfArray = (PdfArray)pdfDictionary.get(PdfName.KIDS);
            pdfIndirectReference = (PdfIndirectReference)pdfArray.arrayList.get(0);
        }
        return pdfIndirectReference.getNumber();
    }

    public static List getBookmark(PdfReader pdfReader) {
        PdfDictionary pdfDictionary = pdfReader.getCatalog();
        PdfObject pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.OUTLINES));
        if (pdfObject == null || !pdfObject.isDictionary()) {
            return null;
        }
        PdfDictionary pdfDictionary2 = (PdfDictionary)pdfObject;
        IntHashtable intHashtable = new IntHashtable();
        int n = pdfReader.getNumberOfPages();
        for (int i = 1; i <= n; ++i) {
            intHashtable.put(pdfReader.getPageOrigRef(i).getNumber(), i);
            pdfReader.releasePage(i);
        }
        return SimpleBookmark.bookmarkDepth(pdfReader, (PdfDictionary)PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.FIRST)), intHashtable);
    }

    public static void eliminatePages(List list, int[] arrn) {
        if (list == null) {
            return;
        }
        ListIterator listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            Object object;
            HashMap hashMap = (HashMap)listIterator.next();
            boolean bl = false;
            if ("GoTo".equals(hashMap.get("Action")) && (object = (String)hashMap.get("Page")) != null) {
                int n = (object = object.trim()).indexOf(32);
                int n2 = n < 0 ? Integer.parseInt((String)object) : Integer.parseInt(object.substring(0, n));
                int n3 = arrn.length & -2;
                for (int i = 0; i < n3; i += 2) {
                    if (n2 < arrn[i] || n2 > arrn[i + 1]) continue;
                    bl = true;
                    break;
                }
            }
            if ((object = (List)hashMap.get("Kids")) != null) {
                SimpleBookmark.eliminatePages((List)object, arrn);
                if (object.isEmpty()) {
                    hashMap.remove("Kids");
                    object = null;
                }
            }
            if (!bl) continue;
            if (object == null) {
                listIterator.remove();
                continue;
            }
            hashMap.remove("Action");
            hashMap.remove("Page");
            hashMap.remove("Named");
        }
    }

    public static void shiftPageNumbers(List list, int n, int[] arrn) {
        if (list == null) {
            return;
        }
        ListIterator listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            Object object;
            HashMap hashMap = (HashMap)listIterator.next();
            if ("GoTo".equals(hashMap.get("Action")) && (object = (String)hashMap.get("Page")) != null) {
                int n2 = (object = object.trim()).indexOf(32);
                int n3 = n2 < 0 ? Integer.parseInt((String)object) : Integer.parseInt(object.substring(0, n2));
                boolean bl = false;
                if (arrn == null) {
                    bl = true;
                } else {
                    int n4 = arrn.length & -2;
                    for (int i = 0; i < n4; i += 2) {
                        if (n3 < arrn[i] || n3 > arrn[i + 1]) continue;
                        bl = true;
                        break;
                    }
                }
                if (bl) {
                    object = n2 < 0 ? Integer.toString(n3 + n) : "" + (n3 + n) + object.substring(n2);
                }
                hashMap.put("Page", object);
            }
            if ((object = (List)hashMap.get("Kids")) == null) continue;
            SimpleBookmark.shiftPageNumbers((List)object, n, arrn);
        }
    }

    static void createOutlineAction(PdfDictionary pdfDictionary, HashMap hashMap, PdfWriter pdfWriter, boolean bl) {
        try {
            String string;
            String string2 = (String)hashMap.get("Action");
            if ("GoTo".equals(string2)) {
                String string3 = (String)hashMap.get("Named");
                if (string3 != null) {
                    if (bl) {
                        pdfDictionary.put(PdfName.DEST, new PdfName(string3));
                    } else {
                        pdfDictionary.put(PdfName.DEST, new PdfString(string3, null));
                    }
                } else {
                    string3 = (String)hashMap.get("Page");
                    if (string3 != null) {
                        PdfArray pdfArray = new PdfArray();
                        StringTokenizer stringTokenizer = new StringTokenizer(string3);
                        int n = Integer.parseInt(stringTokenizer.nextToken());
                        pdfArray.add(pdfWriter.getPageReference(n));
                        if (!stringTokenizer.hasMoreTokens()) {
                            pdfArray.add(PdfName.XYZ);
                            pdfArray.add(new float[]{0.0f, 10000.0f, 0.0f});
                        } else {
                            String string4 = stringTokenizer.nextToken();
                            if (string4.startsWith("/")) {
                                string4 = string4.substring(1);
                            }
                            pdfArray.add(new PdfName(string4));
                            for (int i = 0; i < 4 && stringTokenizer.hasMoreTokens(); ++i) {
                                string4 = stringTokenizer.nextToken();
                                if (string4.equals("null")) {
                                    pdfArray.add(PdfNull.PDFNULL);
                                    continue;
                                }
                                pdfArray.add(new PdfNumber(string4));
                            }
                        }
                        pdfDictionary.put(PdfName.DEST, pdfArray);
                    }
                }
            } else if ("GoToR".equals(string2)) {
                Object object;
                Object object2;
                PdfDictionary pdfDictionary2 = new PdfDictionary();
                String string5 = (String)hashMap.get("Named");
                if (string5 != null) {
                    pdfDictionary2.put(PdfName.D, new PdfString(string5, null));
                } else {
                    string5 = (String)hashMap.get("NamedN");
                    if (string5 != null) {
                        pdfDictionary2.put(PdfName.D, new PdfName(string5));
                    } else {
                        string5 = (String)hashMap.get("Page");
                        if (string5 != null) {
                            object = new PdfArray();
                            object2 = new StringTokenizer(string5);
                            object.add(new PdfNumber(object2.nextToken()));
                            if (!object2.hasMoreTokens()) {
                                object.add(PdfName.XYZ);
                                object.add(new float[]{0.0f, 10000.0f, 0.0f});
                            } else {
                                String string6 = object2.nextToken();
                                if (string6.startsWith("/")) {
                                    string6 = string6.substring(1);
                                }
                                object.add(new PdfName(string6));
                                for (int i = 0; i < 4 && object2.hasMoreTokens(); ++i) {
                                    string6 = object2.nextToken();
                                    if (string6.equals("null")) {
                                        object.add(PdfNull.PDFNULL);
                                        continue;
                                    }
                                    object.add(new PdfNumber(string6));
                                }
                            }
                            pdfDictionary2.put(PdfName.D, (PdfObject)object);
                        }
                    }
                }
                object = (String)hashMap.get("File");
                if (pdfDictionary2.size() > 0 && object != null) {
                    pdfDictionary2.put(PdfName.S, PdfName.GOTOR);
                    pdfDictionary2.put(PdfName.F, new PdfString((String)object));
                    object2 = (String)hashMap.get("NewWindow");
                    if (object2 != null) {
                        if (object2.equals("true")) {
                            pdfDictionary2.put(PdfName.NEWWINDOW, PdfBoolean.PDFTRUE);
                        } else if (object2.equals("false")) {
                            pdfDictionary2.put(PdfName.NEWWINDOW, PdfBoolean.PDFFALSE);
                        }
                    }
                    pdfDictionary.put(PdfName.A, pdfDictionary2);
                }
            } else if ("URI".equals(string2)) {
                String string7 = (String)hashMap.get("URI");
                if (string7 != null) {
                    PdfDictionary pdfDictionary3 = new PdfDictionary();
                    pdfDictionary3.put(PdfName.S, PdfName.URI);
                    pdfDictionary3.put(PdfName.URI, new PdfString(string7));
                    pdfDictionary.put(PdfName.A, pdfDictionary3);
                }
            } else if ("Launch".equals(string2) && (string = (String)hashMap.get("File")) != null) {
                PdfDictionary pdfDictionary4 = new PdfDictionary();
                pdfDictionary4.put(PdfName.S, PdfName.LAUNCH);
                pdfDictionary4.put(PdfName.F, new PdfString(string));
                pdfDictionary.put(PdfName.A, pdfDictionary4);
            }
        }
        catch (Exception var4_5) {
            // empty catch block
        }
    }

    public static Object[] iterateOutlines(PdfWriter pdfWriter, PdfIndirectReference pdfIndirectReference, List list, boolean bl) throws IOException {
        int n;
        PdfIndirectReference[] arrpdfIndirectReference = new PdfIndirectReference[list.size()];
        for (n = 0; n < arrpdfIndirectReference.length; ++n) {
            arrpdfIndirectReference[n] = pdfWriter.getPdfIndirectReference();
        }
        n = 0;
        int n2 = 0;
        ListIterator listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            Object object;
            HashMap hashMap = (HashMap)listIterator.next();
            Object[] arrobject = null;
            List list2 = (List)hashMap.get("Kids");
            if (list2 != null && !list2.isEmpty()) {
                arrobject = SimpleBookmark.iterateOutlines(pdfWriter, arrpdfIndirectReference[n], list2, bl);
            }
            PdfDictionary pdfDictionary = new PdfDictionary();
            ++n2;
            if (arrobject != null) {
                pdfDictionary.put(PdfName.FIRST, (PdfIndirectReference)arrobject[0]);
                pdfDictionary.put(PdfName.LAST, (PdfIndirectReference)arrobject[1]);
                int n3 = (Integer)arrobject[2];
                if ("false".equals(hashMap.get("Open"))) {
                    pdfDictionary.put(PdfName.COUNT, new PdfNumber(- n3));
                } else {
                    pdfDictionary.put(PdfName.COUNT, new PdfNumber(n3));
                    n2 += n3;
                }
            }
            pdfDictionary.put(PdfName.PARENT, pdfIndirectReference);
            if (n > 0) {
                pdfDictionary.put(PdfName.PREV, arrpdfIndirectReference[n - 1]);
            }
            if (n < arrpdfIndirectReference.length - 1) {
                pdfDictionary.put(PdfName.NEXT, arrpdfIndirectReference[n + 1]);
            }
            pdfDictionary.put(PdfName.TITLE, new PdfString((String)hashMap.get("Title"), "UnicodeBig"));
            String string = (String)hashMap.get("Color");
            if (string != null) {
                try {
                    object = new PdfArray();
                    StringTokenizer stringTokenizer = new StringTokenizer(string);
                    for (int i = 0; i < 3; ++i) {
                        float f = Float.parseFloat(stringTokenizer.nextToken());
                        if (f < 0.0f) {
                            f = 0.0f;
                        }
                        if (f > 1.0f) {
                            f = 1.0f;
                        }
                        object.add(new PdfNumber(f));
                    }
                    pdfDictionary.put(PdfName.C, (PdfObject)object);
                }
                catch (Exception var13_15) {
                    // empty catch block
                }
            }
            if ((object = (String)hashMap.get("Style")) != null) {
                object = object.toLowerCase();
                int n4 = 0;
                if (object.indexOf("italic") >= 0) {
                    n4 |= true;
                }
                if (object.indexOf("bold") >= 0) {
                    n4 |= 2;
                }
                if (n4 != 0) {
                    pdfDictionary.put(PdfName.F, new PdfNumber(n4));
                }
            }
            SimpleBookmark.createOutlineAction(pdfDictionary, hashMap, pdfWriter, bl);
            pdfWriter.addToBody((PdfObject)pdfDictionary, arrpdfIndirectReference[n]);
            ++n;
        }
        return new Object[]{arrpdfIndirectReference[0], arrpdfIndirectReference[arrpdfIndirectReference.length - 1], new Integer(n2)};
    }

    public static void exportToXMLNode(List list, Writer writer, int n, boolean bl) throws IOException {
        String string = "";
        for (int i = 0; i < n; ++i) {
            string = string + "  ";
        }
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            HashMap hashMap = (HashMap)iterator.next();
            String string2 = null;
            writer.write(string);
            writer.write("<Title ");
            List list2 = null;
            Iterator iterator2 = hashMap.entrySet().iterator();
            while (iterator2.hasNext()) {
                Map.Entry entry = iterator2.next();
                String string3 = (String)entry.getKey();
                if (string3.equals("Title")) {
                    string2 = (String)entry.getValue();
                    continue;
                }
                if (string3.equals("Kids")) {
                    list2 = (List)entry.getValue();
                    continue;
                }
                writer.write(string3);
                writer.write("=\"");
                String string4 = (String)entry.getValue();
                if (string3.equals("Named") || string3.equals("NamedN")) {
                    string4 = SimpleNamedDestination.escapeBinaryString(string4);
                }
                writer.write(SimpleXMLParser.escapeXML(string4, bl));
                writer.write("\" ");
            }
            writer.write(">");
            if (string2 == null) {
                string2 = "";
            }
            writer.write(SimpleXMLParser.escapeXML(string2, bl));
            if (list2 != null) {
                writer.write("\n");
                SimpleBookmark.exportToXMLNode(list2, writer, n + 1, bl);
                writer.write(string);
            }
            writer.write("</Title>\n");
        }
    }

    public static void exportToXML(List list, OutputStream outputStream, String string, boolean bl) throws IOException {
        String string2 = IanaEncodings.getJavaEncoding(string);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, string2));
        SimpleBookmark.exportToXML(list, bufferedWriter, string, bl);
    }

    public static void exportToXML(List list, Writer writer, String string, boolean bl) throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"");
        writer.write(SimpleXMLParser.escapeXML(string, bl));
        writer.write("\"?>\n<Bookmark>\n");
        SimpleBookmark.exportToXMLNode(list, writer, 1, bl);
        writer.write("</Bookmark>\n");
        writer.flush();
    }

    public static List importFromXML(InputStream inputStream) throws IOException {
        SimpleBookmark simpleBookmark = new SimpleBookmark();
        SimpleXMLParser.parse((SimpleXMLDocHandler)simpleBookmark, inputStream);
        return simpleBookmark.topList;
    }

    public static List importFromXML(Reader reader) throws IOException {
        SimpleBookmark simpleBookmark = new SimpleBookmark();
        SimpleXMLParser.parse((SimpleXMLDocHandler)simpleBookmark, reader);
        return simpleBookmark.topList;
    }

    public void endDocument() {
    }

    public void endElement(String string) {
        if (string.equals("Bookmark")) {
            if (this.attr.isEmpty()) {
                return;
            }
            throw new RuntimeException("Bookmark end tag out of place.");
        }
        if (!string.equals("Title")) {
            throw new RuntimeException("Invalid end tag - " + string);
        }
        HashMap hashMap = (HashMap)this.attr.pop();
        String string2 = (String)hashMap.get("Title");
        hashMap.put("Title", string2.trim());
        String string3 = (String)hashMap.get("Named");
        if (string3 != null) {
            hashMap.put("Named", SimpleNamedDestination.unEscapeBinaryString(string3));
        }
        if ((string3 = (String)hashMap.get("NamedN")) != null) {
            hashMap.put("NamedN", SimpleNamedDestination.unEscapeBinaryString(string3));
        }
        if (this.attr.isEmpty()) {
            this.topList.add(hashMap);
        } else {
            HashMap hashMap2 = (HashMap)this.attr.peek();
            ArrayList<HashMap> arrayList = (ArrayList<HashMap>)hashMap2.get("Kids");
            if (arrayList == null) {
                arrayList = new ArrayList<HashMap>();
                hashMap2.put("Kids", arrayList);
            }
            arrayList.add(hashMap);
        }
    }

    public void startDocument() {
    }

    public void startElement(String string, HashMap hashMap) {
        if (this.topList == null) {
            if (string.equals("Bookmark")) {
                this.topList = new ArrayList();
                return;
            }
            throw new RuntimeException("Root element is not Bookmark: " + string);
        }
        if (!string.equals("Title")) {
            throw new RuntimeException("Tag " + string + " not allowed.");
        }
        HashMap<String, String> hashMap2 = new HashMap<String, String>(hashMap);
        hashMap2.put("Title", "");
        hashMap2.remove("Kids");
        this.attr.push(hashMap2);
    }

    public void text(String string) {
        if (this.attr.isEmpty()) {
            return;
        }
        HashMap hashMap = (HashMap)this.attr.peek();
        String string2 = (String)hashMap.get("Title");
        string2 = string2 + string;
        hashMap.put("Title", string2);
    }
}

