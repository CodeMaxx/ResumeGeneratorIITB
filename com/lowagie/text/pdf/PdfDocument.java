/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Anchor;
import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.MarkedObject;
import com.lowagie.text.MarkedSection;
import com.lowagie.text.Meta;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.MultiColumnText;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfAcroForm;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfChunk;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfContents;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfEncryption;
import com.lowagie.text.pdf.PdfException;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfFont;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLine;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNameTree;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPage;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfTextArray;
import com.lowagie.text.pdf.PdfTransition;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfXConformanceException;
import com.lowagie.text.pdf.collection.PdfCollection;
import com.lowagie.text.pdf.draw.DrawInterface;
import com.lowagie.text.pdf.internal.PdfAnnotationsImp;
import com.lowagie.text.pdf.internal.PdfVersionImp;
import com.lowagie.text.pdf.internal.PdfViewerPreferencesImp;
import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PdfDocument
extends Document {
    protected PdfWriter writer;
    protected PdfContentByte text;
    protected PdfContentByte graphics;
    protected float leading = 0.0f;
    protected int alignment = 0;
    protected float currentHeight = 0.0f;
    protected boolean isSectionTitle = false;
    protected int leadingCount = 0;
    protected PdfAction anchorAction = null;
    protected int textEmptySize;
    protected byte[] xmpMetadata = null;
    protected float nextMarginLeft;
    protected float nextMarginRight;
    protected float nextMarginTop;
    protected float nextMarginBottom;
    protected boolean firstPageEvent = true;
    protected PdfLine line = null;
    protected ArrayList lines = new ArrayList();
    protected int lastElementType = -1;
    static final String hangingPunctuation = ".,;:'";
    protected Indentation indentation = new Indentation();
    protected PdfInfo info = new PdfInfo();
    protected PdfOutline rootOutline;
    protected PdfOutline currentOutline;
    protected PdfViewerPreferencesImp viewerPreferences = new PdfViewerPreferencesImp();
    protected PdfPageLabels pageLabels;
    protected TreeMap localDestinations = new TreeMap();
    int jsCounter;
    protected HashMap documentLevelJS = new HashMap();
    protected static final DecimalFormat SIXTEEN_DIGITS = new DecimalFormat("0000000000000000");
    protected HashMap documentFileAttachment = new HashMap();
    protected String openActionName;
    protected PdfAction openActionAction;
    protected PdfDictionary additionalActions;
    protected PdfCollection collection;
    PdfAnnotationsImp annotationsImp;
    protected int markPoint;
    protected Rectangle nextPageSize = null;
    protected HashMap thisBoxSize = new HashMap();
    protected HashMap boxSize = new HashMap();
    protected boolean pageEmpty = true;
    protected int duration = -1;
    protected PdfTransition transition = null;
    protected PdfDictionary pageAA = null;
    protected PdfIndirectReference thumb;
    protected PageResources pageResources;
    protected boolean strictImageSequence = false;
    protected float imageEnd = -1.0f;
    protected Image imageWait = null;

    public PdfDocument() {
        this.addProducer();
        this.addCreationDate();
    }

    public void addWriter(PdfWriter pdfWriter) throws DocumentException {
        if (this.writer == null) {
            this.writer = pdfWriter;
            this.annotationsImp = new PdfAnnotationsImp(pdfWriter);
            return;
        }
        throw new DocumentException("You can only add a writer to a PdfDocument once.");
    }

    public float getLeading() {
        return this.leading;
    }

    public boolean add(Element element) throws DocumentException {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        try {
            switch (element.type()) {
                case 0: {
                    this.info.addkey(((Meta)element).getName(), ((Meta)element).getContent());
                    break;
                }
                case 1: {
                    this.info.addTitle(((Meta)element).getContent());
                    break;
                }
                case 2: {
                    this.info.addSubject(((Meta)element).getContent());
                    break;
                }
                case 3: {
                    this.info.addKeywords(((Meta)element).getContent());
                    break;
                }
                case 4: {
                    this.info.addAuthor(((Meta)element).getContent());
                    break;
                }
                case 7: {
                    this.info.addCreator(((Meta)element).getContent());
                    break;
                }
                case 5: {
                    this.info.addProducer();
                    break;
                }
                case 6: {
                    this.info.addCreationDate();
                    break;
                }
                case 10: {
                    PdfChunk pdfChunk;
                    if (this.line == null) {
                        this.carriageReturn();
                    }
                    PdfChunk pdfChunk2 = new PdfChunk((Chunk)element, this.anchorAction);
                    while ((pdfChunk = this.line.add(pdfChunk2)) != null) {
                        this.carriageReturn();
                        pdfChunk2 = pdfChunk;
                        pdfChunk2.trimFirstSpace();
                    }
                    this.pageEmpty = false;
                    if (!pdfChunk2.isAttribute("NEWPAGE")) break;
                    this.newPage();
                    break;
                }
                case 17: {
                    ++this.leadingCount;
                    Anchor anchor = (Anchor)element;
                    String string = anchor.getReference();
                    this.leading = anchor.getLeading();
                    if (string != null) {
                        this.anchorAction = new PdfAction(string);
                    }
                    element.process(this);
                    this.anchorAction = null;
                    --this.leadingCount;
                    break;
                }
                case 29: {
                    if (this.line == null) {
                        this.carriageReturn();
                    }
                    Annotation annotation = (Annotation)element;
                    Rectangle rectangle = new Rectangle(0.0f, 0.0f);
                    if (this.line != null) {
                        rectangle = new Rectangle(annotation.llx(this.indentRight() - this.line.widthLeft()), annotation.lly(this.indentTop() - this.currentHeight), annotation.urx(this.indentRight() - this.line.widthLeft() + 20.0f), annotation.ury(this.indentTop() - this.currentHeight - 20.0f));
                    }
                    PdfAnnotation pdfAnnotation = PdfAnnotationsImp.convertAnnotation(this.writer, annotation, rectangle);
                    this.annotationsImp.addPlainAnnotation(pdfAnnotation);
                    this.pageEmpty = false;
                    break;
                }
                case 11: {
                    ++this.leadingCount;
                    this.leading = ((Phrase)element).getLeading();
                    element.process(this);
                    --this.leadingCount;
                    break;
                }
                case 12: {
                    ++this.leadingCount;
                    Paragraph paragraph = (Paragraph)element;
                    this.addSpacing(paragraph.spacingBefore(), this.leading, paragraph.getFont());
                    this.alignment = paragraph.getAlignment();
                    this.leading = paragraph.getTotalLeading();
                    this.carriageReturn();
                    if (this.currentHeight + this.line.height() + this.leading > this.indentTop() - this.indentBottom()) {
                        this.newPage();
                    }
                    this.indentation.indentLeft += paragraph.getIndentationLeft();
                    this.indentation.indentRight += paragraph.getIndentationRight();
                    this.carriageReturn();
                    PdfPageEvent pdfPageEvent = this.writer.getPageEvent();
                    if (pdfPageEvent != null && !this.isSectionTitle) {
                        pdfPageEvent.onParagraph(this.writer, this, this.indentTop() - this.currentHeight);
                    }
                    if (paragraph.getKeepTogether()) {
                        this.carriageReturn();
                        PdfPTable pdfPTable = new PdfPTable(1);
                        pdfPTable.setWidthPercentage(100.0f);
                        PdfPCell pdfPCell = new PdfPCell();
                        pdfPCell.addElement(paragraph);
                        pdfPCell.setBorder(0);
                        pdfPCell.setPadding(0.0f);
                        pdfPTable.addCell(pdfPCell);
                        this.indentation.indentLeft -= paragraph.getIndentationLeft();
                        this.indentation.indentRight -= paragraph.getIndentationRight();
                        this.add(pdfPTable);
                        this.indentation.indentLeft += paragraph.getIndentationLeft();
                        this.indentation.indentRight += paragraph.getIndentationRight();
                    } else {
                        this.line.setExtraIndent(paragraph.getFirstLineIndent());
                        element.process(this);
                        this.carriageReturn();
                        this.addSpacing(paragraph.spacingAfter(), paragraph.getTotalLeading(), paragraph.getFont());
                    }
                    if (pdfPageEvent != null && !this.isSectionTitle) {
                        pdfPageEvent.onParagraphEnd(this.writer, this, this.indentTop() - this.currentHeight);
                    }
                    this.alignment = 0;
                    this.indentation.indentLeft -= paragraph.getIndentationLeft();
                    this.indentation.indentRight -= paragraph.getIndentationRight();
                    this.carriageReturn();
                    --this.leadingCount;
                    break;
                }
                case 13: 
                case 16: {
                    boolean bl;
                    Section section = (Section)element;
                    PdfPageEvent pdfPageEvent = this.writer.getPageEvent();
                    boolean bl2 = bl = section.isNotAddedYet() && section.getTitle() != null;
                    if (section.isTriggerNewPage()) {
                        this.newPage();
                    }
                    if (bl) {
                        PdfOutline pdfOutline;
                        float f = this.indentTop() - this.currentHeight;
                        int n = this.pageSize.getRotation();
                        if (n == 90 || n == 180) {
                            f = this.pageSize.getHeight() - f;
                        }
                        PdfDestination pdfDestination = new PdfDestination(2, f);
                        while (this.currentOutline.level() >= section.getDepth()) {
                            this.currentOutline = this.currentOutline.parent();
                        }
                        this.currentOutline = pdfOutline = new PdfOutline(this.currentOutline, pdfDestination, section.getBookmarkTitle(), section.isBookmarkOpen());
                    }
                    this.carriageReturn();
                    this.indentation.sectionIndentLeft += section.getIndentationLeft();
                    this.indentation.sectionIndentRight += section.getIndentationRight();
                    if (section.isNotAddedYet() && pdfPageEvent != null) {
                        if (element.type() == 16) {
                            pdfPageEvent.onChapter(this.writer, this, this.indentTop() - this.currentHeight, section.getTitle());
                        } else {
                            pdfPageEvent.onSection(this.writer, this, this.indentTop() - this.currentHeight, section.getDepth(), section.getTitle());
                        }
                    }
                    if (bl) {
                        this.isSectionTitle = true;
                        this.add(section.getTitle());
                        this.isSectionTitle = false;
                    }
                    this.indentation.sectionIndentLeft += section.getIndentation();
                    element.process(this);
                    this.flushLines();
                    this.indentation.sectionIndentLeft -= section.getIndentationLeft() + section.getIndentation();
                    this.indentation.sectionIndentRight -= section.getIndentationRight();
                    if (!section.isComplete() || pdfPageEvent == null) break;
                    if (element.type() == 16) {
                        pdfPageEvent.onChapterEnd(this.writer, this, this.indentTop() - this.currentHeight);
                        break;
                    }
                    pdfPageEvent.onSectionEnd(this.writer, this, this.indentTop() - this.currentHeight);
                    break;
                }
                case 14: {
                    List list = (List)element;
                    if (list.isAlignindent()) {
                        list.normalizeIndentation();
                    }
                    this.indentation.listIndentLeft += list.getIndentationLeft();
                    this.indentation.indentRight += list.getIndentationRight();
                    element.process(this);
                    this.indentation.listIndentLeft -= list.getIndentationLeft();
                    this.indentation.indentRight -= list.getIndentationRight();
                    this.carriageReturn();
                    break;
                }
                case 15: {
                    ++this.leadingCount;
                    ListItem listItem = (ListItem)element;
                    this.addSpacing(listItem.spacingBefore(), this.leading, listItem.getFont());
                    this.alignment = listItem.getAlignment();
                    this.indentation.listIndentLeft += listItem.getIndentationLeft();
                    this.indentation.indentRight += listItem.getIndentationRight();
                    this.leading = listItem.getTotalLeading();
                    this.carriageReturn();
                    this.line.setListItem(listItem);
                    element.process(this);
                    this.addSpacing(listItem.spacingAfter(), listItem.getTotalLeading(), listItem.getFont());
                    if (this.line.hasToBeJustified()) {
                        this.line.resetAlignment();
                    }
                    this.carriageReturn();
                    this.indentation.listIndentLeft -= listItem.getIndentationLeft();
                    this.indentation.indentRight -= listItem.getIndentationRight();
                    --this.leadingCount;
                    break;
                }
                case 30: {
                    Rectangle rectangle = (Rectangle)element;
                    this.graphics.rectangle(rectangle);
                    this.pageEmpty = false;
                    break;
                }
                case 23: {
                    PdfPTable pdfPTable = (PdfPTable)element;
                    if (pdfPTable.size() <= pdfPTable.getHeaderRows()) break;
                    this.ensureNewLine();
                    this.flushLines();
                    this.addPTable(pdfPTable);
                    this.pageEmpty = false;
                    this.newLine();
                    break;
                }
                case 40: {
                    this.ensureNewLine();
                    this.flushLines();
                    MultiColumnText multiColumnText = (MultiColumnText)element;
                    float f = multiColumnText.write(this.writer.getDirectContent(), this, this.indentTop() - this.currentHeight);
                    this.currentHeight += f;
                    this.text.moveText(0.0f, -1.0f * f);
                    this.pageEmpty = false;
                    break;
                }
                case 22: {
                    if (element instanceof SimpleTable) {
                        PdfPTable pdfPTable = ((SimpleTable)element).createPdfPTable();
                        if (pdfPTable.size() <= pdfPTable.getHeaderRows()) break;
                        this.ensureNewLine();
                        this.flushLines();
                        this.addPTable(pdfPTable);
                        this.pageEmpty = false;
                        break;
                    }
                    if (element instanceof Table) {
                        try {
                            PdfPTable pdfPTable = ((Table)element).createPdfPTable();
                            if (pdfPTable.size() <= pdfPTable.getHeaderRows()) break;
                            this.ensureNewLine();
                            this.flushLines();
                            this.addPTable(pdfPTable);
                            this.pageEmpty = false;
                        }
                        catch (BadElementException var2_14) {
                            float f = ((Table)element).getOffset();
                            if (Float.isNaN(f)) {
                                f = this.leading;
                            }
                            this.carriageReturn();
                            this.lines.add(new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, f));
                            this.currentHeight += f;
                            this.addPdfTable((Table)element);
                        }
                        break;
                    }
                    return false;
                }
                case 32: 
                case 33: 
                case 34: 
                case 35: {
                    this.add((Image)element);
                    break;
                }
                case 55: {
                    DrawInterface drawInterface = (DrawInterface)((Object)element);
                    drawInterface.draw(this.graphics, this.indentLeft(), this.indentBottom(), this.indentRight(), this.indentTop(), this.indentTop() - this.currentHeight - (this.leadingCount > 0 ? this.leading : 0.0f));
                    this.pageEmpty = false;
                    break;
                }
                case 50: {
                    MarkedObject markedObject;
                    if (element instanceof MarkedSection && (markedObject = ((MarkedSection)element).getTitle()) != null) {
                        markedObject.process(this);
                    }
                    markedObject = (MarkedObject)element;
                    markedObject.process(this);
                    break;
                }
                default: {
                    return false;
                }
            }
            this.lastElementType = element.type();
            return true;
        }
        catch (Exception var2_17) {
            throw new DocumentException(var2_17);
        }
    }

    public void open() {
        if (!this.open) {
            super.open();
            this.writer.open();
            this.currentOutline = this.rootOutline = new PdfOutline(this.writer);
        }
        try {
            this.initPage();
        }
        catch (DocumentException var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    public void close() {
        if (this.close) {
            return;
        }
        try {
            boolean bl = this.imageWait != null;
            this.newPage();
            if (this.imageWait != null || bl) {
                this.newPage();
            }
            if (this.annotationsImp.hasUnusedAnnotations()) {
                throw new RuntimeException("Not all annotations could be added to the document (the document doesn't have enough pages).");
            }
            PdfPageEvent pdfPageEvent = this.writer.getPageEvent();
            if (pdfPageEvent != null) {
                pdfPageEvent.onCloseDocument(this.writer, this);
            }
            super.close();
            this.writer.addLocalDestinations(this.localDestinations);
            this.calculateOutlineCount();
            this.writeOutlines();
        }
        catch (Exception var1_2) {
            throw new ExceptionConverter(var1_2);
        }
        this.writer.close();
    }

    public void setXmpMetadata(byte[] arrby) {
        this.xmpMetadata = arrby;
    }

    public boolean newPage() {
        this.lastElementType = -1;
        if (this.writer == null || this.writer.getDirectContent().size() == 0 && this.writer.getDirectContentUnder().size() == 0 && (this.pageEmpty || this.writer.isPaused())) {
            this.setNewPageSizeAndMargins();
            return false;
        }
        if (!this.open || this.close) {
            throw new RuntimeException("The document isn't open.");
        }
        PdfPageEvent pdfPageEvent = this.writer.getPageEvent();
        if (pdfPageEvent != null) {
            pdfPageEvent.onEndPage(this.writer, this);
        }
        super.newPage();
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.imageIndentRight = 0.0f;
        try {
            PdfDictionary pdfDictionary;
            PdfDictionary pdfDictionary2;
            this.flushLines();
            int n = this.pageSize.getRotation();
            if (this.writer.isPdfX()) {
                if (this.thisBoxSize.containsKey("art") && this.thisBoxSize.containsKey("trim")) {
                    throw new PdfXConformanceException("Only one of ArtBox or TrimBox can exist in the page.");
                }
                if (!this.thisBoxSize.containsKey("art") && !this.thisBoxSize.containsKey("trim")) {
                    if (this.thisBoxSize.containsKey("crop")) {
                        this.thisBoxSize.put("trim", this.thisBoxSize.get("crop"));
                    } else {
                        this.thisBoxSize.put("trim", new PdfRectangle(this.pageSize, this.pageSize.getRotation()));
                    }
                }
            }
            this.pageResources.addDefaultColorDiff(this.writer.getDefaultColorspace());
            if (this.writer.isRgbTransparencyBlending()) {
                pdfDictionary2 = new PdfDictionary();
                pdfDictionary2.put(PdfName.CS, PdfName.DEVICERGB);
                this.pageResources.addDefaultColorDiff(pdfDictionary2);
            }
            pdfDictionary2 = this.pageResources.getResources();
            PdfPage pdfPage = new PdfPage(new PdfRectangle(this.pageSize, n), this.thisBoxSize, pdfDictionary2, n);
            if (this.xmpMetadata != null) {
                pdfDictionary = new PdfStream(this.xmpMetadata);
                pdfDictionary.put(PdfName.TYPE, PdfName.METADATA);
                pdfDictionary.put(PdfName.SUBTYPE, PdfName.XML);
                PdfEncryption pdfEncryption = this.writer.getEncryption();
                if (pdfEncryption != null && !pdfEncryption.isMetadataEncrypted()) {
                    PdfArray pdfArray = new PdfArray();
                    pdfArray.add(PdfName.CRYPT);
                    pdfDictionary.put(PdfName.FILTER, pdfArray);
                }
                pdfPage.put(PdfName.METADATA, this.writer.addToBody(pdfDictionary).getIndirectReference());
            }
            if (this.transition != null) {
                pdfPage.put(PdfName.TRANS, this.transition.getTransitionDictionary());
                this.transition = null;
            }
            if (this.duration > 0) {
                pdfPage.put(PdfName.DUR, new PdfNumber(this.duration));
                this.duration = 0;
            }
            if (this.pageAA != null) {
                pdfPage.put(PdfName.AA, this.writer.addToBody(this.pageAA).getIndirectReference());
                this.pageAA = null;
            }
            if (this.thumb != null) {
                pdfPage.put(PdfName.THUMB, this.thumb);
                this.thumb = null;
            }
            if (this.writer.getUserunit() > 0.0f) {
                pdfPage.put(PdfName.USERUNIT, new PdfNumber(this.writer.getUserunit()));
            }
            if (this.annotationsImp.hasUnusedAnnotations() && (pdfDictionary = this.annotationsImp.rotateAnnotations(this.writer, this.pageSize)).size() != 0) {
                pdfPage.put(PdfName.ANNOTS, pdfDictionary);
            }
            if (this.writer.isTagged()) {
                pdfPage.put(PdfName.STRUCTPARENTS, new PdfNumber(this.writer.getCurrentPageNumber() - 1));
            }
            if (this.text.size() > this.textEmptySize) {
                this.text.endText();
            } else {
                this.text = null;
            }
            this.writer.add(pdfPage, new PdfContents(this.writer.getDirectContentUnder(), this.graphics, this.text, this.writer.getDirectContent(), this.pageSize));
            this.initPage();
        }
        catch (DocumentException var2_3) {
            throw new ExceptionConverter(var2_3);
        }
        catch (IOException var2_4) {
            throw new ExceptionConverter(var2_4);
        }
        return true;
    }

    public boolean setPageSize(Rectangle rectangle) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        this.nextPageSize = new Rectangle(rectangle);
        return true;
    }

    public boolean setMargins(float f, float f2, float f3, float f4) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        this.nextMarginLeft = f;
        this.nextMarginRight = f2;
        this.nextMarginTop = f3;
        this.nextMarginBottom = f4;
        return true;
    }

    public boolean setMarginMirroring(boolean bl) {
        if (this.writer != null && this.writer.isPaused()) {
            return false;
        }
        return super.setMarginMirroring(bl);
    }

    public void setPageCount(int n) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setPageCount(n);
    }

    public void resetPageCount() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetPageCount();
    }

    public void setHeader(HeaderFooter headerFooter) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setHeader(headerFooter);
    }

    public void resetHeader() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetHeader();
    }

    public void setFooter(HeaderFooter headerFooter) {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.setFooter(headerFooter);
    }

    public void resetFooter() {
        if (this.writer != null && this.writer.isPaused()) {
            return;
        }
        super.resetFooter();
    }

    protected void initPage() throws DocumentException {
        ++this.pageN;
        this.annotationsImp.resetAnnotations();
        this.pageResources = new PageResources();
        this.writer.resetContent();
        this.graphics = new PdfContentByte(this.writer);
        this.text = new PdfContentByte(this.writer);
        this.text.reset();
        this.text.beginText();
        this.textEmptySize = this.text.size();
        this.markPoint = 0;
        this.setNewPageSizeAndMargins();
        this.imageEnd = -1.0f;
        this.indentation.imageIndentRight = 0.0f;
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.indentBottom = 0.0f;
        this.indentation.indentTop = 0.0f;
        this.currentHeight = 0.0f;
        this.thisBoxSize = new HashMap(this.boxSize);
        if (this.pageSize.getBackgroundColor() != null || this.pageSize.hasBorders() || this.pageSize.getBorderColor() != null) {
            this.add(this.pageSize);
        }
        float f = this.leading;
        int n = this.alignment;
        this.doFooter();
        this.text.moveText(this.left(), this.top());
        this.doHeader();
        this.pageEmpty = true;
        try {
            if (this.imageWait != null) {
                this.add(this.imageWait);
                this.imageWait = null;
            }
        }
        catch (Exception var3_3) {
            throw new ExceptionConverter(var3_3);
        }
        this.leading = f;
        this.alignment = n;
        this.carriageReturn();
        PdfPageEvent pdfPageEvent = this.writer.getPageEvent();
        if (pdfPageEvent != null) {
            if (this.firstPageEvent) {
                pdfPageEvent.onOpenDocument(this.writer, this);
            }
            pdfPageEvent.onStartPage(this.writer, this);
        }
        this.firstPageEvent = false;
    }

    protected void newLine() throws DocumentException {
        this.lastElementType = -1;
        this.carriageReturn();
        if (this.lines != null && !this.lines.isEmpty()) {
            this.lines.add(this.line);
            this.currentHeight += this.line.height();
        }
        this.line = new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, this.leading);
    }

    protected void carriageReturn() {
        if (this.lines == null) {
            this.lines = new ArrayList();
        }
        if (this.line != null) {
            if (this.currentHeight + this.line.height() + this.leading < this.indentTop() - this.indentBottom()) {
                if (this.line.size() > 0) {
                    this.currentHeight += this.line.height();
                    this.lines.add(this.line);
                    this.pageEmpty = false;
                }
            } else {
                this.newPage();
            }
        }
        if (this.imageEnd > -1.0f && this.currentHeight > this.imageEnd) {
            this.imageEnd = -1.0f;
            this.indentation.imageIndentRight = 0.0f;
            this.indentation.imageIndentLeft = 0.0f;
        }
        this.line = new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, this.leading);
    }

    public float getVerticalPosition(boolean bl) {
        if (bl) {
            this.ensureNewLine();
        }
        return this.top() - this.currentHeight - this.indentation.indentTop;
    }

    protected void ensureNewLine() {
        try {
            if (this.lastElementType == 11 || this.lastElementType == 10) {
                this.newLine();
                this.flushLines();
            }
        }
        catch (DocumentException var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    protected float flushLines() throws DocumentException {
        Float f;
        if (this.lines == null) {
            return 0.0f;
        }
        if (this.line != null && this.line.size() > 0) {
            this.lines.add(this.line);
            this.line = new PdfLine(this.indentLeft(), this.indentRight(), this.alignment, this.leading);
        }
        if (this.lines.isEmpty()) {
            return 0.0f;
        }
        Object[] arrobject = new Object[2];
        PdfFont pdfFont = null;
        float f2 = 0.0f;
        arrobject[1] = f = new Float(0.0f);
        Iterator iterator = this.lines.iterator();
        while (iterator.hasNext()) {
            PdfLine pdfLine = (PdfLine)iterator.next();
            float f3 = pdfLine.indentLeft() - this.indentLeft() + this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.sectionIndentLeft;
            this.text.moveText(f3, - pdfLine.height());
            if (pdfLine.listSymbol() != null) {
                ColumnText.showTextAligned(this.graphics, 0, new Phrase(pdfLine.listSymbol()), this.text.getXTLM() - pdfLine.listIndent(), this.text.getYTLM(), 0.0f);
            }
            arrobject[0] = pdfFont;
            this.writeLineToContent(pdfLine, this.text, this.graphics, arrobject, this.writer.getSpaceCharRatio());
            pdfFont = (PdfFont)arrobject[0];
            f2 += pdfLine.height();
            this.text.moveText(- f3, 0.0f);
        }
        this.lines = new ArrayList();
        return f2;
    }

    void writeLineToContent(PdfLine pdfLine, PdfContentByte pdfContentByte, PdfContentByte pdfContentByte2, Object[] arrobject, float f) throws DocumentException {
        float f2;
        float f3;
        PdfFont pdfFont = (PdfFont)arrobject[0];
        float f4 = ((Float)arrobject[1]).floatValue();
        float f5 = 0.0f;
        float f6 = 1.0f;
        float f7 = Float.NaN;
        float f8 = 0.0f;
        float f9 = 0.0f;
        float f10 = 0.0f;
        int n = pdfLine.numberOfSpaces();
        int n2 = pdfLine.GetLineLengthUtf32();
        boolean bl = pdfLine.hasToBeJustified() && (n != 0 || n2 > 1);
        int n3 = pdfLine.getSeparatorCount();
        if (n3 > 0) {
            f10 = pdfLine.widthLeft() / (float)n3;
        } else if (bl) {
            if (pdfLine.isNewlineSplit() && pdfLine.widthLeft() >= f4 * (f * (float)n + (float)n2 - 1.0f)) {
                if (pdfLine.isRTL()) {
                    pdfContentByte.moveText(pdfLine.widthLeft() - f4 * (f * (float)n + (float)n2 - 1.0f), 0.0f);
                }
                f8 = f * f4;
                f9 = f4;
            } else {
                String string;
                char c;
                float f11 = pdfLine.widthLeft();
                PdfChunk pdfChunk = pdfLine.getChunk(pdfLine.size() - 1);
                if (pdfChunk != null && (string = pdfChunk.toString()).length() > 0 && ".,;:'".indexOf(c = string.charAt(string.length() - 1)) >= 0) {
                    f3 = f11;
                    f5 = (f11 += pdfChunk.font().width(c) * 0.4f) - f3;
                }
                f2 = f11 / (f * (float)n + (float)n2 - 1.0f);
                f8 = f * f2;
                f9 = f2;
                f4 = f2;
            }
        }
        int n4 = pdfLine.getLastStrokeChunk();
        int n5 = 0;
        float f12 = f2 = pdfContentByte.getXTLM();
        f3 = pdfContentByte.getYTLM();
        boolean bl2 = false;
        float f13 = 0.0f;
        Iterator iterator = pdfLine.iterator();
        while (iterator.hasNext()) {
            float f14;
            PdfTextArray pdfTextArray;
            Object object;
            float f15;
            int n6;
            Object object2;
            PdfChunk pdfChunk = (PdfChunk)iterator.next();
            Color color = pdfChunk.color();
            f6 = 1.0f;
            if (n5 <= n4) {
                f15 = bl ? pdfChunk.getWidthCorrected(f9, f8) : pdfChunk.width();
                if (pdfChunk.isStroked()) {
                    Object object3;
                    Object object4;
                    float f16;
                    float f17;
                    Object[] arrobject2;
                    object = pdfLine.getChunk(n5 + 1);
                    if (pdfChunk.isSeparator()) {
                        f15 = f10;
                        arrobject2 = (Object[])pdfChunk.getAttribute("SEPARATOR");
                        object4 = (Object[][])arrobject2[0];
                        object2 = (Boolean)arrobject2[1];
                        object3 = pdfChunk.font().size();
                        f17 = pdfChunk.font().getFont().getFontDescriptor(1, (float)object3);
                        f16 = pdfChunk.font().getFont().getFontDescriptor(3, (float)object3);
                        if (object2.booleanValue()) {
                            object4.draw(pdfContentByte2, f12, f3 + f16, f12 + pdfLine.getOriginalWidth(), f17 - f16, f3);
                        } else {
                            object4.draw(pdfContentByte2, f2, f3 + f16, f2 + f15, f17 - f16, f3);
                        }
                    }
                    if (pdfChunk.isTab()) {
                        arrobject2 = (Object[])pdfChunk.getAttribute("TAB");
                        object4 = (DrawInterface)arrobject2[0];
                        f13 = ((Float)arrobject2[1]).floatValue() + ((Float)arrobject2[3]).floatValue();
                        float f18 = pdfChunk.font().size();
                        object3 = pdfChunk.font().getFont().getFontDescriptor(1, f18);
                        f17 = pdfChunk.font().getFont().getFontDescriptor(3, f18);
                        if (f13 > f2) {
                            object4.draw(pdfContentByte2, f2, f3 + f17, f13, object3 - f17, f3);
                        }
                        f16 = f2;
                        f2 = f13;
                        f13 = f16;
                    }
                    if (pdfChunk.isAttribute("BACKGROUND")) {
                        float f19 = f4;
                        if (object != null && object.isAttribute("BACKGROUND")) {
                            f19 = 0.0f;
                        }
                        if (object == null) {
                            f19 += f5;
                        }
                        float f20 = pdfChunk.font().size();
                        float f21 = pdfChunk.font().getFont().getFontDescriptor(1, f20);
                        object3 = pdfChunk.font().getFont().getFontDescriptor(3, f20);
                        Object[] arrobject3 = (Object[])pdfChunk.getAttribute("BACKGROUND");
                        pdfContentByte2.setColorFill((Color)arrobject3[0]);
                        float[] arrf = (float[])arrobject3[1];
                        pdfContentByte2.rectangle(f2 - arrf[0], f3 + object3 - arrf[1] + pdfChunk.getTextRise(), f15 - f19 + arrf[0] + arrf[2], f21 - object3 + arrf[1] + arrf[3]);
                        pdfContentByte2.fill();
                        pdfContentByte2.setGrayFill(0.0f);
                    }
                    if (pdfChunk.isAttribute("UNDERLINE")) {
                        float f22 = f4;
                        if (object != null && object.isAttribute("UNDERLINE")) {
                            f22 = 0.0f;
                        }
                        if (object == null) {
                            f22 += f5;
                        }
                        object4 = (Object[][])pdfChunk.getAttribute("UNDERLINE");
                        object2 = null;
                        for (int i = 0; i < object4.length; ++i) {
                            Object[] arrobject4 = object4[i];
                            object2 = (Color)arrobject4[0];
                            float[] arrf = (float[])arrobject4[1];
                            if (object2 == null) {
                                object2 = color;
                            }
                            if (object2 != null) {
                                pdfContentByte2.setColorStroke((Color)object2);
                            }
                            f14 = pdfChunk.font().size();
                            pdfContentByte2.setLineWidth(arrf[0] + f14 * arrf[1]);
                            pdfTextArray = (PdfTextArray)(arrf[2] + f14 * arrf[3]);
                            n6 = (int)arrf[4];
                            if (n6 != 0) {
                                pdfContentByte2.setLineCap(n6);
                            }
                            pdfContentByte2.moveTo(f2, f3 + pdfTextArray);
                            pdfContentByte2.lineTo(f2 + f15 - f22, f3 + pdfTextArray);
                            pdfContentByte2.stroke();
                            if (object2 != null) {
                                pdfContentByte2.resetGrayStroke();
                            }
                            if (n6 == 0) continue;
                            pdfContentByte2.setLineCap(0);
                        }
                        pdfContentByte2.setLineWidth(1.0f);
                    }
                    if (pdfChunk.isAttribute("ACTION")) {
                        float f23 = f4;
                        if (object != null && object.isAttribute("ACTION")) {
                            f23 = 0.0f;
                        }
                        if (object == null) {
                            f23 += f5;
                        }
                        pdfContentByte.addAnnotation(new PdfAnnotation(this.writer, f2, f3, f2 + f15 - f23, f3 + pdfChunk.font().size(), (PdfAction)pdfChunk.getAttribute("ACTION")));
                    }
                    if (pdfChunk.isAttribute("REMOTEGOTO")) {
                        float f24 = f4;
                        if (object != null && object.isAttribute("REMOTEGOTO")) {
                            f24 = 0.0f;
                        }
                        if (object == null) {
                            f24 += f5;
                        }
                        object4 = (Object[])pdfChunk.getAttribute("REMOTEGOTO");
                        object2 = (String)object4[0];
                        if (object4[1] instanceof String) {
                            this.remoteGoto((String)object2, (String)object4[1], f2, f3, f2 + f15 - f24, f3 + pdfChunk.font().size());
                        } else {
                            this.remoteGoto((String)object2, (Integer)object4[1], f2, f3, f2 + f15 - f24, f3 + pdfChunk.font().size());
                        }
                    }
                    if (pdfChunk.isAttribute("LOCALGOTO")) {
                        float f25 = f4;
                        if (object != null && object.isAttribute("LOCALGOTO")) {
                            f25 = 0.0f;
                        }
                        if (object == null) {
                            f25 += f5;
                        }
                        this.localGoto((String)pdfChunk.getAttribute("LOCALGOTO"), f2, f3, f2 + f15 - f25, f3 + pdfChunk.font().size());
                    }
                    if (pdfChunk.isAttribute("LOCALDESTINATION")) {
                        float f26 = f4;
                        if (object != null && object.isAttribute("LOCALDESTINATION")) {
                            f26 = 0.0f;
                        }
                        if (object == null) {
                            f26 += f5;
                        }
                        this.localDestination((String)pdfChunk.getAttribute("LOCALDESTINATION"), new PdfDestination(0, f2, f3 + pdfChunk.font().size(), 0.0f));
                    }
                    if (pdfChunk.isAttribute("GENERICTAG")) {
                        float f27 = f4;
                        if (object != null && object.isAttribute("GENERICTAG")) {
                            f27 = 0.0f;
                        }
                        if (object == null) {
                            f27 += f5;
                        }
                        object4 = new Rectangle(f2, f3, f2 + f15 - f27, f3 + pdfChunk.font().size());
                        object2 = this.writer.getPageEvent();
                        if (object2 != null) {
                            object2.onGenericTag(this.writer, this, (Rectangle)object4, (String)pdfChunk.getAttribute("GENERICTAG"));
                        }
                    }
                    if (pdfChunk.isAttribute("PDFANNOTATION")) {
                        float f28 = f4;
                        if (object != null && object.isAttribute("PDFANNOTATION")) {
                            f28 = 0.0f;
                        }
                        if (object == null) {
                            f28 += f5;
                        }
                        float f29 = pdfChunk.font().size();
                        float f30 = pdfChunk.font().getFont().getFontDescriptor(1, f29);
                        object3 = pdfChunk.font().getFont().getFontDescriptor(3, f29);
                        PdfAnnotation pdfAnnotation = PdfFormField.shallowDuplicate((PdfAnnotation)pdfChunk.getAttribute("PDFANNOTATION"));
                        pdfAnnotation.put(PdfName.RECT, new PdfRectangle(f2, f3 + object3, f2 + f15 - f28, f3 + f30));
                        pdfContentByte.addAnnotation(pdfAnnotation);
                    }
                    arrobject2 = (float[])pdfChunk.getAttribute("SKEW");
                    object4 = (Float)pdfChunk.getAttribute("HSCALE");
                    if (arrobject2 != null || object4 != null) {
                        Object object5 = 0.0f;
                        object3 = 0.0f;
                        if (arrobject2 != null) {
                            object5 = arrobject2[0];
                            object3 = arrobject2[1];
                        }
                        if (object4 != null) {
                            f6 = object4.floatValue();
                        }
                        pdfContentByte.setTextMatrix(f6, (float)object5, (float)object3, 1.0f, f2, f3);
                    }
                    if (pdfChunk.isImage()) {
                        object2 = pdfChunk.getImage();
                        float[] arrf = object2.matrix();
                        arrf[4] = f2 + pdfChunk.getImageOffsetX() - arrf[4];
                        arrf[5] = f3 + pdfChunk.getImageOffsetY() - arrf[5];
                        pdfContentByte2.addImage((Image)object2, arrf[0], arrf[1], arrf[2], arrf[3], arrf[4], arrf[5]);
                        pdfContentByte.moveText(f2 + f4 + object2.getScaledWidth() - pdfContentByte.getXTLM(), 0.0f);
                    }
                }
                f2 += f15;
                ++n5;
            }
            if (pdfChunk.font().compareTo(pdfFont) != 0) {
                pdfFont = pdfChunk.font();
                pdfContentByte.setFontAndSize(pdfFont.getFont(), pdfFont.size());
            }
            f15 = 0.0f;
            object = (Object[])pdfChunk.getAttribute("TEXTRENDERMODE");
            int n7 = 0;
            float f31 = 1.0f;
            object2 = null;
            Float f32 = (Float)pdfChunk.getAttribute("SUBSUPSCRIPT");
            if (object != null) {
                n7 = (Integer)object[0] & 3;
                if (n7 != 0) {
                    pdfContentByte.setTextRenderingMode(n7);
                }
                if (n7 == 1 || n7 == 2) {
                    f31 = ((Float)object[1]).floatValue();
                    if (f31 != 1.0f) {
                        pdfContentByte.setLineWidth(f31);
                    }
                    if ((object2 = (Color)object[2]) == null) {
                        object2 = color;
                    }
                    if (object2 != null) {
                        pdfContentByte.setColorStroke((Color)object2);
                    }
                }
            }
            if (f32 != null) {
                f15 = f32.floatValue();
            }
            if (color != null) {
                pdfContentByte.setColorFill(color);
            }
            if (f15 != 0.0f) {
                pdfContentByte.setTextRise(f15);
            }
            if (pdfChunk.isImage()) {
                bl2 = true;
            } else if (pdfChunk.isHorizontalSeparator()) {
                PdfTextArray pdfTextArray2 = new PdfTextArray();
                pdfTextArray2.add((- f10) * 1000.0f / pdfChunk.font.size() / f6);
                pdfContentByte.showText(pdfTextArray2);
            } else if (pdfChunk.isTab()) {
                PdfTextArray pdfTextArray3 = new PdfTextArray();
                pdfTextArray3.add((f13 - f2) * 1000.0f / pdfChunk.font.size() / f6);
                pdfContentByte.showText(pdfTextArray3);
            } else if (bl && n > 0 && pdfChunk.isSpecialEncoding()) {
                int n8;
                String string;
                if (f6 != f7) {
                    f7 = f6;
                    pdfContentByte.setWordSpacing(f8 / f6);
                    pdfContentByte.setCharacterSpacing(f9 / f6);
                }
                if ((n8 = (string = pdfChunk.toString()).indexOf(32)) < 0) {
                    pdfContentByte.showText(string);
                } else {
                    f14 = (- f8) * 1000.0f / pdfChunk.font.size() / f6;
                    pdfTextArray = new PdfTextArray(string.substring(0, n8));
                    n6 = n8;
                    while ((n8 = string.indexOf(32, n6 + 1)) >= 0) {
                        pdfTextArray.add(f14);
                        pdfTextArray.add(string.substring(n6, n8));
                        n6 = n8;
                    }
                    pdfTextArray.add(f14);
                    pdfTextArray.add(string.substring(n6));
                    pdfContentByte.showText(pdfTextArray);
                }
            } else {
                if (bl && f6 != f7) {
                    f7 = f6;
                    pdfContentByte.setWordSpacing(f8 / f6);
                    pdfContentByte.setCharacterSpacing(f9 / f6);
                }
                pdfContentByte.showText(pdfChunk.toString());
            }
            if (f15 != 0.0f) {
                pdfContentByte.setTextRise(0.0f);
            }
            if (color != null) {
                pdfContentByte.resetRGBColorFill();
            }
            if (n7 != 0) {
                pdfContentByte.setTextRenderingMode(0);
            }
            if (object2 != null) {
                pdfContentByte.resetRGBColorStroke();
            }
            if (f31 != 1.0f) {
                pdfContentByte.setLineWidth(1.0f);
            }
            if (!pdfChunk.isAttribute("SKEW") && !pdfChunk.isAttribute("HSCALE")) continue;
            bl2 = true;
            pdfContentByte.setTextMatrix(f2, f3);
        }
        if (bl) {
            pdfContentByte.setWordSpacing(0.0f);
            pdfContentByte.setCharacterSpacing(0.0f);
            if (pdfLine.isNewlineSplit()) {
                f4 = 0.0f;
            }
        }
        if (bl2) {
            pdfContentByte.moveText(f12 - pdfContentByte.getXTLM(), 0.0f);
        }
        arrobject[0] = pdfFont;
        arrobject[1] = new Float(f4);
    }

    protected float indentLeft() {
        return this.left(this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.imageIndentLeft + this.indentation.sectionIndentLeft);
    }

    protected float indentRight() {
        return this.right(this.indentation.indentRight + this.indentation.sectionIndentRight + this.indentation.imageIndentRight);
    }

    protected float indentTop() {
        return this.top(this.indentation.indentTop);
    }

    float indentBottom() {
        return this.bottom(this.indentation.indentBottom);
    }

    protected void addSpacing(float f, float f2, Font font) {
        if (f == 0.0f) {
            return;
        }
        if (this.pageEmpty) {
            return;
        }
        if (this.currentHeight + this.line.height() + this.leading > this.indentTop() - this.indentBottom()) {
            return;
        }
        this.leading = f;
        this.carriageReturn();
        if (font.isUnderlined() || font.isStrikethru()) {
            font = new Font(font);
            int n = font.getStyle();
            n &= -5;
            font.setStyle(-1);
            font.setStyle(n &= -9);
        }
        Chunk chunk = new Chunk(" ", font);
        chunk.process(this);
        this.carriageReturn();
        this.leading = f2;
    }

    PdfInfo getInfo() {
        return this.info;
    }

    PdfCatalog getCatalog(PdfIndirectReference pdfIndirectReference) {
        PdfCatalog pdfCatalog = new PdfCatalog(pdfIndirectReference, this.writer);
        if (this.rootOutline.getKids().size() > 0) {
            pdfCatalog.put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
            pdfCatalog.put(PdfName.OUTLINES, this.rootOutline.indirectReference());
        }
        this.writer.getPdfVersion().addToCatalog(pdfCatalog);
        this.viewerPreferences.addToCatalog(pdfCatalog);
        if (this.pageLabels != null) {
            pdfCatalog.put(PdfName.PAGELABELS, this.pageLabels.getDictionary(this.writer));
        }
        pdfCatalog.addNames(this.localDestinations, this.getDocumentLevelJS(), this.documentFileAttachment, this.writer);
        if (this.openActionName != null) {
            PdfAction pdfAction = this.getLocalGotoAction(this.openActionName);
            pdfCatalog.setOpenAction(pdfAction);
        } else if (this.openActionAction != null) {
            pdfCatalog.setOpenAction(this.openActionAction);
        }
        if (this.additionalActions != null) {
            pdfCatalog.setAdditionalActions(this.additionalActions);
        }
        if (this.collection != null) {
            pdfCatalog.put(PdfName.COLLECTION, this.collection);
        }
        if (this.annotationsImp.hasValidAcroForm()) {
            try {
                pdfCatalog.put(PdfName.ACROFORM, this.writer.addToBody(this.annotationsImp.getAcroForm()).getIndirectReference());
            }
            catch (IOException var3_4) {
                throw new ExceptionConverter(var3_4);
            }
        }
        return pdfCatalog;
    }

    void addOutline(PdfOutline pdfOutline, String string) {
        this.localDestination(string, pdfOutline.getPdfDestination());
    }

    public PdfOutline getRootOutline() {
        return this.rootOutline;
    }

    void calculateOutlineCount() {
        if (this.rootOutline.getKids().size() == 0) {
            return;
        }
        this.traverseOutlineCount(this.rootOutline);
    }

    void traverseOutlineCount(PdfOutline pdfOutline) {
        ArrayList arrayList = pdfOutline.getKids();
        PdfOutline pdfOutline2 = pdfOutline.parent();
        if (arrayList.isEmpty()) {
            if (pdfOutline2 != null) {
                pdfOutline2.setCount(pdfOutline2.getCount() + 1);
            }
        } else {
            for (int i = 0; i < arrayList.size(); ++i) {
                this.traverseOutlineCount((PdfOutline)arrayList.get(i));
            }
            if (pdfOutline2 != null) {
                if (pdfOutline.isOpen()) {
                    pdfOutline2.setCount(pdfOutline.getCount() + pdfOutline2.getCount() + 1);
                } else {
                    pdfOutline2.setCount(pdfOutline2.getCount() + 1);
                    pdfOutline.setCount(- pdfOutline.getCount());
                }
            }
        }
    }

    void writeOutlines() throws IOException {
        if (this.rootOutline.getKids().size() == 0) {
            return;
        }
        this.outlineTree(this.rootOutline);
        this.writer.addToBody((PdfObject)this.rootOutline, this.rootOutline.indirectReference());
    }

    void outlineTree(PdfOutline pdfOutline) throws IOException {
        int n;
        pdfOutline.setIndirectReference(this.writer.getPdfIndirectReference());
        if (pdfOutline.parent() != null) {
            pdfOutline.put(PdfName.PARENT, pdfOutline.parent().indirectReference());
        }
        ArrayList arrayList = pdfOutline.getKids();
        int n2 = arrayList.size();
        for (n = 0; n < n2; ++n) {
            this.outlineTree((PdfOutline)arrayList.get(n));
        }
        for (n = 0; n < n2; ++n) {
            if (n > 0) {
                ((PdfOutline)arrayList.get(n)).put(PdfName.PREV, ((PdfOutline)arrayList.get(n - 1)).indirectReference());
            }
            if (n >= n2 - 1) continue;
            ((PdfOutline)arrayList.get(n)).put(PdfName.NEXT, ((PdfOutline)arrayList.get(n + 1)).indirectReference());
        }
        if (n2 > 0) {
            pdfOutline.put(PdfName.FIRST, ((PdfOutline)arrayList.get(0)).indirectReference());
            pdfOutline.put(PdfName.LAST, ((PdfOutline)arrayList.get(n2 - 1)).indirectReference());
        }
        for (n = 0; n < n2; ++n) {
            PdfOutline pdfOutline2 = (PdfOutline)arrayList.get(n);
            this.writer.addToBody((PdfObject)pdfOutline2, pdfOutline2.indirectReference());
        }
    }

    void setViewerPreferences(int n) {
        this.viewerPreferences.setViewerPreferences(n);
    }

    void addViewerPreference(PdfName pdfName, PdfObject pdfObject) {
        this.viewerPreferences.addViewerPreference(pdfName, pdfObject);
    }

    void setPageLabels(PdfPageLabels pdfPageLabels) {
        this.pageLabels = pdfPageLabels;
    }

    void localGoto(String string, float f, float f2, float f3, float f4) {
        PdfAction pdfAction = this.getLocalGotoAction(string);
        this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, f, f2, f3, f4, pdfAction));
    }

    void remoteGoto(String string, String string2, float f, float f2, float f3, float f4) {
        this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, f, f2, f3, f4, new PdfAction(string, string2)));
    }

    void remoteGoto(String string, int n, float f, float f2, float f3, float f4) {
        this.addAnnotation(new PdfAnnotation(this.writer, f, f2, f3, f4, new PdfAction(string, n)));
    }

    void setAction(PdfAction pdfAction, float f, float f2, float f3, float f4) {
        this.addAnnotation(new PdfAnnotation(this.writer, f, f2, f3, f4, pdfAction));
    }

    PdfAction getLocalGotoAction(String string) {
        PdfAction pdfAction;
        Object[] arrobject = (Object[])this.localDestinations.get(string);
        if (arrobject == null) {
            arrobject = new Object[3];
        }
        if (arrobject[0] == null) {
            if (arrobject[1] == null) {
                arrobject[1] = this.writer.getPdfIndirectReference();
            }
            arrobject[0] = pdfAction = new PdfAction((PdfIndirectReference)arrobject[1]);
            this.localDestinations.put(string, arrobject);
        } else {
            pdfAction = (PdfAction)arrobject[0];
        }
        return pdfAction;
    }

    boolean localDestination(String string, PdfDestination pdfDestination) {
        Object[] arrobject = (Object[])this.localDestinations.get(string);
        if (arrobject == null) {
            arrobject = new Object[3];
        }
        if (arrobject[2] != null) {
            return false;
        }
        arrobject[2] = pdfDestination;
        this.localDestinations.put(string, arrobject);
        pdfDestination.addPage(this.writer.getCurrentPage());
        return true;
    }

    void addJavaScript(PdfAction pdfAction) {
        if (pdfAction.get(PdfName.JS) == null) {
            throw new RuntimeException("Only JavaScript actions are allowed.");
        }
        try {
            this.documentLevelJS.put(SIXTEEN_DIGITS.format(this.jsCounter++), this.writer.addToBody(pdfAction).getIndirectReference());
        }
        catch (IOException var2_2) {
            throw new ExceptionConverter(var2_2);
        }
    }

    void addJavaScript(String string, PdfAction pdfAction) {
        if (pdfAction.get(PdfName.JS) == null) {
            throw new RuntimeException("Only JavaScript actions are allowed.");
        }
        try {
            this.documentLevelJS.put(string, this.writer.addToBody(pdfAction).getIndirectReference());
        }
        catch (IOException var3_3) {
            throw new ExceptionConverter(var3_3);
        }
    }

    HashMap getDocumentLevelJS() {
        return this.documentLevelJS;
    }

    void addFileAttachment(String string, PdfFileSpecification pdfFileSpecification) throws IOException {
        Object object;
        if (string == null) {
            object = (PdfString)pdfFileSpecification.get(PdfName.DESC);
            string = object == null ? "" : PdfEncodings.convertToString(object.getBytes(), null);
        }
        pdfFileSpecification.addDescription(string, true);
        if (string.length() == 0) {
            string = "Unnamed";
        }
        object = PdfEncodings.convertToString(new PdfString(string, "UnicodeBig").getBytes(), null);
        int n = 0;
        while (this.documentFileAttachment.containsKey(object)) {
            object = PdfEncodings.convertToString(new PdfString(string + " " + ++n, "UnicodeBig").getBytes(), null);
        }
        this.documentFileAttachment.put(object, pdfFileSpecification.getReference());
    }

    HashMap getDocumentFileAttachment() {
        return this.documentFileAttachment;
    }

    void setOpenAction(String string) {
        this.openActionName = string;
        this.openActionAction = null;
    }

    void setOpenAction(PdfAction pdfAction) {
        this.openActionAction = pdfAction;
        this.openActionName = null;
    }

    void addAdditionalAction(PdfName pdfName, PdfAction pdfAction) {
        if (this.additionalActions == null) {
            this.additionalActions = new PdfDictionary();
        }
        if (pdfAction == null) {
            this.additionalActions.remove(pdfName);
        } else {
            this.additionalActions.put(pdfName, pdfAction);
        }
        if (this.additionalActions.size() == 0) {
            this.additionalActions = null;
        }
    }

    public void setCollection(PdfCollection pdfCollection) {
        this.collection = pdfCollection;
    }

    PdfAcroForm getAcroForm() {
        return this.annotationsImp.getAcroForm();
    }

    void setSigFlags(int n) {
        this.annotationsImp.setSigFlags(n);
    }

    void addCalculationOrder(PdfFormField pdfFormField) {
        this.annotationsImp.addCalculationOrder(pdfFormField);
    }

    void addAnnotation(PdfAnnotation pdfAnnotation) {
        this.pageEmpty = false;
        this.annotationsImp.addAnnotation(pdfAnnotation);
    }

    int getMarkPoint() {
        return this.markPoint;
    }

    void incMarkPoint() {
        ++this.markPoint;
    }

    void setCropBoxSize(Rectangle rectangle) {
        this.setBoxSize("crop", rectangle);
    }

    void setBoxSize(String string, Rectangle rectangle) {
        if (rectangle == null) {
            this.boxSize.remove(string);
        } else {
            this.boxSize.put(string, new PdfRectangle(rectangle));
        }
    }

    protected void setNewPageSizeAndMargins() {
        this.pageSize = this.nextPageSize;
        if (this.marginMirroring && (this.getPageNumber() & 1) == 0) {
            this.marginRight = this.nextMarginLeft;
            this.marginLeft = this.nextMarginRight;
        } else {
            this.marginLeft = this.nextMarginLeft;
            this.marginRight = this.nextMarginRight;
        }
        this.marginTop = this.nextMarginTop;
        this.marginBottom = this.nextMarginBottom;
    }

    Rectangle getBoxSize(String string) {
        PdfRectangle pdfRectangle = (PdfRectangle)this.thisBoxSize.get(string);
        if (pdfRectangle != null) {
            return pdfRectangle.getRectangle();
        }
        return null;
    }

    void setPageEmpty(boolean bl) {
        this.pageEmpty = bl;
    }

    void setDuration(int n) {
        this.duration = n > 0 ? n : -1;
    }

    void setTransition(PdfTransition pdfTransition) {
        this.transition = pdfTransition;
    }

    void setPageAction(PdfName pdfName, PdfAction pdfAction) {
        if (this.pageAA == null) {
            this.pageAA = new PdfDictionary();
        }
        this.pageAA.put(pdfName, pdfAction);
    }

    void setThumbnail(Image image) throws PdfException, DocumentException {
        this.thumb = this.writer.getImageReference(this.writer.addDirectImageSimple(image));
    }

    PageResources getPageResources() {
        return this.pageResources;
    }

    boolean isStrictImageSequence() {
        return this.strictImageSequence;
    }

    void setStrictImageSequence(boolean bl) {
        this.strictImageSequence = bl;
    }

    public void clearTextWrap() {
        float f = this.imageEnd - this.currentHeight;
        if (this.line != null) {
            f += this.line.height();
        }
        if (this.imageEnd > -1.0f && f > 0.0f) {
            this.carriageReturn();
            this.currentHeight += f;
        }
    }

    protected void add(Image image) throws PdfException, DocumentException {
        if (image.hasAbsoluteY()) {
            this.graphics.addImage(image);
            this.pageEmpty = false;
            return;
        }
        if (this.currentHeight != 0.0f && this.indentTop() - this.currentHeight - image.getScaledHeight() < this.indentBottom()) {
            if (!this.strictImageSequence && this.imageWait == null) {
                this.imageWait = image;
                return;
            }
            this.newPage();
            if (this.currentHeight != 0.0f && this.indentTop() - this.currentHeight - image.getScaledHeight() < this.indentBottom()) {
                this.imageWait = image;
                return;
            }
        }
        this.pageEmpty = false;
        if (image == this.imageWait) {
            this.imageWait = null;
        }
        boolean bl = (image.getAlignment() & 4) == 4 && (image.getAlignment() & 1) != 1;
        boolean bl2 = (image.getAlignment() & 8) == 8;
        float f = this.leading / 2.0f;
        if (bl) {
            f += this.leading;
        }
        float f2 = this.indentTop() - this.currentHeight - image.getScaledHeight() - f;
        float[] arrf = image.matrix();
        float f3 = this.indentLeft() - arrf[4];
        if ((image.getAlignment() & 2) == 2) {
            f3 = this.indentRight() - image.getScaledWidth() - arrf[4];
        }
        if ((image.getAlignment() & 1) == 1) {
            f3 = this.indentLeft() + (this.indentRight() - this.indentLeft() - image.getScaledWidth()) / 2.0f - arrf[4];
        }
        if (image.hasAbsoluteX()) {
            f3 = image.getAbsoluteX();
        }
        if (bl) {
            if (this.imageEnd < 0.0f || this.imageEnd < this.currentHeight + image.getScaledHeight() + f) {
                this.imageEnd = this.currentHeight + image.getScaledHeight() + f;
            }
            if ((image.getAlignment() & 2) == 2) {
                this.indentation.imageIndentRight += image.getScaledWidth() + image.getIndentationLeft();
            } else {
                this.indentation.imageIndentLeft += image.getScaledWidth() + image.getIndentationRight();
            }
        } else {
            f3 = (image.getAlignment() & 2) == 2 ? (f3 -= image.getIndentationRight()) : ((image.getAlignment() & 1) == 1 ? (f3 += image.getIndentationLeft() - image.getIndentationRight()) : (f3 += image.getIndentationLeft()));
        }
        this.graphics.addImage(image, arrf[0], arrf[1], arrf[2], arrf[3], f3, f2 - arrf[5]);
        if (!bl && !bl2) {
            this.currentHeight += image.getScaledHeight() + f;
            this.flushLines();
            this.text.moveText(0.0f, - image.getScaledHeight() + f);
            this.newLine();
        }
    }

    void addPTable(PdfPTable pdfPTable) throws DocumentException {
        ColumnText columnText = new ColumnText(this.writer.getDirectContent());
        if (this.currentHeight > 0.0f) {
            Paragraph paragraph = new Paragraph();
            paragraph.setLeading(0.0f);
            columnText.addElement(paragraph);
            if (pdfPTable.getKeepTogether() && !this.fitsPage(pdfPTable, 0.0f)) {
                this.newPage();
            }
        }
        columnText.addElement(pdfPTable);
        boolean bl = pdfPTable.isHeadersInEvent();
        pdfPTable.setHeadersInEvent(true);
        int n = 0;
        do {
            columnText.setSimpleColumn(this.indentLeft(), this.indentBottom(), this.indentRight(), this.indentTop() - this.currentHeight);
            int n2 = columnText.go();
            if ((n2 & 1) != 0) {
                this.text.moveText(0.0f, columnText.getYLine() - this.indentTop() + this.currentHeight);
                this.currentHeight = this.indentTop() - columnText.getYLine();
                break;
            }
            n = this.indentTop() - this.currentHeight == columnText.getYLine() ? ++n : 0;
            if (n == 3) {
                this.add(new Paragraph("ERROR: Infinite table loop"));
                break;
            }
            this.newPage();
        } while (true);
        pdfPTable.setHeadersInEvent(bl);
    }

    boolean fitsPage(PdfPTable pdfPTable, float f) {
        if (!pdfPTable.isLockedWidth()) {
            float f2 = (this.indentRight() - this.indentLeft()) * pdfPTable.getWidthPercentage() / 100.0f;
            pdfPTable.setTotalWidth(f2);
        }
        this.ensureNewLine();
        return pdfPTable.getTotalHeight() <= this.indentTop() - this.currentHeight - this.indentBottom() - f;
    }

    private void addPdfTable(Table table) throws DocumentException {
        this.flushLines();
        PdfTable pdfTable = new PdfTable(table, this.indentLeft(), this.indentRight(), this.indentTop() - this.currentHeight);
        RenderingContext renderingContext = new RenderingContext();
        renderingContext.pagetop = this.indentTop();
        renderingContext.oldHeight = this.currentHeight;
        renderingContext.cellGraphics = new PdfContentByte(this.writer);
        renderingContext.rowspanMap = new HashMap();
        renderingContext.table = pdfTable;
        ArrayList arrayList = pdfTable.getHeaderCells();
        ArrayList arrayList2 = pdfTable.getCells();
        ArrayList arrayList3 = this.extractRows(arrayList2, renderingContext);
        boolean bl = false;
        while (!arrayList2.isEmpty()) {
            PdfCell pdfCell;
            int n;
            Object object;
            Object object2;
            renderingContext.lostTableBottom = 0.0f;
            boolean bl2 = false;
            Iterator iterator = arrayList3.iterator();
            boolean bl3 = false;
            while (iterator.hasNext()) {
                ArrayList arrayList4 = (ArrayList)iterator.next();
                this.analyzeRow(arrayList3, renderingContext);
                this.renderCells(renderingContext, arrayList4, pdfTable.hasToFitPageCells() & bl3);
                if (!this.mayBeRemoved(arrayList4)) break;
                this.consumeRowspan(arrayList4, renderingContext);
                iterator.remove();
                bl3 = true;
            }
            arrayList2.clear();
            HashSet<PdfCell> arrayList4 = new HashSet<PdfCell>();
            iterator = arrayList3.iterator();
            while (iterator.hasNext()) {
                object2 = (ArrayList)iterator.next();
                object = object2.iterator();
                while (object.hasNext()) {
                    pdfCell = (PdfCell)object.next();
                    if (arrayList4.contains(pdfCell)) continue;
                    arrayList2.add(pdfCell);
                    arrayList4.add(pdfCell);
                }
            }
            object2 = new Rectangle(pdfTable);
            object2.setBorder(pdfTable.getBorder());
            object2.setBorderWidth(pdfTable.getBorderWidth());
            object2.setBorderColor(pdfTable.getBorderColor());
            object2.setBackgroundColor(pdfTable.getBackgroundColor());
            object = this.writer.getDirectContentUnder();
            object.rectangle(object2.rectangle(this.top(), this.indentBottom()));
            object.add(renderingContext.cellGraphics);
            object2.setBackgroundColor(null);
            object2 = object2.rectangle(this.top(), this.indentBottom());
            object2.setBorder(pdfTable.getBorder());
            object.rectangle((Rectangle)object2);
            renderingContext.cellGraphics = new PdfContentByte(null);
            if (arrayList3.isEmpty()) continue;
            bl = true;
            this.graphics.setLineWidth(pdfTable.getBorderWidth());
            if (bl2 && (pdfTable.getBorder() & 2) == 2) {
                Color color = pdfTable.getBorderColor();
                if (color != null) {
                    this.graphics.setColorStroke(color);
                }
                this.graphics.moveTo(pdfTable.getLeft(), Math.max(pdfTable.getBottom(), this.indentBottom()));
                this.graphics.lineTo(pdfTable.getRight(), Math.max(pdfTable.getBottom(), this.indentBottom()));
                this.graphics.stroke();
                if (color != null) {
                    this.graphics.resetRGBColorStroke();
                }
            }
            this.pageEmpty = false;
            float f = renderingContext.lostTableBottom;
            this.newPage();
            float f2 = 0.0f;
            boolean bl4 = false;
            if (this.currentHeight > 0.0f) {
                f2 = 6.0f;
                this.currentHeight += f2;
                bl4 = true;
                this.newLine();
                this.flushLines();
                this.indentation.indentTop = this.currentHeight - this.leading;
                this.currentHeight = 0.0f;
            } else {
                this.flushLines();
            }
            int n2 = arrayList.size();
            if (n2 > 0) {
                pdfCell = (PdfCell)arrayList.get(0);
                float f3 = pdfCell.getTop(0.0f);
                for (int f7 = 0; f7 < n2; ++f7) {
                    Image image /* !! */ ;
                    pdfCell = (PdfCell)arrayList.get(f7);
                    pdfCell.setTop(this.indentTop() - f3 + pdfCell.getTop(0.0f));
                    pdfCell.setBottom(this.indentTop() - f3 + pdfCell.getBottom(0.0f));
                    renderingContext.pagetop = pdfCell.getBottom();
                    renderingContext.cellGraphics.rectangle(pdfCell.rectangle(this.indentTop(), this.indentBottom()));
                    ArrayList f8 = pdfCell.getImages(this.indentTop(), this.indentBottom());
                    Iterator iterator2 = f8.iterator();
                    while (iterator2.hasNext()) {
                        bl2 = true;
                        image /* !! */  = (Image)iterator2.next();
                        this.graphics.addImage(image /* !! */ );
                    }
                    this.lines = pdfCell.getLines(this.indentTop(), this.indentBottom());
                    float f4 = pdfCell.getTop(this.indentTop());
                    this.text.moveText(0.0f, f4 - f2);
                    image /* !! */  = (Image)(this.flushLines() - f4 + f2);
                    this.text.moveText(0.0f, (float)image /* !! */ );
                }
                this.currentHeight = this.indentTop() - renderingContext.pagetop + pdfTable.cellspacing();
                this.text.moveText(0.0f, renderingContext.pagetop - this.indentTop() - this.currentHeight);
            } else if (bl4) {
                renderingContext.pagetop = this.indentTop();
                this.text.moveText(0.0f, - pdfTable.cellspacing());
            }
            renderingContext.oldHeight = this.currentHeight - f2;
            n2 = Math.min(arrayList2.size(), pdfTable.columns());
            for (n = 0; n < n2; ++n) {
                float f5;
                float f6;
                pdfCell = (PdfCell)arrayList2.get(n);
                if (pdfCell.getTop(- pdfTable.cellspacing()) <= renderingContext.lostTableBottom || (f6 = renderingContext.pagetop - f + pdfCell.getBottom()) <= renderingContext.pagetop - (f5 = pdfCell.remainingHeight())) continue;
                f += f6 - (renderingContext.pagetop - f5);
            }
            n2 = arrayList2.size();
            pdfTable.setTop(this.indentTop());
            pdfTable.setBottom(renderingContext.pagetop - f + pdfTable.getBottom(pdfTable.cellspacing()));
            for (n = 0; n < n2; ++n) {
                pdfCell = (PdfCell)arrayList2.get(n);
                float f7 = renderingContext.pagetop - f + pdfCell.getBottom();
                float f8 = renderingContext.pagetop - f + pdfCell.getTop(- pdfTable.cellspacing());
                if (f8 > this.indentTop() - this.currentHeight) {
                    f8 = this.indentTop() - this.currentHeight;
                }
                pdfCell.setTop(f8);
                pdfCell.setBottom(f7);
            }
        }
        float f = pdfTable.getTop() - pdfTable.getBottom();
        if (bl) {
            this.currentHeight = f;
            this.text.moveText(0.0f, - f - renderingContext.oldHeight * 2.0f);
        } else {
            this.currentHeight = renderingContext.oldHeight + f;
            this.text.moveText(0.0f, - f);
        }
        this.pageEmpty = false;
    }

    protected void analyzeRow(ArrayList arrayList, RenderingContext renderingContext) {
        renderingContext.maxCellBottom = this.indentBottom();
        int n = 0;
        ArrayList arrayList2 = (ArrayList)arrayList.get(n);
        int n2 = 1;
        Iterator iterator = arrayList2.iterator();
        while (iterator.hasNext()) {
            PdfCell pdfCell = (PdfCell)iterator.next();
            n2 = Math.max(renderingContext.currentRowspan(pdfCell), n2);
        }
        boolean bl = true;
        if ((n += n2) == arrayList.size()) {
            n = arrayList.size() - 1;
            bl = false;
        }
        if (n < 0 || n >= arrayList.size()) {
            return;
        }
        arrayList2 = (ArrayList)arrayList.get(n);
        iterator = arrayList2.iterator();
        while (iterator.hasNext()) {
            PdfCell pdfCell = (PdfCell)iterator.next();
            Rectangle rectangle = pdfCell.rectangle(renderingContext.pagetop, this.indentBottom());
            if (bl) {
                renderingContext.maxCellBottom = Math.max(renderingContext.maxCellBottom, rectangle.getTop());
                continue;
            }
            if (renderingContext.currentRowspan(pdfCell) != 1) continue;
            renderingContext.maxCellBottom = Math.max(renderingContext.maxCellBottom, rectangle.getBottom());
        }
    }

    protected boolean mayBeRemoved(ArrayList arrayList) {
        Iterator iterator = arrayList.iterator();
        boolean bl = true;
        while (iterator.hasNext()) {
            PdfCell pdfCell = (PdfCell)iterator.next();
            bl &= pdfCell.mayBeRemoved();
        }
        return bl;
    }

    protected void consumeRowspan(ArrayList arrayList, RenderingContext renderingContext) {
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            PdfCell pdfCell = (PdfCell)iterator.next();
            renderingContext.consumeRowspan(pdfCell);
        }
    }

    protected ArrayList extractRows(ArrayList arrayList, RenderingContext renderingContext) {
        int n;
        int n2;
        PdfCell pdfCell = null;
        ArrayList arrayList2 = new ArrayList();
        ArrayList<PdfCell> arrayList3 = new ArrayList<PdfCell>();
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            PdfCell pdfCell2 = (PdfCell)iterator.next();
            n2 = 0;
            boolean bl = !iterator.hasNext();
            int n3 = n = !iterator.hasNext() ? 1 : 0;
            if (pdfCell != null && pdfCell2.getLeft() <= pdfCell.getLeft()) {
                bl = true;
                n = 0;
            }
            if (n != 0) {
                arrayList3.add(pdfCell2);
                n2 = 1;
            }
            if (bl) {
                if (!arrayList3.isEmpty()) {
                    arrayList2.add(arrayList3);
                }
                arrayList3 = new ArrayList();
            }
            if (n2 == 0) {
                arrayList3.add(pdfCell2);
            }
            pdfCell = pdfCell2;
        }
        if (!arrayList3.isEmpty()) {
            arrayList2.add(arrayList3);
        }
        for (n2 = arrayList2.size() - 1; n2 >= 0; --n2) {
            ArrayList arrayList4 = (ArrayList)arrayList2.get(n2);
            for (n = 0; n < arrayList4.size(); ++n) {
                PdfCell pdfCell3 = (PdfCell)arrayList4.get(n);
                int n4 = pdfCell3.rowspan();
                for (int i = 1; i < n4 && arrayList2.size() < n2 + i; ++i) {
                    ArrayList arrayList5 = (ArrayList)arrayList2.get(n2 + i);
                    if (arrayList5.size() <= n) continue;
                    arrayList5.add(n, pdfCell3);
                }
            }
        }
        return arrayList2;
    }

    protected void renderCells(RenderingContext renderingContext, java.util.List list, boolean bl) throws DocumentException {
        Iterator iterator;
        PdfCell pdfCell;
        if (bl) {
            iterator = list.iterator();
            while (iterator.hasNext()) {
                pdfCell = (PdfCell)iterator.next();
                if (pdfCell.isHeader() || pdfCell.getBottom() >= this.indentBottom()) continue;
                return;
            }
        }
        iterator = list.iterator();
        while (iterator.hasNext()) {
            float f;
            pdfCell = (PdfCell)iterator.next();
            if (renderingContext.isCellRenderedOnPage(pdfCell, this.getPageNumber())) continue;
            float f2 = 0.0f;
            if (renderingContext.numCellRendered(pdfCell) >= 1) {
                f2 = 1.0f;
            }
            this.lines = pdfCell.getLines(renderingContext.pagetop, this.indentBottom() - f2);
            if (this.lines != null && !this.lines.isEmpty()) {
                f = pdfCell.getTop(renderingContext.pagetop - renderingContext.oldHeight);
                this.text.moveText(0.0f, f);
                float f3 = this.flushLines() - f;
                this.text.moveText(0.0f, f3);
                if (renderingContext.oldHeight + f3 > this.currentHeight) {
                    this.currentHeight = renderingContext.oldHeight + f3;
                }
                renderingContext.cellRendered(pdfCell, this.getPageNumber());
            }
            f = Math.max(pdfCell.getBottom(), this.indentBottom());
            Rectangle rectangle = renderingContext.table.rectangle(renderingContext.pagetop, this.indentBottom());
            f = Math.max(rectangle.getBottom(), f);
            Rectangle rectangle2 = pdfCell.rectangle(rectangle.getTop(), f);
            if (rectangle2.getHeight() > 0.0f) {
                renderingContext.lostTableBottom = f;
                renderingContext.cellGraphics.rectangle(rectangle2);
            }
            ArrayList arrayList = pdfCell.getImages(renderingContext.pagetop, this.indentBottom());
            Iterator iterator2 = arrayList.iterator();
            while (iterator2.hasNext()) {
                Image image = (Image)iterator2.next();
                this.graphics.addImage(image);
            }
        }
    }

    float bottom(Table table) {
        PdfTable pdfTable = new PdfTable(table, this.indentLeft(), this.indentRight(), this.indentTop() - this.currentHeight);
        return pdfTable.getBottom();
    }

    protected void doFooter() throws DocumentException {
        if (this.footer == null) {
            return;
        }
        float f = this.indentation.indentLeft;
        float f2 = this.indentation.indentRight;
        float f3 = this.indentation.listIndentLeft;
        float f4 = this.indentation.imageIndentLeft;
        float f5 = this.indentation.imageIndentRight;
        this.indentation.indentRight = 0.0f;
        this.indentation.indentLeft = 0.0f;
        this.indentation.listIndentLeft = 0.0f;
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.imageIndentRight = 0.0f;
        this.footer.setPageNumber(this.pageN);
        this.leading = this.footer.paragraph().getTotalLeading();
        this.add(this.footer.paragraph());
        this.indentation.indentBottom = this.currentHeight;
        this.text.moveText(this.left(), this.indentBottom());
        this.flushLines();
        this.text.moveText(- this.left(), - this.bottom());
        this.footer.setTop(this.bottom(this.currentHeight));
        this.footer.setBottom(this.bottom() - 0.75f * this.leading);
        this.footer.setLeft(this.left());
        this.footer.setRight(this.right());
        this.graphics.rectangle(this.footer);
        this.indentation.indentBottom = this.currentHeight + this.leading * 2.0f;
        this.currentHeight = 0.0f;
        this.indentation.indentLeft = f;
        this.indentation.indentRight = f2;
        this.indentation.listIndentLeft = f3;
        this.indentation.imageIndentLeft = f4;
        this.indentation.imageIndentRight = f5;
    }

    protected void doHeader() throws DocumentException {
        if (this.header == null) {
            return;
        }
        float f = this.indentation.indentLeft;
        float f2 = this.indentation.indentRight;
        float f3 = this.indentation.listIndentLeft;
        float f4 = this.indentation.imageIndentLeft;
        float f5 = this.indentation.imageIndentRight;
        this.indentation.indentRight = 0.0f;
        this.indentation.indentLeft = 0.0f;
        this.indentation.listIndentLeft = 0.0f;
        this.indentation.imageIndentLeft = 0.0f;
        this.indentation.imageIndentRight = 0.0f;
        this.header.setPageNumber(this.pageN);
        this.leading = this.header.paragraph().getTotalLeading();
        this.text.moveText(0.0f, this.leading);
        this.add(this.header.paragraph());
        this.newLine();
        this.indentation.indentTop = this.currentHeight - this.leading;
        this.header.setTop(this.top() + this.leading);
        this.header.setBottom(this.indentTop() + this.leading * 2.0f / 3.0f);
        this.header.setLeft(this.left());
        this.header.setRight(this.right());
        this.graphics.rectangle(this.header);
        this.flushLines();
        this.currentHeight = 0.0f;
        this.indentation.indentLeft = f;
        this.indentation.indentRight = f2;
        this.indentation.listIndentLeft = f3;
        this.indentation.imageIndentLeft = f4;
        this.indentation.imageIndentRight = f5;
    }

    protected static class RenderingContext {
        float pagetop = -1.0f;
        float oldHeight = -1.0f;
        PdfContentByte cellGraphics = null;
        float lostTableBottom;
        float maxCellBottom;
        float maxCellHeight;
        Map rowspanMap;
        Map pageMap = new HashMap();
        public PdfTable table;

        protected RenderingContext() {
        }

        public int consumeRowspan(PdfCell pdfCell) {
            if (pdfCell.rowspan() == 1) {
                return 1;
            }
            Integer n = (Integer)this.rowspanMap.get(pdfCell);
            if (n == null) {
                n = new Integer(pdfCell.rowspan());
            }
            n = new Integer(n - 1);
            this.rowspanMap.put(pdfCell, n);
            if (n < 1) {
                return 1;
            }
            return n;
        }

        public int currentRowspan(PdfCell pdfCell) {
            Integer n = (Integer)this.rowspanMap.get(pdfCell);
            if (n == null) {
                return pdfCell.rowspan();
            }
            return n;
        }

        public int cellRendered(PdfCell pdfCell, int n) {
            Integer n2 = (Integer)this.pageMap.get(pdfCell);
            n2 = n2 == null ? new Integer(1) : new Integer(n2 + 1);
            this.pageMap.put(pdfCell, n2);
            Integer n3 = new Integer(n);
            HashSet<PdfCell> hashSet = (HashSet<PdfCell>)this.pageMap.get(n3);
            if (hashSet == null) {
                hashSet = new HashSet<PdfCell>();
                this.pageMap.put(n3, hashSet);
            }
            hashSet.add(pdfCell);
            return n2;
        }

        public int numCellRendered(PdfCell pdfCell) {
            Integer n = (Integer)this.pageMap.get(pdfCell);
            if (n == null) {
                n = new Integer(0);
            }
            return n;
        }

        public boolean isCellRenderedOnPage(PdfCell pdfCell, int n) {
            Integer n2 = new Integer(n);
            Set set = (Set)this.pageMap.get(n2);
            if (set != null) {
                return set.contains(pdfCell);
            }
            return false;
        }
    }

    public static class Indentation {
        float indentLeft = 0.0f;
        float sectionIndentLeft = 0.0f;
        float listIndentLeft = 0.0f;
        float imageIndentLeft = 0.0f;
        float indentRight = 0.0f;
        float sectionIndentRight = 0.0f;
        float imageIndentRight = 0.0f;
        float indentTop = 0.0f;
        float indentBottom = 0.0f;
    }

    static class PdfCatalog
    extends PdfDictionary {
        PdfWriter writer;

        PdfCatalog(PdfIndirectReference pdfIndirectReference, PdfWriter pdfWriter) {
            super(CATALOG);
            this.writer = pdfWriter;
            this.put(PdfName.PAGES, pdfIndirectReference);
        }

        void addNames(TreeMap treeMap, HashMap hashMap, HashMap hashMap2, PdfWriter pdfWriter) {
            if (treeMap.isEmpty() && hashMap.isEmpty() && hashMap2.isEmpty()) {
                return;
            }
            try {
                PdfArray pdfArray;
                PdfDictionary pdfDictionary = new PdfDictionary();
                if (!treeMap.isEmpty()) {
                    pdfArray = new PdfArray();
                    Object object = treeMap.entrySet().iterator();
                    while (object.hasNext()) {
                        Map.Entry entry = object.next();
                        String string = (String)entry.getKey();
                        Object[] arrobject = (Object[])entry.getValue();
                        PdfIndirectReference pdfIndirectReference = (PdfIndirectReference)arrobject[1];
                        pdfArray.add(new PdfString(string, null));
                        pdfArray.add(pdfIndirectReference);
                    }
                    object = new PdfDictionary();
                    object.put(PdfName.NAMES, pdfArray);
                    pdfDictionary.put(PdfName.DESTS, pdfWriter.addToBody((PdfObject)object).getIndirectReference());
                }
                if (!hashMap.isEmpty()) {
                    pdfArray = PdfNameTree.writeTree(hashMap, pdfWriter);
                    pdfDictionary.put(PdfName.JAVASCRIPT, pdfWriter.addToBody(pdfArray).getIndirectReference());
                }
                if (!hashMap2.isEmpty()) {
                    pdfDictionary.put(PdfName.EMBEDDEDFILES, pdfWriter.addToBody(PdfNameTree.writeTree(hashMap2, pdfWriter)).getIndirectReference());
                }
                this.put(PdfName.NAMES, pdfWriter.addToBody(pdfDictionary).getIndirectReference());
            }
            catch (IOException var5_6) {
                throw new ExceptionConverter(var5_6);
            }
        }

        void setOpenAction(PdfAction pdfAction) {
            this.put(PdfName.OPENACTION, pdfAction);
        }

        void setAdditionalActions(PdfDictionary pdfDictionary) {
            try {
                this.put(PdfName.AA, this.writer.addToBody(pdfDictionary).getIndirectReference());
            }
            catch (Exception var2_2) {
                throw new ExceptionConverter(var2_2);
            }
        }
    }

    public static class PdfInfo
    extends PdfDictionary {
        PdfInfo() {
            this.addProducer();
            this.addCreationDate();
        }

        PdfInfo(String string, String string2, String string3) {
            this();
            this.addTitle(string2);
            this.addSubject(string3);
            this.addAuthor(string);
        }

        void addTitle(String string) {
            this.put(PdfName.TITLE, new PdfString(string, "UnicodeBig"));
        }

        void addSubject(String string) {
            this.put(PdfName.SUBJECT, new PdfString(string, "UnicodeBig"));
        }

        void addKeywords(String string) {
            this.put(PdfName.KEYWORDS, new PdfString(string, "UnicodeBig"));
        }

        void addAuthor(String string) {
            this.put(PdfName.AUTHOR, new PdfString(string, "UnicodeBig"));
        }

        void addCreator(String string) {
            this.put(PdfName.CREATOR, new PdfString(string, "UnicodeBig"));
        }

        void addProducer() {
            this.put(PdfName.PRODUCER, new PdfString(Document.getVersion()));
        }

        void addCreationDate() {
            PdfDate pdfDate = new PdfDate();
            this.put(PdfName.CREATIONDATE, pdfDate);
            this.put(PdfName.MODDATE, pdfDate);
        }

        void addkey(String string, String string2) {
            if (string.equals("Producer") || string.equals("CreationDate")) {
                return;
            }
            this.put(new PdfName(string), new PdfString(string2, "UnicodeBig"));
        }
    }

}

