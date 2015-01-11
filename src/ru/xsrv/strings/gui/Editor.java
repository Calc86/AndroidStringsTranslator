package ru.xsrv.strings.gui;

import ru.xsrv.strings.android.xml.Strings;
import ru.xsrv.strings.model.Lang;
import ru.xsrv.strings.model.Model;
import ru.xsrv.strings.model.Node;
import ru.xsrv.strings.model.Translate;

import javax.lang.model.element.Name;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 *
 * Created by calc on 10.01.15.
 */
public class Editor extends JFrame {
    private static Logger Log = Logger.getLogger(Editor.class.toString());

    private JTextField textLangCode;
    private JComboBox<Lang> comboLanguages;
    private JButton buttonDeleteLang;
    private JButton buttonLoadLang;
    private JButton buttonAddLanguage;
    private JTree tree;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("resources");
    private JTextArea textArea;
    private JButton buttonSave;
    private JButton buttonNext;
    private JButton buttonPrev;
    private JButton buttonExport;
    private JPanel contentPane;
    private JButton buttonTranslate;

    private Model model = new Model(Model.defaultLang);
    private TreeNode selectedTreeNode = null;

    public Editor() throws HeadlessException {
        init();
    }

    public Editor(GraphicsConfiguration gc) {
        super(gc);
        init();
    }

    public Editor(String title) throws HeadlessException {
        super(title);
        init();
    }

    public Editor(String title, GraphicsConfiguration gc) {
        super(title, gc);
        init();
    }

    private void init(){
        Log.setUseParentHandlers(false);
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINER);
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                Date d = new Date();
                SimpleDateFormat date = new SimpleDateFormat("[yyyy-MM-dd hh:mm:ss] ");
                return date.format(d) + record.getLevel() + "("
                        + record.getSourceClassName() + " "
                        + record.getSourceMethodName() + "): "
                        + record.getMessage() + "\n";
            }
        });
        Log.addHandler(consoleHandler);

        Log.finest("finest");
        Log.finer("finer");
        Log.fine("fine");
        Log.info("info");
        Log.warning("warning");
        Log.log(Level.SEVERE, "serve");

        //systemTray();

        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
            Log.warning(e.getMessage());
        }
        catch (ClassNotFoundException e) {
            Log.warning(e.getMessage());
        }
        catch (InstantiationException e) {
            Log.warning(e.getMessage());
        }
        catch (IllegalAccessException e) {
            Log.warning(e.getMessage());
        }
        setContentPane(contentPane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        comboLanguages.addItem(Model.defaultLang);  //set default language
        buildTree();    //clear tree

        buttonAddLanguage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lang lang = new Lang(textLangCode.getText());
                model.add(lang);
                comboLanguages.addItem(lang);
                model.normalizeLangs(); //add new lang node
                buildTree();    //rebuild tree
            }
        });

        buttonDeleteLang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //todo delete lang
            }
        });

        buttonLoadLang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(Editor.this);
                if(returnVal != JFileChooser.APPROVE_OPTION) return;
                File file = fc.getSelectedFile();
                if(!file.exists()) return;
                Load(file);
            }
        });

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                JTree tree = (JTree) e.getSource();
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree
                        .getLastSelectedPathComponent();
                if(selectedNode != null && selectedNode.getUserObject() instanceof TreeNode){
                    TreeNode tn = (TreeNode) selectedNode.getUserObject();
                    selectedTreeNode = tn;
                    textArea.setText(tn.getText().trim());
                }
            }
        });

        buttonExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                export();
            }
        });

        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedTreeNode == null) return;

                String name = selectedTreeNode.getName();
                Node node = model.getNodesMap().get(name);
                if(node == null) return;
                Lang l = new Lang(selectedTreeNode.getLang());
                node.setValue(l, textArea.getText());

                Log.info("value saved");
                tree.requestFocus();
            }
        });

        buttonTranslate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = Translate.translate(textArea.getText(), Model.defaultLang, new Lang(selectedTreeNode.getLang()));
                textArea.setText(text);
            }
        });
    }

    private void export(){
        Lang l = model.getLangs().get(comboLanguages.getSelectedIndex());
        String xml = model.toString(l);
        Export e = new Export(xml);
    }

    private void Load(File file){
        //build tree from xml
        Lang l = model.getLangs().get(comboLanguages.getSelectedIndex());
        Log.info("Try to load file for lang " + l.getName());
        Strings ss = new Strings(file, l);
        model.add(ss.getNodes());   //поместить данные в модель

        buildTree();
        System.out.println(model);
    }

    private class TreeNode{
        private String lang;
        private Node node;
        //private String text;

        TreeNode(String lang, Node node) {
            this.lang = lang;
            this.node = node;
        }

        public String getLang() {
            return lang;
        }

        public String getName() {
            return node.getName();
        }

        public Node getNode() {
            return node;
        }

        public String getText() {
            return node.getValue(new Lang(lang));
        }

        @Override
        public String toString() {
            return lang + "-" + node.getName();
        }
    }

    private void buildTree(){
        buildTree2();
    }

    private void buildTree1(){
        root.removeAllChildren();
        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
        m.setRoot(root);

        for(Node n : model.getNodes()){
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(n.getName(), true);
            for(String lang: n.getValues().keySet()){

                DefaultMutableTreeNode langNode = new DefaultMutableTreeNode(
                        new TreeNode(lang, n),
                        false);
                node.add(langNode);
            }
            root.add(node);
        }
    }

    private void buildTree2(){
        root.removeAllChildren();
        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
        m.setRoot(root);

        for(Lang l : model.getLangs()){
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(l.getName(), true);
            for(Node n : model.getNodes()){
                DefaultMutableTreeNode nameNode = new DefaultMutableTreeNode(
                        new TreeNode(l.getName(), n),
                        false);
                node.add(nameNode);
            }
            root.add(node);
        }

        /*for(Node n : model.getNodes()){
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(n.getName(), true);
            for(String lang: n.getValues().keySet()){

                DefaultMutableTreeNode langNode = new DefaultMutableTreeNode(
                        new TreeNode(lang, n),
                        false);
                node.add(langNode);
            }
            root.add(node);
        }*/
    }

    public static void main(String[] args) {
        Editor dialog = new Editor();
        dialog.pack();
        dialog.setVisible(true);
        //System.exit(0);
    }
}
