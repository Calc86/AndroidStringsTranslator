package ru.xsrv.strings.model;

import java.util.*;

/**
 *
 * Created by calc on 10.01.15.
 */
public class Node {
    /**
     * Имя тега
     */
    private String name;
    private String tag;
    /**
     * Значения в разных языках
     */
    private Map<String, String> values = new HashMap<String, String>();
    /**
     * остальные параметры xml тега
     */
    private Map<String, String> other = new HashMap<String, String>();

    public Node(String name, String tag, Lang lang, String value, Map<String, String> other) {
        this.name = name;
        this.tag = tag;
        this.values.put(lang.getName(), value);
        if(other == null) return;
        for(String key : other.keySet()){
            this.other.put(key, other.get(key));
        }
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public Map<String, String> getOther() {
        return other;
    }

    public static List<Node> merge(List<Node> nCurrent, List<Node> nNew){
        Map<String, Node> mnc = new HashMap<String, Node>();
        for(Node n : nCurrent){
            mnc.put(n.getName(), n);
        }
        /*Map<String, Node> mnn = new HashMap<String, Node>(mnc);*/
        for(Node n : nNew){
            if(mnc.get(n.getName()) == null){
                mnc.put(n.getName(), n);    //перезаписываем и получаем большее множество
            }
            else{
                Node nn = mnc.get(n.getName());
                for(String lang : n.getValues().keySet()){
                    Lang l = new Lang(lang);
                    nn.setValue(l, n.getValue(l));  //дописываем язык
                }
            }
        }

        List<Node> nodes = new ArrayList<Node>(mnc.values());
        nodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return nodes;
    }

    public String getTag() {
        return tag;
    }

    public void setValue(Lang lang, String value){
        values.put(lang.getName(), value);
    }

    /**
     *
     * @param lang
     * @return value for lang or value for Model.defaultLang or ""
     */
    public String getValue(Lang lang){
        if(values.containsKey(lang.getName())){
            return values.get(lang.getName());
        }
        else{
            if(values.get(Model.defaultLang.getName()) != null)
                return values.get(Model.defaultLang.getName());
            return "";
        }
    }

    public String toString(Lang lang) {
        String text = "";
        text += "<";
        text += getTag();
        text += " ";
        text += "name=\"" + getName() + "\"";
        for(String key : other.keySet()){
            text+= " " + key + "=\"" + other.get(key) + "\"";
        }
        text += ">";
        text += getValue(lang).trim();
        text += "</" + getTag() + ">\n";

        return text;
    }
}
