/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRendition;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.collection.PdfTargetDictionary;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class PdfAction
extends PdfDictionary {
    public static final int FIRSTPAGE = 1;
    public static final int PREVPAGE = 2;
    public static final int NEXTPAGE = 3;
    public static final int LASTPAGE = 4;
    public static final int PRINTDIALOG = 5;
    public static final int SUBMIT_EXCLUDE = 1;
    public static final int SUBMIT_INCLUDE_NO_VALUE_FIELDS = 2;
    public static final int SUBMIT_HTML_FORMAT = 4;
    public static final int SUBMIT_HTML_GET = 8;
    public static final int SUBMIT_COORDINATES = 16;
    public static final int SUBMIT_XFDF = 32;
    public static final int SUBMIT_INCLUDE_APPEND_SAVES = 64;
    public static final int SUBMIT_INCLUDE_ANNOTATIONS = 128;
    public static final int SUBMIT_PDF = 256;
    public static final int SUBMIT_CANONICAL_FORMAT = 512;
    public static final int SUBMIT_EXCL_NON_USER_ANNOTS = 1024;
    public static final int SUBMIT_EXCL_F_KEY = 2048;
    public static final int SUBMIT_EMBED_FORM = 8196;
    public static final int RESET_EXCLUDE = 1;

    public PdfAction() {
    }

    public PdfAction(URL uRL) {
        this(uRL.toExternalForm());
    }

    public PdfAction(URL uRL, boolean bl) {
        this(uRL.toExternalForm(), bl);
    }

    public PdfAction(String string) {
        this(string, false);
    }

    public PdfAction(String string, boolean bl) {
        this.put(PdfName.S, PdfName.URI);
        this.put(PdfName.URI, new PdfString(string));
        if (bl) {
            this.put(PdfName.ISMAP, PdfBoolean.PDFTRUE);
        }
    }

    PdfAction(PdfIndirectReference pdfIndirectReference) {
        this.put(PdfName.S, PdfName.GOTO);
        this.put(PdfName.D, pdfIndirectReference);
    }

    public PdfAction(String string, String string2) {
        this.put(PdfName.S, PdfName.GOTOR);
        this.put(PdfName.F, new PdfString(string));
        this.put(PdfName.D, new PdfString(string2));
    }

    public PdfAction(String string, int n) {
        this.put(PdfName.S, PdfName.GOTOR);
        this.put(PdfName.F, new PdfString(string));
        this.put(PdfName.D, new PdfLiteral("[" + (n - 1) + " /FitH 10000]"));
    }

    public PdfAction(int n) {
        this.put(PdfName.S, PdfName.NAMED);
        switch (n) {
            case 1: {
                this.put(PdfName.N, PdfName.FIRSTPAGE);
                break;
            }
            case 4: {
                this.put(PdfName.N, PdfName.LASTPAGE);
                break;
            }
            case 3: {
                this.put(PdfName.N, PdfName.NEXTPAGE);
                break;
            }
            case 2: {
                this.put(PdfName.N, PdfName.PREVPAGE);
                break;
            }
            case 5: {
                this.put(PdfName.S, PdfName.JAVASCRIPT);
                this.put(PdfName.JS, new PdfString("this.print(true);\r"));
                break;
            }
            default: {
                throw new RuntimeException("Invalid named action.");
            }
        }
    }

    public PdfAction(String string, String string2, String string3, String string4) {
        this.put(PdfName.S, PdfName.LAUNCH);
        if (string2 == null && string3 == null && string4 == null) {
            this.put(PdfName.F, new PdfString(string));
        } else {
            PdfDictionary pdfDictionary = new PdfDictionary();
            pdfDictionary.put(PdfName.F, new PdfString(string));
            if (string2 != null) {
                pdfDictionary.put(PdfName.P, new PdfString(string2));
            }
            if (string3 != null) {
                pdfDictionary.put(PdfName.O, new PdfString(string3));
            }
            if (string4 != null) {
                pdfDictionary.put(PdfName.D, new PdfString(string4));
            }
            this.put(PdfName.WIN, pdfDictionary);
        }
    }

    public static PdfAction createLaunch(String string, String string2, String string3, String string4) {
        return new PdfAction(string, string2, string3, string4);
    }

    public static PdfAction rendition(String string, PdfFileSpecification pdfFileSpecification, String string2, PdfIndirectReference pdfIndirectReference) throws IOException {
        PdfAction pdfAction = new PdfAction();
        pdfAction.put(PdfName.S, PdfName.RENDITION);
        pdfAction.put(PdfName.R, new PdfRendition(string, pdfFileSpecification, string2));
        pdfAction.put(new PdfName("OP"), new PdfNumber(0));
        pdfAction.put(new PdfName("AN"), pdfIndirectReference);
        return pdfAction;
    }

    public static PdfAction javaScript(String string, PdfWriter pdfWriter, boolean bl) {
        PdfAction pdfAction = new PdfAction();
        pdfAction.put(PdfName.S, PdfName.JAVASCRIPT);
        if (bl && string.length() < 50) {
            pdfAction.put(PdfName.JS, new PdfString(string, "UnicodeBig"));
        } else if (!bl && string.length() < 100) {
            pdfAction.put(PdfName.JS, new PdfString(string));
        } else {
            try {
                byte[] arrby = PdfEncodings.convertToBytes(string, bl ? "UnicodeBig" : "PDF");
                PdfStream pdfStream = new PdfStream(arrby);
                pdfStream.flateCompress(pdfWriter.getCompressionLevel());
                pdfAction.put(PdfName.JS, pdfWriter.addToBody(pdfStream).getIndirectReference());
            }
            catch (Exception var4_5) {
                throw new ExceptionConverter(var4_5);
            }
        }
        return pdfAction;
    }

    public static PdfAction javaScript(String string, PdfWriter pdfWriter) {
        return PdfAction.javaScript(string, pdfWriter, false);
    }

    static PdfAction createHide(PdfObject pdfObject, boolean bl) {
        PdfAction pdfAction = new PdfAction();
        pdfAction.put(PdfName.S, PdfName.HIDE);
        pdfAction.put(PdfName.T, pdfObject);
        if (!bl) {
            pdfAction.put(PdfName.H, PdfBoolean.PDFFALSE);
        }
        return pdfAction;
    }

    public static PdfAction createHide(PdfAnnotation pdfAnnotation, boolean bl) {
        return PdfAction.createHide(pdfAnnotation.getIndirectReference(), bl);
    }

    public static PdfAction createHide(String string, boolean bl) {
        return PdfAction.createHide(new PdfString(string), bl);
    }

    static PdfArray buildArray(Object[] arrobject) {
        PdfArray pdfArray = new PdfArray();
        for (int i = 0; i < arrobject.length; ++i) {
            Object object = arrobject[i];
            if (object instanceof String) {
                pdfArray.add(new PdfString((String)object));
                continue;
            }
            if (object instanceof PdfAnnotation) {
                pdfArray.add(((PdfAnnotation)object).getIndirectReference());
                continue;
            }
            throw new RuntimeException("The array must contain String or PdfAnnotation.");
        }
        return pdfArray;
    }

    public static PdfAction createHide(Object[] arrobject, boolean bl) {
        return PdfAction.createHide(PdfAction.buildArray(arrobject), bl);
    }

    public static PdfAction createSubmitForm(String string, Object[] arrobject, int n) {
        PdfAction pdfAction = new PdfAction();
        pdfAction.put(PdfName.S, PdfName.SUBMITFORM);
        PdfDictionary pdfDictionary = new PdfDictionary();
        pdfDictionary.put(PdfName.F, new PdfString(string));
        pdfDictionary.put(PdfName.FS, PdfName.URL);
        pdfAction.put(PdfName.F, pdfDictionary);
        if (arrobject != null) {
            pdfAction.put(PdfName.FIELDS, PdfAction.buildArray(arrobject));
        }
        pdfAction.put(PdfName.FLAGS, new PdfNumber(n));
        return pdfAction;
    }

    public static PdfAction createResetForm(Object[] arrobject, int n) {
        PdfAction pdfAction = new PdfAction();
        pdfAction.put(PdfName.S, PdfName.RESETFORM);
        if (arrobject != null) {
            pdfAction.put(PdfName.FIELDS, PdfAction.buildArray(arrobject));
        }
        pdfAction.put(PdfName.FLAGS, new PdfNumber(n));
        return pdfAction;
    }

    public static PdfAction createImportData(String string) {
        PdfAction pdfAction = new PdfAction();
        pdfAction.put(PdfName.S, PdfName.IMPORTDATA);
        pdfAction.put(PdfName.F, new PdfString(string));
        return pdfAction;
    }

    public void next(PdfAction pdfAction) {
        PdfObject pdfObject = this.get(PdfName.NEXT);
        if (pdfObject == null) {
            this.put(PdfName.NEXT, pdfAction);
        } else if (pdfObject.isDictionary()) {
            PdfArray pdfArray = new PdfArray(pdfObject);
            pdfArray.add(pdfAction);
            this.put(PdfName.NEXT, pdfArray);
        } else {
            ((PdfArray)pdfObject).add(pdfAction);
        }
    }

    public static PdfAction gotoLocalPage(int n, PdfDestination pdfDestination, PdfWriter pdfWriter) {
        PdfIndirectReference pdfIndirectReference = pdfWriter.getPageReference(n);
        pdfDestination.addPage(pdfIndirectReference);
        PdfAction pdfAction = new PdfAction();
        pdfAction.put(PdfName.S, PdfName.GOTO);
        pdfAction.put(PdfName.D, pdfDestination);
        return pdfAction;
    }

    public static PdfAction gotoLocalPage(String string, boolean bl) {
        PdfAction pdfAction = new PdfAction();
        pdfAction.put(PdfName.S, PdfName.GOTO);
        if (bl) {
            pdfAction.put(PdfName.D, new PdfName(string));
        } else {
            pdfAction.put(PdfName.D, new PdfString(string, null));
        }
        return pdfAction;
    }

    public static PdfAction gotoRemotePage(String string, String string2, boolean bl, boolean bl2) {
        PdfAction pdfAction = new PdfAction();
        pdfAction.put(PdfName.F, new PdfString(string));
        pdfAction.put(PdfName.S, PdfName.GOTOR);
        if (bl) {
            pdfAction.put(PdfName.D, new PdfName(string2));
        } else {
            pdfAction.put(PdfName.D, new PdfString(string2, null));
        }
        if (bl2) {
            pdfAction.put(PdfName.NEWWINDOW, PdfBoolean.PDFTRUE);
        }
        return pdfAction;
    }

    public static PdfAction gotoEmbedded(String string, PdfTargetDictionary pdfTargetDictionary, String string2, boolean bl, boolean bl2) {
        if (bl) {
            return PdfAction.gotoEmbedded(string, pdfTargetDictionary, new PdfName(string2), bl2);
        }
        return PdfAction.gotoEmbedded(string, pdfTargetDictionary, new PdfString(string2, null), bl2);
    }

    public static PdfAction gotoEmbedded(String string, PdfTargetDictionary pdfTargetDictionary, PdfObject pdfObject, boolean bl) {
        PdfAction pdfAction = new PdfAction();
        pdfAction.put(PdfName.S, PdfName.GOTOE);
        pdfAction.put(PdfName.T, pdfTargetDictionary);
        pdfAction.put(PdfName.D, pdfObject);
        pdfAction.put(PdfName.NEWWINDOW, new PdfBoolean(bl));
        if (string != null) {
            pdfAction.put(PdfName.F, new PdfString(string));
        }
        return pdfAction;
    }

    public static PdfAction setOCGstate(ArrayList arrayList, boolean bl) {
        PdfAction pdfAction = new PdfAction();
        pdfAction.put(PdfName.S, PdfName.SETOCGSTATE);
        PdfArray pdfArray = new PdfArray();
        for (int i = 0; i < arrayList.size(); ++i) {
            Object e = arrayList.get(i);
            if (e == null) continue;
            if (e instanceof PdfIndirectReference) {
                pdfArray.add((PdfIndirectReference)e);
                continue;
            }
            if (e instanceof PdfLayer) {
                pdfArray.add(((PdfLayer)e).getRef());
                continue;
            }
            if (e instanceof PdfName) {
                pdfArray.add((PdfName)e);
                continue;
            }
            if (e instanceof String) {
                PdfName pdfName = null;
                String string = (String)e;
                if (string.equalsIgnoreCase("on")) {
                    pdfName = PdfName.ON;
                } else if (string.equalsIgnoreCase("off")) {
                    pdfName = PdfName.OFF;
                } else if (string.equalsIgnoreCase("toggle")) {
                    pdfName = PdfName.TOGGLE;
                } else {
                    throw new IllegalArgumentException("A string '" + string + " was passed in state. Only 'ON', 'OFF' and 'Toggle' are allowed.");
                }
                pdfArray.add(pdfName);
                continue;
            }
            throw new IllegalArgumentException("Invalid type was passed in state: " + e.getClass().getName());
        }
        pdfAction.put(PdfName.STATE, pdfArray);
        if (!bl) {
            pdfAction.put(PdfName.PRESERVERB, PdfBoolean.PDFFALSE);
        }
        return pdfAction;
    }
}

