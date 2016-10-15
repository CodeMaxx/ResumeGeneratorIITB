/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;

public class PRTokeniser {
    public static final int TK_NUMBER = 1;
    public static final int TK_STRING = 2;
    public static final int TK_NAME = 3;
    public static final int TK_COMMENT = 4;
    public static final int TK_START_ARRAY = 5;
    public static final int TK_END_ARRAY = 6;
    public static final int TK_START_DIC = 7;
    public static final int TK_END_DIC = 8;
    public static final int TK_REF = 9;
    public static final int TK_OTHER = 10;
    public static final boolean[] delims = new boolean[]{true, true, false, false, false, false, false, false, false, false, true, true, false, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, true, false, false, true, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    static final String EMPTY = "";
    protected RandomAccessFileOrArray file;
    protected int type;
    protected String stringValue;
    protected int reference;
    protected int generation;
    protected boolean hexString;

    public PRTokeniser(String string) throws IOException {
        this.file = new RandomAccessFileOrArray(string);
    }

    public PRTokeniser(byte[] arrby) {
        this.file = new RandomAccessFileOrArray(arrby);
    }

    public PRTokeniser(RandomAccessFileOrArray randomAccessFileOrArray) {
        this.file = randomAccessFileOrArray;
    }

    public void seek(int n) throws IOException {
        this.file.seek(n);
    }

    public int getFilePointer() throws IOException {
        return this.file.getFilePointer();
    }

    public void close() throws IOException {
        this.file.close();
    }

    public int length() throws IOException {
        return this.file.length();
    }

    public int read() throws IOException {
        return this.file.read();
    }

    public RandomAccessFileOrArray getSafeFile() {
        return new RandomAccessFileOrArray(this.file);
    }

    public RandomAccessFileOrArray getFile() {
        return this.file;
    }

    public String readString(int n) throws IOException {
        int n2;
        StringBuffer stringBuffer = new StringBuffer();
        while (n-- > 0 && (n2 = this.file.read()) != -1) {
            stringBuffer.append((char)n2);
        }
        return stringBuffer.toString();
    }

    public static final boolean isWhitespace(int n) {
        return n == 0 || n == 9 || n == 10 || n == 12 || n == 13 || n == 32;
    }

    public static final boolean isDelimiter(int n) {
        return n == 40 || n == 41 || n == 60 || n == 62 || n == 91 || n == 93 || n == 47 || n == 37;
    }

    public static final boolean isDelimiterWhitespace(int n) {
        return delims[n + 1];
    }

    public int getTokenType() {
        return this.type;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public int getReference() {
        return this.reference;
    }

    public int getGeneration() {
        return this.generation;
    }

    public void backOnePosition(int n) {
        if (n != -1) {
            this.file.pushBack((byte)n);
        }
    }

    public void throwError(String string) throws IOException {
        throw new IOException(string + " at file pointer " + this.file.getFilePointer());
    }

    public char checkPdfHeader() throws IOException {
        this.file.setStartOffset(0);
        String string = this.readString(1024);
        int n = string.indexOf("%PDF-");
        if (n < 0) {
            throw new IOException("PDF header signature not found.");
        }
        this.file.setStartOffset(n);
        return string.charAt(n + 7);
    }

    public void checkFdfHeader() throws IOException {
        this.file.setStartOffset(0);
        String string = this.readString(1024);
        int n = string.indexOf("%FDF-1.2");
        if (n < 0) {
            throw new IOException("FDF header signature not found.");
        }
        this.file.setStartOffset(n);
    }

    public int getStartxref() throws IOException {
        int n = Math.min(1024, this.file.length());
        int n2 = this.file.length() - n;
        this.file.seek(n2);
        String string = this.readString(1024);
        int n3 = string.lastIndexOf("startxref");
        if (n3 < 0) {
            throw new IOException("PDF startxref not found.");
        }
        return n2 + n3;
    }

    public static int getHex(int n) {
        if (n >= 48 && n <= 57) {
            return n - 48;
        }
        if (n >= 65 && n <= 70) {
            return n - 65 + 10;
        }
        if (n >= 97 && n <= 102) {
            return n - 97 + 10;
        }
        return -1;
    }

    public void nextValidToken() throws IOException {
        int n = 0;
        String string = null;
        String string2 = null;
        int n2 = 0;
        block4 : while (this.nextToken()) {
            if (this.type == 4) continue;
            switch (n) {
                case 0: {
                    if (this.type != 1) {
                        return;
                    }
                    n2 = this.file.getFilePointer();
                    string = this.stringValue;
                    ++n;
                    continue block4;
                }
                case 1: {
                    if (this.type != 1) {
                        this.file.seek(n2);
                        this.type = 1;
                        this.stringValue = string;
                        return;
                    }
                    string2 = this.stringValue;
                    ++n;
                    continue block4;
                }
            }
            if (this.type != 10 || !this.stringValue.equals("R")) {
                this.file.seek(n2);
                this.type = 1;
                this.stringValue = string;
                return;
            }
            this.type = 9;
            this.reference = Integer.parseInt(string);
            this.generation = Integer.parseInt(string2);
            return;
        }
        this.throwError("Unexpected end of file");
    }

    public boolean nextToken() throws IOException {
        StringBuffer stringBuffer = null;
        this.stringValue = "";
        int n = 0;
        while ((n = this.file.read()) != -1 && PRTokeniser.isWhitespace(n)) {
        }
        if (n == -1) {
            return false;
        }
        switch (n) {
            case 91: {
                this.type = 5;
                break;
            }
            case 93: {
                this.type = 6;
                break;
            }
            case 47: {
                stringBuffer = new StringBuffer();
                this.type = 3;
                while (!delims[(n = this.file.read()) + 1]) {
                    if (n == 35) {
                        n = (PRTokeniser.getHex(this.file.read()) << 4) + PRTokeniser.getHex(this.file.read());
                    }
                    stringBuffer.append((char)n);
                }
                this.backOnePosition(n);
                break;
            }
            case 62: {
                n = this.file.read();
                if (n != 62) {
                    this.throwError("'>' not expected");
                }
                this.type = 8;
                break;
            }
            case 60: {
                int n2 = this.file.read();
                if (n2 == 60) {
                    this.type = 7;
                    break;
                }
                stringBuffer = new StringBuffer();
                this.type = 2;
                this.hexString = true;
                int n3 = 0;
                do {
                    if (PRTokeniser.isWhitespace(n2)) {
                        n2 = this.file.read();
                        continue;
                    }
                    if (n2 == 62 || (n2 = PRTokeniser.getHex(n2)) < 0) break;
                    n3 = this.file.read();
                    while (PRTokeniser.isWhitespace(n3)) {
                        n3 = this.file.read();
                    }
                    if (n3 == 62) {
                        n = n2 << 4;
                        stringBuffer.append((char)n);
                        break;
                    }
                    if ((n3 = PRTokeniser.getHex(n3)) < 0) break;
                    n = (n2 << 4) + n3;
                    stringBuffer.append((char)n);
                    n2 = this.file.read();
                } while (true);
                if (n2 >= 0 && n3 >= 0) break;
                this.throwError("Error reading string");
                break;
            }
            case 37: {
                this.type = 4;
                while ((n = this.file.read()) != -1 && n != 13 && n != 10) {
                }
                break;
            }
            case 40: {
                stringBuffer = new StringBuffer();
                this.type = 2;
                this.hexString = false;
                int n4 = 0;
                while ((n = this.file.read()) != -1) {
                    if (n == 40) {
                        ++n4;
                    } else if (n == 41) {
                        --n4;
                    } else if (n == 92) {
                        boolean bl = false;
                        n = this.file.read();
                        switch (n) {
                            case 110: {
                                n = 10;
                                break;
                            }
                            case 114: {
                                n = 13;
                                break;
                            }
                            case 116: {
                                n = 9;
                                break;
                            }
                            case 98: {
                                n = 8;
                                break;
                            }
                            case 102: {
                                n = 12;
                                break;
                            }
                            case 40: 
                            case 41: 
                            case 92: {
                                break;
                            }
                            case 13: {
                                bl = true;
                                n = this.file.read();
                                if (n == 10) break;
                                this.backOnePosition(n);
                                break;
                            }
                            case 10: {
                                bl = true;
                                break;
                            }
                            default: {
                                if (n < 48 || n > 55) break;
                                int n5 = n - 48;
                                n = this.file.read();
                                if (n < 48 || n > 55) {
                                    this.backOnePosition(n);
                                    n = n5;
                                    break;
                                }
                                n5 = (n5 << 3) + n - 48;
                                n = this.file.read();
                                if (n < 48 || n > 55) {
                                    this.backOnePosition(n);
                                    n = n5;
                                    break;
                                }
                                n5 = (n5 << 3) + n - 48;
                                n = n5 & 255;
                            }
                        }
                        if (bl) continue;
                        if (n < 0) {
                            break;
                        }
                    } else if (n == 13) {
                        n = this.file.read();
                        if (n < 0) break;
                        if (n != 10) {
                            this.backOnePosition(n);
                            n = 10;
                        }
                    }
                    if (n4 == -1) break;
                    stringBuffer.append((char)n);
                }
                if (n != -1) break;
                this.throwError("Error reading string");
                break;
            }
            default: {
                stringBuffer = new StringBuffer();
                if (n == 45 || n == 43 || n == 46 || n >= 48 && n <= 57) {
                    this.type = 1;
                    do {
                        stringBuffer.append((char)n);
                    } while ((n = this.file.read()) != -1 && (n >= 48 && n <= 57 || n == 46));
                } else {
                    this.type = 10;
                    do {
                        stringBuffer.append((char)n);
                    } while (!delims[(n = this.file.read()) + 1]);
                }
                this.backOnePosition(n);
            }
        }
        if (stringBuffer != null) {
            this.stringValue = stringBuffer.toString();
        }
        return true;
    }

    public int intValue() {
        return Integer.parseInt(this.stringValue);
    }

    public boolean readLineSegment(byte[] arrby) throws IOException {
        int n;
        int n2 = -1;
        boolean bl = false;
        int n3 = 0;
        int n4 = arrby.length;
        if (n3 < n4) {
            while (PRTokeniser.isWhitespace(n2 = this.read())) {
            }
        }
        while (!bl && n3 < n4) {
            switch (n2) {
                case -1: 
                case 10: {
                    bl = true;
                    break;
                }
                case 13: {
                    bl = true;
                    n = this.getFilePointer();
                    if (this.read() == 10) break;
                    this.seek(n);
                    break;
                }
                default: {
                    arrby[n3++] = (byte)n2;
                }
            }
            if (bl || n4 <= n3) break;
            n2 = this.read();
        }
        if (n3 >= n4) {
            bl = false;
            while (!bl) {
                n2 = this.read();
                switch (n2) {
                    case -1: 
                    case 10: {
                        bl = true;
                        break;
                    }
                    case 13: {
                        bl = true;
                        n = this.getFilePointer();
                        if (this.read() == 10) break;
                        this.seek(n);
                    }
                }
            }
        }
        if (n2 == -1 && n3 == 0) {
            return false;
        }
        if (n3 + 2 <= n4) {
            arrby[n3++] = 32;
            arrby[n3] = 88;
        }
        return true;
    }

    public static int[] checkObjectStart(byte[] arrby) {
        try {
            PRTokeniser pRTokeniser = new PRTokeniser(arrby);
            int n = 0;
            int n2 = 0;
            if (!pRTokeniser.nextToken() || pRTokeniser.getTokenType() != 1) {
                return null;
            }
            n = pRTokeniser.intValue();
            if (!pRTokeniser.nextToken() || pRTokeniser.getTokenType() != 1) {
                return null;
            }
            n2 = pRTokeniser.intValue();
            if (!pRTokeniser.nextToken()) {
                return null;
            }
            if (!pRTokeniser.getStringValue().equals("obj")) {
                return null;
            }
            return new int[]{n, n2};
        }
        catch (Exception var1_2) {
            return null;
        }
    }

    public boolean isHexString() {
        return this.hexString;
    }
}

