/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.hyphenation;

import java.util.ArrayList;

public interface PatternConsumer {
    public void addClass(String var1);

    public void addException(String var1, ArrayList var2);

    public void addPattern(String var1, String var2);
}

