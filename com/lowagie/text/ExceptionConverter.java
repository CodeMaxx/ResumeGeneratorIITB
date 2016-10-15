/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ExceptionConverter
extends RuntimeException {
    private static final long serialVersionUID = 8657630363395849399L;
    private Exception ex;
    private String prefix;

    public ExceptionConverter(Exception exception) {
        this.ex = exception;
        this.prefix = exception instanceof RuntimeException ? "" : "ExceptionConverter: ";
    }

    public Exception getException() {
        return this.ex;
    }

    public String getMessage() {
        return this.ex.getMessage();
    }

    public String getLocalizedMessage() {
        return this.ex.getLocalizedMessage();
    }

    public String toString() {
        return this.prefix + this.ex;
    }

    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintStream printStream) {
        PrintStream printStream2 = printStream;
        synchronized (printStream2) {
            printStream.print(this.prefix);
            this.ex.printStackTrace(printStream);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintWriter printWriter) {
        PrintWriter printWriter2 = printWriter;
        synchronized (printWriter2) {
            printWriter.print(this.prefix);
            this.ex.printStackTrace(printWriter);
        }
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}

