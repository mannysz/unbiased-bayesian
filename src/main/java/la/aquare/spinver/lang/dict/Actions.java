package la.aquare.spinver.lang.dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa o dicionário com os padrões de expressão de
 * ação de compra e venda.
 */
public final class Actions {
	
	public enum Action { BUY, SALE, BOTH, NONE };

	private static Map<Action, List<ActionTerm>> dict;
	
	private Actions() {
		//empty
	}
	
	public static Map<Action, List<ActionTerm>> initDicts() {
		dict = new HashMap<Action, List<ActionTerm>>();
		initBuyTermsDict();
		initSellTermsDict();
		initBuyAndSellTermsDict();
		return dict;
	}
	
	/**
	 * Instancia os termos variantes para a expressão composta 
	 * "compra-se e/ou vende-se" (ou vice-versa) no dicionário de ações.
	 */
	private static void initBuyAndSellTermsDict() {
		//cria a lista de padrões de busca para a ação de comprar e/ou vender
		//(ou vice-versa): primeiro, as cadeias maiores; depois, as menores
		List<ActionTerm> buyAndSellTermsList = new ArrayList<ActionTerm>();

		//estou comprando e estou vendendo (e variantes)
		buyAndSellTermsList.add(
				new ActionTerm("(es)?tou?\\s+comprand?(o|u)\\s+"
						+ "(e|ou)?\\s+"
						+ "((es)?tou?)?\\s+vendend?(o|u)", true)
				);
		buyAndSellTermsList.add(
				new ActionTerm("t(ô|o)\\s+comprand?(o|u)\\s+"
						+ "(e|ou)?\\s+"
						+ "(t(ô|o))?\\s+vendend?(o|u)", true)
				);
		buyAndSellTermsList.add(
				new ActionTerm("(es)?tou?\\s+vendend?(o|u)\\s+"
						+ "(e|ou)?\\s+"
						+ "((es)?tou?)?\\s+comprand?(o|u)", true)
				);
		buyAndSellTermsList.add(
				new ActionTerm("t(ô|o)\\s+vendend?(o|u)\\s+"
						+ "(e|ou)?\\s+"
						+ "(t(ô|o))?\\s+comprand?(o|u)", true)
				);
		
		//compra-se e vende-se (e variantes)
		buyAndSellTermsList.add(
				new ActionTerm("compra-se\\s+(e|ou)?\\s+vend(e|i)-se", true)
				);
		buyAndSellTermsList.add(
				new ActionTerm("compra-\\s+se\\s+(e|ou)?\\s+vend(e|i)-\\s+se", true)
				);
		buyAndSellTermsList.add(
				new ActionTerm("compra\\s+-se\\s+(e|ou)?\\s+vend(e|i)\\s-+se", true)
				);
		buyAndSellTermsList.add(
				new ActionTerm("compr(o|u)\\s+(e|ou|,)?\\s+vend(o|u)", true)
				);
		buyAndSellTermsList.add(
				new ActionTerm("vend(e|i)-se\\s+(e|ou)?\\s+compra-se", true)
				);
		buyAndSellTermsList.add(
				new ActionTerm("vend(e|i)-\\s+se\\s+(e|ou)?\\s+compra-\\s+se", true)
				);		
		buyAndSellTermsList.add(
				new ActionTerm("vend(e|i)\\s-+se\\s+(e|ou)?\\s+compra\\s+-se", true)
				);
		buyAndSellTermsList.add(
				new ActionTerm("vend(o|u)\\s+(e|ou|,)?\\s+compr(o|u)", true)
				);
		dict.put(Action.BOTH, buyAndSellTermsList);
	}

