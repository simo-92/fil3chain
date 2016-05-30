package it.scrs.miner.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;

/** classe di utilità per la manipolazione di stringhe in formato JSON */
public class JsonUtility {
    
    /**
     * crea il JSON che rappresenta un generico oggetto
     * @param obj oggetto da codificare in json
     * @return Stringa json che rappresenta l'oggetto in input
     */
    public static String toJson(Object obj){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(obj);
    }
    
    /**
     * crea un oggetto a partire da una stringa json
     * @param json la stringa da decodificare
     * @param type tipo dell'oggetto che sarà creato (quello codificato nella stringa in input)
     * @return 
     */
    public static Object fromJson(String json,Type type){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, type);    
    }
    
}
