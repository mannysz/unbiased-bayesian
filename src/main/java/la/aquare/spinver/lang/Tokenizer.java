package la.aquare.spinver.lang;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * Implementa um separador de <i>tokens</i> (cadeias de caracteres
 * diferentes de espaço, quebra de linha, tabulação ou similar).   
 *
 */
public class Tokenizer {
	private static InputStream is = null;
	private static TokenizerModel model = null;
	
	public static synchronized void start() throws IOException {
		is = new FileInputStream("lang/pt-token.bin");
		model = new TokenizerModel(is);
	}
	
	public static String[] tokenize(String sentence) throws IOException {
		String[] segments = new String[]{""};
		if (is != null) {			
			TokenizerME tokenizer = new TokenizerME(model);
			segments = tokenizer.tokenize(sentence == null?"":sentence);			
		}
		return segments;
	}

	public static void stop() throws IOException {
		if (is != null) {
			is.close();
		}
	}

}
