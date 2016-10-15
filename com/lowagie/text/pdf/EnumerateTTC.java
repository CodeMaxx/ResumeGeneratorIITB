/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.TrueTypeFont;
import java.io.IOException;
import java.util.HashMap;

class EnumerateTTC
extends TrueTypeFont {
    protected String[] names;

    EnumerateTTC(String string) throws DocumentException, IOException {
        this.fileName = string;
        this.rf = new RandomAccessFileOrArray(string);
        this.findNames();
    }

    EnumerateTTC(byte[] arrby) throws DocumentException, IOException {
        this.fileName = "Byte array TTC";
        this.rf = new RandomAccessFileOrArray(arrby);
        this.findNames();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void findNames() throws DocumentException, IOException {
        this.tables = new HashMap();
        try {
            String string = this.readStandardString(4);
            if (!string.equals("ttcf")) {
                throw new DocumentException(this.fileName + " is not a valid TTC file.");
            }
            this.rf.skipBytes(4);
            int n = this.rf.readInt();
            this.names = new String[n];
            int n2 = this.rf.getFilePointer();
            for (int i = 0; i < n; ++i) {
                this.tables.clear();
                this.rf.seek(n2);
                this.rf.skipBytes(i * 4);
                this.directoryOffset = this.rf.readInt();
                this.rf.seek(this.directoryOffset);
                if (this.rf.readInt() != 65536) {
                    throw new DocumentException(this.fileName + " is not a valid TTF file.");
                }
                int n3 = this.rf.readUnsignedShort();
                this.rf.skipBytes(6);
                for (int j = 0; j < n3; ++j) {
                    String string2 = this.readStandardString(4);
                    this.rf.skipBytes(4);
                    int[] arrn = new int[]{this.rf.readInt(), this.rf.readInt()};
                    this.tables.put(string2, arrn);
                }
                this.names[i] = this.getBaseFont();
            }
            Object var10_9 = null;
            if (this.rf == null) return;
        }
        catch (Throwable var9_11) {
            Object var10_10 = null;
            if (this.rf == null) throw var9_11;
            this.rf.close();
            throw var9_11;
        }
        this.rf.close();
    }

    String[] getNames() {
        return this.names;
    }
}

