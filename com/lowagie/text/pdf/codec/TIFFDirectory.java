/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec;

import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TIFFField;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class TIFFDirectory
implements Serializable {
    private static final long serialVersionUID = -168636766193675380L;
    boolean isBigEndian;
    int numEntries;
    TIFFField[] fields;
    Hashtable fieldIndex = new Hashtable();
    long IFDOffset = 8;
    long nextIFDOffset = 0;
    private static final int[] sizeOfType = new int[]{0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};

    TIFFDirectory() {
    }

    private static boolean isValidEndianTag(int n) {
        return n == 18761 || n == 19789;
    }

    public TIFFDirectory(RandomAccessFileOrArray randomAccessFileOrArray, int n) throws IOException {
        long l = randomAccessFileOrArray.getFilePointer();
        randomAccessFileOrArray.seek(0);
        int n2 = randomAccessFileOrArray.readUnsignedShort();
        if (!TIFFDirectory.isValidEndianTag(n2)) {
            throw new IllegalArgumentException("Bad endianness tag (not 0x4949 or 0x4d4d).");
        }
        this.isBigEndian = n2 == 19789;
        int n3 = this.readUnsignedShort(randomAccessFileOrArray);
        if (n3 != 42) {
            throw new IllegalArgumentException("Bad magic number, should be 42.");
        }
        long l2 = this.readUnsignedInt(randomAccessFileOrArray);
        for (int i = 0; i < n; ++i) {
            if (l2 == 0) {
                throw new IllegalArgumentException("Directory number too large.");
            }
            randomAccessFileOrArray.seek(l2);
            int n4 = this.readUnsignedShort(randomAccessFileOrArray);
            randomAccessFileOrArray.skip(12 * n4);
            l2 = this.readUnsignedInt(randomAccessFileOrArray);
        }
        randomAccessFileOrArray.seek(l2);
        this.initialize(randomAccessFileOrArray);
        randomAccessFileOrArray.seek(l);
    }

    public TIFFDirectory(RandomAccessFileOrArray randomAccessFileOrArray, long l, int n) throws IOException {
        long l2 = randomAccessFileOrArray.getFilePointer();
        randomAccessFileOrArray.seek(0);
        int n2 = randomAccessFileOrArray.readUnsignedShort();
        if (!TIFFDirectory.isValidEndianTag(n2)) {
            throw new IllegalArgumentException("Bad endianness tag (not 0x4949 or 0x4d4d).");
        }
        this.isBigEndian = n2 == 19789;
        randomAccessFileOrArray.seek(l);
        for (int i = 0; i < n; ++i) {
            int n3 = this.readUnsignedShort(randomAccessFileOrArray);
            randomAccessFileOrArray.seek(l + (long)(12 * n3));
            l = this.readUnsignedInt(randomAccessFileOrArray);
            randomAccessFileOrArray.seek(l);
        }
        this.initialize(randomAccessFileOrArray);
        randomAccessFileOrArray.seek(l2);
    }

    private void initialize(RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        long l = 0;
        long l2 = randomAccessFileOrArray.length();
        this.IFDOffset = randomAccessFileOrArray.getFilePointer();
        this.numEntries = this.readUnsignedShort(randomAccessFileOrArray);
        this.fields = new TIFFField[this.numEntries];
        for (int i = 0; i < this.numEntries && l < l2; ++i) {
            int n = this.readUnsignedShort(randomAccessFileOrArray);
            int n2 = this.readUnsignedShort(randomAccessFileOrArray);
            int n3 = (int)this.readUnsignedInt(randomAccessFileOrArray);
            boolean bl = true;
            l = randomAccessFileOrArray.getFilePointer() + 4;
            try {
                if (n3 * sizeOfType[n2] > 4) {
                    long l3 = this.readUnsignedInt(randomAccessFileOrArray);
                    if (l3 < l2) {
                        randomAccessFileOrArray.seek(l3);
                    } else {
                        bl = false;
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException var12_12) {
                bl = false;
            }
            if (bl) {
                this.fieldIndex.put(new Integer(n), new Integer(i));
                char[] arrc = null;
                switch (n2) {
                    short[] arrs;
                    int n4;
                    Object object;
                    case 1: 
                    case 2: 
                    case 6: 
                    case 7: {
                        byte[] arrby = new byte[n3];
                        randomAccessFileOrArray.readFully(arrby, 0, n3);
                        if (n2 == 2) {
                            int n5 = 0;
                            int n6 = 0;
                            object = new ArrayList();
                            while (n5 < n3) {
                                while (n5 < n3 && arrby[n5++] != 0) {
                                }
                                object.add(new String(arrby, n6, n5 - n6));
                                n6 = n5;
                            }
                            n3 = object.size();
                            arrs = new String[n3];
                            for (int j = 0; j < n3; ++j) {
                                arrs[j] = (String)object.get(j);
                            }
                            arrc = arrs;
                            break;
                        }
                        arrc = arrby;
                        break;
                    }
                    case 3: {
                        char[] arrc2 = new char[n3];
                        for (n4 = 0; n4 < n3; ++n4) {
                            arrc2[n4] = (char)this.readUnsignedShort(randomAccessFileOrArray);
                        }
                        arrc = arrc2;
                        break;
                    }
                    case 4: {
                        long[] arrl = new long[n3];
                        for (n4 = 0; n4 < n3; ++n4) {
                            arrl[n4] = this.readUnsignedInt(randomAccessFileOrArray);
                        }
                        arrc = arrl;
                        break;
                    }
                    case 5: {
                        object = new long[n3][2];
                        for (n4 = 0; n4 < n3; ++n4) {
                            object[n4][0] = this.readUnsignedInt(randomAccessFileOrArray);
                            object[n4][1] = this.readUnsignedInt(randomAccessFileOrArray);
                        }
                        arrc = object;
                        break;
                    }
                    case 8: {
                        arrs = new short[n3];
                        for (n4 = 0; n4 < n3; ++n4) {
                            arrs[n4] = this.readShort(randomAccessFileOrArray);
                        }
                        arrc = arrs;
                        break;
                    }
                    case 9: {
                        int[] arrn = new int[n3];
                        for (n4 = 0; n4 < n3; ++n4) {
                            arrn[n4] = this.readInt(randomAccessFileOrArray);
                        }
                        arrc = arrn;
                        break;
                    }
                    case 10: {
                        int[][] arrn = new int[n3][2];
                        for (n4 = 0; n4 < n3; ++n4) {
                            arrn[n4][0] = this.readInt(randomAccessFileOrArray);
                            arrn[n4][1] = this.readInt(randomAccessFileOrArray);
                        }
                        arrc = arrn;
                        break;
                    }
                    case 11: {
                        float[] arrf = new float[n3];
                        for (n4 = 0; n4 < n3; ++n4) {
                            arrf[n4] = this.readFloat(randomAccessFileOrArray);
                        }
                        arrc = arrf;
                        break;
                    }
                    case 12: {
                        double[] arrd = new double[n3];
                        for (n4 = 0; n4 < n3; ++n4) {
                            arrd[n4] = this.readDouble(randomAccessFileOrArray);
                        }
                        arrc = arrd;
                        break;
                    }
                }
                this.fields[i] = new TIFFField(n, n2, n3, arrc);
            }
            randomAccessFileOrArray.seek(l);
        }
        try {
            this.nextIFDOffset = this.readUnsignedInt(randomAccessFileOrArray);
        }
        catch (Exception var8_7) {
            this.nextIFDOffset = 0;
        }
    }

    public int getNumEntries() {
        return this.numEntries;
    }

    public TIFFField getField(int n) {
        Integer n2 = (Integer)this.fieldIndex.get(new Integer(n));
        if (n2 == null) {
            return null;
        }
        return this.fields[n2];
    }

    public boolean isTagPresent(int n) {
        return this.fieldIndex.containsKey(new Integer(n));
    }

    public int[] getTags() {
        int[] arrn = new int[this.fieldIndex.size()];
        Enumeration enumeration = this.fieldIndex.keys();
        int n = 0;
        while (enumeration.hasMoreElements()) {
            arrn[n++] = (Integer)enumeration.nextElement();
        }
        return arrn;
    }

    public TIFFField[] getFields() {
        return this.fields;
    }

    public byte getFieldAsByte(int n, int n2) {
        Integer n3 = (Integer)this.fieldIndex.get(new Integer(n));
        byte[] arrby = this.fields[n3].getAsBytes();
        return arrby[n2];
    }

    public byte getFieldAsByte(int n) {
        return this.getFieldAsByte(n, 0);
    }

    public long getFieldAsLong(int n, int n2) {
        Integer n3 = (Integer)this.fieldIndex.get(new Integer(n));
        return this.fields[n3].getAsLong(n2);
    }

    public long getFieldAsLong(int n) {
        return this.getFieldAsLong(n, 0);
    }

    public float getFieldAsFloat(int n, int n2) {
        Integer n3 = (Integer)this.fieldIndex.get(new Integer(n));
        return this.fields[n3].getAsFloat(n2);
    }

    public float getFieldAsFloat(int n) {
        return this.getFieldAsFloat(n, 0);
    }

    public double getFieldAsDouble(int n, int n2) {
        Integer n3 = (Integer)this.fieldIndex.get(new Integer(n));
        return this.fields[n3].getAsDouble(n2);
    }

    public double getFieldAsDouble(int n) {
        return this.getFieldAsDouble(n, 0);
    }

    private short readShort(RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        if (this.isBigEndian) {
            return randomAccessFileOrArray.readShort();
        }
        return randomAccessFileOrArray.readShortLE();
    }

    private int readUnsignedShort(RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        if (this.isBigEndian) {
            return randomAccessFileOrArray.readUnsignedShort();
        }
        return randomAccessFileOrArray.readUnsignedShortLE();
    }

    private int readInt(RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        if (this.isBigEndian) {
            return randomAccessFileOrArray.readInt();
        }
        return randomAccessFileOrArray.readIntLE();
    }

    private long readUnsignedInt(RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        if (this.isBigEndian) {
            return randomAccessFileOrArray.readUnsignedInt();
        }
        return randomAccessFileOrArray.readUnsignedIntLE();
    }

    private long readLong(RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        if (this.isBigEndian) {
            return randomAccessFileOrArray.readLong();
        }
        return randomAccessFileOrArray.readLongLE();
    }

    private float readFloat(RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        if (this.isBigEndian) {
            return randomAccessFileOrArray.readFloat();
        }
        return randomAccessFileOrArray.readFloatLE();
    }

    private double readDouble(RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        if (this.isBigEndian) {
            return randomAccessFileOrArray.readDouble();
        }
        return randomAccessFileOrArray.readDoubleLE();
    }

    private static int readUnsignedShort(RandomAccessFileOrArray randomAccessFileOrArray, boolean bl) throws IOException {
        if (bl) {
            return randomAccessFileOrArray.readUnsignedShort();
        }
        return randomAccessFileOrArray.readUnsignedShortLE();
    }

    private static long readUnsignedInt(RandomAccessFileOrArray randomAccessFileOrArray, boolean bl) throws IOException {
        if (bl) {
            return randomAccessFileOrArray.readUnsignedInt();
        }
        return randomAccessFileOrArray.readUnsignedIntLE();
    }

    public static int getNumDirectories(RandomAccessFileOrArray randomAccessFileOrArray) throws IOException {
        long l = randomAccessFileOrArray.getFilePointer();
        randomAccessFileOrArray.seek(0);
        int n = randomAccessFileOrArray.readUnsignedShort();
        if (!TIFFDirectory.isValidEndianTag(n)) {
            throw new IllegalArgumentException("Bad endianness tag (not 0x4949 or 0x4d4d).");
        }
        boolean bl = n == 19789;
        int n2 = TIFFDirectory.readUnsignedShort(randomAccessFileOrArray, bl);
        if (n2 != 42) {
            throw new IllegalArgumentException("Bad magic number, should be 42.");
        }
        randomAccessFileOrArray.seek(4);
        long l2 = TIFFDirectory.readUnsignedInt(randomAccessFileOrArray, bl);
        int n3 = 0;
        while (l2 != 0) {
            ++n3;
            try {
                randomAccessFileOrArray.seek(l2);
                int n4 = TIFFDirectory.readUnsignedShort(randomAccessFileOrArray, bl);
                randomAccessFileOrArray.skip(12 * n4);
                l2 = TIFFDirectory.readUnsignedInt(randomAccessFileOrArray, bl);
                continue;
            }
            catch (EOFException var9_8) {
                // empty catch block
                break;
            }
        }
        randomAccessFileOrArray.seek(l);
        return n3;
    }

    public boolean isBigEndian() {
        return this.isBigEndian;
    }

    public long getIFDOffset() {
        return this.IFDOffset;
    }

    public long getNextIFDOffset() {
        return this.nextIFDOffset;
    }
}

