/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocListener;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.ImgWMF;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.ColorDetails;
import com.lowagie.text.pdf.DocumentFont;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.FontDetails;
import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfAcroForm;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfContents;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfException;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfICCBased;
import com.lowagie.text.pdf.PdfImage;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfLayerMembership;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfOCProperties;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPage;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfPages;
import com.lowagie.text.pdf.PdfPattern;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfReaderInstance;
import com.lowagie.text.pdf.PdfShading;
import com.lowagie.text.pdf.PdfShadingPattern;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfStructureTreeRoot;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfTransition;
import com.lowagie.text.pdf.PdfXConformanceException;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SimpleBookmark;
import com.lowagie.text.pdf.SpotColor;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.events.PdfPageEventForwarder;
import com.lowagie.text.pdf.interfaces.PdfAnnotations;
import com.lowagie.text.pdf.interfaces.PdfDocumentActions;
import com.lowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.lowagie.text.pdf.interfaces.PdfPageActions;
import com.lowagie.text.pdf.interfaces.PdfRunDirection;
import com.lowagie.text.pdf.interfaces.PdfVersion;
import com.lowagie.text.pdf.interfaces.PdfViewerPreferences;
import com.lowagie.text.pdf.interfaces.PdfXConformance;
import com.lowagie.text.pdf.internal.PdfVersionImp;
import com.lowagie.text.pdf.internal.PdfXConformanceImp;
import com.lowagie.text.xml.xmp.XmpWriter;
import java.awt.Color;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class PdfWriter
extends DocWriter
implements PdfViewerPreferences,
PdfEncryptionSettings,
PdfVersion,
PdfDocumentActions,
PdfPageActions,
PdfXConformance,
PdfRunDirection,
PdfAnnotations {
    protected PdfDocument pdf;
    protected PdfContentByte directContent;
    protected PdfContentByte directContentUnder;
    protected PdfBody body;
    protected PdfDictionary extraCatalog;
    protected PdfPages root;
    protected ArrayList pageReferences;
    protected int currentPageNumber;
    private PdfPageEvent pageEvent;
    protected int prevxref;
    protected List newBookmarks;
    public static final char VERSION_1_2 = '2';
    public static final char VERSION_1_3 = '3';
    public static final char VERSION_1_4 = '4';
    public static final char VERSION_1_5 = '5';
    public static final char VERSION_1_6 = '6';
    public static final char VERSION_1_7 = '7';
    public static final PdfName PDF_VERSION_1_2 = new PdfName("1.2");
    public static final PdfName PDF_VERSION_1_3 = new PdfName("1.3");
    public static final PdfName PDF_VERSION_1_4 = new PdfName("1.4");
    public static final PdfName PDF_VERSION_1_5 = new PdfName("1.5");
    public static final PdfName PDF_VERSION_1_6 = new PdfName("1.6");
    public static final PdfName PDF_VERSION_1_7 = new PdfName("1.7");
    protected PdfVersionImp pdf_version;
    public static final int PageLayoutSinglePage = 1;
    public static final int PageLayoutOneColumn = 2;
    public static final int PageLayoutTwoColumnLeft = 4;
    public static final int PageLayoutTwoColumnRight = 8;
    public static final int PageLayoutTwoPageLeft = 16;
    public static final int PageLayoutTwoPageRight = 32;
    public static final int PageModeUseNone = 64;
    public static final int PageModeUseOutlines = 128;
    public static final int PageModeUseThumbs = 256;
    public static final int PageModeFullScreen = 512;
    public static final int PageModeUseOC = 1024;
    public static final int PageModeUseAttachments = 2048;
    public static final int HideToolbar = 4096;
    public static final int HideMenubar = 8192;
    public static final int HideWindowUI = 16384;
    public static final int FitWindow = 32768;
    public static final int CenterWindow = 65536;
    public static final int DisplayDocTitle = 131072;
    public static final int NonFullScreenPageModeUseNone = 262144;
    public static final int NonFullScreenPageModeUseOutlines = 524288;
    public static final int NonFullScreenPageModeUseThumbs = 1048576;
    public static final int NonFullScreenPageModeUseOC = 2097152;
    public static final int DirectionL2R = 4194304;
    public static final int DirectionR2L = 8388608;
    public static final int PrintScalingNone = 16777216;
    public static final PdfName DOCUMENT_CLOSE = PdfName.WC;
    public static final PdfName WILL_SAVE = PdfName.WS;
    public static final PdfName DID_SAVE = PdfName.DS;
    public static final PdfName WILL_PRINT = PdfName.WP;
    public static final PdfName DID_PRINT = PdfName.DP;
    public static final int SIGNATURE_EXISTS = 1;
    public static final int SIGNATURE_APPEND_ONLY = 2;
    protected byte[] xmpMetadata;
    public static final int PDFXNONE = 0;
    public static final int PDFX1A2001 = 1;
    public static final int PDFX32002 = 2;
    public static final int PDFA1A = 3;
    public static final int PDFA1B = 4;
    private PdfXConformanceImp pdfxConformance;
    public static final int STANDARD_ENCRYPTION_40 = 0;
    public static final int STANDARD_ENCRYPTION_128 = 1;
    public static final int ENCRYPTION_AES_128 = 2;
    static final int ENCRYPTION_MASK = 7;
    public static final int DO_NOT_ENCRYPT_METADATA = 8;
    public static final int EMBEDDED_FILES_ONLY = 24;
    public static final int ALLOW_PRINTING = 2052;
    public static final int ALLOW_MODIFY_CONTENTS = 8;
    public static final int ALLOW_COPY = 16;
    public static final int ALLOW_MODIFY_ANNOTATIONS = 32;
    public static final int ALLOW_FILL_IN = 256;
    public static final int ALLOW_SCREENREADERS = 512;
    public static final int ALLOW_ASSEMBLY = 1024;
    public static final int ALLOW_DEGRADED_PRINTING = 4;
    public static final int AllowPrinting = 2052;
    public static final int AllowModifyContents = 8;
    public static final int AllowCopy = 16;
    public static final int AllowModifyAnnotations = 32;
    public static final int AllowFillIn = 256;
    public static final int AllowScreenReaders = 512;
    public static final int AllowAssembly = 1024;
    public static final int AllowDegradedPrinting = 4;
    public static final boolean STRENGTH40BITS = false;
    public static final boolean STRENGTH128BITS = true;
    protected PdfEncryption crypto;
    protected boolean fullCompression;
    protected int compressionLevel;
    protected HashMap documentFonts;
    protected int fontNumber;
    protected HashMap formXObjects;
    protected int formXObjectsCounter;
    protected HashMap importedPages;
    protected PdfReaderInstance currentPdfReaderInstance;
    protected HashMap documentColors;
    protected int colorNumber;
    protected HashMap documentPatterns;
    protected int patternNumber;
    protected HashMap documentShadingPatterns;
    protected HashMap documentShadings;
    protected HashMap documentExtGState;
    protected HashMap documentProperties;
    protected boolean tagged;
    protected PdfStructureTreeRoot structureTreeRoot;
    protected HashSet documentOCG;
    protected ArrayList documentOCGorder;
    protected PdfOCProperties OCProperties;
    protected PdfArray OCGRadioGroup;
    protected PdfArray OCGLocked;
    public static final PdfName PAGE_OPEN = PdfName.O;
    public static final PdfName PAGE_CLOSE = PdfName.C;
    protected PdfDictionary group;
    public static final float SPACE_CHAR_RATIO_DEFAULT = 2.5f;
    public static final float NO_SPACE_CHAR_RATIO = 1.0E7f;
    private float spaceCharRatio;
    public static final int RUN_DIRECTION_DEFAULT = 0;
    public static final int RUN_DIRECTION_NO_BIDI = 1;
    public static final int RUN_DIRECTION_LTR = 2;
    public static final int RUN_DIRECTION_RTL = 3;
    protected int runDirection;
    protected float userunit;
    protected PdfDictionary defaultColorspace;
    protected HashMap documentSpotPatterns;
    protected ColorDetails patternColorspaceRGB;
    protected ColorDetails patternColorspaceGRAY;
    protected ColorDetails patternColorspaceCMYK;
    protected PdfDictionary imageDictionary;
    private HashMap images;
    private boolean userProperties;
    private boolean rgbTransparencyBlending;

    protected PdfWriter() {
        this.root = new PdfPages(this);
        this.pageReferences = new ArrayList();
        this.currentPageNumber = 1;
        this.prevxref = 0;
        this.pdf_version = new PdfVersionImp();
        this.xmpMetadata = null;
        this.pdfxConformance = new PdfXConformanceImp();
        this.fullCompression = false;
        this.compressionLevel = -1;
        this.documentFonts = new HashMap();
        this.fontNumber = 1;
        this.formXObjects = new HashMap();
        this.formXObjectsCounter = 1;
        this.importedPages = new HashMap();
        this.documentColors = new HashMap();
        this.colorNumber = 1;
        this.documentPatterns = new HashMap();
        this.patternNumber = 1;
        this.documentShadingPatterns = new HashMap();
        this.documentShadings = new HashMap();
        this.documentExtGState = new HashMap();
        this.documentProperties = new HashMap();
        this.tagged = false;
        this.documentOCG = new HashSet();
        this.documentOCGorder = new ArrayList();
        this.OCGRadioGroup = new PdfArray();
        this.OCGLocked = new PdfArray();
        this.spaceCharRatio = 2.5f;
        this.runDirection = 1;
        this.userunit = 0.0f;
        this.defaultColorspace = new PdfDictionary();
        this.documentSpotPatterns = new HashMap();
        this.imageDictionary = new PdfDictionary();
        this.images = new HashMap();
    }

    protected PdfWriter(PdfDocument pdfDocument, OutputStream outputStream) {
        super(pdfDocument, outputStream);
        this.root = new PdfPages(this);
        this.pageReferences = new ArrayList();
        this.currentPageNumber = 1;
        this.prevxref = 0;
        this.pdf_version = new PdfVersionImp();
        this.xmpMetadata = null;
        this.pdfxConformance = new PdfXConformanceImp();
        this.fullCompression = false;
        this.compressionLevel = -1;
        this.documentFonts = new HashMap();
        this.fontNumber = 1;
        this.formXObjects = new HashMap();
        this.formXObjectsCounter = 1;
        this.importedPages = new HashMap();
        this.documentColors = new HashMap();
        this.colorNumber = 1;
        this.documentPatterns = new HashMap();
        this.patternNumber = 1;
        this.documentShadingPatterns = new HashMap();
        this.documentShadings = new HashMap();
        this.documentExtGState = new HashMap();
        this.documentProperties = new HashMap();
        this.tagged = false;
        this.documentOCG = new HashSet();
        this.documentOCGorder = new ArrayList();
        this.OCGRadioGroup = new PdfArray();
        this.OCGLocked = new PdfArray();
        this.spaceCharRatio = 2.5f;
        this.runDirection = 1;
        this.userunit = 0.0f;
        this.defaultColorspace = new PdfDictionary();
        this.documentSpotPatterns = new HashMap();
        this.imageDictionary = new PdfDictionary();
        this.images = new HashMap();
        this.pdf = pdfDocument;
        this.directContent = new PdfContentByte(this);
        this.directContentUnder = new PdfContentByte(this);
    }

    public static PdfWriter getInstance(Document document, OutputStream outputStream) throws DocumentException {
        PdfDocument pdfDocument = new PdfDocument();
        document.addDocListener(pdfDocument);
        PdfWriter pdfWriter = new PdfWriter(pdfDocument, outputStream);
        pdfDocument.addWriter(pdfWriter);
        return pdfWriter;
    }

    public static PdfWriter getInstance(Document document, OutputStream outputStream, DocListener docListener) throws DocumentException {
        PdfDocument pdfDocument = new PdfDocument();
        pdfDocument.addDocListener(docListener);
        document.addDocListener(pdfDocument);
        PdfWriter pdfWriter = new PdfWriter(pdfDocument, outputStream);
        pdfDocument.addWriter(pdfWriter);
        return pdfWriter;
    }

    PdfDocument getPdfDocument() {
        return this.pdf;
    }

    public PdfDictionary getInfo() {
        return this.pdf.getInfo();
    }

    public float getVerticalPosition(boolean bl) {
        return this.pdf.getVerticalPosition(bl);
    }

    public PdfContentByte getDirectContent() {
        if (!this.open) {
            throw new RuntimeException("The document is not open.");
        }
        return this.directContent;
    }

    public PdfContentByte getDirectContentUnder() {
        if (!this.open) {
            throw new RuntimeException("The document is not open.");
        }
        return this.directContentUnder;
    }

    void resetContent() {
        this.directContent.reset();
        this.directContentUnder.reset();
    }

    void addLocalDestinations(TreeMap treeMap) throws IOException {
        Iterator iterator = treeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String string = (String)entry.getKey();
            Object[] arrobject = (Object[])entry.getValue();
            PdfDestination pdfDestination = (PdfDestination)arrobject[2];
            if (pdfDestination == null) {
                throw new RuntimeException("The name '" + string + "' has no local destination.");
            }
            if (arrobject[1] == null) {
                arrobject[1] = this.getPdfIndirectReference();
            }
            this.addToBody((PdfObject)pdfDestination, (PdfIndirectReference)arrobject[1]);
        }
    }

    public PdfIndirectObject addToBody(PdfObject pdfObject) throws IOException {
        PdfIndirectObject pdfIndirectObject = this.body.add(pdfObject);
        return pdfIndirectObject;
    }

    public PdfIndirectObject addToBody(PdfObject pdfObject, boolean bl) throws IOException {
        PdfIndirectObject pdfIndirectObject = this.body.add(pdfObject, bl);
        return pdfIndirectObject;
    }

    public PdfIndirectObject addToBody(PdfObject pdfObject, PdfIndirectReference pdfIndirectReference) throws IOException {
        PdfIndirectObject pdfIndirectObject = this.body.add(pdfObject, pdfIndirectReference);
        return pdfIndirectObject;
    }

    public PdfIndirectObject addToBody(PdfObject pdfObject, PdfIndirectReference pdfIndirectReference, boolean bl) throws IOException {
        PdfIndirectObject pdfIndirectObject = this.body.add(pdfObject, pdfIndirectReference, bl);
        return pdfIndirectObject;
    }

    public PdfIndirectObject addToBody(PdfObject pdfObject, int n) throws IOException {
        PdfIndirectObject pdfIndirectObject = this.body.add(pdfObject, n);
        return pdfIndirectObject;
    }

    public PdfIndirectObject addToBody(PdfObject pdfObject, int n, boolean bl) throws IOException {
        PdfIndirectObject pdfIndirectObject = this.body.add(pdfObject, n, bl);
        return pdfIndirectObject;
    }

    public PdfIndirectReference getPdfIndirectReference() {
        return this.body.getPdfIndirectReference();
    }

    int getIndirectReferenceNumber() {
        return this.body.getIndirectReferenceNumber();
    }

    OutputStreamCounter getOs() {
        return this.os;
    }

    protected PdfDictionary getCatalog(PdfIndirectReference pdfIndirectReference) {
        PdfDocument.PdfCatalog pdfCatalog = this.pdf.getCatalog(pdfIndirectReference);
        if (this.tagged) {
            try {
                this.getStructureTreeRoot().buildTree();
            }
            catch (Exception var3_3) {
                throw new ExceptionConverter(var3_3);
            }
            pdfCatalog.put(PdfName.STRUCTTREEROOT, this.structureTreeRoot.getReference());
            PdfDictionary pdfDictionary = new PdfDictionary();
            pdfDictionary.put(PdfName.MARKED, PdfBoolean.PDFTRUE);
            if (this.userProperties) {
                pdfDictionary.put(PdfName.USERPROPERTIES, PdfBoolean.PDFTRUE);
            }
            pdfCatalog.put(PdfName.MARKINFO, pdfDictionary);
        }
        if (!this.documentOCG.isEmpty()) {
            this.fillOCProperties(false);
            pdfCatalog.put(PdfName.OCPROPERTIES, this.OCProperties);
        }
        return pdfCatalog;
    }

    public PdfDictionary getExtraCatalog() {
        if (this.extraCatalog == null) {
            this.extraCatalog = new PdfDictionary();
        }
        return this.extraCatalog;
    }

    public void setLinearPageMode() {
        this.root.setLinearMode(null);
    }

    public int reorderPages(int[] arrn) throws DocumentException {
        return this.root.reorderPages(arrn);
    }

    public PdfIndirectReference getPageReference(int n) {
        PdfIndirectReference pdfIndirectReference;
        if (--n < 0) {
            throw new IndexOutOfBoundsException("The page numbers start at 1.");
        }
        if (n < this.pageReferences.size()) {
            pdfIndirectReference = (PdfIndirectReference)this.pageReferences.get(n);
            if (pdfIndirectReference == null) {
                pdfIndirectReference = this.body.getPdfIndirectReference();
                this.pageReferences.set(n, pdfIndirectReference);
            }
        } else {
            int n2 = n - this.pageReferences.size();
            for (int i = 0; i < n2; ++i) {
                this.pageReferences.add(null);
            }
            pdfIndirectReference = this.body.getPdfIndirectReference();
            this.pageReferences.add(pdfIndirectReference);
        }
        return pdfIndirectReference;
    }

    public int getPageNumber() {
        return this.pdf.getPageNumber();
    }

    PdfIndirectReference getCurrentPage() {
        return this.getPageReference(this.currentPageNumber);
    }

    public int getCurrentPageNumber() {
        return this.currentPageNumber;
    }

    PdfIndirectReference add(PdfPage pdfPage, PdfContents pdfContents) throws PdfException {
        PdfIndirectObject pdfIndirectObject;
        if (!this.open) {
            throw new PdfException("The document isn't open.");
        }
        try {
            pdfIndirectObject = this.addToBody(pdfContents);
        }
        catch (IOException var4_4) {
            throw new ExceptionConverter(var4_4);
        }
        pdfPage.add(pdfIndirectObject.getIndirectReference());
        if (this.group != null) {
            pdfPage.put(PdfName.GROUP, this.group);
            this.group = null;
        } else if (this.rgbTransparencyBlending) {
            PdfDictionary pdfDictionary = new PdfDictionary();
            pdfDictionary.put(PdfName.TYPE, PdfName.GROUP);
            pdfDictionary.put(PdfName.S, PdfName.TRANSPARENCY);
            pdfDictionary.put(PdfName.CS, PdfName.DEVICERGB);
            pdfPage.put(PdfName.GROUP, pdfDictionary);
        }
        this.root.addPage(pdfPage);
        ++this.currentPageNumber;
        return null;
    }

    public void setPageEvent(PdfPageEvent pdfPageEvent) {
        if (pdfPageEvent == null) {
            this.pageEvent = null;
        } else if (this.pageEvent == null) {
            this.pageEvent = pdfPageEvent;
        } else if (this.pageEvent instanceof PdfPageEventForwarder) {
            ((PdfPageEventForwarder)this.pageEvent).addPageEvent(pdfPageEvent);
        } else {
            PdfPageEventForwarder pdfPageEventForwarder = new PdfPageEventForwarder();
            pdfPageEventForwarder.addPageEvent(this.pageEvent);
            pdfPageEventForwarder.addPageEvent(pdfPageEvent);
            this.pageEvent = pdfPageEventForwarder;
        }
    }

    public PdfPageEvent getPageEvent() {
        return this.pageEvent;
    }

    public void open() {
        super.open();
        try {
            this.pdf_version.writeHeader(this.os);
            this.body = new PdfBody(this);
            if (this.pdfxConformance.isPdfX32002()) {
                PdfDictionary pdfDictionary = new PdfDictionary();
                pdfDictionary.put(PdfName.GAMMA, new PdfArray(new float[]{2.2f, 2.2f, 2.2f}));
                pdfDictionary.put(PdfName.MATRIX, new PdfArray(new float[]{0.4124f, 0.2126f, 0.0193f, 0.3576f, 0.7152f, 0.1192f, 0.1805f, 0.0722f, 0.9505f}));
                pdfDictionary.put(PdfName.WHITEPOINT, new PdfArray(new float[]{0.9505f, 1.0f, 1.089f}));
                PdfArray pdfArray = new PdfArray(PdfName.CALRGB);
                pdfArray.add(pdfDictionary);
                this.setDefaultColorspace(PdfName.DEFAULTRGB, this.addToBody(pdfArray).getIndirectReference());
            }
        }
        catch (IOException var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    public void close() {
        if (this.open) {
            if (this.currentPageNumber - 1 != this.pageReferences.size()) {
                throw new RuntimeException("The page " + this.pageReferences.size() + " was requested but the document has only " + (this.currentPageNumber - 1) + " pages.");
            }
            this.pdf.close();
            try {
                Object object;
                Object object2;
                Object object3;
                this.addSharedObjectsToBody();
                PdfIndirectReference pdfIndirectReference = this.root.writePageTree();
                PdfDictionary pdfDictionary = this.getCatalog(pdfIndirectReference);
                if (this.xmpMetadata != null) {
                    object2 = new PdfStream(this.xmpMetadata);
                    object2.put(PdfName.TYPE, PdfName.METADATA);
                    object2.put(PdfName.SUBTYPE, PdfName.XML);
                    if (this.crypto != null && !this.crypto.isMetadataEncrypted()) {
                        object = new PdfArray();
                        object.add(PdfName.CRYPT);
                        object2.put(PdfName.FILTER, (PdfObject)object);
                    }
                    pdfDictionary.put(PdfName.METADATA, this.body.add((PdfObject)object2).getIndirectReference());
                }
                if (this.isPdfX()) {
                    this.pdfxConformance.completeInfoDictionary(this.getInfo());
                    this.pdfxConformance.completeExtraCatalog(this.getExtraCatalog());
                }
                if (this.extraCatalog != null) {
                    pdfDictionary.mergeDifferent(this.extraCatalog);
                }
                this.writeOutlines(pdfDictionary, false);
                object2 = this.addToBody((PdfObject)pdfDictionary, false);
                object = this.addToBody((PdfObject)this.getInfo(), false);
                PdfIndirectReference pdfIndirectReference2 = null;
                PdfObject pdfObject = null;
                this.body.flushObjStm();
                if (this.crypto != null) {
                    object3 = this.addToBody((PdfObject)this.crypto.getEncryptionDictionary(), false);
                    pdfIndirectReference2 = object3.getIndirectReference();
                    pdfObject = this.crypto.getFileID();
                } else {
                    pdfObject = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
                }
                this.body.writeCrossReferenceTable(this.os, object2.getIndirectReference(), object.getIndirectReference(), pdfIndirectReference2, pdfObject, this.prevxref);
                if (this.fullCompression) {
                    this.os.write(PdfWriter.getISOBytes("startxref\n"));
                    this.os.write(PdfWriter.getISOBytes(String.valueOf(this.body.offset())));
                    this.os.write(PdfWriter.getISOBytes("\n%%EOF\n"));
                } else {
                    object3 = new PdfTrailer(this.body.size(), this.body.offset(), object2.getIndirectReference(), object.getIndirectReference(), pdfIndirectReference2, pdfObject, this.prevxref);
                    object3.toPdf(this, this.os);
                }
                super.close();
            }
            catch (IOException var1_2) {
                throw new ExceptionConverter(var1_2);
            }
        }
    }

    protected void addSharedObjectsToBody() throws IOException {
        PdfObject[] arrpdfObject;
        Object[] arrobject;
        Object object;
        Iterator iterator = this.documentFonts.values().iterator();
        while (iterator.hasNext()) {
            arrobject = (Object[])iterator.next();
            arrobject.writeFont(this);
        }
        iterator = this.formXObjects.values().iterator();
        while (iterator.hasNext()) {
            arrobject = (Object[])iterator.next();
            object = (PdfTemplate)arrobject[1];
            if (object != null && object.getIndirectReference() instanceof PRIndirectReference || object == null || object.getType() != 1) continue;
            this.addToBody((PdfObject)object.getFormXObject(this.compressionLevel), object.getIndirectReference());
        }
        iterator = this.importedPages.values().iterator();
        while (iterator.hasNext()) {
            this.currentPdfReaderInstance = (PdfReaderInstance)iterator.next();
            this.currentPdfReaderInstance.writeAllPages();
        }
        this.currentPdfReaderInstance = null;
        iterator = this.documentColors.values().iterator();
        while (iterator.hasNext()) {
            arrobject = (ColorDetails)iterator.next();
            this.addToBody(arrobject.getSpotColor(this), arrobject.getIndirectReference());
        }
        iterator = this.documentPatterns.keySet().iterator();
        while (iterator.hasNext()) {
            arrobject = (PdfPatternPainter)iterator.next();
            this.addToBody((PdfObject)arrobject.getPattern(this.compressionLevel), arrobject.getIndirectReference());
        }
        iterator = this.documentShadingPatterns.keySet().iterator();
        while (iterator.hasNext()) {
            arrobject = (PdfShadingPattern)iterator.next();
            arrobject.addToBody();
        }
        iterator = this.documentShadings.keySet().iterator();
        while (iterator.hasNext()) {
            arrobject = (PdfShading)iterator.next();
            arrobject.addToBody();
        }
        iterator = this.documentExtGState.entrySet().iterator();
        while (iterator.hasNext()) {
            arrobject = (Map.Entry)iterator.next();
            object = (PdfDictionary)arrobject.getKey();
            arrpdfObject = (PdfObject[])arrobject.getValue();
            this.addToBody((PdfObject)object, (PdfIndirectReference)arrpdfObject[1]);
        }
        iterator = this.documentProperties.entrySet().iterator();
        while (iterator.hasNext()) {
            arrobject = (Map.Entry)iterator.next();
            object = arrobject.getKey();
            arrpdfObject = (PdfObject[])arrobject.getValue();
            if (object instanceof PdfLayerMembership) {
                PdfLayerMembership pdfLayerMembership = (PdfLayerMembership)object;
                this.addToBody(pdfLayerMembership.getPdfObject(), pdfLayerMembership.getRef());
                continue;
            }
            if (!(object instanceof PdfDictionary) || object instanceof PdfLayer) continue;
            this.addToBody((PdfObject)((PdfDictionary)object), (PdfIndirectReference)arrpdfObject[1]);
        }
        iterator = this.documentOCG.iterator();
        while (iterator.hasNext()) {
            arrobject = (PdfOCG)iterator.next();
            this.addToBody(arrobject.getPdfObject(), arrobject.getRef());
        }
    }

    public PdfOutline getRootOutline() {
        return this.directContent.getRootOutline();
    }

    public void setOutlines(List list) {
        this.newBookmarks = list;
    }

    protected void writeOutlines(PdfDictionary pdfDictionary, boolean bl) throws IOException {
        if (this.newBookmarks == null || this.newBookmarks.isEmpty()) {
            return;
        }
        PdfDictionary pdfDictionary2 = new PdfDictionary();
        PdfIndirectReference pdfIndirectReference = this.getPdfIndirectReference();
        Object[] arrobject = SimpleBookmark.iterateOutlines(this, pdfIndirectReference, this.newBookmarks, bl);
        pdfDictionary2.put(PdfName.FIRST, (PdfIndirectReference)arrobject[0]);
        pdfDictionary2.put(PdfName.LAST, (PdfIndirectReference)arrobject[1]);
        pdfDictionary2.put(PdfName.COUNT, new PdfNumber((Integer)arrobject[2]));
        this.addToBody((PdfObject)pdfDictionary2, pdfIndirectReference);
        pdfDictionary.put(PdfName.OUTLINES, pdfIndirectReference);
    }

    public void setPdfVersion(char c) {
        this.pdf_version.setPdfVersion(c);
    }

    public void setAtLeastPdfVersion(char c) {
        this.pdf_version.setAtLeastPdfVersion(c);
    }

    public void setPdfVersion(PdfName pdfName) {
        this.pdf_version.setPdfVersion(pdfName);
    }

    PdfVersionImp getPdfVersion() {
        return this.pdf_version;
    }

    public void setViewerPreferences(int n) {
        this.pdf.setViewerPreferences(n);
    }

    public void addViewerPreference(PdfName pdfName, PdfObject pdfObject) {
        this.pdf.addViewerPreference(pdfName, pdfObject);
    }

    public void setPageLabels(PdfPageLabels pdfPageLabels) {
        this.pdf.setPageLabels(pdfPageLabels);
    }

    public void addJavaScript(PdfAction pdfAction) {
        this.pdf.addJavaScript(pdfAction);
    }

    public void addJavaScript(String string, boolean bl) {
        this.addJavaScript(PdfAction.javaScript(string, this, bl));
    }

    public void addJavaScript(String string) {
        this.addJavaScript(string, false);
    }

    public void addJavaScript(String string, PdfAction pdfAction) {
        this.pdf.addJavaScript(string, pdfAction);
    }

    public void addJavaScript(String string, String string2, boolean bl) {
        this.addJavaScript(string, PdfAction.javaScript(string2, this, bl));
    }

    public void addJavaScript(String string, String string2) {
        this.addJavaScript(string, string2, false);
    }

    public void addFileAttachment(String string, byte[] arrby, String string2, String string3) throws IOException {
        this.addFileAttachment(string, PdfFileSpecification.fileEmbedded(this, string2, string3, arrby));
    }

    public void addFileAttachment(String string, PdfFileSpecification pdfFileSpecification) throws IOException {
        this.pdf.addFileAttachment(string, pdfFileSpecification);
    }

    public void addFileAttachment(PdfFileSpecification pdfFileSpecification) throws IOException {
        this.addFileAttachment(null, pdfFileSpecification);
    }

    public void setOpenAction(String string) {
        this.pdf.setOpenAction(string);
    }

    public void setOpenAction(PdfAction pdfAction) {
        this.pdf.setOpenAction(pdfAction);
    }

    public void setAdditionalAction(PdfName pdfName, PdfAction pdfAction) throws DocumentException {
        if (!(pdfName.equals(DOCUMENT_CLOSE) || pdfName.equals(WILL_SAVE) || pdfName.equals(DID_SAVE) || pdfName.equals(WILL_PRINT) || pdfName.equals(DID_PRINT))) {
            throw new DocumentException("Invalid additional action type: " + pdfName.toString());
        }
        this.pdf.addAdditionalAction(pdfName, pdfAction);
    }

    public void setCollection(PdfCollection pdfCollection) {
        this.setAtLeastPdfVersion('7');
        this.pdf.setCollection(pdfCollection);
    }

    public PdfAcroForm getAcroForm() {
        return this.pdf.getAcroForm();
    }

    public void addAnnotation(PdfAnnotation pdfAnnotation) {
        this.pdf.addAnnotation(pdfAnnotation);
    }

    void addAnnotation(PdfAnnotation pdfAnnotation, int n) {
        this.addAnnotation(pdfAnnotation);
    }

    public void addCalculationOrder(PdfFormField pdfFormField) {
        this.pdf.addCalculationOrder(pdfFormField);
    }

    public void setSigFlags(int n) {
        this.pdf.setSigFlags(n);
    }

    public void setXmpMetadata(byte[] arrby) {
        this.xmpMetadata = arrby;
    }

    public void setPageXmpMetadata(byte[] arrby) {
        this.pdf.setXmpMetadata(arrby);
    }

    public void createXmpMetadata() {
        this.setXmpMetadata(this.createXmpMetadataBytes());
    }

    private byte[] createXmpMetadataBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            XmpWriter xmpWriter = new XmpWriter((OutputStream)byteArrayOutputStream, this.pdf.getInfo(), this.pdfxConformance.getPDFXConformance());
            xmpWriter.close();
        }
        catch (IOException var2_3) {
            var2_3.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    public void setPDFXConformance(int n) {
        if (this.pdfxConformance.getPDFXConformance() == n) {
            return;
        }
        if (this.pdf.isOpen()) {
            throw new PdfXConformanceException("PDFX conformance can only be set before opening the document.");
        }
        if (this.crypto != null) {
            throw new PdfXConformanceException("A PDFX conforming document cannot be encrypted.");
        }
        if (n == 3 || n == 4) {
            this.setPdfVersion('4');
        } else if (n != 0) {
            this.setPdfVersion('3');
        }
        this.pdfxConformance.setPDFXConformance(n);
    }

    public int getPDFXConformance() {
        return this.pdfxConformance.getPDFXConformance();
    }

    public boolean isPdfX() {
        return this.pdfxConformance.isPdfX();
    }

    public void setOutputIntents(String string, String string2, String string3, String string4, byte[] arrby) throws IOException {
        this.getExtraCatalog();
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.OUTPUTINTENT);
        if (string2 != null) {
            pdfDictionary.put(PdfName.OUTPUTCONDITION, new PdfString(string2, "UnicodeBig"));
        }
        if (string != null) {
            pdfDictionary.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString(string, "UnicodeBig"));
        }
        if (string3 != null) {
            pdfDictionary.put(PdfName.REGISTRYNAME, new PdfString(string3, "UnicodeBig"));
        }
        if (string4 != null) {
            pdfDictionary.put(PdfName.INFO, new PdfString(string4, "UnicodeBig"));
        }
        if (arrby != null) {
            PdfStream pdfStream = new PdfStream(arrby);
            pdfStream.flateCompress(this.compressionLevel);
            pdfDictionary.put(PdfName.DESTOUTPUTPROFILE, this.addToBody(pdfStream).getIndirectReference());
        }
        pdfDictionary.put(PdfName.S, PdfName.GTS_PDFX);
        this.extraCatalog.put(PdfName.OUTPUTINTENTS, new PdfArray(pdfDictionary));
    }

    public boolean setOutputIntents(PdfReader pdfReader, boolean bl) throws IOException {
        PdfDictionary pdfDictionary = pdfReader.getCatalog();
        PdfArray pdfArray = (PdfArray)PdfReader.getPdfObject(pdfDictionary.get(PdfName.OUTPUTINTENTS));
        if (pdfArray == null) {
            return false;
        }
        ArrayList arrayList = pdfArray.getArrayList();
        if (arrayList.isEmpty()) {
            return false;
        }
        PdfDictionary pdfDictionary2 = (PdfDictionary)PdfReader.getPdfObject((PdfObject)arrayList.get(0));
        PdfObject pdfObject = PdfReader.getPdfObject(pdfDictionary2.get(PdfName.S));
        if (pdfObject == null || !PdfName.GTS_PDFX.equals(pdfObject)) {
            return false;
        }
        if (bl) {
            return true;
        }
        PRStream pRStream = (PRStream)PdfReader.getPdfObject(pdfDictionary2.get(PdfName.DESTOUTPUTPROFILE));
        byte[] arrby = null;
        if (pRStream != null) {
            arrby = PdfReader.getStreamBytes(pRStream);
        }
        this.setOutputIntents(PdfWriter.getNameString(pdfDictionary2, PdfName.OUTPUTCONDITIONIDENTIFIER), PdfWriter.getNameString(pdfDictionary2, PdfName.OUTPUTCONDITION), PdfWriter.getNameString(pdfDictionary2, PdfName.REGISTRYNAME), PdfWriter.getNameString(pdfDictionary2, PdfName.INFO), arrby);
        return true;
    }

    private static String getNameString(PdfDictionary pdfDictionary, PdfName pdfName) {
        PdfObject pdfObject = PdfReader.getPdfObject(pdfDictionary.get(pdfName));
        if (pdfObject == null || !pdfObject.isString()) {
            return null;
        }
        return ((PdfString)pdfObject).toUnicodeString();
    }

    PdfEncryption getEncryption() {
        return this.crypto;
    }

    public void setEncryption(byte[] arrby, byte[] arrby2, int n, int n2) throws DocumentException {
        if (this.pdf.isOpen()) {
            throw new DocumentException("Encryption can only be added before opening the document.");
        }
        this.crypto = new PdfEncryption();
        this.crypto.setCryptoMode(n2, 0);
        this.crypto.setupAllKeys(arrby, arrby2, n);
    }

    public void setEncryption(Certificate[] arrcertificate, int[] arrn, int n) throws DocumentException {
        if (this.pdf.isOpen()) {
            throw new DocumentException("Encryption can only be added before opening the document.");
        }
        this.crypto = new PdfEncryption();
        if (arrcertificate != null) {
            for (int i = 0; i < arrcertificate.length; ++i) {
                this.crypto.addRecipient(arrcertificate[i], arrn[i]);
            }
        }
        this.crypto.setCryptoMode(n, 0);
        this.crypto.getEncryptionDictionary();
    }

    public void setEncryption(byte[] arrby, byte[] arrby2, int n, boolean bl) throws DocumentException {
        this.setEncryption(arrby, arrby2, n, bl ? 1 : 0);
    }

    public void setEncryption(boolean bl, String string, String string2, int n) throws DocumentException {
        this.setEncryption(PdfWriter.getISOBytes(string), PdfWriter.getISOBytes(string2), n, bl ? 1 : 0);
    }

    public void setEncryption(int n, String string, String string2, int n2) throws DocumentException {
        this.setEncryption(PdfWriter.getISOBytes(string), PdfWriter.getISOBytes(string2), n2, n);
    }

    public boolean isFullCompression() {
        return this.fullCompression;
    }

    public void setFullCompression() {
        this.fullCompression = true;
        this.setAtLeastPdfVersion('5');
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int n) {
        this.compressionLevel = n < 0 || n > 9 ? -1 : n;
    }

    FontDetails addSimple(BaseFont baseFont) {
        if (baseFont.getFontType() == 4) {
            return new FontDetails(new PdfName("F" + this.fontNumber++), ((DocumentFont)baseFont).getIndirectReference(), baseFont);
        }
        FontDetails fontDetails = (FontDetails)this.documentFonts.get(baseFont);
        if (fontDetails == null) {
            PdfXConformanceImp.checkPDFXConformance(this, 4, baseFont);
            fontDetails = new FontDetails(new PdfName("F" + this.fontNumber++), this.body.getPdfIndirectReference(), baseFont);
            this.documentFonts.put(baseFont, fontDetails);
        }
        return fontDetails;
    }

    void eliminateFontSubset(PdfDictionary pdfDictionary) {
        Iterator iterator = this.documentFonts.values().iterator();
        while (iterator.hasNext()) {
            FontDetails fontDetails = (FontDetails)iterator.next();
            if (pdfDictionary.get(fontDetails.getFontName()) == null) continue;
            fontDetails.setSubset(false);
        }
    }

    PdfName addDirectTemplateSimple(PdfTemplate pdfTemplate, PdfName pdfName) {
        PdfIndirectReference pdfIndirectReference = pdfTemplate.getIndirectReference();
        Object[] arrobject = (Object[])this.formXObjects.get(pdfIndirectReference);
        PdfName pdfName2 = null;
        try {
            if (arrobject == null) {
                if (pdfName == null) {
                    pdfName2 = new PdfName("Xf" + this.formXObjectsCounter);
                    ++this.formXObjectsCounter;
                } else {
                    pdfName2 = pdfName;
                }
                if (pdfTemplate.getType() == 2) {
                    PdfImportedPage pdfImportedPage = (PdfImportedPage)pdfTemplate;
                    PdfReader pdfReader = pdfImportedPage.getPdfReaderInstance().getReader();
                    if (!this.importedPages.containsKey(pdfReader)) {
                        this.importedPages.put(pdfReader, pdfImportedPage.getPdfReaderInstance());
                    }
                    pdfTemplate = null;
                }
                this.formXObjects.put(pdfIndirectReference, new Object[]{pdfName2, pdfTemplate});
            } else {
                pdfName2 = (PdfName)arrobject[0];
            }
        }
        catch (Exception var6_7) {
            throw new ExceptionConverter(var6_7);
        }
        return pdfName2;
    }

    public void releaseTemplate(PdfTemplate pdfTemplate) throws IOException {
        PdfIndirectReference pdfIndirectReference = pdfTemplate.getIndirectReference();
        Object[] arrobject = (Object[])this.formXObjects.get(pdfIndirectReference);
        if (arrobject == null || arrobject[1] == null) {
            return;
        }
        PdfTemplate pdfTemplate2 = (PdfTemplate)arrobject[1];
        if (pdfTemplate2.getIndirectReference() instanceof PRIndirectReference) {
            return;
        }
        if (pdfTemplate2.getType() == 1) {
            this.addToBody((PdfObject)pdfTemplate2.getFormXObject(this.compressionLevel), pdfTemplate2.getIndirectReference());
            arrobject[1] = null;
        }
    }

    public PdfImportedPage getImportedPage(PdfReader pdfReader, int n) {
        PdfReaderInstance pdfReaderInstance = (PdfReaderInstance)this.importedPages.get(pdfReader);
        if (pdfReaderInstance == null) {
            pdfReaderInstance = pdfReader.getPdfReaderInstance(this);
            this.importedPages.put(pdfReader, pdfReaderInstance);
        }
        return pdfReaderInstance.getImportedPage(n);
    }

    public void freeReader(PdfReader pdfReader) throws IOException {
        this.currentPdfReaderInstance = (PdfReaderInstance)this.importedPages.get(pdfReader);
        if (this.currentPdfReaderInstance == null) {
            return;
        }
        this.currentPdfReaderInstance.writeAllPages();
        this.currentPdfReaderInstance = null;
        this.importedPages.remove(pdfReader);
    }

    public int getCurrentDocumentSize() {
        return this.body.offset() + this.body.size() * 20 + 72;
    }

    protected int getNewObjectNumber(PdfReader pdfReader, int n, int n2) {
        return this.currentPdfReaderInstance.getNewObjectNumber(n, n2);
    }

    RandomAccessFileOrArray getReaderFile(PdfReader pdfReader) {
        return this.currentPdfReaderInstance.getReaderFile();
    }

    PdfName getColorspaceName() {
        return new PdfName("CS" + this.colorNumber++);
    }

    ColorDetails addSimple(PdfSpotColor pdfSpotColor) {
        ColorDetails colorDetails = (ColorDetails)this.documentColors.get(pdfSpotColor);
        if (colorDetails == null) {
            colorDetails = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), pdfSpotColor);
            this.documentColors.put(pdfSpotColor, colorDetails);
        }
        return colorDetails;
    }

    PdfName addSimplePattern(PdfPatternPainter pdfPatternPainter) {
        PdfName pdfName = (PdfName)this.documentPatterns.get(pdfPatternPainter);
        try {
            if (pdfName == null) {
                pdfName = new PdfName("P" + this.patternNumber);
                ++this.patternNumber;
                this.documentPatterns.put(pdfPatternPainter, pdfName);
            }
        }
        catch (Exception var3_3) {
            throw new ExceptionConverter(var3_3);
        }
        return pdfName;
    }

    void addSimpleShadingPattern(PdfShadingPattern pdfShadingPattern) {
        if (!this.documentShadingPatterns.containsKey(pdfShadingPattern)) {
            pdfShadingPattern.setName(this.patternNumber);
            ++this.patternNumber;
            this.documentShadingPatterns.put(pdfShadingPattern, null);
            this.addSimpleShading(pdfShadingPattern.getShading());
        }
    }

    void addSimpleShading(PdfShading pdfShading) {
        if (!this.documentShadings.containsKey(pdfShading)) {
            this.documentShadings.put(pdfShading, null);
            pdfShading.setName(this.documentShadings.size());
        }
    }

    PdfObject[] addSimpleExtGState(PdfDictionary pdfDictionary) {
        if (!this.documentExtGState.containsKey(pdfDictionary)) {
            PdfXConformanceImp.checkPDFXConformance(this, 6, pdfDictionary);
            this.documentExtGState.put(pdfDictionary, new PdfObject[]{new PdfName("GS" + (this.documentExtGState.size() + 1)), this.getPdfIndirectReference()});
        }
        return (PdfObject[])this.documentExtGState.get(pdfDictionary);
    }

    PdfObject[] addSimpleProperty(Object object, PdfIndirectReference pdfIndirectReference) {
        if (!this.documentProperties.containsKey(object)) {
            if (object instanceof PdfOCG) {
                PdfXConformanceImp.checkPDFXConformance(this, 7, null);
            }
            this.documentProperties.put(object, new PdfObject[]{new PdfName("Pr" + (this.documentProperties.size() + 1)), pdfIndirectReference});
        }
        return (PdfObject[])this.documentProperties.get(object);
    }

    boolean propertyExists(Object object) {
        return this.documentProperties.containsKey(object);
    }

    public void setTagged() {
        if (this.open) {
            throw new IllegalArgumentException("Tagging must be set before opening the document.");
        }
        this.tagged = true;
    }

    public boolean isTagged() {
        return this.tagged;
    }

    public PdfStructureTreeRoot getStructureTreeRoot() {
        if (this.tagged && this.structureTreeRoot == null) {
            this.structureTreeRoot = new PdfStructureTreeRoot(this);
        }
        return this.structureTreeRoot;
    }

    public PdfOCProperties getOCProperties() {
        this.fillOCProperties(true);
        return this.OCProperties;
    }

    public void addOCGRadioGroup(ArrayList arrayList) {
        PdfArray pdfArray = new PdfArray();
        for (int i = 0; i < arrayList.size(); ++i) {
            PdfLayer pdfLayer = (PdfLayer)arrayList.get(i);
            if (pdfLayer.getTitle() != null) continue;
            pdfArray.add(pdfLayer.getRef());
        }
        if (pdfArray.size() == 0) {
            return;
        }
        this.OCGRadioGroup.add(pdfArray);
    }

    public void lockLayer(PdfLayer pdfLayer) {
        this.OCGLocked.add(pdfLayer.getRef());
    }

    private static void getOCGOrder(PdfArray pdfArray, PdfLayer pdfLayer) {
        ArrayList arrayList;
        if (!pdfLayer.isOnPanel()) {
            return;
        }
        if (pdfLayer.getTitle() == null) {
            pdfArray.add(pdfLayer.getRef());
        }
        if ((arrayList = pdfLayer.getChildren()) == null) {
            return;
        }
        PdfArray pdfArray2 = new PdfArray();
        if (pdfLayer.getTitle() != null) {
            pdfArray2.add(new PdfString(pdfLayer.getTitle(), "UnicodeBig"));
        }
        for (int i = 0; i < arrayList.size(); ++i) {
            PdfWriter.getOCGOrder(pdfArray2, (PdfLayer)arrayList.get(i));
        }
        if (pdfArray2.size() > 0) {
            pdfArray.add(pdfArray2);
        }
    }

    private void addASEvent(PdfName pdfName, PdfName pdfName2) {
        PdfDictionary pdfDictionary;
        void var5_8;
        PdfArray pdfArray = new PdfArray();
        Object object = this.documentOCG.iterator();
        while (object.hasNext()) {
            PdfLayer pdfLayer = (PdfLayer)object.next();
            pdfDictionary = (PdfDictionary)pdfLayer.get(PdfName.USAGE);
            if (pdfDictionary == null || pdfDictionary.get(pdfName2) == null) continue;
            pdfArray.add(pdfLayer.getRef());
        }
        if (pdfArray.size() == 0) {
            return;
        }
        object = (PdfDictionary)this.OCProperties.get(PdfName.D);
        PdfArray pdfArray2 = (PdfArray)object.get(PdfName.AS);
        if (pdfArray2 == null) {
            PdfArray pdfArray3 = new PdfArray();
            object.put(PdfName.AS, pdfArray3);
        }
        pdfDictionary = new PdfDictionary();
        pdfDictionary.put(PdfName.EVENT, pdfName);
        pdfDictionary.put(PdfName.CATEGORY, new PdfArray(pdfName2));
        pdfDictionary.put(PdfName.OCGS, pdfArray);
        var5_8.add(pdfDictionary);
    }

    protected void fillOCProperties(boolean bl) {
        Object object3;
        Object object2;
        Object object;
        if (this.OCProperties == null) {
            this.OCProperties = new PdfOCProperties();
        }
        if (bl) {
            this.OCProperties.remove(PdfName.OCGS);
            this.OCProperties.remove(PdfName.D);
        }
        if (this.OCProperties.get(PdfName.OCGS) == null) {
            object2 = new PdfArray();
            object3 = this.documentOCG.iterator();
            while (object3.hasNext()) {
                object = (PdfLayer)object3.next();
                object2.add(object.getRef());
            }
            this.OCProperties.put(PdfName.OCGS, (PdfObject)object2);
        }
        if (this.OCProperties.get(PdfName.D) != null) {
            return;
        }
        object2 = new ArrayList(this.documentOCGorder);
        object3 = object2.iterator();
        while (object3.hasNext()) {
            object = (PdfLayer)object3.next();
            if (object.getParent() == null) continue;
            object3.remove();
        }
        object3 = new PdfArray();
        object = object2.iterator();
        while (object.hasNext()) {
            PdfLayer pdfLayer = (PdfLayer)object.next();
            PdfWriter.getOCGOrder((PdfArray)object3, pdfLayer);
        }
        object = new PdfDictionary();
        this.OCProperties.put(PdfName.D, (PdfObject)object);
        object.put(PdfName.ORDER, (PdfObject)object3);
        PdfArray pdfArray = new PdfArray();
        Iterator iterator = this.documentOCG.iterator();
        while (iterator.hasNext()) {
            PdfLayer pdfLayer = (PdfLayer)iterator.next();
            if (pdfLayer.isOn()) continue;
            pdfArray.add(pdfLayer.getRef());
        }
        if (pdfArray.size() > 0) {
            object.put(PdfName.OFF, pdfArray);
        }
        if (this.OCGRadioGroup.size() > 0) {
            object.put(PdfName.RBGROUPS, this.OCGRadioGroup);
        }
        if (this.OCGLocked.size() > 0) {
            object.put(PdfName.LOCKED, this.OCGLocked);
        }
        this.addASEvent(PdfName.VIEW, PdfName.ZOOM);
        this.addASEvent(PdfName.VIEW, PdfName.VIEW);
        this.addASEvent(PdfName.PRINT, PdfName.PRINT);
        this.addASEvent(PdfName.EXPORT, PdfName.EXPORT);
        object.put(PdfName.LISTMODE, PdfName.VISIBLEPAGES);
    }

    void registerLayer(PdfOCG pdfOCG) {
        PdfXConformanceImp.checkPDFXConformance(this, 7, null);
        if (pdfOCG instanceof PdfLayer) {
            PdfLayer pdfLayer = (PdfLayer)pdfOCG;
            if (pdfLayer.getTitle() == null) {
                if (!this.documentOCG.contains(pdfOCG)) {
                    this.documentOCG.add(pdfOCG);
                    this.documentOCGorder.add(pdfOCG);
                }
            } else {
                this.documentOCGorder.add(pdfOCG);
            }
        } else {
            throw new IllegalArgumentException("Only PdfLayer is accepted.");
        }
    }

    public Rectangle getPageSize() {
        return this.pdf.getPageSize();
    }

    public void setCropBoxSize(Rectangle rectangle) {
        this.pdf.setCropBoxSize(rectangle);
    }

    public void setBoxSize(String string, Rectangle rectangle) {
        this.pdf.setBoxSize(string, rectangle);
    }

    public Rectangle getBoxSize(String string) {
        return this.pdf.getBoxSize(string);
    }

    public void setPageEmpty(boolean bl) {
        this.pdf.setPageEmpty(bl);
    }

    public void setPageAction(PdfName pdfName, PdfAction pdfAction) throws DocumentException {
        if (!pdfName.equals(PAGE_OPEN) && !pdfName.equals(PAGE_CLOSE)) {
            throw new DocumentException("Invalid page additional action type: " + pdfName.toString());
        }
        this.pdf.setPageAction(pdfName, pdfAction);
    }

    public void setDuration(int n) {
        this.pdf.setDuration(n);
    }

    public void setTransition(PdfTransition pdfTransition) {
        this.pdf.setTransition(pdfTransition);
    }

    public void setThumbnail(Image image) throws PdfException, DocumentException {
        this.pdf.setThumbnail(image);
    }

    public PdfDictionary getGroup() {
        return this.group;
    }

    public void setGroup(PdfDictionary pdfDictionary) {
        this.group = pdfDictionary;
    }

    public float getSpaceCharRatio() {
        return this.spaceCharRatio;
    }

    public void setSpaceCharRatio(float f) {
        this.spaceCharRatio = f < 0.001f ? 0.001f : f;
    }

    public void setRunDirection(int n) {
        if (n < 1 || n > 3) {
            throw new RuntimeException("Invalid run direction: " + n);
        }
        this.runDirection = n;
    }

    public int getRunDirection() {
        return this.runDirection;
    }

    public float getUserunit() {
        return this.userunit;
    }

    public void setUserunit(float f) throws DocumentException {
        if (f < 1.0f || f > 75000.0f) {
            throw new DocumentException("UserUnit should be a value between 1 and 75000.");
        }
        this.userunit = f;
        this.setAtLeastPdfVersion('6');
    }

    public PdfDictionary getDefaultColorspace() {
        return this.defaultColorspace;
    }

    public void setDefaultColorspace(PdfName pdfName, PdfObject pdfObject) {
        if (pdfObject == null || pdfObject.isNull()) {
            this.defaultColorspace.remove(pdfName);
        }
        this.defaultColorspace.put(pdfName, pdfObject);
    }

    ColorDetails addSimplePatternColorspace(Color color) {
        int n = ExtendedColor.getType(color);
        if (n == 4 || n == 5) {
            throw new RuntimeException("An uncolored tile pattern can not have another pattern or shading as color.");
        }
        try {
            switch (n) {
                case 0: {
                    if (this.patternColorspaceRGB == null) {
                        this.patternColorspaceRGB = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray pdfArray = new PdfArray(PdfName.PATTERN);
                        pdfArray.add(PdfName.DEVICERGB);
                        this.addToBody((PdfObject)pdfArray, this.patternColorspaceRGB.getIndirectReference());
                    }
                    return this.patternColorspaceRGB;
                }
                case 2: {
                    if (this.patternColorspaceCMYK == null) {
                        this.patternColorspaceCMYK = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray pdfArray = new PdfArray(PdfName.PATTERN);
                        pdfArray.add(PdfName.DEVICECMYK);
                        this.addToBody((PdfObject)pdfArray, this.patternColorspaceCMYK.getIndirectReference());
                    }
                    return this.patternColorspaceCMYK;
                }
                case 1: {
                    if (this.patternColorspaceGRAY == null) {
                        this.patternColorspaceGRAY = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray pdfArray = new PdfArray(PdfName.PATTERN);
                        pdfArray.add(PdfName.DEVICEGRAY);
                        this.addToBody((PdfObject)pdfArray, this.patternColorspaceGRAY.getIndirectReference());
                    }
                    return this.patternColorspaceGRAY;
                }
                case 3: {
                    ColorDetails colorDetails = this.addSimple(((SpotColor)color).getPdfSpotColor());
                    ColorDetails colorDetails2 = (ColorDetails)this.documentSpotPatterns.get(colorDetails);
                    if (colorDetails2 == null) {
                        colorDetails2 = new ColorDetails(this.getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray pdfArray = new PdfArray(PdfName.PATTERN);
                        pdfArray.add(colorDetails.getIndirectReference());
                        this.addToBody((PdfObject)pdfArray, colorDetails2.getIndirectReference());
                        this.documentSpotPatterns.put(colorDetails, colorDetails2);
                    }
                    return colorDetails2;
                }
            }
            throw new RuntimeException("Invalid color type in PdfWriter.addSimplePatternColorspace().");
        }
        catch (Exception var3_7) {
            throw new RuntimeException(var3_7.getMessage());
        }
    }

    public boolean isStrictImageSequence() {
        return this.pdf.isStrictImageSequence();
    }

    public void setStrictImageSequence(boolean bl) {
        this.pdf.setStrictImageSequence(bl);
    }

    public void clearTextWrap() throws DocumentException {
        this.pdf.clearTextWrap();
    }

    public PdfName addDirectImageSimple(Image image) throws PdfException, DocumentException {
        return this.addDirectImageSimple(image, null);
    }

    public PdfName addDirectImageSimple(Image image, PdfIndirectReference pdfIndirectReference) throws PdfException, DocumentException {
        PdfName pdfName;
        if (this.images.containsKey(image.getMySerialId())) {
            pdfName = (PdfName)this.images.get(image.getMySerialId());
        } else {
            if (image.isImgTemplate()) {
                pdfName = new PdfName("img" + this.images.size());
                if (image instanceof ImgWMF) {
                    try {
                        ImgWMF imgWMF = (ImgWMF)image;
                        imgWMF.readWMF(PdfTemplate.createTemplate(this, 0.0f, 0.0f));
                    }
                    catch (Exception var4_5) {
                        throw new DocumentException(var4_5);
                    }
                }
            } else {
                PdfIndirectReference pdfIndirectReference2 = image.getDirectReference();
                if (pdfIndirectReference2 != null) {
                    PdfName pdfName3 = new PdfName("img" + this.images.size());
                    this.images.put(image.getMySerialId(), pdfName3);
                    this.imageDictionary.put(pdfName3, pdfIndirectReference2);
                    return pdfName3;
                }
                Image image2 = image.getImageMask();
                PdfIndirectReference pdfIndirectReference3 = null;
                if (image2 != null) {
                    PdfName pdfName2 = (PdfName)this.images.get(image2.getMySerialId());
                    pdfIndirectReference3 = this.getImageReference(pdfName2);
                }
                PdfImage pdfImage = new PdfImage(image, "img" + this.images.size(), pdfIndirectReference3);
                if (image.hasICCProfile()) {
                    PdfICCBased pdfICCBased = new PdfICCBased(image.getICCProfile(), image.getCompressionLevel());
                    PdfIndirectReference pdfIndirectReference4 = this.add(pdfICCBased);
                    PdfArray pdfArray = new PdfArray();
                    pdfArray.add(PdfName.ICCBASED);
                    pdfArray.add(pdfIndirectReference4);
                    PdfObject pdfObject = pdfImage.get(PdfName.COLORSPACE);
                    if (pdfObject != null && pdfObject.isArray()) {
                        ArrayList arrayList = ((PdfArray)pdfObject).getArrayList();
                        if (arrayList.size() > 1 && PdfName.INDEXED.equals(arrayList.get(0))) {
                            arrayList.set(1, pdfArray);
                        } else {
                            pdfImage.put(PdfName.COLORSPACE, pdfArray);
                        }
                    } else {
                        pdfImage.put(PdfName.COLORSPACE, pdfArray);
                    }
                }
                this.add(pdfImage, pdfIndirectReference);
                pdfName = pdfImage.name();
            }
            this.images.put(image.getMySerialId(), pdfName);
        }
        return pdfName;
    }

    PdfIndirectReference add(PdfImage pdfImage, PdfIndirectReference pdfIndirectReference) throws PdfException {
        if (!this.imageDictionary.contains(pdfImage.name())) {
            PdfXConformanceImp.checkPDFXConformance(this, 5, pdfImage);
            if (pdfIndirectReference instanceof PRIndirectReference) {
                PRIndirectReference pRIndirectReference = (PRIndirectReference)pdfIndirectReference;
                pdfIndirectReference = new PdfIndirectReference(0, this.getNewObjectNumber(pRIndirectReference.getReader(), pRIndirectReference.getNumber(), pRIndirectReference.getGeneration()));
            }
            try {
                if (pdfIndirectReference == null) {
                    pdfIndirectReference = this.addToBody(pdfImage).getIndirectReference();
                } else {
                    this.addToBody((PdfObject)pdfImage, pdfIndirectReference);
                }
            }
            catch (IOException var3_4) {
                throw new ExceptionConverter(var3_4);
            }
            this.imageDictionary.put(pdfImage.name(), pdfIndirectReference);
            return pdfIndirectReference;
        }
        return (PdfIndirectReference)this.imageDictionary.get(pdfImage.name());
    }

    PdfIndirectReference getImageReference(PdfName pdfName) {
        return (PdfIndirectReference)this.imageDictionary.get(pdfName);
    }

    protected PdfIndirectReference add(PdfICCBased pdfICCBased) {
        PdfIndirectObject pdfIndirectObject;
        try {
            pdfIndirectObject = this.addToBody(pdfICCBased);
        }
        catch (IOException var3_3) {
            throw new ExceptionConverter(var3_3);
        }
        return pdfIndirectObject.getIndirectReference();
    }

    public boolean fitsPage(Table table, float f) {
        return this.pdf.bottom(table) > this.pdf.indentBottom() + f;
    }

    public boolean fitsPage(Table table) {
        return this.fitsPage(table, 0.0f);
    }

    public boolean isUserProperties() {
        return this.userProperties;
    }

    public void setUserProperties(boolean bl) {
        this.userProperties = bl;
    }

    public boolean isRgbTransparencyBlending() {
        return this.rgbTransparencyBlending;
    }

    public void setRgbTransparencyBlending(boolean bl) {
        this.rgbTransparencyBlending = bl;
    }

    static class PdfTrailer
    extends PdfDictionary {
        int offset;

        PdfTrailer(int n, int n2, PdfIndirectReference pdfIndirectReference, PdfIndirectReference pdfIndirectReference2, PdfIndirectReference pdfIndirectReference3, PdfObject pdfObject, int n3) {
            this.offset = n2;
            this.put(PdfName.SIZE, new PdfNumber(n));
            this.put(PdfName.ROOT, pdfIndirectReference);
            if (pdfIndirectReference2 != null) {
                this.put(PdfName.INFO, pdfIndirectReference2);
            }
            if (pdfIndirectReference3 != null) {
                this.put(PdfName.ENCRYPT, pdfIndirectReference3);
            }
            if (pdfObject != null) {
                this.put(PdfName.ID, pdfObject);
            }
            if (n3 > 0) {
                this.put(PdfName.PREV, new PdfNumber(n3));
            }
        }

        public void toPdf(PdfWriter pdfWriter, OutputStream outputStream) throws IOException {
            outputStream.write(DocWriter.getISOBytes("trailer\n"));
            super.toPdf(null, outputStream);
            outputStream.write(DocWriter.getISOBytes("\nstartxref\n"));
            outputStream.write(DocWriter.getISOBytes(String.valueOf(this.offset)));
            outputStream.write(DocWriter.getISOBytes("\n%%EOF\n"));
        }
    }

    public static class PdfBody {
        private static final int OBJSINSTREAM = 200;
        private TreeSet xrefs = new TreeSet();
        private int refnum;
        private int position;
        private PdfWriter writer;
        private ByteBuffer index;
        private ByteBuffer streamObjects;
        private int currentObjNum;
        private int numObj = 0;

        PdfBody(PdfWriter pdfWriter) {
            this.xrefs.add(new PdfCrossReference(0, 0, 65535));
            this.position = pdfWriter.getOs().getCounter();
            this.refnum = 1;
            this.writer = pdfWriter;
        }

        void setRefnum(int n) {
            this.refnum = n;
        }

        private PdfCrossReference addToObjStm(PdfObject pdfObject, int n) throws IOException {
            if (this.numObj >= 200) {
                this.flushObjStm();
            }
            if (this.index == null) {
                this.index = new ByteBuffer();
                this.streamObjects = new ByteBuffer();
                this.currentObjNum = this.getIndirectReferenceNumber();
                this.numObj = 0;
            }
            int n2 = this.streamObjects.size();
            int n3 = this.numObj++;
            PdfEncryption pdfEncryption = this.writer.crypto;
            this.writer.crypto = null;
            pdfObject.toPdf(this.writer, this.streamObjects);
            this.writer.crypto = pdfEncryption;
            this.streamObjects.append(' ');
            this.index.append(n).append(' ').append(n2).append(' ');
            return new PdfCrossReference(2, n, this.currentObjNum, n3);
        }

        private void flushObjStm() throws IOException {
            if (this.numObj == 0) {
                return;
            }
            int n = this.index.size();
            this.index.append(this.streamObjects);
            PdfStream pdfStream = new PdfStream(this.index.toByteArray());
            pdfStream.flateCompress(this.writer.getCompressionLevel());
            pdfStream.put(PdfName.TYPE, PdfName.OBJSTM);
            pdfStream.put(PdfName.N, new PdfNumber(this.numObj));
            pdfStream.put(PdfName.FIRST, new PdfNumber(n));
            this.add((PdfObject)pdfStream, this.currentObjNum);
            this.index = null;
            this.streamObjects = null;
            this.numObj = 0;
        }

        PdfIndirectObject add(PdfObject pdfObject) throws IOException {
            return this.add(pdfObject, this.getIndirectReferenceNumber());
        }

        PdfIndirectObject add(PdfObject pdfObject, boolean bl) throws IOException {
            return this.add(pdfObject, this.getIndirectReferenceNumber(), bl);
        }

        PdfIndirectReference getPdfIndirectReference() {
            return new PdfIndirectReference(0, this.getIndirectReferenceNumber());
        }

        int getIndirectReferenceNumber() {
            int n = this.refnum++;
            this.xrefs.add(new PdfCrossReference(n, 0, 65536));
            return n;
        }

        PdfIndirectObject add(PdfObject pdfObject, PdfIndirectReference pdfIndirectReference) throws IOException {
            return this.add(pdfObject, pdfIndirectReference.getNumber());
        }

        PdfIndirectObject add(PdfObject pdfObject, PdfIndirectReference pdfIndirectReference, boolean bl) throws IOException {
            return this.add(pdfObject, pdfIndirectReference.getNumber(), bl);
        }

        PdfIndirectObject add(PdfObject pdfObject, int n) throws IOException {
            return this.add(pdfObject, n, true);
        }

        PdfIndirectObject add(PdfObject pdfObject, int n, boolean bl) throws IOException {
            if (bl && pdfObject.canBeInObjStm() && this.writer.isFullCompression()) {
                PdfCrossReference pdfCrossReference = this.addToObjStm(pdfObject, n);
                PdfIndirectObject pdfIndirectObject = new PdfIndirectObject(n, pdfObject, this.writer);
                if (!this.xrefs.add(pdfCrossReference)) {
                    this.xrefs.remove(pdfCrossReference);
                    this.xrefs.add(pdfCrossReference);
                }
                return pdfIndirectObject;
            }
            PdfIndirectObject pdfIndirectObject = new PdfIndirectObject(n, pdfObject, this.writer);
            PdfCrossReference pdfCrossReference = new PdfCrossReference(n, this.position);
            if (!this.xrefs.add(pdfCrossReference)) {
                this.xrefs.remove(pdfCrossReference);
                this.xrefs.add(pdfCrossReference);
            }
            pdfIndirectObject.writeTo(this.writer.getOs());
            this.position = this.writer.getOs().getCounter();
            return pdfIndirectObject;
        }

        int offset() {
            return this.position;
        }

        int size() {
            return Math.max(((PdfCrossReference)this.xrefs.last()).getRefnum() + 1, this.refnum);
        }

        void writeCrossReferenceTable(OutputStream outputStream, PdfIndirectReference pdfIndirectReference, PdfIndirectReference pdfIndirectReference2, PdfIndirectReference pdfIndirectReference3, PdfObject pdfObject, int n) throws IOException {
            int n2 = 0;
            if (this.writer.isFullCompression()) {
                this.flushObjStm();
                n2 = this.getIndirectReferenceNumber();
                this.xrefs.add(new PdfCrossReference(n2, this.position));
            }
            PdfCrossReference pdfCrossReference = (PdfCrossReference)this.xrefs.first();
            int n3 = pdfCrossReference.getRefnum();
            int n4 = 0;
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            Iterator iterator = this.xrefs.iterator();
            while (iterator.hasNext()) {
                pdfCrossReference = (PdfCrossReference)iterator.next();
                if (n3 + n4 == pdfCrossReference.getRefnum()) {
                    ++n4;
                    continue;
                }
                arrayList.add(new Integer(n3));
                arrayList.add(new Integer(n4));
                n3 = pdfCrossReference.getRefnum();
                n4 = 1;
            }
            arrayList.add(new Integer(n3));
            arrayList.add(new Integer(n4));
            if (this.writer.isFullCompression()) {
                int n5;
                int n6 = -16777216;
                for (n5 = 4; n5 > 1 && (n6 & this.position) == 0; --n5) {
                    n6 >>>= 8;
                }
                ByteBuffer byteBuffer = new ByteBuffer();
                Object object = this.xrefs.iterator();
                while (object.hasNext()) {
                    pdfCrossReference = (PdfCrossReference)object.next();
                    pdfCrossReference.toPdf(n5, byteBuffer);
                }
                object = new PdfStream(byteBuffer.toByteArray());
                byteBuffer = null;
                object.flateCompress(this.writer.getCompressionLevel());
                object.put(PdfName.SIZE, new PdfNumber(this.size()));
                object.put(PdfName.ROOT, pdfIndirectReference);
                if (pdfIndirectReference2 != null) {
                    object.put(PdfName.INFO, pdfIndirectReference2);
                }
                if (pdfIndirectReference3 != null) {
                    object.put(PdfName.ENCRYPT, pdfIndirectReference3);
                }
                if (pdfObject != null) {
                    object.put(PdfName.ID, pdfObject);
                }
                object.put(PdfName.W, new PdfArray(new int[]{1, n5, 2}));
                object.put(PdfName.TYPE, PdfName.XREF);
                PdfArray pdfArray = new PdfArray();
                for (int i = 0; i < arrayList.size(); ++i) {
                    pdfArray.add(new PdfNumber((Integer)arrayList.get(i)));
                }
                object.put(PdfName.INDEX, pdfArray);
                if (n > 0) {
                    object.put(PdfName.PREV, new PdfNumber(n));
                }
                PdfEncryption pdfEncryption = this.writer.crypto;
                this.writer.crypto = null;
                PdfIndirectObject pdfIndirectObject = new PdfIndirectObject(n2, (PdfObject)object, this.writer);
                pdfIndirectObject.writeTo(this.writer.getOs());
                this.writer.crypto = pdfEncryption;
            } else {
                outputStream.write(DocWriter.getISOBytes("xref\n"));
                iterator = this.xrefs.iterator();
                for (int i = 0; i < arrayList.size(); i += 2) {
                    n3 = (Integer)arrayList.get(i);
                    n4 = (Integer)arrayList.get(i + 1);
                    outputStream.write(DocWriter.getISOBytes(String.valueOf(n3)));
                    outputStream.write(DocWriter.getISOBytes(" "));
                    outputStream.write(DocWriter.getISOBytes(String.valueOf(n4)));
                    outputStream.write(10);
                    while (n4-- > 0) {
                        pdfCrossReference = (PdfCrossReference)iterator.next();
                        pdfCrossReference.toPdf(outputStream);
                    }
                }
            }
        }

        static class PdfCrossReference
        implements Comparable {
            private int type;
            private int offset;
            private int refnum;
            private int generation;

            PdfCrossReference(int n, int n2, int n3) {
                this.type = 0;
                this.offset = n2;
                this.refnum = n;
                this.generation = n3;
            }

            PdfCrossReference(int n, int n2) {
                this.type = 1;
                this.offset = n2;
                this.refnum = n;
                this.generation = 0;
            }

            PdfCrossReference(int n, int n2, int n3, int n4) {
                this.type = n;
                this.offset = n3;
                this.refnum = n2;
                this.generation = n4;
            }

            int getRefnum() {
                return this.refnum;
            }

            public void toPdf(OutputStream outputStream) throws IOException {
                StringBuffer stringBuffer = new StringBuffer("0000000000").append(this.offset);
                stringBuffer.delete(0, stringBuffer.length() - 10);
                StringBuffer stringBuffer2 = new StringBuffer("00000").append(this.generation);
                stringBuffer2.delete(0, stringBuffer2.length() - 5);
                stringBuffer.append(' ').append(stringBuffer2).append(this.generation == 65535 ? " f \n" : " n \n");
                outputStream.write(DocWriter.getISOBytes(stringBuffer.toString()));
            }

            public void toPdf(int n, OutputStream outputStream) throws IOException {
                outputStream.write((byte)this.type);
                while (--n >= 0) {
                    outputStream.write((byte)(this.offset >>> 8 * n & 255));
                }
                outputStream.write((byte)(this.generation >>> 8 & 255));
                outputStream.write((byte)(this.generation & 255));
            }

            public int compareTo(Object object) {
                PdfCrossReference pdfCrossReference = (PdfCrossReference)object;
                return this.refnum < pdfCrossReference.refnum ? -1 : (this.refnum == pdfCrossReference.refnum ? 0 : 1);
            }

            public boolean equals(Object object) {
                if (object instanceof PdfCrossReference) {
                    PdfCrossReference pdfCrossReference = (PdfCrossReference)object;
                    return this.refnum == pdfCrossReference.refnum;
                }
                return false;
            }

            public int hashCode() {
                return this.refnum;
            }
        }

    }

}

