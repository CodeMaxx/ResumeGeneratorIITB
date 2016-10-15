/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseField;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.DocumentFont;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.FdfReader;
import com.lowagie.text.pdf.FdfWriter;
import com.lowagie.text.pdf.FontDetails;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamperImp;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PushbuttonField;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.TextField;
import com.lowagie.text.pdf.XfaForm;
import com.lowagie.text.pdf.XfdfReader;
import com.lowagie.text.pdf.codec.Base64;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Node;

public class AcroFields {
    PdfReader reader;
    PdfWriter writer;
    HashMap fields;
    private int topFirst;
    private HashMap sigNames;
    private boolean append;
    public static final int DA_FONT = 0;
    public static final int DA_SIZE = 1;
    public static final int DA_COLOR = 2;
    private HashMap extensionFonts = new HashMap();
    private XfaForm xfa;
    public static final int FIELD_TYPE_NONE = 0;
    public static final int FIELD_TYPE_PUSHBUTTON = 1;
    public static final int FIELD_TYPE_CHECKBOX = 2;
    public static final int FIELD_TYPE_RADIOBUTTON = 3;
    public static final int FIELD_TYPE_TEXT = 4;
    public static final int FIELD_TYPE_LIST = 5;
    public static final int FIELD_TYPE_COMBO = 6;
    public static final int FIELD_TYPE_SIGNATURE = 7;
    private boolean lastWasString;
    private boolean generateAppearances = true;
    private HashMap localFonts = new HashMap();
    private float extraMarginLeft;
    private float extraMarginTop;
    private ArrayList substitutionFonts;
    private static final HashMap stdFieldFontNames = new HashMap();
    private int totalRevisions;
    private HashMap fieldCache;
    private static final PdfName[] buttonRemove;

    AcroFields(PdfReader pdfReader, PdfWriter pdfWriter) {
        this.reader = pdfReader;
        this.writer = pdfWriter;
        try {
            this.xfa = new XfaForm(pdfReader);
        }
        catch (Exception var3_3) {
            throw new ExceptionConverter(var3_3);
        }
        if (pdfWriter instanceof PdfStamperImp) {
            this.append = ((PdfStamperImp)pdfWriter).isAppend();
        }
        this.fill();
    }

