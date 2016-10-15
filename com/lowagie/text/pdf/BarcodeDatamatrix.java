/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.codec.CCITTG4Encoder;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Hashtable;

public class BarcodeDatamatrix {
    public static final int DM_NO_ERROR = 0;
    public static final int DM_ERROR_TEXT_TOO_BIG = 1;
    public static final int DM_ERROR_INVALID_SQUARE = 3;
    public static final int DM_ERROR_EXTENSION = 5;
    public static final int DM_AUTO = 0;
    public static final int DM_ASCII = 1;
    public static final int DM_C40 = 2;
    public static final int DM_TEXT = 3;
    public static final int DM_B256 = 4;
    public static final int DM_X21 = 5;
    public static final int DM_EDIFACT = 6;
    public static final int DM_RAW = 7;
    public static final int DM_EXTENSION = 32;
    public static final int DM_TEST = 64;
    private static final DmParams[] dmSizes = new DmParams[]{new DmParams(10, 10, 10, 10, 3, 3, 5), new DmParams(12, 12, 12, 12, 5, 5, 7), new DmParams(8, 18, 8, 18, 5, 5, 7), new DmParams(14, 14, 14, 14, 8, 8, 10), new DmParams(8, 32, 8, 16, 10, 10, 11), new DmParams(16, 16, 16, 16, 12, 12, 12), new DmParams(12, 26, 12, 26, 16, 16, 14), new DmParams(18, 18, 18, 18, 18, 18, 14), new DmParams(20, 20, 20, 20, 22, 22, 18), new DmParams(12, 36, 12, 18, 22, 22, 18), new DmParams(22, 22, 22, 22, 30, 30, 20), new DmParams(16, 36, 16, 18, 32, 32, 24), new DmParams(24, 24, 24, 24, 36, 36, 24), new DmParams(26, 26, 26, 26, 44, 44, 28), new DmParams(16, 48, 16, 24, 49, 49, 28), new DmParams(32, 32, 16, 16, 62, 62, 36), new DmParams(36, 36, 18, 18, 86, 86, 42), new DmParams(40, 40, 20, 20, 114, 114, 48), new DmParams(44, 44, 22, 22, 144, 144, 56), new DmParams(48, 48, 24, 24, 174, 174, 68), new DmParams(52, 52, 26, 26, 204, 102, 42), new DmParams(64, 64, 16, 16, 280, 140, 56), new DmParams(72, 72, 18, 18, 368, 92, 36), new DmParams(80, 80, 20, 20, 456, 114, 48), new DmParams(88, 88, 22, 22, 576, 144, 56), new DmParams(96, 96, 24, 24, 696, 174, 68), new DmParams(104, 104, 26, 26, 816, 136, 56), new DmParams(120, 120, 20, 20, 1050, 175, 68), new DmParams(132, 132, 22, 22, 1304, 163, 62), new DmParams(144, 144, 24, 24, 1558, 156, 62)};
    private static final String x12 = "\r*> 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private int extOut;
    private short[] place;
    private byte[] image;
    private int height;
    private int width;
    private int ws;
    private int options;

    private void setBit(int n, int n2, int n3) {
        byte[] arrby = this.image;
        int n4 = n2 * n3 + n / 8;
        arrby[n4] = (byte)(arrby[n4] | (byte)(128 >> (n & 7)));
    }

    private void draw(byte[] arrby, int n, DmParams dmParams) {
        int n2;
        int n3;
        int n4 = (dmParams.width + this.ws * 2 + 7) / 8;
        Arrays.fill(this.image, 0);
        for (n3 = this.ws; n3 < dmParams.height + this.ws; n3 += dmParams.heightSection) {
            for (n2 = this.ws; n2 < dmParams.width + this.ws; n2 += 2) {
                this.setBit(n2, n3, n4);
            }
        }
        for (n3 = dmParams.heightSection - 1 + this.ws; n3 < dmParams.height + this.ws; n3 += dmParams.heightSection) {
            for (n2 = this.ws; n2 < dmParams.width + this.ws; ++n2) {
                this.setBit(n2, n3, n4);
            }
        }
        for (n3 = this.ws; n3 < dmParams.width + this.ws; n3 += dmParams.widthSection) {
            for (n2 = this.ws; n2 < dmParams.height + this.ws; ++n2) {
                this.setBit(n3, n2, n4);
            }
        }
        for (n3 = dmParams.widthSection - 1 + this.ws; n3 < dmParams.width + this.ws; n3 += dmParams.widthSection) {
            for (n2 = 1 + this.ws; n2 < dmParams.height + this.ws; n2 += 2) {
                this.setBit(n3, n2, n4);
            }
        }
        int n5 = 0;
        for (int i = 0; i < dmParams.height; i += dmParams.heightSection) {
            for (int j = 1; j < dmParams.heightSection - 1; ++j) {
                for (int k = 0; k < dmParams.width; k += dmParams.widthSection) {
                    for (int i2 = 1; i2 < dmParams.widthSection - 1; ++i2) {
                        short s;
                        if ((s = this.place[n5++]) != 1 && (s <= 1 || (arrby[s / 8 - 1] & 255 & 128 >> s % 8) == 0)) continue;
                        this.setBit(i2 + k + this.ws, j + i + this.ws, n4);
                    }
                }
            }
        }
    }

