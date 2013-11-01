package Analyzer.classification.bayes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class performs both the training and classification steps of a Naive Bayes Classifier.
 *
 */
public class NaiveBayesSentimentClassifier {
	//the possible sentiment labels
	private static final String[] SENTIMENT_LABELS = {"happy", "sad"};
	//the tokens to look for in labeling the sentiment.
	private static final String[] HAPPY_SMILEYS = {":)", ";)", ":D", ":-)", ":o)", ":-D"};
	private static final String[] SAD_SMILEYS = {":(", ":-(", ":'(", ":'-(", "D:"};
	//store these as a set for faster retrieval
	private static final Set<String> HAPPY_SMILEY_SET = new HashSet<String>(Arrays.asList(HAPPY_SMILEYS));
	private static final Set<String> SAD_SMILEY_SET = new HashSet<String>(Arrays.asList(SAD_SMILEYS));
	
	//counter for the number of times each word has been associated with each sentiment.
	private Map<String, Integer[]> sentOccurs;
	//counter for the number of times we've seen each sentiment.
	private Integer[] sentCount;
	
	public NaiveBayesSentimentClassifier(){
		//initialize the counters
		sentOccurs = new HashMap<String, Integer[]>();
		sentCount = new Integer[SENTIMENT_LABELS.length];
		for(int i = 0; i < SENTIMENT_LABELS.length; i++){
			sentCount[i] = 0;
		}
	}
	
	/**
	 * Tokenize a string. Turns string into list of words based on whitespace, then
	 * removes stopwords, punctuation, and reduces the word to its stem. 
	 * @param text
	 * The piece of text
	 * @return
	 * Each individual word.
	 */
	private List<String> getTokens(String text){
		StringTokenizer tokens = new StringTokenizer(text);
		ArrayList<String> words = new ArrayList<String>();
		
		String tmp;
		StringBuilder sb;
		while(tokens.hasMoreTokens()){
			sb = new StringBuilder();
			tmp = tokens.nextToken();
			tmp = tmp.toLowerCase();
			
			for(char ch : tmp.toCharArray()){
				if(Character.isLetter(ch)){
					sb.append(ch);
				}
			}
			tmp = sb.toString();
			if(tmp.length() > 0 && !StopwordsList.stopwordsSet.contains(tmp)){
				words.add(sb.toString());
			}
		}
		
		return words;
	}
	
	/**
	 * Checks if tweet has a "label" (emoticon). If so, stores the words in
	 * the prior.
	 * @param tweetText
	 * The text of the document to check.
	 */
	public void trainInstance(String tweetText){
		//see if the tweet is labeled (i.e. has a smiley)
		int tweetLabel = extractLabel(tweetText);
		List<String> tokens = getTokens(tweetText);
		if(tweetLabel != -1){
			//add these words to the classifier
			updateClassifier(tokens, tweetLabel);
		}
	}
	
