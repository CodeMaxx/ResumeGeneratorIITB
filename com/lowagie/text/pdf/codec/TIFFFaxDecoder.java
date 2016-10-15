/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec;

public class TIFFFaxDecoder {
    private int bitPointer;
    private int bytePointer;
    private byte[] data;
    private int w;
    private int h;
    private int fillOrder;
    private int changingElemSize = 0;
    private int[] prevChangingElems;
    private int[] currChangingElems;
    private int lastChangingElement = 0;
    private int compression = 2;
    private int uncompressedMode = 0;
    private int fillBits = 0;
    private int oneD;
    static int[] table1 = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255};
    static int[] table2 = new int[]{0, 128, 192, 224, 240, 248, 252, 254, 255};
    static byte[] flipTable = new byte[]{0, -128, 64, -64, 32, -96, 96, -32, 16, -112, 80, -48, 48, -80, 112, -16, 8, -120, 72, -56, 40, -88, 104, -24, 24, -104, 88, -40, 56, -72, 120, -8, 4, -124, 68, -60, 36, -92, 100, -28, 20, -108, 84, -44, 52, -76, 116, -12, 12, -116, 76, -52, 44, -84, 108, -20, 28, -100, 92, -36, 60, -68, 124, -4, 2, -126, 66, -62, 34, -94, 98, -30, 18, -110, 82, -46, 50, -78, 114, -14, 10, -118, 74, -54, 42, -86, 106, -22, 26, -102, 90, -38, 58, -70, 122, -6, 6, -122, 70, -58, 38, -90, 102, -26, 22, -106, 86, -42, 54, -74, 118, -10, 14, -114, 78, -50, 46, -82, 110, -18, 30, -98, 94, -34, 62, -66, 126, -2, 1, -127, 65, -63, 33, -95, 97, -31, 17, -111, 81, -47, 49, -79, 113, -15, 9, -119, 73, -55, 41, -87, 105, -23, 25, -103, 89, -39, 57, -71, 121, -7, 5, -123, 69, -59, 37, -91, 101, -27, 21, -107, 85, -43, 53, -75, 117, -11, 13, -115, 77, -51, 45, -83, 109, -19, 29, -99, 93, -35, 61, -67, 125, -3, 3, -125, 67, -61, 35, -93, 99, -29, 19, -109, 83, -45, 51, -77, 115, -13, 11, -117, 75, -53, 43, -85, 107, -21, 27, -101, 91, -37, 59, -69, 123, -5, 7, -121, 71, -57, 39, -89, 103, -25, 23, -105, 87, -41, 55, -73, 119, -9, 15, -113, 79, -49, 47, -81, 111, -17, 31, -97, 95, -33, 63, -65, 127, -1};
    static short[] white = new short[]{6430, 6400, 6400, 6400, 3225, 3225, 3225, 3225, 944, 944, 944, 944, 976, 976, 976, 976, 1456, 1456, 1456, 1456, 1488, 1488, 1488, 1488, 718, 718, 718, 718, 718, 718, 718, 718, 750, 750, 750, 750, 750, 750, 750, 750, 1520, 1520, 1520, 1520, 1552, 1552, 1552, 1552, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 428, 654, 654, 654, 654, 654, 654, 654, 654, 1072, 1072, 1072, 1072, 1104, 1104, 1104, 1104, 1136, 1136, 1136, 1136, 1168, 1168, 1168, 1168, 1200, 1200, 1200, 1200, 1232, 1232, 1232, 1232, 622, 622, 622, 622, 622, 622, 622, 622, 1008, 1008, 1008, 1008, 1040, 1040, 1040, 1040, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 44, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 396, 1712, 1712, 1712, 1712, 1744, 1744, 1744, 1744, 846, 846, 846, 846, 846, 846, 846, 846, 1264, 1264, 1264, 1264, 1296, 1296, 1296, 1296, 1328, 1328, 1328, 1328, 1360, 1360, 1360, 1360, 1392, 1392, 1392, 1392, 1424, 1424, 1424, 1424, 686, 686, 686, 686, 686, 686, 686, 686, 910, 910, 910, 910, 910, 910, 910, 910, 1968, 1968, 1968, 1968, 2000, 2000, 2000, 2000, 2032, 2032, 2032, 2032, 16, 16, 16, 16, 10257, 10257, 10257, 10257, 12305, 12305, 12305, 12305, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 330, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 362, 878, 878, 878, 878, 878, 878, 878, 878, 1904, 1904, 1904, 1904, 1936, 1936, 1936, 1936, -18413, -18413, -16365, -16365, -14317, -14317, -10221, -10221, 590, 590, 590, 590, 590, 590, 590, 590, 782, 782, 782, 782, 782, 782, 782, 782, 1584, 1584, 1584, 1584, 1616, 1616, 1616, 1616, 1648, 1648, 1648, 1648, 1680, 1680, 1680, 1680, 814, 814, 814, 814, 814, 814, 814, 814, 1776, 1776, 1776, 1776, 1808, 1808, 1808, 1808, 1840, 1840, 1840, 1840, 1872, 1872, 1872, 1872, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, 6157, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, -12275, 14353, 14353, 14353, 14353, 16401, 16401, 16401, 16401, 22547, 22547, 24595, 24595, 20497, 20497, 20497, 20497, 18449, 18449, 18449, 18449, 26643, 26643, 28691, 28691, 30739, 30739, -32749, -32749, -30701, -30701, -28653, -28653, -26605, -26605, -24557, -24557, -22509, -22509, -20461, -20461, 8207, 8207, 8207, 8207, 8207, 8207, 8207, 8207, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 72, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 4107, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 266, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 298, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 136, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 168, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 460, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 492, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 2059, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232, 232};
    static short[] additionalMakeup = new short[]{28679, 28679, 31752, -32759, -31735, -30711, -29687, -28663, 29703, 29703, 30727, 30727, -27639, -26615, -25591, -24567};
    static short[] initBlack = new short[]{3226, 6412, 200, 168, 38, 38, 134, 134, 100, 100, 100, 100, 68, 68, 68, 68};
    static short[] twoBitBlack = new short[]{292, 260, 226, 226};
    static short[] black = new short[]{62, 62, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 3225, 588, 588, 588, 588, 588, 588, 588, 588, 1680, 1680, 20499, 22547, 24595, 26643, 1776, 1776, 1808, 1808, -24557, -22509, -20461, -18413, 1904, 1904, 1936, 1936, -16365, -14317, 782, 782, 782, 782, 814, 814, 814, 814, -12269, -10221, 10257, 10257, 12305, 12305, 14353, 14353, 16403, 18451, 1712, 1712, 1744, 1744, 28691, 30739, -32749, -30701, -28653, -26605, 2061, 2061, 2061, 2061, 2061, 2061, 2061, 2061, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 424, 750, 750, 750, 750, 1616, 1616, 1648, 1648, 1424, 1424, 1456, 1456, 1488, 1488, 1520, 1520, 1840, 1840, 1872, 1872, 1968, 1968, 8209, 8209, 524, 524, 524, 524, 524, 524, 524, 524, 556, 556, 556, 556, 556, 556, 556, 556, 1552, 1552, 1584, 1584, 2000, 2000, 2032, 2032, 976, 976, 1008, 1008, 1040, 1040, 1072, 1072, 1296, 1296, 1328, 1328, 718, 718, 718, 718, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 456, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 326, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 358, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 490, 4113, 4113, 6161, 6161, 848, 848, 880, 880, 912, 912, 944, 944, 622, 622, 622, 622, 654, 654, 654, 654, 1104, 1104, 1136, 1136, 1168, 1168, 1200, 1200, 1232, 1232, 1264, 1264, 686, 686, 686, 686, 1360, 1360, 1392, 1392, 12, 12, 12, 12, 12, 12, 12, 12, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390, 390};
    static byte[] twoDCodes = new byte[]{80, 88, 23, 71, 30, 30, 62, 62, 4, 4, 4, 4, 4, 4, 4, 4, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41};

    public TIFFFaxDecoder(int n, int n2, int n3) {
        this.fillOrder = n;
        this.w = n2;
        this.h = n3;
        this.bitPointer = 0;
        this.bytePointer = 0;
        this.prevChangingElems = new int[n2];
        this.currChangingElems = new int[n2];
    }

    public static void reverseBits(byte[] arrby) {
        for (int i = 0; i < arrby.length; ++i) {
            arrby[i] = flipTable[arrby[i] & 255];
        }
    }

    public void decode1D(byte[] arrby, byte[] arrby2, int n, int n2) {
        this.data = arrby2;
        int n3 = 0;
        int n4 = (this.w + 7) / 8;
        this.bitPointer = 0;
        this.bytePointer = 0;
        for (int i = 0; i < n2; ++i) {
            this.decodeNextScanline(arrby, n3, n);
            n3 += n4;
        }
    }

    public void decodeNextScanline(byte[] arrby, int n, int n2) {
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        boolean bl = true;
        this.changingElemSize = 0;
        while (n2 < this.w) {
            short s;
            int n6;
            while (bl) {
                n6 = this.nextNBits(10);
                s = white[n6];
                n5 = s & 1;
                n3 = s >>> 1 & 15;
                if (n3 == 12) {
                    int n7 = this.nextLesserThan8Bits(2);
                    n6 = n6 << 2 & 12 | n7;
                    s = additionalMakeup[n6];
                    n3 = s >>> 1 & 7;
                    n4 = s >>> 4 & 4095;
                    n2 += n4;
                    this.updatePointer(4 - n3);
                    continue;
                }
                if (n3 == 0) {
                    throw new RuntimeException("Invalid code encountered.");
                }
                if (n3 == 15) {
                    throw new RuntimeException("EOL code word encountered in White run.");
                }
                n4 = s >>> 5 & 2047;
                n2 += n4;
                this.updatePointer(10 - n3);
                if (n5 != 0) continue;
                bl = false;
                this.currChangingElems[this.changingElemSize++] = n2;
            }
            if (n2 == this.w) {
                if (this.compression != 2) break;
                this.advancePointer();
                break;
            }
            while (!bl) {
                n6 = this.nextLesserThan8Bits(4);
                s = initBlack[n6];
                n5 = s & 1;
                n3 = s >>> 1 & 15;
                n4 = s >>> 5 & 2047;
                if (n4 == 100) {
                    n6 = this.nextNBits(9);
                    s = black[n6];
                    n5 = s & 1;
                    n3 = s >>> 1 & 15;
                    n4 = s >>> 5 & 2047;
                    if (n3 == 12) {
                        this.updatePointer(5);
                        n6 = this.nextLesserThan8Bits(4);
                        s = additionalMakeup[n6];
                        n3 = s >>> 1 & 7;
                        n4 = s >>> 4 & 4095;
                        this.setToBlack(arrby, n, n2, n4);
                        n2 += n4;
                        this.updatePointer(4 - n3);
                        continue;
                    }
                    if (n3 == 15) {
                        throw new RuntimeException("EOL code word encountered in Black run.");
                    }
                    this.setToBlack(arrby, n, n2, n4);
                    n2 += n4;
                    this.updatePointer(9 - n3);
                    if (n5 != 0) continue;
                    bl = true;
                    this.currChangingElems[this.changingElemSize++] = n2;
                    continue;
                }
                if (n4 == 200) {
                    n6 = this.nextLesserThan8Bits(2);
                    s = twoBitBlack[n6];
                    n4 = s >>> 5 & 2047;
                    n3 = s >>> 1 & 15;
                    this.setToBlack(arrby, n, n2, n4);
                    this.updatePointer(2 - n3);
                    bl = true;
                    this.currChangingElems[this.changingElemSize++] = n2 += n4;
                    continue;
                }
                this.setToBlack(arrby, n, n2, n4);
                this.updatePointer(4 - n3);
                bl = true;
                this.currChangingElems[this.changingElemSize++] = n2 += n4;
            }
            if (n2 != this.w) continue;
            if (this.compression != 2) break;
            this.advancePointer();
            break;
        }
        this.currChangingElems[this.changingElemSize++] = n2;
    }

    public void decode2D(byte[] arrby, byte[] arrby2, int n, int n2, long l) {
        this.data = arrby2;
        this.compression = 3;
        this.bitPointer = 0;
        this.bytePointer = 0;
        int n3 = (this.w + 7) / 8;
        int[] arrn = new int[2];
        int n4 = 0;
        this.oneD = (int)(l & 1);
        this.uncompressedMode = (int)((l & 2) >> 1);
        this.fillBits = (int)((l & 4) >> 2);
        if (this.readEOL(true) != 1) {
            throw new RuntimeException("First scanline must be 1D encoded.");
        }
        int n5 = 0;
        this.decodeNextScanline(arrby, n5, n);
        n5 += n3;
        for (int i = 1; i < n2; ++i) {
            if (this.readEOL(false) == 0) {
                int[] arrn2 = this.prevChangingElems;
                this.prevChangingElems = this.currChangingElems;
                this.currChangingElems = arrn2;
                n4 = 0;
                int n6 = -1;
                boolean bl = true;
                int n7 = n;
                this.lastChangingElement = 0;
                while (n7 < this.w) {
                    this.getNextChangingElement(n6, bl, arrn);
                    int n8 = arrn[0];
                    int n9 = arrn[1];
                    int n10 = this.nextLesserThan8Bits(7);
                    n10 = twoDCodes[n10] & 255;
                    int n11 = (n10 & 120) >>> 3;
                    int n12 = n10 & 7;
                    if (n11 == 0) {
                        if (!bl) {
                            this.setToBlack(arrby, n5, n7, n9 - n7);
                        }
                        n7 = n6 = n9;
                        this.updatePointer(7 - n12);
                        continue;
                    }
                    if (n11 == 1) {
                        int n13;
                        this.updatePointer(7 - n12);
                        if (bl) {
                            n13 = this.decodeWhiteCodeWord();
                            this.currChangingElems[n4++] = n7 += n13;
                            n13 = this.decodeBlackCodeWord();
                            this.setToBlack(arrby, n5, n7, n13);
                            this.currChangingElems[n4++] = n7 += n13;
                        } else {
                            n13 = this.decodeBlackCodeWord();
                            this.setToBlack(arrby, n5, n7, n13);
                            this.currChangingElems[n4++] = n7 += n13;
                            n13 = this.decodeWhiteCodeWord();
                            this.currChangingElems[n4++] = n7 += n13;
                        }
                        n6 = n7;
                        continue;
                    }
                    if (n11 <= 8) {
                        int n14 = n8 + (n11 - 5);
                        this.currChangingElems[n4++] = n14;
                        if (!bl) {
                            this.setToBlack(arrby, n5, n7, n14 - n7);
                        }
                        n7 = n6 = n14;
                        bl = !bl;
                        this.updatePointer(7 - n12);
                        continue;
                    }
                    throw new RuntimeException("Invalid code encountered while decoding 2D group 3 compressed data.");
                }
                this.currChangingElems[n4++] = n7;
                this.changingElemSize = n4;
            } else {
                this.decodeNextScanline(arrby, n5, n);
            }
            n5 += n3;
        }
    }

    public void decodeT6(byte[] arrby, byte[] arrby2, int n, int n2, long l) {
        this.data = arrby2;
        this.compression = 4;
        this.bitPointer = 0;
        this.bytePointer = 0;
        int n3 = (this.w + 7) / 8;
        int[] arrn = new int[2];
        this.uncompressedMode = (int)((l & 2) >> 1);
        int[] arrn2 = this.currChangingElems;
        this.changingElemSize = 0;
        arrn2[this.changingElemSize++] = this.w;
        arrn2[this.changingElemSize++] = this.w;
        int n4 = 0;
        for (int i = 0; i < n2; ++i) {
            int n5 = -1;
            boolean bl = true;
            int[] arrn3 = this.prevChangingElems;
            this.prevChangingElems = this.currChangingElems;
            arrn2 = this.currChangingElems = arrn3;
            int n6 = 0;
            int n7 = n;
            this.lastChangingElement = 0;
            while (n7 < this.w) {
                int n8;
                this.getNextChangingElement(n5, bl, arrn);
                int n9 = arrn[0];
                int n10 = arrn[1];
                int n11 = this.nextLesserThan8Bits(7);
                n11 = twoDCodes[n11] & 255;
                int n12 = (n11 & 120) >>> 3;
                int n13 = n11 & 7;
                if (n12 == 0) {
                    if (!bl) {
                        this.setToBlack(arrby, n4, n7, n10 - n7);
                    }
                    n7 = n5 = n10;
                    this.updatePointer(7 - n13);
                    continue;
                }
                if (n12 == 1) {
                    this.updatePointer(7 - n13);
                    if (bl) {
                        n8 = this.decodeWhiteCodeWord();
                        arrn2[n6++] = n7 += n8;
                        n8 = this.decodeBlackCodeWord();
                        this.setToBlack(arrby, n4, n7, n8);
                        arrn2[n6++] = n7 += n8;
                    } else {
                        n8 = this.decodeBlackCodeWord();
                        this.setToBlack(arrby, n4, n7, n8);
                        arrn2[n6++] = n7 += n8;
                        n8 = this.decodeWhiteCodeWord();
                        arrn2[n6++] = n7 += n8;
                    }
                    n5 = n7;
                    continue;
                }
                if (n12 <= 8) {
                    int n14 = n9 + (n12 - 5);
                    arrn2[n6++] = n14;
                    if (!bl) {
                        this.setToBlack(arrby, n4, n7, n14 - n7);
                    }
                    n7 = n5 = n14;
                    bl = !bl;
                    this.updatePointer(7 - n13);
                    continue;
                }
                if (n12 == 11) {
                    if (this.nextLesserThan8Bits(3) != 7) {
                        throw new RuntimeException("Invalid code encountered while decoding 2D group 4 compressed data.");
                    }
                    n8 = 0;
                    boolean bl2 = false;
                    while (!bl2) {
                        while (this.nextLesserThan8Bits(1) != 1) {
                            ++n8;
                        }
                        if (n8 > 5) {
                            if (!bl && (n8 -= 6) > 0) {
                                arrn2[n6++] = n7;
                            }
                            n7 += n8;
                            if (n8 > 0) {
                                bl = true;
                            }
                            if (this.nextLesserThan8Bits(1) == 0) {
                                if (!bl) {
                                    arrn2[n6++] = n7;
                                }
                                bl = true;
                            } else {
                                if (bl) {
                                    arrn2[n6++] = n7;
                                }
                                bl = false;
                            }
                            bl2 = true;
                        }
                        if (n8 == 5) {
                            if (!bl) {
                                arrn2[n6++] = n7;
                            }
                            n7 += n8;
                            bl = true;
                            continue;
                        }
                        arrn2[n6++] = n7 += n8;
                        this.setToBlack(arrby, n4, n7, 1);
                        ++n7;
                        bl = false;
                    }
                    continue;
                }
                n7 = this.w;
                this.updatePointer(7 - n13);
            }
            if (n6 < arrn2.length) {
                arrn2[n6++] = n7;
            }
            this.changingElemSize = n6;
            n4 += n3;
        }
    }

    private void setToBlack(byte[] arrby, int n, int n2, int n3) {
        int n4 = 8 * n + n2;
        int n5 = n4 + n3;
        int n6 = n4 >> 3;
        int n7 = n4 & 7;
        if (n7 > 0) {
            byte by = arrby[n6];
            for (int i = 1 << 7 - n7; i > 0 && n4 < n5; i >>= 1, ++n4) {
                by = (byte)(by | i);
            }
            arrby[n6] = by;
        }
        n6 = n4 >> 3;
        while (n4 < n5 - 7) {
            arrby[n6++] = -1;
            n4 += 8;
        }
        while (n4 < n5) {
            n6 = n4 >> 3;
            byte[] arrby2 = arrby;
            int n8 = n6;
            arrby2[n8] = (byte)(arrby2[n8] | 1 << 7 - (n4 & 7));
            ++n4;
        }
    }

    private int decodeWhiteCodeWord() {
        int n = -1;
        int n2 = 0;
        boolean bl = true;
        while (bl) {
            int n3 = this.nextNBits(10);
            short s = white[n3];
            int n4 = s & 1;
            int n5 = s >>> 1 & 15;
            if (n5 == 12) {
                int n6 = this.nextLesserThan8Bits(2);
                n3 = n3 << 2 & 12 | n6;
                s = additionalMakeup[n3];
                n5 = s >>> 1 & 7;
                n = s >>> 4 & 4095;
                n2 += n;
                this.updatePointer(4 - n5);
                continue;
            }
            if (n5 == 0) {
                throw new RuntimeException("Invalid code encountered.");
            }
            if (n5 == 15) {
                throw new RuntimeException("EOL code word encountered in White run.");
            }
            n = s >>> 5 & 2047;
            n2 += n;
            this.updatePointer(10 - n5);
            if (n4 != 0) continue;
            bl = false;
        }
        return n2;
    }

    private int decodeBlackCodeWord() {
        int n = -1;
        int n2 = 0;
        boolean bl = false;
        while (!bl) {
            int n3 = this.nextLesserThan8Bits(4);
            short s = initBlack[n3];
            int n4 = s & 1;
            int n5 = s >>> 1 & 15;
            n = s >>> 5 & 2047;
            if (n == 100) {
                n3 = this.nextNBits(9);
                s = black[n3];
                n4 = s & 1;
                n5 = s >>> 1 & 15;
                n = s >>> 5 & 2047;
                if (n5 == 12) {
                    this.updatePointer(5);
                    n3 = this.nextLesserThan8Bits(4);
                    s = additionalMakeup[n3];
                    n5 = s >>> 1 & 7;
                    n = s >>> 4 & 4095;
                    n2 += n;
                    this.updatePointer(4 - n5);
                    continue;
                }
                if (n5 == 15) {
                    throw new RuntimeException("EOL code word encountered in Black run.");
                }
                n2 += n;
                this.updatePointer(9 - n5);
                if (n4 != 0) continue;
                bl = true;
                continue;
            }
            if (n == 200) {
                n3 = this.nextLesserThan8Bits(2);
                s = twoBitBlack[n3];
                n = s >>> 5 & 2047;
                n2 += n;
                n5 = s >>> 1 & 15;
                this.updatePointer(2 - n5);
                bl = true;
                continue;
            }
            n2 += n;
            this.updatePointer(4 - n5);
            bl = true;
        }
        return n2;
    }

    private int readEOL(boolean bl) {
        if (this.fillBits == 0) {
            int n = this.nextNBits(12);
            if (bl && n == 0 && this.nextNBits(4) == 1) {
                this.fillBits = 1;
                return 1;
            }
            if (n != 1) {
                throw new RuntimeException("Scanline must begin with EOL code word.");
            }
        } else if (this.fillBits == 1) {
            int n;
            int n2 = 8 - this.bitPointer;
            if (this.nextNBits(n2) != 0) {
                throw new RuntimeException("All fill bits preceding EOL code must be 0.");
            }
            if (n2 < 4 && this.nextNBits(8) != 0) {
                throw new RuntimeException("All fill bits preceding EOL code must be 0.");
            }
            while ((n = this.nextNBits(8)) != 1) {
                if (n == 0) continue;
                throw new RuntimeException("All fill bits preceding EOL code must be 0.");
            }
        }
        if (this.oneD == 0) {
            return 1;
        }
        return this.nextLesserThan8Bits(1);
    }

    private void getNextChangingElement(int n, boolean bl, int[] arrn) {
        int n2;
        int n3;
        int[] arrn2 = this.prevChangingElems;
        int n4 = this.changingElemSize;
        int n5 = n3 = this.lastChangingElement > 0 ? this.lastChangingElement - 1 : 0;
        n3 = bl ? (n3 &= -2) : (n3 |= 1);
        for (n2 = n3; n2 < n4; n2 += 2) {
            int n6 = arrn2[n2];
            if (n6 <= n) continue;
            this.lastChangingElement = n2;
            arrn[0] = n6;
            break;
        }
        if (n2 + 1 < n4) {
            arrn[1] = arrn2[n2 + 1];
        }
    }

    private int nextNBits(int n) {
        byte by;
        byte by2;
        byte by3;
        int n2 = this.data.length - 1;
        int n3 = this.bytePointer;
        if (this.fillOrder == 1) {
            by = this.data[n3];
            if (n3 == n2) {
                by2 = 0;
                by3 = 0;
            } else if (n3 + 1 == n2) {
                by2 = this.data[n3 + 1];
                by3 = 0;
            } else {
                by2 = this.data[n3 + 1];
                by3 = this.data[n3 + 2];
            }
        } else if (this.fillOrder == 2) {
            by = flipTable[this.data[n3] & 255];
            if (n3 == n2) {
                by2 = 0;
                by3 = 0;
            } else if (n3 + 1 == n2) {
                by2 = flipTable[this.data[n3 + 1] & 255];
                by3 = 0;
            } else {
                by2 = flipTable[this.data[n3 + 1] & 255];
                by3 = flipTable[this.data[n3 + 2] & 255];
            }
        } else {
            throw new RuntimeException("TIFF_FILL_ORDER tag must be either 1 or 2.");
        }
        int n4 = 8 - this.bitPointer;
        int n5 = n - n4;
        int n6 = 0;
        if (n5 > 8) {
            n6 = n5 - 8;
            n5 = 8;
        }
        ++this.bytePointer;
        int n7 = (by & table1[n4]) << n - n4;
        int n8 = (by2 & table2[n5]) >>> 8 - n5;
        int n9 = 0;
        if (n6 != 0) {
            n8 <<= n6;
            n9 = (by3 & table2[n6]) >>> 8 - n6;
            n8 |= n9;
            ++this.bytePointer;
            this.bitPointer = n6;
        } else if (n5 == 8) {
            this.bitPointer = 0;
            ++this.bytePointer;
        } else {
            this.bitPointer = n5;
        }
        int n10 = n7 | n8;
        return n10;
    }

    private int nextLesserThan8Bits(int n) {
        byte by;
        byte by2;
        int n2;
        int n3 = this.data.length - 1;
        int n4 = this.bytePointer;
        if (this.fillOrder == 1) {
            by = this.data[n4];
            by2 = n4 == n3 ? 0 : this.data[n4 + 1];
        } else if (this.fillOrder == 2) {
            by = flipTable[this.data[n4] & 255];
            by2 = n4 == n3 ? 0 : flipTable[this.data[n4 + 1] & 255];
        } else {
            throw new RuntimeException("TIFF_FILL_ORDER tag must be either 1 or 2.");
        }
        int n5 = 8 - this.bitPointer;
        int n6 = n - n5;
        int n7 = n5 - n;
        if (n7 >= 0) {
            n2 = (by & table1[n5]) >>> n7;
            this.bitPointer += n;
            if (this.bitPointer == 8) {
                this.bitPointer = 0;
                ++this.bytePointer;
            }
        } else {
            n2 = (by & table1[n5]) << - n7;
            int n8 = (by2 & table2[n6]) >>> 8 - n6;
            n2 |= n8;
            ++this.bytePointer;
            this.bitPointer = n6;
        }
        return n2;
    }

    private void updatePointer(int n) {
        int n2 = this.bitPointer - n;
        if (n2 < 0) {
            --this.bytePointer;
            this.bitPointer = 8 + n2;
        } else {
            this.bitPointer = n2;
        }
    }

    private boolean advancePointer() {
        if (this.bitPointer != 0) {
            ++this.bytePointer;
            this.bitPointer = 0;
        }
        return true;
    }
}

