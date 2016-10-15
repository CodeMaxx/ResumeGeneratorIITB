/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.codec.BmpImage;
import com.lowagie.text.pdf.codec.wmf.InputMeta;
import com.lowagie.text.pdf.codec.wmf.MetaBrush;
import com.lowagie.text.pdf.codec.wmf.MetaFont;
import com.lowagie.text.pdf.codec.wmf.MetaObject;
import com.lowagie.text.pdf.codec.wmf.MetaPen;
import com.lowagie.text.pdf.codec.wmf.MetaState;
import java.awt.Color;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

public class MetaDo {
    public static final int META_SETBKCOLOR = 513;
    public static final int META_SETBKMODE = 258;
    public static final int META_SETMAPMODE = 259;
    public static final int META_SETROP2 = 260;
    public static final int META_SETRELABS = 261;
    public static final int META_SETPOLYFILLMODE = 262;
    public static final int META_SETSTRETCHBLTMODE = 263;
    public static final int META_SETTEXTCHAREXTRA = 264;
    public static final int META_SETTEXTCOLOR = 521;
    public static final int META_SETTEXTJUSTIFICATION = 522;
    public static final int META_SETWINDOWORG = 523;
    public static final int META_SETWINDOWEXT = 524;
    public static final int META_SETVIEWPORTORG = 525;
    public static final int META_SETVIEWPORTEXT = 526;
    public static final int META_OFFSETWINDOWORG = 527;
    public static final int META_SCALEWINDOWEXT = 1040;
    public static final int META_OFFSETVIEWPORTORG = 529;
    public static final int META_SCALEVIEWPORTEXT = 1042;
    public static final int META_LINETO = 531;
    public static final int META_MOVETO = 532;
    public static final int META_EXCLUDECLIPRECT = 1045;
    public static final int META_INTERSECTCLIPRECT = 1046;
    public static final int META_ARC = 2071;
    public static final int META_ELLIPSE = 1048;
    public static final int META_FLOODFILL = 1049;
    public static final int META_PIE = 2074;
    public static final int META_RECTANGLE = 1051;
    public static final int META_ROUNDRECT = 1564;
    public static final int META_PATBLT = 1565;
    public static final int META_SAVEDC = 30;
    public static final int META_SETPIXEL = 1055;
    public static final int META_OFFSETCLIPRGN = 544;
    public static final int META_TEXTOUT = 1313;
    public static final int META_BITBLT = 2338;
    public static final int META_STRETCHBLT = 2851;
    public static final int META_POLYGON = 804;
    public static final int META_POLYLINE = 805;
    public static final int META_ESCAPE = 1574;
    public static final int META_RESTOREDC = 295;
    public static final int META_FILLREGION = 552;
    public static final int META_FRAMEREGION = 1065;
    public static final int META_INVERTREGION = 298;
    public static final int META_PAINTREGION = 299;
    public static final int META_SELECTCLIPREGION = 300;
    public static final int META_SELECTOBJECT = 301;
    public static final int META_SETTEXTALIGN = 302;
    public static final int META_CHORD = 2096;
    public static final int META_SETMAPPERFLAGS = 561;
    public static final int META_EXTTEXTOUT = 2610;
    public static final int META_SETDIBTODEV = 3379;
    public static final int META_SELECTPALETTE = 564;
    public static final int META_REALIZEPALETTE = 53;
    public static final int META_ANIMATEPALETTE = 1078;
    public static final int META_SETPALENTRIES = 55;
    public static final int META_POLYPOLYGON = 1336;
    public static final int META_RESIZEPALETTE = 313;
    public static final int META_DIBBITBLT = 2368;
    public static final int META_DIBSTRETCHBLT = 2881;
    public static final int META_DIBCREATEPATTERNBRUSH = 322;
    public static final int META_STRETCHDIB = 3907;
    public static final int META_EXTFLOODFILL = 1352;
    public static final int META_DELETEOBJECT = 496;
    public static final int META_CREATEPALETTE = 247;
    public static final int META_CREATEPATTERNBRUSH = 505;
    public static final int META_CREATEPENINDIRECT = 762;
    public static final int META_CREATEFONTINDIRECT = 763;
    public static final int META_CREATEBRUSHINDIRECT = 764;
    public static final int META_CREATEREGION = 1791;
    public PdfContentByte cb;
    public InputMeta in;
    int left;
    int top;
    int right;
    int bottom;
    int inch;
    MetaState state = new MetaState();

