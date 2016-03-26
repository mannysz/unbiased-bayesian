package la.aquare.spinver.lang.dict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Representa o dicion&aacute;rio de palavras permitidas (quando
 * existirem) que podem ocorrer imediatamente antes de um termo 
 * que nomeia um produto de interesse.
 */
public final class BeforeWordsAllowed {
	private static List<String> wordList;
//	private static String connectors = "e|ou";
	
	private BeforeWordsAllowed() {
		//empty
	}
	
	public static boolean contains(List<String> inputWordList) {
//		System.out.println("precedent = " + inputWordList);
		if (wordList == null) {
			wordList = initDict();
		}
		boolean found = !wordList.isEmpty();
		boolean previousAllowed = false;
		for (int i = 0; i < inputWordList.size(); i++) {
			String word = inputWordList.get(i);
			word = word.trim();
			boolean newFound = false;
			if (word.length() > 0) {
				for (String words : wordList) {
//					newFound = newFound | word.matches("(?iU)"+words);

					//se o item antecedente for uma palavra permitida no
					//dicionário BeforeWordsAllowed ou for uma palavra-chave
					//de um item (produto) no dicionário Itens antecedida por um
					//conector (vide método privado addConnectors(...) para uma lista
					//dos conectores reconhecidos)
//					newFound = newFound | 
//							(word.matches("(?iU)"+words) | Itens.hasItem(word));
					boolean isMatching = false;
					if (!(isMatching = word.matches("(?iU)"+words))) {
						if (Itens.hasItem(word) && previousAllowed) {
							isMatching = true;
						}
					}
					newFound = newFound | isMatching;
					if (isMatching) {
						break;
					}
				}
			}
			else {
				newFound = true;
			}
			previousAllowed = newFound;
			found = found & newFound;
		}
		return found;
	}
	
	public static List<String> initDict() {
		wordList = new ArrayList<String>();
		addArticles();
//		addPrepositions();
		addAdjPronouns();
		addNumerals();
		addConnectors();
		addSpecialChars();
		addOthers();
		return wordList;
	}
	
	private static void addArticles() {
		String words = "(a|o)s?,u(m|mas?),uns";
		wordList.addAll(Arrays.asList(words.split("\\s*,\\s*")));
	}
	
//	private static void addPrepositions() {
//		String words = "a,com,em,por,ante,contra," +
//					"entre,sem,ap(ó|o)s,de,p'?a?r(a|o|u)s?,sob," +
//					"at(é|e),desde,perante,sobre,tr(á|a)s";
//		wordList.addAll(Arrays.asList(words.split("\\s*,\\s*")));
//	}
	
	private static void addAdjPronouns() {
		String words = "(m|t|s)eus?,(t|s)uas?,(n|v)oss(a|o|u)s?," +
					"es(s|t)(a|e|i)s?,aquel(a|e|i)," +
					"a(l|u)gu(m|ns?|mas?),n(e|i)nhu(m|ns?|mas?)," +
					"tod(a|o|u)s?,o(u)?tr(a|o|u)s?,mu(it|ch)(a|o|u)s?,p(ou?|ô)c(a|o|u)s?," +
					"cert(a|o|u)s?,cada,v(a|á)ri(a|o|u)s?,tant(a|o|u)s?,quant(a|o|u)s?," +
					"mais,men(a|o|u)s,pr(o|ó)pri(a|o|u)s?";
		wordList.addAll(Arrays.asList(words.split("\\s*,\\s*")));
	}
	
	//XXX: considerar a inserção de mais numerais por escrito
	private static void addNumerals() {
		String words = "dois,duas,tr(e|ê)i?s,quatr(o|u),cinc(o|u),"+
				"seis,set(e|i),oit(o|u),nov(e|i),dei?(s|z),[0-9]+";
		wordList.addAll(Arrays.asList(words.split("\\s*,\\s*")));
	}

	private static void addConnectors() {
		String words = "e,ou";
		wordList.addAll(Arrays.asList(words.split("\\s*,\\s*")));
	}
	
	private static void addSpecialChars() {
		String words = "\\\\+";
		wordList.addAll(Arrays.asList(words.split("\\s*,\\s*")));
	}
	
	private static void addOthers() {
		String words = "tamb(e|é)i?(n|m),tbm?," + 
						"barat(o|u),zer(ad)?(o|u)," + 
						"pa?ra (hj|hoj(e|i)),(hj|hoj(e|i))," +
						"me(s|r)?m(o|u),a(e|ê|i|í)+,a(qu|k)i";
		wordList.addAll(Arrays.asList(words.split("\\s*,\\s*")));
	}
}
