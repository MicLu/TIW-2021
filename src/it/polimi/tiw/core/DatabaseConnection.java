package it.polimi.tiw.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;


import org.json.simple.*;

import it.polimi.tiw.debugger.Debugger;


public class Database
{
	
	private ServletContext context;
	private Connection connection = null;
	
	public Database(ServletContext context) throws ServletException
	{
		
		try
		{
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String dbuser = context.getInitParameter("dbUser");
			String dbpass = context.getInitParameter("dbPassword");
			
			Class.forName(driver);
			
			connection = DriverManager.getConnection(url, dbuser, dbpass);
			Debugger.log("Connessione effettuata correttamente");
		}
		catch (ClassNotFoundException e)
		{
			throw new UnavailableException("Impossibile caricare il driver del database");
		}
		catch (SQLException e)
		{
			throw new UnavailableException("Impossibile connettersi al database");
		} 
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
			
			stm = connection.createStatement();
			res = stm.executeQuery(query);
			
			return res;
				
		}
		catch(SQLException e)
		{
			Debugger.log("Errore SQL");
		} 
		
		return null;
		
	}
	
	
	// Di esempio, può anche essere fatto nella servlet
	public String getPerson()
	{
		String res = null;
		String jsonText = null;
		
		String sql = "SELECT firstname, lastname FROM persons";
		
		ResultSet resset = this.query(sql);
		
		JSONObject json = new JSONObject();
		
		try {
			int i = 0;
			while (resset.next())
			{
				JSONObject row = new JSONObject();
				//persons.add(new Person(resset.getString("firstname"), resset.getString("lastname")));
				row.put("nome", resset.getString("firstname"));
				row.put("cognome", resset.getString("lastname"));
				
				json.put(i, row);
				i++;
				
			}
			
			StringWriter out = new StringWriter();
			try {
				json.writeJSONString(out);
				jsonText = out.toString();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonText;
		
		
		
	}
	
	/*
	 * Chiude la connessione con il database
	 */
	public void destroyConnection()
	{
		try {
			if (connection != null)
			{
				connection.close();
			}
		} catch (SQLException e)
		{
			Debugger.log("Errore nella chiusura della connessione");
		}
	}

}
