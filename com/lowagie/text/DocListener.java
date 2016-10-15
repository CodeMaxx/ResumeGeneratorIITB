/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.ElementListener;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Rectangle;

public interface DocListener
extends ElementListener {
    public void open();

    public void close();

    public boolean newPage();

    public boolean setPageSize(Rectangle var1);

    public boolean setMargins(float var1, float var2, float var3, float var4);

    public boolean setMarginMirroring(boolean var1);

    public void setPageCount(int var1);

    public void resetPageCount();

    public void setHeader(HeaderFooter var1);

    public void resetHeader();

    public void setFooter(HeaderFooter var1);

    public void resetFooter();
}

