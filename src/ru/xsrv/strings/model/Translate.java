package ru.xsrv.strings.model;

import ru.xsrv.strings.model.translate.yandex.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 *
 * Created by calc on 11.01.15.
 */
public class Translate {
    //trnsl.1.1.20150111T155613Z.bac40d7d3aad7e2a.ee269e31163e1b3aec34bc3c2852d83ae5f7bf28
    //https://translate.yandex.net/api/v1.5/tr.json/translate?key=APIkey&lang=en-ru&text=To+be,+or+not+to+be%3F&text=That+is+the+question.
    //private final static String TRANSLATE_URL = "https://www.googleapis.com/language/translate/v2?key=vernal-aleph-822&q=%s&source=%s&target=%s";
    private final static String TRANSLATE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20150111T155613Z.bac40d7d3aad7e2a.ee269e31163e1b3aec34bc3c2852d83ae5f7bf28&lang=%s-%s&text=%s";
    /*private final static String Q_HOLDER = "[query]";
    private final static String S_HOLDER = "[source]";
    private final static String T_HOLDER = "[target]";*/

    public static String translate(String text, Lang from, Lang to){
        try {
            String u = String.format(TRANSLATE_URL, from.getName(), to.getName(), URLEncoder.encode(text, "UTF-8"));
            URL url = new URL(u);
            URLConnection connection = url.openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setDoOutput(false);
            InputStream in = connection.getInputStream();
            byte[] data = streamToByteArray(in);
            if(data == null) return text;
            String json = new String(data);
            Response r = Response.fromJson(json, Response.class);
            if(r == null) return text;

            if(r.getText().size() == 0) return text;
            String tr = r.getText().get(0);
            return tr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    private static byte[] streamToByteArray(InputStream stream) throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();

        int ch;

        while((ch = stream.read()) != -1){
            ba.write(ch);
        }


        return ba.toByteArray();
    }

}
