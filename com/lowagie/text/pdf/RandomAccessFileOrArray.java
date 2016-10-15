/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.MappedRandomAccessFile;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class RandomAccessFileOrArray
implements DataInput {
    MappedRandomAccessFile rf;
    RandomAccessFile trf;
    boolean plainRandomAccess;
    String filename;
    byte[] arrayIn;
    int arrayInPtr;
    byte back;
    boolean isBack = false;
    private int startOffset = 0;

    public RandomAccessFileOrArray(String string) throws IOException {
        this(string, false, Document.plainRandomAccess);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public RandomAccessFileOrArray(String string, boolean bl, boolean bl2) throws IOException {
        this.plainRandomAccess = bl2;
        File file = new File(string);
        if (!file.canRead()) {
            if (string.startsWith("file:/") || string.startsWith("http://") || string.startsWith("https://") || string.startsWith("jar:")) {
                InputStream inputStream = new URL(string).openStream();
                try {
                    this.arrayIn = RandomAccessFileOrArray.InputStreamToArray(inputStream);
                    return;
                }
                finally {
                    try {
                        inputStream.close();
                    }
                    catch (IOException var6_8) {}
                }
            }
            InputStream inputStream = BaseFont.getResourceStream(string);
            if (inputStream == null) {
                throw new IOException(string + " not found as file or resource.");
            }
            try {
                this.arrayIn = RandomAccessFileOrArray.InputStreamToArray(inputStream);
                return;
            }
            finally {
                try {
                    inputStream.close();
                }
                catch (IOException var6_9) {}
            }
        }
        if (bl) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                this.arrayIn = RandomAccessFileOrArray.InputStreamToArray(fileInputStream);
                Object var12_14 = null;
            }
            catch (Throwable var11_18) {
                Object var12_15 = null;
                try {
                    if (fileInputStream == null) throw var11_18;
                    fileInputStream.close();
                    throw var11_18;
                }
                catch (Exception var13_17) {
                    // empty catch block
                }
                throw var11_18;
            }
            try {
                if (fileInputStream == null) return;
                fileInputStream.close();
                return;
            }
            catch (Exception var13_16) {}
            return;
        }
        this.filename = string;
        if (bl2) {
            this.trf = new RandomAccessFile(string, "r");
            return;
        } else {
            this.rf = new MappedRandomAccessFile(string, "r");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RandomAccessFileOrArray(URL uRL) throws IOException {
        InputStream inputStream = uRL.openStream();
        try {
            this.arrayIn = RandomAccessFileOrArray.InputStreamToArray(inputStream);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException var3_3) {}
        }
    }

    public RandomAccessFileOrArray(InputStream inputStream) throws IOException {
        this.arrayIn = RandomAccessFileOrArray.InputStreamToArray(inputStream);
    }

    public static byte[] InputStreamToArray(InputStream inputStream) throws IOException {
        int n;
        byte[] arrby = new byte[8192];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while ((n = inputStream.read(arrby)) >= 1) {
            byteArrayOutputStream.write(arrby, 0, n);
        }
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public RandomAccessFileOrArray(byte[] arrby) {
        this.arrayIn = arrby;
    }

    public RandomAccessFileOrArray(RandomAccessFileOrArray randomAccessFileOrArray) {
        this.filename = randomAccessFileOrArray.filename;
        this.arrayIn = randomAccessFileOrArray.arrayIn;
        this.startOffset = randomAccessFileOrArray.startOffset;
        this.plainRandomAccess = randomAccessFileOrArray.plainRandomAccess;
    }

    public void pushBack(byte by) {
        this.back = by;
        this.isBack = true;
    }

    public int read() throws IOException {
        if (this.isBack) {
            this.isBack = false;
            return this.back & 255;
        }
        if (this.arrayIn == null) {
            return this.plainRandomAccess ? this.trf.read() : this.rf.read();
        }
        if (this.arrayInPtr >= this.arrayIn.length) {
            return -1;
        }
        return this.arrayIn[this.arrayInPtr++] & 255;
    }

    public int read(byte[] arrby, int n, int n2) throws IOException {
        if (n2 == 0) {
            return 0;
        }
        int n3 = 0;
        if (this.isBack) {
            this.isBack = false;
            if (n2 == 1) {
                arrby[n] = this.back;
                return 1;
            }
            n3 = 1;
            arrby[n++] = this.back;
            --n2;
        }
        if (this.arrayIn == null) {
            return (this.plainRandomAccess ? this.trf.read(arrby, n, n2) : this.rf.read(arrby, n, n2)) + n3;
        }
        if (this.arrayInPtr >= this.arrayIn.length) {
            return -1;
        }
        if (this.arrayInPtr + n2 > this.arrayIn.length) {
            n2 = this.arrayIn.length - this.arrayInPtr;
        }
        System.arraycopy(this.arrayIn, this.arrayInPtr, arrby, n, n2);
        this.arrayInPtr += n2;
        return n2 + n3;
    }

    public int read(byte[] arrby) throws IOException {
        return this.read(arrby, 0, arrby.length);
    }

    public void readFully(byte[] arrby) throws IOException {
        this.readFully(arrby, 0, arrby.length);
    }

    public void readFully(byte[] arrby, int n, int n2) throws IOException {
        int n3;
        int n4 = 0;
        do {
            if ((n3 = this.read(arrby, n + n4, n2 - n4)) >= 0) continue;
            throw new EOFException();
        } while ((n4 += n3) < n2);
    }

    public long skip(long l) throws IOException {
        return this.skipBytes((int)l);
    }

    public int skipBytes(int n) throws IOException {
        int n2;
        int n3;
        int n4;
        if (n <= 0) {
            return 0;
        }
        int n5 = 0;
        if (this.isBack) {
            this.isBack = false;
            if (n == 1) {
                return 1;
            }
            --n;
            n5 = 1;
        }
        if ((n4 = (n2 = this.getFilePointer()) + n) > (n3 = this.length())) {
            n4 = n3;
        }
        this.seek(n4);
        return n4 - n2 + n5;
    }

    public void reOpen() throws IOException {
        if (this.filename != null && this.rf == null && this.trf == null) {
            if (this.plainRandomAccess) {
                this.trf = new RandomAccessFile(this.filename, "r");
            } else {
                this.rf = new MappedRandomAccessFile(this.filename, "r");
            }
        }
        this.seek(0);
    }

    protected void insureOpen() throws IOException {
        if (this.filename != null && this.rf == null && this.trf == null) {
            this.reOpen();
        }
    }

    public boolean isOpen() {
        return this.filename == null || this.rf != null || this.trf != null;
    }

    public void close() throws IOException {
        this.isBack = false;
        if (this.rf != null) {
            this.rf.close();
            this.rf = null;
            this.plainRandomAccess = true;
        } else if (this.trf != null) {
            this.trf.close();
            this.trf = null;
        }
    }

    public int length() throws IOException {
        if (this.arrayIn == null) {
            this.insureOpen();
            return (int)(this.plainRandomAccess ? this.trf.length() : this.rf.length()) - this.startOffset;
        }
        return this.arrayIn.length - this.startOffset;
    }

    public void seek(int n) throws IOException {
        n += this.startOffset;
        this.isBack = false;
        if (this.arrayIn == null) {
            this.insureOpen();
            if (this.plainRandomAccess) {
                this.trf.seek(n);
            } else {
                this.rf.seek(n);
            }
        } else {
            this.arrayInPtr = n;
        }
    }

    public void seek(long l) throws IOException {
        this.seek((int)l);
    }

    public int getFilePointer() throws IOException {
        int n;
        this.insureOpen();
        int n2 = n = this.isBack ? 1 : 0;
        if (this.arrayIn == null) {
            return (int)(this.plainRandomAccess ? this.trf.getFilePointer() : this.rf.getFilePointer()) - n - this.startOffset;
        }
        return this.arrayInPtr - n - this.startOffset;
    }

    public boolean readBoolean() throws IOException {
        int n = this.read();
        if (n < 0) {
            throw new EOFException();
        }
        return n != 0;
    }

    public byte readByte() throws IOException {
        int n = this.read();
        if (n < 0) {
            throw new EOFException();
        }
        return (byte)n;
    }

    public int readUnsignedByte() throws IOException {
        int n = this.read();
        if (n < 0) {
            throw new EOFException();
        }
        return n;
    }

    public short readShort() throws IOException {
        int n;
        int n2 = this.read();
        if ((n2 | (n = this.read())) < 0) {
            throw new EOFException();
        }
        return (short)((n2 << 8) + n);
    }

    public final short readShortLE() throws IOException {
        int n;
        int n2 = this.read();
        if ((n2 | (n = this.read())) < 0) {
            throw new EOFException();
        }
        return (short)((n << 8) + (n2 << 0));
    }

    public int readUnsignedShort() throws IOException {
        int n;
        int n2 = this.read();
        if ((n2 | (n = this.read())) < 0) {
            throw new EOFException();
        }
        return (n2 << 8) + n;
    }

    public final int readUnsignedShortLE() throws IOException {
        int n;
        int n2 = this.read();
        if ((n2 | (n = this.read())) < 0) {
            throw new EOFException();
        }
        return (n << 8) + (n2 << 0);
    }

    public char readChar() throws IOException {
        int n;
        int n2 = this.read();
        if ((n2 | (n = this.read())) < 0) {
            throw new EOFException();
        }
        return (char)((n2 << 8) + n);
    }

    public final char readCharLE() throws IOException {
        int n;
        int n2 = this.read();
        if ((n2 | (n = this.read())) < 0) {
            throw new EOFException();
        }
        return (char)((n << 8) + (n2 << 0));
    }

    public int readInt() throws IOException {
        int n;
        int n2;
        int n3;
        int n4 = this.read();
        if ((n4 | (n3 = this.read()) | (n = this.read()) | (n2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (n4 << 24) + (n3 << 16) + (n << 8) + n2;
    }

    public final int readIntLE() throws IOException {
        int n;
        int n2;
        int n3;
        int n4 = this.read();
        if ((n4 | (n3 = this.read()) | (n = this.read()) | (n2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (n2 << 24) + (n << 16) + (n3 << 8) + (n4 << 0);
    }

    public final long readUnsignedInt() throws IOException {
        long l;
        long l2;
        long l3;
        long l4 = this.read();
        if ((l4 | (l3 = (long)this.read()) | (l = (long)this.read()) | (l2 = (long)this.read())) < 0) {
            throw new EOFException();
        }
        return (l4 << 24) + (l3 << 16) + (l << 8) + (l2 << 0);
    }

    public final long readUnsignedIntLE() throws IOException {
        long l;
        long l2;
        long l3;
        long l4 = this.read();
        if ((l4 | (l3 = (long)this.read()) | (l = (long)this.read()) | (l2 = (long)this.read())) < 0) {
            throw new EOFException();
        }
        return (l2 << 24) + (l << 16) + (l3 << 8) + (l4 << 0);
    }

    public long readLong() throws IOException {
        return ((long)this.readInt() << 32) + ((long)this.readInt() & 0xFFFFFFFFL);
    }

    public final long readLongLE() throws IOException {
        int n = this.readIntLE();
        int n2 = this.readIntLE();
        return ((long)n2 << 32) + ((long)n & 0xFFFFFFFFL);
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    public final float readFloatLE() throws IOException {
        return Float.intBitsToFloat(this.readIntLE());
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    public final double readDoubleLE() throws IOException {
        return Double.longBitsToDouble(this.readLongLE());
    }

    public String readLine() throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        int n = -1;
        boolean bl = false;
        block4 : while (!bl) {
            n = this.read();
            switch (n) {
                case -1: 
                case 10: {
                    bl = true;
                    continue block4;
                }
                case 13: {
                    bl = true;
                    int n2 = this.getFilePointer();
                    if (this.read() == 10) continue block4;
                    this.seek(n2);
                    continue block4;
                }
            }
            stringBuffer.append((char)n);
        }
        if (n == -1 && stringBuffer.length() == 0) {
            return null;
        }
        return stringBuffer.toString();
    }

    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    public int getStartOffset() {
        return this.startOffset;
    }

    public void setStartOffset(int n) {
        this.startOffset = n;
    }

    public ByteBuffer getNioByteBuffer() throws IOException {
        if (this.filename != null) {
            FileChannel fileChannel = this.plainRandomAccess ? this.trf.getChannel() : this.rf.getChannel();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        }
        return ByteBuffer.wrap(this.arrayIn);
    }
}