    private static void makePadding(byte[] arrby, int n, int n2) {
        if (n2 <= 0) {
            return;
        }
        arrby[n++] = -127;
        while (--n2 > 0) {
            int n3 = 129 + (n + 1) * 149 % 253 + 1;
            if (n3 > 254) {
                n3 -= 254;
            }
            arrby[n++] = (byte)n3;
        }
    }

    private static boolean isDigit(int n) {
        return n >= 48 && n <= 57;
    }

    private static int asciiEncodation(byte[] arrby, int n, int n2, byte[] arrby2, int n3, int n4) {
        int n5 = n;
        int n6 = n3;
        n4 += n3;
        while (n5 < (n2 += n)) {
            int n7;
            if (n6 >= n4) {
                return -1;
            }
            if (BarcodeDatamatrix.isDigit(n7 = arrby[n5++] & 255) && n5 < n2 && BarcodeDatamatrix.isDigit(arrby[n5] & 255)) {
                arrby2[n6++] = (byte)((n7 - 48) * 10 + (arrby[n5++] & 255) - 48 + 130);
                continue;
            }
            if (n7 > 127) {
                if (n6 + 1 >= n4) {
                    return -1;
                }
                arrby2[n6++] = -21;
                arrby2[n6++] = (byte)(n7 - 128 + 1);
                continue;
            }
            arrby2[n6++] = (byte)(n7 + 1);
        }
        return n6 - n3;
    }

    private static int b256Encodation(byte[] arrby, int n, int n2, byte[] arrby2, int n3, int n4) {
        int n5;
        if (n2 == 0) {
            return 0;
        }
        if (n2 < 250 && n2 + 2 > n4) {
            return -1;
        }
        if (n2 >= 250 && n2 + 3 > n4) {
            return -1;
        }
        arrby2[n3] = -25;
        if (n2 < 250) {
            arrby2[n3 + 1] = (byte)n2;
            n5 = 2;
        } else {
            arrby2[n3 + 1] = (byte)(n2 / 250 + 249);
            arrby2[n3 + 2] = (byte)(n2 % 250);
            n5 = 3;
        }
        System.arraycopy(arrby, n, arrby2, n5 + n3, n2);
        for (int i = n3 + 1; i < (n5 += n2 + n3); ++i) {
            int n6 = arrby2[i] & 255;
            int n7 = 149 * (i + 1) % 255 + 1;
            int n8 = n6 + n7;
            if (n8 > 255) {
                n8 -= 256;
            }
            arrby2[i] = (byte)n8;
        }
        return n5 - n3;
    }

    private static int X12Encodation(byte[] arrby, int n, int n2, byte[] arrby2, int n3, int n4) {
        int n5;
        int n6;
        if (n2 == 0) {
            return 0;
        }
        int n7 = 0;
        byte[] arrby3 = new byte[n2];
        int n8 = 0;
        for (n6 = 0; n6 < n2; ++n6) {
            int n9 = "\r*> 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(arrby[n6 + n]);
            if (n9 >= 0) {
                arrby3[n6] = (byte)n9;
                ++n8;
                continue;
            }
            arrby3[n6] = 100;
            if (n8 >= 6) {
                n8 -= n8 / 3 * 3;
            }
            for (n5 = 0; n5 < n8; ++n5) {
                arrby3[n6 - n5 - 1] = 100;
            }
            n8 = 0;
        }
        if (n8 >= 6) {
            n8 -= n8 / 3 * 3;
        }
        for (n5 = 0; n5 < n8; ++n5) {
            arrby3[n6 - n5 - 1] = 100;
        }
        int n10 = 0;
        for (n6 = 0; n6 < n2; ++n6) {
            int n11;
            n10 = arrby3[n6];
            if (n7 >= n4) break;
            if (n10 < 40) {
                if (n6 == 0 || n6 > 0 && arrby3[n6 - 1] > 40) {
                    arrby2[n3 + n7++] = -18;
                }
                if (n7 + 2 > n4) break;
                int n12 = 1600 * arrby3[n6] + 40 * arrby3[n6 + 1] + arrby3[n6 + 2] + 1;
                arrby2[n3 + n7++] = (byte)(n12 / 256);
                arrby2[n3 + n7++] = (byte)n12;
                n6 += 2;
                continue;
            }
            if (n6 > 0 && arrby3[n6 - 1] < 40) {
                arrby2[n3 + n7++] = -2;
            }
            if ((n11 = arrby[n6 + n] & 255) > 127) {
                arrby2[n3 + n7++] = -21;
                n11 -= 128;
            }
            if (n7 >= n4) break;
            arrby2[n3 + n7++] = (byte)(n11 + 1);
        }
        n10 = 100;
        if (n2 > 0) {
            n10 = arrby3[n2 - 1];
        }
        if (n6 != n2 || n10 < 40 && n7 >= n4) {
            return -1;
        }
        if (n10 < 40) {
            arrby2[n3 + n7++] = -2;
        }
        return n7;
    }

