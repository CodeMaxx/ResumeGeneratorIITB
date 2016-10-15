/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec;

import com.lowagie.text.pdf.ByteBuffer;

public class CCITTG4Encoder {
    private int rowbytes;
    private int rowpixels;
    private int bit = 8;
    private int data;
    private byte[] refline;
    private ByteBuffer outBuf = new ByteBuffer(1024);
    private byte[] dataBp;
    private int offsetData;
    private int sizeData;
    private static byte[] zeroruns = new byte[]{8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static byte[] oneruns = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 7, 8};
    private static final int LENGTH = 0;
    private static final int CODE = 1;
    private static final int RUNLEN = 2;
    private static final int EOL = 1;
    private static final int G3CODE_EOL = -1;
    private static final int G3CODE_INVALID = -2;
    private static final int G3CODE_EOF = -3;
    private static final int G3CODE_INCOMP = -4;
    private int[][] TIFFFaxWhiteCodes = new int[][]{{8, 53, 0}, {6, 7, 1}, {4, 7, 2}, {4, 8, 3}, {4, 11, 4}, {4, 12, 5}, {4, 14, 6}, {4, 15, 7}, {5, 19, 8}, {5, 20, 9}, {5, 7, 10}, {5, 8, 11}, {6, 8, 12}, {6, 3, 13}, {6, 52, 14}, {6, 53, 15}, {6, 42, 16}, {6, 43, 17}, {7, 39, 18}, {7, 12, 19}, {7, 8, 20}, {7, 23, 21}, {7, 3, 22}, {7, 4, 23}, {7, 40, 24}, {7, 43, 25}, {7, 19, 26}, {7, 36, 27}, {7, 24, 28}, {8, 2, 29}, {8, 3, 30}, {8, 26, 31}, {8, 27, 32}, {8, 18, 33}, {8, 19, 34}, {8, 20, 35}, {8, 21, 36}, {8, 22, 37}, {8, 23, 38}, {8, 40, 39}, {8, 41, 40}, {8, 42, 41}, {8, 43, 42}, {8, 44, 43}, {8, 45, 44}, {8, 4, 45}, {8, 5, 46}, {8, 10, 47}, {8, 11, 48}, {8, 82, 49}, {8, 83, 50}, {8, 84, 51}, {8, 85, 52}, {8, 36, 53}, {8, 37, 54}, {8, 88, 55}, {8, 89, 56}, {8, 90, 57}, {8, 91, 58}, {8, 74, 59}, {8, 75, 60}, {8, 50, 61}, {8, 51, 62}, {8, 52, 63}, {5, 27, 64}, {5, 18, 128}, {6, 23, 192}, {7, 55, 256}, {8, 54, 320}, {8, 55, 384}, {8, 100, 448}, {8, 101, 512}, {8, 104, 576}, {8, 103, 640}, {9, 204, 704}, {9, 205, 768}, {9, 210, 832}, {9, 211, 896}, {9, 212, 960}, {9, 213, 1024}, {9, 214, 1088}, {9, 215, 1152}, {9, 216, 1216}, {9, 217, 1280}, {9, 218, 1344}, {9, 219, 1408}, {9, 152, 1472}, {9, 153, 1536}, {9, 154, 1600}, {6, 24, 1664}, {9, 155, 1728}, {11, 8, 1792}, {11, 12, 1856}, {11, 13, 1920}, {12, 18, 1984}, {12, 19, 2048}, {12, 20, 2112}, {12, 21, 2176}, {12, 22, 2240}, {12, 23, 2304}, {12, 28, 2368}, {12, 29, 2432}, {12, 30, 2496}, {12, 31, 2560}, {12, 1, -1}, {9, 1, -2}, {10, 1, -2}, {11, 1, -2}, {12, 0, -2}};
    private int[][] TIFFFaxBlackCodes = new int[][]{{10, 55, 0}, {3, 2, 1}, {2, 3, 2}, {2, 2, 3}, {3, 3, 4}, {4, 3, 5}, {4, 2, 6}, {5, 3, 7}, {6, 5, 8}, {6, 4, 9}, {7, 4, 10}, {7, 5, 11}, {7, 7, 12}, {8, 4, 13}, {8, 7, 14}, {9, 24, 15}, {10, 23, 16}, {10, 24, 17}, {10, 8, 18}, {11, 103, 19}, {11, 104, 20}, {11, 108, 21}, {11, 55, 22}, {11, 40, 23}, {11, 23, 24}, {11, 24, 25}, {12, 202, 26}, {12, 203, 27}, {12, 204, 28}, {12, 205, 29}, {12, 104, 30}, {12, 105, 31}, {12, 106, 32}, {12, 107, 33}, {12, 210, 34}, {12, 211, 35}, {12, 212, 36}, {12, 213, 37}, {12, 214, 38}, {12, 215, 39}, {12, 108, 40}, {12, 109, 41}, {12, 218, 42}, {12, 219, 43}, {12, 84, 44}, {12, 85, 45}, {12, 86, 46}, {12, 87, 47}, {12, 100, 48}, {12, 101, 49}, {12, 82, 50}, {12, 83, 51}, {12, 36, 52}, {12, 55, 53}, {12, 56, 54}, {12, 39, 55}, {12, 40, 56}, {12, 88, 57}, {12, 89, 58}, {12, 43, 59}, {12, 44, 60}, {12, 90, 61}, {12, 102, 62}, {12, 103, 63}, {10, 15, 64}, {12, 200, 128}, {12, 201, 192}, {12, 91, 256}, {12, 51, 320}, {12, 52, 384}, {12, 53, 448}, {13, 108, 512}, {13, 109, 576}, {13, 74, 640}, {13, 75, 704}, {13, 76, 768}, {13, 77, 832}, {13, 114, 896}, {13, 115, 960}, {13, 116, 1024}, {13, 117, 1088}, {13, 118, 1152}, {13, 119, 1216}, {13, 82, 1280}, {13, 83, 1344}, {13, 84, 1408}, {13, 85, 1472}, {13, 90, 1536}, {13, 91, 1600}, {13, 100, 1664}, {13, 101, 1728}, {11, 8, 1792}, {11, 12, 1856}, {11, 13, 1920}, {12, 18, 1984}, {12, 19, 2048}, {12, 20, 2112}, {12, 21, 2176}, {12, 22, 2240}, {12, 23, 2304}, {12, 28, 2368}, {12, 29, 2432}, {12, 30, 2496}, {12, 31, 2560}, {12, 1, -1}, {9, 1, -2}, {10, 1, -2}, {11, 1, -2}, {12, 0, -2}};
    private int[] horizcode = new int[]{3, 1, 0};
    private int[] passcode = new int[]{4, 1, 0};
    private int[][] vcodes = new int[][]{{7, 3, 0}, {6, 3, 0}, {3, 3, 0}, {1, 1, 0}, {3, 2, 0}, {6, 2, 0}, {7, 2, 0}};
    private int[] msbmask = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255};

    public CCITTG4Encoder(int n) {
        this.rowpixels = n;
        this.rowbytes = (this.rowpixels + 7) / 8;
        this.refline = new byte[this.rowbytes];
    }

    public void fax4Encode(byte[] arrby, int n, int n2) {
        this.dataBp = arrby;
        this.offsetData = n;
        this.sizeData = n2;
        while (this.sizeData > 0) {
            this.Fax3Encode2DRow();
            System.arraycopy(this.dataBp, this.offsetData, this.refline, 0, this.rowbytes);
            this.offsetData += this.rowbytes;
            this.sizeData -= this.rowbytes;
        }
    }

    public static byte[] compress(byte[] arrby, int n, int n2) {
        CCITTG4Encoder cCITTG4Encoder = new CCITTG4Encoder(n);
        cCITTG4Encoder.fax4Encode(arrby, 0, cCITTG4Encoder.rowbytes * n2);
        return cCITTG4Encoder.close();
    }

    public void fax4Encode(byte[] arrby, int n) {
        this.fax4Encode(arrby, 0, this.rowbytes * n);
    }

    private void putcode(int[] arrn) {
        this.putBits(arrn[1], arrn[0]);
    }

    private void putspan(int n, int[][] arrn) {
        int n2;
        int n3;
        int[] arrn2;
        while (n >= 2624) {
            arrn2 = arrn[103];
            n2 = arrn2[1];
            n3 = arrn2[0];
            this.putBits(n2, n3);
            n -= arrn2[2];
        }
        if (n >= 64) {
            arrn2 = arrn[63 + (n >> 6)];
            n2 = arrn2[1];
            n3 = arrn2[0];
            this.putBits(n2, n3);
            n -= arrn2[2];
        }
        n2 = arrn[n][1];
        n3 = arrn[n][0];
        this.putBits(n2, n3);
    }

    private void putBits(int n, int n2) {
        while (n2 > this.bit) {
            this.data |= n >> n2 - this.bit;
            n2 -= this.bit;
            this.outBuf.append((byte)this.data);
            this.data = 0;
            this.bit = 8;
        }
        this.data |= (n & this.msbmask[n2]) << this.bit - n2;
        this.bit -= n2;
        if (this.bit == 0) {
            this.outBuf.append((byte)this.data);
            this.data = 0;
            this.bit = 8;
        }
    }

    private void Fax3Encode2DRow() {
        int n;
        int n2 = 0;
        int n3 = this.pixel(this.dataBp, this.offsetData, 0) != 0 ? 0 : CCITTG4Encoder.finddiff(this.dataBp, this.offsetData, 0, this.rowpixels, 0);
        int n4 = n = this.pixel(this.refline, 0, 0) != 0 ? 0 : CCITTG4Encoder.finddiff(this.refline, 0, 0, this.rowpixels, 0);
        do {
            int n5;
            if ((n5 = CCITTG4Encoder.finddiff2(this.refline, 0, n, this.rowpixels, this.pixel(this.refline, 0, n))) >= n3) {
                int n6 = n - n3;
                if (-3 > n6 || n6 > 3) {
                    int n7 = CCITTG4Encoder.finddiff2(this.dataBp, this.offsetData, n3, this.rowpixels, this.pixel(this.dataBp, this.offsetData, n3));
                    this.putcode(this.horizcode);
                    if (n2 + n3 == 0 || this.pixel(this.dataBp, this.offsetData, n2) == 0) {
                        this.putspan(n3 - n2, this.TIFFFaxWhiteCodes);
                        this.putspan(n7 - n3, this.TIFFFaxBlackCodes);
                    } else {
                        this.putspan(n3 - n2, this.TIFFFaxBlackCodes);
                        this.putspan(n7 - n3, this.TIFFFaxWhiteCodes);
                    }
                    n2 = n7;
                } else {
                    this.putcode(this.vcodes[n6 + 3]);
                    n2 = n3;
                }
            } else {
                this.putcode(this.passcode);
                n2 = n5;
            }
            if (n2 >= this.rowpixels) break;
            n3 = CCITTG4Encoder.finddiff(this.dataBp, this.offsetData, n2, this.rowpixels, this.pixel(this.dataBp, this.offsetData, n2));
            n = CCITTG4Encoder.finddiff(this.refline, 0, n2, this.rowpixels, this.pixel(this.dataBp, this.offsetData, n2) ^ 1);
            n = CCITTG4Encoder.finddiff(this.refline, 0, n, this.rowpixels, this.pixel(this.dataBp, this.offsetData, n2));
        } while (true);
    }

    private void Fax4PostEncode() {
        this.putBits(1, 12);
        this.putBits(1, 12);
        if (this.bit != 8) {
            this.outBuf.append((byte)this.data);
            this.data = 0;
            this.bit = 8;
        }
    }

    public byte[] close() {
        this.Fax4PostEncode();
        return this.outBuf.toByteArray();
    }

    private int pixel(byte[] arrby, int n, int n2) {
        if (n2 >= this.rowpixels) {
            return 0;
        }
        return (arrby[n + (n2 >> 3)] & 255) >> 7 - (n2 & 7) & 1;
    }

    private static int find1span(byte[] arrby, int n, int n2, int n3) {
        int n4;
        int n5;
        int n6 = n3 - n2;
        int n7 = n + (n2 >> 3);
        if (n6 > 0 && (n5 = n2 & 7) != 0) {
            n4 = oneruns[arrby[n7] << n5 & 255];
            if (n4 > 8 - n5) {
                n4 = 8 - n5;
            }
            if (n4 > n6) {
                n4 = n6;
            }
            if (n5 + n4 < 8) {
                return n4;
            }
            n6 -= n4;
            ++n7;
        } else {
            n4 = 0;
        }
        while (n6 >= 8) {
            if (arrby[n7] != -1) {
                return n4 + oneruns[arrby[n7] & 255];
            }
            n4 += 8;
            n6 -= 8;
            ++n7;
        }
        if (n6 > 0) {
            n5 = oneruns[arrby[n7] & 255];
            n4 += n5 > n6 ? n6 : n5;
        }
        return n4;
    }

    private static int find0span(byte[] arrby, int n, int n2, int n3) {
        int n4;
        int n5;
        int n6 = n3 - n2;
        int n7 = n + (n2 >> 3);
        if (n6 > 0 && (n5 = n2 & 7) != 0) {
            n4 = zeroruns[arrby[n7] << n5 & 255];
            if (n4 > 8 - n5) {
                n4 = 8 - n5;
            }
            if (n4 > n6) {
                n4 = n6;
            }
            if (n5 + n4 < 8) {
                return n4;
            }
            n6 -= n4;
            ++n7;
        } else {
            n4 = 0;
        }
        while (n6 >= 8) {
            if (arrby[n7] != 0) {
                return n4 + zeroruns[arrby[n7] & 255];
            }
            n4 += 8;
            n6 -= 8;
            ++n7;
        }
        if (n6 > 0) {
            n5 = zeroruns[arrby[n7] & 255];
            n4 += n5 > n6 ? n6 : n5;
        }
        return n4;
    }

    private static int finddiff(byte[] arrby, int n, int n2, int n3, int n4) {
        return n2 + (n4 != 0 ? CCITTG4Encoder.find1span(arrby, n, n2, n3) : CCITTG4Encoder.find0span(arrby, n, n2, n3));
    }

    private static int finddiff2(byte[] arrby, int n, int n2, int n3, int n4) {
        return n2 < n3 ? CCITTG4Encoder.finddiff(arrby, n, n2, n3, n4) : n3;
    }
}

