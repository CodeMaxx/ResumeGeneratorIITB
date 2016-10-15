/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.html.simpleparser;

import com.lowagie.text.DocListener;
import com.lowagie.text.Image;
import com.lowagie.text.html.simpleparser.ChainedProperties;
import java.util.HashMap;

public interface ImageProvider {
    public Image getImage(String var1, HashMap var2, ChainedProperties var3, DocListener var4);
}

