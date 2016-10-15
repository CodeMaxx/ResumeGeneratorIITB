/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.internal;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;

public class PdfViewerPreferencesImp
implements PdfViewerPreferences {
    public static final PdfName[] VIEWER_PREFERENCES = new PdfName[]{PdfName.HIDETOOLBAR, PdfName.HIDEMENUBAR, PdfName.HIDEWINDOWUI, PdfName.FITWINDOW, PdfName.CENTERWINDOW, PdfName.DISPLAYDOCTITLE, PdfName.NONFULLSCREENPAGEMODE, PdfName.DIRECTION, PdfName.VIEWAREA, PdfName.VIEWCLIP, PdfName.PRINTAREA, PdfName.PRINTCLIP, PdfName.PRINTSCALING, PdfName.DUPLEX, PdfName.PICKTRAYBYPDFSIZE, PdfName.PRINTPAGERANGE, PdfName.NUMCOPIES};
    public static final PdfName[] NONFULLSCREENPAGEMODE_PREFERENCES = new PdfName[]{PdfName.USENONE, PdfName.USEOUTLINES, PdfName.USETHUMBS, PdfName.USEOC};
    public static final PdfName[] DIRECTION_PREFERENCES = new PdfName[]{PdfName.L2R, PdfName.R2L};
    public static final PdfName[] PAGE_BOUNDARIES = new PdfName[]{PdfName.MEDIABOX, PdfName.CROPBOX, PdfName.BLEEDBOX, PdfName.TRIMBOX, PdfName.ARTBOX};
    public static final PdfName[] PRINTSCALING_PREFERENCES = new PdfName[]{PdfName.APPDEFAULT, PdfName.NONE};
    public static final PdfName[] DUPLEX_PREFERENCES = new PdfName[]{PdfName.SIMPLEX, PdfName.DUPLEXFLIPSHORTEDGE, PdfName.DUPLEXFLIPLONGEDGE};
    private int pageLayoutAndMode = 0;
    private PdfDictionary viewerPreferences = new PdfDictionary();
    private static final int viewerPreferencesMask = 16773120;

    public int getPageLayoutAndMode() {
        return this.pageLayoutAndMode;
    }

    public PdfDictionary getViewerPreferences() {
        return this.viewerPreferences;
    }

    public void setViewerPreferences(int n) {
        this.pageLayoutAndMode |= n;
        if ((n & 16773120) != 0) {
            this.pageLayoutAndMode = -16773121 & this.pageLayoutAndMode;
            if ((n & 4096) != 0) {
                this.viewerPreferences.put(PdfName.HIDETOOLBAR, PdfBoolean.PDFTRUE);
            }
            if ((n & 8192) != 0) {
                this.viewerPreferences.put(PdfName.HIDEMENUBAR, PdfBoolean.PDFTRUE);
            }
            if ((n & 16384) != 0) {
                this.viewerPreferences.put(PdfName.HIDEWINDOWUI, PdfBoolean.PDFTRUE);
            }
            if ((n & 32768) != 0) {
                this.viewerPreferences.put(PdfName.FITWINDOW, PdfBoolean.PDFTRUE);
            }
            if ((n & 65536) != 0) {
                this.viewerPreferences.put(PdfName.CENTERWINDOW, PdfBoolean.PDFTRUE);
            }
            if ((n & 131072) != 0) {
                this.viewerPreferences.put(PdfName.DISPLAYDOCTITLE, PdfBoolean.PDFTRUE);
            }
            if ((n & 262144) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USENONE);
            } else if ((n & 524288) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USEOUTLINES);
            } else if ((n & 1048576) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USETHUMBS);
            } else if ((n & 2097152) != 0) {
                this.viewerPreferences.put(PdfName.NONFULLSCREENPAGEMODE, PdfName.USEOC);
            }
            if ((n & 4194304) != 0) {
                this.viewerPreferences.put(PdfName.DIRECTION, PdfName.L2R);
            } else if ((n & 8388608) != 0) {
                this.viewerPreferences.put(PdfName.DIRECTION, PdfName.R2L);
            }
            if ((n & 16777216) != 0) {
                this.viewerPreferences.put(PdfName.PRINTSCALING, PdfName.NONE);
            }
        }
    }

    private int getIndex(PdfName pdfName) {
        for (int i = 0; i < VIEWER_PREFERENCES.length; ++i) {
            if (!VIEWER_PREFERENCES[i].equals(pdfName)) continue;
            return i;
        }
        return -1;
    }

    private boolean isPossibleValue(PdfName pdfName, PdfName[] arrpdfName) {
        for (int i = 0; i < arrpdfName.length; ++i) {
            if (!arrpdfName[i].equals(pdfName)) continue;
            return true;
        }
        return false;
    }

    public void addViewerPreference(PdfName pdfName, PdfObject pdfObject) {
        switch (this.getIndex(pdfName)) {
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 14: {
                if (!(pdfObject instanceof PdfBoolean)) break;
                this.viewerPreferences.put(pdfName, pdfObject);
                break;
            }
            case 6: {
                if (!(pdfObject instanceof PdfName) || !this.isPossibleValue((PdfName)pdfObject, NONFULLSCREENPAGEMODE_PREFERENCES)) break;
                this.viewerPreferences.put(pdfName, pdfObject);
                break;
            }
            case 7: {
                if (!(pdfObject instanceof PdfName) || !this.isPossibleValue((PdfName)pdfObject, DIRECTION_PREFERENCES)) break;
                this.viewerPreferences.put(pdfName, pdfObject);
                break;
            }
            case 8: 
            case 9: 
            case 10: 
            case 11: {
                if (!(pdfObject instanceof PdfName) || !this.isPossibleValue((PdfName)pdfObject, PAGE_BOUNDARIES)) break;
                this.viewerPreferences.put(pdfName, pdfObject);
                break;
            }
            case 12: {
                if (!(pdfObject instanceof PdfName) || !this.isPossibleValue((PdfName)pdfObject, PRINTSCALING_PREFERENCES)) break;
                this.viewerPreferences.put(pdfName, pdfObject);
                break;
            }
            case 13: {
                if (!(pdfObject instanceof PdfName) || !this.isPossibleValue((PdfName)pdfObject, DUPLEX_PREFERENCES)) break;
                this.viewerPreferences.put(pdfName, pdfObject);
                break;
            }
            case 15: {
                if (!(pdfObject instanceof PdfArray)) break;
                this.viewerPreferences.put(pdfName, pdfObject);
                break;
            }
            case 16: {
                if (!(pdfObject instanceof PdfNumber)) break;
                this.viewerPreferences.put(pdfName, pdfObject);
            }
        }
    }

    public void addToCatalog(PdfDictionary pdfDictionary) {
        pdfDictionary.remove(PdfName.PAGELAYOUT);
        if ((this.pageLayoutAndMode & 1) != 0) {
            pdfDictionary.put(PdfName.PAGELAYOUT, PdfName.SINGLEPAGE);
        } else if ((this.pageLayoutAndMode & 2) != 0) {
            pdfDictionary.put(PdfName.PAGELAYOUT, PdfName.ONECOLUMN);
        } else if ((this.pageLayoutAndMode & 4) != 0) {
            pdfDictionary.put(PdfName.PAGELAYOUT, PdfName.TWOCOLUMNLEFT);
        } else if ((this.pageLayoutAndMode & 8) != 0) {
            pdfDictionary.put(PdfName.PAGELAYOUT, PdfName.TWOCOLUMNRIGHT);
        } else if ((this.pageLayoutAndMode & 16) != 0) {
            pdfDictionary.put(PdfName.PAGELAYOUT, PdfName.TWOPAGELEFT);
        } else if ((this.pageLayoutAndMode & 32) != 0) {
            pdfDictionary.put(PdfName.PAGELAYOUT, PdfName.TWOPAGERIGHT);
        }
        pdfDictionary.remove(PdfName.PAGEMODE);
        if ((this.pageLayoutAndMode & 64) != 0) {
            pdfDictionary.put(PdfName.PAGEMODE, PdfName.USENONE);
        } else if ((this.pageLayoutAndMode & 128) != 0) {
            pdfDictionary.put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
        } else if ((this.pageLayoutAndMode & 256) != 0) {
            pdfDictionary.put(PdfName.PAGEMODE, PdfName.USETHUMBS);
        } else if ((this.pageLayoutAndMode & 512) != 0) {
            pdfDictionary.put(PdfName.PAGEMODE, PdfName.FULLSCREEN);
        } else if ((this.pageLayoutAndMode & 1024) != 0) {
            pdfDictionary.put(PdfName.PAGEMODE, PdfName.USEOC);
        } else if ((this.pageLayoutAndMode & 2048) != 0) {
            pdfDictionary.put(PdfName.PAGEMODE, PdfName.USEATTACHMENTS);
        }
        pdfDictionary.remove(PdfName.VIEWERPREFERENCES);
        if (this.viewerPreferences.size() > 0) {
            pdfDictionary.put(PdfName.VIEWERPREFERENCES, this.viewerPreferences);
        }
    }

    public static PdfViewerPreferencesImp getViewerPreferences(PdfDictionary pdfDictionary) {
        PdfViewerPreferencesImp pdfViewerPreferencesImp = new PdfViewerPreferencesImp();
        int n = 0;
        PdfName pdfName = null;
        PdfObject pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.PAGELAYOUT));
        if (pdfObject != null && pdfObject.isName()) {
            pdfName = (PdfName)pdfObject;
            if (pdfName.equals(PdfName.SINGLEPAGE)) {
                n |= 1;
            } else if (pdfName.equals(PdfName.ONECOLUMN)) {
                n |= 2;
            } else if (pdfName.equals(PdfName.TWOCOLUMNLEFT)) {
                n |= 4;
            } else if (pdfName.equals(PdfName.TWOCOLUMNRIGHT)) {
                n |= 8;
            } else if (pdfName.equals(PdfName.TWOPAGELEFT)) {
                n |= 16;
            } else if (pdfName.equals(PdfName.TWOPAGERIGHT)) {
                n |= 32;
            }
        }
        if ((pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.PAGEMODE))) != null && pdfObject.isName()) {
            pdfName = (PdfName)pdfObject;
            if (pdfName.equals(PdfName.USENONE)) {
                n |= 64;
            } else if (pdfName.equals(PdfName.USEOUTLINES)) {
                n |= 128;
            } else if (pdfName.equals(PdfName.USETHUMBS)) {
                n |= 256;
            } else if (pdfName.equals(PdfName.USEOC)) {
                n |= 1024;
            } else if (pdfName.equals(PdfName.USEATTACHMENTS)) {
                n |= 2048;
            }
        }
        pdfViewerPreferencesImp.setViewerPreferences(n);
        pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary.get(PdfName.VIEWERPREFERENCES));
        if (pdfObject != null && pdfObject.isDictionary()) {
            PdfDictionary pdfDictionary2 = (PdfDictionary)pdfObject;
            for (int i = 0; i < VIEWER_PREFERENCES.length; ++i) {
                pdfObject = PdfReader.getPdfObjectRelease(pdfDictionary2.get(VIEWER_PREFERENCES[i]));
                pdfViewerPreferencesImp.addViewerPreference(VIEWER_PREFERENCES[i], pdfObject);
            }
        }
        return pdfViewerPreferencesImp;
    }
}

