package la.aquare.spinver.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import la.aquare.spinver.Result;
import la.aquare.spinver.lang.dict.ActionTerm;
import la.aquare.spinver.lang.dict.Actions;
import la.aquare.spinver.lang.dict.BeforeWordsAllowed;
import la.aquare.spinver.lang.dict.Item;
import la.aquare.spinver.lang.dict.ItemAttribute;
import la.aquare.spinver.lang.dict.ItemAttributes;
import la.aquare.spinver.lang.dict.ItemModel;
import la.aquare.spinver.lang.dict.ItemVersion;
import la.aquare.spinver.lang.dict.Itens;
import la.aquare.spinver.lang.dict.NegativePreVerbWords;
import la.aquare.spinver.lang.dict.RegexLib;

/**
 * Implementa o mecanismo de busca de padrões numa dada cadeia
 * de caracteres.
 */
public class Matcher {
	/**
	 * Define o tamanho máximo do ngrama a ser analisado,
	 * representando um protótipo de oração de compra ou venda.
	 * Tem um tamanho mínimo igual a 2.
	 * Esquematicamente, um ngrama obedece ao padrão:
	 * <pre>
	 * Verbo [Palavra [Palavra [... Palavra]]]
	 * </pre>
	 * 
	 * Onde:
	 * <ul>
	 * <li><b>Verbo (obrigatório)</b>: estrutura (simples ou composta) com
	 *  verbo que expressa uma ação de compra ou venda;</li>
	 * <li><b>Palavra (facultativo)</b>: cadeia de caracteres alfanuméricos 
	 * (incluindo caracteres acentuados e hífen) que representam um verbete.</li>
	 * </ul>
	 */
	public static int NGRAM_MAX_SIZE = 7;
	private static Map<Actions.Action, List<ActionTerm>> actions;
	/**
	 * Nível de correspondência alcançada na última análise. Níveis
	 * possíveis:
	 * <ul>
	 * <li>0: nenhuma correspondência;</li>
	 * <li>1: verbo de ação;</li>
	 * <li>2: verbo + elementos pós-verbais;</li>
	 * <li>3: verbo + elementos pós-verbais com menção aos objetos da ação;</li>
	 * <li>4: verbo + elementos pós-verbais com os objetos da ação;</li>
	 * <li>5: verbo + elementos pós-verbais com os objetos da ação e seus atributos.</li>
	 */
	public static short STATUS = 0; 
	
	public static List<Result> matchAction(String sentence, Actions.Action action) {
		STATUS = 0;
		
		if (actions == null) actions = Actions.initDicts();
		
		List<Result> results = new ArrayList<Result>();
		
		if (sentence != null && actions != null && !actions.isEmpty()) {
			List<ActionTerm> actionTermsList = actions.get(action);

			if (actionTermsList != null) {
				for (ActionTerm actionTerm : actionTermsList) {
					//i: ignore case; s: matching across multiple lines
					String pattern = null;
					if (actionTerm.hasBlockingBefore) {
						pattern = 
								"(?iUs)(.*"
								+ "(?<!\\b(" 
								+ NegativePreVerbWords.toRegex() 
								+ ")\\b)(\\s+|$)"
								+ "\\b" + actionTerm.term + "\\b"
								+ ".*)";
					}
					else {
						pattern = "(?iUs)(.*\\b" + actionTerm.term + "\\b.*)";;
					}
					//TODO: inseri um "X" antes para a regex na condição
					//actionTerm.hasBlockingBefore == true funcionar. Como alterar isso?
					if (("X " + sentence).matches(pattern)) {
						if (!sentence.matches("(?iU)" + actionTerm.negativeRegex)) {
//							System.out.println("term = " + actionTerm.term);
							STATUS = 1;
							results = matchNGram(sentence, actionTerm.term, action);
							
							if (STATUS > 2) break;
						}
					}
				}
			}
		}		
		return results;
	}
		
	private static List<Result> matchNGram(String sentence, String actionTerm,
			Actions.Action action) 
	{
		//gera um array de ngramas até NGRAM_MAX_SIZE
		String[] patterns = getNgramRegexes(actionTerm);
		List<Result> results = new ArrayList<Result>();
		
		if (sentence != null) {
			int numOfGrams = NGRAM_MAX_SIZE;
//			System.out.println("patterns = " + Arrays.toString(patterns));
			for (String pattern : patterns) {
				//verifica se o tamanho da janela de análise (tamanho do ngrama) 
				if (sentence.matches(pattern)) {
//					System.out.println("Encontrado padrão (\"" + numOfGrams + "\"-gram): " + pattern);					
					results = matchItem(sentence, actionTerm, numOfGrams, action);					
					break;
				}
				numOfGrams--;
			}			
		}
		return results;
	}
	
