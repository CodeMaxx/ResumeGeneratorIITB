/*
 * Decompiled with CFR 0_115.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfShading;
import com.lowagie.text.pdf.PdfShadingPattern;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.internal.PolylineShape;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

public class PdfGraphics2D
extends Graphics2D {
    private static final int FILL = 1;
    private static final int STROKE = 2;
    private static final int CLIP = 3;
    private BasicStroke strokeOne = new BasicStroke(1.0f);
    private static final AffineTransform IDENTITY = new AffineTransform();
    private Font font;
    private BaseFont baseFont;
    private float fontSize;
    private AffineTransform transform;
    private Paint paint;
    private Color background;
    private float width;
    private float height;
    private Area clip;
    private RenderingHints rhints = new RenderingHints(null);
    private Stroke stroke;
    private Stroke originalStroke;
    private PdfContentByte cb;
    private HashMap baseFonts;
    private boolean disposeCalled = false;
    private FontMapper fontMapper;
    private ArrayList kids;
    private boolean kid = false;
    private Graphics2D dg2 = new BufferedImage(2, 2, 1).createGraphics();
    private boolean onlyShapes = false;
    private Stroke oldStroke;
    private Paint paintFill;
    private Paint paintStroke;
    private MediaTracker mediaTracker;
    protected boolean underline;
    protected PdfGState[] fillGState = new PdfGState[256];
    protected PdfGState[] strokeGState = new PdfGState[256];
    protected int currentFillGState = 255;
    protected int currentStrokeGState = 255;
    public static final int AFM_DIVISOR = 1000;
    private boolean convertImagesToJPEG = false;
    private float jpegQuality = 0.95f;
    private float alpha;
    private Composite composite;
    private Paint realPaint;

    private PdfGraphics2D() {
        this.dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
    }

    PdfGraphics2D(PdfContentByte pdfContentByte, float f, float f2, FontMapper fontMapper, boolean bl, boolean bl2, float f3) {
        this.dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
        this.convertImagesToJPEG = bl2;
        this.jpegQuality = f3;
        this.onlyShapes = bl;
        this.transform = new AffineTransform();
        this.baseFonts = new HashMap();
        if (!bl) {
            this.fontMapper = fontMapper;
            if (this.fontMapper == null) {
                this.fontMapper = new DefaultFontMapper();
            }
        }
        this.paint = Color.black;
        this.background = Color.white;
        this.setFont(new Font("sanserif", 0, 12));
        this.cb = pdfContentByte;
        pdfContentByte.saveState();
        this.width = f;
        this.height = f2;
        this.clip = new Area(new Rectangle2D.Float(0.0f, 0.0f, f, f2));
        this.clip(this.clip);
        this.stroke = this.oldStroke = this.strokeOne;
        this.originalStroke = this.oldStroke;
        this.setStrokeDiff(this.stroke, null);
        pdfContentByte.saveState();
    }

    public void draw(Shape shape) {
        this.followPath(shape, 2);
    }

    public boolean drawImage(java.awt.Image image, AffineTransform affineTransform, ImageObserver imageObserver) {
        return this.drawImage(image, null, affineTransform, null, imageObserver);
    }

    public void drawImage(BufferedImage bufferedImage, BufferedImageOp bufferedImageOp, int n, int n2) {
        BufferedImage bufferedImage2 = bufferedImage;
        if (bufferedImageOp != null) {
            bufferedImage2 = bufferedImageOp.createCompatibleDestImage(bufferedImage, bufferedImage.getColorModel());
            bufferedImage2 = bufferedImageOp.filter(bufferedImage, bufferedImage2);
        }
        this.drawImage((java.awt.Image)bufferedImage2, n, n2, null);
    }

    public void drawRenderedImage(RenderedImage renderedImage, AffineTransform affineTransform) {
        BufferedImage bufferedImage = null;
        if (renderedImage instanceof BufferedImage) {
            bufferedImage = (BufferedImage)renderedImage;
        } else {
            ColorModel colorModel = renderedImage.getColorModel();
            int n = renderedImage.getWidth();
            int n2 = renderedImage.getHeight();
            WritableRaster writableRaster = colorModel.createCompatibleWritableRaster(n, n2);
            boolean bl = colorModel.isAlphaPremultiplied();
            Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
            String[] arrstring = renderedImage.getPropertyNames();
            if (arrstring != null) {
                for (int i = 0; i < arrstring.length; ++i) {
                    hashtable.put(arrstring[i], renderedImage.getProperty(arrstring[i]));
                }
            }
            BufferedImage bufferedImage2 = new BufferedImage(colorModel, writableRaster, bl, hashtable);
            renderedImage.copyData(writableRaster);
            bufferedImage = bufferedImage2;
        }
        this.drawImage(bufferedImage, affineTransform, null);
    }

    public void drawRenderableImage(RenderableImage renderableImage, AffineTransform affineTransform) {
        this.drawRenderedImage(renderableImage.createDefaultRendering(), affineTransform);
    }

    public void drawString(String string, int n, int n2) {
        this.drawString(string, (float)n, (float)n2);
    }

    public static double asPoints(double d, int n) {
        return d * (double)n / 1000.0;
    }

    protected void doAttributes(AttributedCharacterIterator attributedCharacterIterator) {
        this.underline = false;
        Set<AttributedCharacterIterator.Attribute> set = attributedCharacterIterator.getAttributes().keySet();
        Iterator<AttributedCharacterIterator.Attribute> iterator = set.iterator();
        while (iterator.hasNext()) {
            Object object;
            AttributedCharacterIterator.Attribute attribute = iterator.next();
            if (!(attribute instanceof TextAttribute)) continue;
            TextAttribute textAttribute = (TextAttribute)attribute;
            if (textAttribute.equals(TextAttribute.FONT)) {
                object = (Font)attributedCharacterIterator.getAttributes().get(textAttribute);
                this.setFont((Font)object);
                continue;
            }
            if (textAttribute.equals(TextAttribute.UNDERLINE)) {
                if (attributedCharacterIterator.getAttributes().get(textAttribute) != TextAttribute.UNDERLINE_ON) continue;
                this.underline = true;
                continue;
            }
            if (textAttribute.equals(TextAttribute.SIZE)) {
                object = attributedCharacterIterator.getAttributes().get(textAttribute);
                if (object instanceof Integer) {
                    int n = (Integer)object;
                    this.setFont(this.getFont().deriveFont(this.getFont().getStyle(), n));
                    continue;
                }
                if (!(object instanceof Float)) continue;
                float f = ((Float)object).floatValue();
                this.setFont(this.getFont().deriveFont(this.getFont().getStyle(), f));
                continue;
            }
            if (textAttribute.equals(TextAttribute.FOREGROUND)) {
                this.setColor((Color)attributedCharacterIterator.getAttributes().get(textAttribute));
                continue;
            }
            if (textAttribute.equals(TextAttribute.FAMILY)) {
                object = this.getFont();
                Map map = object.getAttributes();
                map.put(TextAttribute.FAMILY, (Object)attributedCharacterIterator.getAttributes().get(textAttribute));
                this.setFont(object.deriveFont(map));
                continue;
            }
            if (textAttribute.equals(TextAttribute.POSTURE)) {
                object = this.getFont();
                Map map = object.getAttributes();
                map.put(TextAttribute.POSTURE, (Object)attributedCharacterIterator.getAttributes().get(textAttribute));
                this.setFont(object.deriveFont(map));
                continue;
            }
            if (!textAttribute.equals(TextAttribute.WEIGHT)) continue;
            object = this.getFont();
            Map map = object.getAttributes();
            map.put(TextAttribute.WEIGHT, (Object)attributedCharacterIterator.getAttributes().get(textAttribute));
            this.setFont(object.deriveFont(map));
        }
    }

    public void drawString(String string, float f, float f2) {
        if (string.length() == 0) {
            return;
        }
        this.setFillPaint();
        if (this.onlyShapes) {
            this.drawGlyphVector(this.font.layoutGlyphVector(this.getFontRenderContext(), string.toCharArray(), 0, string.length(), 0), f, f2);
        } else {
            Object object;
            boolean bl = false;
            AffineTransform affineTransform = this.getTransform();
            AffineTransform affineTransform2 = this.getTransform();
            affineTransform2.translate(f, f2);
            affineTransform2.concatenate(this.font.getTransform());
            this.setTransform(affineTransform2);
            AffineTransform affineTransform3 = this.normalizeMatrix();
            AffineTransform affineTransform4 = AffineTransform.getScaleInstance(1.0, -1.0);
            affineTransform3.concatenate(affineTransform4);
            double[] arrd = new double[6];
            affineTransform3.getMatrix(arrd);
            this.cb.beginText();
            this.cb.setFontAndSize(this.baseFont, this.fontSize);
            if (this.font.isItalic() && this.font.getFontName().equals(this.font.getName())) {
                float f3 = this.baseFont.getFontDescriptor(4, 1000.0f);
                float f4 = this.font.getItalicAngle();
                f4 = f4 == 0.0f ? 15.0f : - f4;
                if (f3 == 0.0f) {
                    arrd[2] = f4 / 100.0f;
                }
            }
            this.cb.setTextMatrix((float)arrd[0], (float)arrd[1], (float)arrd[2], (float)arrd[3], (float)arrd[4], (float)arrd[5]);
            Float f5 = (Float)this.font.getAttributes().get(TextAttribute.WIDTH);
            Float f6 = f5 = f5 == null ? TextAttribute.WIDTH_REGULAR : f5;
            if (!TextAttribute.WIDTH_REGULAR.equals(f5)) {
                this.cb.setHorizontalScaling(100.0f / f5.floatValue());
            }
            if (this.baseFont.getPostscriptFontName().toLowerCase().indexOf("bold") < 0) {
                float f7;
                Float f8 = (Float)this.font.getAttributes().get(TextAttribute.WEIGHT);
                if (f8 == null) {
                    Float f9 = f8 = this.font.isBold() ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR;
                }
                if ((this.font.isBold() || f8.floatValue() >= TextAttribute.WEIGHT_SEMIBOLD.floatValue()) && this.font.getFontName().equals(this.font.getName()) && (f7 = this.font.getSize2D() * (f8.floatValue() - TextAttribute.WEIGHT_REGULAR.floatValue()) / 30.0f) != 1.0f) {
                    this.cb.setTextRenderingMode(2);
                    this.cb.setLineWidth(f7);
                    this.cb.setColorStroke(this.getColor());
                    bl = true;
                }
            }
            double d = 0.0;
            if (this.font.getSize2D() > 0.0f) {
                float f10 = 1000.0f / this.font.getSize2D();
                Font font = this.font.deriveFont(AffineTransform.getScaleInstance(f10, f10));
                d = font.getStringBounds(string, this.getFontRenderContext()).getWidth();
                if (font.isTransformed()) {
                    d /= (double)f10;
                }
            }
            if ((object = this.getRenderingHint(HyperLinkKey.KEY_INSTANCE)) != null && !object.equals(HyperLinkKey.VALUE_HYPERLINKKEY_OFF)) {
                float f11 = 1000.0f / this.font.getSize2D();
                Font font = this.font.deriveFont(AffineTransform.getScaleInstance(f11, f11));
                double d2 = font.getStringBounds(string, this.getFontRenderContext()).getHeight();
                if (font.isTransformed()) {
                    d2 /= (double)f11;
                }
                double d3 = this.cb.getXTLM();
                double d4 = this.cb.getYTLM();
                PdfAction pdfAction = new PdfAction(object.toString());
                this.cb.setAction(pdfAction, (float)d3, (float)d4, (float)(d3 + d), (float)(d4 + d2));
            }
            if (string.length() > 1) {
                float f12 = ((float)d - this.baseFont.getWidthPoint(string, this.fontSize)) / (float)(string.length() - 1);
                this.cb.setCharacterSpacing(f12);
            }
            this.cb.showText(string);
            if (string.length() > 1) {
                this.cb.setCharacterSpacing(0.0f);
            }
            if (!TextAttribute.WIDTH_REGULAR.equals(f5)) {
                this.cb.setHorizontalScaling(100.0f);
            }
            if (bl) {
                this.cb.setTextRenderingMode(0);
            }
            this.cb.endText();
            this.setTransform(affineTransform);
            if (this.underline) {
                int n = 50;
                double d5 = PdfGraphics2D.asPoints(n, (int)this.fontSize);
                Stroke stroke = this.originalStroke;
                this.setStroke(new BasicStroke((float)d5));
                f2 = (float)((double)f2 + PdfGraphics2D.asPoints(n, (int)this.fontSize));
                Line2D.Double double_ = new Line2D.Double(f, f2, d + (double)f, f2);
                this.draw(double_);
                this.setStroke(stroke);
            }
        }
    }

    public void drawString(AttributedCharacterIterator attributedCharacterIterator, int n, int n2) {
        this.drawString(attributedCharacterIterator, (float)n, (float)n2);
    }

    public void drawString(AttributedCharacterIterator attributedCharacterIterator, float f, float f2) {
        StringBuffer stringBuffer = new StringBuffer(attributedCharacterIterator.getEndIndex());
        char c = attributedCharacterIterator.first();
        while (c != '\uffff') {
            if (attributedCharacterIterator.getIndex() == attributedCharacterIterator.getRunStart()) {
                if (stringBuffer.length() > 0) {
                    this.drawString(stringBuffer.toString(), f, f2);
                    FontMetrics fontMetrics = this.getFontMetrics();
                    f = (float)((double)f + fontMetrics.getStringBounds(stringBuffer.toString(), this).getWidth());
                    stringBuffer.delete(0, stringBuffer.length());
                }
                this.doAttributes(attributedCharacterIterator);
            }
            stringBuffer.append(c);
            c = attributedCharacterIterator.next();
        }
        this.drawString(stringBuffer.toString(), f, f2);
        this.underline = false;
    }

    public void drawGlyphVector(GlyphVector glyphVector, float f, float f2) {
        Shape shape = glyphVector.getOutline(f, f2);
        this.fill(shape);
    }

    public void fill(Shape shape) {
        this.followPath(shape, 1);
    }

    public boolean hit(Rectangle rectangle, Shape shape, boolean bl) {
        if (bl) {
            shape = this.stroke.createStrokedShape(shape);
        }
        shape = this.transform.createTransformedShape(shape);
        Area area = new Area(shape);
        if (this.clip != null) {
            area.intersect(this.clip);
        }
        return area.intersects(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return this.dg2.getDeviceConfiguration();
    }

    public void setComposite(Composite composite) {
        AlphaComposite alphaComposite;
        if (composite instanceof AlphaComposite && (alphaComposite = (AlphaComposite)composite).getRule() == 3) {
            this.alpha = alphaComposite.getAlpha();
            this.composite = alphaComposite;
            if (this.realPaint != null && this.realPaint instanceof Color) {
                Color color = (Color)this.realPaint;
                this.paint = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((float)color.getAlpha() * this.alpha));
            }
            return;
        }
        this.composite = composite;
        this.alpha = 1.0f;
    }

    public void setPaint(Paint paint) {
        AlphaComposite alphaComposite;
        if (paint == null) {
            return;
        }
        this.paint = paint;
        this.realPaint = paint;
        if (this.composite instanceof AlphaComposite && paint instanceof Color && (alphaComposite = (AlphaComposite)this.composite).getRule() == 3) {
            Color color = (Color)paint;
            this.paint = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((float)color.getAlpha() * this.alpha));
            this.realPaint = paint;
        }
    }

    private Stroke transformStroke(Stroke stroke) {
        if (!(stroke instanceof BasicStroke)) {
            return stroke;
        }
        BasicStroke basicStroke = (BasicStroke)stroke;
        float f = (float)Math.sqrt(Math.abs(this.transform.getDeterminant()));
        float[] arrf = basicStroke.getDashArray();
        if (arrf != null) {
            int n = 0;
            while (n < arrf.length) {
                float[] arrf2 = arrf;
                int n2 = n++;
                arrf2[n2] = arrf2[n2] * f;
            }
        }
        return new BasicStroke(basicStroke.getLineWidth() * f, basicStroke.getEndCap(), basicStroke.getLineJoin(), basicStroke.getMiterLimit(), arrf, basicStroke.getDashPhase() * f);
    }

    private void setStrokeDiff(Stroke stroke, Stroke stroke2) {
        if (stroke == stroke2) {
            return;
        }
        if (!(stroke instanceof BasicStroke)) {
            return;
        }
        BasicStroke basicStroke = (BasicStroke)stroke;
        boolean bl = stroke2 instanceof BasicStroke;
        BasicStroke basicStroke2 = null;
        if (bl) {
            basicStroke2 = (BasicStroke)stroke2;
        }
        if (!bl || basicStroke.getLineWidth() != basicStroke2.getLineWidth()) {
            this.cb.setLineWidth(basicStroke.getLineWidth());
        }
        if (!bl || basicStroke.getEndCap() != basicStroke2.getEndCap()) {
            switch (basicStroke.getEndCap()) {
                case 0: {
                    this.cb.setLineCap(0);
                    break;
                }
                case 2: {
                    this.cb.setLineCap(2);
                    break;
                }
                default: {
                    this.cb.setLineCap(1);
                }
            }
        }
        if (!bl || basicStroke.getLineJoin() != basicStroke2.getLineJoin()) {
            switch (basicStroke.getLineJoin()) {
                case 0: {
                    this.cb.setLineJoin(0);
                    break;
                }
                case 2: {
                    this.cb.setLineJoin(2);
                    break;
                }
                default: {
                    this.cb.setLineJoin(1);
                }
            }
        }
        if (!bl || basicStroke.getMiterLimit() != basicStroke2.getMiterLimit()) {
            this.cb.setMiterLimit(basicStroke.getMiterLimit());
        }
        boolean bl2 = bl ? (basicStroke.getDashArray() != null ? (basicStroke.getDashPhase() != basicStroke2.getDashPhase() ? true : !Arrays.equals(basicStroke.getDashArray(), basicStroke2.getDashArray())) : basicStroke2.getDashArray() != null) : true;
        if (bl2) {
            float[] arrf = basicStroke.getDashArray();
            if (arrf == null) {
                this.cb.setLiteral("[]0 d\n");
            } else {
                this.cb.setLiteral('[');
                int n = arrf.length;
                for (int i = 0; i < n; ++i) {
                    this.cb.setLiteral(arrf[i]);
                    this.cb.setLiteral(' ');
                }
                this.cb.setLiteral(']');
                this.cb.setLiteral(basicStroke.getDashPhase());
                this.cb.setLiteral(" d\n");
            }
        }
    }

    public void setStroke(Stroke stroke) {
        this.originalStroke = stroke;
        this.stroke = this.transformStroke(stroke);
    }

    public void setRenderingHint(RenderingHints.Key key, Object object) {
        if (object != null) {
            this.rhints.put(key, object);
        } else if (key instanceof HyperLinkKey) {
            this.rhints.put(key, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
        } else {
            this.rhints.remove(key);
        }
    }

    public Object getRenderingHint(RenderingHints.Key key) {
        return this.rhints.get(key);
    }

    public void setRenderingHints(Map map) {
        this.rhints.clear();
        this.rhints.putAll(map);
    }

    public void addRenderingHints(Map map) {
        this.rhints.putAll(map);
    }

    public RenderingHints getRenderingHints() {
        return this.rhints;
    }

    public void translate(int n, int n2) {
        this.translate((double)n, (double)n2);
    }

    public void translate(double d, double d2) {
        this.transform.translate(d, d2);
    }

    public void rotate(double d) {
        this.transform.rotate(d);
    }

    public void rotate(double d, double d2, double d3) {
        this.transform.rotate(d, d2, d3);
    }

    public void scale(double d, double d2) {
        this.transform.scale(d, d2);
        this.stroke = this.transformStroke(this.originalStroke);
    }

    public void shear(double d, double d2) {
        this.transform.shear(d, d2);
    }

    public void transform(AffineTransform affineTransform) {
        this.transform.concatenate(affineTransform);
        this.stroke = this.transformStroke(this.originalStroke);
    }

    public void setTransform(AffineTransform affineTransform) {
        this.transform = new AffineTransform(affineTransform);
        this.stroke = this.transformStroke(this.originalStroke);
    }

    public AffineTransform getTransform() {
        return new AffineTransform(this.transform);
    }

    public Paint getPaint() {
        if (this.realPaint != null) {
            return this.realPaint;
        }
        return this.paint;
    }

    public Composite getComposite() {
        return this.composite;
    }

    public void setBackground(Color color) {
        this.background = color;
    }

    public Color getBackground() {
        return this.background;
    }

    public Stroke getStroke() {
        return this.originalStroke;
    }

    public FontRenderContext getFontRenderContext() {
        boolean bl = RenderingHints.VALUE_TEXT_ANTIALIAS_ON.equals(this.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
        boolean bl2 = RenderingHints.VALUE_FRACTIONALMETRICS_ON.equals(this.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
        return new FontRenderContext(new AffineTransform(), bl, bl2);
    }

    public Graphics create() {
        PdfGraphics2D pdfGraphics2D = new PdfGraphics2D();
        pdfGraphics2D.onlyShapes = this.onlyShapes;
        pdfGraphics2D.transform = new AffineTransform(this.transform);
        pdfGraphics2D.baseFonts = this.baseFonts;
        pdfGraphics2D.fontMapper = this.fontMapper;
        pdfGraphics2D.paint = this.paint;
        pdfGraphics2D.fillGState = this.fillGState;
        pdfGraphics2D.strokeGState = this.strokeGState;
        pdfGraphics2D.background = this.background;
        pdfGraphics2D.mediaTracker = this.mediaTracker;
        pdfGraphics2D.convertImagesToJPEG = this.convertImagesToJPEG;
        pdfGraphics2D.jpegQuality = this.jpegQuality;
        pdfGraphics2D.setFont(this.font);
        pdfGraphics2D.cb = this.cb.getDuplicate();
        pdfGraphics2D.cb.saveState();
        pdfGraphics2D.width = this.width;
        pdfGraphics2D.height = this.height;
        pdfGraphics2D.followPath(new Area(new Rectangle2D.Float(0.0f, 0.0f, this.width, this.height)), 3);
        if (this.clip != null) {
            pdfGraphics2D.clip = new Area(this.clip);
        }
        pdfGraphics2D.composite = this.composite;
        pdfGraphics2D.stroke = this.stroke;
        pdfGraphics2D.originalStroke = this.originalStroke;
        pdfGraphics2D.oldStroke = pdfGraphics2D.strokeOne = (BasicStroke)pdfGraphics2D.transformStroke(pdfGraphics2D.strokeOne);
        pdfGraphics2D.setStrokeDiff(pdfGraphics2D.oldStroke, null);
        pdfGraphics2D.cb.saveState();
        if (pdfGraphics2D.clip != null) {
            pdfGraphics2D.followPath(pdfGraphics2D.clip, 3);
        }
        pdfGraphics2D.kid = true;
        if (this.kids == null) {
            this.kids = new ArrayList();
        }
        this.kids.add(new Integer(this.cb.getInternalBuffer().size()));
        this.kids.add(pdfGraphics2D);
        return pdfGraphics2D;
    }

    public PdfContentByte getContent() {
        return this.cb;
    }

    public Color getColor() {
        if (this.paint instanceof Color) {
            return (Color)this.paint;
        }
        return Color.black;
    }

    public void setColor(Color color) {
        this.setPaint(color);
    }

    public void setPaintMode() {
    }

    public void setXORMode(Color color) {
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        if (font == null) {
            return;
        }
        if (this.onlyShapes) {
            this.font = font;
            return;
        }
        if (font == this.font) {
            return;
        }
        this.font = font;
        this.fontSize = font.getSize2D();
        this.baseFont = this.getCachedBaseFont(font);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BaseFont getCachedBaseFont(Font font) {
        HashMap hashMap = this.baseFonts;
        synchronized (hashMap) {
            BaseFont baseFont = (BaseFont)this.baseFonts.get(font.getFontName());
            if (baseFont == null) {
                baseFont = this.fontMapper.awtToPdf(font);
                this.baseFonts.put(font.getFontName(), baseFont);
            }
            return baseFont;
        }
    }

    public FontMetrics getFontMetrics(Font font) {
        return this.dg2.getFontMetrics(font);
    }

    public Rectangle getClipBounds() {
        if (this.clip == null) {
            return null;
        }
        return this.getClip().getBounds();
    }

    public void clipRect(int n, int n2, int n3, int n4) {
        Rectangle2D.Double double_ = new Rectangle2D.Double(n, n2, n3, n4);
        this.clip(double_);
    }

    public void setClip(int n, int n2, int n3, int n4) {
        Rectangle2D.Double double_ = new Rectangle2D.Double(n, n2, n3, n4);
        this.setClip(double_);
    }

    public void clip(Shape shape) {
        if (shape == null) {
            this.setClip(null);
            return;
        }
        shape = this.transform.createTransformedShape(shape);
        if (this.clip == null) {
            this.clip = new Area(shape);
        } else {
            this.clip.intersect(new Area(shape));
        }
        this.followPath(shape, 3);
    }

    public Shape getClip() {
        try {
            return this.transform.createInverse().createTransformedShape(this.clip);
        }
        catch (NoninvertibleTransformException var1_1) {
            return null;
        }
    }

    public void setClip(Shape shape) {
        this.cb.restoreState();
        this.cb.saveState();
        if (shape != null) {
            shape = this.transform.createTransformedShape(shape);
        }
        if (shape == null) {
            this.clip = null;
        } else {
            this.clip = new Area(shape);
            this.followPath(shape, 3);
        }
        this.paintStroke = null;
        this.paintFill = null;
        this.currentStrokeGState = 255;
        this.currentFillGState = 255;
        this.oldStroke = this.strokeOne;
    }

    public void copyArea(int n, int n2, int n3, int n4, int n5, int n6) {
    }

    public void drawLine(int n, int n2, int n3, int n4) {
        Line2D.Double double_ = new Line2D.Double(n, n2, n3, n4);
        this.draw(double_);
    }

    public void drawRect(int n, int n2, int n3, int n4) {
        this.draw(new Rectangle(n, n2, n3, n4));
    }

    public void fillRect(int n, int n2, int n3, int n4) {
        this.fill(new Rectangle(n, n2, n3, n4));
    }

    public void clearRect(int n, int n2, int n3, int n4) {
        Paint paint = this.paint;
        this.setPaint(this.background);
        this.fillRect(n, n2, n3, n4);
        this.setPaint(paint);
    }

    public void drawRoundRect(int n, int n2, int n3, int n4, int n5, int n6) {
        RoundRectangle2D.Double double_ = new RoundRectangle2D.Double(n, n2, n3, n4, n5, n6);
        this.draw(double_);
    }

    public void fillRoundRect(int n, int n2, int n3, int n4, int n5, int n6) {
        RoundRectangle2D.Double double_ = new RoundRectangle2D.Double(n, n2, n3, n4, n5, n6);
        this.fill(double_);
    }

    public void drawOval(int n, int n2, int n3, int n4) {
        Ellipse2D.Float float_ = new Ellipse2D.Float(n, n2, n3, n4);
        this.draw(float_);
    }

    public void fillOval(int n, int n2, int n3, int n4) {
        Ellipse2D.Float float_ = new Ellipse2D.Float(n, n2, n3, n4);
        this.fill(float_);
    }

    public void drawArc(int n, int n2, int n3, int n4, int n5, int n6) {
        Arc2D.Double double_ = new Arc2D.Double(n, n2, n3, n4, n5, n6, 0);
        this.draw(double_);
    }

    public void fillArc(int n, int n2, int n3, int n4, int n5, int n6) {
        Arc2D.Double double_ = new Arc2D.Double(n, n2, n3, n4, n5, n6, 2);
        this.fill(double_);
    }

    public void drawPolyline(int[] arrn, int[] arrn2, int n) {
        PolylineShape polylineShape = new PolylineShape(arrn, arrn2, n);
        this.draw(polylineShape);
    }

    public void drawPolygon(int[] arrn, int[] arrn2, int n) {
        Polygon polygon = new Polygon(arrn, arrn2, n);
        this.draw(polygon);
    }

    public void fillPolygon(int[] arrn, int[] arrn2, int n) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < n; ++i) {
            polygon.addPoint(arrn[i], arrn2[i]);
        }
        this.fill(polygon);
    }

    public boolean drawImage(java.awt.Image image, int n, int n2, ImageObserver imageObserver) {
        return this.drawImage(image, n, n2, null, imageObserver);
    }

    public boolean drawImage(java.awt.Image image, int n, int n2, int n3, int n4, ImageObserver imageObserver) {
        return this.drawImage(image, n, n2, n3, n4, null, imageObserver);
    }

    public boolean drawImage(java.awt.Image image, int n, int n2, Color color, ImageObserver imageObserver) {
        this.waitForImage(image);
        return this.drawImage(image, n, n2, image.getWidth(imageObserver), image.getHeight(imageObserver), color, imageObserver);
    }

    public boolean drawImage(java.awt.Image image, int n, int n2, int n3, int n4, Color color, ImageObserver imageObserver) {
        this.waitForImage(image);
        double d = (double)n3 / (double)image.getWidth(imageObserver);
        double d2 = (double)n4 / (double)image.getHeight(imageObserver);
        AffineTransform affineTransform = AffineTransform.getTranslateInstance(n, n2);
        affineTransform.scale(d, d2);
        return this.drawImage(image, null, affineTransform, color, imageObserver);
    }

    public boolean drawImage(java.awt.Image image, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, ImageObserver imageObserver) {
        return this.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, null, imageObserver);
    }

    public boolean drawImage(java.awt.Image image, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, Color color, ImageObserver imageObserver) {
        this.waitForImage(image);
        double d = (double)n3 - (double)n;
        double d2 = (double)n4 - (double)n2;
        double d3 = (double)n7 - (double)n5;
        double d4 = (double)n8 - (double)n6;
        if (d == 0.0 || d2 == 0.0 || d3 == 0.0 || d4 == 0.0) {
            return true;
        }
        double d5 = d / d3;
        double d6 = d2 / d4;
        double d7 = (double)n5 * d5;
        double d8 = (double)n6 * d6;
        AffineTransform affineTransform = AffineTransform.getTranslateInstance((double)n - d7, (double)n2 - d8);
        affineTransform.scale(d5, d6);
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(imageObserver), image.getHeight(imageObserver), 12);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.fillRect(n5, n6, (int)d3, (int)d4);
        this.drawImage(image, bufferedImage, affineTransform, null, imageObserver);
        graphics.dispose();
        return true;
    }

    public void dispose() {
        if (this.kid) {
            return;
        }
        if (!this.disposeCalled) {
            this.disposeCalled = true;
            this.cb.restoreState();
            this.cb.restoreState();
            this.dg2.dispose();
            this.dg2 = null;
            if (this.kids != null) {
                ByteBuffer byteBuffer = new ByteBuffer();
                this.internalDispose(byteBuffer);
                ByteBuffer byteBuffer2 = this.cb.getInternalBuffer();
                byteBuffer2.reset();
                byteBuffer2.append(byteBuffer);
            }
        }
    }

    private void internalDispose(ByteBuffer byteBuffer) {
        int n = 0;
        int n2 = 0;
        ByteBuffer byteBuffer2 = this.cb.getInternalBuffer();
        if (this.kids != null) {
            for (int i = 0; i < this.kids.size(); i += 2) {
                n2 = (Integer)this.kids.get(i);
                PdfGraphics2D pdfGraphics2D = (PdfGraphics2D)this.kids.get(i + 1);
                pdfGraphics2D.cb.restoreState();
                pdfGraphics2D.cb.restoreState();
                byteBuffer.append(byteBuffer2.getBuffer(), n, n2 - n);
                pdfGraphics2D.dg2.dispose();
                pdfGraphics2D.dg2 = null;
                pdfGraphics2D.internalDispose(byteBuffer);
                n = n2;
            }
        }
        byteBuffer.append(byteBuffer2.getBuffer(), n, byteBuffer2.size() - n);
    }

    private void followPath(Shape shape, int n) {
        if (shape == null) {
            return;
        }
        if (n == 2 && !(this.stroke instanceof BasicStroke)) {
            shape = this.stroke.createStrokedShape(shape);
            this.followPath(shape, 1);
            return;
        }
        if (n == 2) {
            this.setStrokeDiff(this.stroke, this.oldStroke);
            this.oldStroke = this.stroke;
            this.setStrokePaint();
        } else if (n == 1) {
            this.setFillPaint();
        }
        int n2 = 0;
        PathIterator pathIterator = n == 3 ? shape.getPathIterator(IDENTITY) : shape.getPathIterator(this.transform);
        float[] arrf = new float[6];
        while (!pathIterator.isDone()) {
            ++n2;
            int n3 = pathIterator.currentSegment(arrf);
            this.normalizeY(arrf);
            switch (n3) {
                case 4: {
                    this.cb.closePath();
                    break;
                }
                case 3: {
                    this.cb.curveTo(arrf[0], arrf[1], arrf[2], arrf[3], arrf[4], arrf[5]);
                    break;
                }
                case 1: {
                    this.cb.lineTo(arrf[0], arrf[1]);
                    break;
                }
                case 0: {
                    this.cb.moveTo(arrf[0], arrf[1]);
                    break;
                }
                case 2: {
                    this.cb.curveTo(arrf[0], arrf[1], arrf[2], arrf[3]);
                }
            }
            pathIterator.next();
        }
        switch (n) {
            case 1: {
                if (n2 <= 0) break;
                if (pathIterator.getWindingRule() == 0) {
                    this.cb.eoFill();
                    break;
                }
                this.cb.fill();
                break;
            }
            case 2: {
                if (n2 <= 0) break;
                this.cb.stroke();
                break;
            }
            default: {
                if (n2 == 0) {
                    this.cb.rectangle(0.0f, 0.0f, 0.0f, 0.0f);
                }
                if (pathIterator.getWindingRule() == 0) {
                    this.cb.eoClip();
                } else {
                    this.cb.clip();
                }
                this.cb.newPath();
            }
        }
    }

    private float normalizeY(float f) {
        return this.height - f;
    }

    private void normalizeY(float[] arrf) {
        arrf[1] = this.normalizeY(arrf[1]);
        arrf[3] = this.normalizeY(arrf[3]);
        arrf[5] = this.normalizeY(arrf[5]);
    }

    private AffineTransform normalizeMatrix() {
        double[] arrd = new double[6];
        AffineTransform affineTransform = AffineTransform.getTranslateInstance(0.0, 0.0);
        affineTransform.getMatrix(arrd);
        arrd[3] = -1.0;
        arrd[5] = this.height;
        affineTransform = new AffineTransform(arrd);
        affineTransform.concatenate(this.transform);
        return affineTransform;
    }

    private boolean drawImage(java.awt.Image image, java.awt.Image image2, AffineTransform affineTransform, Color color, ImageObserver imageObserver) {
        Object object;
        affineTransform = affineTransform == null ? new AffineTransform() : new AffineTransform(affineTransform);
        affineTransform.translate(0.0, image.getHeight(imageObserver));
        affineTransform.scale(image.getWidth(imageObserver), image.getHeight(imageObserver));
        AffineTransform affineTransform2 = this.normalizeMatrix();
        AffineTransform affineTransform3 = AffineTransform.getScaleInstance(1.0, -1.0);
        affineTransform2.concatenate(affineTransform);
        affineTransform2.concatenate(affineTransform3);
        double[] arrd = new double[6];
        affineTransform2.getMatrix(arrd);
        if (this.currentFillGState != 255) {
            object = this.fillGState[255];
            if (object == null) {
                object = new PdfGState();
                object.setFillOpacity(1.0f);
                this.fillGState[255] = object;
            }
            this.cb.setGState((PdfGState)object);
        }
        try {
            Object object2;
            Object object3;
            object = null;
            if (!this.convertImagesToJPEG) {
                object = Image.getInstance(image, color);
            } else {
                object2 = new BufferedImage(image.getWidth(null), image.getHeight(null), 1);
                object3 = object2.createGraphics();
                object3.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
                object3.dispose();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                JPEGImageWriteParam jPEGImageWriteParam = new JPEGImageWriteParam(Locale.getDefault());
                jPEGImageWriteParam.setCompressionMode(2);
                jPEGImageWriteParam.setCompressionQuality(this.jpegQuality);
                ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
                imageWriter.setOutput(imageOutputStream);
                imageWriter.write(null, new IIOImage((RenderedImage)object2, null, null), jPEGImageWriteParam);
                imageWriter.dispose();
                imageOutputStream.close();
                object2.flush();
                object2 = null;
                object = Image.getInstance(byteArrayOutputStream.toByteArray());
            }
            if (image2 != null) {
                object2 = Image.getInstance(image2, null, true);
                object2.makeMask();
                object2.setInverted(true);
                object.setImageMask((Image)object2);
            }
            this.cb.addImage((Image)object, (float)arrd[0], (float)arrd[1], (float)arrd[2], (float)arrd[3], (float)arrd[4], (float)arrd[5]);
            object2 = this.getRenderingHint(HyperLinkKey.KEY_INSTANCE);
            if (object2 != null && !object2.equals(HyperLinkKey.VALUE_HYPERLINKKEY_OFF)) {
                object3 = new PdfAction(object2.toString());
                this.cb.setAction((PdfAction)object3, (float)arrd[4], (float)arrd[5], (float)(arrd[0] + arrd[4]), (float)(arrd[3] + arrd[5]));
            }
        }
        catch (Exception var9_10) {
            throw new IllegalArgumentException();
        }
        if (this.currentFillGState != 255) {
            object = this.fillGState[this.currentFillGState];
            this.cb.setGState((PdfGState)object);
        }
        return true;
    }

    private boolean checkNewPaint(Paint paint) {
        if (this.paint == paint) {
            return false;
        }
        return !(this.paint instanceof Color) || !this.paint.equals(paint);
    }

    private void setFillPaint() {
        if (this.checkNewPaint(this.paintFill)) {
            this.paintFill = this.paint;
            this.setPaint(false, 0.0, 0.0, true);
        }
    }

    private void setStrokePaint() {
        if (this.checkNewPaint(this.paintStroke)) {
            this.paintStroke = this.paint;
            this.setPaint(false, 0.0, 0.0, false);
        }
    }

    private void setPaint(boolean bl, double d, double d2, boolean bl2) {
        block25 : {
            if (this.paint instanceof Color) {
                Color color = (Color)this.paint;
                int n = color.getAlpha();
                if (bl2) {
                    if (n != this.currentFillGState) {
                        this.currentFillGState = n;
                        PdfGState pdfGState = this.fillGState[n];
                        if (pdfGState == null) {
                            pdfGState = new PdfGState();
                            pdfGState.setFillOpacity((float)n / 255.0f);
                            this.fillGState[n] = pdfGState;
                        }
                        this.cb.setGState(pdfGState);
                    }
                    this.cb.setColorFill(color);
                } else {
                    if (n != this.currentStrokeGState) {
                        this.currentStrokeGState = n;
                        PdfGState pdfGState = this.strokeGState[n];
                        if (pdfGState == null) {
                            pdfGState = new PdfGState();
                            pdfGState.setStrokeOpacity((float)n / 255.0f);
                            this.strokeGState[n] = pdfGState;
                        }
                        this.cb.setGState(pdfGState);
                    }
                    this.cb.setColorStroke(color);
                }
            } else if (this.paint instanceof GradientPaint) {
                GradientPaint gradientPaint = (GradientPaint)this.paint;
                Point2D point2D = gradientPaint.getPoint1();
                this.transform.transform(point2D, point2D);
                Point2D point2D2 = gradientPaint.getPoint2();
                this.transform.transform(point2D2, point2D2);
                Color color = gradientPaint.getColor1();
                Color color2 = gradientPaint.getColor2();
                PdfShading pdfShading = PdfShading.simpleAxial(this.cb.getPdfWriter(), (float)point2D.getX(), this.normalizeY((float)point2D.getY()), (float)point2D2.getX(), this.normalizeY((float)point2D2.getY()), color, color2);
                PdfShadingPattern pdfShadingPattern = new PdfShadingPattern(pdfShading);
                if (bl2) {
                    this.cb.setShadingFill(pdfShadingPattern);
                } else {
                    this.cb.setShadingStroke(pdfShadingPattern);
                }
            } else if (this.paint instanceof TexturePaint) {
                try {
                    TexturePaint texturePaint = (TexturePaint)this.paint;
                    BufferedImage bufferedImage = texturePaint.getImage();
                    Rectangle2D rectangle2D = texturePaint.getAnchorRect();
                    Image image = Image.getInstance(bufferedImage, null);
                    PdfPatternPainter pdfPatternPainter = this.cb.createPattern(image.getWidth(), image.getHeight());
                    AffineTransform affineTransform = this.normalizeMatrix();
                    affineTransform.translate(rectangle2D.getX(), rectangle2D.getY());
                    affineTransform.scale(rectangle2D.getWidth() / (double)image.getWidth(), (- rectangle2D.getHeight()) / (double)image.getHeight());
                    double[] arrd = new double[6];
                    affineTransform.getMatrix(arrd);
                    pdfPatternPainter.setPatternMatrix((float)arrd[0], (float)arrd[1], (float)arrd[2], (float)arrd[3], (float)arrd[4], (float)arrd[5]);
                    image.setAbsolutePosition(0.0f, 0.0f);
                    pdfPatternPainter.addImage(image);
                    if (bl2) {
                        this.cb.setPatternFill(pdfPatternPainter);
                        break block25;
                    }
                    this.cb.setPatternStroke(pdfPatternPainter);
                }
                catch (Exception var7_8) {
                    if (bl2) {
                        this.cb.setColorFill(Color.gray);
                        break block25;
                    }
                    this.cb.setColorStroke(Color.gray);
                }
            } else {
                try {
                    Object object;
                    BufferedImage bufferedImage = null;
                    int n = 6;
                    if (this.paint.getTransparency() == 1) {
                        n = 5;
                    }
                    bufferedImage = new BufferedImage((int)this.width, (int)this.height, n);
                    Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
                    graphics2D.transform(this.transform);
                    AffineTransform affineTransform = this.transform.createInverse();
                    Shape shape = new Rectangle2D.Double(0.0, 0.0, bufferedImage.getWidth(), bufferedImage.getHeight());
                    shape = affineTransform.createTransformedShape(shape);
                    graphics2D.setPaint(this.paint);
                    graphics2D.fill(shape);
                    if (bl) {
                        object = new AffineTransform();
                        object.scale(1.0, -1.0);
                        object.translate(- d, - d2);
                        graphics2D.drawImage(bufferedImage, (AffineTransform)object, null);
                    }
                    graphics2D.dispose();
                    graphics2D = null;
                    object = Image.getInstance(bufferedImage, null);
                    PdfPatternPainter pdfPatternPainter = this.cb.createPattern(this.width, this.height);
                    object.setAbsolutePosition(0.0f, 0.0f);
                    pdfPatternPainter.addImage((Image)object);
                    if (bl2) {
                        this.cb.setPatternFill(pdfPatternPainter);
                    } else {
                        this.cb.setPatternStroke(pdfPatternPainter);
                    }
                }
                catch (Exception var7_10) {
                    if (bl2) {
                        this.cb.setColorFill(Color.gray);
                    }
                    this.cb.setColorStroke(Color.gray);
                }
            }
        }
    }

    private synchronized void waitForImage(java.awt.Image image) {
        if (this.mediaTracker == null) {
            this.mediaTracker = new MediaTracker(new FakeComponent());
        }
        this.mediaTracker.addImage(image, 0);
        try {
            this.mediaTracker.waitForID(0);
        }
        catch (InterruptedException var2_2) {
            // empty catch block
        }
        this.mediaTracker.removeImage(image);
    }

    public static class HyperLinkKey
    extends RenderingHints.Key {
        public static final HyperLinkKey KEY_INSTANCE = new HyperLinkKey(9999);
        public static final Object VALUE_HYPERLINKKEY_OFF = "0";

        protected HyperLinkKey(int n) {
            super(n);
        }

        public boolean isCompatibleValue(Object object) {
            return true;
        }

        public String toString() {
            return "HyperLinkKey";
        }
    }

    private static class FakeComponent
    extends Component {
        private static final long serialVersionUID = 6450197945596086638L;

        private FakeComponent() {
        }
    }

}

