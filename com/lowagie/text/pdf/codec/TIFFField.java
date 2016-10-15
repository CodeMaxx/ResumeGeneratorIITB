/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec;

import java.io.Serializable;

public class TIFFField
implements Comparable,
Serializable {
    private static final long serialVersionUID = 9088332901412823834L;
    public static final int TIFF_BYTE = 1;
    public static final int TIFF_ASCII = 2;
    public static final int TIFF_SHORT = 3;
    public static final int TIFF_LONG = 4;
    public static final int TIFF_RATIONAL = 5;
    public static final int TIFF_SBYTE = 6;
    public static final int TIFF_UNDEFINED = 7;
    public static final int TIFF_SSHORT = 8;
    public static final int TIFF_SLONG = 9;
    public static final int TIFF_SRATIONAL = 10;
    public static final int TIFF_FLOAT = 11;
    public static final int TIFF_DOUBLE = 12;
    int tag;
    int type;
    int count;
    Object data;

    TIFFField() {
    }

    public TIFFField(int n, int n2, int n3, Object object) {
        this.tag = n;
        this.type = n2;
        this.count = n3;
        this.data = object;
    }

    public int getTag() {
        return this.tag;
    }

    public int getType() {
        return this.type;
    }

    public int getCount() {
        return this.count;
    }

    public byte[] getAsBytes() {
        return (byte[])this.data;
    }

    public char[] getAsChars() {
        return (char[])this.data;
    }

    public short[] getAsShorts() {
        return (short[])this.data;
    }

    public int[] getAsInts() {
        return (int[])this.data;
    }

    public long[] getAsLongs() {
        return (long[])this.data;
    }

    public float[] getAsFloats() {
        return (float[])this.data;
    }

    public double[] getAsDoubles() {
        return (double[])this.data;
    }

    public int[][] getAsSRationals() {
        return (int[][])this.data;
    }

    public long[][] getAsRationals() {
        return (long[][])this.data;
    }

    public int getAsInt(int n) {
        switch (this.type) {
            case 1: 
            case 7: {
                return ((byte[])this.data)[n] & 255;
            }
            case 6: {
                return ((byte[])this.data)[n];
            }
            case 3: {
                return ((char[])this.data)[n] & 65535;
            }
            case 8: {
                return ((short[])this.data)[n];
            }
            case 9: {
                return ((int[])this.data)[n];
            }
        }
        throw new ClassCastException();
    }

    public long getAsLong(int n) {
        switch (this.type) {
            case 1: 
            case 7: {
                return ((byte[])this.data)[n] & 255;
            }
            case 6: {
                return ((byte[])this.data)[n];
            }
            case 3: {
                return ((char[])this.data)[n] & 65535;
            }
            case 8: {
                return ((short[])this.data)[n];
            }
            case 9: {
                return ((int[])this.data)[n];
            }
            case 4: {
                return ((long[])this.data)[n];
            }
        }
        throw new ClassCastException();
    }

    public float getAsFloat(int n) {
        switch (this.type) {
            case 1: {
                return ((byte[])this.data)[n] & 255;
            }
            case 6: {
                return ((byte[])this.data)[n];
            }
            case 3: {
                return ((char[])this.data)[n] & 65535;
            }
            case 8: {
                return ((short[])this.data)[n];
            }
            case 9: {
                return ((int[])this.data)[n];
            }
            case 4: {
                return ((long[])this.data)[n];
            }
            case 11: {
                return ((float[])this.data)[n];
            }
            case 12: {
                return (float)((double[])this.data)[n];
            }
            case 10: {
                int[] arrn = this.getAsSRational(n);
                return (float)((double)arrn[0] / (double)arrn[1]);
            }
            case 5: {
                long[] arrl = this.getAsRational(n);
                return (float)((double)arrl[0] / (double)arrl[1]);
            }
        }
        throw new ClassCastException();
    }

    public double getAsDouble(int n) {
        switch (this.type) {
            case 1: {
                return ((byte[])this.data)[n] & 255;
            }
            case 6: {
                return ((byte[])this.data)[n];
            }
            case 3: {
                return ((char[])this.data)[n] & 65535;
            }
            case 8: {
                return ((short[])this.data)[n];
            }
            case 9: {
                return ((int[])this.data)[n];
            }
            case 4: {
                return ((long[])this.data)[n];
            }
            case 11: {
                return ((float[])this.data)[n];
            }
            case 12: {
                return ((double[])this.data)[n];
            }
            case 10: {
                int[] arrn = this.getAsSRational(n);
                return (double)arrn[0] / (double)arrn[1];
            }
            case 5: {
                long[] arrl = this.getAsRational(n);
                return (double)arrl[0] / (double)arrl[1];
            }
        }
        throw new ClassCastException();
    }

    public String getAsString(int n) {
        return ((String[])this.data)[n];
    }

    public int[] getAsSRational(int n) {
        return ((int[][])this.data)[n];
    }

    public long[] getAsRational(int n) {
        if (this.type == 4) {
            return this.getAsLongs();
        }
        return ((long[][])this.data)[n];
    }

    public int compareTo(Object object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
        int n = ((TIFFField)object).getTag();
        if (this.tag < n) {
            return -1;
        }
        if (this.tag > n) {
            return 1;
        }
        return 0;
    }
}

