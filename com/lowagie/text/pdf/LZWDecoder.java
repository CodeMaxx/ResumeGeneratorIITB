/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.io.IOException;
import java.io.OutputStream;

public class LZWDecoder {
    byte[][] stringTable;
    byte[] data = null;
    OutputStream uncompData;
    int tableIndex;
    int bitsToGet = 9;
    int bytePointer;
    int bitPointer;
    int nextData = 0;
    int nextBits = 0;
    int[] andTable = new int[]{511, 1023, 2047, 4095};

    public void decode(byte[] arrby, OutputStream outputStream) {
        int n;
        if (arrby[0] == 0 && arrby[1] == 1) {
            throw new RuntimeException("LZW flavour not supported.");
        }
        this.initializeStringTable();
        this.data = arrby;
        this.uncompData = outputStream;
        this.bytePointer = 0;
        this.bitPointer = 0;
        this.nextData = 0;
        this.nextBits = 0;
        int n2 = 0;
        while ((n = this.getNextCode()) != 257) {
            byte[] arrby2;
            if (n == 256) {
                this.initializeStringTable();
                n = this.getNextCode();
                if (n == 257) break;
                this.writeString(this.stringTable[n]);
                n2 = n;
                continue;
            }
            if (n < this.tableIndex) {
                arrby2 = this.stringTable[n];
                this.writeString(arrby2);
                this.addStringToTable(this.stringTable[n2], arrby2[0]);
                n2 = n;
                continue;
            }
            arrby2 = this.stringTable[n2];
            arrby2 = this.composeString(arrby2, arrby2[0]);
            this.writeString(arrby2);
            this.addStringToTable(arrby2);
            n2 = n;
        }
    }

    public void initializeStringTable() {
        this.stringTable = new byte[8192][];
        for (int i = 0; i < 256; ++i) {
            this.stringTable[i] = new byte[1];
            this.stringTable[i][0] = (byte)i;
        }
        this.tableIndex = 258;
        this.bitsToGet = 9;
    }

    public void writeString(byte[] arrby) {
        try {
            this.uncompData.write(arrby);
        }
        catch (IOException var2_2) {
            throw new ExceptionConverter(var2_2);
        }
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

