/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.crypto.BlockCipher
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.engines.AESFastEngine
 *  org.bouncycastle.crypto.modes.CBCBlockCipher
 *  org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
 *  org.bouncycastle.crypto.params.KeyParameter
 *  org.bouncycastle.crypto.params.ParametersWithIV
 */
package com.lowagie.text.pdf.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class AESCipher {
    private PaddedBufferedBlockCipher bp;

    public AESCipher(boolean bl, byte[] arrby, byte[] arrby2) {
        AESFastEngine aESFastEngine = new AESFastEngine();
        CBCBlockCipher cBCBlockCipher = new CBCBlockCipher((BlockCipher)aESFastEngine);
        this.bp = new PaddedBufferedBlockCipher((BlockCipher)cBCBlockCipher);
        KeyParameter keyParameter = new KeyParameter(arrby);
        ParametersWithIV parametersWithIV = new ParametersWithIV((CipherParameters)keyParameter, arrby2);
        this.bp.init(bl, (CipherParameters)parametersWithIV);
    }

    public byte[] update(byte[] arrby, int n, int n2) {
        int n3 = this.bp.getUpdateOutputSize(n2);
        byte[] arrby2 = null;
        if (n3 > 0) {
            arrby2 = new byte[n3];
        } else {
            n3 = 0;
        }
        this.bp.processBytes(arrby, n, n2, arrby2, 0);
        return arrby2;
    }

    public byte[] doFinal() {
        int n = this.bp.getOutputSize(0);
        byte[] arrby = new byte[n];
        int n2 = 0;
        try {
            n2 = this.bp.doFinal(arrby, 0);
        }
        catch (Exception var4_4) {
            return arrby;
        }
        if (n2 != arrby.length) {
            byte[] arrby2 = new byte[n2];
            System.arraycopy(arrby, 0, arrby2, 0, n2);
            return arrby2;
        }
        return arrby;
    }
}

