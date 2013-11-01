package Analyzer.classification.bayes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class NBCxv {
	public static void main(String[] args){
			
		String filename = args.length >= 1 ? args[0] : "file/DreamD.json";
		
		ArrayList<String> allTexts = new ArrayList<String>();
	    
		try {
			//read the file, and train each document
			JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			JsonObject elem;
			while (parser.hasNext()) {
				elem = parser.next().getAsJsonObject();
	            allTexts.add(elem.get("text").getAsString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//do 5-fold cross validation 3 times
		Map<Integer, ArrayList<String>> buckets;
		int bucketIdx;
		NaiveBayesSentimentClassifier nbsc;
		for(int i = 0; i < 3; i++){
			
			//randomly split the texts into 5 buckets
			buckets = new HashMap<Integer, ArrayList<String>>();
			//initialize the 5 buckets
			for(int j = 0; j < 5; j++) buckets.put(j, new ArrayList<String>());
			for(String text : allTexts){
				bucketIdx = (int) (Math.random()*5);
				buckets.get(bucketIdx).add(text);
			}
			
			for(int j = 0; j < 5; j++){
				//use all but j as the training, use j as the test.
				nbsc = new NaiveBayesSentimentClassifier();
				for(int k = 0; k < 5; k++){
					if(k != j){
						nbsc.trainInstances(buckets.get(k));
					}
				}
				//test with bucket j
				
			}
		}
		
	}
}
