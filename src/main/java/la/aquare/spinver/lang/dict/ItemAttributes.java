package la.aquare.spinver.lang.dict;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import la.aquare.spinver.lang.Normalizer;
import la.aquare.spinver.lang.Tokenizer;
import la.aquare.spinver.sql.SQLConn;
import la.aquare.spinver.util.StringUtil;


/**
 * Representa o dicionário dos atributos (modelo e versão) de 
 * um {@link Item} que é objeto das ações de compra e venda
 * previstas em {@link Actions}.
 */
public final class ItemAttributes {
	private static Map<Item, List<ItemModel>> modelDict = null;
	private static Map<Item, List<ItemVersion>> versionDict = null;
	public enum TYPE { MODEL, VERSION };
	public static short ATTR_NGRAM_MAX_SIZE = 3;
	
	
	private ItemAttributes() {
		//empty
	}
	
	public static void printDicts(TYPE type) {
		if (type == TYPE.MODEL) {
			for (Entry<Item, List<ItemModel>> e : modelDict.entrySet()) {
				System.out.println("******" + e.getKey() + "******");
				for (ItemModel m : e.getValue()) {
					System.out.println(m);
				}
			}
		}
		else if (type == TYPE.VERSION) {
			for (Entry<Item, List<ItemVersion>> e : versionDict.entrySet()) {
				System.out.println("******" + e.getKey() + "******");
				for (ItemVersion v : e.getValue()) {
					System.out.println(v);
				}
			}
		}
	}
	
	public static List<ItemModel> get(Item item) {
		return modelDict.get(item);
	}
	
	private static void addToDict(Item item, ItemModel model) {
		if (modelDict == null) {
			modelDict = new HashMap<Item, List<ItemModel>>();
		}
		List<ItemModel> models;
		if ((models = modelDict.get(item)) == null) {
			models = new ArrayList<ItemModel>();
		}
		models.add(model);
		modelDict.put(item, models);
	}
	
	private static void addToDict(Item item, ItemVersion version) {
		if (versionDict == null) {
			versionDict = new HashMap<Item, List<ItemVersion>>();
		}
		if (item != null && version != null) {
			List<ItemVersion> versions;
			if ((versions = versionDict.get(item)) == null) {
				versions = new ArrayList<ItemVersion>();
			}
			versions.add(version);
			versionDict.put(item, versions);
		}
	}
	
	private static ResultSet retrieve(TYPE type) {
		String sqlQuery = null;
		
		if (type == TYPE.VERSION) {
			sqlQuery = "SELECT "
					+ "b.name AS product, "
					+ "a.name AS version, "
					+ "a.id_product_version AS versionId, "
					+ "a.keywords AS versionKeywords "
					+ "FROM "
					+ "product_version a "
					+ "INNER JOIN product b ON a.id_product = b.id_product "
					+ "ORDER BY product";
		}
		else if (type == TYPE.MODEL) {
			sqlQuery = "SELECT "
					+ "b.id_product AS idProduct, "
					+ "b.name AS product, "
					+ "a.name AS model, "
					+ "a.id_product_model AS modelId, "
					+ "a.type AS modelType, "
					+ "a.keywords AS modelKeywords "
					+ "FROM "
					+ "product_model a "
					+ "INNER JOIN product b ON a.id_product = b.id_product "
					+ "ORDER BY product";
		}
		ResultSet rs = SQLConn.performQuery(sqlQuery);
		if (rs == null) {
			System.out.println("[" + ItemAttributes.class.getSimpleName() + "] " +
								"ResultSet is null: " + SQLConn.getSQLError());
		}
		return rs; 
	}

