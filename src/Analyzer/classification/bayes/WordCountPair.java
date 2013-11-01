package Analyzer.classification.bayes;

public class WordCountPair implements Comparable<WordCountPair>{

	
	private String word;
	private double count;
	
	public WordCountPair(String word, double count){
		this.word = word;
		this.count = count;
	}
	
	public int compareTo(WordCountPair arg0) {
		return arg0.count - count < 0 ? -1 : 1;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public double getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
 
}
