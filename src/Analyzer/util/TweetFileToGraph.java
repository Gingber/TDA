package Analyzer.util;

import java.io.File;

import GraphElements.RetweetEdge;
import GraphElements.UserNode;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.netlib.util.intW;

/**
 *	Some basic functionality to convert files collected 
 *	in Chapter 2 to JUNG graphs.
 */
public class TweetFileToGraph {
	
	public static final SimpleDateFormat SDM = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.US);
	public static final long HOUR = 8*3600*1000; // in milli-seconds.
	
	public static DirectedGraph<UserNode, RetweetEdge> getRetweetNetwork(File tweetFile) throws ParseException {
		
		JSONObject tmp;
		
		TweetFileProcessor tfp = new TweetFileProcessor(tweetFile);
		DirectedSparseGraph<UserNode, RetweetEdge> dsg = new DirectedSparseGraph<UserNode, RetweetEdge>();
		int num = 0;
		
		while (tfp.hasNext()){
			tmp = tfp.next();        
			if(tmp==null)       
			{
                continue;
            }
			//get the author
			String user =null;
			String id = null;
			String text = null;
			String create_at = null;
			String retweet_date = null;
            try {
            	user = tmp.getJSONObject("user").getString("screen_name");
            	id = tmp.getString("id");
            	text = tmp.getString("text");
            	create_at = tmp.getString("created_at").toString();
                Date date = sdf.parse(create_at);
                retweet_date = SDM.format(new Date(date.getTime()));   
            } catch (JSONException ex) {
                Logger.getLogger(TweetFileToGraph.class.getName()).log(Level.SEVERE, null, ex);
            }
			if(user==null)
            {
                continue;
            }
			//get the retweeted user
			try{
				JSONObject retweet = tmp.getJSONObject("retweeted_status");
				String retweeted_user = retweet.getJSONObject("user").getString("screen_name");
				String retweeted_id = retweet.getString("id");
				String retweeted_text = retweet.getString("text");
				create_at = retweet.getString("created_at").toString();
                Date date = sdf.parse(create_at);
                String original_date = SDM.format(new Date(date.getTime()));
				
				//make an edge or increment the weight if it exists.
				UserNode toUser = new UserNode(retweeted_user, original_date, retweeted_id, retweeted_text);
				UserNode fromUser = new UserNode(user, retweet_date, id, text);
				
				dsg.addVertex(toUser);
				dsg.addVertex(fromUser);
			
				RetweetEdge edge = new RetweetEdge(toUser, fromUser); 
				
				if(dsg.containsEdge(edge)){
					dsg.findEdge(fromUser, toUser).incrementRTCount();
					//System.out.println(edge);
				}
				else{
					dsg.addEdge(edge, fromUser, toUser);
				}
				dsg.addEdge(edge, fromUser, toUser, EdgeType.DIRECTED);
			}
			catch(JSONException ex){
				//the tweet is not a retweet. this is not a problem.
			}
		}
		
		return dsg;
	}
}
