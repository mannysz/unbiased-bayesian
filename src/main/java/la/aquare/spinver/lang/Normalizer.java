package la.aquare.spinver.lang;

public final class Normalizer {
	
	private Normalizer() {
		//empty
	}
	
	public static String normalize(String text) {
		String normalizedText = null;
		
		if (text != null) {
			normalizedText = text.toLowerCase().trim();
		}
		return normalizedText;
	}
	
	/**
	 * Realiza uma série de modificações (limpezas) no texto de entrada
	 * (tipicamente, um <em>post</em>) publicado.
	 * 
	 * Alterações aplicadas:
	 * <ul>
	 * <li>Remove os parênteses, chaves, colchetes, hífens (com
	 * espaço antes e depois), travessões;</li>
	 * <li>A exemplo do caso anterior, idem para o conteúdo no padrão 
	 * &gt;&gt;&gt;CONTEÚDO AQUI&lt;&lt;&lt;;
	 * <li>Remove vários tipos de aspas, incluindo apóstrofos;</li>
	 * <li>Remove <em>underlines</em>.</li>
	 * </ul>
	 * 
	 * XXX: verificar mais caracteres especiais 
	 * <a href="http://www.ascii.cl/htmlcodes.htm">aqui</a>
	 * 
	 * @param text texto do <i>post</i> publicado
	 * @return texto modificado (limpo para análise)
	 */
	public static String normalizeText(String text) {
		String normalizedText = null;
		
		if (text != null) {			
			//XXX: as expressões regulares comentadas abaixo
			//eram usadas para remover o conteúdo entre os símbolos especificados
//			text = text.replaceAll("(\\s+)?\\(+.*\\)+", "");
//			text = text.replaceAll("(\\s+)?\\[+.*\\]+", "");
//			text = text.replaceAll("(\\s+)?\\{+.*\\}+", "");
//			text = text.replaceAll("(\\s+)?>+.*<+", "");
//			text = text.replaceAll("(\\s+)?-+\\s+.*\\s+-+", "");
//			text = text.replaceAll("(\\s+)?—+.*—+", "");
			text = text.replaceAll("(\\s+)-+(\\s+)", " ");
			text = text.replaceAll("(\\s+)?[\\(\\)\\[\\]\\{\\}><—]+", "");
			text = text.replaceAll("\n+", " ");
			text = text.replaceAll("~+", " ");
			text = text.replaceAll("-+>+", " ");
			text = text.replaceAll("\\*+", " ");
			text = text.replaceAll("[#,]", " ");
			text = text.replaceAll("\\s+\\.{2,}\\s+", " ");
			text = text.replaceAll("[^\\p{L}\\d\\s!?\\.\\+-]", "");
			normalizedText = text.replaceAll("\"\'<>«»“”‘’_:", " ");
		}
		return normalizedText;
	}

}