	/**
	 * Carrega o dicionário dos atributos (modelo/versão)
	 * de um item que são objetos de compra/venda a partir
	 * de um banco de dados remoto.
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	public static void createDict(TYPE type) {
		java.sql.ResultSet rs = retrieve(type);
		try {
			if (rs != null) {
				while (rs.next()) {
					String productName = rs.getString("product");
					Item item = new Item();
					item.name = productName.trim();
					
					if (productName != null) {
						if (type == TYPE.MODEL) {
							String modelName = rs.getString("model");
							int modelId = rs.getInt("modelId");
							String modelType = rs.getString("modelType");
							String modelKeywords = rs.getString("modelKeywords");
							
							if (modelName != null && modelKeywords != null) {
								ItemModel model = new ItemModel();
								model.id = modelId;
								model.name = modelName.trim();
								model.type = Normalizer.normalize(modelType);
								model.keywords = "(" + Normalizer.
										normalize(modelKeywords.replaceAll("\\s*,\\s*", "|")) + ")";
								addToDict(item, model);
							}
						}
						else if (type == TYPE.VERSION) {
							String versionName = rs.getString("version");
							int versionId = rs.getInt("versionId");
							String versionKeywords = rs.getString("versionKeywords");
							
							if (versionName != null && versionKeywords != null) {
								ItemVersion version = new ItemVersion();
								version.id = versionId;
								version.name = versionName.trim();
								version.keywords = "(" + Normalizer.
										normalize(versionKeywords.replaceAll("\\s*,\\s*", "|")) + ")";
								addToDict(item, version);
							}
						}						
					}
				}
				rs.close();
			}
		} catch (SQLException ex) {
			SQLConn.setSQLError(ex);
			closeAll(rs);
		}
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
			System.out.println("[" + ItemAttributes.class.getSimpleName() +
								"] SQL ERROR: " + SQLConn.getSQLError());
		}
	}
	
	public static Map<Item, List<ItemModel>> getModelsDict() {
		return modelDict;
	}
	
	public static Map<Item, List<ItemVersion>> getVersionsDict() {
		return versionDict;
	}
	
	/**
	 * Carrega o dicionário dos atributos de modelo de um item 
	 * que é objeto de compra/venda a partir de um arquivo de texto local. 
	 * 
	 * @param filename
	 * @return
	 * @throws java.io.IOException
	 */
	public static void createDict(String filename, TYPE type) 
			throws java.io.IOException {
		BufferedReader breader = new BufferedReader(new FileReader(filename));
		String line = null;
		while ((line = breader.readLine()) != null) {
			if (!line.trim().startsWith("#") && line.indexOf("|") > -1) {
				String[] parts = line.split("\\|");
				switch (type) {
					case MODEL:
						//parts[0]: model id
						//parts[1]: item name
						//parts[2]: model name
						//parts[3]: model type
						//parts[4]: comma-separated model name keywords
						if (parts.length == 5) {
							int modelId = -1;
							try {
								modelId = Integer.
										valueOf(Normalizer.normalize(parts[0]));
							} catch (NumberFormatException ex) {
								//empty
							}
							if (modelId != -1) {
								Item item = new Item();
								item.name = parts[1].trim();

								ItemModel model = new ItemModel();
								model.id = modelId;
								model.name = parts[2].trim();
								model.type = Normalizer.normalize(parts[3]);
								model.keywords = "(" + Normalizer.normalize(parts[4]).
										replaceAll("\\s*,\\s*", "|") + ")";
//										Arrays.asList(Normalizer.normalize(parts[3]).
//												split("\\s*,\\s*"));
								addToDict(item, model);
							}
						}
						break;
				case VERSION:
					//parts[0]: version id
					//parts[1]: item name
					//parts[2]: version name
					//parts[3]: comma-separated version name keywords
					if (parts.length == 4) {
						int versionId = -1;
						try {
							versionId = Integer.
									valueOf(Normalizer.normalize(parts[0]));
						} catch (NumberFormatException ex) {
							//empty
						}
						if (versionId != -1) {
							Item item = new Item();
							item.name = parts[1].trim();

							ItemVersion version = new ItemVersion();
							version.id = versionId;
							version.name = parts[2].trim();
							version.keywords = "(" + Normalizer.normalize(parts[3]).
									replaceAll("\\s*,\\s*", "|") + ")";
//									Arrays.asList(Normalizer.normalize(parts[2]).
//											split("\\s*,\\s*"));
							addToDict(item, version);							
						}
					}
					break;
				default:
					break;
				}

			}			
		}
		breader.close();
	}
	
	public static Map<Item,List<ItemAttribute>> filterAttributes(List<Item> items, String sequence) {
//		System.out.println("items: " + items);
		Map<Item,List<ItemAttribute>> allAttributes = new HashMap<Item,List<ItemAttribute>>();
		if (sequence != null && items != null && !items.isEmpty()) {
			String subSequence = sequence;
			for (int i = items.size()-1; i >= 0 ; i--) {
//			for (int i = 0; i < items.size(); i++) {
				int k = -1;
				if ((k = subSequence.lastIndexOf(items.get(i).keyword)) > -1) {
//				if ((k = subSequence.indexOf(items.get(i).keyword)) > -1) {
					List<ItemAttribute> attrs = findAttributes(items.get(i), subSequence);
					if (!attrs.isEmpty()) {
						allAttributes.put(items.get(i), attrs);
//						break;
					}
//					System.out.println("subSequence 0: " + subSequence + ", kw: " + items.get(i).keyword + ", k: " + k);
					subSequence = subSequence.substring(0,k);
//					System.out.println("subSequence 1: " + subSequence + ", k: " + items.get(i).keyword);
				}
			}
		}
		return allAttributes;
	}
	
