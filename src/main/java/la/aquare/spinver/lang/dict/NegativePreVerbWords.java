package la.aquare.spinver.lang.dict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Representa o dicionário contendo os termos pré-verbais de
 * negação que atuam sobre um verbo modificando seu sentido.
 */
public class NegativePreVerbWords {
	private static List<String> wordList;
	private static String wordListRegex;
	
	private NegativePreVerbWords() {
		//empty
	}
	
	public static List<String> initDict() {
		wordList = new ArrayList<String>();
		String words = "nei?(n|m),"
				+ "(s|c)equer?,"
				+ "nun?ca,"
				+ "jamai?s,"
				+ "n(a|ã|â|á|à)(o|um|un)(\\s+se?)?," //XXX:"não se compra/vende"
				+ "ñ,"
				+ "tamp(o|ô|õ|ó|ò)u?c(o|u)";
		wordListRegex = words.replaceAll(",", "|");
		wordList.addAll(Arrays.asList(words.split("\\s*,\\s*")));

//		XXX: Exemplo de como deve ficar a expressão regular 
//      que desconsidera os termos negativos antes do termo a ser negado:
//		(?<!\\b(nei?(n|m)|(s|c)equer?|nun?ca|jamai?s|n(a|ã)(o|um|un)(\\s+se)?|ñ|tamp(o|ô)u?c(o|u))\b)(\\s+|$)		
		return wordList;
	}
	
	public static String toRegex() {
		if (wordList == null) {
			initDict();
		}
		return wordListRegex;
	}
}
