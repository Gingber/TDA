/* TweetTracker. Copyright (c) Arizona Board of Regents on behalf of Arizona State University
 * @author shamanth
 */
package Crawler.restapi;

import Crawler.openauthentication.OAuthExample;
import Crawler.support.OAuthTokenSecret;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class RESTSearchExample
{
    BufferedWriter OutFileWriter;
    OAuthTokenSecret OAuthTokens;
    OAuthConsumer Consumer;
    String query = "#protest";
    String DEF_FILENAME = "file/searchresults.json";
    
    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8580));

    /**
     * Creates a OAuthConsumer with the current consumer & user access tokens and secrets
     * @return consumer
     */
    public OAuthConsumer GetConsumer()
    {
        OAuthConsumer consumer = new DefaultOAuthConsumer(utils.OAuthUtils.CONSUMER_KEY,utils.OAuthUtils.CONSUMER_SECRET);
        consumer.setTokenWithSecret(OAuthTokens.getAccessToken(), OAuthTokens.getAccessSecret());
        return consumer;
    }

    /**
     * Load the User Access Token, and the User Access Secret
     */
    public void LoadTwitterToken()
    {
        //Un-comment before release
//        OAuthExample oae = new OAuthExample();
//        OAuthTokens =  oae.GetUserAccessKeySecret();
        //Remove before release
        OAuthTokens = OAuthExample.DEBUGUserAccessSecret();
    }

    /**
     * Fetches tweets matching a query
     * @param query for which tweets need to be fetched
     * @return an array of status objects
     */
    public JSONArray GetSearchResults(String query)
    {
        try{
            //construct the request url
            String URL_PARAM_SEPERATOR = "&";
            StringBuilder url = new StringBuilder();
            url.append("https://api.twitter.com/1.1/search/tweets.json?q=");
            //query needs to be encoded
            url.append(URLEncoder.encode(query, "UTF-8"));
            /*url.append(URL_PARAM_SEPERATOR);
            url.append("page=1");*/
            url.append(URL_PARAM_SEPERATOR);
            url.append("count=100");
            URL navurl = new URL(url.toString());
            HttpURLConnection huc = (HttpURLConnection) navurl.openConnection(proxy);
            huc.setReadTimeout(5000);
            Consumer.sign(huc);
            huc.connect();
            if(huc.getResponseCode()==400||huc.getResponseCode()==404||huc.getResponseCode()==429)
            {
                System.out.println(huc.getResponseMessage());
                try {
                    huc.disconnect();
                    Thread.sleep(this.GetWaitTime("/friends/list"));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            if(huc.getResponseCode()==500||huc.getResponseCode()==502||huc.getResponseCode()==503)
            {
                System.out.println(huc.getResponseMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RESTSearchExample.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            BufferedReader bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getInputStream()));
            String temp;
            StringBuilder page = new StringBuilder();
            while( (temp = bRead.readLine())!=null)
            {
                page.append(temp);
            }
            JSONTokener jsonTokener = new JSONTokener(page.toString());
            try {
                JSONObject json = new JSONObject(jsonTokener);
                JSONArray results = json.getJSONArray("statuses");
                return results;
            } catch (JSONException ex) {
                Logger.getLogger(RESTSearchExample.class.getName()).log(Level.SEVERE, null, ex);
            }            
        } catch (OAuthCommunicationException ex) {
            Logger.getLogger(RESTSearchExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OAuthMessageSignerException ex) {
            Logger.getLogger(RESTSearchExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OAuthExpectationFailedException ex) {
            Logger.getLogger(RESTSearchExample.class.getName()).log(Level.SEVERE, null, ex);
        }catch(IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

     /**
     * Retrieves the rate limit status of the application
     * @return
     */
   public JSONObject GetRateLimitStatus()
   {
     try{
            URL url = new URL("https://api.twitter.com/1.1/application/rate_limit_status.json");
            HttpURLConnection huc = (HttpURLConnection) url.openConnection(proxy);
            huc.setReadTimeout(5000);
            OAuthConsumer consumer = new DefaultOAuthConsumer(utils.OAuthUtils.CONSUMER_KEY,utils.OAuthUtils.CONSUMER_SECRET);
            consumer.setTokenWithSecret(OAuthTokens.getAccessToken(), OAuthTokens.getAccessSecret());
            consumer.sign(huc);
            huc.connect();
            BufferedReader bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getContent()));
            StringBuffer page = new StringBuffer();
            String temp= "";
            while((temp = bRead.readLine())!=null)
            {
                page.append(temp);
            }
            bRead.close();
            return (new JSONObject(page.toString()));
        } catch (JSONException ex) {
            Logger.getLogger(RESTApiExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OAuthCommunicationException ex) {
            Logger.getLogger(RESTApiExample.class.getName()).log(Level.SEVERE, null, ex);
        }  catch (OAuthMessageSignerException ex) {
            Logger.getLogger(RESTApiExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OAuthExpectationFailedException ex) {
            Logger.getLogger(RESTApiExample.class.getName()).log(Level.SEVERE, null, ex);
        }catch(IOException ex)
        {
            Logger.getLogger(RESTApiExample.class.getName()).log(Level.SEVERE, null, ex);
        }
     return null;
   }

   /**
    * Initialize the file writer
    * @param path of the file
    * @param outFilename name of the file
    */
   public void InitializeWriters(String outFilename) {
        try {
            File fl = new File(outFilename);
            if(!fl.exists())
            {
                fl.createNewFile();
            }
            /**
             * Use UTF-8 encoding when saving files to avoid
             * losing Unicode characters in the data
             */
            OutFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFilename,true),"UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

   /**
    * Close the opened filewriter to save the data
    */
   public void CleanupAfterFinish()
   {
        try {
            OutFileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(RESTSearchExample.class.getName()).log(Level.SEVERE, null, ex);
        }
   }

   /**
    * Writes the retrieved data to the output file
    * @param data containing the retrived information in JSON
    * @param user name of the user currently being written
    */
    public void WriteToFile(JSONArray searchResults)
    {
        try
        {
            for(int i=0;i<searchResults.length();i++)
            {
                try {
                    OutFileWriter.write(searchResults.getJSONObject(i).toString());
                    OutFileWriter.newLine();
                } catch (JSONException ex) {
                    Logger.getLogger(RESTSearchExample.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Retrieves the wait time if the API Rate Limit has been hit
     * @param api the name of the API currently being used
     * @return the number of milliseconds to wait before initiating a new request
     */
    public long GetWaitTime(String api)
    {
        JSONObject jobj = this.GetRateLimitStatus();
        if(jobj!=null)
        {
            try {
                if(!jobj.isNull("resources"))
                {
                    JSONObject resourcesobj = jobj.getJSONObject("resources");
                    JSONObject statusobj = resourcesobj.getJSONObject("statuses");
                    JSONObject apilimit = statusobj.getJSONObject(api);
                    int numremhits = apilimit.getInt("remaining");
                    if(numremhits<=1)
                    {
                        long resettime = apilimit.getInt("reset");
                        resettime = resettime*1000; //convert to milliseconds
                        return resettime;
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * Creates an OR search query from the supplied terms
     * @param queryTerms
     * @return a String formatted as term1 OR term2
     */
    public String CreateORQuery(ArrayList<String> queryTerms)
    {
        String OR_Operator = " OR ";
        StringBuffer querystr = new StringBuffer();
        int count = 1;
        for(String term:queryTerms)
        {
            if(count==1)
            {
                querystr.append(term);
            }
            else
            {
                querystr.append(OR_Operator).append(term);
            }
        }
        return querystr.toString();
    }

    public static void main(String[] args)
    {
        RESTSearchExample rse = new RESTSearchExample();
        ArrayList<String> queryterms = new ArrayList<String>();        
        String outfilename = rse.DEF_FILENAME;
        if(args!=null)
        {
            if(args.length>0)
            {
                for(int i=0;i<args.length;i++)
                {
                    queryterms.add(args[i]);
                }
            }
            else
            {
                queryterms.add(rse.query);
            }
        }
        rse.LoadTwitterToken();
        rse.Consumer = rse.GetConsumer();
        System.out.println(rse.GetRateLimitStatus());
        rse.InitializeWriters(outfilename);
        JSONArray results = rse.GetSearchResults(rse.CreateORQuery(queryterms));
        if(results!=null)
        {
            rse.WriteToFile(results);
        }
        rse.CleanupAfterFinish();
    }
}