    private static int EdifactEncodation(byte[] arrby, int n, int n2, byte[] arrby2, int n3, int n4) {
        int n5;
        if (n2 == 0) {
            return 0;
        }
        int n6 = 0;
        int n7 = 0;
        int n8 = 18;
        boolean bl = true;
        for (n5 = 0; n5 < n2; ++n5) {
            int n9 = arrby[n5 + n] & 255;
            if (((n9 & 224) == 64 || (n9 & 224) == 32) && n9 != 95) {
                if (bl) {
                    if (n6 + 1 > n4) break;
                    arrby2[n3 + n6++] = -16;
                    bl = false;
                }
                n7 |= (n9 &= 63) << n8;
                if (n8 == 0) {
                    if (n6 + 3 > n4) break;
                    arrby2[n3 + n6++] = (byte)(n7 >> 16);
                    arrby2[n3 + n6++] = (byte)(n7 >> 8);
                    arrby2[n3 + n6++] = (byte)n7;
                    n7 = 0;
                    n8 = 18;
                    continue;
                }
                n8 -= 6;
                continue;
            }
            if (!bl) {
                n7 |= 31 << n8;
                if (n6 + (3 - n8 / 8) > n4) break;
                arrby2[n3 + n6++] = (byte)(n7 >> 16);
                if (n8 <= 12) {
                    arrby2[n3 + n6++] = (byte)(n7 >> 8);
                }
                if (n8 <= 6) {
                    arrby2[n3 + n6++] = (byte)n7;
                }
                bl = true;
                n8 = 18;
                n7 = 0;
            }
            if (n9 > 127) {
                if (n6 >= n4) break;
                arrby2[n3 + n6++] = -21;
                n9 -= 128;
            }
            if (n6 >= n4) break;
            arrby2[n3 + n6++] = (byte)(n9 + 1);
        }
        if (n5 != n2) {
            return -1;
        }
        if (!bl) {
            n7 |= 31 << n8;
            if (n6 + (3 - n8 / 8) > n4) {
                return -1;
            }
            arrby2[n3 + n6++] = (byte)(n7 >> 16);
            if (n8 <= 12) {
                arrby2[n3 + n6++] = (byte)(n7 >> 8);
            }
            if (n8 <= 6) {
                arrby2[n3 + n6++] = (byte)n7;
            }
        }
        return n6;
    }

    private static int C40OrTextEncodation(byte[] arrby, int n, int n2, byte[] arrby2, int n3, int n4, boolean bl) {
        String string;
        int n5;
        String string2;
        if (n2 == 0) {
            return 0;
        }
        int n6 = 0;
        int n7 = 0;
        arrby2[n3 + n7++] = bl ? -26 : -17;
        String string3 = "!\"#$%&'()*+,-./:;<=>?@[\\]^_";
        if (bl) {
            string = " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            string2 = "`abcdefghijklmnopqrstuvwxyz{|}~";
        } else {
            string = " 0123456789abcdefghijklmnopqrstuvwxyz";
            string2 = "`ABCDEFGHIJKLMNOPQRSTUVWXYZ{|}~";
        }
        int[] arrn = new int[n2 * 4 + 10];
        int n8 = 0;
        int n9 = 0;
        int n10 = 0;
        while (n6 < n2) {
            int n11;
            int n12;
            if (n8 % 3 == 0) {
                n9 = n6;
                n10 = n8;
            }
            if ((n11 = arrby[n + n6++] & 255) > 127) {
                n11 -= 128;
                arrn[n8++] = 1;
                arrn[n8++] = 30;
            }
            if ((n12 = string.indexOf((char)n11)) >= 0) {
                arrn[n8++] = n12 + 3;
                continue;
            }
            if (n11 < 32) {
                arrn[n8++] = 0;
                arrn[n8++] = n11;
                continue;
            }
            n12 = string3.indexOf((char)n11);
            if (n12 >= 0) {
                arrn[n8++] = 1;
                arrn[n8++] = n12;
                continue;
            }
            n12 = string2.indexOf((char)n11);
            if (n12 < 0) continue;
            arrn[n8++] = 2;
            arrn[n8++] = n12;
        }
        if (n8 % 3 != 0) {
            n6 = n9;
            n8 = n10;
        }
        if (n8 / 3 * 2 > n4 - 2) {
            return -1;
        }
        for (n5 = 0; n5 < n8; n5 += 3) {
            int n13 = 1600 * arrn[n5] + 40 * arrn[n5 + 1] + arrn[n5 + 2] + 1;
            arrby2[n3 + n7++] = (byte)(n13 / 256);
            arrby2[n3 + n7++] = (byte)n13;
        }
        arrby2[n7++] = -2;
        n5 = BarcodeDatamatrix.asciiEncodation(arrby, n6, n2 - n6, arrby2, n7, n4 - n7);
        if (n5 < 0) {
            return n5;
        }
        return n7 + n5;
    }

