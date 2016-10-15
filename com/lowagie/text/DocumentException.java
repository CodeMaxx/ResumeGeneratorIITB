/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text;

import java.io.PrintStream;
import java.io.PrintWriter;

public class DocumentException
extends Exception {
    private static final long serialVersionUID = -2191131489390840739L;
    private Exception ex;

    public DocumentException(Exception exception) {
        this.ex = exception;
    }

    public DocumentException() {
    }

    public DocumentException(String string) {
        super(string);
    }

    public String getMessage() {
        if (this.ex == null) {
            return super.getMessage();
        }
        return this.ex.getMessage();
    }

    public String getLocalizedMessage() {
        if (this.ex == null) {
            return super.getLocalizedMessage();
        }
        return this.ex.getLocalizedMessage();
    }

    public String toString() {
        if (this.ex == null) {
            return super.toString();
        }
        return DocumentException.split(this.getClass().getName()) + ": " + this.ex;
    }

    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintStream printStream) {
        if (this.ex == null) {
            super.printStackTrace(printStream);
        } else {
            PrintStream printStream2 = printStream;
            synchronized (printStream2) {
                printStream.print(DocumentException.split(this.getClass().getName()) + ": ");
                this.ex.printStackTrace(printStream);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintWriter printWriter) {
        if (this.ex == null) {
            super.printStackTrace(printWriter);
        } else {
            PrintWriter printWriter2 = printWriter;
            synchronized (printWriter2) {
                printWriter.print(DocumentException.split(this.getClass().getName()) + ": ");
                this.ex.printStackTrace(printWriter);
            }
        }
    }

    private static String split(String string) {
        int n = string.lastIndexOf(46);
        if (n < 0) {
            return string;
        }
        return string.substring(n + 1);
    }
}

