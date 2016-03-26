package la.aquare.spinver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import la.aquare.spinver.exception.DictNotFoundException;
import la.aquare.spinver.lang.Matcher;
import la.aquare.spinver.lang.Normalizer;
import la.aquare.spinver.lang.SentenceDetector;
import la.aquare.spinver.lang.Tokenizer;
import la.aquare.spinver.lang.dict.Actions;
import la.aquare.spinver.lang.dict.Actions.Action;
import la.aquare.spinver.lang.dict.Item;
import la.aquare.spinver.lang.dict.ItemAttributes;
import la.aquare.spinver.lang.dict.ItemModel;
import la.aquare.spinver.lang.dict.ItemVersion;
import la.aquare.spinver.lang.dict.Itens;
import la.aquare.spinver.log.LogFormatter;
import la.aquare.spinver.log.SpinverLogger;
import la.aquare.spinver.sql.SQLConn;
import la.aquare.spinver.sql.SQLDataHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
	public static boolean interactiveMode = false;
	public static boolean dbPersistence = false;
	public static long dbCheckingTimeout = 2; //em segundos
	private static String itensDictFilename = null;
	private static String modelsDictFilename = null;
	private static String versionsDictFilename = null;
	
	/**
	 * Idade máxima dos 'posts' a serem processados pelo Spinver 
	 * (em dias). Default: 2 dias. 
	 */
	private static int spinverInterval = 2;
	
	private static void loadConfig() throws IOException {
		Properties connProps = new Properties();
		connProps.load(new FileInputStream(new File("app.properties")));
    	
		SQLDataHolder.host = connProps.getProperty("spinver.mysql.host");
    	SQLDataHolder.db = connProps.getProperty("spinver.mysql.dbname");
    	SQLDataHolder.user = connProps.getProperty("spinver.mysql.user");
    	SQLDataHolder.password = connProps.getProperty("spinver.mysql.password");
    	SQLDataHolder.sourceDataTable = 
    			connProps.getProperty("spinver.mysql.source.table", "facebook_post");
    	
		String logFileDir = connProps.getProperty("spinver.logdir", System.getProperty("java.io.tmpdir"));
		itensDictFilename = connProps.getProperty("spinver.itens.dict");
		modelsDictFilename = connProps.getProperty("spinver.models.dict");
		versionsDictFilename = connProps.getProperty("spinver.versions.dict");
		
		String interval= connProps.getProperty("spinver.interval");		
		if (interval != null) {
			try {
				spinverInterval = Integer.valueOf(interval);
			} catch (NumberFormatException e) {
				//empty
			}			 
		}
		
		String checkingTimeout = connProps.getProperty("spinver.timeout");		
		if (interval != null) {
			try {
				dbCheckingTimeout = Long.valueOf(checkingTimeout);
			} catch (NumberFormatException e) {
				//empty
			}			 
		}
		
		String mode = connProps.getProperty("spinver.interactive", "false");		
		if (mode.equalsIgnoreCase("true")) {
			interactiveMode = true;			
		}
		boolean logToFile = !interactiveMode;
		
		String persistence = connProps.getProperty("spinver.dbpersistence", "false");		
		if (persistence.equalsIgnoreCase("true")) {
			dbPersistence = true &&
					(itensDictFilename == null && 
					modelsDictFilename == null && 
					versionsDictFilename == null
					);			
		}
		
		SpinverLogger.init(logFileDir, logToFile);
	}
	
	private static void setupDBConnection() {
		SpinverLogger.logToScreen("Connecting to spinver DB in " + SQLDataHolder.host);
    	SQLConn.setupConnection(SQLDataHolder.host, SQLDataHolder.db, SQLDataHolder.user, SQLDataHolder.password);
	}
	
	private static Map<Item, List<String>> getItensDict() throws IOException {
		Map<Item, List<String>> itensDict = null;
		
		if (itensDictFilename == null) {
			setupDBConnection();
			SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] Loading products dict from DB...");
			itensDict = Itens.createDict();
		}
		else {
			SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] Loading products dict from local file...");
			itensDict = Itens.createDict(itensDictFilename);
		}
		return itensDict;
	}
	
	public static Map<Item, List<ItemModel>> getModelsDict() throws IOException {
		if (modelsDictFilename == null) {
			setupDBConnection();
			SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] Loading models dict from DB...");
			ItemAttributes.createDict(ItemAttributes.TYPE.MODEL);
		}
		else {
			SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] Loading models dict from local file...");
			ItemAttributes.createDict(modelsDictFilename, ItemAttributes.TYPE.MODEL);
		}
		return ItemAttributes.getModelsDict();
	}
	
	public static Map<Item, List<ItemVersion>> getVersionsDict() throws IOException {
		if (versionsDictFilename == null) {
			setupDBConnection();
			SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] Loading versions dict from DB...");
			ItemAttributes.createDict(ItemAttributes.TYPE.VERSION);
		}
		else {
			SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] Loading versions dict from local file...");
			ItemAttributes.createDict(versionsDictFilename, ItemAttributes.TYPE.VERSION);
		}		
		return ItemAttributes.getVersionsDict();
	}
	
	public static void main(String[] args) {
		try {
			SentenceDetector.start();
			Tokenizer.start();
			loadConfig();
			if (getItensDict() == null) {
				throw new DictNotFoundException("Dictionary of products is null.");
			}
			if (getModelsDict() == null) {
				throw new DictNotFoundException("Dictionary of product models is null.");
			}
			if (getVersionsDict() == null) {
				throw new DictNotFoundException("Dictionary of product versions is null.");
			}
//			ItemAttributes.printDicts(ItemAttributes.TYPE.MODEL);
//			System.out.println("==================================");
//			ItemAttributes.printDicts(ItemAttributes.TYPE.VERSION);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		catch (DictNotFoundException ex) {
			ex.printStackTrace();
			return;
		}
		Thread runtimeHookThread = new Thread() {
		    @Override
		    public void run() {
		        shutdownHook();
		    }
		};
		Runtime.getRuntime().addShutdownHook(runtimeHookThread);
		
		if (interactiveMode) {
			interactiveMode(args);
		}
		else {
			setupDBConnection();
			
			SpinverLogger.logToScreen("[" + LogFormatter.getNow() + 
							"] Starting non-interactive execution " +
							"with" + (dbPersistence?"":"out") + " DB persistence " +
							"(logfile: " +
							SpinverLogger.getLogFileName()  + "*.log)");
			
			Object lock = new Object();
			(new Thread(new DBUpdateListener(lock))).start();
			(new Thread(new Analyzer(lock, spinverInterval))).start();
		}
	}
	
    /**
     * "Hook" chamado quando a aplicação é desligada.
     */
    private static void shutdownHook() {
    	SpinverLogger.logToScreen("");
    	SpinverLogger.logToScreen("Application is shutting down...");
    	try {
    		SentenceDetector.stop();
    		Tokenizer.stop();
    	} catch (IOException e) {
    		//empty
    	}
    	SQLConn.closeConnection();
    	SpinverLogger.logToScreen("Done.");
    }
	
	public static void interactiveMode(String[] args) {
		if (args.length == 0) {
			SpinverLogger.logToScreen("Nenhuma frase foi informada!");
			return;
		}		
		try {
			String text = Normalizer.normalizeText(args[0]);
			SpinverLogger.logToScreen("texto de entrada: " + text);
			SpinverLogger.logToScreen("");			
			for (String sentence : SentenceDetector.detect(text)) {
				List<Result> allResults = new ArrayList<Result>();
				List<Result> results = new ArrayList<Result>();
				short lastStatus = 0;
				if (!(results = Matcher.matchAction(sentence, Actions.Action.BOTH)).isEmpty()) {					
					SpinverLogger.logToScreen(results + " (status: " + Matcher.STATUS + ")" + " " + sentence);
					allResults.addAll(results);
				}
				else {
					lastStatus = Matcher.STATUS;

					if (!(results = Matcher.matchAction(sentence, Actions.Action.BUY)).isEmpty()) {
						SpinverLogger.logToScreen(results + " (status: " + Matcher.STATUS + ")" + " " + sentence);
						allResults.addAll(results);
					}
					lastStatus = lastStatus > Matcher.STATUS ? lastStatus : Matcher.STATUS;

					if (!(results = Matcher.matchAction(sentence, Actions.Action.SALE)).isEmpty()) {
						lastStatus = lastStatus > Matcher.STATUS ? lastStatus : Matcher.STATUS;
						SpinverLogger.logToScreen(results + " (status: " + Matcher.STATUS + ")" + " " + sentence);
						allResults.addAll(results);
					}
					lastStatus = lastStatus > Matcher.STATUS ? lastStatus : Matcher.STATUS;
				}
				if (allResults.isEmpty()) {
					SpinverLogger.logToScreen("[" + Action.NONE + "] " + "(status: " + lastStatus + ") " + sentence);
				}
			}
		}
		catch (IOException ex) {
			SpinverLogger.logToScreen(ex);
		}
	}

}