	/**
	 * Instancia os termos variantes para a expressão "compra-se"
	 * no dicionário de ações.
	 */
	private static void initBuyTermsDict() {
		//cria a lista de padrões de busca para a ação de comprar:
		//primeiro, as cadeias maiores; depois, as menores
		List<ActionTerm> buyTermsList = new ArrayList<ActionTerm>();

		//estou a fim de comprar (e variantes) (//\\s+: 1 ou mais espaços)
		buyTermsList.add(
				new ActionTerm("(es)?tou?\\s+"
						+ "(afi(n|m)|a\\s+fi(m|n))\\s+de\\s+"
						+ "compr(ar|á)", true)
				);
		buyTermsList.add(
				new ActionTerm("t(ô|o)\\s+"
						+ "(afi(n|m)|a\\s+fi(m|n))\\s+de\\s+"
						+ "compr(ar|á)", true)
				);
		
		//estou à procura de (e variantes). TODO: como reduzir essas expressões?
		buyTermsList.add(
				new ActionTerm("(es)?tou?\\s+(a|à)\\s+pr(o|u)cura\\s+de", 
						true) //, RegexLib.notMatchForActions) XXX: obsoleto
				);
		buyTermsList.add(
				new ActionTerm("t(ô|o)\\s+(a|à)\\s+pr(o|u)cura\\s+de",
						true) //, RegexLib.notMatchForActions)
				);
		buyTermsList.add(
				new ActionTerm("(es)?tou?\\s+(a|à)\\s+pr(o|u)cura", 
						true) //, RegexLib.notMatchForActions)
				);
		buyTermsList.add(
				new ActionTerm("t(ô|o)\\s+(a|à)\\s+pr(o|u)cura", 
						true) //, RegexLib.notMatchForActions)
				);
		
		//alguém (aí) que está com... ?
		buyTermsList.add(
				new ActionTerm("algu?(em|ém|ein)(\\s+"
						+ RegexLib.inAsksings
						+ ")?\\s+"
						+ "q(ue)?\\s+(es)t(a|á)\\s+c(o|u)m", 
						false) //, RegexLib.notMatchForActions) XXX: obsoleto
				);
		
		//alguém (aí) que tenha... ?
		buyTermsList.add(
				new ActionTerm("algu?(em|ém|ein)(\\s+"
						+ RegexLib.inAsksings
						+ ")?\\s+"
						+ "q(ue)?\\s+tenha", 
						false) //, RegexLib.notMatchForActions) XXX: obsoleto
				);
		
		//alguém (aí) está vendendo/tendo... ? (//\\s+: 1 ou mais espaços)
		buyTermsList.add(
				new ActionTerm("algu(em|ém|ein)(\\s+"
						+ RegexLib.inAsksings
						+ ")?\\s+"
						+ "(es)?t(a|á)\\s+((vendend?(o|u))|tend?(o|u))", 
						false) //, RegexLib.notMatchForActions) XXX: obsoleto
				);
		
		buyTermsList.add(
				new ActionTerm("algu?(em|ém|ein)"
						+ "(\\s+"
						+ RegexLib.inAsksings
						+ ")?"
						+ "\\s+tem",
						false) //, RegexLib.notMatchForActions)  XXX: obsoleto
				);
		
		//quem/alguém aí com/tem/teria/vende? (e variantes)
		buyTermsList.add(
				new ActionTerm("(quei?(m|n)|algu?(em|ém|ein))"
						+ "(\\s+"
						+ RegexLib.inAsksings
						+ ")?"
						+ "\\s+(c(u|o)m|vende|(te(m|ria)))"
						+ "(\\s++"
						+ RegexLib.inAsksings
						+ ")?+",
						false) //, RegexLib.notMatchForActions)  XXX: obsoleto
				);
		
		//alguém aí vende/vendendo? (e variantes)
		buyTermsList.add(
				new ActionTerm("algu(em|ém|ein)(\\s+"
						+ RegexLib.inAsksings 
						+ ")?\\s+vend(e|i)nd?(o|u)", false)
				);
		
		//tenho interesse em (e variantes)
		buyTermsList.add(
				new ActionTerm("tenh(o|u)\\s+interesse"
						+ "(\\s++("
						+ "(p(or|el(a|o|u)))|"
						+ "(em|n?um|n?(a|o)s?)"
						+ "))?+",
						true) //, RegexLib.notMatchForActions) XXX: obsoleto
				);
		
		//estou interessado em (e variantes)
		buyTermsList.add(
				new ActionTerm("(es)?tou?\\s+interessad(a|o|u)"
						+ "(\\s++("
						+ "(p(or|el(a|o|u)))|"
						+ "(em|n?um|n?(a|o)s?)"
						+ "))?+",
						true) //, RegexLib.notMatchForActions) XXX: obsoleto
				);
		buyTermsList.add(
				new ActionTerm("t(ô|o)\\s+interessad(a|o|u)"
						+ "(\\s++("
						+ "(p(or|el(a|o|u)))|"
						+ "(em|n?um|n?(a|o)s?)"
						+ "))?+",
						true) //, RegexLib.notMatchForActions) XXX: obsoleto
				);
		
		//estou querendo/tentando comprar (//\\s+: 1 ou mais espaços)
		buyTermsList.add(
				new ActionTerm("(es)?tou?\\s+(queren|tentan)d?(o|u)\\s+"
						+ "compr(ar|á)", true)
				);
		buyTermsList.add(
				new ActionTerm("t(ô|o)\\s+(queren|tentan)d?(o|u)\\s+"
						+ "compr(ar|á)", true)
				);
		
		//compro ou troco por (e variantes) XXX: inferência: troco por X => "compro" X
		buyTermsList.add(
				new ActionTerm("compr(o|u)\\s+(e|ou|,)?\\s+troc(o|u)\\s+p(or|el(a|o|u))",
						true)
				);
		
		//estou/tou/tô/to compran(d)o/compran(d)u (//\\s+: 1 ou mais espaços)
		buyTermsList.add(
				new ActionTerm("(es)?tou?\\s+comprand?(o|u)",true)
				);
		buyTermsList.add(
				new ActionTerm("t(ô|o)\\s+comprand?(o|u)",true)
				);
		
		//quero comprar (e variantes) (//\\s+: 1 ou mais espaços)
		buyTermsList.add(
				new ActionTerm("quer(ia|o|u)\\s+compr(ar|á)", true)
				);
		
		//preciso comprar (e variantes) (//\\s+: 1 ou mais espaços)
		buyTermsList.add(
				new ActionTerm("preci(s|z)(o|u)\\s+compr(ar|á)")
				);
		
		//quero trocar por (//\\s+: 1 ou mais espaços)
		buyTermsList.add(
				new ActionTerm("quer(ia|o|u)\\s+troc(ar|á)\\s+p(or|el(a|o|u))", true)
				);
		
		//estou procurando/querendo (e variantes) (//\\s+: 1 ou mais espaços)	
		buyTermsList.add(
				new ActionTerm("(es)?tou?\\s+pr(o|u)curand?(o|u)", 
						true) //, RegexLib.notMatchForActions) XXX: obsoleto
				);
		buyTermsList.add(
				new ActionTerm("t(ô|o)\\s+pr(o|u)curand?(o|u)", 
						true) //, RegexLib.notMatchForActions)
				);
		buyTermsList.add(
				new ActionTerm("(es)?tou?\\s+querend?(o|u)", 
						true) //, RegexLib.notMatchForActions)
				);
		buyTermsList.add(
				new ActionTerm("t(ô|o)\\s+querend?(o|u)", 
						true) //, RegexLib.notMatchForActions)
				);
		
		//XXX: confunde com o particípio de pagar (e.g., foi pago por).
//		buyTermsList.add("pag(o|u)\\s+por");    //XXX: pag(o|u)(\\s+por)? não funcionou
		
		//compra-se (e variantes)
		//XXX: não inclui "compra", pois confunde-se com o substantivo "compra" (= "ato de comprar")
		buyTermsList.add(
				new ActionTerm("compra-se", true)
				);
		buyTermsList.add(
				new ActionTerm("compra-\\s+se", true)
				);       //deve vir antes da regra seguinte, se esta estiver ativada
//		buyTermsList.add("compra(?!\\s+-?se)"); //'matching' se não houver " -se/se" depois
		buyTermsList.add(
				new ActionTerm("compra\\s+se", true)
				);
		buyTermsList.add(
				new ActionTerm("compra\\s+-se", true)
				);
		//XXX: gera falso positivo (confunde-se com a forma "comprasse",
		//verbo comprar conjugado no pretérito do subjuntivo)
//		buyTermsList.add(
//				new ActionTerm("comprass(e|i)", true)
//				);
		
		//compro (e variantes)
		buyTermsList.add(
				new ActionTerm("compr(o|u)", true)
				);

		//procuro (por)/preciso (de)/troco por/gostaria de
		buyTermsList.add(
				new ActionTerm("troc(o|u)\\s+p(or|el(a|o|u))", true)
				);		
		buyTermsList.add(
				new ActionTerm("pr(o|u)cur(o|u)\\s+p(or|el(a|o|u))", 
						true) //, RegexLib.notMatchForActions) XXX: obsoleto
				);
		buyTermsList.add(
				new ActionTerm("preci(s|z)(o|u)\\s+de", 
						true) //, RegexLib.notMatchForActions)
				);
		buyTermsList.add(
				new ActionTerm("gostaria(\\s++de)?+(\\s++compr(ar|á))?+",  //XXX: em pt_PT, gostava de
						true) //, RegexLib.notMatchForActions)
				);
		
		//procuro/preciso/quero/aceito/gostaria
		buyTermsList.add(
				new ActionTerm("pr(o|u)cur(o|u)", 
						true) //, RegexLib.notMatchForActions)
				);
		buyTermsList.add(
				new ActionTerm("preci(s|z)(o|u)", 
						true) //, RegexLib.notMatchForActions)
				);
		buyTermsList.add(
				new ActionTerm("quer(ia|o|u)", 
						true) //, RegexLib.notMatchForActions)
				);
//		buyTermsList.add(
//				new ActionTerm("gostaria",  //XXX: em pt_PT, gostava
//						true) //, RegexLib.notMatchForActions)
//				);
		//XXX: inferência: aceito X => "compro" X
		buyTermsList.add(
				new ActionTerm("aceit(o|u)", 
						true) //, RegexLib.notMatchForActions)
				);
//		buyTermsList.add("pag(o|u)");  //XXX: confunde com o particípio de pagar (e.g., foi pago).
		
		dict.put(Action.BUY, buyTermsList);
	}

