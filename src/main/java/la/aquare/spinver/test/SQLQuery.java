package la.aquare.spinver.test;

import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;

import la.aquare.spinver.Post;
import la.aquare.spinver.sql.SQLConn;

import org.junit.Before;
import org.junit.Test;

public class SQLQuery {
	Post post;
	
	@Before
	public void setup(){
		 //empty
	}
	
	@Test
	public void Positive() {
		boolean queryStatus = false;
		ResultSet rs = SQLConn.performCursorQuery(
				"SELECT id_facebook_post, description " +
				"FROM facebook_post", 100);
		
		try {
			if (rs != null) {
				while (rs.next()) {
					queryStatus = true;
					String postText = rs.getString("description");
					Post.process(postText);
				}				
			}
			else {
				System.out.println("[" + this.getClass().getSimpleName() + "] " + "ResultSet is null");
			}
		} catch (SQLException ex) {
			SQLConn.setSQLError(ex);
		}
		finally {
			if (rs != null) {
				try {
					rs.getStatement().close(); //encerra ambos ResultSet e Statement
				}
				catch (SQLException ex) {
					SQLConn.setSQLError(ex);
				}
			}
		}
		if (SQLConn.getSQLError() != null) {
			System.out.println("[" + this.getClass().getSimpleName() + "] " +
								"[SQL ERROR]: " + SQLConn.getSQLError()
								);
		}
		assertTrue(queryStatus);
	}
}
