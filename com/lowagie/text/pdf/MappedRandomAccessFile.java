/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class MappedRandomAccessFile {
    private MappedByteBuffer mappedByteBuffer = null;
    private FileChannel channel = null;

    public MappedRandomAccessFile(String string, String string2) throws FileNotFoundException, IOException {
        if (string2.equals("rw")) {
            this.init(new RandomAccessFile(string, string2).getChannel(), FileChannel.MapMode.READ_WRITE);
        } else {
            this.init(new FileInputStream(string).getChannel(), FileChannel.MapMode.READ_ONLY);
        }
    }

    private void init(FileChannel fileChannel, FileChannel.MapMode mapMode) throws IOException {
        this.channel = fileChannel;
        this.mappedByteBuffer = fileChannel.map(mapMode, 0, fileChannel.size());
        this.mappedByteBuffer.load();
    }

    public FileChannel getChannel() {
        return this.channel;
    }

    public int read() {
        try {
            byte by = this.mappedByteBuffer.get();
            int n = by & 255;
            return n;
        }
        catch (BufferUnderflowException var1_2) {
            return -1;
        }
    }

    public int read(byte[] arrby, int n, int n2) {
        int n3;
        int n4 = this.mappedByteBuffer.position();
        if (n4 == (n3 = this.mappedByteBuffer.limit())) {
            return -1;
        }
        int n5 = n4 + n2 - n;
        if (n5 > n3) {
            n2 = n3 - n4;
        }
        this.mappedByteBuffer.get(arrby, n, n2);
        return n2;
    }

    public long getFilePointer() {
        return this.mappedByteBuffer.position();
    }

    public void seek(long l) {
        this.mappedByteBuffer.position((int)l);
    }

    public long length() {
        return this.mappedByteBuffer.limit();
    }

    public void close() throws IOException {
        MappedRandomAccessFile.clean(this.mappedByteBuffer);
        this.mappedByteBuffer = null;
        if (this.channel != null) {
            this.channel.close();
        }
        this.channel = null;
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    public static boolean clean(final ByteBuffer byteBuffer) {
        if (byteBuffer == null || !byteBuffer.isDirect()) {
            return false;
        }
        Boolean bl = (Boolean)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                Boolean bl = Boolean.FALSE;
                try {
                    Method method = byteBuffer.getClass().getMethod("cleaner", null);
                    method.setAccessible(true);
                    Object object = method.invoke(byteBuffer, null);
                    Method method2 = object.getClass().getMethod("clean", null);
                    method2.invoke(object, null);
                    bl = Boolean.TRUE;
                }
                catch (Exception var2_3) {
                    // empty catch block
                }
                return bl;
            }
        });
        return bl;
    }

}

