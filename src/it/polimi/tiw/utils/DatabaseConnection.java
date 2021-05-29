package it.polimi.tiw.utils;

import java.sql.*;

import javax.servlet.*;


public class DatabaseConnection
{
	
	public static Connection getConnection(ServletContext context) throws ServletException
	{
		Connection connection = null;
		
		try{
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String dbuser = context.getInitParameter("dbUser");
			String dbpass = context.getInitParameter("dbPassword");
			
			Class.forName(driver);
			
			connection = DriverManager.getConnection(url, dbuser, dbpass);
			Debugger.log("Connessione effettuata correttamente");
		}
		catch (ClassNotFoundException e){
			throw new UnavailableException("Impossibile caricare il driver del database");
		}
		catch (SQLException e){
			throw new UnavailableException("Impossibile connettersi al database");
		} 
		
		return connection;
	}
	
	/*
	 * Lancia una query sul database e ritorna il risultato
	 * String query -> La query da eseguire
	 */
	
	public ResultSet query(String query)
	{
		ResultSet res = null;
		Statement stm = null;
		
		
		try {
			
			//stm = connection.createStatement();
			res = stm.executeQuery(query);
			
			return res;
				
		}
		catch(SQLException e)
		{
			Debugger.log("Errore SQL");
		} 
		
		return null;
		
	}
	
	/*
	 * Chiude la connessione con il database
	 */
	public static void destroyConnection(Connection connection){
		try {
			if (connection != null){
				connection.close();
			}
		} catch (SQLException e){
			Debugger.log("Errore nella chiusura della connessione");
		}
	}

}
