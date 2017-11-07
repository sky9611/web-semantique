/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WS;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;

/**
 *
 * @author JossTheBoss
 */
public class textExtractor {
    private static NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
                 "901b18f3-9e37-4153-b659-0833905301e9",
                 "qAbQygtbrJ1W");
                
    private static  EntitiesOptions entities = new EntitiesOptions.Builder().limit(1).sentiment(true).build();
    private static  ConceptsOptions concepts = new ConceptsOptions.Builder()
                .limit(5)
                .build();
    public static String ExtractTextUrl(String url) {
         
        Features features = new Features.Builder().concepts(concepts).build();
        //Features features1 = new Features.Builder().entities(entities).build();
        AnalyzeOptions parameters
                = new AnalyzeOptions.Builder()
                        .url(url)
                        .features(features)
                        .returnAnalyzedText(true)
                        .build();

        AnalysisResults results ;
        results= service.analyze(parameters).execute();
        System.out.println(results.getAnalyzedText());
        return results.getAnalyzedText();
        
    }

    public static void main(String[] args) throws Exception {
        
        
        String url = "https://en.wikipedia.org/wiki/Watson_(computer)";
        ExtractTextUrl(url);
      
    }
}
