/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.util.Iterator;
import java.util.LinkedList;

public class CFFFont {
    static final String[] operatorNames = new String[]{"version", "Notice", "FullName", "FamilyName", "Weight", "FontBBox", "BlueValues", "OtherBlues", "FamilyBlues", "FamilyOtherBlues", "StdHW", "StdVW", "UNKNOWN_12", "UniqueID", "XUID", "charset", "Encoding", "CharStrings", "Private", "Subrs", "defaultWidthX", "nominalWidthX", "UNKNOWN_22", "UNKNOWN_23", "UNKNOWN_24", "UNKNOWN_25", "UNKNOWN_26", "UNKNOWN_27", "UNKNOWN_28", "UNKNOWN_29", "UNKNOWN_30", "UNKNOWN_31", "Copyright", "isFixedPitch", "ItalicAngle", "UnderlinePosition", "UnderlineThickness", "PaintType", "CharstringType", "FontMatrix", "StrokeWidth", "BlueScale", "BlueShift", "BlueFuzz", "StemSnapH", "StemSnapV", "ForceBold", "UNKNOWN_12_15", "UNKNOWN_12_16", "LanguageGroup", "ExpansionFactor", "initialRandomSeed", "SyntheticBase", "PostScript", "BaseFontName", "BaseFontBlend", "UNKNOWN_12_24", "UNKNOWN_12_25", "UNKNOWN_12_26", "UNKNOWN_12_27", "UNKNOWN_12_28", "UNKNOWN_12_29", "ROS", "CIDFontVersion", "CIDFontRevision", "CIDFontType", "CIDCount", "UIDBase", "FDArray", "FDSelect", "FontName"};
    static final String[] standardStrings = new String[]{".notdef", "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quoteright", "parenleft", "parenright", "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon", "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "bracketleft", "backslash", "bracketright", "asciicircum", "underscore", "quoteleft", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "braceleft", "bar", "braceright", "asciitilde", "exclamdown", "cent", "sterling", "fraction", "yen", "florin", "section", "currency", "quotesingle", "quotedblleft", "guillemotleft", "guilsinglleft", "guilsinglright", "fi", "fl", "endash", "dagger", "daggerdbl", "periodcentered", "paragraph", "bullet", "quotesinglbase", "quotedblbase", "quotedblright", "guillemotright", "ellipsis", "perthousand", "questiondown", "grave", "acute", "circumflex", "tilde", "macron", "breve", "dotaccent", "dieresis", "ring", "cedilla", "hungarumlaut", "ogonek", "caron", "emdash", "AE", "ordfeminine", "Lslash", "Oslash", "OE", "ordmasculine", "ae", "dotlessi", "lslash", "oslash", "oe", "germandbls", "onesuperior", "logicalnot", "mu", "trademark", "Eth", "onehalf", "plusminus", "Thorn", "onequarter", "divide", "brokenbar", "degree", "thorn", "threequarters", "twosuperior", "registered", "minus", "eth", "multiply", "threesuperior", "copyright", "Aacute", "Acircumflex", "Adieresis", "Agrave", "Aring", "Atilde", "Ccedilla", "Eacute", "Ecircumflex", "Edieresis", "Egrave", "Iacute", "Icircumflex", "Idieresis", "Igrave", "Ntilde", "Oacute", "Ocircumflex", "Odieresis", "Ograve", "Otilde", "Scaron", "Uacute", "Ucircumflex", "Udieresis", "Ugrave", "Yacute", "Ydieresis", "Zcaron", "aacute", "acircumflex", "adieresis", "agrave", "aring", "atilde", "ccedilla", "eacute", "ecircumflex", "edieresis", "egrave", "iacute", "icircumflex", "idieresis", "igrave", "ntilde", "oacute", "ocircumflex", "odieresis", "ograve", "otilde", "scaron", "uacute", "ucircumflex", "udieresis", "ugrave", "yacute", "ydieresis", "zcaron", "exclamsmall", "Hungarumlautsmall", "dollaroldstyle", "dollarsuperior", "ampersandsmall", "Acutesmall", "parenleftsuperior", "parenrightsuperior", "twodotenleader", "onedotenleader", "zerooldstyle", "oneoldstyle", "twooldstyle", "threeoldstyle", "fouroldstyle", "fiveoldstyle", "sixoldstyle", "sevenoldstyle", "eightoldstyle", "nineoldstyle", "commasuperior", "threequartersemdash", "periodsuperior", "questionsmall", "asuperior", "bsuperior", "centsuperior", "dsuperior", "esuperior", "isuperior", "lsuperior", "msuperior", "nsuperior", "osuperior", "rsuperior", "ssuperior", "tsuperior", "ff", "ffi", "ffl", "parenleftinferior", "parenrightinferior", "Circumflexsmall", "hyphensuperior", "Gravesmall", "Asmall", "Bsmall", "Csmall", "Dsmall", "Esmall", "Fsmall", "Gsmall", "Hsmall", "Ismall", "Jsmall", "Ksmall", "Lsmall", "Msmall", "Nsmall", "Osmall", "Psmall", "Qsmall", "Rsmall", "Ssmall", "Tsmall", "Usmall", "Vsmall", "Wsmall", "Xsmall", "Ysmall", "Zsmall", "colonmonetary", "onefitted", "rupiah", "Tildesmall", "exclamdownsmall", "centoldstyle", "Lslashsmall", "Scaronsmall", "Zcaronsmall", "Dieresissmall", "Brevesmall", "Caronsmall", "Dotaccentsmall", "Macronsmall", "figuredash", "hypheninferior", "Ogoneksmall", "Ringsmall", "Cedillasmall", "questiondownsmall", "oneeighth", "threeeighths", "fiveeighths", "seveneighths", "onethird", "twothirds", "zerosuperior", "foursuperior", "fivesuperior", "sixsuperior", "sevensuperior", "eightsuperior", "ninesuperior", "zeroinferior", "oneinferior", "twoinferior", "threeinferior", "fourinferior", "fiveinferior", "sixinferior", "seveninferior", "eightinferior", "nineinferior", "centinferior", "dollarinferior", "periodinferior", "commainferior", "Agravesmall", "Aacutesmall", "Acircumflexsmall", "Atildesmall", "Adieresissmall", "Aringsmall", "AEsmall", "Ccedillasmall", "Egravesmall", "Eacutesmall", "Ecircumflexsmall", "Edieresissmall", "Igravesmall", "Iacutesmall", "Icircumflexsmall", "Idieresissmall", "Ethsmall", "Ntildesmall", "Ogravesmall", "Oacutesmall", "Ocircumflexsmall", "Otildesmall", "Odieresissmall", "OEsmall", "Oslashsmall", "Ugravesmall", "Uacutesmall", "Ucircumflexsmall", "Udieresissmall", "Yacutesmall", "Thornsmall", "Ydieresissmall", "001.000", "001.001", "001.002", "001.003", "Black", "Bold", "Book", "Light", "Medium", "Regular", "Roman", "Semibold"};
    int nextIndexOffset;
    protected String key;
    protected Object[] args = new Object[48];
    protected int arg_count = 0;
    protected RandomAccessFileOrArray buf;
    private int offSize;
    protected int nameIndexOffset;
    protected int topdictIndexOffset;
    protected int stringIndexOffset;
    protected int gsubrIndexOffset;
    protected int[] nameOffsets;
    protected int[] topdictOffsets;
    protected int[] stringOffsets;
    protected int[] gsubrOffsets;
    protected Font[] fonts;

    public String getString(char c) {
        if (c < standardStrings.length) {
            return standardStrings[c];
        }
        if (c >= standardStrings.length + (this.stringOffsets.length - 1)) {
            return null;
        }
        int n = c - standardStrings.length;
        int n2 = this.getPosition();
        this.seek(this.stringOffsets[n]);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = this.stringOffsets[n]; i < this.stringOffsets[n + 1]; ++i) {
            stringBuffer.append(this.getCard8());
        }
        this.seek(n2);
        return stringBuffer.toString();
    }

