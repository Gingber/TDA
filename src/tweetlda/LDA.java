package tweetlda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.json.JSONObject;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

public class LDA {

	private static final String STOP_WORDS = "stopwords/stopwords.txt";
    private static final int ITERATIONS = 100;
    private static final int THREADS = 4;
    private static final int NUM_TOPICS = 25;
	private static final int NOM_WORDS_TO_ANALYZE = 25;

	public static void main(String[] args) throws Exception {
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		File stopwords = new File(STOP_WORDS);
		
		String inputFileName = args.length >= 1 ? args[0] : "file/DreamD.json";

		// Lowercase, tokenize, remove stopwords, stem, and convert to features
		pipeList.add((Pipe) new CharSequenceLowercase());
        pipeList.add((Pipe) new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        pipeList.add((Pipe) new TokenSequenceRemoveStopwords(stopwords, "UTF-8", false, false, false));
        pipeList.add((Pipe) new PorterStemmer());
        pipeList.add((Pipe) new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipeList));

        BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName), "UTF-8"));
        LinkedList<String> textList = new LinkedList<String>();
        String line;
        while((line = fileReader.readLine()) != null){
        	JSONObject elem = new JSONObject(line);
        	if(elem.has("text")){
        		textList.add(elem.getString("text"));
        	}
        }

        instances.addThruPipe(new StringArrayIterator(textList.toArray(new String[textList.size()])));

        ParallelTopicModel model = new ParallelTopicModel(NUM_TOPICS); 
        model.addInstances(instances);
        model.setNumThreads(THREADS);
        model.setNumIterations(ITERATIONS);
        model.estimate();

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();

        int topicIdx=0;
        StringBuilder sb;
        for (TreeSet<IDSorter> set : model.getSortedWords()) {
            sb = new StringBuilder().append(topicIdx);
            sb.append(" - ");
            int j = 0;
            double sum = 0.0;
            for (IDSorter s : set) {
            	sum += s.getWeight();
            }
            for (IDSorter s : set) {
                sb.append(dataAlphabet.lookupObject(s.getID())).append(":").append(s.getWeight() / sum).append(", ");
                if (++j >= NOM_WORDS_TO_ANALYZE) break;
            }
            System.out.println(sb.append("\n").toString());
            topicIdx++;
        }
	}
}