	private static List<Result> matchItem(String sentence, String actionTerm,
			int numOfGrams, Actions.Action action) 
	{
		List<Item> itemsFound = new ArrayList<Item>();
		List<Result> results = new ArrayList<Result>();
		//notação "?:" significa "non-capturing group"
		String regexHead = "(" 
						+ actionTerm.replace("(", "(?:").
						replaceAll("\\(\\?:\\?\\!.*\\)", "") 
						+ "(!|,|\\.)" 
						+ "*)";
		String regexTail = "";
		for (int i = 0; i < numOfGrams-1; i++) {
			regexTail = regexTail 
					+ "\\s+("
//					+ "[\\p{L}|\\d]+[-[\\p{L}|\\d]]*"
					+ RegexLib.wordRegex
					+ "|" + RegexLib.specialChars
					+ ")[,]*";
		}
		String regex = regexHead + regexTail;
//		System.out.println("r: " + regex);

		Pattern pattern = Pattern.compile(regex);
		java.util.regex.Matcher matcher = pattern.matcher(Normalizer.normalize(sentence));
		String afterActionTerm = ""; 
		
		while (matcher.find()) {
			for (int i = 2; i <= matcher.groupCount(); i++) {
				if (matcher.group(i) != null) {
					afterActionTerm = afterActionTerm + " " + matcher.group(i);
				}
			}
		}
//		System.out.println("afterActionTerm = " + afterActionTerm);
		if (afterActionTerm.length() > 0) {
			STATUS = 2;
			afterActionTerm = afterActionTerm.substring(1);
			itemsFound = findNGrams(afterActionTerm, 3);			
			Map<Item,List<ItemAttribute>> itemsFoundWithAttrs = 
					ItemAttributes.filterAttributes(itemsFound, afterActionTerm);
			
			for (Item item : itemsFound) {
				Result result = new Result();
				result.action = action;
				result.item = item;
				
				List<ItemAttribute> itemAttrs = itemsFoundWithAttrs.get(item);
				if (itemAttrs != null && !itemAttrs.isEmpty()) {
					STATUS = 5;
					for (ItemAttribute attr : itemAttrs) {
						if (attr instanceof ItemVersion) {
							result.version = (ItemVersion) attr;
						}
						else if (attr instanceof ItemModel) {
							result.models.add((ItemModel) attr);
						}
					}
				}
				results.add(result);
			}	
		}
		return results;
	}
	
	private static String[] getNgramRegexes(String headTerm) {
		if (NGRAM_MAX_SIZE < 2) {
			NGRAM_MAX_SIZE = 2;
		}
		int numOfPatterns = NGRAM_MAX_SIZE - 1;
		//notação \\p{L}+[-\\p{L}]* significa: 
		//palavras com caracteres em Unicode (inclui os acentuados) considerando palavras hifenizadas
		String regexHead = "(?iUs)(.*\\b" + headTerm + "\\b(!|,|\\.)*";
		String regexGram = "\\s+("
//				+ "\\b" + "[\\p{L}|\\d]+[-[\\p{L}|\\d]]*" + "\\b"
				+ "\\b" + RegexLib.wordRegex + "\\b"
				+ "|" + RegexLib.specialChars
				+ ")[,]*";
		String regexTail = ".*)";
		String[] patterns = new String[numOfPatterns];
		
		for (int i = numOfPatterns; i > 0; i--) {
			String regexNGram = "";
			for (int j = 0; j < i; j++) {
				regexNGram = regexNGram + regexGram;
			}
			patterns[numOfPatterns-i] = regexHead + regexNGram + regexTail;
		}
		return patterns;
	}

	private static List<Item> findNGrams(String sequence, int maxNGramSize) {
		List<Item> itemsFound = new ArrayList<Item>();
		
		if (maxNGramSize < 1) {
			maxNGramSize = 1;
		}
		for (int ngramSize = maxNGramSize; ngramSize > 0; ngramSize--) {
			List<String> ngrams = SimpleNGramGenerator.generate(sequence, ngramSize);
			List<Item> ngramsFound = new ArrayList<Item>();
			
//			System.out.println(ngrams);
			if (!(ngramsFound = Itens.sharesWith(ngrams)).isEmpty()) {
				STATUS = 3;
				//XXX: comentei a linha abaixo para pegar o nome do produto
				//em situações em que tanto o nome do produto quanto sua versão
				//são palavras-chave do produto
				//Exemplo: compro iphone 5c (iphone e 5c são palavras chave para Iphone)
//				Collections.reverse(ngramsFound);
//				System.out.println(ngramSize + "-gram: " +  ngramsFound);
				for (Item ngramFound : ngramsFound) {
					if (itemsFound.contains(ngramFound)) {
						continue;
					}
					if (!hasBarrierBefore(sequence, ngramFound.keyword)) {
						STATUS = 4;
						itemsFound.add(ngramFound);

//						int k = -1;
//						if ((k = sequence.indexOf(ngramFound.keyword)) > -1) {
//							//ainda pode haver produtos, por exemplo, encontrou X e ainda
//							//falta encontrar Y na frase "compro X ou Y"
//							if ( (k+ngramFound.keyword.length()) < sequence.length()) {
////								System.out.println("ngramFound: " + ngramFound.keyword + ", sequence = " + sequence + ", k = " + k);
//								sequence = sequence.substring(k+ngramFound.keyword.length());
////								System.out.println("new sequence = " + sequence);
//							}
//						}
					}
					else {
//						System.out.println("Remova " + ngramFound);
						itemsFound.remove(ngramFound);
					}
				}
			}
		}
		return itemsFound;
	}
	
	private static boolean hasBarrierBefore(String sequence, String reference) {
		boolean barrierExists = false;		
		if (sequence != null && reference != null) {
			if (!sequence.startsWith(reference)) {
				int k = -1;
				if ((k = sequence.indexOf(reference)) > -1) {
					String precedent = sequence.substring(0, k);
					precedent = precedent.replaceAll("[^\\p{L}\\d,_-]", " ").trim();
//					System.out.println("precedent = " + precedent);
					List<String> precedentList = Arrays.asList(precedent.split("\\s+"));
					Collections.reverse(precedentList);
					barrierExists = !precedentList.isEmpty() &&
							!BeforeWordsAllowed.contains(precedentList); 
				}
			}
		}
		return barrierExists;
	}
	
}