	private static List<ItemAttribute> findAttributes(Item item, String sequence) {
//		System.out.println("item.keyword = "+ item.keyword);
//		System.out.println("sequence 1 " + sequence);
		boolean tryVersion = true;
		List<String> modelTypesAdded = new ArrayList<String>();
		List<ItemAttribute> attrsFound = new ArrayList<ItemAttribute>();
		sequence = sequence.replaceAll("[^\\p{L}\\d\\._-]", " ").trim();
//		System.out.println("sequence 2 " + sequence);
		int k = -1;
		if ((k = sequence.indexOf(item.keyword)) > -1) {
			sequence = sequence.substring((k+item.keyword.length()));						
		}
		String[] tokens = null;
		try {
			tokens = Tokenizer.tokenize(sequence.trim());
		} catch (IOException e) {
			//empty
		}
//		System.out.println("sequence 3 " + sequence);
		//coleta as informações de atributo, que pode ser até um n-grama do
		//tamanho ATTR_NGRAM_MAX_SIZE e deve começar imediatamente
		//após o nome do produto até encontrar o primeiro termo interveniente
		//na cadeia, i.e., que não é nem versão nem modelo.
		if (tokens != null) {
			List<? extends ItemAttribute> attrs = null;
			String tryKeyword = null;
			for (int i = 0; i < tokens.length; i++) {
				attrs = (tryVersion ? versionDict.get(item) : modelDict.get(item));
//				System.out.println("*** token, i = " + i + "***");
				boolean isAttrFound = false;
				
				for (int j = 0; j < ATTR_NGRAM_MAX_SIZE; j++) {
					tryKeyword = StringUtil.join(tokens, i, i+j+1, " ");
//					System.out.println("tryKeyword = " + tryKeyword);
					
					if (attrs == null) {
						break;
					}
					for (ItemAttribute attr : attrs) {
						String keywords = null;
						if (attr instanceof ItemVersion) {
							keywords = ((ItemVersion) attr).keywords;
						}
						else if (attr instanceof ItemModel) {
							keywords = ((ItemModel) attr).keywords;
						}
//						System.out.println("tryKeyword = " + tryKeyword + ", keywords = " + keywords);
						if (tryKeyword.matches(keywords)) {
							isAttrFound = true;
							attrsFound.add(attr);
							i = i + j;
							if (!tryVersion) {
								if (!modelTypesAdded.contains(((ItemModel)attr).type)) {
									modelTypesAdded.add(((ItemModel)attr).type);	
								}
								else {
									attrsFound.remove(attr);
								}
							}							
//							System.out.println("next pos = " + i);
							j = ATTR_NGRAM_MAX_SIZE; //força a saída do 2º loop externo
							break; //força a saída do 1º loop interno
						}
					}
				}
				if (!isAttrFound) {
					if(tryVersion) {
						i--;
					}
					else {
						break;
					}
				}
				if(tryVersion) {
					tryVersion = false;
				}
			}
		}
		return attrsFound;
	}

//	private static ItemAttribute modelExists(Item item, String modelKeyword) {
//		ItemModel modelFound = null;
//		List<ItemModel> models = modelDict.get(item);
//		modelKeyword = Normalizer.normalize(modelKeyword);
//		if (modelKeyword != null && models != null && !models.isEmpty()) {
//			for (ItemModel model : models) {
//				if (modelKeyword.matches(model.keywords)) {
//					modelFound = model;
//					break;
//				}
//			}
//		}
//		return modelFound;
//	}
//	
//	private static ItemAttribute versionExists(Item item, String versionKeyword) {
//		ItemVersion versionFound = null;
//		List<ItemVersion> versions = versionDict.get(item);		
//		versionKeyword = Normalizer.normalize(versionKeyword);
//		if (versionKeyword != null && versions != null && !versions.isEmpty()) {
//			for (ItemVersion version : versions) {				
//				if (versionKeyword.matches(version.keywords)) {
//					versionFound =  version;
//					break;
//				}
//			}
//		}
//		return versionFound;
//	}
}
