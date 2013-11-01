package Analyzer.centrality.examples;

import Analyzer.util.TweetFileToGraph;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import utils.TxtWriter;
import GraphElements.RetweetEdge;
import GraphElements.UserNode;
import edu.uci.ics.jung.graph.DirectedGraph;

public class InDegreeCentralityExample {
	
	public static void main(String[] args) throws IOException, ParseException {
		
		File tweetFile;
		
		if(args.length > 0){
			tweetFile = new File(args[0]);
		}
		else{
			tweetFile = new File("file/DreamD.json");
		}
		
		DirectedGraph<UserNode, RetweetEdge> retweetGraph = TweetFileToGraph.getRetweetNetwork(tweetFile);
		
		//calculate the betweenness centrality
		System.out.println("Vertices in-degree count as follows:");
		for(UserNode node : retweetGraph.getVertices()){
			System.out.println(node + " - " + retweetGraph.getInEdges(node).size());
		}
		
		System.out.println("\nRetweet edge relationship as follows:");
		StringBuilder sb = new StringBuilder();
		for(RetweetEdge rtedge : retweetGraph.getEdges()) {
			/*System.out.println("(" + rtedge.getFrom().getUsername() + "," + rtedge.getFrom().getId() + ")" 
							+ "->"  
							+ "(" + rtedge.getTo().getUsername() + "," + rtedge.getTo().getId() + ")" );*/
			
			sb.append(rtedge.getFrom().getUsername());
			sb.append(",");
			sb.append(rtedge.getFrom().getId());
			sb.append(",");
			sb.append(rtedge.getFrom().getText());
			sb.append(",");
			sb.append(rtedge.getFrom().getCreated_at());
			sb.append(",");
			sb.append(rtedge.getTo().getUsername());
			sb.append(",");
			sb.append(rtedge.getTo().getId());
			sb.append(",");
			sb.append(rtedge.getTo().getText());
			sb.append(",");
			sb.append(rtedge.getTo().getCreated_at());
			sb.append("\n");
		}
		
		TxtWriter.saveToFile(sb.toString(), new File("file/rt_DreamD.txt"), "UTF-8");
		

	}
}
