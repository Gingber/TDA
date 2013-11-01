package Analyzer.centrality.examples;

import Analyzer.util.TweetFileToGraph;

import java.io.File;
import java.text.ParseException;

import GraphElements.RetweetEdge;
import GraphElements.UserNode;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.graph.DirectedGraph;

public class BetweennessCentralityExample {
	public static void main(String[] args) throws ParseException{
		
		File tweetFile;
		
		if(args.length > 0){
			tweetFile = new File(args[0]);
		}
		else{
			tweetFile = new File("file/synthetic_retweet_network.json");
		}
		
		DirectedGraph<UserNode, RetweetEdge> retweetGraph = TweetFileToGraph.getRetweetNetwork(tweetFile);
		
		//calculate the betweenness centrality
		BetweennessCentrality<UserNode, RetweetEdge> betweenness = new BetweennessCentrality<UserNode, RetweetEdge>(retweetGraph);
		
		betweenness.evaluate();
		betweenness.printRankings(true, true);
		
	}
} 
