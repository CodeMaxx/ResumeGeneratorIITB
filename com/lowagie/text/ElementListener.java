/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import java.util.EventListener;

public interface ElementListener
extends EventListener {
    public boolean add(Element var1) throws DocumentException;
}