    private static int getEncodation(byte[] arrby, int n, int n2, byte[] arrby2, int n3, int n4, int n5, boolean bl) {
        int[] arrn = new int[6];
        if (n4 < 0) {
            return -1;
        }
        int n6 = -1;
        if ((n5 &= 7) == 0) {
            arrn[0] = BarcodeDatamatrix.asciiEncodation(arrby, n, n2, arrby2, n3, n4);
            if (bl && arrn[0] >= 0) {
                return arrn[0];
            }
            arrn[1] = BarcodeDatamatrix.C40OrTextEncodation(arrby, n, n2, arrby2, n3, n4, false);
            if (bl && arrn[1] >= 0) {
                return arrn[1];
            }
            arrn[2] = BarcodeDatamatrix.C40OrTextEncodation(arrby, n, n2, arrby2, n3, n4, true);
            if (bl && arrn[2] >= 0) {
                return arrn[2];
            }
            arrn[3] = BarcodeDatamatrix.b256Encodation(arrby, n, n2, arrby2, n3, n4);
            if (bl && arrn[3] >= 0) {
                return arrn[3];
            }
            arrn[4] = BarcodeDatamatrix.X12Encodation(arrby, n, n2, arrby2, n3, n4);
            if (bl && arrn[4] >= 0) {
                return arrn[4];
            }
            arrn[5] = BarcodeDatamatrix.EdifactEncodation(arrby, n, n2, arrby2, n3, n4);
            if (bl && arrn[5] >= 0) {
                return arrn[5];
            }
            if (arrn[0] < 0 && arrn[1] < 0 && arrn[2] < 0 && arrn[3] < 0 && arrn[4] < 0 && arrn[5] < 0) {
                return -1;
            }
            int n7 = 0;
            n6 = 99999;
            for (int i = 0; i < 6; ++i) {
                if (arrn[i] < 0 || arrn[i] >= n6) continue;
                n6 = arrn[i];
                n7 = i;
            }
            if (n7 == 0) {
                n6 = BarcodeDatamatrix.asciiEncodation(arrby, n, n2, arrby2, n3, n4);
            } else if (n7 == 1) {
                n6 = BarcodeDatamatrix.C40OrTextEncodation(arrby, n, n2, arrby2, n3, n4, false);
            } else if (n7 == 2) {
                n6 = BarcodeDatamatrix.C40OrTextEncodation(arrby, n, n2, arrby2, n3, n4, true);
            } else if (n7 == 3) {
                n6 = BarcodeDatamatrix.b256Encodation(arrby, n, n2, arrby2, n3, n4);
            } else if (n7 == 4) {
                n6 = BarcodeDatamatrix.X12Encodation(arrby, n, n2, arrby2, n3, n4);
            }
            return n6;
        }
        switch (n5) {
            case 1: {
                return BarcodeDatamatrix.asciiEncodation(arrby, n, n2, arrby2, n3, n4);
            }
            case 2: {
                return BarcodeDatamatrix.C40OrTextEncodation(arrby, n, n2, arrby2, n3, n4, true);
            }
            case 3: {
                return BarcodeDatamatrix.C40OrTextEncodation(arrby, n, n2, arrby2, n3, n4, false);
            }
            case 4: {
                return BarcodeDatamatrix.b256Encodation(arrby, n, n2, arrby2, n3, n4);
            }
            case 5: {
                return BarcodeDatamatrix.X12Encodation(arrby, n, n2, arrby2, n3, n4);
            }
            case 6: {
                return BarcodeDatamatrix.EdifactEncodation(arrby, n, n2, arrby2, n3, n4);
            }
            case 7: {
                if (n2 > n4) {
                    return -1;
                }
                System.arraycopy(arrby, n, arrby2, n3, n2);
                return n2;
            }
        }
        return -1;
    }

