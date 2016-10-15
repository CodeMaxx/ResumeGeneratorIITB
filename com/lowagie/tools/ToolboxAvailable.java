package com.lowagie.tools;

import com.lowagie.text.Document;
import java.awt.GraphicsEnvironment;
import java.io.PrintStream;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;

public class ToolboxAvailable {
    public static void main(String[] arrstring) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println(Document.getVersion() + " Toolbox error: headless display");
        } else {
            try {
                Class class_ = Class.forName("com.lowagie.toolbox.Toolbox");
                Method method = class_.getMethod("main", arrstring.getClass());
                method.invoke(null, arrstring);
            }
            catch (Exception var1_2) {
                JOptionPane.showMessageDialog(null, "You need the iText-toolbox.jar with class com.lowagie.toolbox.Toolbox to use the iText Toolbox.", Document.getVersion() + " Toolbox error", 0);
            }
        }
    }
}

