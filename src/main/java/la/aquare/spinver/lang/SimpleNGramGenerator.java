package la.aquare.spinver.lang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleNGramGenerator {
	public static List<String> generate(String tokens, int n) {
		List<String> ngrams = null;
		
		if (n > 0) {
			String[] tokensArray = null;
			try {
				tokensArray = Tokenizer.tokenize(tokens);
			}
			catch (IOException ex) {
				//empty
			}
			if (tokensArray != null && tokensArray.length >= n) {
				ngrams = new ArrayList<String>();
				
				for (int i = 0; i < tokensArray.length - (n-1); i++) {
					String ngram = "";
					for (int j = i; j < (i+n); j++) {
						ngram = ngram + " " + tokensArray[j];
					}
					ngrams.add(ngram.substring(1));
				}
			}		
		}
		return ngrams;
	}
}
