/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html.simpleparser;

import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Phrase;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.html.Markup;
import com.lowagie.text.html.simpleparser.ChainedProperties;
import com.lowagie.text.pdf.PdfPCell;
import java.awt.Color;
import java.util.ArrayList;

public class IncCell
implements TextElementArray {
    private ArrayList chunks = new ArrayList();
    private PdfPCell cell = new PdfPCell((Phrase)null);

    public IncCell(String string, ChainedProperties chainedProperties) {
        String string2 = chainedProperties.getProperty("colspan");
        if (string2 != null) {
            this.cell.setColspan(Integer.parseInt(string2));
        }
        string2 = chainedProperties.getProperty("align");
        if (string.equals("th")) {
            this.cell.setHorizontalAlignment(1);
        }
        if (string2 != null) {
            if ("center".equalsIgnoreCase(string2)) {
                this.cell.setHorizontalAlignment(1);
            } else if ("right".equalsIgnoreCase(string2)) {
                this.cell.setHorizontalAlignment(2);
            } else if ("left".equalsIgnoreCase(string2)) {
                this.cell.setHorizontalAlignment(0);
            } else if ("justify".equalsIgnoreCase(string2)) {
                this.cell.setHorizontalAlignment(3);
            }
        }
        string2 = chainedProperties.getProperty("valign");
        this.cell.setVerticalAlignment(5);
        if (string2 != null) {
            if ("top".equalsIgnoreCase(string2)) {
                this.cell.setVerticalAlignment(4);
            } else if ("bottom".equalsIgnoreCase(string2)) {
                this.cell.setVerticalAlignment(6);
            }
        }
        string2 = chainedProperties.getProperty("border");
        float f = 0.0f;
        if (string2 != null) {
            f = Float.parseFloat(string2);
        }
        this.cell.setBorderWidth(f);
        string2 = chainedProperties.getProperty("cellpadding");
        if (string2 != null) {
            this.cell.setPadding(Float.parseFloat(string2));
        }
        this.cell.setUseDescender(true);
        string2 = chainedProperties.getProperty("bgcolor");
        this.cell.setBackgroundColor(Markup.decodeColor(string2));
    }

    public boolean add(Object object) {
        if (!(object instanceof Element)) {
            return false;
        }
        this.cell.addElement((Element)object);
        return true;
    }

    public ArrayList getChunks() {
        return this.chunks;
    }

    public boolean process(ElementListener elementListener) {
        return true;
    }

    public int type() {
        return 30;
    }

    public PdfPCell getCell() {
        return this.cell;
    }

    public boolean isContent() {
        return true;
    }

    public boolean isNestable() {
        return true;
    }
}

