package ru.xsrv.strings.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by calc on 10.01.15.
 */
public class Model {
    public static Lang defaultLang = new Lang("ru");
    private List<Lang> langs = new ArrayList<Lang>();
    private List<Node> nodes = new ArrayList<Node>();

    public Model(Lang defaultLang) {
        langs.add(defaultLang);
    }

    public Map<String, Node> getNodesMap(){
        Map<String, Node> map = new HashMap<String, Node>();

        for(Node n : nodes){
            map.put(n.getName(), n);
        }

        return map;
    }

    public void add(List<Node> nodes){
        this.nodes = Node.merge(this.nodes, nodes);
        // add other langs
        normalizeLangs();
    }

    public void normalizeLangs(){
        for(Node n : nodes){
            for(Lang l : langs){
                n.setValue(l, n.getValue(l));
            }
        }
    }

    public void add(Lang lang){
        langs.add(lang);
    }

    public List<Lang> getLangs() {
        return langs;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public String toString(Lang lang){
        String text = "<resources>\n";

        for(Node n: nodes){
            text+= n.toString(lang);
        }

        text += "</resources>\n";
        return text;
    }

    @Override
    public String toString() {
        return toString(Model.defaultLang);
    }
}
