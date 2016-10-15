/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import java.security.cert.Certificate;

public class PdfPublicKeyRecipient {
    private Certificate certificate = null;
    private int permission = 0;
    protected byte[] cms = null;

    public PdfPublicKeyRecipient(Certificate certificate, int n) {
        this.certificate = certificate;
        this.permission = n;
    }

    public Certificate getCertificate() {
        return this.certificate;
    }

    public int getPermission() {
        return this.permission;
    }

    protected void setCms(byte[] arrby) {
        this.cms = arrby;
    }

    protected byte[] getCms() {
        return this.cms;
    }
}

