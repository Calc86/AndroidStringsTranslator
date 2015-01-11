package ru.xsrv.strings.gui;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 *
 * Created by calc on 11.01.15.
 */
public class Export extends JFrame  {
    private static Logger Log = Logger.getLogger(Export.class.toString());

    private String xml;
    private JTextArea text;
    private JPanel contentPane;

    public Export(String xml) throws HeadlessException {
        this.xml = xml;
        init();
    }

    public Export(GraphicsConfiguration gc, String xml) {
        super(gc);
        this.xml = xml;
        init();
    }

    public Export(String title, String xml) throws HeadlessException {
        super(title);
        this.xml = xml;
        init();
    }

    public Export(String title, GraphicsConfiguration gc, String xml) {
        super(title, gc);
        this.xml = xml;
        init();
    }

    private void init(){
        setContentPane(contentPane);
        text.setText(xml);
        pack();
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
