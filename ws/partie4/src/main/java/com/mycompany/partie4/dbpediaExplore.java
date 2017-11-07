/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.partie4;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.net.*;
import java.util.Scanner;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;


public class dbpediaExplore {
    public static void main(String [] args) throws MalformedURLException, IOException, Exception {
        String s1 = "http://dbpedia.org/resource/Invictus_(film)";
        String s2 = "http://dbpedia.org/resource/Morgan_Freeman";
        String s3 = "http://dbpedia.org/resource/Quentin_Tarantino";
        String s = dataDetermination(s1);
        //System.out.println(s);
        //String s = parameterizedRequest(s1, "http://dbpedia.org/property/studio");
        System.out.println(getfilmLink(s));
    }
    
    public static String getRequestResult(String query) throws ParseException {
        String result = "";
        String r ="";
        try{
        ParameterizedSparqlString queryString = new ParameterizedSparqlString(query);
        QueryExecution exec = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", queryString.asQuery() );
        ResultSet results = ResultSetFactory.copyResults(exec.execSelect());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, results);
        //Turn result into a String
        result = new String(outputStream.toByteArray());
        } catch(QueryException q) {
            System.out.println("Erreur" + q);
        }
        
        //Select the value URI into the result json
        if(result.equals("")) {
            return result;
        } else {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(result);
        JSONArray array = ((JSONArray)((JSONObject)json.get("results")).get("bindings"));
        for(int i=0; i<array.size();i++){
            JSONObject jo = (JSONObject)((JSONObject)array.get(i));
            r = r + jo.toString() + "\n";
        }
        return r;  
        }
    }
    
    public static String dataDetermination(String uri) {
        try {
        if(isfilm(uri)) {
            return filmInfo(uri);
        } else if(isdirector(uri)) {
            return directorInfo(uri);
        } else if(isactor(uri)) {
            return actorInfo(uri);
        } else {
        return "No resource";
        }
        } catch(ParseException p){
        return "Erreur : " + p;
        }
    }
    
    public static String parameterizedRequest(String uri, String predicat){
        String result = "";
        String r = "";
        String query = "select * WHERE "
        + "{ "
        + "FILTER(?s IN (<" + uri + ">) && "
        + "(regex(?type, \"^" + predicat + "\"))). "
        + "?s ?type ?o . "
        + " } ";
        
        try{
        ParameterizedSparqlString queryString = new ParameterizedSparqlString(query);
        QueryExecution exec = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", queryString.asQuery() );
        ResultSet results = ResultSetFactory.copyResults(exec.execSelect());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, results);
        //Turn result into a String
        result = new String(outputStream.toByteArray());
        } catch(QueryException q) {
            System.out.println("Erreur" + q);
        }
        
        try {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(result);
        JSONArray array = ((JSONArray)((JSONObject)json.get("results")).get("bindings"));
        for(int i=0; i<array.size();i++){
            JSONObject jo = (JSONObject)((JSONObject)array.get(i));
            r = r + jo.toString() + "\n";
        }
        return r;  
        } catch (Exception e) {
            return "";
        }
    }
    
    public static boolean isactor(String uri) {
        String sparam = parameterizedRequest(uri, "http://purl.org/linguistics/gold/hypernym");
        
        try {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(sparam);
        JSONObject jo = (JSONObject)json.get("o");
        String s = jo.get("value").toString();
        String stest = "http://dbpedia.org/resource/Actor";
        return s.equals(stest);
        } catch(Exception e) {
            return false;
        }
    }
    
    public static boolean isdirector(String uri) {
        String sparam = parameterizedRequest(uri, "http://purl.org/linguistics/gold/hypernym");
        try {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(sparam);
        JSONObject jo = (JSONObject)json.get("o");
        String s = jo.get("value").toString();
        String stest = "http://dbpedia.org/resource/Filmmaker";
        return s.equals(stest);
        } catch(Exception e) {
            return false;
        }
    }
    //yyyyyyyy
    public static boolean isfilm(String uri) {
        String s1,s2,s3, s4, s5;
        s1 = parameterizedRequest(uri, "http://dbpedia.org/property/studio");
        s2 = parameterizedRequest(uri, "http://dbpedia.org/property/language");
        s3 = parameterizedRequest(uri, "http://dbpedia.org/ontology/starring");
        s4 = parameterizedRequest(uri, "http://dbpedia.org/ontology/director");
        s5 = parameterizedRequest(uri, "http://dbpedia.org/ontology/country");
        int isAFilm = 5;
        if(s1.equals("")){
            isAFilm--;
        } if(s2.equals("")){
            isAFilm--;
        } if(s3.equals("")){
            isAFilm--;
        } if(s4.equals("")){
            isAFilm--;
        } if(s5.equals("")){
            isAFilm--;
        } 
        /*
        if(s1.equals("") || s2.equals("") || s3.equals("") || s4.equals("") || s5.equals("")) {
            return false;
        } else {
            return true;
        }
        */
        if(isAFilm<=2){
            return false; 
        }else{
            return true;
        }
    }
    
    public static String filmInfo(String uri) throws ParseException {
        //SPARQL request
        String query = "select * WHERE "
        + "{ "
        + "FILTER(?s IN (<" + uri + ">) && "
        + "(regex(?type, \"^http://dbpedia.org/property/studio\") "
        + "|| regex(?type, \"^http://dbpedia.org/property/language\") "
        + "|| regex(?type, \"^http://dbpedia.org/ontology/starring\") "
        + "|| regex(?type, \"^http://dbpedia.org/ontology/director\") "
        + "|| regex(?type, \"^http://dbpedia.org/ontology/country\"))) . " 
        + "?s ?type ?o . "
        + " } ";
        return getRequestResult(query);  
    }

    public static String directorInfo(String uri) throws ParseException {
        //SPARQL request
        String query = "select * WHERE "
        + "{ "
        + "FILTER(?s IN (<" + uri + ">) && "
        + "(regex(?type, \"^http://dbpedia.org/resource/name\") "
        + "|| regex(?type, \"^http://dbpedia.org/ontology/birthDate\"))). "
        + "?s ?type ?o . "
        + " } ";
        
        return getRequestResult(query);  
    }    
 
    public static String actorInfo(String uri) throws ParseException {
        //SPARQL request
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> select * WHERE "
        + "{ "
        + "{ FILTER(?s IN (<" + uri + ">) && "
        + "(regex(?type, \"^http://dbpedia.org/property/name\") "
        + "|| regex(?type, \"^http://dbpedia.org/ontology/birthDate\"))). "
        + "?s ?type ?o . }"
        + "UNION { FILTER(?o IN (<http://dbpedia.org/ontology/starring>)) . ?s rdf:type <http://dbpedia.org/ontology/Film> . ?s ?o <" + uri + "> . }"
        + " } ";
        
        return getRequestResult(query);     
    }   
    
    //Get director of a movie
    public static String getDirectorFilm(String jsonstring) {
        String s="";
        try {
        JSONParser parser = new JSONParser();
        String[] lines = jsonstring.split(System.getProperty("line.separator"));
        Scanner scanner = new Scanner(jsonstring);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.contains("http:\\/\\/dbpedia.org\\/ontology\\/director")) {
                JSONObject json = (JSONObject)parser.parse(line);
                s = s + ((JSONObject)json.get("o")).get("value").toString() +"\n";
            }
        }              
        return s;
        } catch(Exception e) {
        return s;            
        }
    }
    
    //Get actors playing in a movie
    public static String getActorsFilm(String jsonstring) {
        String s="";
        try {
        JSONParser parser = new JSONParser();
        Scanner scanner = new Scanner(jsonstring);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.contains("http:\\/\\/dbpedia.org\\/ontology\\/starring")) {
                JSONObject json = (JSONObject)parser.parse(line);
                s = s + ((JSONObject)json.get("o")).get("value").toString() + "\n";
            }
        }              
        return s;
        } catch(Exception e) {
        return s;            
        }
    }
    
    //Get films where an actor played
    public static String getFilmsActor(String line) throws ParseException {
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> select * WHERE "
        + "{ FILTER(?o IN (<http://dbpedia.org/ontology/starring>)) . ?s rdf:type <http://dbpedia.org/ontology/Film> . ?s ?o <" + line + "> . }";      
        return getRequestResult(query);
        
    }
    
    //Get films directed by a director
    public static String getFilmsDirector(String line) throws ParseException {
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> select * WHERE "
        + "{ FILTER(?o IN (<http://dbpedia.org/ontology/director>)) . ?s rdf:type <http://dbpedia.org/ontology/Film> . ?s ?o <" + line + "> . }";      
        return getRequestResult(query);
        
    }
    
    public static String getfilmLink(String jsonstring) throws ParseException {
        String Director = getDirectorFilm(jsonstring);
        String Actor = getActorsFilm(jsonstring);
        String movies = "";
        Scanner scannerdirector = new Scanner(Director);  
        Scanner scanneractor = new Scanner(Actor);
        
        //Find all movies associated with the actors
        while(scanneractor.hasNextLine()) {
        String line = scanneractor.nextLine();
        JSONParser parser = new JSONParser();
        String result = getFilmsActor(line);
        Scanner s = new Scanner(result);
        while(s.hasNextLine()) {
        String l = s.nextLine();
        JSONObject json = (JSONObject)parser.parse(l);
        movies = movies + ((JSONObject)json.get("s")).get("value").toString() + "\n";
        }
        }
        
        //Find all movies associated with the director
        while(scannerdirector.hasNextLine()) {
        String line = scannerdirector.nextLine();
        JSONParser parser = new JSONParser();
        String result = getFilmsDirector(line);
        Scanner s = new Scanner(result);
        while(s.hasNextLine()) {
        String l = s.nextLine();
        JSONObject json = (JSONObject)parser.parse(l);
        movies = movies + ((JSONObject)json.get("s")).get("value").toString() + "\n";
        }
        }
        
        //return movies list, we have to order this list to only get 5 results (look movies appearing multiple time)
        return movies;
    } 
}
