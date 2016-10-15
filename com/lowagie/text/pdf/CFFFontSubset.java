/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.CFFFont;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class CFFFontSubset
extends CFFFont {
    static final String[] SubrsFunctions = new String[]{"RESERVED_0", "hstem", "RESERVED_2", "vstem", "vmoveto", "rlineto", "hlineto", "vlineto", "rrcurveto", "RESERVED_9", "callsubr", "return", "escape", "RESERVED_13", "endchar", "RESERVED_15", "RESERVED_16", "RESERVED_17", "hstemhm", "hintmask", "cntrmask", "rmoveto", "hmoveto", "vstemhm", "rcurveline", "rlinecurve", "vvcurveto", "hhcurveto", "shortint", "callgsubr", "vhcurveto", "hvcurveto"};
    static final String[] SubrsEscapeFuncs = new String[]{"RESERVED_0", "RESERVED_1", "RESERVED_2", "and", "or", "not", "RESERVED_6", "RESERVED_7", "RESERVED_8", "abs", "add", "sub", "div", "RESERVED_13", "neg", "eq", "RESERVED_16", "RESERVED_17", "drop", "RESERVED_19", "put", "get", "ifelse", "random", "mul", "RESERVED_25", "sqrt", "dup", "exch", "index", "roll", "RESERVED_31", "RESERVED_32", "RESERVED_33", "hflex", "flex", "hflex1", "flex1", "RESERVED_REST"};
    HashMap GlyphsUsed;
    ArrayList glyphsInList;
    HashMap FDArrayUsed = new HashMap();
    HashMap[] hSubrsUsed;
    ArrayList[] lSubrsUsed;
    HashMap hGSubrsUsed = new HashMap();
    ArrayList lGSubrsUsed = new ArrayList();
    HashMap hSubrsUsedNonCID = new HashMap();
    ArrayList lSubrsUsedNonCID = new ArrayList();
    byte[][] NewLSubrsIndex;
    byte[] NewSubrsIndexNonCID;
    byte[] NewGSubrsIndex;
    byte[] NewCharStringsIndex;
    int GBias = 0;
    LinkedList OutputList;
    int NumOfHints = 0;

    public CFFFontSubset(RandomAccessFileOrArray randomAccessFileOrArray, HashMap hashMap) {
        super(randomAccessFileOrArray);
        this.GlyphsUsed = hashMap;
        this.glyphsInList = new ArrayList(hashMap.keySet());
        for (int i = 0; i < this.fonts.length; ++i) {
            this.seek(this.fonts[i].charstringsOffset);
            this.fonts[i].nglyphs = this.getCard16();
            this.seek(this.stringIndexOffset);
            this.fonts[i].nstrings = this.getCard16() + standardStrings.length;
            this.fonts[i].charstringsOffsets = this.getIndex(this.fonts[i].charstringsOffset);
            if (this.fonts[i].fdselectOffset >= 0) {
                this.readFDSelect(i);
                this.BuildFDArrayUsed(i);
            }
            if (this.fonts[i].isCID) {
                this.ReadFDArray(i);
            }
            this.fonts[i].CharsetLength = this.CountCharset(this.fonts[i].charsetOffset, this.fonts[i].nglyphs);
        }
    }

    int CountCharset(int n, int n2) {
        int n3 = 0;
        this.seek(n);
        char c = this.getCard8();
        switch (c) {
            case '\u0000': {
                n3 = 1 + 2 * n2;
                break;
            }
            case '\u0001': {
                n3 = 1 + 3 * this.CountRange(n2, 1);
                break;
            }
            case '\u0002': {
                n3 = 1 + 4 * this.CountRange(n2, 2);
                break;
            }
        }
        return n3;
    }

    int CountRange(int n, int n2) {
        char c;
        int n3 = 0;
        for (int i = 1; i < n; i += c + '\u0001') {
            ++n3;
            char c2 = this.getCard16();
            c = n2 == 1 ? this.getCard8() : this.getCard16();
        }
        return n3;
    }

    protected void readFDSelect(int n) {
        int n2 = this.fonts[n].nglyphs;
        int[] arrn = new int[n2];
        this.seek(this.fonts[n].fdselectOffset);
        this.fonts[n].FDSelectFormat = this.getCard8();
        switch (this.fonts[n].FDSelectFormat) {
            case 0: {
                for (int i = 0; i < n2; ++i) {
                    arrn[i] = this.getCard8();
                }
                this.fonts[n].FDSelectLength = this.fonts[n].nglyphs + 1;
                break;
            }
            case 3: {
                int n3 = this.getCard16();
                int n4 = 0;
                char c = this.getCard16();
                for (int i = 0; i < n3; ++i) {
                    char c2 = this.getCard8();
                    char c3 = this.getCard16();
                    int n5 = c3 - c;
                    for (int j = 0; j < n5; ++j) {
                        arrn[n4] = c2;
                        ++n4;
                    }
                    c = c3;
                }
                this.fonts[n].FDSelectLength = 3 + n3 * 3 + 2;
                break;
            }
        }
        this.fonts[n].FDSelect = arrn;
    }

    protected void BuildFDArrayUsed(int n) {
        int[] arrn = this.fonts[n].FDSelect;
        for (int i = 0; i < this.glyphsInList.size(); ++i) {
            int n2 = (Integer)this.glyphsInList.get(i);
            int n3 = arrn[n2];
            this.FDArrayUsed.put(new Integer(n3), null);
        }
    }

    protected void ReadFDArray(int n) {
        this.seek(this.fonts[n].fdarrayOffset);
        this.fonts[n].FDArrayCount = this.getCard16();
        this.fonts[n].FDArrayOffsize = this.getCard8();
        if (this.fonts[n].FDArrayOffsize < 4) {
            ++this.fonts[n].FDArrayOffsize;
        }
        this.fonts[n].FDArrayOffsets = this.getIndex(this.fonts[n].fdarrayOffset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] Process(String string) throws IOException {
        try {
            byte[] arrby;
            int n;
            this.buf.reOpen();
            for (n = 0; n < this.fonts.length && !string.equals(this.fonts[n].name); ++n) {
            }
            if (n == this.fonts.length) {
                byte[] arrby2 = null;
                return arrby2;
            }
            if (this.gsubrIndexOffset >= 0) {
                this.GBias = this.CalcBias(this.gsubrIndexOffset, n);
            }
            this.BuildNewCharString(n);
            this.BuildNewLGSubrs(n);
            byte[] arrby3 = arrby = this.BuildNewFile(n);
            return arrby3;
        }
        finally {
            try {
                this.buf.close();
            }
            catch (Exception var4_5) {}
        }
    }

    protected int CalcBias(int n, int n2) {
        this.seek(n);
        char c = this.getCard16();
        if (this.fonts[n2].CharstringType == 1) {
            return 0;
        }
        if (c < '\u04d8') {
            return 107;
        }
        if (c < '\u846c') {
            return 1131;
        }
        return 32768;
    }

    protected void BuildNewCharString(int n) throws IOException {
        this.NewCharStringsIndex = this.BuildNewIndex(this.fonts[n].charstringsOffsets, this.GlyphsUsed);
    }

    protected void BuildNewLGSubrs(int n) throws IOException {
        if (this.fonts[n].isCID) {
            this.hSubrsUsed = new HashMap[this.fonts[n].fdprivateOffsets.length];
            this.lSubrsUsed = new ArrayList[this.fonts[n].fdprivateOffsets.length];
            this.NewLSubrsIndex = new byte[this.fonts[n].fdprivateOffsets.length][];
            this.fonts[n].PrivateSubrsOffset = new int[this.fonts[n].fdprivateOffsets.length];
            this.fonts[n].PrivateSubrsOffsetsArray = new int[this.fonts[n].fdprivateOffsets.length][];
            ArrayList arrayList = new ArrayList(this.FDArrayUsed.keySet());
            for (int i = 0; i < arrayList.size(); ++i) {
                int n2 = (Integer)arrayList.get(i);
                this.hSubrsUsed[n2] = new HashMap();
                this.lSubrsUsed[n2] = new ArrayList();
                this.BuildFDSubrsOffsets(n, n2);
                if (this.fonts[n].PrivateSubrsOffset[n2] < 0) continue;
                this.BuildSubrUsed(n, n2, this.fonts[n].PrivateSubrsOffset[n2], this.fonts[n].PrivateSubrsOffsetsArray[n2], this.hSubrsUsed[n2], this.lSubrsUsed[n2]);
                this.NewLSubrsIndex[n2] = this.BuildNewIndex(this.fonts[n].PrivateSubrsOffsetsArray[n2], this.hSubrsUsed[n2]);
            }
        } else if (this.fonts[n].privateSubrs >= 0) {
            this.fonts[n].SubrsOffsets = this.getIndex(this.fonts[n].privateSubrs);
            this.BuildSubrUsed(n, -1, this.fonts[n].privateSubrs, this.fonts[n].SubrsOffsets, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID);
        }
        this.BuildGSubrsUsed(n);
        if (this.fonts[n].privateSubrs >= 0) {
            this.NewSubrsIndexNonCID = this.BuildNewIndex(this.fonts[n].SubrsOffsets, this.hSubrsUsedNonCID);
        }
        this.NewGSubrsIndex = this.BuildNewIndex(this.gsubrOffsets, this.hGSubrsUsed);
    }

    protected void BuildFDSubrsOffsets(int n, int n2) {
        this.fonts[n].PrivateSubrsOffset[n2] = -1;
        this.seek(this.fonts[n].fdprivateOffsets[n2]);
        while (this.getPosition() < this.fonts[n].fdprivateOffsets[n2] + this.fonts[n].fdprivateLengths[n2]) {
            this.getDictItem();
            if (this.key != "Subrs") continue;
            this.fonts[n].PrivateSubrsOffset[n2] = (Integer)this.args[0] + this.fonts[n].fdprivateOffsets[n2];
        }
        if (this.fonts[n].PrivateSubrsOffset[n2] >= 0) {
            this.fonts[n].PrivateSubrsOffsetsArray[n2] = this.getIndex(this.fonts[n].PrivateSubrsOffset[n2]);
        }
    }

    protected void BuildSubrUsed(int n, int n2, int n3, int[] arrn, HashMap hashMap, ArrayList arrayList) {
        int n4;
        int n5;
        int n6;
        int n7;
        int n8 = this.CalcBias(n3, n);
        for (n6 = 0; n6 < this.glyphsInList.size(); ++n6) {
            n5 = (Integer)this.glyphsInList.get(n6);
            n4 = this.fonts[n].charstringsOffsets[n5];
            n7 = this.fonts[n].charstringsOffsets[n5 + 1];
            if (n2 >= 0) {
                this.EmptyStack();
                this.NumOfHints = 0;
                int n9 = this.fonts[n].FDSelect[n5];
                if (n9 != n2) continue;
                this.ReadASubr(n4, n7, this.GBias, n8, hashMap, arrayList, arrn);
                continue;
            }
            this.ReadASubr(n4, n7, this.GBias, n8, hashMap, arrayList, arrn);
        }
        for (n6 = 0; n6 < arrayList.size(); ++n6) {
            n5 = (Integer)arrayList.get(n6);
            if (n5 >= arrn.length - 1 || n5 < 0) continue;
            n4 = arrn[n5];
            n7 = arrn[n5 + 1];
            this.ReadASubr(n4, n7, this.GBias, n8, hashMap, arrayList, arrn);
        }
    }

    protected void BuildGSubrsUsed(int n) {
        int n2 = 0;
        int n3 = 0;
        if (this.fonts[n].privateSubrs >= 0) {
            n2 = this.CalcBias(this.fonts[n].privateSubrs, n);
            n3 = this.lSubrsUsedNonCID.size();
        }
        for (int i = 0; i < this.lGSubrsUsed.size(); ++i) {
            int n4 = (Integer)this.lGSubrsUsed.get(i);
            if (n4 >= this.gsubrOffsets.length - 1 || n4 < 0) continue;
            int n5 = this.gsubrOffsets[n4];
            int n6 = this.gsubrOffsets[n4 + 1];
            if (this.fonts[n].isCID) {
                this.ReadASubr(n5, n6, this.GBias, 0, this.hGSubrsUsed, this.lGSubrsUsed, null);
                continue;
            }
            this.ReadASubr(n5, n6, this.GBias, n2, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID, this.fonts[n].SubrsOffsets);
            if (n3 >= this.lSubrsUsedNonCID.size()) continue;
            for (int j = n3; j < this.lSubrsUsedNonCID.size(); ++j) {
                int n7 = (Integer)this.lSubrsUsedNonCID.get(j);
                if (n7 >= this.fonts[n].SubrsOffsets.length - 1 || n7 < 0) continue;
                int n8 = this.fonts[n].SubrsOffsets[n7];
                int n9 = this.fonts[n].SubrsOffsets[n7 + 1];
                this.ReadASubr(n8, n9, this.GBias, n2, this.hSubrsUsedNonCID, this.lSubrsUsedNonCID, this.fonts[n].SubrsOffsets);
            }
            n3 = this.lSubrsUsedNonCID.size();
        }
    }

    protected void ReadASubr(int n, int n2, int n3, int n4, HashMap hashMap, ArrayList arrayList, int[] arrn) {
        this.EmptyStack();
        this.NumOfHints = 0;
        this.seek(n);
        while (this.getPosition() < n2) {
            int n5;
            this.ReadCommand();
            int n6 = this.getPosition();
            Object object = null;
            if (this.arg_count > 0) {
                object = this.args[this.arg_count - 1];
            }
            int n7 = this.arg_count;
            this.HandelStack();
            if (this.key == "callsubr") {
                if (n7 <= 0) continue;
                n5 = (Integer)object + n4;
                if (!hashMap.containsKey(new Integer(n5))) {
                    hashMap.put(new Integer(n5), null);
                    arrayList.add(new Integer(n5));
                }
                this.CalcHints(arrn[n5], arrn[n5 + 1], n4, n3, arrn);
                this.seek(n6);
                continue;
            }
            if (this.key == "callgsubr") {
                if (n7 <= 0) continue;
                n5 = (Integer)object + n3;
                if (!this.hGSubrsUsed.containsKey(new Integer(n5))) {
                    this.hGSubrsUsed.put(new Integer(n5), null);
                    this.lGSubrsUsed.add(new Integer(n5));
                }
                this.CalcHints(this.gsubrOffsets[n5], this.gsubrOffsets[n5 + 1], n4, n3, arrn);
                this.seek(n6);
                continue;
            }
            if (this.key == "hstem" || this.key == "vstem" || this.key == "hstemhm" || this.key == "vstemhm") {
                this.NumOfHints += n7 / 2;
                continue;
            }
            if (this.key != "hintmask" && this.key != "cntrmask") continue;
            n5 = this.NumOfHints / 8;
            if (this.NumOfHints % 8 != 0 || n5 == 0) {
                ++n5;
            }
            for (int i = 0; i < n5; ++i) {
                this.getCard8();
            }
        }
    }

    protected void HandelStack() {
        int n = this.StackOpp();
        if (n < 2) {
            if (n == 1) {
                this.PushStack();
            } else {
                for (int i = 0; i < (n *= -1); ++i) {
                    this.PopStack();
                }
            }
        } else {
            this.EmptyStack();
        }
    }

    protected int StackOpp() {
        if (this.key == "ifelse") {
            return -3;
        }
        if (this.key == "roll" || this.key == "put") {
            return -2;
        }
        if (this.key == "callsubr" || this.key == "callgsubr" || this.key == "add" || this.key == "sub" || this.key == "div" || this.key == "mul" || this.key == "drop" || this.key == "and" || this.key == "or" || this.key == "eq") {
            return -1;
        }
        if (this.key == "abs" || this.key == "neg" || this.key == "sqrt" || this.key == "exch" || this.key == "index" || this.key == "get" || this.key == "not" || this.key == "return") {
            return 0;
        }
        if (this.key == "random" || this.key == "dup") {
            return 1;
        }
        return 2;
    }

    protected void EmptyStack() {
        for (int i = 0; i < this.arg_count; ++i) {
            this.args[i] = null;
        }
        this.arg_count = 0;
    }

    protected void PopStack() {
        if (this.arg_count > 0) {
            this.args[this.arg_count - 1] = null;
            --this.arg_count;
        }
    }

    protected void PushStack() {
        ++this.arg_count;
    }

    protected void ReadCommand() {
        this.key = null;
        boolean bl = false;
        while (!bl) {
            char c;
            int n;
            char c2 = this.getCard8();
            if (c2 == '\u001c') {
                n = this.getCard8();
                c = this.getCard8();
                this.args[this.arg_count] = new Integer(n << 8 | c);
                ++this.arg_count;
                continue;
            }
            if (c2 >= ' ' && c2 <= '\u00f6') {
                this.args[this.arg_count] = new Integer(c2 - 139);
                ++this.arg_count;
                continue;
            }
            if (c2 >= '\u00f7' && c2 <= '\u00fa') {
                n = this.getCard8();
                this.args[this.arg_count] = new Integer((c2 - 247) * 256 + n + 108);
                ++this.arg_count;
                continue;
            }
            if (c2 >= '\u00fb' && c2 <= '\u00fe') {
                n = this.getCard8();
                this.args[this.arg_count] = new Integer((- c2 - 251) * 256 - n - 108);
                ++this.arg_count;
                continue;
            }
            if (c2 == '\u00ff') {
                n = this.getCard8();
                c = this.getCard8();
                char c3 = this.getCard8();
                char c4 = this.getCard8();
                this.args[this.arg_count] = new Integer(n << 24 | c << 16 | c3 << 8 | c4);
                ++this.arg_count;
                continue;
            }
            if (c2 > '\u001f' || c2 == '\u001c') continue;
            bl = true;
            if (c2 == '\f') {
                n = this.getCard8();
                if (n > SubrsEscapeFuncs.length - 1) {
                    n = SubrsEscapeFuncs.length - 1;
                }
                this.key = SubrsEscapeFuncs[n];
                continue;
            }
            this.key = SubrsFunctions[c2];
        }
    }

    protected int CalcHints(int n, int n2, int n3, int n4, int[] arrn) {
        this.seek(n);
        while (this.getPosition() < n2) {
            int n5;
            this.ReadCommand();
            int n6 = this.getPosition();
            Object object = null;
            if (this.arg_count > 0) {
                object = this.args[this.arg_count - 1];
            }
            int n7 = this.arg_count;
            this.HandelStack();
            if (this.key == "callsubr") {
                if (n7 <= 0) continue;
                n5 = (Integer)object + n3;
                this.CalcHints(arrn[n5], arrn[n5 + 1], n3, n4, arrn);
                this.seek(n6);
                continue;
            }
            if (this.key == "callgsubr") {
                if (n7 <= 0) continue;
                n5 = (Integer)object + n4;
                this.CalcHints(this.gsubrOffsets[n5], this.gsubrOffsets[n5 + 1], n3, n4, arrn);
                this.seek(n6);
                continue;
            }
            if (this.key == "hstem" || this.key == "vstem" || this.key == "hstemhm" || this.key == "vstemhm") {
                this.NumOfHints += n7 / 2;
                continue;
            }
            if (this.key != "hintmask" && this.key != "cntrmask") continue;
            n5 = this.NumOfHints / 8;
            if (this.NumOfHints % 8 != 0 || n5 == 0) {
                ++n5;
            }
            for (int i = 0; i < n5; ++i) {
                this.getCard8();
            }
        }
        return this.NumOfHints;
    }

    protected byte[] BuildNewIndex(int[] arrn, HashMap hashMap) throws IOException {
        int n = 0;
        int[] arrn2 = new int[arrn.length];
        for (int i = 0; i < arrn.length; ++i) {
            arrn2[i] = n;
            if (!hashMap.containsKey(new Integer(i))) continue;
            n += arrn[i + 1] - arrn[i];
        }
        byte[] arrby = new byte[n];
        for (int j = 0; j < arrn.length - 1; ++j) {
            int n2 = arrn2[j];
            int n3 = arrn2[j + 1];
            if (n2 == n3) continue;
            this.buf.seek(arrn[j]);
            this.buf.readFully(arrby, n2, n3 - n2);
        }
        return this.AssembleIndex(arrn2, arrby);
    }

    protected byte[] AssembleIndex(int[] arrn, byte[] arrby) {
        int n;
        char c = (char)(arrn.length - 1);
        int n2 = arrn[arrn.length - 1];
        int n3 = n2 <= 255 ? 1 : (n2 <= 65535 ? 2 : (n2 <= 16777215 ? 3 : 4));
        byte[] arrby2 = new byte[3 + n3 * (c + '\u0001') + arrby.length];
        int n4 = 0;
        arrby2[n4++] = (byte)(c >>> 8 & 255);
        arrby2[n4++] = (byte)(c >>> 0 & 255);
        arrby2[n4++] = n3;
        for (n = 0; n < arrn.length; ++n) {
            int n5 = arrn[n] - arrn[0] + 1;
            switch (n3) {
                case 4: {
                    arrby2[n4++] = (byte)(n5 >>> 24 & 255);
                }
                case 3: {
                    arrby2[n4++] = (byte)(n5 >>> 16 & 255);
                }
                case 2: {
                    arrby2[n4++] = (byte)(n5 >>> 8 & 255);
                }
                case 1: {
                    arrby2[n4++] = (byte)(n5 >>> 0 & 255);
                }
            }
        }
        for (n = 0; n < arrby.length; ++n) {
            arrby2[n4++] = arrby[n];
        }
        return arrby2;
    }

    protected byte[] BuildNewFile(int n) {
        CFFFont.Item item;
        Iterator iterator;
        Object object;
        this.OutputList = new LinkedList();
        this.CopyHeader();
        this.BuildIndexHeader(1, 1, 1);
        this.OutputList.addLast(new CFFFont.UInt8Item((char)(1 + this.fonts[n].name.length())));
        this.OutputList.addLast(new CFFFont.StringItem(this.fonts[n].name));
        this.BuildIndexHeader(1, 2, 1);
        CFFFont.IndexOffsetItem indexOffsetItem = new CFFFont.IndexOffsetItem(2);
        this.OutputList.addLast(indexOffsetItem);
        CFFFont.IndexBaseItem indexBaseItem = new CFFFont.IndexBaseItem();
        this.OutputList.addLast(indexBaseItem);
        CFFFont.DictOffsetItem dictOffsetItem = new CFFFont.DictOffsetItem();
        CFFFont.DictOffsetItem dictOffsetItem2 = new CFFFont.DictOffsetItem();
        CFFFont.DictOffsetItem dictOffsetItem3 = new CFFFont.DictOffsetItem();
        CFFFont.DictOffsetItem dictOffsetItem4 = new CFFFont.DictOffsetItem();
        CFFFont.DictOffsetItem dictOffsetItem5 = new CFFFont.DictOffsetItem();
        if (!this.fonts[n].isCID) {
            this.OutputList.addLast(new CFFFont.DictNumberItem(this.fonts[n].nstrings));
            this.OutputList.addLast(new CFFFont.DictNumberItem(this.fonts[n].nstrings + 1));
            this.OutputList.addLast(new CFFFont.DictNumberItem(0));
            this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
            this.OutputList.addLast(new CFFFont.UInt8Item('\u001e'));
            this.OutputList.addLast(new CFFFont.DictNumberItem(this.fonts[n].nglyphs));
            this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
            this.OutputList.addLast(new CFFFont.UInt8Item('\"'));
        }
        this.seek(this.topdictOffsets[n]);
        while (this.getPosition() < this.topdictOffsets[n + 1]) {
            object = this.getPosition();
            this.getDictItem();
            iterator = this.getPosition();
            if (this.key == "Encoding" || this.key == "Private" || this.key == "FDSelect" || this.key == "FDArray" || this.key == "charset" || this.key == "CharStrings") continue;
            this.OutputList.add(new CFFFont.RangeItem(this.buf, (int)object, iterator - object));
        }
        this.CreateKeys(dictOffsetItem3, dictOffsetItem4, dictOffsetItem, dictOffsetItem2);
        this.OutputList.addLast(new CFFFont.IndexMarkerItem(indexOffsetItem, indexBaseItem));
        if (this.fonts[n].isCID) {
            this.OutputList.addLast(this.getEntireIndexRange(this.stringIndexOffset));
        } else {
            this.CreateNewStringIndex(n);
        }
        this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewGSubrsIndex), 0, this.NewGSubrsIndex.length));
        if (this.fonts[n].isCID) {
            this.OutputList.addLast(new CFFFont.MarkerItem(dictOffsetItem4));
            if (this.fonts[n].fdselectOffset >= 0) {
                this.OutputList.addLast(new CFFFont.RangeItem(this.buf, this.fonts[n].fdselectOffset, this.fonts[n].FDSelectLength));
            } else {
                this.CreateFDSelect(dictOffsetItem4, this.fonts[n].nglyphs);
            }
            this.OutputList.addLast(new CFFFont.MarkerItem(dictOffsetItem));
            this.OutputList.addLast(new CFFFont.RangeItem(this.buf, this.fonts[n].charsetOffset, this.fonts[n].CharsetLength));
            if (this.fonts[n].fdarrayOffset >= 0) {
                this.OutputList.addLast(new CFFFont.MarkerItem(dictOffsetItem3));
                this.Reconstruct(n);
            } else {
                this.CreateFDArray(dictOffsetItem3, dictOffsetItem5, n);
            }
        } else {
            this.CreateFDSelect(dictOffsetItem4, this.fonts[n].nglyphs);
            this.CreateCharset(dictOffsetItem, this.fonts[n].nglyphs);
            this.CreateFDArray(dictOffsetItem3, dictOffsetItem5, n);
        }
        if (this.fonts[n].privateOffset >= 0) {
            CFFFont.IndexBaseItem indexBaseItem2 = new CFFFont.IndexBaseItem();
            this.OutputList.addLast(indexBaseItem2);
            this.OutputList.addLast(new CFFFont.MarkerItem(dictOffsetItem5));
            CFFFont.DictOffsetItem dictOffsetItem6 = new CFFFont.DictOffsetItem();
            this.CreateNonCIDPrivate(n, dictOffsetItem6);
            this.CreateNonCIDSubrs(n, indexBaseItem2, dictOffsetItem6);
        }
        this.OutputList.addLast(new CFFFont.MarkerItem(dictOffsetItem2));
        this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewCharStringsIndex), 0, this.NewCharStringsIndex.length));
        object = new int[1];
        object[0] = false;
        iterator = this.OutputList.iterator();
        while (iterator.hasNext()) {
            item = (CFFFont.Item)iterator.next();
            item.increment((int[])object);
        }
        iterator = this.OutputList.iterator();
        while (iterator.hasNext()) {
            item = (CFFFont.Item)iterator.next();
            item.xref();
        }
        Object object2 = object[0];
        byte[] arrby = new byte[object2];
        iterator = this.OutputList.iterator();
        while (iterator.hasNext()) {
            CFFFont.Item item2 = (CFFFont.Item)iterator.next();
            item2.emit(arrby);
        }
        return arrby;
    }

    protected void CopyHeader() {
        this.seek(0);
        char c = this.getCard8();
        char c2 = this.getCard8();
        char c3 = this.getCard8();
        char c4 = this.getCard8();
        this.nextIndexOffset = c3;
        this.OutputList.addLast(new CFFFont.RangeItem(this.buf, 0, c3));
    }

    protected void BuildIndexHeader(int n, int n2, int n3) {
        this.OutputList.addLast(new CFFFont.UInt16Item((char)n));
        this.OutputList.addLast(new CFFFont.UInt8Item((char)n2));
        switch (n2) {
            case 1: {
                this.OutputList.addLast(new CFFFont.UInt8Item((char)n3));
                break;
            }
            case 2: {
                this.OutputList.addLast(new CFFFont.UInt16Item((char)n3));
                break;
            }
            case 3: {
                this.OutputList.addLast(new CFFFont.UInt24Item((char)n3));
                break;
            }
            case 4: {
                this.OutputList.addLast(new CFFFont.UInt32Item((char)n3));
                break;
            }
        }
    }

    protected void CreateKeys(CFFFont.OffsetItem offsetItem, CFFFont.OffsetItem offsetItem2, CFFFont.OffsetItem offsetItem3, CFFFont.OffsetItem offsetItem4) {
        this.OutputList.addLast(offsetItem);
        this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
        this.OutputList.addLast(new CFFFont.UInt8Item('$'));
        this.OutputList.addLast(offsetItem2);
        this.OutputList.addLast(new CFFFont.UInt8Item('\f'));
        this.OutputList.addLast(new CFFFont.UInt8Item('%'));
        this.OutputList.addLast(offsetItem3);
        this.OutputList.addLast(new CFFFont.UInt8Item('\u000f'));
        this.OutputList.addLast(offsetItem4);
        this.OutputList.addLast(new CFFFont.UInt8Item('\u0011'));
    }

    protected void CreateNewStringIndex(int n) {
        int n2;
        String string = this.fonts[n].name + "-OneRange";
        if (string.length() > 127) {
            string = string.substring(0, 127);
        }
        String string2 = "AdobeIdentity" + string;
        int n3 = this.stringOffsets[this.stringOffsets.length - 1] - this.stringOffsets[0];
        int n4 = this.stringOffsets[0] - 1;
        int n5 = n3 + string2.length() <= 255 ? 1 : (n3 + string2.length() <= 65535 ? 2 : (n3 + string2.length() <= 16777215 ? 3 : 4));
        this.OutputList.addLast(new CFFFont.UInt16Item((char)(this.stringOffsets.length - 1 + 3)));
        this.OutputList.addLast(new CFFFont.UInt8Item((char)n5));
        for (n2 = 0; n2 < this.stringOffsets.length; ++n2) {
            this.OutputList.addLast(new CFFFont.IndexOffsetItem(n5, this.stringOffsets[n2] - n4));
        }
        n2 = this.stringOffsets[this.stringOffsets.length - 1] - n4;
        this.OutputList.addLast(new CFFFont.IndexOffsetItem(n5, n2 += "Adobe".length()));
        this.OutputList.addLast(new CFFFont.IndexOffsetItem(n5, n2 += "Identity".length()));
        this.OutputList.addLast(new CFFFont.IndexOffsetItem(n5, n2 += string.length()));
        this.OutputList.addLast(new CFFFont.RangeItem(this.buf, this.stringOffsets[0], n3));
        this.OutputList.addLast(new CFFFont.StringItem(string2));
    }

    protected void CreateFDSelect(CFFFont.OffsetItem offsetItem, int n) {
        this.OutputList.addLast(new CFFFont.MarkerItem(offsetItem));
        this.OutputList.addLast(new CFFFont.UInt8Item('\u0003'));
        this.OutputList.addLast(new CFFFont.UInt16Item('\u0001'));
        this.OutputList.addLast(new CFFFont.UInt16Item('\u0000'));
        this.OutputList.addLast(new CFFFont.UInt8Item('\u0000'));
        this.OutputList.addLast(new CFFFont.UInt16Item((char)n));
    }

    protected void CreateCharset(CFFFont.OffsetItem offsetItem, int n) {
        this.OutputList.addLast(new CFFFont.MarkerItem(offsetItem));
        this.OutputList.addLast(new CFFFont.UInt8Item('\u0002'));
        this.OutputList.addLast(new CFFFont.UInt16Item('\u0001'));
        this.OutputList.addLast(new CFFFont.UInt16Item((char)(n - 1)));
    }

    protected void CreateFDArray(CFFFont.OffsetItem offsetItem, CFFFont.OffsetItem offsetItem2, int n) {
        this.OutputList.addLast(new CFFFont.MarkerItem(offsetItem));
        this.BuildIndexHeader(1, 1, 1);
        CFFFont.IndexOffsetItem indexOffsetItem = new CFFFont.IndexOffsetItem(1);
        this.OutputList.addLast(indexOffsetItem);
        CFFFont.IndexBaseItem indexBaseItem = new CFFFont.IndexBaseItem();
        this.OutputList.addLast(indexBaseItem);
        int n2 = this.fonts[n].privateLength;
        int n3 = this.CalcSubrOffsetSize(this.fonts[n].privateOffset, this.fonts[n].privateLength);
        if (n3 != 0) {
            n2 += 5 - n3;
        }
        this.OutputList.addLast(new CFFFont.DictNumberItem(n2));
        this.OutputList.addLast(offsetItem2);
        this.OutputList.addLast(new CFFFont.UInt8Item('\u0012'));
        this.OutputList.addLast(new CFFFont.IndexMarkerItem(indexOffsetItem, indexBaseItem));
    }

    void Reconstruct(int n) {
        CFFFont.OffsetItem[] arroffsetItem = new CFFFont.DictOffsetItem[this.fonts[n].FDArrayOffsets.length - 1];
        CFFFont.IndexBaseItem[] arrindexBaseItem = new CFFFont.IndexBaseItem[this.fonts[n].fdprivateOffsets.length];
        CFFFont.OffsetItem[] arroffsetItem2 = new CFFFont.DictOffsetItem[this.fonts[n].fdprivateOffsets.length];
        this.ReconstructFDArray(n, arroffsetItem);
        this.ReconstructPrivateDict(n, arroffsetItem, arrindexBaseItem, arroffsetItem2);
        this.ReconstructPrivateSubrs(n, arrindexBaseItem, arroffsetItem2);
    }

    void ReconstructFDArray(int n, CFFFont.OffsetItem[] arroffsetItem) {
        this.BuildIndexHeader(this.fonts[n].FDArrayCount, this.fonts[n].FDArrayOffsize, 1);
        CFFFont.IndexOffsetItem[] arrindexOffsetItem = new CFFFont.IndexOffsetItem[this.fonts[n].FDArrayOffsets.length - 1];
        for (int i = 0; i < this.fonts[n].FDArrayOffsets.length - 1; ++i) {
            arrindexOffsetItem[i] = new CFFFont.IndexOffsetItem(this.fonts[n].FDArrayOffsize);
            this.OutputList.addLast(arrindexOffsetItem[i]);
        }
        CFFFont.IndexBaseItem indexBaseItem = new CFFFont.IndexBaseItem();
        this.OutputList.addLast(indexBaseItem);
        for (int j = 0; j < this.fonts[n].FDArrayOffsets.length - 1; ++j) {
            if (this.FDArrayUsed.containsKey(new Integer(j))) {
                this.seek(this.fonts[n].FDArrayOffsets[j]);
                while (this.getPosition() < this.fonts[n].FDArrayOffsets[j + 1]) {
                    int n2 = this.getPosition();
                    this.getDictItem();
                    int n3 = this.getPosition();
                    if (this.key == "Private") {
                        int n4 = (Integer)this.args[0];
                        int n5 = this.CalcSubrOffsetSize(this.fonts[n].fdprivateOffsets[j], this.fonts[n].fdprivateLengths[j]);
                        if (n5 != 0) {
                            n4 += 5 - n5;
                        }
                        this.OutputList.addLast(new CFFFont.DictNumberItem(n4));
                        arroffsetItem[j] = new CFFFont.DictOffsetItem();
                        this.OutputList.addLast(arroffsetItem[j]);
                        this.OutputList.addLast(new CFFFont.UInt8Item('\u0012'));
                        this.seek(n3);
                        continue;
                    }
                    this.OutputList.addLast(new CFFFont.RangeItem(this.buf, n2, n3 - n2));
                }
            }
            this.OutputList.addLast(new CFFFont.IndexMarkerItem(arrindexOffsetItem[j], indexBaseItem));
        }
    }

    void ReconstructPrivateDict(int n, CFFFont.OffsetItem[] arroffsetItem, CFFFont.IndexBaseItem[] arrindexBaseItem, CFFFont.OffsetItem[] arroffsetItem2) {
        for (int i = 0; i < this.fonts[n].fdprivateOffsets.length; ++i) {
            if (!this.FDArrayUsed.containsKey(new Integer(i))) continue;
            this.OutputList.addLast(new CFFFont.MarkerItem(arroffsetItem[i]));
            arrindexBaseItem[i] = new CFFFont.IndexBaseItem();
            this.OutputList.addLast(arrindexBaseItem[i]);
            this.seek(this.fonts[n].fdprivateOffsets[i]);
            while (this.getPosition() < this.fonts[n].fdprivateOffsets[i] + this.fonts[n].fdprivateLengths[i]) {
                int n2 = this.getPosition();
                this.getDictItem();
                int n3 = this.getPosition();
                if (this.key == "Subrs") {
                    arroffsetItem2[i] = new CFFFont.DictOffsetItem();
                    this.OutputList.addLast(arroffsetItem2[i]);
                    this.OutputList.addLast(new CFFFont.UInt8Item('\u0013'));
                    continue;
                }
                this.OutputList.addLast(new CFFFont.RangeItem(this.buf, n2, n3 - n2));
            }
        }
    }

    void ReconstructPrivateSubrs(int n, CFFFont.IndexBaseItem[] arrindexBaseItem, CFFFont.OffsetItem[] arroffsetItem) {
        for (int i = 0; i < this.fonts[n].fdprivateLengths.length; ++i) {
            if (arroffsetItem[i] == null || this.fonts[n].PrivateSubrsOffset[i] < 0) continue;
            this.OutputList.addLast(new CFFFont.SubrMarkerItem(arroffsetItem[i], arrindexBaseItem[i]));
            this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewLSubrsIndex[i]), 0, this.NewLSubrsIndex[i].length));
        }
    }

    int CalcSubrOffsetSize(int n, int n2) {
        int n3 = 0;
        this.seek(n);
        while (this.getPosition() < n + n2) {
            int n4 = this.getPosition();
            this.getDictItem();
            int n5 = this.getPosition();
            if (this.key != "Subrs") continue;
            n3 = n5 - n4 - 1;
        }
        return n3;
    }

    protected int countEntireIndexRange(int n) {
        this.seek(n);
        char c = this.getCard16();
        if (c == '\u0000') {
            return 2;
        }
        char c2 = this.getCard8();
        this.seek(n + 2 + 1 + c * c2);
        int n2 = this.getOffset(c2) - 1;
        return 3 + (c + '\u0001') * c2 + n2;
    }

    void CreateNonCIDPrivate(int n, CFFFont.OffsetItem offsetItem) {
        this.seek(this.fonts[n].privateOffset);
        while (this.getPosition() < this.fonts[n].privateOffset + this.fonts[n].privateLength) {
            int n2 = this.getPosition();
            this.getDictItem();
            int n3 = this.getPosition();
            if (this.key == "Subrs") {
                this.OutputList.addLast(offsetItem);
                this.OutputList.addLast(new CFFFont.UInt8Item('\u0013'));
                continue;
            }
            this.OutputList.addLast(new CFFFont.RangeItem(this.buf, n2, n3 - n2));
        }
    }

    void CreateNonCIDSubrs(int n, CFFFont.IndexBaseItem indexBaseItem, CFFFont.OffsetItem offsetItem) {
        this.OutputList.addLast(new CFFFont.SubrMarkerItem(offsetItem, indexBaseItem));
        this.OutputList.addLast(new CFFFont.RangeItem(new RandomAccessFileOrArray(this.NewSubrsIndexNonCID), 0, this.NewSubrsIndexNonCID.length));
    }
}

