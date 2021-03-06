/* TweetTracker. Copyright (c) Arizona Board of Regents on behalf of Arizona State University
 * @author shamanth
 */
package Visualization.trends;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SparkLineExample
{
    static final String DEF_INFILENAME = "file/BarackObama.json";
    public static final SimpleDateFormat SDM = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
   	public static final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.US);
   	public static final long HOUR = 8*3600*1000; // in milli-seconds.
       
    public JSONObject GenerateDataTrend(String inFilename, ArrayList<String> keywords) throws ParseException
    {
        BufferedReader br = null;
        JSONObject result = new JSONObject();
        HashMap<String,HashMap<String,Integer>> datecount = new HashMap<String,HashMap<String,Integer>>();
        try{
            br= new BufferedReader(new InputStreamReader(new FileInputStream(inFilename),"UTF-8"));
            String temp = "";
            while((temp = br.readLine())!=null)
            {
                try {
                    JSONObject jobj = new JSONObject(temp);
                    String text = jobj.getString("text").toLowerCase();
                    String orgstrdate = jobj.getString("created_at").toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
                    Date date = sdf.parse(orgstrdate); 
                    String strdate = SDM.format(new Date(date.getTime() - HOUR));
                    for(String word:keywords)
                    {
                        if(text.contains(word))
                        {
                            HashMap<String,Integer> wordcount = new HashMap<String,Integer>();
                            if(datecount.containsKey(strdate))
                            {
                                wordcount = datecount.get(strdate);
                            }
                            if(wordcount.containsKey(word))
                            {
                                wordcount.put(word, wordcount.get(word)+1);
                            }
                            else
                            {
                                wordcount.put(word, 1);
                            }
                            //update the wordcount for the specific date
                            datecount.put(strdate, wordcount);
                        }
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(SparkLineExample.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //sort the dates
            ArrayList<TCDateInfo> dinfos = new ArrayList<TCDateInfo>();
            Set<String> keys = datecount.keySet();
            for(String key:keys)
            {
                TCDateInfo dinfo = new TCDateInfo();
                try {
                    dinfo.d = SDM.parse(key);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                    continue;
                }
                dinfo.wordcount = datecount.get(key);
                dinfos.add(dinfo);
            }
            Collections.sort(dinfos);
            JSONArray[] tseriesvals = new JSONArray[keywords.size()];
            for(int i=0;i<tseriesvals.length;i++)
            {
                tseriesvals[i] = new JSONArray();
            }
            //prepare the output
            for(TCDateInfo date:dinfos)
            {                
                HashMap<String,Integer> wordcount = date.wordcount;
                int counter=0;
                for(String word:keywords)
                {
                    if(wordcount.containsKey(word))
                    {
                        tseriesvals[counter].put(wordcount.get(word));
                    }
                    else
                    {
                        tseriesvals[counter].put(0);
                    }
                    counter++;
                }
            }
            int counter=0;
            for(String word:keywords)
            {
                try {
                    result.put(word, tseriesvals[counter]);
                } catch (JSONException ex) {
                    Logger.getLogger(SparkLineExample.class.getName()).log(Level.SEVERE, null, ex);
                }
                counter++;
            }
        }catch(IOException ex)
        {
            ex.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(SparkLineExample.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public static void main(String[] args) throws ParseException
    {
        SparkLineExample sle = new SparkLineExample();
        ArrayList<String> words = new ArrayList<String>();
        String infilename = DEF_INFILENAME;
        if(args!=null)
        {
            if(args.length>=1&&!args[0].isEmpty())
            {
                File fl = new File(args[0]);
                if(fl.exists())
                {
                    infilename = args[0];
                }
            }
            for(int i=1;i<args.length;i++)
            {
                if(args[i]!=null&&!args[i].isEmpty())
                {
                    words.add(args[i]);
                }
            }
        }
        if(words.isEmpty())
        {
            words.add("#nypd");
            words.add("#ows");
        }
        System.out.println(sle.GenerateDataTrend(infilename,words));
    }

}