class DBUpdateListener implements Runnable {
	Object lock_;
	String lastChecksum = "";
	static long TIMEOUT_IN_SECONDS = Main.dbCheckingTimeout;
	
	public DBUpdateListener(Object lock) {
		lock_ = lock;
	}
	
	public void run() {
		while (true) {
			if (hasNewData()) {
				SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] [" +
									getClass().getSimpleName() + "] ** NEW DATA **");
				synchronized(lock_) {
					lock_.notify();
					try {
						lock_.wait();
					} catch (InterruptedException e) {
						SpinverLogger.logToScreen(e);
					}
				}
			}
			SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] [" +
								getClass().getSimpleName() +
								"] ** WAIT " + TIMEOUT_IN_SECONDS + "s **");
			try {
				Thread.sleep(TIMEOUT_IN_SECONDS*1000);
			} catch (InterruptedException e) {
				SpinverLogger.logToScreen(e);
			}
			finally {
				//empty
			}
		}
	}
	
	private boolean hasNewData() {
		boolean isUpdated = false;
		ResultSet rs = SQLConn.performQuery(
					"checksum table " +
					SQLDataHolder.db + "." + SQLDataHolder.sourceDataTable
					);
		if (rs != null) {
			try {
				if (rs.next()) {
					String checksum = rs.getString(2);
					isUpdated = !lastChecksum.equals(checksum);
					lastChecksum = checksum;
				}
				rs.close();
			} catch (SQLException e) {
				SpinverLogger.logToScreen(e);
			}
			finally {
				//empty
			}
		}
		return isUpdated;				
	}	
}

