/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

class TrueTypeFontSubSet {
    static final String[] tableNamesSimple = new String[]{"cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep"};
    static final String[] tableNamesCmap = new String[]{"cmap", "cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "prep"};
    static final String[] tableNamesExtra = new String[]{"OS/2", "cmap", "cvt ", "fpgm", "glyf", "head", "hhea", "hmtx", "loca", "maxp", "name, prep"};
    static final int[] entrySelectors = new int[]{0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4};
    static final int TABLE_CHECKSUM = 0;
    static final int TABLE_OFFSET = 1;
    static final int TABLE_LENGTH = 2;
    static final int HEAD_LOCA_FORMAT_OFFSET = 51;
    static final int ARG_1_AND_2_ARE_WORDS = 1;
    static final int WE_HAVE_A_SCALE = 8;
    static final int MORE_COMPONENTS = 32;
    static final int WE_HAVE_AN_X_AND_Y_SCALE = 64;
    static final int WE_HAVE_A_TWO_BY_TWO = 128;
    protected HashMap tableDirectory;
    protected RandomAccessFileOrArray rf;
    protected String fileName;
    protected boolean includeCmap;
    protected boolean includeExtras;
    protected boolean locaShortTable;
    protected int[] locaTable;
    protected HashMap glyphsUsed;
    protected ArrayList glyphsInList;
    protected int tableGlyphOffset;
    protected int[] newLocaTable;
    protected byte[] newLocaTableOut;
    protected byte[] newGlyfTable;
    protected int glyfTableRealSize;
    protected int locaTableRealSize;
    protected byte[] outFont;
    protected int fontPtr;
    protected int directoryOffset;

    TrueTypeFontSubSet(String string, RandomAccessFileOrArray randomAccessFileOrArray, HashMap hashMap, int n, boolean bl, boolean bl2) {
        this.fileName = string;
        this.rf = randomAccessFileOrArray;
        this.glyphsUsed = hashMap;
        this.includeCmap = bl;
        this.includeExtras = bl2;
        this.directoryOffset = n;
        this.glyphsInList = new ArrayList(hashMap.keySet());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    byte[] process() throws IOException, DocumentException {
        try {
            this.rf.reOpen();
            this.createTableDirectory();
            this.readLoca();
            this.flatGlyphs();
            this.createNewGlyphTables();
            this.locaTobytes();
            this.assembleFont();
            byte[] arrby = this.outFont;
            return arrby;
        }
        finally {
            try {
                this.rf.close();
            }
            catch (Exception var2_2) {}
        }
    }

    protected void assembleFont() throws IOException {
        int[] arrn;
        int n;
        int n2;
        String string;
        int n3 = 0;
        String[] arrstring = this.includeExtras ? tableNamesExtra : (this.includeCmap ? tableNamesCmap : tableNamesSimple);
        int n4 = 2;
        int n5 = 0;
        for (n = 0; n < arrstring.length; ++n) {
            String string2 = arrstring[n];
            if (string2.equals("glyf") || string2.equals("loca") || (arrn = (int[])this.tableDirectory.get(string2)) == null) continue;
            ++n4;
            n3 += arrn[2] + 3 & -4;
        }
        n3 += this.newLocaTableOut.length;
        n3 += this.newGlyfTable.length;
        n = 16 * n4 + 12;
        this.outFont = new byte[n3 += n];
        this.fontPtr = 0;
        this.writeFontInt(65536);
        this.writeFontShort(n4);
        int n6 = entrySelectors[n4];
        this.writeFontShort((1 << n6) * 16);
        this.writeFontShort(n6);
        this.writeFontShort((n4 - (1 << n6)) * 16);
        for (n2 = 0; n2 < arrstring.length; ++n2) {
            string = arrstring[n2];
            arrn = (int[])this.tableDirectory.get(string);
            if (arrn == null) continue;
            this.writeFontString(string);
            if (string.equals("glyf")) {
                this.writeFontInt(this.calculateChecksum(this.newGlyfTable));
                n5 = this.glyfTableRealSize;
            } else if (string.equals("loca")) {
                this.writeFontInt(this.calculateChecksum(this.newLocaTableOut));
                n5 = this.locaTableRealSize;
            } else {
                this.writeFontInt(arrn[0]);
                n5 = arrn[2];
            }
            this.writeFontInt(n);
            this.writeFontInt(n5);
            n += n5 + 3 & -4;
        }
        for (n2 = 0; n2 < arrstring.length; ++n2) {
            string = arrstring[n2];
            arrn = (int[])this.tableDirectory.get(string);
            if (arrn == null) continue;
            if (string.equals("glyf")) {
                System.arraycopy(this.newGlyfTable, 0, this.outFont, this.fontPtr, this.newGlyfTable.length);
                this.fontPtr += this.newGlyfTable.length;
                this.newGlyfTable = null;
                continue;
            }
            if (string.equals("loca")) {
                System.arraycopy(this.newLocaTableOut, 0, this.outFont, this.fontPtr, this.newLocaTableOut.length);
                this.fontPtr += this.newLocaTableOut.length;
                this.newLocaTableOut = null;
                continue;
            }
            this.rf.seek(arrn[1]);
            this.rf.readFully(this.outFont, this.fontPtr, arrn[2]);
            this.fontPtr += arrn[2] + 3 & -4;
        }
    }

    protected void createTableDirectory() throws IOException, DocumentException {
        this.tableDirectory = new HashMap();
        this.rf.seek(this.directoryOffset);
        int n = this.rf.readInt();
        if (n != 65536) {
            throw new DocumentException(this.fileName + " is not a true type file.");
        }
        int n2 = this.rf.readUnsignedShort();
        this.rf.skipBytes(6);
        for (int i = 0; i < n2; ++i) {
            String string = this.readStandardString(4);
            int[] arrn = new int[]{this.rf.readInt(), this.rf.readInt(), this.rf.readInt()};
            this.tableDirectory.put(string, arrn);
        }
    }

    protected void readLoca() throws IOException, DocumentException {
        int[] arrn = (int[])this.tableDirectory.get("head");
        if (arrn == null) {
            throw new DocumentException("Table 'head' does not exist in " + this.fileName);
        }
        this.rf.seek(arrn[1] + 51);
        this.locaShortTable = this.rf.readUnsignedShort() == 0;
        arrn = (int[])this.tableDirectory.get("loca");
        if (arrn == null) {
            throw new DocumentException("Table 'loca' does not exist in " + this.fileName);
        }
        this.rf.seek(arrn[1]);
        if (this.locaShortTable) {
            int n = arrn[2] / 2;
            this.locaTable = new int[n];
            for (int i = 0; i < n; ++i) {
                this.locaTable[i] = this.rf.readUnsignedShort() * 2;
            }
        } else {
            int n = arrn[2] / 4;
            this.locaTable = new int[n];
            for (int i = 0; i < n; ++i) {
                this.locaTable[i] = this.rf.readInt();
            }
        }
    }

    protected void createNewGlyphTables() throws IOException {
        int n;
        int n2;
        int n3;
        this.newLocaTable = new int[this.locaTable.length];
        int[] arrn = new int[this.glyphsInList.size()];
        for (n3 = 0; n3 < arrn.length; ++n3) {
            arrn[n3] = (Integer)this.glyphsInList.get(n3);
        }
        Arrays.sort(arrn);
        n3 = 0;
        for (n = 0; n < arrn.length; ++n) {
            n2 = arrn[n];
            n3 += this.locaTable[n2 + 1] - this.locaTable[n2];
        }
        this.glyfTableRealSize = n3;
        n3 = n3 + 3 & -4;
        this.newGlyfTable = new byte[n3];
        n = 0;
        n2 = 0;
        for (int i = 0; i < this.newLocaTable.length; ++i) {
            this.newLocaTable[i] = n;
            if (n2 >= arrn.length || arrn[n2] != i) continue;
            ++n2;
            this.newLocaTable[i] = n;
            int n4 = this.locaTable[i];
            int n5 = this.locaTable[i + 1] - n4;
            if (n5 <= 0) continue;
            this.rf.seek(this.tableGlyphOffset + n4);
            this.rf.readFully(this.newGlyfTable, n, n5);
            n += n5;
        }
    }

    protected void locaTobytes() {
        this.locaTableRealSize = this.locaShortTable ? this.newLocaTable.length * 2 : this.newLocaTable.length * 4;
        this.outFont = this.newLocaTableOut = new byte[this.locaTableRealSize + 3 & -4];
        this.fontPtr = 0;
        for (int i = 0; i < this.newLocaTable.length; ++i) {
            if (this.locaShortTable) {
                this.writeFontShort(this.newLocaTable[i] / 2);
                continue;
            }
            this.writeFontInt(this.newLocaTable[i]);
        }
    }

    protected void flatGlyphs() throws IOException, DocumentException {
        int[] arrn = (int[])this.tableDirectory.get("glyf");
        if (arrn == null) {
            throw new DocumentException("Table 'glyf' does not exist in " + this.fileName);
        }
        Integer n = new Integer(0);
        if (!this.glyphsUsed.containsKey(n)) {
            this.glyphsUsed.put(n, null);
            this.glyphsInList.add(n);
        }
        this.tableGlyphOffset = arrn[1];
        for (int i = 0; i < this.glyphsInList.size(); ++i) {
            int n2 = (Integer)this.glyphsInList.get(i);
            this.checkGlyphComposite(n2);
        }
    }

    protected void checkGlyphComposite(int n) throws IOException {
        int n2 = this.locaTable[n];
        if (n2 == this.locaTable[n + 1]) {
            return;
        }
        this.rf.seek(this.tableGlyphOffset + n2);
        short s = this.rf.readShort();
        if (s >= 0) {
            return;
        }
        this.rf.skipBytes(8);
        do {
            int n3 = this.rf.readUnsignedShort();
            Integer n4 = new Integer(this.rf.readUnsignedShort());
            if (!this.glyphsUsed.containsKey(n4)) {
                this.glyphsUsed.put(n4, null);
                this.glyphsInList.add(n4);
            }
            if ((n3 & 32) == 0) {
                return;
            }
            int n5 = (n3 & 1) != 0 ? 4 : 2;
            if ((n3 & 8) != 0) {
                n5 += 2;
            } else if ((n3 & 64) != 0) {
                n5 += 4;
            }
            if ((n3 & 128) != 0) {
                n5 += 8;
            }
            this.rf.skipBytes(n5);
        } while (true);
    }

    protected String readStandardString(int n) throws IOException {
        byte[] arrby = new byte[n];
        this.rf.readFully(arrby);
        try {
            return new String(arrby, "Cp1252");
        }
        catch (Exception var3_3) {
            throw new ExceptionConverter(var3_3);
        }
    }

    protected void writeFontShort(int n) {
        this.outFont[this.fontPtr++] = (byte)(n >> 8);
        this.outFont[this.fontPtr++] = (byte)n;
    }

    protected void writeFontInt(int n) {
        this.outFont[this.fontPtr++] = (byte)(n >> 24);
        this.outFont[this.fontPtr++] = (byte)(n >> 16);
        this.outFont[this.fontPtr++] = (byte)(n >> 8);
        this.outFont[this.fontPtr++] = (byte)n;
    }

    protected void writeFontString(String string) {
        byte[] arrby = PdfEncodings.convertToBytes(string, "Cp1252");
        System.arraycopy(arrby, 0, this.outFont, this.fontPtr, arrby.length);
        this.fontPtr += arrby.length;
    }

    protected int calculateChecksum(byte[] arrby) {
        int n = arrby.length / 4;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        for (int i = 0; i < n; ++i) {
            n5 += arrby[n6++] & 255;
            n4 += arrby[n6++] & 255;
            n3 += arrby[n6++] & 255;
            n2 += arrby[n6++] & 255;
        }
        return n2 + (n3 << 8) + (n4 << 16) + (n5 << 24);
    }
}

