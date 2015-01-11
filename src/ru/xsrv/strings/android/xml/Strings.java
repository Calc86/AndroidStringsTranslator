package ru.xsrv.strings.android.xml;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import ru.xsrv.strings.model.*;
import sun.plugin2.message.GetAppletMessage;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Text;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xml parser of android strings.xml
 * Created by calc on 10.01.15.
 */
public class Strings {
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    private File file;
    private List<ru.xsrv.strings.model.Node> nodes = new ArrayList<ru.xsrv.strings.model.Node>();
    private Lang lang;

    public Strings(File file, Lang lang) {
        this.file = file;
        this.lang = lang;

        load();
    }

    private void load(){
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }
        Document doc;
        try {
            doc = db.parse(file);
        } catch (SAXException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        visit(doc, 0);
    }

    public void visit(Node node, int level) {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node childNode = list.item(i); // текущий нод
            process(childNode, level + 1); // обработка
            visit(childNode, level + 1); // рекурсия
        }
    }

    public void process(Node node, int level) {
        if(level == 1 && !node.getNodeName().equals("resources")){
            //todo throw wrong format
            return;
        }
        if(level == 1) return;  //next
        if(level > 2) return; //continue, нас интересует только первый уровень ресурсов, остальное остается как есть

        if(node.getTextContent().trim().equals("")) return; //пустые строки пропускаем
        if(node instanceof Comment) return; // комменты пропускаем

        for (int i = 0; i < level; i++) {
            System.out.print('\t');
        }

        System.out.print(node.getNodeName());
        if (node instanceof Element){
            Element e = (Element) node;
            // работаем как с элементом (у него есть атрибуты и схема)
            System.out.print("-" + e.getAttribute("name"));
            System.out.println(makeText(node));

            ru.xsrv.strings.model.Node n = new ru.xsrv.strings.model.Node(e.getAttribute("name"), e.getNodeName(), lang, makeText(node), getAttributes(e));
            nodes.add(n);
        }
        else{
            System.out.print("-" + node.getTextContent());
        }
        System.out.println();
    }

    private String makeText(Node node){
        String text = "";
        if(node.getNodeName().equals("string-array")){
            System.out.println("here");
        }

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node childNode = list.item(i); // текущий нод
            if (childNode instanceof Element){
                Element e = (Element) childNode;
                // работаем как с элементом (у него есть атрибуты и схема)
                //System.out.print("-" + e.getAttribute("name"));
                text += "<" + e.getNodeName() + ">" + e.getTextContent() + "</" + e.getNodeName() + ">";
            }
            else{
                text += childNode.getTextContent();
            }
        }
        return text;
    }

    private Map<String, String> getAttributes(Element element){
        //System.out.println("List attributes for node: " + element.getNodeName());
        Map<String, String> map = new HashMap<String, String>();
        // get a map containing the attributes of this node
        NamedNodeMap attributes = element.getAttributes();
        // get the number of nodes in this map
        int numAttrs = attributes.getLength();

        for (int i = 0; i < numAttrs; i++) {
            Attr attr = (Attr) attributes.item(i);
            String attrName = attr.getNodeName();
            if(attrName.equals("name")) continue;
            String attrValue = attr.getNodeValue();
            map.put(attrName, attrValue);
            //System.out.println("Found attribute: " + attrName + " with value: " + attrValue);
        }

        return map;
    }

    public List<ru.xsrv.strings.model.Node> getNodes() {
        return nodes;
    }
}