	/**
	 * Instancia os termos variantes para a expressão "vende-se"
	 * no dicionário de ações.
	 */
	private static void initSellTermsDict() {
		//cria a lista de padrões de busca para a ação de vender:
		//primeiro, as cadeias maiores; depois, as menores
		List<ActionTerm> sellTermsList = new ArrayList<ActionTerm>();
		
		//estou a fim de vender (e variantes) (//\\s+: 1 ou mais espaços)
		sellTermsList.add(
				new ActionTerm("(es)?tou?\\s+"
						+ "(afi(n|m)|a\\s+fi(m|n))\\s+de\\s+"
						+ "vend(er|ê)", true)
				);
		sellTermsList.add(
				new ActionTerm("t(ô|o)\\s+"
						+ "(afi(n|m)|a\\s+fi(m|n))\\s+de\\s+"
						+ "vend(er|ê)", true)
				);
		
		//alguém aí tem/teria interesse em X? (e variantes)
		sellTermsList.add(
				new ActionTerm("algu?(em|ém|ein)(\\s+"
						+ RegexLib.inAsksings
						+ ")?\\s+te(m|ria)\\s+interesse(\\s+"
						+ RegexLib.inAsksings
						+ ")?"
						+ "(\\s++("
						+ "(p(or|el(a|o|u)))|"
						+ "(em|n?um|n?(a|o)s?)"
						+ "))?+",
						false) //, RegexLib.notMatchForActions)  XXX: obsoleto
				);
		
		//estou precisando/tentando/querendo vender (e variantes)
		sellTermsList.add(
				new ActionTerm("(es)?tou?\\s+"
						+ "(preci(s|z)an|queren|tentan)d?(o|u)\\s+"
						+ "vend(ê|er)")
				);
		sellTermsList.add(
				new ActionTerm("t(ô|o)\\s+"
						+ "(preci(s|z)an|queren|tentan)d?(o|u)\\s+"
						+ "vend(ê|er)", true)
				);
		
		//vendo ou troco (e variantes)
		sellTermsList.add(
				new ActionTerm("vend(o|u)\\s+(e|ou|,)?\\s+troc(o|u)(?!\\s+p(or|el(a|o|u)))")
				);
		sellTermsList.add(
				new ActionTerm("troc(o|u)\\s+(e|ou|,)?\\s+vend(o|u)", true)
				);

		//estou vendendo (e variantes)
		sellTermsList.add(
				new ActionTerm("(es)?tou?\\s+vendend?(o|u)", true)
				); //\\s+: 1 ou mais espaços
		sellTermsList.add(
				new ActionTerm("t(ô|o)\\s+vendend?(o|u)", true)
				);

		//quero vender (e variantes) (//\\s+: 1 ou mais espaços)
		sellTermsList.add(
				new ActionTerm("quer(o|u)\\s+vend(er|ê)", true)
				);
		
		//preciso vender (e variantes) (//\\s+: 1 ou mais espaços)
		sellTermsList.add(
				new ActionTerm("preci(s|z)(o|u)\\s+vend(er|ê)", true)
				);
		
		//XXX: inferência: troco X => "vendo" X
		sellTermsList.add(
				new ActionTerm("troc(o|u)(?!\\s+p(or|el(a|o|u)))", true)
				);
		
		//vende-se e variantes
		sellTermsList.add(
				new ActionTerm("vend(e|i)-se", true)
				);
		sellTermsList.add(
				new ActionTerm("vend(e|i)-\\s+se", true)
				);
		sellTermsList.add(
				new ActionTerm("vend(e|i)\\s+-se", true)
				);
		//XXX: gera falso positivo (confunde-se com a forma "vendesse",
		//verbo vender conjugado no pretérito do subjuntivo)
//		sellTermsList.add(
//				new ActionTerm("vend(e|i)ss?(e|i)", true)
//				);
		
		//vendo (e variantes)
		sellTermsList.add(
				new ActionTerm("vend(o|u)", true)
				);
		
		dict.put(Action.SALE, sellTermsList);
	}
}
