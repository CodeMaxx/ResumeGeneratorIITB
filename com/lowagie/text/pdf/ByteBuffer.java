/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.pdf.PdfEncodings;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ByteBuffer
extends OutputStream {
    protected int count;
    protected byte[] buf;
    private static int byteCacheSize = 0;
    private static byte[][] byteCache = new byte[byteCacheSize][];
    public static final byte ZERO = 48;
    private static final char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final byte[] bytes = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    public static boolean HIGH_PRECISION = false;
    private static final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

    public ByteBuffer() {
        this(128);
    }

    public ByteBuffer(int n) {
        if (n < 1) {
            n = 128;
        }
        this.buf = new byte[n];
    }

    public static void setCacheSize(int n) {
        if (n > 3276700) {
            n = 3276700;
        }
        if (n <= byteCacheSize) {
            return;
        }
        byte[][] arrarrby = new byte[n][];
        System.arraycopy(byteCache, 0, arrarrby, 0, byteCacheSize);
        byteCache = arrarrby;
        byteCacheSize = n;
    }

    public static void fillCache(int n) {
        int n2 = 1;
        switch (n) {
            case 0: {
                n2 = 100;
                break;
            }
            case 1: {
                n2 = 10;
            }
        }
        for (int i = 1; i < byteCacheSize; i += n2) {
            if (byteCache[i] != null) continue;
            ByteBuffer.byteCache[i] = ByteBuffer.convertToBytes(i);
        }
    }

    private static byte[] convertToBytes(int n) {
        int n2 = (int)Math.floor(Math.log(n) / Math.log(10.0));
        if (n % 100 != 0) {
            n2 += 2;
        }
        if (n % 10 != 0) {
            ++n2;
        }
        if (n < 100) {
            ++n2;
            if (n < 10) {
                ++n2;
            }
        }
        byte[] arrby = new byte[--n2];
        --n2;
        if (n < 100) {
            arrby[0] = 48;
        }
        if (n % 10 != 0) {
            arrby[n2--] = bytes[n % 10];
        }
        if (n % 100 != 0) {
            arrby[n2--] = bytes[n / 10 % 10];
            arrby[n2--] = 46;
        }
        n2 = (int)Math.floor(Math.log(n) / Math.log(10.0)) - 1;
        for (int i = 0; i < n2; ++i) {
            arrby[i] = bytes[n / (int)Math.pow(10.0, n2 - i + 1) % 10];
        }
        return arrby;
    }

    public ByteBuffer append_i(int n) {
        int n2 = this.count + 1;
        if (n2 > this.buf.length) {
            byte[] arrby = new byte[Math.max(this.buf.length << 1, n2)];
            System.arraycopy(this.buf, 0, arrby, 0, this.count);
            this.buf = arrby;
        }
        this.buf[this.count] = (byte)n;
        this.count = n2;
        return this;
    }

    public ByteBuffer append(byte[] arrby, int n, int n2) {
        if (n < 0 || n > arrby.length || n2 < 0 || n + n2 > arrby.length || n + n2 < 0 || n2 == 0) {
            return this;
        }
        int n3 = this.count + n2;
        if (n3 > this.buf.length) {
            byte[] arrby2 = new byte[Math.max(this.buf.length << 1, n3)];
            System.arraycopy(this.buf, 0, arrby2, 0, this.count);
            this.buf = arrby2;
        }
        System.arraycopy(arrby, n, this.buf, this.count, n2);
        this.count = n3;
        return this;
    }

    public ByteBuffer append(byte[] arrby) {
        return this.append(arrby, 0, arrby.length);
    }

    public ByteBuffer append(String string) {
        if (string != null) {
            return this.append(DocWriter.getISOBytes(string));
        }
        return this;
    }

    public ByteBuffer append(char c) {
        return this.append_i(c);
    }

    public ByteBuffer append(ByteBuffer byteBuffer) {
        return this.append(byteBuffer.buf, 0, byteBuffer.count);
    }

    public ByteBuffer append(int n) {
        return this.append((double)n);
    }

    public ByteBuffer append(byte by) {
        return this.append_i(by);
    }

    public ByteBuffer appendHex(byte by) {
        this.append(bytes[by >> 4 & 15]);
        return this.append(bytes[by & 15]);
    }

    public ByteBuffer append(float f) {
        return this.append((double)f);
    }

    public ByteBuffer append(double d) {
        this.append(ByteBuffer.formatDouble(d, this));
        return this;
    }

    public static String formatDouble(double d) {
        return ByteBuffer.formatDouble(d, null);
    }

    public static String formatDouble(double d, ByteBuffer byteBuffer) {
        if (HIGH_PRECISION) {
            DecimalFormat decimalFormat = new DecimalFormat("0.######", dfs);
            String string = decimalFormat.format(d);
            if (byteBuffer == null) {
                return string;
            }
            byteBuffer.append(string);
            return null;
        }
        boolean bl = false;
        if (Math.abs(d) < 1.5E-5) {
            if (byteBuffer != null) {
                byteBuffer.append(48);
                return null;
            }
            return "0";
        }
        if (d < 0.0) {
            bl = true;
            d = - d;
        }
        if (d < 1.0) {
            if ((d += 5.0E-6) >= 1.0) {
                if (bl) {
                    if (byteBuffer != null) {
                        byteBuffer.append(45);
                        byteBuffer.append(49);
                        return null;
                    }
                    return "-1";
                }
                if (byteBuffer != null) {
                    byteBuffer.append(49);
                    return null;
                }
                return "1";
            }
            if (byteBuffer != null) {
                int n = (int)(d * 100000.0);
                if (bl) {
                    byteBuffer.append(45);
                }
                byteBuffer.append(48);
                byteBuffer.append(46);
                byteBuffer.append((byte)(n / 10000 + 48));
                if (n % 10000 != 0) {
                    byteBuffer.append((byte)(n / 1000 % 10 + 48));
                    if (n % 1000 != 0) {
                        byteBuffer.append((byte)(n / 100 % 10 + 48));
                        if (n % 100 != 0) {
                            byteBuffer.append((byte)(n / 10 % 10 + 48));
                            if (n % 10 != 0) {
                                byteBuffer.append((byte)(n % 10 + 48));
                            }
                        }
                    }
                }
                return null;
            }
            int n = 100000;
            int n2 = (int)(d * (double)n);
            StringBuffer stringBuffer = new StringBuffer();
            if (bl) {
                stringBuffer.append('-');
            }
            stringBuffer.append("0.");
            while (n2 < n / 10) {
                stringBuffer.append('0');
                n /= 10;
            }
            stringBuffer.append(n2);
            int n3 = stringBuffer.length() - 1;
            while (stringBuffer.charAt(n3) == '0') {
                --n3;
            }
            stringBuffer.setLength(n3 + 1);
            return stringBuffer.toString();
        }
        if (d <= 32767.0) {
            int n = (int)((d += 0.005) * 100.0);
            if (n < byteCacheSize && byteCache[n] != null) {
                if (byteBuffer != null) {
                    if (bl) {
                        byteBuffer.append(45);
                    }
                    byteBuffer.append(byteCache[n]);
                    return null;
                }
                String string = PdfEncodings.convertToString(byteCache[n], null);
                if (bl) {
                    string = "-" + string;
                }
                return string;
            }
            if (byteBuffer != null) {
                if (n < byteCacheSize) {
                    int n4 = 0;
                    if (n >= 1000000) {
                        n4 += 5;
                    } else if (n >= 100000) {
                        n4 += 4;
                    } else if (n >= 10000) {
                        n4 += 3;
                    } else if (n >= 1000) {
                        n4 += 2;
                    } else if (n >= 100) {
                        ++n4;
                    }
                    if (n % 100 != 0) {
                        n4 += 2;
                    }
                    if (n % 10 != 0) {
                        ++n4;
                    }
                    byte[] arrby = new byte[n4];
                    int n5 = 0;
                    if (n >= 1000000) {
                        arrby[n5++] = bytes[n / 1000000];
                    }
                    if (n >= 100000) {
                        arrby[n5++] = bytes[n / 100000 % 10];
                    }
                    if (n >= 10000) {
                        arrby[n5++] = bytes[n / 10000 % 10];
                    }
                    if (n >= 1000) {
                        arrby[n5++] = bytes[n / 1000 % 10];
                    }
                    if (n >= 100) {
                        arrby[n5++] = bytes[n / 100 % 10];
                    }
                    if (n % 100 != 0) {
                        arrby[n5++] = 46;
                        arrby[n5++] = bytes[n / 10 % 10];
                        if (n % 10 != 0) {
                            arrby[n5++] = bytes[n % 10];
                        }
                    }
                    ByteBuffer.byteCache[n] = arrby;
                }
                if (bl) {
                    byteBuffer.append(45);
                }
                if (n >= 1000000) {
                    byteBuffer.append(bytes[n / 1000000]);
                }
                if (n >= 100000) {
                    byteBuffer.append(bytes[n / 100000 % 10]);
                }
                if (n >= 10000) {
                    byteBuffer.append(bytes[n / 10000 % 10]);
                }
                if (n >= 1000) {
                    byteBuffer.append(bytes[n / 1000 % 10]);
                }
                if (n >= 100) {
                    byteBuffer.append(bytes[n / 100 % 10]);
                }
                if (n % 100 != 0) {
                    byteBuffer.append(46);
                    byteBuffer.append(bytes[n / 10 % 10]);
                    if (n % 10 != 0) {
                        byteBuffer.append(bytes[n % 10]);
                    }
                }
                return null;
            }
            StringBuffer stringBuffer = new StringBuffer();
            if (bl) {
                stringBuffer.append('-');
            }
            if (n >= 1000000) {
                stringBuffer.append(chars[n / 1000000]);
            }
            if (n >= 100000) {
                stringBuffer.append(chars[n / 100000 % 10]);
            }
            if (n >= 10000) {
                stringBuffer.append(chars[n / 10000 % 10]);
            }
            if (n >= 1000) {
                stringBuffer.append(chars[n / 1000 % 10]);
            }
            if (n >= 100) {
                stringBuffer.append(chars[n / 100 % 10]);
            }
            if (n % 100 != 0) {
                stringBuffer.append('.');
                stringBuffer.append(chars[n / 10 % 10]);
                if (n % 10 != 0) {
                    stringBuffer.append(chars[n % 10]);
                }
            }
            return stringBuffer.toString();
        }
        StringBuffer stringBuffer = new StringBuffer();
        if (bl) {
            stringBuffer.append('-');
        }
        long l = (long)(d += 0.5);
        return stringBuffer.append(l).toString();
    }

    public void reset() {
        this.count = 0;
    }

    public byte[] toByteArray() {
        byte[] arrby = new byte[this.count];
        System.arraycopy(this.buf, 0, arrby, 0, this.count);
        return arrby;
    }

    public int size() {
        return this.count;
    }

    public void setSize(int n) {
        if (n > this.count || n < 0) {
            throw new IndexOutOfBoundsException("The new size must be positive and <= of the current size");
        }
        this.count = n;
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }

    public String toString(String string) throws UnsupportedEncodingException {
        return new String(this.buf, 0, this.count, string);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(this.buf, 0, this.count);
    }

    public void write(int n) throws IOException {
        this.append((byte)n);
    }

    public void write(byte[] arrby, int n, int n2) {
        this.append(arrby, n, n2);
    }

    public byte[] getBuffer() {
        return this.buf;
    }
}

