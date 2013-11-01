/**
 * 
 */
package Analyzer.centrality.examples;

/**
 * @author hadoop
 *
 */
import Analyzer.util.EigenVectorScorer;
import Analyzer.util.TweetFileToGraph;

import java.io.File;
import java.text.ParseException;

import GraphElements.RetweetEdge;
import GraphElements.UserNode;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import edu.uci.ics.jung.graph.DirectedGraph;

public class EigenvectorCentralityExample {
	public static void main(String[] args) throws ParseException{
		
		File tweetFile;
		
		if(args.length > 0){
			tweetFile = new File(args[0]);
		}
		else{
			tweetFile = new File("file/synthetic_retweet_network.json");
		}
		
		DirectedGraph<UserNode, RetweetEdge> retweetGraph = TweetFileToGraph.getRetweetNetwork(tweetFile);
		
		EigenVectorScorer scorer = new EigenVectorScorer(retweetGraph);
		for(UserNode node : retweetGraph.getVertices()){
			System.out.println(node + " - " + scorer.getVertexScore(node));
		}
		
		EigenvectorCentrality<UserNode, RetweetEdge> eig = new EigenvectorCentrality<UserNode, RetweetEdge>(retweetGraph);
		eig.evaluate();
		
		for(UserNode node : retweetGraph.getVertices()){
			System.out.println(node + " - " + eig.getVertexScore(node));
		}
	}
}