class Analyzer implements Runnable {
	Object lock_;
	int lastPostId = -1;
	int spinverInterval;
	
	public Analyzer(Object lock, int interval) {
		lock_ = lock;
		spinverInterval = interval;
	}
	
	private Map<String,String> filterMetadata(String jsonContent) {
		Map<String,String> dataMap = new HashMap<String,String>(); 
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(jsonContent);
			
			JsonNode node = null;
			node = rootNode.path("created_time");
			dataMap.put("createdTime", node.textValue());
			node = rootNode.path("picture");
			dataMap.put("picture", node.textValue());
			node = rootNode.path("link");
			dataMap.put("externalLink", node.textValue());
		} catch (JsonProcessingException e) {
			//empty
		} catch (IOException e) {
			//empty
		}			
		return dataMap;
	}
	
	public void run() {
		try {
			synchronized(lock_) {
				lock_.wait();
			}
		} catch (InterruptedException e) {
			SpinverLogger.logToScreen(e);
			return;
		}
		while (true) {
			SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] [" +
								getClass().getSimpleName() + "] Retrieving new posts...");
			String sqlQuery = "SELECT " +
								"a.id_facebook_post AS postId, a.description AS postContent, " +
								"a.id_advertiser AS advertiserId, a.data as jsonRawData, "+
								"a.externo_id AS externoId " +
								"FROM " + SQLDataHolder.sourceDataTable + " a " +
								"INNER JOIN advertiser b on a.id_advertiser = b.id_advertiser " +
								"WHERE " +
//								"a.id_facebook_post = 14574 AND " +
//								"a.id_facebook_post > 12000 AND " +
//								"a.id_facebook_post < 12500 AND " +
//								"a.id_facebook_post IN (13040, 13041) AND " +
								"a.analyzed IS NULL AND " +								
								"a.created >= CURDATE() - INTERVAL " + spinverInterval + " DAY AND " +
								"b.locale = 'pt_BR'";

			//XXX: SQLConn.performQueryCursor(...) NÃO funciona neste esquema
			ResultSet rs = SQLConn.performQuery(sqlQuery);
			
			if (rs != null) {
				try {
					SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] Analyzing...");
					while (rs.next()) {
						lastPostId = rs.getInt("postId");
						int advertiserId = rs.getInt("advertiserId");
						String postText = rs.getString("postContent");
						String externoId = rs.getString("externoId");
						String jsonRawData = rs.getString("jsonRawData");
						
						//persistência no banco de dados (se ativada)
						List<Result> results = Post.process(lastPostId, postText);
						if (Main.dbPersistence) {
							PostMetadata metadata = new PostMetadata();
							metadata.postId = lastPostId;
							metadata.advertiserId = advertiserId;
							metadata.externalId = "facebook#" + externoId;
							
							Map<String, String> dataMap = filterMetadata(jsonRawData);
							metadata.externalLink = dataMap.get("externalLink");
							metadata.picture = dataMap.get("picture");
							metadata.createdTime = dataMap.get("createdTime");

							if (results == null || results.isEmpty()) {
								boolean writeStatus = Post.markAsAnalyzedOnly(metadata);
								if (!writeStatus) {
									SpinverLogger.logToScreen("[" + LogFormatter.getNow() +
											"] Error on marking as analyzed only, post id = " + lastPostId);
									if (SQLConn.getSQLError() != null) {
										SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] SQL ERROR: " + SQLConn.getSQLError());
									}
								}
							}
							else {
								for (Result result : results) {
									if (Post.hasAlreadyOffered(result, metadata)) {
										SpinverLogger.logToScreen("[" + LogFormatter.getNow() +
												"] duplicated post with id " + lastPostId +
												", advertiser: " + metadata.advertiserId);
										if (!Post.writeDuplicatedOffer(result, metadata)) {
											SpinverLogger.logToScreen("[" + LogFormatter.getNow() +
													"] FAIL to write duplicated");										
										}
									} 
									else {
										boolean writeStatus = Post.writeOffer(result, postText, metadata);
										if (!writeStatus) {
											SpinverLogger.logToScreen("[" + LogFormatter.getNow() +
																"] Error on writing offer, post id = " + lastPostId);
											if (SQLConn.getSQLError() != null) {
												SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] SQL ERROR: " + SQLConn.getSQLError());
											}
										}
									}
								}	
							}							
						}
					}
					rs.close();
				} catch (SQLException e) {
//					SQLConn.setSQLError(e);
					SpinverLogger.logToScreen(e);
				}
				finally {
					//empty
				}
//				if (SQLConn.getSQLError() != null) {
//					SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] SQL ERROR: " + SQLConn.getSQLError());
//				}
			}			
			SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] [" +
								getClass().getSimpleName() + "] lastPostId = " + lastPostId);
			SpinverLogger.logToScreen("[" + LogFormatter.getNow() + "] Done.");
			synchronized(lock_) {
				lock_.notify();
				try {
					lock_.wait();
				} catch (InterruptedException e) {
					SpinverLogger.logToScreen(e);
				}
				finally {
					//empty
				}
			}
		}
	}
}
