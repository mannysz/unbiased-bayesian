package la.aquare.spinver.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexão ao banco MySQL via driver JDBC.
 */
public final class SQLConn {
	static final String DRIVER_NAME = "com.mysql.jdbc.Driver";

	private static String sqlError = null;
	private static Connection conn = null;
	private static String JDBC_URI = null;
	
	private SQLConn() {
		//empty
	}
	
    private static Connection getConnection() {
    	return conn;
    }

    public static void setupConnection(String host, String db, String user, String passwd) {
    	if (host == null || db == null || user == null || passwd == null) {
    		return;
    	}
    	try {
        	if (conn != null && !conn.isClosed()) {
        		return;
        	}
    	} catch (SQLException e) {
    		setSQLError(e);
    	}    	
    	
    	JDBC_URI = "jdbc:mysql://" +
    				host +			 		 //DB server
					"/" + db +				 //DB name
					"?user=" + user +		 //DB user name
					"&password=" + passwd;   //DB user password
    	
    	try {
			Class.forName(DRIVER_NAME).newInstance();
			conn = DriverManager.getConnection(JDBC_URI);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}    	
    }
    
    public static void closeConnection() {
    	if (conn != null) {
    		try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public static void setAutoCommit(boolean flag) {
    	if (getConnection() != null) {
    		try {
				conn.setAutoCommit(flag);
			} catch (SQLException ex) {
				setSQLError(ex);
			}
    	}
    }
    
    public static void commit() {
    	if (getConnection() != null) {
    		try {
				conn.commit();
			} catch (SQLException ex) {
				setSQLError(ex);
			}
    	}
    }
    
    public static void rollback() {
    	if (getConnection() != null) {
    		try {
    			conn.rollback();
			} catch (SQLException ex) {
				setSQLError(ex);
			}
    	}
    }
    
    public static ResultSet performUpdateAndReturn(String sqlQuery) {
    	ResultSet rs = null;
    	int rowsUpdated = 0;
    	
    	if (getConnection() != null) {
//    		PreparedStatement stat = null;
    		Statement stat = null;
    		
    		try {
//    			stat = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
    			stat = conn.createStatement();
    			rowsUpdated = stat.executeUpdate(sqlQuery, Statement.RETURN_GENERATED_KEYS);
    		} catch (SQLException ex) {
    			setSQLError(ex);
    		}
        	if (rowsUpdated > 0) {
        		try {
        			rs = stat.getGeneratedKeys();
        		} catch (SQLException ex) {
        			setSQLError(ex);
        		}
        		
        	}
    	}
    	return rs;
    }
    
    public static int performUpdate(String sqlQuery) {
    	int rowsUpdated = 0;
    	
    	if (getConnection() != null) {
    		Statement stat = null;
    		
    		try {
    			stat = conn.createStatement();
    			rowsUpdated = stat.executeUpdate(sqlQuery);
    			stat.close();
    		} catch (SQLException ex) {
    			setSQLError(ex);
    		}
    		finally {
    			//empty
    		}
    	}
    	return rowsUpdated;
    }
    
    public static int performUpdate(String[] sqlQueries) {
    	int[] rowsUpdatedArray = null;
    	int rowsUpdated = 0;
    	
    	if (getConnection() != null) {
    		Statement stat = null;
    		
    		try {
    			stat = conn.createStatement();
    			for (String sqlQuery : sqlQueries) {
    				stat.addBatch(sqlQuery);
    			}
    			rowsUpdatedArray = stat.executeBatch();
    			stat.close();
    		} catch (SQLException ex) {
    			setSQLError(ex);
    		}
    		finally {
    			//empty
    		}
    	}
    	if (rowsUpdatedArray != null) {
    		for (int updateCount : rowsUpdatedArray) {
    			rowsUpdated = rowsUpdated + updateCount;
    		}
    	}
    	return rowsUpdated;
    }
    
    public static ResultSet performQuery(String sqlQuery) {
//    	System.out.println("[" + LogFormatter.getNow() + "] sqlQuery = " + sqlQuery);
    	ResultSet rs = null;
    	sqlError = null;
    	
        if(getConnection() != null) {
        	Statement stat = null;

        	try {
        		stat = conn.createStatement();
        		stat.setFetchSize(100);
        		rs = stat.executeQuery(sqlQuery);
        	}
        	catch (SQLException ex){
        		setSQLError(ex);
        	}
        }
        return rs;
    }
    
    public static ResultSet performCursorQuery(String sqlQuery, int rows) {
//    	System.out.println("[" + LogFormatter.getNow() + "] sqlQuery = " + sqlQuery);
    	ResultSet rs = null;
    	sqlError = null;
    	
        if(getConnection() != null) {
        	Statement stat = null;
        	try {
        		conn.setAutoCommit(false);        		
        		stat = conn.createStatement();
        		stat.setFetchSize(rows);
        		rs = stat.executeQuery(sqlQuery);
        	}
        	catch (SQLException ex){
        		setSQLError(ex);
        	}
        }
        return rs;    	
    }
    
    public static void setSQLError(Exception ex) {
    	if(ex instanceof SQLException) {
    		SQLException sqlEx = (SQLException)ex;
    		sqlError = "SQLException: " + sqlEx.getMessage() + "\n"
    				+ "SQLState: " + sqlEx.getSQLState() + "\n"
    				+ "VendorError: " + sqlEx.getErrorCode();
    	}
    	else {
    		sqlError = ex.getMessage();
    	}
    }

    public static String getSQLError() {
    	return (conn == null?"Connnection is null (is connection established?)":sqlError);
    }    

}
