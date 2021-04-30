package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;


public class UserDAO {
	
	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public User checkUserCredential(String username, String password) throws SQLException {
		
		String query = "SELECT  * FROM utenti  WHERE username = ? AND password =?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results
					return null;
				else {
					result.next();
					User user = new User();
					user.setEmail(result.getString("email"));
					user.setUsername(result.getString("username"));
					user.setNome(result.getString("nome"));
					user.setCognome(result.getString("cognome"));
					user.setPassword(result.getString("password"));
					user.setIndirizzo(result.getString("indirizzo"));
					
					return user;
				}
			}
		}
		
	}
	
	public User getUserByUsername(String username) throws SQLException {
		
		String query = "SELECT  * FROM utenti  WHERE username = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results
					return null;
				else {
					result.next();
					User user = new User();
					user.setEmail(result.getString("email"));
					user.setUsername(result.getString("username"));
					user.setNome(result.getString("nome"));
					user.setCognome(result.getString("cognome"));
					user.setPassword(result.getString("password"));
					user.setIndirizzo(result.getString("indirizzo"));
					
					return user;
				}
			}
		}
		
	}
	
	

}
