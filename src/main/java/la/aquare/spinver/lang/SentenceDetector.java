package la.aquare.spinver.lang;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 * Implementa um separador de <a href="http://en.wikipedia.org/wiki/Sentence_%28linguistics%29">
 * frases (<i>sentences</i>)</a>, cadeia de palavras
 * que veicula um enunciado (afirmação, exclamação, indagação, comando, etc.).   
 *
 */
public class SentenceDetector {
	private static InputStream is = null;
	private static SentenceModel model = null;
	
	public static synchronized void start() throws IOException {
		is = new FileInputStream("lang/pt-sent.bin");
		model = new SentenceModel(is);		
	}
	
	public static String[] detect(String text) throws IOException {
		String[] segments = new String[]{""};
		if (model != null) {			
			SentenceDetectorME sdetector = new SentenceDetectorME(model);
			segments = sdetector.sentDetect(text == null?"":text);
		}		
		return segments;
	}
	
	public static void stop() throws IOException {
		if (is != null) {
			is.close();
		}
	}
}
