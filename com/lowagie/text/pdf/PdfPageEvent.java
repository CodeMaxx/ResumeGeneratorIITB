/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

public interface PdfPageEvent {
    public void onOpenDocument(PdfWriter var1, Document var2);

    public void onStartPage(PdfWriter var1, Document var2);

    public void onEndPage(PdfWriter var1, Document var2);

    public void onCloseDocument(PdfWriter var1, Document var2);

    public void onParagraph(PdfWriter var1, Document var2, float var3);

    public void onParagraphEnd(PdfWriter var1, Document var2, float var3);

    public void onChapter(PdfWriter var1, Document var2, float var3, Paragraph var4);

    public void onChapterEnd(PdfWriter var1, Document var2, float var3);

    public void onSection(PdfWriter var1, Document var2, float var3, int var4, Paragraph var5);

    public void onSectionEnd(PdfWriter var1, Document var2, float var3);

    public void onGenericTag(PdfWriter var1, Document var2, Rectangle var3, String var4);
}