    char getCard8() {
        try {
            byte by = this.buf.readByte();
            return (char)(by & 255);
        }
        catch (Exception var1_2) {
            throw new ExceptionConverter(var1_2);
        }
    }

    char getCard16() {
        try {
            return this.buf.readChar();
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    int getOffset(int n) {
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            n2 *= 256;
            n2 += this.getCard8();
        }
        return n2;
    }

    void seek(int n) {
        try {
            this.buf.seek(n);
        }
        catch (Exception var2_2) {
            throw new ExceptionConverter(var2_2);
        }
    }

    short getShort() {
        try {
            return this.buf.readShort();
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    int getInt() {
        try {
            return this.buf.readInt();
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    int getPosition() {
        try {
            return this.buf.getFilePointer();
        }
        catch (Exception var1_1) {
            throw new ExceptionConverter(var1_1);
        }
    }

    int[] getIndex(int n) {
        this.seek(n);
        int n2 = this.getCard16();
        int[] arrn = new int[n2 + '\u0001'];
        if (n2 == 0) {
            arrn[0] = -1;
            n += 2;
            return arrn;
        }
        char c = this.getCard8();
        for (int i = 0; i <= n2; ++i) {
            arrn[i] = n + 2 + 1 + (n2 + '\u0001') * c - 1 + this.getOffset(c);
        }
        return arrn;
    }

    protected void getDictItem() {
        int n;
        for (n = 0; n < this.arg_count; ++n) {
            this.args[n] = null;
        }
        this.arg_count = 0;
        this.key = null;
        n = 0;
        while (n == 0) {
            int n2;
            short s;
            char c = this.getCard8();
            if (c == '\u001d') {
                n2 = this.getInt();
                this.args[this.arg_count] = new Integer(n2);
                ++this.arg_count;
                continue;
            }
            if (c == '\u001c') {
                n2 = this.getShort();
                this.args[this.arg_count] = new Integer(n2);
                ++this.arg_count;
                continue;
            }
            if (c >= ' ' && c <= '\u00f6') {
                n2 = (byte)(c - 139);
                this.args[this.arg_count] = new Integer(n2);
                ++this.arg_count;
                continue;
            }
            if (c >= '\u00f7' && c <= '\u00fa') {
                n2 = this.getCard8();
                s = (short)((c - 247) * 256 + n2 + 108);
                this.args[this.arg_count] = new Integer(s);
                ++this.arg_count;
                continue;
            }
            if (c >= '\u00fb' && c <= '\u00fe') {
                n2 = this.getCard8();
                s = (short)((- c - 251) * 256 - n2 - 108);
                this.args[this.arg_count] = new Integer(s);
                ++this.arg_count;
                continue;
            }
            if (c == '\u001e') {
                String string = "";
                s = 0;
                char c2 = '\u0000';
                int n3 = 0;
                int n4 = 0;
                block9 : while (s == 0) {
                    if (n3 == 0) {
                        c2 = this.getCard8();
                        n3 = 2;
                    }
                    if (n3 == 1) {
                        n4 = c2 / 16;
                        n3 = (byte)(n3 - 1);
                    }
                    if (n3 == 2) {
                        n4 = c2 % 16;
                        n3 = (byte)(n3 - 1);
                    }
                    switch (n4) {
                        case 10: {
                            string = string + ".";
                            continue block9;
                        }
                        case 11: {
                            string = string + "E";
                            continue block9;
                        }
                        case 12: {
                            string = string + "E-";
                            continue block9;
                        }
                        case 14: {
                            string = string + "-";
                            continue block9;
                        }
                        case 15: {
                            s = 1;
                            continue block9;
                        }
                    }
                    if (n4 >= 0 && n4 <= 9) {
                        string = string + String.valueOf(n4);
                        continue;
                    }
                    string = string + "<NIBBLE ERROR: " + n4 + '>';
                    s = 1;
                }
                this.args[this.arg_count] = string;
                ++this.arg_count;
                continue;
            }
            if (c > '\u0015') continue;
            n = 1;
            if (c != '\f') {
                this.key = operatorNames[c];
                continue;
            }
            this.key = operatorNames[32 + this.getCard8()];
        }
    }

    protected RangeItem getEntireIndexRange(int n) {
        this.seek(n);
        char c = this.getCard16();
        if (c == '\u0000') {
            return new RangeItem(this.buf, n, 2);
        }
        char c2 = this.getCard8();
        this.seek(n + 2 + 1 + c * c2);
        int n2 = this.getOffset(c2) - 1;
        return new RangeItem(this.buf, n, 3 + (c + '\u0001') * c2 + n2);
    }

    public byte[] getCID(String string) {
        Object object;
        int n;
        Object object2;
        Object object3;
        for (n = 0; n < this.fonts.length && !string.equals(this.fonts[n].name); ++n) {
        }
        if (n == this.fonts.length) {
            return null;
        }
        LinkedList<Object> linkedList = new LinkedList<Object>();
        this.seek(0);
        char c = this.getCard8();
        char c2 = this.getCard8();
        char c3 = this.getCard8();
        char c4 = this.getCard8();
        this.nextIndexOffset = c3;
        linkedList.addLast(new RangeItem(this.buf, 0, c3));
        int n2 = -1;
        int n3 = -1;
        if (!this.fonts[n].isCID) {
            this.seek(this.fonts[n].charstringsOffset);
            n2 = this.getCard16();
            this.seek(this.stringIndexOffset);
            n3 = this.getCard16() + standardStrings.length;
        }
        linkedList.addLast(new UInt16Item('\u0001'));
        linkedList.addLast(new UInt8Item('\u0001'));
        linkedList.addLast(new UInt8Item('\u0001'));
        linkedList.addLast(new UInt8Item((char)(1 + this.fonts[n].name.length())));
        linkedList.addLast(new StringItem(this.fonts[n].name));
        linkedList.addLast(new UInt16Item('\u0001'));
        linkedList.addLast(new UInt8Item('\u0002'));
        linkedList.addLast(new UInt16Item('\u0001'));
        IndexOffsetItem indexOffsetItem = new IndexOffsetItem(2);
        linkedList.addLast(indexOffsetItem);
        IndexBaseItem indexBaseItem = new IndexBaseItem();
        linkedList.addLast(indexBaseItem);
        DictOffsetItem dictOffsetItem = new DictOffsetItem();
        DictOffsetItem dictOffsetItem2 = new DictOffsetItem();
        DictOffsetItem dictOffsetItem3 = new DictOffsetItem();
        DictOffsetItem dictOffsetItem4 = new DictOffsetItem();
        if (!this.fonts[n].isCID) {
            linkedList.addLast(new DictNumberItem(n3));
            linkedList.addLast(new DictNumberItem(n3 + 1));
            linkedList.addLast(new DictNumberItem(0));
            linkedList.addLast(new UInt8Item('\f'));
            linkedList.addLast(new UInt8Item('\u001e'));
            linkedList.addLast(new DictNumberItem(n2));
            linkedList.addLast(new UInt8Item('\f'));
            linkedList.addLast(new UInt8Item('\"'));
        }
        linkedList.addLast(dictOffsetItem3);
        linkedList.addLast(new UInt8Item('\f'));
        linkedList.addLast(new UInt8Item('$'));
        linkedList.addLast(dictOffsetItem4);
        linkedList.addLast(new UInt8Item('\f'));
        linkedList.addLast(new UInt8Item('%'));
        linkedList.addLast(dictOffsetItem);
        linkedList.addLast(new UInt8Item('\u000f'));
        linkedList.addLast(dictOffsetItem2);
        linkedList.addLast(new UInt8Item('\u0011'));
        this.seek(this.topdictOffsets[n]);
        while (this.getPosition() < this.topdictOffsets[n + 1]) {
            object = this.getPosition();
            this.getDictItem();
            object3 = this.getPosition();
            if (this.key == "Encoding" || this.key == "Private" || this.key == "FDSelect" || this.key == "FDArray" || this.key == "charset" || this.key == "CharStrings") continue;
            linkedList.add(new RangeItem(this.buf, (int)object, object3 - object));
        }
        linkedList.addLast(new IndexMarkerItem(indexOffsetItem, indexBaseItem));
        if (this.fonts[n].isCID) {
            linkedList.addLast(this.getEntireIndexRange(this.stringIndexOffset));
        } else {
            int n4;
            String string2 = this.fonts[n].name + "-OneRange";
            if (string2.length() > 127) {
                string2 = string2.substring(0, 127);
            }
            String string3 = "AdobeIdentity" + string2;
            object2 = this.stringOffsets[this.stringOffsets.length - 1] - this.stringOffsets[0];
            int n5 = this.stringOffsets[0] - 1;
            int n6 = object2 + string3.length() <= 255 ? 1 : (object2 + string3.length() <= 65535 ? 2 : (object2 + string3.length() <= 16777215 ? 3 : 4));
            linkedList.addLast(new UInt16Item((char)(this.stringOffsets.length - 1 + 3)));
            linkedList.addLast(new UInt8Item((char)n6));
            for (n4 = 0; n4 < this.stringOffsets.length; ++n4) {
                linkedList.addLast(new IndexOffsetItem(n6, this.stringOffsets[n4] - n5));
            }
            n4 = this.stringOffsets[this.stringOffsets.length - 1] - n5;
            linkedList.addLast(new IndexOffsetItem(n6, n4 += "Adobe".length()));
            linkedList.addLast(new IndexOffsetItem(n6, n4 += "Identity".length()));
            linkedList.addLast(new IndexOffsetItem(n6, n4 += string2.length()));
            linkedList.addLast(new RangeItem(this.buf, this.stringOffsets[0], (int)object2));
            linkedList.addLast(new StringItem(string3));
        }
        linkedList.addLast(this.getEntireIndexRange(this.gsubrIndexOffset));
        if (!this.fonts[n].isCID) {
            linkedList.addLast(new MarkerItem(dictOffsetItem4));
            linkedList.addLast(new UInt8Item('\u0003'));
            linkedList.addLast(new UInt16Item('\u0001'));
            linkedList.addLast(new UInt16Item('\u0000'));
            linkedList.addLast(new UInt8Item('\u0000'));
            linkedList.addLast(new UInt16Item((char)n2));
            linkedList.addLast(new MarkerItem(dictOffsetItem));
            linkedList.addLast(new UInt8Item('\u0002'));
            linkedList.addLast(new UInt16Item('\u0001'));
            linkedList.addLast(new UInt16Item((char)(n2 - 1)));
            linkedList.addLast(new MarkerItem(dictOffsetItem3));
            linkedList.addLast(new UInt16Item('\u0001'));
            linkedList.addLast(new UInt8Item('\u0001'));
            linkedList.addLast(new UInt8Item('\u0001'));
            object = new IndexOffsetItem(1);
            linkedList.addLast(object);
            object3 = new IndexBaseItem();
            linkedList.addLast(object3);
            linkedList.addLast(new DictNumberItem(this.fonts[n].privateLength));
            DictOffsetItem dictOffsetItem5 = new DictOffsetItem();
            linkedList.addLast(dictOffsetItem5);
            linkedList.addLast(new UInt8Item('\u0012'));
            linkedList.addLast(new IndexMarkerItem((OffsetItem)object, (IndexBaseItem)object3));
            linkedList.addLast(new MarkerItem(dictOffsetItem5));
            linkedList.addLast(new RangeItem(this.buf, this.fonts[n].privateOffset, this.fonts[n].privateLength));
            if (this.fonts[n].privateSubrs >= 0) {
                linkedList.addLast(this.getEntireIndexRange(this.fonts[n].privateSubrs));
            }
        }
        linkedList.addLast(new MarkerItem(dictOffsetItem2));
        linkedList.addLast(this.getEntireIndexRange(this.fonts[n].charstringsOffset));
        object = new int[1];
        object[0] = false;
        object3 = linkedList.iterator();
        while (object3.hasNext()) {
            Item item = (Item)object3.next();
            item.increment((int[])object);
        }
        object3 = linkedList.iterator();
        while (object3.hasNext()) {
            Item item = (Item)object3.next();
            item.xref();
        }
        object2 = object[0];
        byte[] arrby = new byte[object2];
        object3 = linkedList.iterator();
        while (object3.hasNext()) {
            Item item = (Item)object3.next();
            item.emit(arrby);
        }
        return arrby;
    }

    public boolean isCID(String string) {
        for (int i = 0; i < this.fonts.length; ++i) {
            if (!string.equals(this.fonts[i].name)) continue;
            return this.fonts[i].isCID;
        }
        return false;
    }

    public boolean exists(String string) {
        for (int i = 0; i < this.fonts.length; ++i) {
            if (!string.equals(this.fonts[i].name)) continue;
            return true;
        }
        return false;
    }

    public String[] getNames() {
        String[] arrstring = new String[this.fonts.length];
        for (int i = 0; i < this.fonts.length; ++i) {
            arrstring[i] = this.fonts[i].name;
        }
        return arrstring;
    }

    public CFFFont(RandomAccessFileOrArray randomAccessFileOrArray) {
        int n;
        this.buf = randomAccessFileOrArray;
        this.seek(0);
        char c = this.getCard8();
        char c2 = this.getCard8();
        char c3 = this.getCard8();
        this.offSize = this.getCard8();
        this.nameIndexOffset = c3;
        this.nameOffsets = this.getIndex(this.nameIndexOffset);
        this.topdictIndexOffset = this.nameOffsets[this.nameOffsets.length - 1];
        this.topdictOffsets = this.getIndex(this.topdictIndexOffset);
        this.stringIndexOffset = this.topdictOffsets[this.topdictOffsets.length - 1];
        this.stringOffsets = this.getIndex(this.stringIndexOffset);
        this.gsubrIndexOffset = this.stringOffsets[this.stringOffsets.length - 1];
        this.gsubrOffsets = this.getIndex(this.gsubrIndexOffset);
        this.fonts = new Font[this.nameOffsets.length - 1];
        for (n = 0; n < this.nameOffsets.length - 1; ++n) {
            this.fonts[n] = new Font();
            this.seek(this.nameOffsets[n]);
            this.fonts[n].name = "";
            for (int i = this.nameOffsets[n]; i < this.nameOffsets[n + 1]; ++i) {
                this.fonts[n].name = this.fonts[n].name + this.getCard8();
            }
        }
        for (n = 0; n < this.topdictOffsets.length - 1; ++n) {
            this.seek(this.topdictOffsets[n]);
            while (this.getPosition() < this.topdictOffsets[n + 1]) {
                this.getDictItem();
                if (this.key == "FullName") {
                    this.fonts[n].fullName = this.getString((char)((Integer)this.args[0]).intValue());
                    continue;
                }
                if (this.key == "ROS") {
                    this.fonts[n].isCID = true;
                    continue;
                }
                if (this.key == "Private") {
                    this.fonts[n].privateLength = (Integer)this.args[0];
                    this.fonts[n].privateOffset = (Integer)this.args[1];
                    continue;
                }
                if (this.key == "charset") {
                    this.fonts[n].charsetOffset = (Integer)this.args[0];
                    continue;
                }
                if (this.key == "Encoding") {
                    this.fonts[n].encodingOffset = (Integer)this.args[0];
                    this.ReadEncoding(this.fonts[n].encodingOffset);
                    continue;
                }
                if (this.key == "CharStrings") {
                    this.fonts[n].charstringsOffset = (Integer)this.args[0];
                    int n2 = this.getPosition();
                    this.fonts[n].charstringsOffsets = this.getIndex(this.fonts[n].charstringsOffset);
                    this.seek(n2);
                    continue;
                }
                if (this.key == "FDArray") {
                    this.fonts[n].fdarrayOffset = (Integer)this.args[0];
                    continue;
                }
                if (this.key == "FDSelect") {
                    this.fonts[n].fdselectOffset = (Integer)this.args[0];
                    continue;
                }
                if (this.key != "CharstringType") continue;
                this.fonts[n].CharstringType = (Integer)this.args[0];
            }
            if (this.fonts[n].privateOffset >= 0) {
                this.seek(this.fonts[n].privateOffset);
                while (this.getPosition() < this.fonts[n].privateOffset + this.fonts[n].privateLength) {
                    this.getDictItem();
                    if (this.key != "Subrs") continue;
                    this.fonts[n].privateSubrs = (Integer)this.args[0] + this.fonts[n].privateOffset;
                }
            }
            if (this.fonts[n].fdarrayOffset < 0) continue;
            int[] arrn = this.getIndex(this.fonts[n].fdarrayOffset);
            this.fonts[n].fdprivateOffsets = new int[arrn.length - 1];
            this.fonts[n].fdprivateLengths = new int[arrn.length - 1];
            for (int i = 0; i < arrn.length - 1; ++i) {
                this.seek(arrn[i]);
                while (this.getPosition() < arrn[i + 1]) {
                    this.getDictItem();
                }
                if (this.key != "Private") continue;
                this.fonts[n].fdprivateLengths[i] = (Integer)this.args[0];
                this.fonts[n].fdprivateOffsets[i] = (Integer)this.args[1];
            }
        }
    }

    void ReadEncoding(int n) {
        this.seek(n);
        char c = this.getCard8();
    }

    protected final class Font {
        public String name;
        public String fullName;
        public boolean isCID;
        public int privateOffset;
        public int privateLength;
        public int privateSubrs;
        public int charstringsOffset;
        public int encodingOffset;
        public int charsetOffset;
        public int fdarrayOffset;
        public int fdselectOffset;
        public int[] fdprivateOffsets;
        public int[] fdprivateLengths;
        public int[] fdprivateSubrs;
        public int nglyphs;
        public int nstrings;
        public int CharsetLength;
        public int[] charstringsOffsets;
        public int[] charset;
        public int[] FDSelect;
        public int FDSelectLength;
        public int FDSelectFormat;
        public int CharstringType;
        public int FDArrayCount;
        public int FDArrayOffsize;
        public int[] FDArrayOffsets;
        public int[] PrivateSubrsOffset;
        public int[][] PrivateSubrsOffsetsArray;
        public int[] SubrsOffsets;

        protected Font() {
            this.isCID = false;
            this.privateOffset = -1;
            this.privateLength = -1;
            this.privateSubrs = -1;
            this.charstringsOffset = -1;
            this.encodingOffset = -1;
            this.charsetOffset = -1;
            this.fdarrayOffset = -1;
            this.fdselectOffset = -1;
            this.CharstringType = 2;
        }
    }

    protected static final class MarkerItem
    extends Item {
        OffsetItem p;

        public MarkerItem(OffsetItem offsetItem) {
            this.p = offsetItem;
        }

        public void xref() {
            this.p.set(this.myOffset);
        }
    }

    protected static final class DictNumberItem
    extends Item {
        public final int value;
        public int size = 5;

        public DictNumberItem(int n) {
            this.value = n;
        }

        public void increment(int[] arrn) {
            super.increment(arrn);
            int[] arrn2 = arrn;
            arrn2[0] = arrn2[0] + this.size;
        }

        public void emit(byte[] arrby) {
            if (this.size == 5) {
                arrby[this.myOffset] = 29;
                arrby[this.myOffset + 1] = (byte)(this.value >>> 24 & 255);
                arrby[this.myOffset + 2] = (byte)(this.value >>> 16 & 255);
                arrby[this.myOffset + 3] = (byte)(this.value >>> 8 & 255);
                arrby[this.myOffset + 4] = (byte)(this.value >>> 0 & 255);
            }
        }
    }

    protected static final class StringItem
    extends Item {
        public String s;

        public StringItem(String string) {
            this.s = string;
        }

        public void increment(int[] arrn) {
            super.increment(arrn);
            int[] arrn2 = arrn;
            arrn2[0] = arrn2[0] + this.s.length();
        }

        public void emit(byte[] arrby) {
            for (int i = 0; i < this.s.length(); ++i) {
                arrby[this.myOffset + i] = (byte)(this.s.charAt(i) & 255);
            }
        }
    }

    protected static final class UInt8Item
    extends Item {
        public char value;

        public UInt8Item(char c) {
            this.value = c;
        }

        public void increment(int[] arrn) {
            super.increment(arrn);
            int[] arrn2 = arrn;
            arrn2[0] = arrn2[0] + 1;
        }

        public void emit(byte[] arrby) {
            arrby[this.myOffset + 0] = (byte)(this.value >>> 0 & 255);
        }
    }

    protected static final class UInt16Item
    extends Item {
        public char value;

        public UInt16Item(char c) {
            this.value = c;
        }

        public void increment(int[] arrn) {
            super.increment(arrn);
            int[] arrn2 = arrn;
            arrn2[0] = arrn2[0] + 2;
        }

        public void emit(byte[] arrby) {
            arrby[this.myOffset + 0] = (byte)(this.value >>> 8 & 255);
            arrby[this.myOffset + 1] = (byte)(this.value >>> 0 & 255);
        }
    }

    protected static final class UInt32Item
    extends Item {
        public int value;

        public UInt32Item(int n) {
            this.value = n;
        }

        public void increment(int[] arrn) {
            super.increment(arrn);
            int[] arrn2 = arrn;
            arrn2[0] = arrn2[0] + 4;
        }

        public void emit(byte[] arrby) {
            arrby[this.myOffset + 0] = (byte)(this.value >>> 24 & 255);
            arrby[this.myOffset + 1] = (byte)(this.value >>> 16 & 255);
            arrby[this.myOffset + 2] = (byte)(this.value >>> 8 & 255);
            arrby[this.myOffset + 3] = (byte)(this.value >>> 0 & 255);
        }
    }

    protected static final class UInt24Item
    extends Item {
        public int value;

        public UInt24Item(int n) {
            this.value = n;
        }

        public void increment(int[] arrn) {
            super.increment(arrn);
            int[] arrn2 = arrn;
            arrn2[0] = arrn2[0] + 3;
        }

        public void emit(byte[] arrby) {
            arrby[this.myOffset + 0] = (byte)(this.value >>> 16 & 255);
            arrby[this.myOffset + 1] = (byte)(this.value >>> 8 & 255);
            arrby[this.myOffset + 2] = (byte)(this.value >>> 0 & 255);
        }
    }

    protected static final class DictOffsetItem
    extends OffsetItem {
        public final int size = 5;

        public void increment(int[] arrn) {
            super.increment(arrn);
            int[] arrn2 = arrn;
            arrn2[0] = arrn2[0] + this.size;
        }

        public void emit(byte[] arrby) {
            if (this.size == 5) {
                arrby[this.myOffset] = 29;
                arrby[this.myOffset + 1] = (byte)(this.value >>> 24 & 255);
                arrby[this.myOffset + 2] = (byte)(this.value >>> 16 & 255);
                arrby[this.myOffset + 3] = (byte)(this.value >>> 8 & 255);
                arrby[this.myOffset + 4] = (byte)(this.value >>> 0 & 255);
            }
        }
    }

    protected static final class SubrMarkerItem
    extends Item {
        private OffsetItem offItem;
        private IndexBaseItem indexBase;

        public SubrMarkerItem(OffsetItem offsetItem, IndexBaseItem indexBaseItem) {
            this.offItem = offsetItem;
            this.indexBase = indexBaseItem;
        }

        public void xref() {
            this.offItem.set(this.myOffset - this.indexBase.myOffset);
        }
    }

    protected static final class IndexMarkerItem
    extends Item {
        private OffsetItem offItem;
        private IndexBaseItem indexBase;

        public IndexMarkerItem(OffsetItem offsetItem, IndexBaseItem indexBaseItem) {
            this.offItem = offsetItem;
            this.indexBase = indexBaseItem;
        }

        public void xref() {
            this.offItem.set(this.myOffset - this.indexBase.myOffset + 1);
        }
    }

    protected static final class IndexBaseItem
    extends Item {
    }

    protected static final class IndexOffsetItem
    extends OffsetItem {
        public final int size;

        public IndexOffsetItem(int n, int n2) {
            this.size = n;
            this.value = n2;
        }

        public IndexOffsetItem(int n) {
            this.size = n;
        }

        public void increment(int[] arrn) {
            super.increment(arrn);
            int[] arrn2 = arrn;
            arrn2[0] = arrn2[0] + this.size;
        }

        public void emit(byte[] arrby) {
            int n = 0;
            switch (this.size) {
                case 4: {
                    arrby[this.myOffset + n] = (byte)(this.value >>> 24 & 255);
                    ++n;
                }
                case 3: {
                    arrby[this.myOffset + n] = (byte)(this.value >>> 16 & 255);
                    ++n;
                }
                case 2: {
                    arrby[this.myOffset + n] = (byte)(this.value >>> 8 & 255);
                    ++n;
                }
                case 1: {
                    arrby[this.myOffset + n] = (byte)(this.value >>> 0 & 255);
                    ++n;
                }
            }
        }
    }

    protected static final class RangeItem
    extends Item {
        public int offset;
        public int length;
        private RandomAccessFileOrArray buf;

        public RangeItem(RandomAccessFileOrArray randomAccessFileOrArray, int n, int n2) {
            this.offset = n;
            this.length = n2;
            this.buf = randomAccessFileOrArray;
        }

        public void increment(int[] arrn) {
            super.increment(arrn);
            int[] arrn2 = arrn;
            arrn2[0] = arrn2[0] + this.length;
        }

        public void emit(byte[] arrby) {
            try {
                this.buf.seek(this.offset);
                for (int i = this.myOffset; i < this.myOffset + this.length; ++i) {
                    arrby[i] = this.buf.readByte();
                }
            }
            catch (Exception var2_3) {
                throw new ExceptionConverter(var2_3);
            }
        }
    }

    protected static abstract class OffsetItem
    extends Item {
        public int value;

        protected OffsetItem() {
        }

        public void set(int n) {
            this.value = n;
        }
    }

    protected static abstract class Item {
        protected int myOffset = -1;

        protected Item() {
        }

        public void increment(int[] arrn) {
            this.myOffset = arrn[0];
        }

        public void emit(byte[] arrby) {
        }

        public void xref() {
        }
    }

}

