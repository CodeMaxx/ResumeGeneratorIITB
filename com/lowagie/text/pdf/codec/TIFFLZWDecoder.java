/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec;

public class TIFFLZWDecoder {
    byte[][] stringTable;
    byte[] data = null;
    byte[] uncompData;
    int tableIndex;
    int bitsToGet = 9;
    int bytePointer;
    int bitPointer;
    int dstIndex;
    int w;
    int h;
    int predictor;
    int samplesPerPixel;
    int nextData = 0;
    int nextBits = 0;
    int[] andTable = new int[]{511, 1023, 2047, 4095};

    public TIFFLZWDecoder(int n, int n2, int n3) {
        this.w = n;
        this.predictor = n2;
        this.samplesPerPixel = n3;
    }

    public byte[] decode(byte[] arrby, byte[] arrby2, int n) {
        int n2;
        if (arrby[0] == 0 && arrby[1] == 1) {
            throw new UnsupportedOperationException("TIFF 5.0-style LZW codes are not supported.");
        }
        this.initializeStringTable();
        this.data = arrby;
        this.h = n;
        this.uncompData = arrby2;
        this.bytePointer = 0;
        this.bitPointer = 0;
        this.dstIndex = 0;
        this.nextData = 0;
        this.nextBits = 0;
        int n3 = 0;
        while ((n2 = this.getNextCode()) != 257 && this.dstIndex < arrby2.length) {
            byte[] arrby3;
            if (n2 == 256) {
                this.initializeStringTable();
                n2 = this.getNextCode();
                if (n2 == 257) break;
                this.writeString(this.stringTable[n2]);
                n3 = n2;
                continue;
            }
            if (n2 < this.tableIndex) {
                arrby3 = this.stringTable[n2];
                this.writeString(arrby3);
                this.addStringToTable(this.stringTable[n3], arrby3[0]);
                n3 = n2;
                continue;
            }
            arrby3 = this.stringTable[n3];
            arrby3 = this.composeString(arrby3, arrby3[0]);
            this.writeString(arrby3);
            this.addStringToTable(arrby3);
            n3 = n2;
        }
        if (this.predictor == 2) {
            for (int i = 0; i < n; ++i) {
                int n4 = this.samplesPerPixel * (i * this.w + 1);
                for (int j = this.samplesPerPixel; j < this.w * this.samplesPerPixel; ++j) {
                    byte[] arrby4 = arrby2;
                    int n5 = n4;
                    arrby4[n5] = (byte)(arrby4[n5] + arrby2[n4 - this.samplesPerPixel]);
                    ++n4;
                }
            }
        }
        return arrby2;
    }

    public void initializeStringTable() {
        this.stringTable = new byte[4096][];
        for (int i = 0; i < 256; ++i) {
            this.stringTable[i] = new byte[1];
            this.stringTable[i][0] = (byte)i;
        }
        this.tableIndex = 258;
        this.bitsToGet = 9;
    }

    public void writeString(byte[] arrby) {
        int n = this.uncompData.length - this.dstIndex;
        if (arrby.length < n) {
            n = arrby.length;
        }
        System.arraycopy(arrby, 0, this.uncompData, this.dstIndex, n);
        this.dstIndex += n;
    }

    public void addStringToTable(byte[] arrby, byte by) {
        int n = arrby.length;
        byte[] arrby2 = new byte[n + 1];
        System.arraycopy(arrby, 0, arrby2, 0, n);
        arrby2[n] = by;
        this.stringTable[this.tableIndex++] = arrby2;
        if (this.tableIndex == 511) {
            this.bitsToGet = 10;
        } else if (this.tableIndex == 1023) {
            this.bitsToGet = 11;
        } else if (this.tableIndex == 2047) {
            this.bitsToGet = 12;
        }
    }

    public void addStringToTable(byte[] arrby) {
        this.stringTable[this.tableIndex++] = arrby;
        if (this.tableIndex == 511) {
            this.bitsToGet = 10;
        } else if (this.tableIndex == 1023) {
            this.bitsToGet = 11;
        } else if (this.tableIndex == 2047) {
            this.bitsToGet = 12;
        }
    }

    public byte[] composeString(byte[] arrby, byte by) {
        int n = arrby.length;
        byte[] arrby2 = new byte[n + 1];
        System.arraycopy(arrby, 0, arrby2, 0, n);
        arrby2[n] = by;
        return arrby2;
    }

    public int getNextCode() {
        try {
            this.nextData = this.nextData << 8 | this.data[this.bytePointer++] & 255;
            this.nextBits += 8;
            if (this.nextBits < this.bitsToGet) {
                this.nextData = this.nextData << 8 | this.data[this.bytePointer++] & 255;
                this.nextBits += 8;
            }
            int n = this.nextData >> this.nextBits - this.bitsToGet & this.andTable[this.bitsToGet - 9];
            this.nextBits -= this.bitsToGet;
            return n;
        }
        catch (ArrayIndexOutOfBoundsException var1_2) {
            return 257;
        }
    }
}