    void fill() {
        this.fields = new HashMap();
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObjectRelease(this.reader.getCatalog().get(PdfName.ACROFORM));
        if (pdfDictionary == null) {
            return;
        }
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.FIELDS));
        if (pdfArray == null || pdfArray.size() == 0) {
            return;
        }
        pdfArray = null;
        for (int i = 1; i <= this.reader.getNumberOfPages(); ++i) {
            PdfDictionary pdfDictionary2 = this.reader.getPageNRelease(i);
            PdfArray pdfArray2 = (PdfArray)PdfReader.getPdfObjectRelease(pdfDictionary2.get(PdfName.ANNOTS), pdfDictionary2);
            if (pdfArray2 == null) continue;
            ArrayList arrayList = pdfArray2.getArrayList();
            for (int j = 0; j < arrayList.size(); ++j) {
                Object object;
                PdfObject pdfObject = PdfReader.getPdfObject((PdfObject)arrayList.get(j), pdfArray2);
                if (!(pdfObject instanceof PdfDictionary)) {
                    PdfReader.releaseLastXrefPartial((PdfObject)arrayList.get(j));
                    continue;
                }
                PdfDictionary pdfDictionary3 = (PdfDictionary)pdfObject;
                if (!PdfName.WIDGET.equals(pdfDictionary3.get(PdfName.SUBTYPE))) {
                    PdfReader.releaseLastXrefPartial((PdfObject)arrayList.get(j));
                    continue;
                }
                PdfDictionary pdfDictionary4 = pdfDictionary3;
                PdfDictionary pdfDictionary5 = new PdfDictionary();
                pdfDictionary5.putAll(pdfDictionary3);
                String string = "";
                PdfDictionary pdfDictionary6 = null;
                PdfObject pdfObject2 = null;
                while (pdfDictionary3 != null) {
                    pdfDictionary5.mergeDifferent(pdfDictionary3);
                    object = (PdfString)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.T));
                    if (object != null) {
                        string = object.toUnicodeString() + "." + string;
                    }
                    if (pdfObject2 == null && pdfDictionary3.get(PdfName.V) != null) {
                        pdfObject2 = PdfReader.getPdfObjectRelease(pdfDictionary3.get(PdfName.V));
                    }
                    if (pdfDictionary6 == null && object != null) {
                        pdfDictionary6 = pdfDictionary3;
                        if (pdfDictionary3.get(PdfName.V) == null && pdfObject2 != null) {
                            pdfDictionary6.put(PdfName.V, pdfObject2);
                        }
                    }
                    pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.PARENT), pdfDictionary3);
                }
                if (string.length() > 0) {
                    string = string.substring(0, string.length() - 1);
                }
                if ((object = (Item)this.fields.get(string)) == null) {
                    object = new Item();
                    this.fields.put(string, object);
                }
                if (pdfDictionary6 == null) {
                    object.values.add(pdfDictionary4);
                } else {
                    object.values.add(pdfDictionary6);
                }
                object.widgets.add(pdfDictionary4);
                object.widget_refs.add(arrayList.get(j));
                if (pdfDictionary != null) {
                    pdfDictionary5.mergeDifferent(pdfDictionary);
                }
                object.merged.add(pdfDictionary5);
                object.page.add(new Integer(i));
                object.tabOrder.add(new Integer(j));
            }
        }
    }

    public String[] getAppearanceStates(String string) {
        ArrayList arrayList;
        int n;
        Item item = (Item)this.fields.get(string);
        if (item == null) {
            return null;
        }
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        PdfDictionary pdfDictionary2 = (PdfDictionary)item.values.get(0);
        PdfObject pdfObject = PdfReader.getPdfObject(pdfDictionary2.get(PdfName.OPT));
        if (pdfObject != null) {
            if (pdfObject.isString()) {
                hashMap.put(((PdfString)pdfObject).toUnicodeString(), null);
            } else if (pdfObject.isArray()) {
                arrayList = ((PdfArray)pdfObject).getArrayList();
                for (n = 0; n < arrayList.size(); ++n) {
                    PdfObject pdfDictionary = PdfReader.getPdfObject((PdfObject)arrayList.get(n));
                    if (pdfDictionary == null || !pdfDictionary.isString()) continue;
                    hashMap.put(((PdfString)pdfDictionary).toUnicodeString(), null);
                }
            }
        }
        arrayList = item.widgets;
        for (n = 0; n < arrayList.size(); ++n) {
            PdfObject pdfObject2;
            PdfDictionary pdfDictionary = (PdfDictionary)arrayList.get(n);
            PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.AP));
            if (pdfDictionary3 == null || (pdfObject2 = PdfReader.getPdfObject(pdfDictionary3.get(PdfName.N))) == null || !pdfObject2.isDictionary()) continue;
            PdfDictionary pdfDictionary4 = (PdfDictionary)pdfObject2;
            Iterator iterator = pdfDictionary4.getKeys().iterator();
            while (iterator.hasNext()) {
                String string2 = PdfName.decodeName(((PdfName)iterator.next()).toString());
                hashMap.put(string2, null);
            }
        }
        String[] arrstring = new String[hashMap.size()];
        return hashMap.keySet().toArray(arrstring);
    }

    private String[] getListOption(String string, int n) {
        Item item = this.getFieldItem(string);
        if (item == null) {
            return null;
        }
        PdfObject pdfObject = PdfReader.getPdfObject(((PdfDictionary)item.merged.get(0)).get(PdfName.OPT));
        if (pdfObject == null || !pdfObject.isArray()) {
            return null;
        }
        PdfArray pdfArray = (PdfArray)pdfObject;
        String[] arrstring = new String[pdfArray.size()];
        ArrayList arrayList = pdfArray.getArrayList();
        for (int i = 0; i < arrayList.size(); ++i) {
            pdfObject = PdfReader.getPdfObject((PdfObject)arrayList.get(i));
            try {
                if (pdfObject.isArray()) {
                    pdfObject = (PdfObject)((PdfArray)pdfObject).getArrayList().get(n);
                }
                if (pdfObject.isString()) {
                    arrstring[i] = ((PdfString)pdfObject).toUnicodeString();
                    continue;
                }
                arrstring[i] = pdfObject.toString();
                continue;
            }
            catch (Exception var9_9) {
                arrstring[i] = "";
            }
        }
        return arrstring;
    }

    public String[] getListOptionExport(String string) {
        return this.getListOption(string, 0);
    }

    public String[] getListOptionDisplay(String string) {
        return this.getListOption(string, 1);
    }

    public boolean setListOption(String string, String[] arrstring, String[] arrstring2) {
        int n;
        if (arrstring == null && arrstring2 == null) {
            return false;
        }
        if (arrstring != null && arrstring2 != null && arrstring.length != arrstring2.length) {
            throw new IllegalArgumentException("The export and the display array must have the same size.");
        }
        int n2 = this.getFieldType(string);
        if (n2 != 6 && n2 != 5) {
            return false;
        }
        Item item = (Item)this.fields.get(string);
        String[] arrstring3 = null;
        if (arrstring == null && arrstring2 != null) {
            arrstring3 = arrstring2;
        } else if (arrstring != null && arrstring2 == null) {
            arrstring3 = arrstring;
        }
        PdfArray pdfArray = new PdfArray();
        if (arrstring3 != null) {
            for (n = 0; n < arrstring3.length; ++n) {
                pdfArray.add(new PdfString(arrstring3[n], "UnicodeBig"));
            }
        } else {
            for (n = 0; n < arrstring.length; ++n) {
                PdfArray pdfArray2 = new PdfArray();
                pdfArray2.add(new PdfString(arrstring[n], "UnicodeBig"));
                pdfArray2.add(new PdfString(arrstring2[n], "UnicodeBig"));
                pdfArray.add(pdfArray2);
            }
        }
        ((PdfDictionary)item.values.get(0)).put(PdfName.OPT, pdfArray);
        for (n = 0; n < item.merged.size(); ++n) {
            ((PdfDictionary)item.merged.get(n)).put(PdfName.OPT, pdfArray);
        }
        return true;
    }

    public int getFieldType(String string) {
        Item item = this.getFieldItem(string);
        if (item == null) {
            return 0;
        }
        PdfObject pdfObject = PdfReader.getPdfObject(((PdfDictionary)item.merged.get(0)).get(PdfName.FT));
        if (pdfObject == null) {
            return 0;
        }
        int n = 0;
        PdfObject pdfObject2 = PdfReader.getPdfObject(((PdfDictionary)item.merged.get(0)).get(PdfName.FF));
        if (pdfObject2 != null && pdfObject2.type() == 2) {
            n = ((PdfNumber)pdfObject2).intValue();
        }
        if (PdfName.BTN.equals(pdfObject)) {
            if ((n & 65536) != 0) {
                return 1;
            }
            if ((n & 32768) != 0) {
                return 3;
            }
            return 2;
        }
        if (PdfName.TX.equals(pdfObject)) {
            return 4;
        }
        if (PdfName.CH.equals(pdfObject)) {
            if ((n & 131072) != 0) {
                return 6;
            }
            return 5;
        }
        if (PdfName.SIG.equals(pdfObject)) {
            return 7;
        }
        return 0;
    }

    public void exportAsFdf(FdfWriter fdfWriter) {
        Iterator iterator = this.fields.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            Item item = (Item)entry.getValue();
            String string = (String)entry.getKey();
            PdfObject pdfObject = PdfReader.getPdfObject(((PdfDictionary)item.merged.get(0)).get(PdfName.V));
            if (pdfObject == null) continue;
            String string2 = this.getField(string);
            if (this.lastWasString) {
                fdfWriter.setFieldAsString(string, string2);
                continue;
            }
            fdfWriter.setFieldAsName(string, string2);
        }
    }

    public boolean renameField(String string, String string2) {
        int n;
        int n2 = string.lastIndexOf(46) + 1;
        if (n2 != (n = string2.lastIndexOf(46) + 1)) {
            return false;
        }
        if (!string.substring(0, n2).equals(string2.substring(0, n))) {
            return false;
        }
        if (this.fields.containsKey(string2)) {
            return false;
        }
        Item item = (Item)this.fields.get(string);
        if (item == null) {
            return false;
        }
        string2 = string2.substring(n);
        PdfString pdfString = new PdfString(string2, "UnicodeBig");
        for (int i = 0; i < item.merged.size(); ++i) {
            PdfDictionary pdfDictionary = (PdfDictionary)item.values.get(i);
            pdfDictionary.put(PdfName.T, pdfString);
            this.markUsed(pdfDictionary);
            pdfDictionary = (PdfDictionary)item.merged.get(i);
            pdfDictionary.put(PdfName.T, pdfString);
        }
        this.fields.remove(string);
        this.fields.put(string2, item);
        return true;
    }

    public static Object[] splitDAelements(String string) {
        try {
            PRTokeniser pRTokeniser = new PRTokeniser(PdfEncodings.convertToBytes(string, null));
            ArrayList<String> arrayList = new ArrayList<String>();
            Object[] arrobject = new Object[3];
            while (pRTokeniser.nextToken()) {
                if (pRTokeniser.getTokenType() == 4) continue;
                if (pRTokeniser.getTokenType() == 10) {
                    float f;
                    float f2;
                    float f3;
                    String string2 = pRTokeniser.getStringValue();
                    if (string2.equals("Tf")) {
                        if (arrayList.size() >= 2) {
                            arrobject[0] = arrayList.get(arrayList.size() - 2);
                            arrobject[1] = new Float((String)arrayList.get(arrayList.size() - 1));
                        }
                    } else if (string2.equals("g")) {
                        if (arrayList.size() >= 1 && (f2 = new Float((String)arrayList.get(arrayList.size() - 1)).floatValue()) != 0.0f) {
                            arrobject[2] = new GrayColor(f2);
                        }
                    } else if (string2.equals("rg")) {
                        if (arrayList.size() >= 3) {
                            f2 = new Float((String)arrayList.get(arrayList.size() - 3)).floatValue();
                            f3 = new Float((String)arrayList.get(arrayList.size() - 2)).floatValue();
                            f = new Float((String)arrayList.get(arrayList.size() - 1)).floatValue();
                            arrobject[2] = new Color(f2, f3, f);
                        }
                    } else if (string2.equals("k") && arrayList.size() >= 4) {
                        f2 = new Float((String)arrayList.get(arrayList.size() - 4)).floatValue();
                        f3 = new Float((String)arrayList.get(arrayList.size() - 3)).floatValue();
                        f = new Float((String)arrayList.get(arrayList.size() - 2)).floatValue();
                        float f4 = new Float((String)arrayList.get(arrayList.size() - 1)).floatValue();
                        arrobject[2] = new CMYKColor(f2, f3, f, f4);
                    }
                    arrayList.clear();
                    continue;
                }
                arrayList.add(pRTokeniser.getStringValue());
            }
            return arrobject;
        }
        catch (IOException var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    public void decodeGenericDictionary(PdfDictionary pdfDictionary, BaseField baseField) throws IOException, DocumentException {
        PdfObject pdfObject;
        Object object;
        Object object2;
        Object[] arrobject;
        Object object3;
        int n = 0;
        PdfString pdfString = (PdfString)PdfReader.getPdfObject(pdfDictionary.get(PdfName.DA));
        if (pdfString != null) {
            arrobject = AcroFields.splitDAelements(pdfString.toUnicodeString());
            if (arrobject[1] != null) {
                baseField.setFontSize(((Float)arrobject[1]).floatValue());
            }
            if (arrobject[2] != null) {
                baseField.setTextColor((Color)arrobject[2]);
            }
            if (arrobject[0] != null && (pdfObject = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.DR))) != null && (pdfObject = (PdfDictionary)PdfReader.getPdfObject(pdfObject.get(PdfName.FONT))) != null) {
                object2 = pdfObject.get(new PdfName((String)arrobject[0]));
                if (object2 != null && object2.type() == 10) {
                    PdfDictionary pdfDictionary2;
                    PdfDictionary pdfDictionary3;
                    object3 = (PRIndirectReference)object2;
                    object = new String[]((PRIndirectReference)object2);
                    baseField.setFont((BaseFont)object);
                    Integer n2 = new Integer(object3.getNumber());
                    BaseFont baseFont = (BaseFont)this.extensionFonts.get(n2);
                    if (baseFont == null && !this.extensionFonts.containsKey(n2) && (pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject((pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject((PdfObject)object2)).get(PdfName.FONTDESCRIPTOR))) != null) {
                        PRStream pRStream = (PRStream)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.FONTFILE2));
                        if (pRStream == null) {
                            pRStream = (PRStream)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.FONTFILE3));
                        }
                        if (pRStream == null) {
                            this.extensionFonts.put(n2, null);
                        } else {
                            try {
                                baseFont = BaseFont.createFont("font.ttf", "Identity-H", true, false, PdfReader.getStreamBytes(pRStream), null);
                            }
                            catch (Exception var15_18) {
                                // empty catch block
                            }
                            this.extensionFonts.put(n2, baseFont);
                        }
                    }
                    if (baseField instanceof TextField) {
                        ((TextField)baseField).setExtensionFont(baseFont);
                    }
                } else {
                    object3 = (BaseFont)this.localFonts.get(arrobject[0]);
                    if (object3 == null) {
                        object = (String[])stdFieldFontNames.get(arrobject[0]);
                        if (object != null) {
                            try {
                                Object object4 = "winansi";
                                if (object.length > 1) {
                                    object4 = object[1];
                                }
                                object3 = BaseFont.createFont((String)object[0], (String)object4, false);
                                baseField.setFont((BaseFont)object3);
                            }
                            catch (Exception var10_13) {}
                        }
                    } else {
                        baseField.setFont((BaseFont)object3);
                    }
                }
            }
        }
        if ((arrobject = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.MK))) != null) {
            pdfObject = (PdfArray)PdfReader.getPdfObject(arrobject.get(PdfName.BC));
            object2 = this.getMKColor((PdfArray)pdfObject);
            baseField.setBorderColor((Color)object2);
            if (object2 != null) {
                baseField.setBorderWidth(1.0f);
            }
            pdfObject = (PdfArray)PdfReader.getPdfObject(arrobject.get(PdfName.BG));
            baseField.setBackgroundColor(this.getMKColor((PdfArray)pdfObject));
            object3 = (PdfNumber)PdfReader.getPdfObject(arrobject.get(PdfName.R));
            if (object3 != null) {
                baseField.setRotation(object3.intValue());
            }
        }
        pdfObject = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.F));
        n = 0;
        baseField.setVisibility(2);
        if (pdfObject != null) {
            n = pdfObject.intValue();
            if ((n & 4) != 0 && (n & 2) != 0) {
                baseField.setVisibility(1);
            } else if ((n & 4) != 0 && (n & 32) != 0) {
                baseField.setVisibility(3);
            } else if ((n & 4) != 0) {
                baseField.setVisibility(0);
            }
        }
        pdfObject = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.FF));
        n = 0;
        if (pdfObject != null) {
            n = pdfObject.intValue();
        }
        baseField.setOptions(n);
        if ((n & 16777216) != 0) {
            object2 = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.MAXLEN));
            int n3 = 0;
            if (object2 != null) {
                n3 = object2.intValue();
            }
            baseField.setMaxCharacterLength(n3);
        }
        if ((pdfObject = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.Q))) != null) {
            if (pdfObject.intValue() == 1) {
                baseField.setAlignment(1);
            } else if (pdfObject.intValue() == 2) {
                baseField.setAlignment(2);
            }
        }
        if ((object2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.BS))) != null) {
            object3 = (PdfNumber)PdfReader.getPdfObject(object2.get(PdfName.W));
            if (object3 != null) {
                baseField.setBorderWidth(object3.floatValue());
            }
            if (PdfName.D.equals(object = (PdfName)PdfReader.getPdfObject(object2.get(PdfName.S)))) {
                baseField.setBorderStyle(1);
            } else if (PdfName.B.equals(object)) {
                baseField.setBorderStyle(2);
            } else if (PdfName.I.equals(object)) {
                baseField.setBorderStyle(3);
            } else if (PdfName.U.equals(object)) {
                baseField.setBorderStyle(4);
            }
        } else {
            object3 = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.BORDER));
            if (object3 != null) {
                object = object3.getArrayList();
                if (object.size() >= 3) {
                    baseField.setBorderWidth(((PdfNumber)object.get(2)).floatValue());
                }
                if (object.size() >= 4) {
                    baseField.setBorderStyle(1);
                }
            }
        }
    }

    PdfAppearance getAppearance(PdfDictionary pdfDictionary, String string, String string2) throws IOException, DocumentException {
        Object object;
        Object object2;
        PdfObject pdfObject2;
        this.topFirst = 0;
        TextField textField = null;
        if (this.fieldCache == null || !this.fieldCache.containsKey(string2)) {
            textField = new TextField(this.writer, null, null);
            textField.setExtraMargin(this.extraMarginLeft, this.extraMarginTop);
            textField.setBorderWidth(0.0f);
            textField.setSubstitutionFonts(this.substitutionFonts);
            this.decodeGenericDictionary(pdfDictionary, textField);
            PdfObject pdfObject2 = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.RECT));
            object = PdfReader.getNormalizedRectangle((PdfArray)pdfObject2);
            if (textField.getRotation() == 90 || textField.getRotation() == 270) {
                object = object.rotate();
            }
            textField.setBox((Rectangle)object);
            if (this.fieldCache != null) {
                this.fieldCache.put(string2, textField);
            }
        } else {
            textField = (TextField)this.fieldCache.get(string2);
            textField.setWriter(this.writer);
        }
        if (PdfName.TX.equals(pdfObject2 = (PdfName)PdfReader.getPdfObject(pdfDictionary.get(PdfName.FT)))) {
            textField.setText(string);
            return textField.getAppearance();
        }
        if (!PdfName.CH.equals(pdfObject2)) {
            throw new DocumentException("An appearance was requested without a variable text field.");
        }
        object = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.OPT));
        int n = 0;
        PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(pdfDictionary.get(PdfName.FF));
        if (pdfNumber != null) {
            n = pdfNumber.intValue();
        }
        if ((n & 131072) != 0 && object == null) {
            textField.setText(string);
            return textField.getAppearance();
        }
        if (object != null) {
            int n2;
            object2 = object.getArrayList();
            String[] arrstring = new String[object2.size()];
            String[] arrstring2 = new String[object2.size()];
            for (n2 = 0; n2 < object2.size(); ++n2) {
                PdfObject pdfObject3 = (PdfObject)object2.get(n2);
                if (pdfObject3.isString()) {
                    arrstring[n2] = arrstring2[n2] = ((PdfString)pdfObject3).toUnicodeString();
                    continue;
                }
                ArrayList arrayList = ((PdfArray)pdfObject3).getArrayList();
                arrstring2[n2] = ((PdfString)arrayList.get(0)).toUnicodeString();
                arrstring[n2] = ((PdfString)arrayList.get(1)).toUnicodeString();
            }
            if ((n & 131072) != 0) {
                for (n2 = 0; n2 < arrstring.length; ++n2) {
                    if (!string.equals(arrstring2[n2])) continue;
                    string = arrstring[n2];
                    break;
                }
                textField.setText(string);
                return textField.getAppearance();
            }
            n2 = 0;
            for (int i = 0; i < arrstring2.length; ++i) {
                if (!string.equals(arrstring2[i])) continue;
                n2 = i;
                break;
            }
            textField.setChoices(arrstring);
            textField.setChoiceExports(arrstring2);
            textField.setChoiceSelection(n2);
        }
        object2 = textField.getListAppearance();
        this.topFirst = textField.getTopFirst();
        return object2;
    }

    Color getMKColor(PdfArray pdfArray) {
        if (pdfArray == null) {
            return null;
        }
        ArrayList arrayList = pdfArray.getArrayList();
        switch (arrayList.size()) {
            case 1: {
                return new GrayColor(((PdfNumber)arrayList.get(0)).floatValue());
            }
            case 3: {
                return new Color(ExtendedColor.normalize(((PdfNumber)arrayList.get(0)).floatValue()), ExtendedColor.normalize(((PdfNumber)arrayList.get(1)).floatValue()), ExtendedColor.normalize(((PdfNumber)arrayList.get(2)).floatValue()));
            }
            case 4: {
                return new CMYKColor(((PdfNumber)arrayList.get(0)).floatValue(), ((PdfNumber)arrayList.get(1)).floatValue(), ((PdfNumber)arrayList.get(2)).floatValue(), ((PdfNumber)arrayList.get(3)).floatValue());
            }
        }
        return null;
    }

    public String getField(String string) {
        if (this.xfa.isXfaPresent()) {
            if ((string = this.xfa.findFieldName(string, this)) == null) {
                return null;
            }
            string = XfaForm.Xml2Som.getShortName(string);
            return XfaForm.getNodeText(this.xfa.findDatasetsNode(string));
        }
        Item item = (Item)this.fields.get(string);
        if (item == null) {
            return null;
        }
        this.lastWasString = false;
        PdfObject pdfObject = PdfReader.getPdfObject(((PdfDictionary)item.merged.get(0)).get(PdfName.V));
        if (pdfObject == null) {
            return "";
        }
        PdfName pdfName = (PdfName)PdfReader.getPdfObject(((PdfDictionary)item.merged.get(0)).get(PdfName.FT));
        if (PdfName.BTN.equals(pdfName)) {
            PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(((PdfDictionary)item.merged.get(0)).get(PdfName.FF));
            int n = 0;
            if (pdfNumber != null) {
                n = pdfNumber.intValue();
            }
            if ((n & 65536) != 0) {
                return "";
            }
            String string2 = "";
            if (pdfObject.isName()) {
                string2 = PdfName.decodeName(pdfObject.toString());
            } else if (pdfObject.isString()) {
                string2 = ((PdfString)pdfObject).toUnicodeString();
            }
            PdfObject pdfObject2 = PdfReader.getPdfObject(((PdfDictionary)item.values.get(0)).get(PdfName.OPT));
            if (pdfObject2 != null && pdfObject2.isArray()) {
                ArrayList arrayList = ((PdfArray)pdfObject2).getArrayList();
                int n2 = 0;
                try {
                    n2 = Integer.parseInt(string2);
                    PdfString pdfString = (PdfString)arrayList.get(n2);
                    string2 = pdfString.toUnicodeString();
                    this.lastWasString = true;
                }
                catch (Exception var11_12) {
                    // empty catch block
                }
            }
            return string2;
        }
        if (pdfObject.isString()) {
            this.lastWasString = true;
            return ((PdfString)pdfObject).toUnicodeString();
        }
        return PdfName.decodeName(pdfObject.toString());
    }

    public String[] getListSelection(String string) {
        String string2 = this.getField(string);
        String[] arrstring = string2 == null ? new String[]{} : new String[]{string2};
        Item item = (Item)this.fields.get(string);
        if (item == null) {
            return arrstring;
        }
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(((PdfDictionary)item.merged.get(0)).get(PdfName.I));
        if (pdfArray == null) {
            return arrstring;
        }
        arrstring = new String[pdfArray.size()];
        String[] arrstring2 = this.getListOptionExport(string);
        int n = 0;
        ListIterator listIterator = pdfArray.listIterator();
        while (listIterator.hasNext()) {
            PdfNumber pdfNumber = (PdfNumber)listIterator.next();
            arrstring[n++] = arrstring2[pdfNumber.intValue()];
        }
        return arrstring;
    }

    public boolean setFieldProperty(String string, String string2, Object object, int[] arrn) {
        if (this.writer == null) {
            throw new RuntimeException("This AcroFields instance is read-only.");
        }
        try {
            Item item = (Item)this.fields.get(string);
            if (item == null) {
                return false;
            }
            InstHit instHit = new InstHit(arrn);
            if (string2.equalsIgnoreCase("textfont")) {
                for (int i = 0; i < item.merged.size(); ++i) {
                    PdfDictionary pdfDictionary;
                    Object object2;
                    if (!instHit.isHit(i)) continue;
                    PdfString pdfString = (PdfString)PdfReader.getPdfObject(((PdfDictionary)item.merged.get(i)).get(PdfName.DA));
                    PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(((PdfDictionary)item.merged.get(i)).get(PdfName.DR));
                    if (pdfString == null || pdfDictionary2 == null) continue;
                    Object[] arrobject = AcroFields.splitDAelements(pdfString.toUnicodeString());
                    PdfAppearance pdfAppearance = new PdfAppearance();
                    if (arrobject[0] == null) continue;
                    BaseFont baseFont = (BaseFont)object;
                    PdfName pdfName = (PdfName)PdfAppearance.stdFieldFontNames.get(baseFont.getPostscriptFontName());
                    if (pdfName == null) {
                        pdfName = new PdfName(baseFont.getPostscriptFontName());
                    }
                    if ((pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.FONT))) == null) {
                        pdfDictionary = new PdfDictionary();
                        pdfDictionary2.put(PdfName.FONT, pdfDictionary);
                    }
                    PdfIndirectReference pdfIndirectReference = (PdfIndirectReference)pdfDictionary.get(pdfName);
                    PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(this.reader.getCatalog().get(PdfName.ACROFORM));
                    this.markUsed(pdfDictionary3);
                    pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.DR));
                    if (pdfDictionary2 == null) {
                        pdfDictionary2 = new PdfDictionary();
                        pdfDictionary3.put(PdfName.DR, pdfDictionary2);
                    }
                    this.markUsed(pdfDictionary2);
                    PdfDictionary pdfDictionary4 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.FONT));
                    if (pdfDictionary4 == null) {
                        pdfDictionary4 = new PdfDictionary();
                        pdfDictionary2.put(PdfName.FONT, pdfDictionary4);
                    }
                    this.markUsed(pdfDictionary4);
                    PdfIndirectReference pdfIndirectReference2 = (PdfIndirectReference)pdfDictionary4.get(pdfName);
                    if (pdfIndirectReference2 != null) {
                        if (pdfIndirectReference == null) {
                            pdfDictionary.put(pdfName, pdfIndirectReference2);
                        }
                    } else if (pdfIndirectReference == null) {
                        if (baseFont.getFontType() == 4) {
                            object2 = new FontDetails(null, ((DocumentFont)baseFont).getIndirectReference(), baseFont);
                        } else {
                            baseFont.setSubset(false);
                            object2 = this.writer.addSimple(baseFont);
                            this.localFonts.put(pdfName.toString().substring(1), baseFont);
                        }
                        pdfDictionary4.put(pdfName, object2.getIndirectReference());
                        pdfDictionary.put(pdfName, object2.getIndirectReference());
                    }
                    object2 = pdfAppearance.getInternalBuffer();
                    object2.append(pdfName.getBytes()).append(' ').append(((Float)arrobject[1]).floatValue()).append(" Tf ");
                    if (arrobject[2] != null) {
                        pdfAppearance.setColorFill((Color)arrobject[2]);
                    }
                    PdfString pdfString2 = new PdfString(pdfAppearance.toString());
                    ((PdfDictionary)item.merged.get(i)).put(PdfName.DA, pdfString2);
                    ((PdfDictionary)item.widgets.get(i)).put(PdfName.DA, pdfString2);
                    this.markUsed((PdfDictionary)item.widgets.get(i));
                }
            } else if (string2.equalsIgnoreCase("textcolor")) {
                for (int i = 0; i < item.merged.size(); ++i) {
                    PdfString pdfString;
                    if (!instHit.isHit(i) || (pdfString = (PdfString)PdfReader.getPdfObject(((PdfDictionary)item.merged.get(i)).get(PdfName.DA))) == null) continue;
                    Object[] arrobject = AcroFields.splitDAelements(pdfString.toUnicodeString());
                    PdfAppearance pdfAppearance = new PdfAppearance();
                    if (arrobject[0] == null) continue;
                    ByteBuffer byteBuffer = pdfAppearance.getInternalBuffer();
                    byteBuffer.append(new PdfName((String)arrobject[0]).getBytes()).append(' ').append(((Float)arrobject[1]).floatValue()).append(" Tf ");
                    pdfAppearance.setColorFill((Color)object);
                    PdfString pdfString3 = new PdfString(pdfAppearance.toString());
                    ((PdfDictionary)item.merged.get(i)).put(PdfName.DA, pdfString3);
                    ((PdfDictionary)item.widgets.get(i)).put(PdfName.DA, pdfString3);
                    this.markUsed((PdfDictionary)item.widgets.get(i));
                }
            } else if (string2.equalsIgnoreCase("textsize")) {
                for (int i = 0; i < item.merged.size(); ++i) {
                    PdfString pdfString;
                    if (!instHit.isHit(i) || (pdfString = (PdfString)PdfReader.getPdfObject(((PdfDictionary)item.merged.get(i)).get(PdfName.DA))) == null) continue;
                    Object[] arrobject = AcroFields.splitDAelements(pdfString.toUnicodeString());
                    PdfAppearance pdfAppearance = new PdfAppearance();
                    if (arrobject[0] == null) continue;
                    ByteBuffer byteBuffer = pdfAppearance.getInternalBuffer();
                    byteBuffer.append(new PdfName((String)arrobject[0]).getBytes()).append(' ').append(((Float)object).floatValue()).append(" Tf ");
                    if (arrobject[2] != null) {
                        pdfAppearance.setColorFill((Color)arrobject[2]);
                    }
                    PdfString pdfString4 = new PdfString(pdfAppearance.toString());
                    ((PdfDictionary)item.merged.get(i)).put(PdfName.DA, pdfString4);
                    ((PdfDictionary)item.widgets.get(i)).put(PdfName.DA, pdfString4);
                    this.markUsed((PdfDictionary)item.widgets.get(i));
                }
            } else if (string2.equalsIgnoreCase("bgcolor") || string2.equalsIgnoreCase("bordercolor")) {
                PdfName pdfName = string2.equalsIgnoreCase("bgcolor") ? PdfName.BG : PdfName.BC;
                for (int i = 0; i < item.merged.size(); ++i) {
                    if (!instHit.isHit(i)) continue;
                    PdfObject pdfObject = PdfReader.getPdfObject(((PdfDictionary)item.merged.get(i)).get(PdfName.MK));
                    this.markUsed(pdfObject);
                    PdfDictionary pdfDictionary = (PdfDictionary)pdfObject;
                    if (pdfDictionary == null) {
                        if (object == null) {
                            return true;
                        }
                        pdfDictionary = new PdfDictionary();
                        ((PdfDictionary)item.merged.get(i)).put(PdfName.MK, pdfDictionary);
                        ((PdfDictionary)item.widgets.get(i)).put(PdfName.MK, pdfDictionary);
                        this.markUsed((PdfDictionary)item.widgets.get(i));
                    }
                    if (object == null) {
                        pdfDictionary.remove(pdfName);
                        continue;
                    }
                    pdfDictionary.put(pdfName, PdfFormField.getMKColor((Color)object));
                }
            } else {
                return false;
            }
            return true;
        }
        catch (Exception var5_6) {
            throw new ExceptionConverter(var5_6);
        }
    }

    public boolean setFieldProperty(String string, String string2, int n, int[] arrn) {
        if (this.writer == null) {
            throw new RuntimeException("This AcroFields instance is read-only.");
        }
        Item item = (Item)this.fields.get(string);
        if (item == null) {
            return false;
        }
        InstHit instHit = new InstHit(arrn);
        if (string2.equalsIgnoreCase("flags")) {
            PdfNumber pdfNumber = new PdfNumber(n);
            for (int i = 0; i < item.merged.size(); ++i) {
                if (!instHit.isHit(i)) continue;
                ((PdfDictionary)item.merged.get(i)).put(PdfName.F, pdfNumber);
                ((PdfDictionary)item.widgets.get(i)).put(PdfName.F, pdfNumber);
                this.markUsed((PdfDictionary)item.widgets.get(i));
            }
        } else if (string2.equalsIgnoreCase("setflags")) {
            for (int i = 0; i < item.merged.size(); ++i) {
                if (!instHit.isHit(i)) continue;
                PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(((PdfDictionary)item.widgets.get(i)).get(PdfName.F));
                int n2 = 0;
                if (pdfNumber != null) {
                    n2 = pdfNumber.intValue();
                }
                pdfNumber = new PdfNumber(n2 | n);
                ((PdfDictionary)item.merged.get(i)).put(PdfName.F, pdfNumber);
                ((PdfDictionary)item.widgets.get(i)).put(PdfName.F, pdfNumber);
                this.markUsed((PdfDictionary)item.widgets.get(i));
            }
        } else if (string2.equalsIgnoreCase("clrflags")) {
            for (int i = 0; i < item.merged.size(); ++i) {
                if (!instHit.isHit(i)) continue;
                PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(((PdfDictionary)item.widgets.get(i)).get(PdfName.F));
                int n3 = 0;
                if (pdfNumber != null) {
                    n3 = pdfNumber.intValue();
                }
                pdfNumber = new PdfNumber(n3 & ~ n);
                ((PdfDictionary)item.merged.get(i)).put(PdfName.F, pdfNumber);
                ((PdfDictionary)item.widgets.get(i)).put(PdfName.F, pdfNumber);
                this.markUsed((PdfDictionary)item.widgets.get(i));
            }
        } else if (string2.equalsIgnoreCase("fflags")) {
            PdfNumber pdfNumber = new PdfNumber(n);
            for (int i = 0; i < item.merged.size(); ++i) {
                if (!instHit.isHit(i)) continue;
                ((PdfDictionary)item.merged.get(i)).put(PdfName.FF, pdfNumber);
                ((PdfDictionary)item.values.get(i)).put(PdfName.FF, pdfNumber);
                this.markUsed((PdfDictionary)item.values.get(i));
            }
        } else if (string2.equalsIgnoreCase("setfflags")) {
            for (int i = 0; i < item.merged.size(); ++i) {
                if (!instHit.isHit(i)) continue;
                PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(((PdfDictionary)item.values.get(i)).get(PdfName.FF));
                int n4 = 0;
                if (pdfNumber != null) {
                    n4 = pdfNumber.intValue();
                }
                pdfNumber = new PdfNumber(n4 | n);
                ((PdfDictionary)item.merged.get(i)).put(PdfName.FF, pdfNumber);
                ((PdfDictionary)item.values.get(i)).put(PdfName.FF, pdfNumber);
                this.markUsed((PdfDictionary)item.values.get(i));
            }
        } else if (string2.equalsIgnoreCase("clrfflags")) {
            for (int i = 0; i < item.merged.size(); ++i) {
                if (!instHit.isHit(i)) continue;
                PdfNumber pdfNumber = (PdfNumber)PdfReader.getPdfObject(((PdfDictionary)item.values.get(i)).get(PdfName.FF));
                int n5 = 0;
                if (pdfNumber != null) {
                    n5 = pdfNumber.intValue();
                }
                pdfNumber = new PdfNumber(n5 & ~ n);
                ((PdfDictionary)item.merged.get(i)).put(PdfName.FF, pdfNumber);
                ((PdfDictionary)item.values.get(i)).put(PdfName.FF, pdfNumber);
                this.markUsed((PdfDictionary)item.values.get(i));
            }
        } else {
            return false;
        }
        return true;
    }

    public void mergeXfaData(Node node) throws IOException, DocumentException {
        XfaForm.Xml2SomDatasets xml2SomDatasets = new XfaForm.Xml2SomDatasets(node);
        Iterator iterator = xml2SomDatasets.getOrder().iterator();
        while (iterator.hasNext()) {
            String string = (String)iterator.next();
            String string2 = XfaForm.getNodeText((Node)xml2SomDatasets.getName2Node().get(string));
            this.setField(string, string2);
        }
    }

    public void setFields(FdfReader fdfReader) throws IOException, DocumentException {
        HashMap hashMap = fdfReader.getFields();
        Iterator iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String string = (String)iterator.next();
            String string2 = fdfReader.getFieldValue(string);
            if (string2 == null) continue;
            this.setField(string, string2);
        }
    }

    public void setFields(XfdfReader xfdfReader) throws IOException, DocumentException {
        HashMap hashMap = xfdfReader.getFields();
        Iterator iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String string = (String)iterator.next();
            String string2 = xfdfReader.getFieldValue(string);
            if (string2 == null) continue;
            this.setField(string, string2);
        }
    }

    public boolean regenerateField(String string) throws IOException, DocumentException {
        String string2 = this.getField(string);
        return this.setField(string, string2, string2);
    }

    public boolean setField(String string, String string2) throws IOException, DocumentException {
        return this.setField(string, string2, null);
    }

    public boolean setField(String string, String string2, String string3) throws IOException, DocumentException {
        PdfNumber pdfNumber;
        Object object;
        int n;
        Object object2;
        if (this.writer == null) {
            throw new DocumentException("This AcroFields instance is read-only.");
        }
        if (this.xfa.isXfaPresent()) {
            if ((string = this.xfa.findFieldName(string, this)) == null) {
                return false;
            }
            object2 = XfaForm.Xml2Som.getShortName(string);
            object = this.xfa.findDatasetsNode((String)object2);
            if (object == null) {
                object = this.xfa.getDatasetsSom().insertNode(this.xfa.getDatasetsNode(), (String)object2);
            }
            this.xfa.setNodeText((Node)object, string2);
        }
        if ((object2 = (Item)this.fields.get(string)) == null) {
            return false;
        }
        object = (PdfName)PdfReader.getPdfObject(((PdfDictionary)object2.merged.get(0)).get(PdfName.FT));
        if (PdfName.TX.equals(object)) {
            pdfNumber = (PdfNumber)PdfReader.getPdfObject(((PdfDictionary)object2.merged.get(0)).get(PdfName.MAXLEN));
            n = 0;
            if (pdfNumber != null) {
                n = pdfNumber.intValue();
            }
            if (n > 0) {
                string2 = string2.substring(0, Math.min(n, string2.length()));
            }
        }
        if (string3 == null) {
            string3 = string2;
        }
        if (PdfName.TX.equals(object) || PdfName.CH.equals(object)) {
            pdfNumber = new PdfString(string2, "UnicodeBig");
            for (n = 0; n < object2.values.size(); ++n) {
                PdfDictionary pdfDictionary = (PdfDictionary)object2.values.get(n);
                pdfDictionary.put(PdfName.V, pdfNumber);
                pdfDictionary.remove(PdfName.I);
                this.markUsed(pdfDictionary);
                PdfDictionary pdfDictionary2 = (PdfDictionary)object2.merged.get(n);
                pdfDictionary2.remove(PdfName.I);
                pdfDictionary2.put(PdfName.V, pdfNumber);
                PdfDictionary pdfDictionary3 = (PdfDictionary)object2.widgets.get(n);
                if (this.generateAppearances) {
                    PdfObject pdfObject2;
                    PdfAppearance pdfAppearance = this.getAppearance(pdfDictionary2, string3, string);
                    if (PdfName.CH.equals(object)) {
                        PdfObject pdfObject2 = new PdfNumber(this.topFirst);
                        pdfDictionary3.put(PdfName.TI, pdfObject2);
                        pdfDictionary2.put(PdfName.TI, pdfObject2);
                    }
                    if ((pdfObject2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.AP))) == null) {
                        pdfObject2 = new PdfDictionary();
                        pdfDictionary3.put(PdfName.AP, pdfObject2);
                        pdfDictionary2.put(PdfName.AP, pdfObject2);
                    }
                    pdfObject2.put(PdfName.N, pdfAppearance.getIndirectReference());
                    this.writer.releaseTemplate(pdfAppearance);
                } else {
                    pdfDictionary3.remove(PdfName.AP);
                    pdfDictionary2.remove(PdfName.AP);
                }
                this.markUsed(pdfDictionary3);
            }
            return true;
        }
        if (PdfName.BTN.equals(object)) {
            PdfObject pdfObject;
            pdfNumber = (PdfNumber)PdfReader.getPdfObject(((PdfDictionary)object2.merged.get(0)).get(PdfName.FF));
            n = 0;
            if (pdfNumber != null) {
                n = pdfNumber.intValue();
            }
            if ((n & 65536) != 0) {
                Image image;
                try {
                    image = Image.getInstance(Base64.decode(string2));
                }
                catch (Exception var9_12) {
                    return false;
                }
                PushbuttonField pushbuttonField = this.getNewPushbuttonFromField(string);
                pushbuttonField.setImage(image);
                this.replacePushbuttonField(string, pushbuttonField.getField());
                return true;
            }
            PdfName pdfName = new PdfName(string2);
            ArrayList<String> arrayList = new ArrayList<String>();
            PdfObject pdfObject3 = PdfReader.getPdfObject(((PdfDictionary)object2.values.get(0)).get(PdfName.OPT));
            if (pdfObject3 != null && pdfObject3.isArray()) {
                ArrayList arrayList2 = ((PdfArray)pdfObject3).getArrayList();
                for (int i = 0; i < arrayList2.size(); ++i) {
                    pdfObject = PdfReader.getPdfObject((PdfObject)arrayList2.get(i));
                    if (pdfObject != null && pdfObject.isString()) {
                        arrayList.add(((PdfString)pdfObject).toUnicodeString());
                        continue;
                    }
                    arrayList.add(null);
                }
            }
            int n2 = arrayList.indexOf(string2);
            PdfName pdfName2 = null;
            pdfObject = n2 >= 0 ? (pdfName2 = new PdfName(String.valueOf(n2))) : pdfName;
            for (int i = 0; i < object2.values.size(); ++i) {
                PdfDictionary pdfDictionary = (PdfDictionary)object2.merged.get(i);
                PdfDictionary pdfDictionary4 = (PdfDictionary)object2.widgets.get(i);
                this.markUsed((PdfDictionary)object2.values.get(i));
                if (pdfName2 != null) {
                    PdfString pdfString = new PdfString(string2, "UnicodeBig");
                    ((PdfDictionary)object2.values.get(i)).put(PdfName.V, pdfString);
                    pdfDictionary.put(PdfName.V, pdfString);
                } else {
                    ((PdfDictionary)object2.values.get(i)).put(PdfName.V, pdfName);
                    pdfDictionary.put(PdfName.V, pdfName);
                }
                this.markUsed(pdfDictionary4);
                if (this.isInAP(pdfDictionary4, (PdfName)pdfObject)) {
                    pdfDictionary.put(PdfName.AS, pdfObject);
                    pdfDictionary4.put(PdfName.AS, pdfObject);
                    continue;
                }
                pdfDictionary.put(PdfName.AS, PdfName.Off);
                pdfDictionary4.put(PdfName.AS, PdfName.Off);
            }
            return true;
        }
        return false;
    }

    boolean isInAP(PdfDictionary pdfDictionary, PdfName pdfName) {
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.AP));
        if (pdfDictionary2 == null) {
            return false;
        }
        PdfDictionary pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.N));
        return pdfDictionary3 != null && pdfDictionary3.get(pdfName) != null;
    }

    public HashMap getFields() {
        return this.fields;
    }

    public Item getFieldItem(String string) {
        if (this.xfa.isXfaPresent() && (string = this.xfa.findFieldName(string, this)) == null) {
            return null;
        }
        return (Item)this.fields.get(string);
    }

    public String getTranslatedFieldName(String string) {
        String string2;
        if (this.xfa.isXfaPresent() && (string2 = this.xfa.findFieldName(string, this)) != null) {
            string = string2;
        }
        return string;
    }

    public float[] getFieldPositions(String string) {
        Item item = this.getFieldItem(string);
        if (item == null) {
            return null;
        }
        float[] arrf = new float[item.page.size() * 5];
        int n = 0;
        for (int i = 0; i < item.page.size(); ++i) {
            try {
                PdfDictionary pdfDictionary = (PdfDictionary)item.widgets.get(i);
                PdfArray pdfArray = (PdfArray)pdfDictionary.get(PdfName.RECT);
                if (pdfArray == null) continue;
                Rectangle rectangle = PdfReader.getNormalizedRectangle(pdfArray);
                int n2 = (Integer)item.page.get(i);
                int n3 = this.reader.getPageRotation(n2);
                arrf[n++] = n2;
                if (n3 != 0) {
                    Rectangle rectangle2 = this.reader.getPageSize(n2);
                    switch (n3) {
                        case 270: {
                            rectangle = new Rectangle(rectangle2.getTop() - rectangle.getBottom(), rectangle.getLeft(), rectangle2.getTop() - rectangle.getTop(), rectangle.getRight());
                            break;
                        }
                        case 180: {
                            rectangle = new Rectangle(rectangle2.getRight() - rectangle.getLeft(), rectangle2.getTop() - rectangle.getBottom(), rectangle2.getRight() - rectangle.getRight(), rectangle2.getTop() - rectangle.getTop());
                            break;
                        }
                        case 90: {
                            rectangle = new Rectangle(rectangle.getBottom(), rectangle2.getRight() - rectangle.getLeft(), rectangle.getTop(), rectangle2.getRight() - rectangle.getRight());
                        }
                    }
                    rectangle.normalize();
                }
                arrf[n++] = rectangle.getLeft();
                arrf[n++] = rectangle.getBottom();
                arrf[n++] = rectangle.getRight();
                arrf[n++] = rectangle.getTop();
                continue;
            }
            catch (Exception var6_8) {
                // empty catch block
            }
        }
        if (n < arrf.length) {
            float[] arrf2 = new float[n];
            System.arraycopy(arrf, 0, arrf2, 0, n);
            return arrf2;
        }
        return arrf;
    }

    private int removeRefFromArray(PdfArray pdfArray, PdfObject pdfObject) {
        ArrayList arrayList = pdfArray.getArrayList();
        if (pdfObject == null || !pdfObject.isIndirect()) {
            return arrayList.size();
        }
        PdfIndirectReference pdfIndirectReference = (PdfIndirectReference)pdfObject;
        for (int i = 0; i < arrayList.size(); ++i) {
            PdfObject pdfObject2 = (PdfObject)arrayList.get(i);
            if (!pdfObject2.isIndirect() || ((PdfIndirectReference)pdfObject2).getNumber() != pdfIndirectReference.getNumber()) continue;
            arrayList.remove(i--);
        }
        return arrayList.size();
    }

    public boolean removeFieldsFromPage(int n) {
        if (n < 1) {
            return false;
        }
        String[] arrstring = new String[this.fields.size()];
        this.fields.keySet().toArray(arrstring);
        boolean bl = false;
        for (int i = 0; i < arrstring.length; ++i) {
            boolean bl2 = this.removeField(arrstring[i], n);
            bl = bl || bl2;
        }
        return bl;
    }

    public boolean removeField(String string, int n) {
        Item item = this.getFieldItem(string);
        if (item == null) {
            return false;
        }
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(this.reader.getCatalog().get(PdfName.ACROFORM), this.reader.getCatalog());
        if (pdfDictionary == null) {
            return false;
        }
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.FIELDS), pdfDictionary);
        if (pdfArray == null) {
            return false;
        }
        for (int i = 0; i < item.widget_refs.size(); ++i) {
            PdfArray pdfArray2;
            int n2 = (Integer)item.page.get(i);
            if (n != -1 && n != n2) continue;
            PdfIndirectReference pdfIndirectReference = (PdfIndirectReference)item.widget_refs.get(i);
            PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfIndirectReference);
            PdfDictionary pdfDictionary3 = this.reader.getPageN(n2);
            PdfArray pdfArray3 = (PdfArray)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.ANNOTS), pdfDictionary3);
            if (pdfArray3 != null) {
                if (this.removeRefFromArray(pdfArray3, pdfIndirectReference) == 0) {
                    pdfDictionary3.remove(PdfName.ANNOTS);
                    this.markUsed(pdfDictionary3);
                } else {
                    this.markUsed(pdfArray3);
                }
            }
            PdfReader.killIndirect(pdfIndirectReference);
            PdfIndirectReference pdfIndirectReference2 = pdfIndirectReference;
            while ((pdfIndirectReference = (PdfIndirectReference)pdfDictionary2.get(PdfName.PARENT)) != null && this.removeRefFromArray(pdfArray2 = (PdfArray)PdfReader.getPdfObject((pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfIndirectReference)).get(PdfName.KIDS)), pdfIndirectReference2) == 0) {
                pdfIndirectReference2 = pdfIndirectReference;
                PdfReader.killIndirect(pdfIndirectReference);
            }
            if (pdfIndirectReference == null) {
                this.removeRefFromArray(pdfArray, pdfIndirectReference2);
                this.markUsed(pdfArray);
            }
            if (n == -1) continue;
            item.merged.remove(i);
            item.page.remove(i);
            item.values.remove(i);
            item.widget_refs.remove(i);
            item.widgets.remove(i);
            --i;
        }
        if (n == -1 || item.merged.size() == 0) {
            this.fields.remove(string);
        }
        return true;
    }

    public boolean removeField(String string) {
        return this.removeField(string, -1);
    }

    public boolean isGenerateAppearances() {
        return this.generateAppearances;
    }

    public void setGenerateAppearances(boolean bl) {
        this.generateAppearances = bl;
        PdfDictionary pdfDictionary = (PdfDictionary)PdfReader.getPdfObject(this.reader.getCatalog().get(PdfName.ACROFORM));
        if (bl) {
            pdfDictionary.remove(PdfName.NEEDAPPEARANCES);
        } else {
            pdfDictionary.put(PdfName.NEEDAPPEARANCES, PdfBoolean.PDFTRUE);
        }
    }

    public ArrayList getSignatureNames() {
        Object object;
        int[] arrn;
        Object[] arrobject;
        if (this.sigNames != null) {
            return new ArrayList(this.sigNames.keySet());
        }
        this.sigNames = new HashMap();
        ArrayList<Object[]> arrayList = new ArrayList<Object[]>();
        Iterator iterator = this.fields.entrySet().iterator();
        while (iterator.hasNext()) {
            PdfDictionary pdfDictionary;
            ArrayList arrayList2;
            PdfObject pdfObject;
            PdfObject pdfObject2;
            PdfObject pdfObject3;
            arrobject = iterator.next();
            object = (Item)arrobject.getValue();
            arrn = (int[])object.merged.get(0);
            if (!PdfName.SIG.equals(arrn.get(PdfName.FT)) || (pdfObject3 = PdfReader.getPdfObject(arrn.get(PdfName.V))) == null || pdfObject3.type() != 6 || (pdfObject = (pdfDictionary = (PdfDictionary)pdfObject3).get(PdfName.CONTENTS)) == null || pdfObject.type() != 3 || (pdfObject2 = pdfDictionary.get(PdfName.BYTERANGE)) == null || pdfObject2.type() != 5 || (arrayList2 = ((PdfArray)pdfObject2).getArrayList()).size() < 2) continue;
            int n = ((PdfNumber)arrayList2.get(arrayList2.size() - 1)).intValue() + ((PdfNumber)arrayList2.get(arrayList2.size() - 2)).intValue();
            arrayList.add(new Object[]{arrobject.getKey(), {n, 0}});
        }
        Collections.sort(arrayList, new SorterComparator());
        if (!arrayList.isEmpty()) {
            this.totalRevisions = ((int[])((Object[])arrayList.get(arrayList.size() - 1))[1])[0] == this.reader.getFileLength() ? arrayList.size() : arrayList.size() + 1;
            for (int i = 0; i < arrayList.size(); ++i) {
                arrobject = (Object[])arrayList.get(i);
                object = (String)arrobject[0];
                arrn = (int[])arrobject[1];
                arrn[1] = i + 1;
                this.sigNames.put(object, arrn);
            }
        }
        return new ArrayList(this.sigNames.keySet());
    }

    public ArrayList getBlankSignatureNames() {
        this.getSignatureNames();
        ArrayList arrayList = new ArrayList();
        Iterator iterator = this.fields.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            Item item = (Item)entry.getValue();
            PdfDictionary pdfDictionary = (PdfDictionary)item.merged.get(0);
            if (!PdfName.SIG.equals(pdfDictionary.get(PdfName.FT)) || this.sigNames.containsKey(entry.getKey())) continue;
            arrayList.add(entry.getKey());
        }
        return arrayList;
    }

    public PdfDictionary getSignatureDictionary(String string) {
        this.getSignatureNames();
        string = this.getTranslatedFieldName(string);
        if (!this.sigNames.containsKey(string)) {
            return null;
        }
        Item item = (Item)this.fields.get(string);
        PdfDictionary pdfDictionary = (PdfDictionary)item.merged.get(0);
        return (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.V));
    }

    public boolean signatureCoversWholeDocument(String string) {
        this.getSignatureNames();
        string = this.getTranslatedFieldName(string);
        if (!this.sigNames.containsKey(string)) {
            return false;
        }
        return ((int[])this.sigNames.get(string))[0] == this.reader.getFileLength();
    }

    public PdfPKCS7 verifySignature(String string) {
        return this.verifySignature(string, null);
    }

    public PdfPKCS7 verifySignature(String string, String string2) {
        PdfDictionary pdfDictionary = this.getSignatureDictionary(string);
        if (pdfDictionary == null) {
            return null;
        }
        try {
            PdfObject pdfObject;
            PdfString pdfString;
            PdfName pdfName = (PdfName)PdfReader.getPdfObject(pdfDictionary.get(PdfName.SUBFILTER));
            PdfString pdfString2 = (PdfString)PdfReader.getPdfObject(pdfDictionary.get(PdfName.CONTENTS));
            PdfPKCS7 pdfPKCS7 = null;
            if (pdfName.equals(PdfName.ADBE_X509_RSA_SHA1)) {
                pdfString = (PdfString)PdfReader.getPdfObject(pdfDictionary.get(PdfName.CERT));
                pdfPKCS7 = new PdfPKCS7(pdfString2.getOriginalBytes(), pdfString.getBytes(), string2);
            } else {
                pdfPKCS7 = new PdfPKCS7(pdfString2.getOriginalBytes(), string2);
            }
            this.updateByteRange(pdfPKCS7, pdfDictionary);
            pdfString = (PdfString)PdfReader.getPdfObject(pdfDictionary.get(PdfName.M));
            if (pdfString != null) {
                pdfPKCS7.setSignDate(PdfDate.decode(pdfString.toString()));
            }
            if ((pdfObject = PdfReader.getPdfObject(pdfDictionary.get(PdfName.NAME))) != null) {
                if (pdfObject.isString()) {
                    pdfPKCS7.setSignName(((PdfString)pdfObject).toUnicodeString());
                } else if (pdfObject.isName()) {
                    pdfPKCS7.setSignName(PdfName.decodeName(pdfObject.toString()));
                }
            }
            if ((pdfString = (PdfString)PdfReader.getPdfObject(pdfDictionary.get(PdfName.REASON))) != null) {
                pdfPKCS7.setReason(pdfString.toUnicodeString());
            }
            if ((pdfString = (PdfString)PdfReader.getPdfObject(pdfDictionary.get(PdfName.LOCATION))) != null) {
                pdfPKCS7.setLocation(pdfString.toUnicodeString());
            }
            return pdfPKCS7;
        }
        catch (Exception var4_5) {
            throw new ExceptionConverter(var4_5);
        }
    }

    private void updateByteRange(PdfPKCS7 pdfPKCS7, PdfDictionary pdfDictionary) {
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.BYTERANGE));
        RandomAccessFileOrArray randomAccessFileOrArray = this.reader.getSafeFile();
        try {
            randomAccessFileOrArray.reOpen();
            byte[] arrby = new byte[8192];
            ArrayList arrayList = pdfArray.getArrayList();
            for (int i = 0; i < arrayList.size(); ++i) {
                int n;
                int n2 = ((PdfNumber)arrayList.get(i)).intValue();
                randomAccessFileOrArray.seek(n2);
                for (int j = ((PdfNumber)arrayList.get((int)(++i))).intValue(); j > 0 && (n = randomAccessFileOrArray.read(arrby, 0, Math.min(j, arrby.length))) > 0; j -= n) {
                    pdfPKCS7.update(arrby, 0, n);
                }
            }
        }
        catch (Exception var5_7) {
            throw new ExceptionConverter(var5_7);
        }
        finally {
            try {
                randomAccessFileOrArray.close();
            }
            catch (Exception var12_14) {}
        }
    }

    private void markUsed(PdfObject pdfObject) {
        if (!this.append) {
            return;
        }
        ((PdfStamperImp)this.writer).markUsed(pdfObject);
    }

    public int getTotalRevisions() {
        this.getSignatureNames();
        return this.totalRevisions;
    }

    public int getRevision(String string) {
        this.getSignatureNames();
        string = this.getTranslatedFieldName(string);
        if (!this.sigNames.containsKey(string)) {
            return 0;
        }
        return ((int[])this.sigNames.get(string))[1];
    }

    public InputStream extractRevision(String string) throws IOException {
        this.getSignatureNames();
        string = this.getTranslatedFieldName(string);
        if (!this.sigNames.containsKey(string)) {
            return null;
        }
        int n = ((int[])this.sigNames.get(string))[0];
        RandomAccessFileOrArray randomAccessFileOrArray = this.reader.getSafeFile();
        randomAccessFileOrArray.reOpen();
        randomAccessFileOrArray.seek(0);
        return new RevisionStream(randomAccessFileOrArray, n);
    }

    public HashMap getFieldCache() {
        return this.fieldCache;
    }

    public void setFieldCache(HashMap hashMap) {
        this.fieldCache = hashMap;
    }

    public void setExtraMargin(float f, float f2) {
        this.extraMarginLeft = f;
        this.extraMarginTop = f2;
    }

    public void addSubstitutionFont(BaseFont baseFont) {
        if (this.substitutionFonts == null) {
            this.substitutionFonts = new ArrayList();
        }
        this.substitutionFonts.add(baseFont);
    }

    public ArrayList getSubstitutionFonts() {
        return this.substitutionFonts;
    }

    public void setSubstitutionFonts(ArrayList arrayList) {
        this.substitutionFonts = arrayList;
    }

    public XfaForm getXfa() {
        return this.xfa;
    }

    public PushbuttonField getNewPushbuttonFromField(String string) {
        return this.getNewPushbuttonFromField(string, 0);
    }

    public PushbuttonField getNewPushbuttonFromField(String string, int n) {
        try {
            if (this.getFieldType(string) != 1) {
                return null;
            }
            Item item = this.getFieldItem(string);
            if (n >= item.merged.size()) {
                return null;
            }
            int n2 = n * 5;
            float[] arrf = this.getFieldPositions(string);
            Rectangle rectangle = new Rectangle(arrf[n2 + 1], arrf[n2 + 2], arrf[n2 + 3], arrf[n2 + 4]);
            PushbuttonField pushbuttonField = new PushbuttonField(this.writer, rectangle, null);
            PdfDictionary pdfDictionary = (PdfDictionary)item.merged.get(n);
            this.decodeGenericDictionary(pdfDictionary, pushbuttonField);
            PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary.get(PdfName.MK));
            if (pdfDictionary2 != null) {
                PdfDictionary pdfDictionary3;
                PdfNumber pdfNumber;
                PdfObject pdfObject;
                PdfString pdfString = (PdfString)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.CA));
                if (pdfString != null) {
                    pushbuttonField.setText(pdfString.toUnicodeString());
                }
                if ((pdfNumber = (PdfNumber)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.TP))) != null) {
                    pushbuttonField.setLayout(pdfNumber.intValue() + 1);
                }
                if ((pdfDictionary3 = (PdfDictionary)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.IF))) != null) {
                    PdfArray pdfArray;
                    PdfObject pdfObject2;
                    pdfObject = (PdfName)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.SW));
                    if (pdfObject != null) {
                        int n3 = 1;
                        if (pdfObject.equals(PdfName.B)) {
                            n3 = 3;
                        } else if (pdfObject.equals(PdfName.S)) {
                            n3 = 4;
                        } else if (pdfObject.equals(PdfName.N)) {
                            n3 = 2;
                        }
                        pushbuttonField.setScaleIcon(n3);
                    }
                    if ((pdfObject = (PdfName)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.S))) != null && pdfObject.equals(PdfName.A)) {
                        pushbuttonField.setProportionalIcon(false);
                    }
                    if ((pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary3.get(PdfName.A))) != null && pdfArray.size() == 2) {
                        float f = ((PdfNumber)PdfReader.getPdfObject((PdfObject)pdfArray.getArrayList().get(0))).floatValue();
                        float f2 = ((PdfNumber)PdfReader.getPdfObject((PdfObject)pdfArray.getArrayList().get(1))).floatValue();
                        pushbuttonField.setIconHorizontalAdjustment(f);
                        pushbuttonField.setIconVerticalAdjustment(f2);
                    }
                    if ((pdfObject2 = PdfReader.getPdfObject(pdfDictionary3.get(PdfName.FB))) != null && pdfObject2.toString().equals("true")) {
                        pushbuttonField.setIconFitToBounds(true);
                    }
                }
                if ((pdfObject = pdfDictionary2.get(PdfName.I)) != null && pdfObject.isIndirect()) {
                    pushbuttonField.setIconReference((PRIndirectReference)pdfObject);
                }
            }
            return pushbuttonField;
        }
        catch (Exception var3_4) {
            throw new ExceptionConverter(var3_4);
        }
    }

    public boolean replacePushbuttonField(String string, PdfFormField pdfFormField) {
        return this.replacePushbuttonField(string, pdfFormField, 0);
    }

    public boolean replacePushbuttonField(String string, PdfFormField pdfFormField, int n) {
        if (this.getFieldType(string) != 1) {
            return false;
        }
        Item item = this.getFieldItem(string);
        if (n >= item.merged.size()) {
            return false;
        }
        PdfDictionary pdfDictionary = (PdfDictionary)item.merged.get(n);
        PdfDictionary pdfDictionary2 = (PdfDictionary)item.values.get(n);
        PdfDictionary pdfDictionary3 = (PdfDictionary)item.widgets.get(n);
        for (int i = 0; i < buttonRemove.length; ++i) {
            pdfDictionary.remove(buttonRemove[i]);
            pdfDictionary2.remove(buttonRemove[i]);
            pdfDictionary3.remove(buttonRemove[i]);
        }
        Iterator iterator = pdfFormField.getKeys().iterator();
        while (iterator.hasNext()) {
            PdfName pdfName = (PdfName)iterator.next();
            if (pdfName.equals(PdfName.T) || pdfName.equals(PdfName.RECT)) continue;
            if (pdfName.equals(PdfName.FF)) {
                pdfDictionary2.put(pdfName, pdfFormField.get(pdfName));
            } else {
                pdfDictionary3.put(pdfName, pdfFormField.get(pdfName));
            }
            pdfDictionary.put(pdfName, pdfFormField.get(pdfName));
        }
        return true;
    }

    static {
        stdFieldFontNames.put("CoBO", new String[]{"Courier-BoldOblique"});
        stdFieldFontNames.put("CoBo", new String[]{"Courier-Bold"});
        stdFieldFontNames.put("CoOb", new String[]{"Courier-Oblique"});
        stdFieldFontNames.put("Cour", new String[]{"Courier"});
        stdFieldFontNames.put("HeBO", new String[]{"Helvetica-BoldOblique"});
        stdFieldFontNames.put("HeBo", new String[]{"Helvetica-Bold"});
        stdFieldFontNames.put("HeOb", new String[]{"Helvetica-Oblique"});
        stdFieldFontNames.put("Helv", new String[]{"Helvetica"});
        stdFieldFontNames.put("Symb", new String[]{"Symbol"});
        stdFieldFontNames.put("TiBI", new String[]{"Times-BoldItalic"});
        stdFieldFontNames.put("TiBo", new String[]{"Times-Bold"});
        stdFieldFontNames.put("TiIt", new String[]{"Times-Italic"});
        stdFieldFontNames.put("TiRo", new String[]{"Times-Roman"});
        stdFieldFontNames.put("ZaDb", new String[]{"ZapfDingbats"});
        stdFieldFontNames.put("HySm", new String[]{"HYSMyeongJo-Medium", "UniKS-UCS2-H"});
        stdFieldFontNames.put("HyGo", new String[]{"HYGoThic-Medium", "UniKS-UCS2-H"});
        stdFieldFontNames.put("KaGo", new String[]{"HeiseiKakuGo-W5", "UniKS-UCS2-H"});
        stdFieldFontNames.put("KaMi", new String[]{"HeiseiMin-W3", "UniJIS-UCS2-H"});
        stdFieldFontNames.put("MHei", new String[]{"MHei-Medium", "UniCNS-UCS2-H"});
        stdFieldFontNames.put("MSun", new String[]{"MSung-Light", "UniCNS-UCS2-H"});
        stdFieldFontNames.put("STSo", new String[]{"STSong-Light", "UniGB-UCS2-H"});
        buttonRemove = new PdfName[]{PdfName.MK, PdfName.F, PdfName.FF, PdfName.Q, PdfName.BS, PdfName.BORDER};
    }

    private static class SorterComparator
    implements Comparator {
        private SorterComparator() {
        }

        public int compare(Object object, Object object2) {
            int n = ((int[])((Object[])object)[1])[0];
            int n2 = ((int[])((Object[])object2)[1])[0];
            return n - n2;
        }
    }

    private static class RevisionStream
    extends InputStream {
        private byte[] b = new byte[1];
        private RandomAccessFileOrArray raf;
        private int length;
        private int rangePosition = 0;
        private boolean closed;

        private RevisionStream(RandomAccessFileOrArray randomAccessFileOrArray, int n) {
            this.raf = randomAccessFileOrArray;
            this.length = n;
        }

        public int read() throws IOException {
            int n = this.read(this.b);
            if (n != 1) {
                return -1;
            }
            return this.b[0] & 255;
        }

        public int read(byte[] arrby, int n, int n2) throws IOException {
            if (arrby == null) {
                throw new NullPointerException();
            }
            if (n < 0 || n > arrby.length || n2 < 0 || n + n2 > arrby.length || n + n2 < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (n2 == 0) {
                return 0;
            }
            if (this.rangePosition >= this.length) {
                this.close();
                return -1;
            }
            int n3 = Math.min(n2, this.length - this.rangePosition);
            this.raf.readFully(arrby, n, n3);
            this.rangePosition += n3;
            return n3;
        }

        public void close() throws IOException {
            if (!this.closed) {
                this.raf.close();
                this.closed = true;
            }
        }
    }

    private static class InstHit {
        IntHashtable hits;

        public InstHit(int[] arrn) {
            if (arrn == null) {
                return;
            }
            this.hits = new IntHashtable();
            for (int i = 0; i < arrn.length; ++i) {
                this.hits.put(arrn[i], 1);
            }
        }

        public boolean isHit(int n) {
            if (this.hits == null) {
                return true;
            }
            return this.hits.containsKey(n);
        }
    }

    public static class Item {
        public ArrayList values = new ArrayList();
        public ArrayList widgets = new ArrayList();
        public ArrayList widget_refs = new ArrayList();
        public ArrayList merged = new ArrayList();
        public ArrayList page = new ArrayList();
        public ArrayList tabOrder = new ArrayList();
    }

}

