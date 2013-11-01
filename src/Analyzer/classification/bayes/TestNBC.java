package Analyzer.classification.bayes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class TestNBC {
	public static void main(String[] args){
		
		String filename = args.length >= 1 ? args[0] : "file/DreamD.json";
		
		//initialize the sentiment classifier
		NaiveBayesSentimentClassifier nbsc = new NaiveBayesSentimentClassifier();
        
		try {
			//read the file, and train each document
			JsonStreamParser parser = new JsonStreamParser(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			JsonObject elem;
//			JsonElement element;
			String text;
			while (parser.hasNext()) {
	            elem = parser.next().getAsJsonObject();
				//element = parser.next();
	            text = elem.get("text").getAsString();
	            nbsc.trainInstance(text);
			}
			
			//print out the positive and negative dictionary
			System.out.println("=== Positive Dictionary ===");
			System.out.println(nbsc.printWordOccurs(0, 25));
			System.out.println("=== Negative Dictionary ===");
			System.out.println(nbsc.printWordOccurs(1, 25));
			
	        //now go through and classify each line as positive or negative
			parser = new JsonStreamParser(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			while (parser.hasNext()) {
	            elem = parser.next().getAsJsonObject();
	            text = elem.get("text").getAsString();
	            Classification c = nbsc.classify(text);
	            System.out.println(c + " -> " + text);
			}
			System.out.println(nbsc.classify("I love new york"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}	
