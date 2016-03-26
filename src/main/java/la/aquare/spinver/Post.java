package la.aquare.spinver;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import la.aquare.spinver.lang.Matcher;
import la.aquare.spinver.lang.Normalizer;
import la.aquare.spinver.lang.SentenceDetector;
import la.aquare.spinver.lang.dict.Actions;
import la.aquare.spinver.lang.dict.Actions.Action;
import la.aquare.spinver.lang.dict.ItemModel;
import la.aquare.spinver.log.SpinverLogger;
import la.aquare.spinver.sql.SQLConn;

public final class Post {	

	private Post() {
		//empty
	}

	
	public static Action processTest(String postText) {
		return processTest(-1, postText);
	}
	
	public static Action processTest(int postId, String postText) {		
		int numOfMatches = 0;
		List<Result> allResults = process(postId, postText);
		numOfMatches = (allResults != null && !allResults.isEmpty() ? allResults.size() : 0);
		switch (numOfMatches) {
			case 0:
				return Action.NONE;				
			case 1:
				return allResults.get(0).action;				
			default:
				return Action.BOTH;
		}
	}

	public static List<Result> process(String postText) {
		return process(-1, postText);
	}
	
	/**
	 * Retorna se um {@link Result}, no qual, entre outras coisas, informa
	 * se o texto informado do post é uma compra (BUY), uma venda (SELL),
	 * ambos (BOTH) ou nenhum destes (NONE).
	 * 
	 * @param postId id do post coletado
	 * @param postText conte&uacute;do textual do post coletado
	 * @return
	 */
	public static List<Result> process(int postId, String postText) {
//		int numOfMatches = 0;
		List<Result> allResults = new ArrayList<Result>();
		try {
			String text = Normalizer.normalizeText(postText);
			for (String sentence : SentenceDetector.detect(text)) {
				List<Result> results = new ArrayList<Result>();
				short lastStatus = 0;
				if (!(results = Matcher.matchAction(sentence, Actions.Action.BOTH)).isEmpty()) {
					SpinverLogger.log(results + " (status: " + Matcher.STATUS + ") " +
								(postId > -1 ? "[" + postId + "] " : "") + sentence);
					allResults.addAll(results);
				}
				else {
					lastStatus = Matcher.STATUS;
					
					if (!(results = Matcher.matchAction(sentence, Actions.Action.BUY)).isEmpty()) {
						SpinverLogger.log(results + " (status: " + Matcher.STATUS + ") " + 
									(postId > -1 ? "[" + postId + "] " : "") + sentence);
						allResults.addAll(results);
					}
					lastStatus = lastStatus > Matcher.STATUS ? lastStatus : Matcher.STATUS;
					
					if (!(results = Matcher.matchAction(sentence, Actions.Action.SALE)).isEmpty()) {
						lastStatus = lastStatus > Matcher.STATUS ? lastStatus : Matcher.STATUS;
						SpinverLogger.log(results + " (status: " + Matcher.STATUS + ") " +
								(postId > -1 ? "[" + postId + "] " : "") + sentence);
						allResults.addAll(results);
					}
					lastStatus = lastStatus > Matcher.STATUS ? lastStatus : Matcher.STATUS;
				}
				if (allResults.isEmpty()) {
					SpinverLogger.logRejected("[" + Action.NONE + "] " + "(status: " + lastStatus + ") " +
								(postId > -1 ? "[" + postId + "] " : "") + sentence);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return allResults;
	}
	
	public static boolean hasAlreadyOffered(Result result, PostMetadata metadata) {
		boolean isOffered = false;
		int versionId = (result.version==null?0:result.version.id);
		
		//XXX: informação de data recebida do JSON armazenado em banco está em GMT-0.
		//Mantendo o fuso horário GMT-0.
	    String createdTimeFormated = null;
	    SimpleDateFormat inFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
	    try {
	      Date d2 = inFormatter.parse(metadata.createdTime);
	      SimpleDateFormat outFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      outFormatter.setTimeZone(TimeZone.getTimeZone("GMT-0"));
	      createdTimeFormated = outFormatter.format(d2);
	    } catch (ParseException e) {
	      e.printStackTrace();
	    }

	    //verifica se um dado anunciante publicou o produto num mesmo dia
	    String sqlQuery = "SELECT a.id_offer_product FROM offer_product a "
	            + "INNER JOIN offer b ON a.id_offer = b.id_offer "
	            + "WHERE b.id_advertiser = "  + metadata.advertiserId + " AND "
	            + "a.id_product = " + result.item.id + " AND "
	            + "a.id_product_version = " + versionId + " AND "
	            + "ABS(TIMESTAMPDIFF("
	            + "DAY, STR_TO_DATE('" + createdTimeFormated + "', '%Y-%m-%d'), "
	            + "DATE_FORMAT(date_offer, '%Y-%m-%d'))"
	            + ") = 0";
		
		ResultSet rs = SQLConn.performQuery(sqlQuery);
		if (rs != null) {
			try {
				isOffered = rs.next();
				rs.close();
			} catch (SQLException e) {
				SQLConn.setSQLError(e);
			}
			finally {
				//empty
			}
		}
		return isOffered;
	}

	public static boolean writeOffer(Result result, String content, PostMetadata metadata) {
		boolean writeOK = false;
		
		//XXX: informação de data recebida do JSON armazenado em banco está em GMT-0.
		//Mantendo o fuso horário GMT-0.
		String createdTimeFormated = null;
		SimpleDateFormat inFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		try {
			Date d2 = inFormatter.parse(metadata.createdTime);
			SimpleDateFormat outFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			outFormatter.setTimeZone(TimeZone.getTimeZone("GMT-0"));
			createdTimeFormated = outFormatter.format(d2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//TODO: por enquanto, gerando uma oferta somente para BUY,
		//alterar o código para gerar também uma oferta para SALE
		if (result.action == Actions.Action.BOTH) {
			result.action = Actions.Action.BUY;
		}
		String sqlInsertOffer = "INSERT INTO "
				+ "offer (product, description, date_offer, "
				+ "picture, finality, external_id, external_link, id_advertiser) "
				+ "VALUES ('"
				+ result.getFullItemName() + "','"
				+ content.replace("\\", "").replaceAll("\'", "\\\\\'") + "',"
				+ "STR_TO_DATE('" + createdTimeFormated + "','%Y-%m-%d %H:%i:%s')" + ","
				+ (metadata.picture==null?"NULL":"'" + metadata.picture + "'") + ",'"
				+ result.action.toString().toLowerCase() + "','"
				+ metadata.externalId + "',"
				+ (metadata.externalLink==null?"NULL":"'" + metadata.externalLink + "'") + ","
				+ metadata.advertiserId
				+ ")";
		SQLConn.setAutoCommit(false);
		ResultSet rs = SQLConn.performUpdateAndReturn(sqlInsertOffer);
		int rowsUpdated = 0;
		if (rs != null) {
			try {
				if (rs.next()) {
					String models = "";
					if (!result.models.isEmpty()) {
						for (ItemModel model : result.models) {
							models = models + "\"" + model.id + "\"" + ",";
						}
						models = "[" + models.substring(0, models.length()-1) + "]";
					}
					String sqlInsertOfferProduct = "INSERT INTO "
							+ "offer_product (id_offer, id_product, id_product_version, models) "
							+ "VALUES ("
							+ rs.getInt(1) + ","
							+ result.item.id + ","
							//XXX: coluna id_product_version é não nula
							//atribuí 0 para casos nulos após realizar a seguinte consulta
							//select count(*) from offer_product where id_product_version = 0
							+ (result.version==null?"0":result.version.id) + ",'"
							+ models + "'"
							+ ")";
					String sqlUpdate = "UPDATE facebook_post "
							+ "SET analyzed = DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'), "
							+ "id_offer = " + rs.getInt(1) + " "							
							+ "WHERE "
							+ "id_facebook_post = " + metadata.postId;

					rowsUpdated =
							SQLConn.performUpdate(new String[]{sqlInsertOfferProduct, sqlUpdate}) + 1;					
				}
				rs.close();
			} catch (SQLException e) {
				SQLConn.setSQLError(e);
			} finally {
				//empty
			}			
		}
		if (rowsUpdated >= 3) {
			writeOK = true;
			SQLConn.commit();
		}
		else {
			System.out.println("FAIL, rowsUpdated = " + rowsUpdated);
			System.out.println("FAIL, sqlInsertOffer = " + sqlInsertOffer);
			SQLConn.rollback();
		}
		SQLConn.setAutoCommit(true);
		return writeOK;
	}
	
	public static boolean writeDuplicatedOffer(Result result, PostMetadata metadata) {
		int rowsUpdated = 0;
		String sqlUpdate = "UPDATE facebook_post "
				+ "SET analyzed = DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'), "
				+ "analyzed_info = 'duplicate_post#" + metadata.postId + "', "
				+ "id_offer = null "							
				+ "WHERE "
				+ "id_facebook_post = " + metadata.postId;
		rowsUpdated = SQLConn.performUpdate(sqlUpdate);
		return rowsUpdated > 0;
	}
	
	public static boolean markAsAnalyzedOnly(PostMetadata metadata) {
		int rowsUpdated = 0;
		String sqlUpdate = "UPDATE facebook_post "
				+ "SET analyzed = DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s') "
				+ "WHERE "
				+ "id_facebook_post = " + metadata.postId;
		rowsUpdated = SQLConn.performUpdate(sqlUpdate);
		return rowsUpdated > 0;
	}

}
