package la.aquare.spinver.lang.dict;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import la.aquare.spinver.lang.Normalizer;
import la.aquare.spinver.sql.SQLConn;


/**
 * Representa o dicionário de algum item que é objeto
 * das ações de compra e venda previstas em {@link Actions}.
 */
public final class Itens {
	private static Map<Item, List<String>> dict;
	
	
	private Itens() {
		//empty
	}

	private static void addToDict(Item term, List<String> wordsList) {
		if (dict == null) {
			dict = new HashMap<Item, List<String>>();
		}
		if (term != null && dict.get(term) == null && wordsList != null && !wordsList.isEmpty()) {
			dict.put(term, wordsList);
		}
	}
	
	private static ResultSet retrieve() {
		ResultSet rs = SQLConn.performQuery(
					"SELECT id_product, name, keywords " +
					"FROM product");
		if (rs == null) {
			System.out.println("[" + Itens.class.getSimpleName() + "] " + "ResultSet is null");
		}
		return rs; 
	}

	/**
	 * Carrega o dicion&aacute;rio dos itens que s&atilde;o objetos de compra/venda
	 * a partir de um banco de dados remoto.
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	public static Map<Item, List<String>> createDict() {
		java.sql.ResultSet rs = retrieve();
		try {
			if (rs != null) {
				while (rs.next()) {
					int productId = rs.getInt("id_product");
					String productName = rs.getString("name");
					String productKeywords = rs.getString("keywords");
					List<String> wordsList = null;
					
					if (productName != null && productKeywords != null) {
						wordsList = Arrays.
								asList(Normalizer.normalize(productKeywords).split("\\s*,\\s*"));
						Item item = new Item();
						item.id = productId;
						item.name = productName.trim();
						addToDict(item, wordsList);
					}
				}
				rs.close();
			}
		} catch (SQLException ex) {
			SQLConn.setSQLError(ex);
			closeAll(rs);
		}
//		System.out.println(dict);
		return dict;
	}
	
	private static void closeAll(java.sql.ResultSet rs) {
		if (rs != null) {
			try {
				rs.getStatement().close(); //encerra ambos ResultSet e Statement
			}
			catch (SQLException ex) {
				SQLConn.setSQLError(ex);
			}
		}
		if (SQLConn.getSQLError() != null) {
			System.out.println("[" + Itens.class.getSimpleName() +
								"] SQL ERROR: " + SQLConn.getSQLError());
		}
	}
	
	/**
	 * Carrega o dicion&aacute;rio de itens que s&atilde;o objetos de compra/venda
	 * a partir de um arquivo de texto local. 
	 * 
	 * @param filename
	 * @return
	 * @throws java.io.IOException
	 */
	public static Map<Item, List<String>> createDict(String filename) throws java.io.IOException {
		BufferedReader breader = new BufferedReader(new FileReader(filename));
		String line = null;
		while ((line = breader.readLine()) != null) {
			if (!line.trim().startsWith("#") && line.indexOf("=") > -1) {
				String[] parts = line.split("=");
				List<String> wordsList = null;
				//parts[0]: id,term
				//parts[1]: comma-separated list of words
				if (parts.length == 2) {
					String[] subParts = parts[0].split(",");
					if (subParts.length == 2) {
						int productId = -1;
						try {
							productId = Integer.valueOf(Normalizer.normalize(subParts[0]));	
						} catch (NumberFormatException ex) {
							//empty
						}
						if (productId != -1) {
							Item item = new Item();
							item.id = productId;
							item.name = subParts[1].trim();
							wordsList = Arrays.asList(Normalizer.normalize(parts[1]).
									split("\\s*,\\s*"));

							addToDict(item, wordsList);
						}
					}
				}
			}			
		}
		breader.close();
//		System.out.println(dict);
		return dict;
	}
	
	/**
	 * Devolve o primeiro item cadastrado correspondente ao termo-chave
	 * informado.
	 * 
	 * @param term
	 * @return Item ou null, se Item não for encontrado
	 */
	public static Item contains(String term) {
		Item itemFound = null;
		
		if (term != null && dict != null && !dict.isEmpty()) {
			term = Normalizer.normalize(term);
			
			for (Entry<Item, List<String>> entry : dict.entrySet()) {
				List<String> wordsList = entry.getValue();
				
				if (wordsList != null && !wordsList.isEmpty()) {
					if (wordsList.contains(term)) {
						itemFound = entry.getKey();
						itemFound.keyword = term;
						break;
					}
				}
			}
		}
		return itemFound;
	}
	
	public static boolean hasItem(Item item) {
		List<String> wordsList = dict.get(item.name);
		return wordsList!=null && !wordsList.isEmpty();
	}
	
	public static List<Item> sharesWith(List<String> wordsList) {
		ArrayList<Item> itemsFound = new ArrayList<Item>();
		
		if (wordsList != null && !wordsList.isEmpty()) {			
			for (String word : wordsList) {
				Item itemFound = null;
				
				if ((itemFound = contains(word)) != null) {
					itemsFound.add(itemFound.clone());	
//					break; XXX: deixei assim p/ contemplar o caso "Verbo X ou Y"
				}
			}
		}
		return itemsFound;
	}
	
	public static boolean hasItem(String word) {
		List<String> list = new ArrayList<String>();
		if (word != null) {
			list.add(word);	
		}		
		return !sharesWith(list).isEmpty();
	}

	
}