    public MetaDo(InputStream inputStream, PdfContentByte pdfContentByte) {
        this.cb = pdfContentByte;
        this.in = new InputMeta(inputStream);
    }

    public void readAll() throws IOException, DocumentException {
        if (this.in.readInt() != -1698247209) {
            throw new DocumentException("Not a placeable windows metafile");
        }
        this.in.readWord();
        this.left = this.in.readShort();
        this.top = this.in.readShort();
        this.right = this.in.readShort();
        this.bottom = this.in.readShort();
        this.inch = this.in.readWord();
        this.state.setScalingX((float)(this.right - this.left) / (float)this.inch * 72.0f);
        this.state.setScalingY((float)(this.bottom - this.top) / (float)this.inch * 72.0f);
        this.state.setOffsetWx(this.left);
        this.state.setOffsetWy(this.top);
        this.state.setExtentWx(this.right - this.left);
        this.state.setExtentWy(this.bottom - this.top);
        this.in.readInt();
        this.in.readWord();
        this.in.skip(18);
        this.cb.setLineCap(1);
        this.cb.setLineJoin(1);
        do {
            int n = this.in.getLength();
            int n2 = this.in.readInt();
            if (n2 < 3) break;
            int n3 = this.in.readWord();
            switch (n3) {
                int n4;
                float f3;
                float[] arrf;
                float f;
                float f6;
                int n5;
                int n6;
                Object object2;
                reference var7_52;
                ArrayList arrayList;
                float f2;
                float f5;
                Object object;
                float f4;
                case 0: {
                    break;
                }
                case 247: 
                case 322: 
                case 1791: {
                    this.state.addMetaObject(new MetaObject());
                    break;
                }
                case 762: {
                    object = new MetaPen();
                    object.init(this.in);
                    this.state.addMetaObject((MetaObject)object);
                    break;
                }
                case 764: {
                    object = new MetaBrush();
                    object.init(this.in);
                    this.state.addMetaObject((MetaObject)object);
                    break;
                }
                case 763: {
                    object = new MetaFont();
                    object.init(this.in);
                    this.state.addMetaObject((MetaObject)object);
                    break;
                }
                case 301: {
                    int n7 = this.in.readWord();
                    this.state.selectMetaObject(n7, this.cb);
                    break;
                }
                case 496: {
                    int n8 = this.in.readWord();
                    this.state.deleteMetaObject(n8);
                    break;
                }
                case 30: {
                    this.state.saveState(this.cb);
                    break;
                }
                case 295: {
                    int n9 = this.in.readShort();
                    this.state.restoreState(n9, this.cb);
                    break;
                }
                case 523: {
                    this.state.setOffsetWy(this.in.readShort());
                    this.state.setOffsetWx(this.in.readShort());
                    break;
                }
                case 524: {
                    this.state.setExtentWy(this.in.readShort());
                    this.state.setExtentWx(this.in.readShort());
                    break;
                }
                case 532: {
                    int n10 = this.in.readShort();
                    object2 = new Point(this.in.readShort(), n10);
                    this.state.setCurrentPoint((Point)object2);
                    break;
                }
                case 531: {
                    int n11 = this.in.readShort();
                    int n12 = this.in.readShort();
                    Point point = this.state.getCurrentPoint();
                    this.cb.moveTo(this.state.transformX(point.x), this.state.transformY(point.y));
                    this.cb.lineTo(this.state.transformX(n12), this.state.transformY(n11));
                    this.cb.stroke();
                    this.state.setCurrentPoint(new Point(n12, n11));
                    break;
                }
                case 805: {
                    this.state.setLineJoinPolygon(this.cb);
                    int n13 = this.in.readWord();
                    int n14 = this.in.readShort();
                    int n15 = this.in.readShort();
                    this.cb.moveTo(this.state.transformX(n14), this.state.transformY(n15));
                    for (var7_52 = (reference)true ? 1 : 0; var7_52 < n13; ++var7_52) {
                        n14 = this.in.readShort();
                        n15 = this.in.readShort();
                        this.cb.lineTo(this.state.transformX(n14), this.state.transformY(n15));
                    }
                    this.cb.stroke();
                    break;
                }
                case 804: {
                    if (this.isNullStrokeFill(false)) break;
                    int n16 = this.in.readWord();
                    int n17 = this.in.readShort();
                    int n18 = this.in.readShort();
                    this.cb.moveTo(this.state.transformX(n17), this.state.transformY(n18));
                    for (var7_52 = (reference)true ? 1 : 0; var7_52 < n16; ++var7_52) {
                        n5 = this.in.readShort();
                        f5 = this.in.readShort();
                        this.cb.lineTo(this.state.transformX(n5), this.state.transformY((int)f5));
                    }
                    this.cb.lineTo(this.state.transformX(n17), this.state.transformY(n18));
                    this.strokeAndFill();
                    break;
                }
                case 1336: {
                    int n19;
                    if (this.isNullStrokeFill(false)) break;
                    int n20 = this.in.readWord();
                    object2 = new int[n20];
                    for (n19 = 0; n19 < object2.length; ++n19) {
                        object2[n19] = this.in.readWord();
                    }
                    for (n19 = 0; n19 < object2.length; ++n19) {
                        var7_52 = (reference)object2[n19];
                        int n21 = this.in.readShort();
                        int n22 = this.in.readShort();
                        this.cb.moveTo(this.state.transformX(n21), this.state.transformY(n22));
                        for (f3 = (float)true ? 1 : 0; f3 < var7_52; ++f3) {
                            f6 = this.in.readShort();
                            n6 = this.in.readShort();
                            this.cb.lineTo(this.state.transformX((int)f6), this.state.transformY(n6));
                        }
                        this.cb.lineTo(this.state.transformX(n21), this.state.transformY(n22));
                    }
                    this.strokeAndFill();
                    break;
                }
                case 1048: {
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) break;
                    int n23 = this.in.readShort();
                    int n24 = this.in.readShort();
                    int n25 = this.in.readShort();
                    var7_52 = this.in.readShort();
                    this.cb.arc(this.state.transformX((int)var7_52), this.state.transformY(n23), this.state.transformX(n24), this.state.transformY(n25), 0.0f, 360.0f);
                    this.strokeAndFill();
                    break;
                }
                case 2071: {
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) break;
                    float f7 = this.state.transformY(this.in.readShort());
                    float f8 = this.state.transformX(this.in.readShort());
                    float f9 = this.state.transformY(this.in.readShort());
                    float f10 = this.state.transformX(this.in.readShort());
                    n5 = (int)this.state.transformY(this.in.readShort());
                    f5 = this.state.transformX(this.in.readShort());
                    f3 = this.state.transformY(this.in.readShort());
                    f6 = this.state.transformX(this.in.readShort());
                    n6 = (int)((f5 + f6) / 2.0f);
                    f4 = (f3 + n5) / 2.0f;
                    f2 = MetaDo.getArc(n6, f4, f10, f9);
                    f = MetaDo.getArc(n6, f4, f8, f7);
                    if ((f -= f2) <= 0.0f) {
                        f += 360.0f;
                    }
                    this.cb.arc(f6, n5, f5, f3, f2, f);
                    this.cb.stroke();
                    break;
                }
                case 2074: {
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) break;
                    float f11 = this.state.transformY(this.in.readShort());
                    float f12 = this.state.transformX(this.in.readShort());
                    float f13 = this.state.transformY(this.in.readShort());
                    float f14 = this.state.transformX(this.in.readShort());
                    n5 = (int)this.state.transformY(this.in.readShort());
                    f5 = this.state.transformX(this.in.readShort());
                    f3 = this.state.transformY(this.in.readShort());
                    f6 = this.state.transformX(this.in.readShort());
                    n6 = (int)((f5 + f6) / 2.0f);
                    f4 = (f3 + n5) / 2.0f;
                    f2 = MetaDo.getArc(n6, f4, f14, f13);
                    f = MetaDo.getArc(n6, f4, f12, f11);
                    if ((f -= f2) <= 0.0f) {
                        f += 360.0f;
                    }
                    if ((arrayList = PdfContentByte.bezierArc(f6, n5, f5, f3, f2, f)).isEmpty()) break;
                    arrf = (float[])arrayList.get(0);
                    this.cb.moveTo(n6, f4);
                    this.cb.lineTo(arrf[0], arrf[1]);
                    for (n4 = 0; n4 < arrayList.size(); ++n4) {
                        arrf = (float[])arrayList.get(n4);
                        this.cb.curveTo(arrf[2], arrf[3], arrf[4], arrf[5], arrf[6], arrf[7]);
                    }
                    this.cb.lineTo(n6, f4);
                    this.strokeAndFill();
                    break;
                }
                case 2096: {
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) break;
                    float f15 = this.state.transformY(this.in.readShort());
                    float f16 = this.state.transformX(this.in.readShort());
                    float f17 = this.state.transformY(this.in.readShort());
                    float f18 = this.state.transformX(this.in.readShort());
                    n5 = (int)this.state.transformY(this.in.readShort());
                    f5 = this.state.transformX(this.in.readShort());
                    f3 = this.state.transformY(this.in.readShort());
                    f6 = this.state.transformX(this.in.readShort());
                    n6 = (int)((f5 + f6) / 2.0f);
                    f4 = (f3 + n5) / 2.0f;
                    f2 = MetaDo.getArc(n6, f4, f18, f17);
                    f = MetaDo.getArc(n6, f4, f16, f15);
                    if ((f -= f2) <= 0.0f) {
                        f += 360.0f;
                    }
                    if ((arrayList = PdfContentByte.bezierArc(f6, n5, f5, f3, f2, f)).isEmpty()) break;
                    arrf = (float[])arrayList.get(0);
                    n6 = (int)arrf[0];
                    f4 = arrf[1];
                    this.cb.moveTo(n6, f4);
                    for (n4 = 0; n4 < arrayList.size(); ++n4) {
                        arrf = (float[])arrayList.get(n4);
                        this.cb.curveTo(arrf[2], arrf[3], arrf[4], arrf[5], arrf[6], arrf[7]);
                    }
                    this.cb.lineTo(n6, f4);
                    this.strokeAndFill();
                    break;
                }
                case 1051: {
                    if (this.isNullStrokeFill(true)) break;
                    float f19 = this.state.transformY(this.in.readShort());
                    float f20 = this.state.transformX(this.in.readShort());
                    float f21 = this.state.transformY(this.in.readShort());
                    float f22 = this.state.transformX(this.in.readShort());
                    this.cb.rectangle(f22, f19, f20 - f22, f21 - f19);
                    this.strokeAndFill();
                    break;
                }
                case 1564: {
                    if (this.isNullStrokeFill(true)) break;
                    float f23 = this.state.transformY(0) - this.state.transformY(this.in.readShort());
                    float f24 = this.state.transformX(this.in.readShort()) - this.state.transformX(0);
                    float f25 = this.state.transformY(this.in.readShort());
                    float f26 = this.state.transformX(this.in.readShort());
                    n5 = (int)this.state.transformY(this.in.readShort());
                    f5 = this.state.transformX(this.in.readShort());
                    this.cb.roundRectangle(f5, f25, f26 - f5, n5 - f25, (f23 + f24) / 4.0f);
                    this.strokeAndFill();
                    break;
                }
                case 1046: {
                    float f27 = this.state.transformY(this.in.readShort());
                    float f28 = this.state.transformX(this.in.readShort());
                    float f29 = this.state.transformY(this.in.readShort());
                    float f30 = this.state.transformX(this.in.readShort());
                    this.cb.rectangle(f30, f27, f28 - f30, f29 - f27);
                    this.cb.eoClip();
                    this.cb.newPath();
                    break;
                }
                case 2610: {
                    int n26;
                    String string;
                    byte by;
                    int n27 = this.in.readShort();
                    int n28 = this.in.readShort();
                    int n29 = this.in.readWord();
                    var7_52 = this.in.readWord();
                    int n30 = 0;
                    int n31 = 0;
                    int n32 = 0;
                    int n33 = 0;
                    if ((var7_52 & 6) != 0) {
                        n30 = this.in.readShort();
                        n31 = this.in.readShort();
                        n32 = this.in.readShort();
                        n33 = this.in.readShort();
                    }
                    byte[] arrby = new byte[n29];
                    for (n26 = 0; n26 < n29 && (by = (byte)this.in.readByte()) != 0; ++n26) {
                        arrby[n26] = by;
                    }
                    try {
                        string = new String(arrby, 0, n26, "Cp1252");
                    }
                    catch (UnsupportedEncodingException var15_86) {
                        string = new String(arrby, 0, n26);
                    }
                    this.outputText(n28, n27, (int)var7_52, n30, n31, n32, n33, string);
                    break;
                }
                case 1313: {
                    String string;
                    int n34;
                    int n35 = this.in.readWord();
                    object2 = new byte[n35];
                    for (n34 = 0; n34 < n35 && (var7_52 = (reference)this.in.readByte()) != 0; ++n34) {
                        object2[n34] = var7_52;
                    }
                    try {
                        string = new String((byte[])object2, 0, n34, "Cp1252");
                    }
                    catch (UnsupportedEncodingException var8_63) {
                        string = new String((byte[])object2, 0, n34);
                    }
                    n35 = n35 + 1 & 65534;
                    this.in.skip(n35 - n34);
                    int n36 = this.in.readShort();
                    int n37 = this.in.readShort();
                    this.outputText(n37, n36, 0, 0, 0, 0, 0, string);
                    break;
                }
                case 513: {
                    this.state.setCurrentBackgroundColor(this.in.readColor());
                    break;
                }
                case 521: {
                    this.state.setCurrentTextColor(this.in.readColor());
                    break;
                }
                case 302: {
                    this.state.setTextAlign(this.in.readWord());
                    break;
                }
                case 258: {
                    this.state.setBackgroundMode(this.in.readWord());
                    break;
                }
                case 262: {
                    this.state.setPolyFillMode(this.in.readWord());
                    break;
                }
                case 1055: {
                    object = this.in.readColor();
                    int n38 = this.in.readShort();
                    int n39 = this.in.readShort();
                    this.cb.saveState();
                    this.cb.setColorFill((Color)object);
                    this.cb.rectangle(this.state.transformX(n39), this.state.transformY(n38), 0.2f, 0.2f);
                    this.cb.fill();
                    this.cb.restoreState();
                    break;
                }
                case 2881: 
                case 3907: {
                    int n40 = this.in.readInt();
                    if (n3 == 3907) {
                        this.in.readWord();
                    }
                    int n41 = this.in.readShort();
                    int n42 = this.in.readShort();
                    var7_52 = this.in.readShort();
                    int n43 = this.in.readShort();
                    f5 = this.state.transformY(this.in.readShort()) - this.state.transformY(0);
                    f3 = this.state.transformX(this.in.readShort()) - this.state.transformX(0);
                    f6 = this.state.transformY(this.in.readShort());
                    n6 = (int)this.state.transformX(this.in.readShort());
                    byte[] arrby = new byte[n2 * 2 - (this.in.getLength() - n)];
                    for (int i = 0; i < arrby.length; ++i) {
                        arrby[i] = (byte)this.in.readByte();
                    }
                    try {
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
                        Image image = BmpImage.getImage(byteArrayInputStream, true, arrby.length);
                        this.cb.saveState();
                        this.cb.rectangle(n6, f6, f3, f5);
                        this.cb.clip();
                        this.cb.newPath();
                        image.scaleAbsolute(f3 * image.getWidth() / (float)n42, (- f5) * image.getHeight() / (float)n41);
                        image.setAbsolutePosition(n6 - f3 * (float)n43 / (float)n42, f6 + f5 * (Object)var7_52 / (float)n41 - image.getScaledHeight());
                        this.cb.addImage(image);
                        this.cb.restoreState();
                        break;
                    }
                    catch (Exception var14_84) {
                        // empty catch block
                    }
                }
            }
            this.in.skip(n2 * 2 - (this.in.getLength() - n));
        } while (true);
        this.state.cleanup(this.cb);
    }

    public void outputText(int n, int n2, int n3, int n4, int n5, int n6, int n7, String string) {
        Color color;
        MetaFont metaFont = this.state.getCurrentFont();
        float f = this.state.transformX(n);
        float f2 = this.state.transformY(n2);
        float f3 = this.state.transformAngle(metaFont.getAngle());
        float f4 = (float)Math.sin(f3);
        float f5 = (float)Math.cos(f3);
        float f6 = metaFont.getFontSize(this.state);
        BaseFont baseFont = metaFont.getFont();
        int n8 = this.state.getTextAlign();
        float f7 = baseFont.getWidthPoint(string, f6);
        float f8 = 0.0f;
        float f9 = 0.0f;
        float f10 = baseFont.getFontDescriptor(3, f6);
        float f11 = baseFont.getFontDescriptor(8, f6);
        this.cb.saveState();
        this.cb.concatCTM(f5, f4, - f4, f5, f, f2);
        if ((n8 & 6) == 6) {
            f8 = (- f7) / 2.0f;
        } else if ((n8 & 2) == 2) {
            f8 = - f7;
        }
        f9 = (n8 & 24) == 24 ? 0.0f : ((n8 & 8) == 8 ? - f10 : - f11);
        if (this.state.getBackgroundMode() == 2) {
            color = this.state.getCurrentBackgroundColor();
            this.cb.setColorFill(color);
            this.cb.rectangle(f8, f9 + f10, f7, f11 - f10);
            this.cb.fill();
        }
        color = this.state.getCurrentTextColor();
        this.cb.setColorFill(color);
        this.cb.beginText();
        this.cb.setFontAndSize(baseFont, f6);
        this.cb.setTextMatrix(f8, f9);
        this.cb.showText(string);
        this.cb.endText();
        if (metaFont.isUnderline()) {
            this.cb.rectangle(f8, f9 - f6 / 4.0f, f7, f6 / 15.0f);
            this.cb.fill();
        }
        if (metaFont.isStrikeout()) {
            this.cb.rectangle(f8, f9 + f6 / 3.0f, f7, f6 / 15.0f);
            this.cb.fill();
        }
        this.cb.restoreState();
    }

    public boolean isNullStrokeFill(boolean bl) {
        boolean bl2;
        MetaPen metaPen = this.state.getCurrentPen();
        MetaBrush metaBrush = this.state.getCurrentBrush();
        boolean bl3 = metaPen.getStyle() == 5;
        int n = metaBrush.getStyle();
        boolean bl4 = n == 0 || n == 2 && this.state.getBackgroundMode() == 2;
        boolean bl5 = bl2 = bl3 && !bl4;
        if (!bl3) {
            if (bl) {
                this.state.setLineJoinRectangle(this.cb);
            } else {
                this.state.setLineJoinPolygon(this.cb);
            }
        }
        return bl2;
    }

    public void strokeAndFill() {
        MetaPen metaPen = this.state.getCurrentPen();
        MetaBrush metaBrush = this.state.getCurrentBrush();
        int n = metaPen.getStyle();
        int n2 = metaBrush.getStyle();
        if (n == 5) {
            this.cb.closePath();
            if (this.state.getPolyFillMode() == 1) {
                this.cb.eoFill();
            } else {
                this.cb.fill();
            }
        } else {
            boolean bl;
            boolean bl2 = bl = n2 == 0 || n2 == 2 && this.state.getBackgroundMode() == 2;
            if (bl) {
                if (this.state.getPolyFillMode() == 1) {
                    this.cb.closePathEoFillStroke();
                } else {
                    this.cb.closePathFillStroke();
                }
            } else {
                this.cb.closePathStroke();
            }
        }
    }

    static float getArc(float f, float f2, float f3, float f4) {
        double d = Math.atan2(f4 - f2, f3 - f);
        if (d < 0.0) {
            d += 6.283185307179586;
        }
        return (float)(d / 3.141592653589793 * 180.0);
    }

    public static byte[] wrapBMP(Image image) throws IOException {
        if (image.getOriginalType() != 4) {
            throw new IOException("Only BMP can be wrapped in WMF.");
        }
        byte[] arrby = null;
        if (image.getOriginalData() == null) {
            InputStream inputStream = image.getUrl().openStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int n = 0;
            while ((n = inputStream.read()) != -1) {
                byteArrayOutputStream.write(n);
            }
            inputStream.close();
            arrby = byteArrayOutputStream.toByteArray();
        } else {
            arrby = image.getOriginalData();
        }
        int n = arrby.length - 14 + 1 >>> 1;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MetaDo.writeWord(byteArrayOutputStream, 1);
        MetaDo.writeWord(byteArrayOutputStream, 9);
        MetaDo.writeWord(byteArrayOutputStream, 768);
        MetaDo.writeDWord(byteArrayOutputStream, 23 + (13 + n) + 3);
        MetaDo.writeWord(byteArrayOutputStream, 1);
        MetaDo.writeDWord(byteArrayOutputStream, 14 + n);
        MetaDo.writeWord(byteArrayOutputStream, 0);
        MetaDo.writeDWord(byteArrayOutputStream, 4);
        MetaDo.writeWord(byteArrayOutputStream, 259);
        MetaDo.writeWord(byteArrayOutputStream, 8);
        MetaDo.writeDWord(byteArrayOutputStream, 5);
        MetaDo.writeWord(byteArrayOutputStream, 523);
        MetaDo.writeWord(byteArrayOutputStream, 0);
        MetaDo.writeWord(byteArrayOutputStream, 0);
        MetaDo.writeDWord(byteArrayOutputStream, 5);
        MetaDo.writeWord(byteArrayOutputStream, 524);
        MetaDo.writeWord(byteArrayOutputStream, (int)image.getHeight());
        MetaDo.writeWord(byteArrayOutputStream, (int)image.getWidth());
        MetaDo.writeDWord(byteArrayOutputStream, 13 + n);
        MetaDo.writeWord(byteArrayOutputStream, 2881);
        MetaDo.writeDWord(byteArrayOutputStream, 13369376);
        MetaDo.writeWord(byteArrayOutputStream, (int)image.getHeight());
        MetaDo.writeWord(byteArrayOutputStream, (int)image.getWidth());
        MetaDo.writeWord(byteArrayOutputStream, 0);
        MetaDo.writeWord(byteArrayOutputStream, 0);
        MetaDo.writeWord(byteArrayOutputStream, (int)image.getHeight());
        MetaDo.writeWord(byteArrayOutputStream, (int)image.getWidth());
        MetaDo.writeWord(byteArrayOutputStream, 0);
        MetaDo.writeWord(byteArrayOutputStream, 0);
        byteArrayOutputStream.write(arrby, 14, arrby.length - 14);
        if ((arrby.length & 1) == 1) {
            byteArrayOutputStream.write(0);
        }
        MetaDo.writeDWord(byteArrayOutputStream, 3);
        MetaDo.writeWord(byteArrayOutputStream, 0);
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static void writeWord(OutputStream outputStream, int n) throws IOException {
        outputStream.write(n & 255);
        outputStream.write(n >>> 8 & 255);
    }

    public static void writeDWord(OutputStream outputStream, int n) throws IOException {
        MetaDo.writeWord(outputStream, n & 65535);
        MetaDo.writeWord(outputStream, n >>> 16 & 65535);
    }
}