    private static int getNumber(byte[] arrby, int n, int n2) {
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            int n4;
            if ((n4 = arrby[n++] & 255) < 48 || n4 > 57) {
                return -1;
            }
            n3 = n3 * 10 + n4 - 48;
        }
        return n3;
    }

    private int processExtensions(byte[] arrby, int n, int n2, byte[] arrby2) {
        if ((this.options & 32) == 0) {
            return 0;
        }
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        while (n4 < n2) {
            if (n3 > 20) {
                return -1;
            }
            int n6 = arrby[n + n4++] & 255;
            ++n3;
            switch (n6) {
                case 46: {
                    this.extOut = n4;
                    return n5;
                }
                case 101: {
                    if (n4 + 6 > n2) {
                        return -1;
                    }
                    int n7 = BarcodeDatamatrix.getNumber(arrby, n + n4, 6);
                    if (n7 < 0) {
                        return -1;
                    }
                    n4 += 6;
                    arrby2[n5++] = -15;
                    if (n7 < 127) {
                        arrby2[n5++] = (byte)(n7 + 1);
                        break;
                    }
                    if (n7 < 16383) {
                        arrby2[n5++] = (byte)((n7 - 127) / 254 + 128);
                        arrby2[n5++] = (byte)((n7 - 127) % 254 + 1);
                        break;
                    }
                    arrby2[n5++] = (byte)((n7 - 16383) / 64516 + 192);
                    arrby2[n5++] = (byte)((n7 - 16383) / 254 % 254 + 1);
                    arrby2[n5++] = (byte)((n7 - 16383) % 254 + 1);
                    break;
                }
                case 115: {
                    if (n3 != 1) {
                        return -1;
                    }
                    if (n4 + 9 > n2) {
                        return -1;
                    }
                    int n8 = BarcodeDatamatrix.getNumber(arrby, n + n4, 2);
                    if (n8 <= 0 || n8 > 16) {
                        return -1;
                    }
                    int n9 = BarcodeDatamatrix.getNumber(arrby, n + (n4 += 2), 2);
                    if (n9 <= 1 || n9 > 16) {
                        return -1;
                    }
                    int n10 = BarcodeDatamatrix.getNumber(arrby, n + (n4 += 2), 5);
                    if (n10 < 0 || n8 >= 64516) {
                        return -1;
                    }
                    n4 += 5;
                    arrby2[n5++] = -23;
                    arrby2[n5++] = (byte)(n8 - 1 << 4 | 17 - n9);
                    arrby2[n5++] = (byte)(n10 / 254 + 1);
                    arrby2[n5++] = (byte)(n10 % 254 + 1);
                    break;
                }
                case 112: {
                    if (n3 != 1) {
                        return -1;
                    }
                    arrby2[n5++] = -22;
                    break;
                }
                case 109: {
                    if (n3 != 1) {
                        return -1;
                    }
                    if (n4 + 1 > n2) {
                        return -1;
                    }
                    if ((n6 = arrby[n + n4++] & 255) != 53 && n6 != 53) {
                        return -1;
                    }
                    arrby2[n5++] = -22;
                    arrby2[n5++] = (byte)(n6 == 53 ? 236 : 237);
                    break;
                }
                case 102: {
                    if (n3 != 1 && (n3 != 2 || arrby[n] != 115 && arrby[n] != 109)) {
                        return -1;
                    }
                    arrby2[n5++] = -24;
                }
            }
        }
        return -1;
    }

    public int generate(String string) throws UnsupportedEncodingException {
        byte[] arrby = string.getBytes("iso-8859-1");
        return this.generate(arrby, 0, arrby.length);
    }

    public int generate(byte[] arrby, int n, int n2) {
        DmParams dmParams;
        byte[] arrby2 = new byte[2500];
        this.extOut = 0;
        int n3 = this.processExtensions(arrby, n, n2, arrby2);
        if (n3 < 0) {
            return 5;
        }
        int n4 = -1;
        if (this.height == 0 || this.width == 0) {
            int n5;
            DmParams dmParams2 = dmSizes[dmSizes.length - 1];
            n4 = BarcodeDatamatrix.getEncodation(arrby, n + this.extOut, n2 - this.extOut, arrby2, n3, dmParams2.dataSize - n3, this.options, false);
            if (n4 < 0) {
                return 1;
            }
            for (n5 = 0; n5 < dmSizes.length && BarcodeDatamatrix.dmSizes[n5].dataSize < (n4 += n3); ++n5) {
            }
            dmParams = dmSizes[n5];
            this.height = dmParams.height;
            this.width = dmParams.width;
        } else {
            int n6;
            for (n6 = 0; n6 < dmSizes.length && (this.height != BarcodeDatamatrix.dmSizes[n6].height || this.width != BarcodeDatamatrix.dmSizes[n6].width); ++n6) {
            }
            if (n6 == dmSizes.length) {
                return 3;
            }
            dmParams = dmSizes[n6];
            n4 = BarcodeDatamatrix.getEncodation(arrby, n + this.extOut, n2 - this.extOut, arrby2, n3, dmParams.dataSize - n3, this.options, true);
            if (n4 < 0) {
                return 1;
            }
            n4 += n3;
        }
        if ((this.options & 64) != 0) {
            return 0;
        }
        this.image = new byte[(dmParams.width + 2 * this.ws + 7) / 8 * (dmParams.height + 2 * this.ws)];
        BarcodeDatamatrix.makePadding(arrby2, n4, dmParams.dataSize - n4);
        this.place = Placement.doPlacement(dmParams.height - dmParams.height / dmParams.heightSection * 2, dmParams.width - dmParams.width / dmParams.widthSection * 2);
        int n7 = dmParams.dataSize + (dmParams.dataSize + 2) / dmParams.dataBlock * dmParams.errorBlock;
        ReedSolomon.generateECC(arrby2, dmParams.dataSize, dmParams.dataBlock, dmParams.errorBlock);
        this.draw(arrby2, n7, dmParams);
        return 0;
    }

    public Image createImage() throws BadElementException {
        if (this.image == null) {
            return null;
        }
        byte[] arrby = CCITTG4Encoder.compress(this.image, this.width + 2 * this.ws, this.height + 2 * this.ws);
        return Image.getInstance(this.width + 2 * this.ws, this.height + 2 * this.ws, false, 256, 0, arrby, null);
    }

    public java.awt.Image createAwtImage(Color color, Color color2) {
        if (this.image == null) {
            return null;
        }
        int n = color.getRGB();
        int n2 = color2.getRGB();
        Canvas canvas = new Canvas();
        int n3 = this.width + 2 * this.ws;
        int n4 = this.height + 2 * this.ws;
        int[] arrn = new int[n3 * n4];
        int n5 = (n3 + 7) / 8;
        int n6 = 0;
        for (int i = 0; i < n4; ++i) {
            int n7 = i * n5;
            for (int j = 0; j < n3; ++j) {
                int n8 = this.image[n7 + j / 8] & 255;
                arrn[n6++] = ((n8 <<= j % 8) & 128) == 0 ? n2 : n;
            }
        }
        java.awt.Image image = canvas.createImage(new MemoryImageSource(n3, n4, arrn, 0, n3));
        return image;
    }

    public byte[] getImage() {
        return this.image;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int n) {
        this.height = n;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int n) {
        this.width = n;
    }

    public int getWs() {
        return this.ws;
    }

    public void setWs(int n) {
        this.ws = n;
    }

    public int getOptions() {
        return this.options;
    }

    public void setOptions(int n) {
        this.options = n;
    }

    static class ReedSolomon {
        private static final int[] log = new int[]{0, 255, 1, 240, 2, 225, 241, 53, 3, 38, 226, 133, 242, 43, 54, 210, 4, 195, 39, 114, 227, 106, 134, 28, 243, 140, 44, 23, 55, 118, 211, 234, 5, 219, 196, 96, 40, 222, 115, 103, 228, 78, 107, 125, 135, 8, 29, 162, 244, 186, 141, 180, 45, 99, 24, 49, 56, 13, 119, 153, 212, 199, 235, 91, 6, 76, 220, 217, 197, 11, 97, 184, 41, 36, 223, 253, 116, 138, 104, 193, 229, 86, 79, 171, 108, 165, 126, 145, 136, 34, 9, 74, 30, 32, 163, 84, 245, 173, 187, 204, 142, 81, 181, 190, 46, 88, 100, 159, 25, 231, 50, 207, 57, 147, 14, 67, 120, 128, 154, 248, 213, 167, 200, 63, 236, 110, 92, 176, 7, 161, 77, 124, 221, 102, 218, 95, 198, 90, 12, 152, 98, 48, 185, 179, 42, 209, 37, 132, 224, 52, 254, 239, 117, 233, 139, 22, 105, 27, 194, 113, 230, 206, 87, 158, 80, 189, 172, 203, 109, 175, 166, 62, 127, 247, 146, 66, 137, 192, 35, 252, 10, 183, 75, 216, 31, 83, 33, 73, 164, 144, 85, 170, 246, 65, 174, 61, 188, 202, 205, 157, 143, 169, 82, 72, 182, 215, 191, 251, 47, 178, 89, 151, 101, 94, 160, 123, 26, 112, 232, 21, 51, 238, 208, 131, 58, 69, 148, 18, 15, 16, 68, 17, 121, 149, 129, 19, 155, 59, 249, 70, 214, 250, 168, 71, 201, 156, 64, 60, 237, 130, 111, 20, 93, 122, 177, 150};
        private static final int[] alog = new int[]{1, 2, 4, 8, 16, 32, 64, 128, 45, 90, 180, 69, 138, 57, 114, 228, 229, 231, 227, 235, 251, 219, 155, 27, 54, 108, 216, 157, 23, 46, 92, 184, 93, 186, 89, 178, 73, 146, 9, 18, 36, 72, 144, 13, 26, 52, 104, 208, 141, 55, 110, 220, 149, 7, 14, 28, 56, 112, 224, 237, 247, 195, 171, 123, 246, 193, 175, 115, 230, 225, 239, 243, 203, 187, 91, 182, 65, 130, 41, 82, 164, 101, 202, 185, 95, 190, 81, 162, 105, 210, 137, 63, 126, 252, 213, 135, 35, 70, 140, 53, 106, 212, 133, 39, 78, 156, 21, 42, 84, 168, 125, 250, 217, 159, 19, 38, 76, 152, 29, 58, 116, 232, 253, 215, 131, 43, 86, 172, 117, 234, 249, 223, 147, 11, 22, 44, 88, 176, 77, 154, 25, 50, 100, 200, 189, 87, 174, 113, 226, 233, 255, 211, 139, 59, 118, 236, 245, 199, 163, 107, 214, 129, 47, 94, 188, 85, 170, 121, 242, 201, 191, 83, 166, 97, 194, 169, 127, 254, 209, 143, 51, 102, 204, 181, 71, 142, 49, 98, 196, 165, 103, 206, 177, 79, 158, 17, 34, 68, 136, 61, 122, 244, 197, 167, 99, 198, 161, 111, 222, 145, 15, 30, 60, 120, 240, 205, 183, 67, 134, 33, 66, 132, 37, 74, 148, 5, 10, 20, 40, 80, 160, 109, 218, 153, 31, 62, 124, 248, 221, 151, 3, 6, 12, 24, 48, 96, 192, 173, 119, 238, 241, 207, 179, 75, 150, 1};
        private static final int[] poly5 = new int[]{228, 48, 15, 111, 62};
        private static final int[] poly7 = new int[]{23, 68, 144, 134, 240, 92, 254};
        private static final int[] poly10 = new int[]{28, 24, 185, 166, 223, 248, 116, 255, 110, 61};
        private static final int[] poly11 = new int[]{175, 138, 205, 12, 194, 168, 39, 245, 60, 97, 120};
        private static final int[] poly12 = new int[]{41, 153, 158, 91, 61, 42, 142, 213, 97, 178, 100, 242};
        private static final int[] poly14 = new int[]{156, 97, 192, 252, 95, 9, 157, 119, 138, 45, 18, 186, 83, 185};
        private static final int[] poly18 = new int[]{83, 195, 100, 39, 188, 75, 66, 61, 241, 213, 109, 129, 94, 254, 225, 48, 90, 188};
        private static final int[] poly20 = new int[]{15, 195, 244, 9, 233, 71, 168, 2, 188, 160, 153, 145, 253, 79, 108, 82, 27, 174, 186, 172};
        private static final int[] poly24 = new int[]{52, 190, 88, 205, 109, 39, 176, 21, 155, 197, 251, 223, 155, 21, 5, 172, 254, 124, 12, 181, 184, 96, 50, 193};
        private static final int[] poly28 = new int[]{211, 231, 43, 97, 71, 96, 103, 174, 37, 151, 170, 53, 75, 34, 249, 121, 17, 138, 110, 213, 141, 136, 120, 151, 233, 168, 93, 255};
        private static final int[] poly36 = new int[]{245, 127, 242, 218, 130, 250, 162, 181, 102, 120, 84, 179, 220, 251, 80, 182, 229, 18, 2, 4, 68, 33, 101, 137, 95, 119, 115, 44, 175, 184, 59, 25, 225, 98, 81, 112};
        private static final int[] poly42 = new int[]{77, 193, 137, 31, 19, 38, 22, 153, 247, 105, 122, 2, 245, 133, 242, 8, 175, 95, 100, 9, 167, 105, 214, 111, 57, 121, 21, 1, 253, 57, 54, 101, 248, 202, 69, 50, 150, 177, 226, 5, 9, 5};
        private static final int[] poly48 = new int[]{245, 132, 172, 223, 96, 32, 117, 22, 238, 133, 238, 231, 205, 188, 237, 87, 191, 106, 16, 147, 118, 23, 37, 90, 170, 205, 131, 88, 120, 100, 66, 138, 186, 240, 82, 44, 176, 87, 187, 147, 160, 175, 69, 213, 92, 253, 225, 19};
        private static final int[] poly56 = new int[]{175, 9, 223, 238, 12, 17, 220, 208, 100, 29, 175, 170, 230, 192, 215, 235, 150, 159, 36, 223, 38, 200, 132, 54, 228, 146, 218, 234, 117, 203, 29, 232, 144, 238, 22, 150, 201, 117, 62, 207, 164, 13, 137, 245, 127, 67, 247, 28, 155, 43, 203, 107, 233, 53, 143, 46};
        private static final int[] poly62 = new int[]{242, 93, 169, 50, 144, 210, 39, 118, 202, 188, 201, 189, 143, 108, 196, 37, 185, 112, 134, 230, 245, 63, 197, 190, 250, 106, 185, 221, 175, 64, 114, 71, 161, 44, 147, 6, 27, 218, 51, 63, 87, 10, 40, 130, 188, 17, 163, 31, 176, 170, 4, 107, 232, 7, 94, 166, 224, 124, 86, 47, 11, 204};
        private static final int[] poly68 = new int[]{220, 228, 173, 89, 251, 149, 159, 56, 89, 33, 147, 244, 154, 36, 73, 127, 213, 136, 248, 180, 234, 197, 158, 177, 68, 122, 93, 213, 15, 160, 227, 236, 66, 139, 153, 185, 202, 167, 179, 25, 220, 232, 96, 210, 231, 136, 223, 239, 181, 241, 59, 52, 172, 25, 49, 232, 211, 189, 64, 54, 108, 153, 132, 63, 96, 103, 82, 186};

        ReedSolomon() {
        }

        private static int[] getPoly(int n) {
            switch (n) {
                case 5: {
                    return poly5;
                }
                case 7: {
                    return poly7;
                }
                case 10: {
                    return poly10;
                }
                case 11: {
                    return poly11;
                }
                case 12: {
                    return poly12;
                }
                case 14: {
                    return poly14;
                }
                case 18: {
                    return poly18;
                }
                case 20: {
                    return poly20;
                }
                case 24: {
                    return poly24;
                }
                case 28: {
                    return poly28;
                }
                case 36: {
                    return poly36;
                }
                case 42: {
                    return poly42;
                }
                case 48: {
                    return poly48;
                }
                case 56: {
                    return poly56;
                }
                case 62: {
                    return poly62;
                }
                case 68: {
                    return poly68;
                }
            }
            return null;
        }

        private static void reedSolomonBlock(byte[] arrby, int n, byte[] arrby2, int n2, int[] arrn) {
            int n3;
            for (n3 = 0; n3 <= n2; ++n3) {
                arrby2[n3] = 0;
            }
            for (n3 = 0; n3 < n; ++n3) {
                int n4 = (arrby2[0] ^ arrby[n3]) & 255;
                for (int i = 0; i < n2; ++i) {
                    arrby2[i] = (byte)(arrby2[i + 1] ^ (n4 == 0 ? 0 : (byte)alog[(log[n4] + log[arrn[n2 - i - 1]]) % 255]));
                }
            }
        }

        static void generateECC(byte[] arrby, int n, int n2, int n3) {
            int n4 = (n + 2) / n2;
            byte[] arrby2 = new byte[256];
            byte[] arrby3 = new byte[256];
            int[] arrn = ReedSolomon.getPoly(n3);
            for (int i = 0; i < n4; ++i) {
                int n5;
                int n6 = 0;
                for (n5 = i; n5 < n; n5 += n4) {
                    arrby2[n6++] = arrby[n5];
                }
                ReedSolomon.reedSolomonBlock(arrby2, n6, arrby3, n3, arrn);
                n6 = 0;
                for (n5 = i; n5 < n3 * n4; n5 += n4) {
                    arrby[n + n5] = arrby3[n6++];
                }
            }
        }
    }

    static class Placement {
        private int nrow;
        private int ncol;
        private short[] array;
        private static final Hashtable cache = new Hashtable();

        private Placement() {
        }

        static short[] doPlacement(int n, int n2) {
            Integer n3 = new Integer(n * 1000 + n2);
            short[] arrs = (short[])cache.get(n3);
            if (arrs != null) {
                return arrs;
            }
            Placement placement = new Placement();
            placement.nrow = n;
            placement.ncol = n2;
            placement.array = new short[n * n2];
            placement.ecc200();
            cache.put(n3, placement.array);
            return placement.array;
        }

        private void module(int n, int n2, int n3, int n4) {
            if (n < 0) {
                n += this.nrow;
                n2 += 4 - (this.nrow + 4) % 8;
            }
            if (n2 < 0) {
                n2 += this.ncol;
                n += 4 - (this.ncol + 4) % 8;
            }
            this.array[n * this.ncol + n2] = (short)(8 * n3 + n4);
        }

        private void utah(int n, int n2, int n3) {
            this.module(n - 2, n2 - 2, n3, 0);
            this.module(n - 2, n2 - 1, n3, 1);
            this.module(n - 1, n2 - 2, n3, 2);
            this.module(n - 1, n2 - 1, n3, 3);
            this.module(n - 1, n2, n3, 4);
            this.module(n, n2 - 2, n3, 5);
            this.module(n, n2 - 1, n3, 6);
            this.module(n, n2, n3, 7);
        }

        private void corner1(int n) {
            this.module(this.nrow - 1, 0, n, 0);
            this.module(this.nrow - 1, 1, n, 1);
            this.module(this.nrow - 1, 2, n, 2);
            this.module(0, this.ncol - 2, n, 3);
            this.module(0, this.ncol - 1, n, 4);
            this.module(1, this.ncol - 1, n, 5);
            this.module(2, this.ncol - 1, n, 6);
            this.module(3, this.ncol - 1, n, 7);
        }

        private void corner2(int n) {
            this.module(this.nrow - 3, 0, n, 0);
            this.module(this.nrow - 2, 0, n, 1);
            this.module(this.nrow - 1, 0, n, 2);
            this.module(0, this.ncol - 4, n, 3);
            this.module(0, this.ncol - 3, n, 4);
            this.module(0, this.ncol - 2, n, 5);
            this.module(0, this.ncol - 1, n, 6);
            this.module(1, this.ncol - 1, n, 7);
        }

        private void corner3(int n) {
            this.module(this.nrow - 3, 0, n, 0);
            this.module(this.nrow - 2, 0, n, 1);
            this.module(this.nrow - 1, 0, n, 2);
            this.module(0, this.ncol - 2, n, 3);
            this.module(0, this.ncol - 1, n, 4);
            this.module(1, this.ncol - 1, n, 5);
            this.module(2, this.ncol - 1, n, 6);
            this.module(3, this.ncol - 1, n, 7);
        }

        private void corner4(int n) {
            this.module(this.nrow - 1, 0, n, 0);
            this.module(this.nrow - 1, this.ncol - 1, n, 1);
            this.module(0, this.ncol - 3, n, 2);
            this.module(0, this.ncol - 2, n, 3);
            this.module(0, this.ncol - 1, n, 4);
            this.module(1, this.ncol - 3, n, 5);
            this.module(1, this.ncol - 2, n, 6);
            this.module(1, this.ncol - 1, n, 7);
        }

        private void ecc200() {
            Arrays.fill(this.array, 0);
            int n = 1;
            int n2 = 4;
            int n3 = 0;
            do {
                if (n2 == this.nrow && n3 == 0) {
                    this.corner1(n++);
                }
                if (n2 == this.nrow - 2 && n3 == 0 && this.ncol % 4 != 0) {
                    this.corner2(n++);
                }
                if (n2 == this.nrow - 2 && n3 == 0 && this.ncol % 8 == 4) {
                    this.corner3(n++);
                }
                if (n2 == this.nrow + 4 && n3 == 2 && this.ncol % 8 == 0) {
                    this.corner4(n++);
                }
                do {
                    if (n2 >= this.nrow || n3 < 0 || this.array[n2 * this.ncol + n3] != 0) continue;
                    this.utah(n2, n3, n++);
                } while ((n2 -= 2) >= 0 && (n3 += 2) < this.ncol);
                ++n2;
                n3 += 3;
                do {
                    if (n2 < 0 || n3 >= this.ncol || this.array[n2 * this.ncol + n3] != 0) continue;
                    this.utah(n2, n3, n++);
                } while ((n2 += 2) < this.nrow && (n3 -= 2) >= 0);
            } while ((n2 += 3) < this.nrow || ++n3 < this.ncol);
            if (this.array[this.nrow * this.ncol - 1] == 0) {
                this.array[this.nrow * this.ncol - this.ncol - 2] = 1;
                this.array[this.nrow * this.ncol - 1] = 1;
            }
        }
    }

    private static class DmParams {
        int height;
        int width;
        int heightSection;
        int widthSection;
        int dataSize;
        int dataBlock;
        int errorBlock;

        DmParams(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
            this.height = n;
            this.width = n2;
            this.heightSection = n3;
            this.widthSection = n4;
            this.dataSize = n5;
            this.dataBlock = n6;
            this.errorBlock = n7;
        }
    }

}

