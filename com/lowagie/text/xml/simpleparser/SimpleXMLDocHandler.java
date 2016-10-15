/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.xml.simpleparser;

import java.util.HashMap;

public interface SimpleXMLDocHandler {
    public void startElement(String var1, HashMap var2);

    public void endElement(String var1);

    public void startDocument();

    public void endDocument();

    public void text(String var1);
}

