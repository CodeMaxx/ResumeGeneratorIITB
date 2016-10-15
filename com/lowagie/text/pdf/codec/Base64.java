/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Base64 {
    public static final int NO_OPTIONS = 0;
    public static final int ENCODE = 1;
    public static final int DECODE = 0;
    public static final int GZIP = 2;
    public static final int DONT_BREAK_LINES = 8;
    public static final int URL_SAFE = 16;
    public static final int ORDERED = 32;
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte EQUALS_SIGN = 61;
    private static final byte NEW_LINE = 10;
    private static final String PREFERRED_ENCODING = "UTF-8";
    private static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;
    private static final byte[] _STANDARD_ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] _STANDARD_DECODABET = new byte[]{-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9};
    private static final byte[] _URL_SAFE_ALPHABET = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
    private static final byte[] _URL_SAFE_DECODABET = new byte[]{-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9};
    private static final byte[] _ORDERED_ALPHABET = new byte[]{45, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 95, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122};
    private static final byte[] _ORDERED_DECODABET = new byte[]{-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 0, -9, -9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -9, -9, -9, -1, -9, -9, -9, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, -9, -9, -9, -9, 37, -9, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, -9, -9, -9, -9};

    private static final byte[] getAlphabet(int n) {
        if ((n & 16) == 16) {
            return _URL_SAFE_ALPHABET;
        }
        if ((n & 32) == 32) {
            return _ORDERED_ALPHABET;
        }
        return _STANDARD_ALPHABET;
    }

    private static final byte[] getDecodabet(int n) {
        if ((n & 16) == 16) {
            return _URL_SAFE_DECODABET;
        }
        if ((n & 32) == 32) {
            return _ORDERED_DECODABET;
        }
        return _STANDARD_DECODABET;
    }

    private Base64() {
    }

    public static final void main(String[] arrstring) {
        if (arrstring.length < 3) {
            Base64.usage("Not enough arguments.");
        } else {
            String string = arrstring[0];
            String string2 = arrstring[1];
            String string3 = arrstring[2];
            if (string.equals("-e")) {
                Base64.encodeFileToFile(string2, string3);
            } else if (string.equals("-d")) {
                Base64.decodeFileToFile(string2, string3);
            } else {
                Base64.usage("Unknown flag: " + string);
            }
        }
    }

    private static final void usage(String string) {
        System.err.println(string);
        System.err.println("Usage: java Base64 -e|-d inputfile outputfile");
    }

    private static byte[] encode3to4(byte[] arrby, byte[] arrby2, int n, int n2) {
        Base64.encode3to4(arrby2, 0, n, arrby, 0, n2);
        return arrby;
    }

    private static byte[] encode3to4(byte[] arrby, int n, int n2, byte[] arrby2, int n3, int n4) {
        byte[] arrby3 = Base64.getAlphabet(n4);
        int n5 = (n2 > 0 ? arrby[n] << 24 >>> 8 : 0) | (n2 > 1 ? arrby[n + 1] << 24 >>> 16 : 0) | (n2 > 2 ? arrby[n + 2] << 24 >>> 24 : 0);
        switch (n2) {
            case 3: {
                arrby2[n3] = arrby3[n5 >>> 18];
                arrby2[n3 + 1] = arrby3[n5 >>> 12 & 63];
                arrby2[n3 + 2] = arrby3[n5 >>> 6 & 63];
                arrby2[n3 + 3] = arrby3[n5 & 63];
                return arrby2;
            }
            case 2: {
                arrby2[n3] = arrby3[n5 >>> 18];
                arrby2[n3 + 1] = arrby3[n5 >>> 12 & 63];
                arrby2[n3 + 2] = arrby3[n5 >>> 6 & 63];
                arrby2[n3 + 3] = 61;
                return arrby2;
            }
            case 1: {
                arrby2[n3] = arrby3[n5 >>> 18];
                arrby2[n3 + 1] = arrby3[n5 >>> 12 & 63];
                arrby2[n3 + 2] = 61;
                arrby2[n3 + 3] = 61;
                return arrby2;
            }
        }
        return arrby2;
    }

    public static String encodeObject(Serializable serializable) {
        return Base64.encodeObject(serializable, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static String encodeObject(Serializable var0, int var1_1) {
        var2_2 = null;
        var3_3 = null;
        var4_4 = null;
        var5_5 = null;
        var6_6 = var1_1 & 2;
        var7_7 = var1_1 & 8;
        try {
            block26 : {
                block25 : {
                    block24 : {
                        try {
                            var2_2 = new ByteArrayOutputStream();
                            var3_3 = new OutputStream(var2_2, 1 | var1_1);
                            if (var6_6 == 2) {
                                var5_5 = new GZIPOutputStream(var3_3);
                                var4_4 = new ObjectOutputStream(var5_5);
                            } else {
                                var4_4 = new ObjectOutputStream(var3_3);
                            }
                            var4_4.writeObject(var0);
                        }
                        catch (IOException var8_14) {
                            var8_14.printStackTrace();
                            var9_16 = null;
                            var11_9 = null;
                            ** try [egrp 2[TRYBLOCK] [4 : 119->127)] { 
lbl23: // 1 sources:
                            var4_4.close();
                            ** GOTO lbl27
lbl25: // 1 sources:
                            catch (Exception var12_12) {
                                // empty catch block
                            }
lbl27: // 2 sources:
                            ** try [egrp 3[TRYBLOCK] [5 : 129->137)] { 
lbl28: // 1 sources:
                            var5_5.close();
                            ** GOTO lbl32
lbl30: // 1 sources:
                            catch (Exception var12_12) {
                                // empty catch block
                            }
lbl32: // 2 sources:
                            ** try [egrp 4[TRYBLOCK] [6 : 139->146)] { 
lbl33: // 1 sources:
                            var3_3.close();
                            ** GOTO lbl37
lbl35: // 1 sources:
                            catch (Exception var12_12) {
                                // empty catch block
                            }
lbl37: // 2 sources:
                            ** try [egrp 5[TRYBLOCK] [7 : 148->155)] { 
lbl38: // 1 sources:
                            var2_2.close();
                            return var9_16;
lbl40: // 1 sources:
                            catch (Exception var12_12) {
                                // empty catch block
                            }
                            return var9_16;
                        }
                        var11_8 = null;
                        var4_4.close();
                        break block24;
                        catch (Exception var12_11) {
                            // empty catch block
                        }
                    }
                    var5_5.close();
                    break block25;
                    catch (Exception var12_11) {
                        // empty catch block
                    }
                }
                var3_3.close();
                break block26;
                catch (Exception var12_11) {
                    // empty catch block
                }
            }
            var2_2.close();
            catch (Exception var12_11) {}
            ** try [egrp 6[TRYBLOCK] [8 : 159->173)] { 
lbl68: // 1 sources:
            return new String(var2_2.toByteArray(), "UTF-8");
        }
        catch (Throwable var10_17) {
            var11_10 = null;
            ** try [egrp 2[TRYBLOCK] [4 : 119->127)] { 
lbl73: // 1 sources:
            var4_4.close();
            ** GOTO lbl77
lbl75: // 1 sources:
            catch (Exception var12_13) {
                // empty catch block
            }
lbl77: // 2 sources:
            ** try [egrp 3[TRYBLOCK] [5 : 129->137)] { 
lbl78: // 1 sources:
            var5_5.close();
            ** GOTO lbl82
lbl80: // 1 sources:
            catch (Exception var12_13) {
                // empty catch block
            }
lbl82: // 2 sources:
            ** try [egrp 4[TRYBLOCK] [6 : 139->146)] { 
lbl83: // 1 sources:
            var3_3.close();
            ** GOTO lbl87
lbl85: // 1 sources:
            catch (Exception var12_13) {
                // empty catch block
            }
lbl87: // 2 sources:
            ** try [egrp 5[TRYBLOCK] [7 : 148->155)] { 
lbl88: // 1 sources:
            var2_2.close();
            throw var10_17;
lbl90: // 1 sources:
            catch (Exception var12_13) {
                // empty catch block
            }
            throw var10_17;
        }
lbl93: // 1 sources:
        catch (UnsupportedEncodingException var8_15) {
            return new String(var2_2.toByteArray());
        }
    }

    public static String encodeBytes(byte[] arrby) {
        return Base64.encodeBytes(arrby, 0, arrby.length, 0);
    }

    public static String encodeBytes(byte[] arrby, int n) {
        return Base64.encodeBytes(arrby, 0, arrby.length, n);
    }

    public static String encodeBytes(byte[] arrby, int n, int n2) {
        return Base64.encodeBytes(arrby, n, n2, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static String encodeBytes(byte[] var0, int var1_1, int var2_2, int var3_3) {
        var4_4 = var3_3 & 8;
        var5_5 = var3_3 & 2;
        if (var5_5 != 2) ** GOTO lbl76
        var6_6 = null;
        var7_8 = null;
        var8_10 = null;
        try {
            block23 : {
                block22 : {
                    try {
                        var6_6 = new ByteArrayOutputStream();
                        var8_10 = new OutputStream(var6_6, 1 | var3_3);
                        var7_8 = new GZIPOutputStream(var8_10);
                        var7_8.write(var0, var1_1, var2_2);
                        var7_8.close();
                    }
                    catch (IOException var9_20) {
                        var9_20.printStackTrace();
                        var10_23 = null;
                        var12_13 = null;
                        ** try [egrp 2[TRYBLOCK] [4 : 105->113)] { 
lbl20: // 1 sources:
                        var7_8.close();
                        ** GOTO lbl24
lbl22: // 1 sources:
                        catch (Exception var13_17) {
                            // empty catch block
                        }
lbl24: // 2 sources:
                        ** try [egrp 3[TRYBLOCK] [5 : 115->123)] { 
lbl25: // 1 sources:
                        var8_10.close();
                        ** GOTO lbl29
lbl27: // 1 sources:
                        catch (Exception var13_17) {
                            // empty catch block
                        }
lbl29: // 2 sources:
                        ** try [egrp 4[TRYBLOCK] [6 : 125->133)] { 
lbl30: // 1 sources:
                        var6_6.close();
                        return var10_23;
lbl32: // 1 sources:
                        catch (Exception var13_17) {
                            // empty catch block
                        }
                        return var10_23;
                    }
                    var12_12 = null;
                    var7_8.close();
                    break block22;
                    catch (Exception var13_16) {
                        // empty catch block
                    }
                }
                var8_10.close();
                break block23;
                catch (Exception var13_16) {
                    // empty catch block
                }
            }
            var6_6.close();
            catch (Exception var13_16) {}
            ** try [egrp 5[TRYBLOCK] [7 : 137->152)] { 
lbl54: // 1 sources:
            return new String(var6_6.toByteArray(), "UTF-8");
        }
        catch (Throwable var11_25) {
            var12_14 = null;
            ** try [egrp 2[TRYBLOCK] [4 : 105->113)] { 
lbl59: // 1 sources:
            var7_8.close();
            ** GOTO lbl63
lbl61: // 1 sources:
            catch (Exception var13_18) {
                // empty catch block
            }
lbl63: // 2 sources:
            ** try [egrp 3[TRYBLOCK] [5 : 115->123)] { 
lbl64: // 1 sources:
            var8_10.close();
            ** GOTO lbl68
lbl66: // 1 sources:
            catch (Exception var13_18) {
                // empty catch block
            }
lbl68: // 2 sources:
            ** try [egrp 4[TRYBLOCK] [6 : 125->133)] { 
lbl69: // 1 sources:
            var6_6.close();
            throw var11_25;
lbl71: // 1 sources:
            catch (Exception var13_18) {
                // empty catch block
            }
            throw var11_25;
        }
lbl74: // 1 sources:
        catch (UnsupportedEncodingException var9_21) {
            return new String(var6_6.toByteArray());
        }
lbl76: // 1 sources:
        var6_7 = var4_4 == 0;
        var7_9 = var2_2 * 4 / 3;
        var8_11 = new byte[var7_9 + (var2_2 % 3 > 0 ? 4 : 0) + (var6_7 != false ? var7_9 / 76 : 0)];
        var10_24 = 0;
        var11_26 = var2_2 - 2;
        var12_15 = 0;
        for (var9_22 = 0; var9_22 < var11_26; var9_22 += 3, var10_24 += 4) {
            Base64.encode3to4(var0, var9_22 + var1_1, 3, var8_11, var10_24, var3_3);
            if (!var6_7 || (var12_15 += 4) != 76) continue;
            var8_11[var10_24 + 4] = 10;
            ++var10_24;
            var12_15 = 0;
        }
        if (var9_22 < var2_2) {
            Base64.encode3to4(var0, var9_22 + var1_1, var2_2 - var9_22, var8_11, var10_24, var3_3);
            var10_24 += 4;
        }
        try {
            return new String(var8_11, 0, var10_24, "UTF-8");
        }
        catch (UnsupportedEncodingException var13_19) {
            return new String(var8_11, 0, var10_24);
        }
    }

    private static int decode4to3(byte[] arrby, int n, byte[] arrby2, int n2, int n3) {
        byte[] arrby3 = Base64.getDecodabet(n3);
        if (arrby[n + 2] == 61) {
            int n4 = (arrby3[arrby[n]] & 255) << 18 | (arrby3[arrby[n + 1]] & 255) << 12;
            arrby2[n2] = (byte)(n4 >>> 16);
            return 1;
        }
        if (arrby[n + 3] == 61) {
            int n5 = (arrby3[arrby[n]] & 255) << 18 | (arrby3[arrby[n + 1]] & 255) << 12 | (arrby3[arrby[n + 2]] & 255) << 6;
            arrby2[n2] = (byte)(n5 >>> 16);
            arrby2[n2 + 1] = (byte)(n5 >>> 8);
            return 2;
        }
        try {
            int n6 = (arrby3[arrby[n]] & 255) << 18 | (arrby3[arrby[n + 1]] & 255) << 12 | (arrby3[arrby[n + 2]] & 255) << 6 | arrby3[arrby[n + 3]] & 255;
            arrby2[n2] = (byte)(n6 >> 16);
            arrby2[n2 + 1] = (byte)(n6 >> 8);
            arrby2[n2 + 2] = (byte)n6;
            return 3;
        }
        catch (Exception var6_9) {
            System.out.println("" + arrby[n] + ": " + arrby3[arrby[n]]);
            System.out.println("" + arrby[n + 1] + ": " + arrby3[arrby[n + 1]]);
            System.out.println("" + arrby[n + 2] + ": " + arrby3[arrby[n + 2]]);
            System.out.println("" + arrby[n + 3] + ": " + arrby3[arrby[n + 3]]);
            return -1;
        }
    }

    public static byte[] decode(byte[] arrby, int n, int n2, int n3) {
        byte[] arrby2 = Base64.getDecodabet(n3);
        int n4 = n2 * 3 / 4;
        byte[] arrby3 = new byte[n4];
        int n5 = 0;
        byte[] arrby4 = new byte[4];
        int n6 = 0;
        int n7 = 0;
        byte by = 0;
        byte by2 = 0;
        for (n7 = n; n7 < n + n2; ++n7) {
            by = (byte)(arrby[n7] & 127);
            by2 = arrby2[by];
            if (by2 >= -5) {
                if (by2 < -1) continue;
                arrby4[n6++] = by;
                if (n6 <= 3) continue;
                n5 += Base64.decode4to3(arrby4, 0, arrby3, n5, n3);
                n6 = 0;
                if (by != 61) continue;
                break;
            }
            System.err.println("Bad Base64 input character at " + n7 + ": " + arrby[n7] + "(decimal)");
            return null;
        }
        byte[] arrby5 = new byte[n5];
        System.arraycopy(arrby3, 0, arrby5, 0, n5);
        return arrby5;
    }

    public static byte[] decode(String string) {
        return Base64.decode(string, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static byte[] decode(String string, int n) {
        try {
            arrby = string.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException var3_3) {
            arrby = string.getBytes();
        }
        arrby = Base64.decode(arrby, 0, arrby.length, n);
        if (arrby == null) return arrby;
        if (arrby.length < 4) return arrby;
        n2 = arrby[0] & 255 | arrby[1] << 8 & 65280;
        if (35615 != n2) return arrby;
        byteArrayInputStream = null;
        gZIPInputStream = null;
        byteArrayOutputStream = null;
        arrby2 = new byte[2048];
        n3 = 0;
        try {
            block20 : {
                block19 : {
                    try {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        byteArrayInputStream = new ByteArrayInputStream(arrby);
                        gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
                        while ((n3 = gZIPInputStream.read(arrby2)) >= 0) {
                            byteArrayOutputStream.write(arrby2, 0, n3);
                        }
                        arrby = byteArrayOutputStream.toByteArray();
                    }
                    catch (IOException iOException) {
                        var11_11 = null;
                        ** try [egrp 3[TRYBLOCK] [5 : 164->172)] { 
lbl29: // 1 sources:
                        byteArrayOutputStream.close();
                        ** GOTO lbl33
lbl31: // 1 sources:
                        catch (Exception exception2) {
                            // empty catch block
                        }
lbl33: // 2 sources:
                        ** try [egrp 4[TRYBLOCK] [6 : 174->182)] { 
lbl34: // 1 sources:
                        gZIPInputStream.close();
                        ** GOTO lbl-1000
lbl36: // 1 sources:
                        catch (Exception exception2) {
                            // empty catch block
                        }
lbl-1000: // 2 sources:
                        try {}
                        catch (Exception exception2) {
                            return arrby;
                        }
                        byteArrayInputStream.close();
                        return arrby;
                    }
                    var11_10 = null;
                    byteArrayOutputStream.close();
                    break block19;
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                gZIPInputStream.close();
                break block20;
                catch (Exception exception) {
                    // empty catch block
                }
            }
            byteArrayInputStream.close();
            return arrby;
            catch (Exception exception) {
                return arrby;
            }
        }
        catch (Throwable throwable) {
            var11_12 = null;
            ** try [egrp 3[TRYBLOCK] [5 : 164->172)] { 
lbl66: // 1 sources:
            byteArrayOutputStream.close();
            ** GOTO lbl70
lbl68: // 1 sources:
            catch (Exception var12_15) {
                // empty catch block
            }
lbl70: // 2 sources:
            ** try [egrp 4[TRYBLOCK] [6 : 174->182)] { 
lbl71: // 1 sources:
            gZIPInputStream.close();
            ** GOTO lbl75
lbl73: // 1 sources:
            catch (Exception var12_15) {
                // empty catch block
            }
lbl75: // 2 sources:
            ** try [egrp 5[TRYBLOCK] [7 : 184->192)] { 
lbl76: // 1 sources:
            byteArrayInputStream.close();
            throw throwable;
lbl78: // 1 sources:
            catch (Exception var12_15) {
                // empty catch block
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object decodeToObject(String string) {
        byte[] arrby = Base64.decode(string);
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        Object object = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(arrby);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            object = objectInputStream.readObject();
        }
        catch (IOException var5_7) {
            var5_7.printStackTrace();
        }
        catch (ClassNotFoundException var5_10) {
            var5_10.printStackTrace();
        }
        finally {
            try {
                byteArrayInputStream.close();
            }
            catch (Exception var5_8) {}
            try {
                objectInputStream.close();
            }
            catch (Exception var5_9) {}
        }
        return object;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean encodeToFile(byte[] arrby, String string) {
        boolean bl = false;
        OutputStream outputStream = null;
        try {
            outputStream = new OutputStream(new FileOutputStream(string), 1);
            outputStream.write(arrby);
            bl = true;
        }
        catch (IOException var4_5) {
            bl = false;
        }
        finally {
            try {
                outputStream.close();
            }
            catch (Exception var4_6) {}
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean decodeToFile(String string, String string2) {
        boolean bl = false;
        OutputStream outputStream = null;
        try {
            outputStream = new OutputStream(new FileOutputStream(string2), 0);
            outputStream.write(string.getBytes("UTF-8"));
            bl = true;
        }
        catch (IOException var4_5) {
            bl = false;
        }
        finally {
            try {
                outputStream.close();
            }
            catch (Exception var4_6) {}
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] decodeFromFile(String string) {
        byte[] arrby = null;
        FilterInputStream filterInputStream = null;
        try {
            File file = new File(string);
            byte[] arrby2 = null;
            int n = 0;
            int n2 = 0;
            if (file.length() > Integer.MAX_VALUE) {
                System.err.println("File is too big for this convenience method (" + file.length() + " bytes).");
                byte[] arrby3 = null;
                return arrby3;
            }
            arrby2 = new byte[(int)file.length()];
            filterInputStream = new InputStream(new BufferedInputStream(new FileInputStream(file)), 0);
            while ((n2 = filterInputStream.read(arrby2, n, 4096)) >= 0) {
                n += n2;
            }
            arrby = new byte[n];
            System.arraycopy(arrby2, 0, arrby, 0, n);
        }
        catch (IOException var3_5) {
            System.err.println("Error decoding from file " + string);
        }
        finally {
            try {
                filterInputStream.close();
            }
            catch (Exception var3_6) {}
        }
        return arrby;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String encodeFromFile(String string) {
        String string2 = null;
        FilterInputStream filterInputStream = null;
        try {
            File file = new File(string);
            byte[] arrby = new byte[Math.max((int)((double)file.length() * 1.4), 40)];
            int n = 0;
            int n2 = 0;
            filterInputStream = new InputStream(new BufferedInputStream(new FileInputStream(file)), 1);
            while ((n2 = filterInputStream.read(arrby, n, 4096)) >= 0) {
                n += n2;
            }
            string2 = new String(arrby, 0, n, "UTF-8");
        }
        catch (IOException var3_5) {
            System.err.println("Error encoding from file " + string);
        }
        finally {
            try {
                filterInputStream.close();
            }
            catch (Exception var3_6) {}
        }
        return string2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void encodeFileToFile(String string, String string2) {
        String string3 = Base64.encodeFromFile(string);
        java.io.OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(string2));
            outputStream.write(string3.getBytes("US-ASCII"));
        }
        catch (IOException var4_5) {
            var4_5.printStackTrace();
        }
        finally {
            try {
                outputStream.close();
            }
            catch (Exception var4_6) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void decodeFileToFile(String string, String string2) {
        byte[] arrby = Base64.decodeFromFile(string);
        java.io.OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(string2));
            outputStream.write(arrby);
        }
        catch (IOException var4_5) {
            var4_5.printStackTrace();
        }
        finally {
            try {
                outputStream.close();
            }
            catch (Exception var4_6) {}
        }
    }

    public static class OutputStream
    extends FilterOutputStream {
        private boolean encode;
        private int position;
        private byte[] buffer;
        private int bufferLength;
        private int lineLength;
        private boolean breakLines;
        private byte[] b4;
        private boolean suspendEncoding;
        private int options;
        private byte[] alphabet;
        private byte[] decodabet;

        public OutputStream(java.io.OutputStream outputStream) {
            this(outputStream, 1);
        }

        public OutputStream(java.io.OutputStream outputStream, int n) {
            super(outputStream);
            this.breakLines = (n & 8) != 8;
            this.encode = (n & 1) == 1;
            this.bufferLength = this.encode ? 3 : 4;
            this.buffer = new byte[this.bufferLength];
            this.position = 0;
            this.lineLength = 0;
            this.suspendEncoding = false;
            this.b4 = new byte[4];
            this.options = n;
            this.alphabet = Base64.getAlphabet(n);
            this.decodabet = Base64.getDecodabet(n);
        }

        public void write(int n) throws IOException {
            if (this.suspendEncoding) {
                this.out.write(n);
                return;
            }
            if (this.encode) {
                this.buffer[this.position++] = (byte)n;
                if (this.position >= this.bufferLength) {
                    this.out.write(Base64.encode3to4(this.b4, this.buffer, this.bufferLength, this.options));
                    this.lineLength += 4;
                    if (this.breakLines && this.lineLength >= 76) {
                        this.out.write(10);
                        this.lineLength = 0;
                    }
                    this.position = 0;
                }
            } else if (this.decodabet[n & 127] > -5) {
                this.buffer[this.position++] = (byte)n;
                if (this.position >= this.bufferLength) {
                    int n2 = Base64.decode4to3(this.buffer, 0, this.b4, 0, this.options);
                    this.out.write(this.b4, 0, n2);
                    this.position = 0;
                }
            } else if (this.decodabet[n & 127] != -5) {
                throw new IOException("Invalid character in Base64 data.");
            }
        }

        public void write(byte[] arrby, int n, int n2) throws IOException {
            if (this.suspendEncoding) {
                this.out.write(arrby, n, n2);
                return;
            }
            for (int i = 0; i < n2; ++i) {
                this.write(arrby[n + i]);
            }
        }

        public void flushBase64() throws IOException {
            if (this.position > 0) {
                if (this.encode) {
                    this.out.write(Base64.encode3to4(this.b4, this.buffer, this.position, this.options));
                    this.position = 0;
                } else {
                    throw new IOException("Base64 input not properly padded.");
                }
            }
        }

        public void close() throws IOException {
            this.flushBase64();
            super.close();
            this.buffer = null;
            this.out = null;
        }

        public void suspendEncoding() throws IOException {
            this.flushBase64();
            this.suspendEncoding = true;
        }

        public void resumeEncoding() {
            this.suspendEncoding = false;
        }
    }

    public static class InputStream
    extends FilterInputStream {
        private boolean encode;
        private int position;
        private byte[] buffer;
        private int bufferLength;
        private int numSigBytes;
        private int lineLength;
        private boolean breakLines;
        private int options;
        private byte[] alphabet;
        private byte[] decodabet;

        public InputStream(java.io.InputStream inputStream) {
            this(inputStream, 0);
        }

        public InputStream(java.io.InputStream inputStream, int n) {
            super(inputStream);
            this.breakLines = (n & 8) != 8;
            this.encode = (n & 1) == 1;
            this.bufferLength = this.encode ? 4 : 3;
            this.buffer = new byte[this.bufferLength];
            this.position = -1;
            this.lineLength = 0;
            this.options = n;
            this.alphabet = Base64.getAlphabet(n);
            this.decodabet = Base64.getDecodabet(n);
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public int read() throws IOException {
            if (this.position < 0) {
                byte[] arrby;
                if (this.encode) {
                    arrby = new byte[3];
                    int n = 0;
                    for (int i = 0; i < 3; ++i) {
                        try {
                            int n2 = this.in.read();
                            if (n2 < 0) continue;
                            arrby[i] = (byte)n2;
                            ++n;
                            continue;
                        }
                        catch (IOException var4_8) {
                            if (i != 0) continue;
                            throw var4_8;
                        }
                    }
                    if (n <= 0) return -1;
                    Base64.encode3to4(arrby, 0, n, this.buffer, 0, this.options);
                    this.position = 0;
                    this.numSigBytes = 4;
                } else {
                    int n;
                    block13 : {
                        arrby = new byte[4];
                        n = 0;
                        n = 0;
                        while (n < 4) {
                            int n3 = 0;
                            while ((n3 = this.in.read()) >= 0 && this.decodabet[n3 & 127] <= -5) {
                            }
                            if (n3 >= 0) {
                                arrby[n] = (byte)n3;
                                ++n;
                                continue;
                            }
                            break block13;
                        }
                        return -1;
                    }
                    if (n == 4) {
                        this.numSigBytes = Base64.decode4to3(arrby, 0, this.buffer, 0, this.options);
                        this.position = 0;
                    } else {
                        if (n != 0) throw new IOException("Improperly padded Base64 input.");
                        return -1;
                    }
                }
            }
            if (this.position < 0) throw new IOException("Error in Base64 code reading stream.");
            if (this.position >= this.numSigBytes) {
                return -1;
            }
            if (this.encode && this.breakLines && this.lineLength >= 76) {
                this.lineLength = 0;
                return 10;
            }
            ++this.lineLength;
            byte by = this.buffer[this.position++];
            if (this.position < this.bufferLength) return by & 255;
            this.position = -1;
            return by & 255;
        }

        public int read(byte[] arrby, int n, int n2) throws IOException {
            int n3;
            for (n3 = 0; n3 < n2; ++n3) {
                int n4 = this.read();
                if (n4 < 0) {
                    if (n3 != 0) break;
                    return -1;
                }
                arrby[n + n3] = (byte)n4;
            }
            return n3;
        }
    }

}

