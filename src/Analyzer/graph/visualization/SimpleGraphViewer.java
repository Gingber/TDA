/**
 * 
 */
package Analyzer.graph.visualization;

/**
 * @author hadoop
 *
 */
import Analyzer.util.TweetFileToGraph;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.text.ParseException;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import GraphElements.RetweetEdge;
import GraphElements.UserNode;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class SimpleGraphViewer {
	public static void main(String[] args) throws ParseException{
		
		File tweetFile;
		
		if(args.length > 0){
			tweetFile = new File(args[0]);
		}
		else{
			tweetFile = new File("file/searchresults.json");
		}
		
		DirectedGraph<UserNode, RetweetEdge> retweetGraph = TweetFileToGraph.getRetweetNetwork(tweetFile);
		
		/*
		 * Converts a node to its string representation
		 */
		Transformer<UserNode, String> stringer = new Transformer<UserNode, String>(){
			public String transform(UserNode n){
				return n.toString();
			}
		};
		
		/*
		 * Calculate the centrality
		 */
		//calculate the betweenness centrality
//		final InDegreeScorer<UserNode> centralityScore = new InDegreeScorer<UserNode>(retweetGraph);
//		final BetweennessCentrality<UserNode, RetweetEdge> centralityScore = new BetweennessCentrality<UserNode, RetweetEdge>(retweetGraph);
//		final PageRank<UserNode, RetweetEdge> centralityScore = new PageRank<UserNode, RetweetEdge>(retweetGraph, 0.85);
		final EigenvectorCentrality<UserNode, RetweetEdge> centralityScore = new EigenvectorCentrality<UserNode, RetweetEdge>(retweetGraph);
		//centralityScore.evaluate();
		
		double centralityMax = 0.0f;
		for(UserNode node : retweetGraph.getVertices()){
			centralityMax = Math.max(centralityMax, centralityScore.getVertexScore(node));
		}
		final double centralityMaxFinal = centralityMax;
		
		/*
		 * Sizes a node by some centrality measure
		 */
		Transformer<UserNode, Shape> shaper = new Transformer<UserNode, Shape>(){
			public Shape transform(UserNode n){
				System.out.println("User: " + n.getUsername() + " Cent: " + centralityScore.getVertexScore(n) + " Max: " + centralityMaxFinal);
				double radius = 50 * (centralityScore.getVertexScore(n)) / centralityMaxFinal;
				radius = Math.max(radius, 5.0f);
				float fRadius = (float) radius;
				return new Ellipse2D.Float(-fRadius/2, -fRadius/2, fRadius, fRadius);
			}
		};
		
		Layout<UserNode, RetweetEdge> layout = new KKLayout<UserNode, RetweetEdge>(retweetGraph);
		layout.setSize(new Dimension(800, 800));
		
		BasicVisualizationServer<UserNode, RetweetEdge> vv = new BasicVisualizationServer<UserNode, RetweetEdge>(layout);
		vv.setPreferredSize(new Dimension(1000, 800));
		vv.getRenderContext().setVertexLabelTransformer(stringer);
		vv.getRenderContext().setVertexShapeTransformer(shaper);
		
		JFrame jframe = new JFrame("Simple Graph View");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.getContentPane().add(vv);
		jframe.pack();
		jframe.setVisible(true);
	}
}