	public String printWordOccurs(int sentIndex, int topN){
		StringBuilder sb = new StringBuilder();
			
		WordCountPair wpcset[] = new WordCountPair[sentOccurs.keySet().size()]; 
		
		String s;
		int t = 0;
		Iterator<String> sIter = sentOccurs.keySet().iterator();
//		int totalCount = 0;
//		while(sIter.hasNext()){
//			s = sIter.next();
//			totalCount += sentOccurs.get(s)[sentIndex];
//		}
		
		sIter = sentOccurs.keySet().iterator();
		while(sIter.hasNext()){
			s = sIter.next();
//			wpcset[t++] = new WordCountPair(s, sentOccurs.get(s)[sentIndex] * 1.0 / totalCount);
			wpcset[t++] = new WordCountPair(s, Math.sqrt(sentOccurs.get(s)[sentIndex] * 1.0 ));
		}
		
		// jdk 7的sort函数的实现变了,改一下系统设置，还是选择使用老版本的排序方法
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true"); 
		Arrays.sort(wpcset);
		
		double frac;
		for(int i = 0; (i < topN || topN <= 0) && i < wpcset.length; i++){
			s = wpcset[i].getWord();
			frac = wpcset[i].getCount();
			
			sb.append(s);
			sb.append(":");
			sb.append(frac);
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public void trainInstances(List<String> tweetTexts){
		for(String text : tweetTexts){
			trainInstance(text);
		}
	}
	
	/**
	 * Classify a tweet as happy or sad. This ignores the emoticon for demonstration purposes.
	 * @param tweetText
	 * The text of the tweet
	 * @return
	 * A Classification object that returns the sentiment of the tweet.
	 */
	public Classification classify(String tweetText){
		//stores the probability of each sentiment being the tweets true sentiment.
		double[] labelProbs = new double[SENTIMENT_LABELS.length];
		//tokenize the string
		List<String> tokens = getTokens(tweetText);		
		int maxLabelIdx = 0;
		for(int i = 0; i < labelProbs.length; i++){
			//calculate the probability that the tweet has that sentiment.
			labelProbs[i] = calcLabelProb(tokens, i);
			System.out.println(i + " -> " + labelProbs[i] );
			//keep track of the label probability
			maxLabelIdx = labelProbs[i] > labelProbs[maxLabelIdx] ? i : maxLabelIdx;
		}
		//calc the confidence
		double conf = labelProbs[maxLabelIdx];
		labelProbs[maxLabelIdx] = 0;
		conf -= sumVector(labelProbs);
		
		return new Classification(SENTIMENT_LABELS[maxLabelIdx], conf);
	}
	
	private int extractLabel(String tweetText){
		StringTokenizer tokens = new StringTokenizer(tweetText);
		while(tokens.hasMoreTokens()){
			String token = tokens.nextToken();
			if(HAPPY_SMILEY_SET.contains(token)){
				return 0;
			}
			else if(SAD_SMILEY_SET.contains(token)){
				return 1;
			}
		}
		return -1;
	}
	
	/**
	 * This updates the classifier's probabilites for each word
	 * with the new piece of text.
	 * @param tokens
	 * The tokens in the tweet.
	 * @param sentIndex
	 * The sentiment label.
	 */
	private void updateClassifier(List<String> tokens, int sentIndex){
		for(String token : tokens){
			if(sentOccurs.containsKey(token)){
				sentOccurs.get(token)[sentIndex] ++ ;
			}
			else{
				//make a new array and put it
				Integer[] newArray = {0, 0};
				newArray[sentIndex] ++;
				sentOccurs.put(token, newArray);
			}
		}
		//update the overall document count
		sentCount[sentIndex]++;
	}
	
	/**
	 * The probability of the tweet having a given label.
	 * @param tokens
	 * The tokens in the tweet.
	 * @param sentIndex
	 * The probability we are testing.
	 * @return
	 * The probability the tweet has the class label indicated by "sentIndex".
	 */
	private double calcLabelProb(List<String> tokens, int sentIndex){
		
		//calculate the class probabilities
		double[] pClass = new double[SENTIMENT_LABELS.length];
		int cSum = sumVector(sentCount);
		int totalWordCount = 0;
		
		for(int i = 0; i < sentCount.length; i++){
			pClass[i] = sentCount[i] * 1.0 / cSum; 
		}
		
		for(String word : sentOccurs.keySet()){
			Integer[] wordCt = sentOccurs.get(word);
			totalWordCount = sumVector(wordCt);
		}
		
		
		double p = 1.0;
		boolean foundOne = false;
		for(String token : tokens){
			if(sentOccurs.containsKey(token)){
				foundOne = true;
				Integer[] probs = sentOccurs.get(token);
				double pWordGivenClass = probs[sentIndex] / (double)(sumVector(probs)); 
				double pWord = sumVector(probs) / totalWordCount;
				p *= pWordGivenClass * pClass[sentIndex] / pWord;
			}
		}
		return foundOne ? p : 0.0;
	}
	
	/**
	 * Helper function to sum the values in a 1D array.
	 * @param vector
	 * The 1D array to sum.
	 * @return
	 * The sum.
	 */
	private double sumVector(double[] vector){
		double sum = 0.0;
		for(double d : vector) sum += d;
		return sum;
	}
	
	/**
	 * Helper function to sum the values in a 1D array.
	 * @param vector
	 * The 1D array to sum.
	 * @return
	 * The sum.
	 */
	private int sumVector(Integer[] vector){
		int sum = 0;
		for(int d : vector) sum += d;
		return sum;
	}
}
