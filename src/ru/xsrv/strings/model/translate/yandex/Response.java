package ru.xsrv.strings.model.translate.yandex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.List;

/**
 *
 * Created by calc on 11.01.15.
 */
public class Response {

    protected final static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private int code;
    private String lang;
    private List<String> text;

    public static <T extends Response> T fromJson(String json, Class<T> c){
        if(json == null) return null;

        //Log.d(Response.class.toString(), json);
        try {
            return gson.fromJson(json, c);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            //Log.w(Response.class.toString(), e.getMessage());
            //TODO запихать информацию о том, что пришли "левые данные"
            //json = "{\"error\":\"" + e.getMessage() + "\"}"; //NON-NLS
            //return gson.fromJson(json, c);
            return null;
        }
    }

    public List<String> getText() {
        return text;
    }
}
