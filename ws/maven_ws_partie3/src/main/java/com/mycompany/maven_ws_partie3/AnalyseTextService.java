/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.maven_ws_partie3;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dbpedia.spotlight.exceptions.AnnotationException;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class AnalyseTextService extends AnnotationClient{
    private final static String API_URL = "http://model.dbpedia-spotlight.org/";
    private static final double CONFIDENCE = 0.9;
    private static final int SUPPORT = 0;
    private static final String TYPE = "colour";
    private static final String FORMAT = "application/json";
   
    /**
     *
     * @param text
     * @return 
     * @throws AnnotationException
     */
    @Override
    public List<DBpediaResource> extract(Text text) throws AnnotationException {
        String spotlightResponse;
        try{
            GetMethod getMethod = new GetMethod(API_URL + "en/annotate/?" +
					"confidence=" + CONFIDENCE
					+ "&support=" + SUPPORT
                                        + "&types" + TYPE
                                        + "&text=" + URLEncoder.encode(text.text(), "utf-8")
                                        );
            getMethod.addRequestHeader(new Header("Accept", "application/json"));
            spotlightResponse = request(getMethod);
            //System.out.println(spotlightResponse);
            
        } catch (UnsupportedEncodingException e){
            throw new  AnnotationException("Could not encode text.",e);
        }
        assert spotlightResponse != null;
        
        JSONObject resultJSON = null;
        JSONArray entities = null;
        try{
            resultJSON = new JSONObject(spotlightResponse);
            entities = resultJSON.getJSONArray("Resources");
        } catch (JSONException e){
            throw new AnnotationException("Received invalid response from DBpedia Spotlight API.");
        }
        LinkedList<DBpediaResource> resources = new LinkedList<DBpediaResource>();
        for(int i = 0; i < entities.length(); i++){
            try{
                JSONObject entity = entities.getJSONObject(i);
                //System.out.println(entity.getString("@URI"));
                resources.add(new DBpediaResource(entity.getString("@URI"),Integer.parseInt(entity.getString("@support"))));
            }catch (JSONException e){
                System.out.println("error");
            }
        }
        //System.out.println(resources);
        return resources;
    }
    public static void main(String[] args) throws Exception{
        AnalyseTextService a = new AnalyseTextService();
        File input = new File("D:\\documents\\INSA\\4IF\\Web Sémantique\\in.txt");
        File output = new File("D:\\documents\\INSA\\4IF\\Web Sémantique\\out.txt");
        a.evaluate(input, output);

    }
}
