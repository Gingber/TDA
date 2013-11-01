package Analyzer.classification.bayes;

public class Classification {
	private String label;
	private double confidence;
	
	public Classification(String label, double confidence){
		this.label = label;
		this.confidence = confidence;
	}
	
	public String getLabel() {
		return label;
	}
	public double getConfidence() {
		return confidence;
	}
	
	public String toString(){
		return "(" + label + ", " + confidence + ")";
	}
}
