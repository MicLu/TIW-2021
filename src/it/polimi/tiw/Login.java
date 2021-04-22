package it.polimi.tiw;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.core.Database;
import it.polimi.tiw.debugger.Debugger;

@WebServlet("/Login")
public class Login extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.setContentType("text/plain");
		
		PrintWriter out = response.getWriter();
		
		ServletContext context = getServletContext();
		
		
		
		String username = request.getParameterValues("username")[0];
		String password = request.getParameterValues("password")[0];
		
		Boolean logged = false;
		
		try {
			logged = login(username, password, context);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if (logged)
		{
			out.println("Logged OK");
		} 
		else 
		{
			out.println("Logged NO");
		}
		
	}
	
	Boolean login(String username, String password, ServletContext context) throws SQLException, ServletException
	{
		
		StringBuilder sql = new StringBuilder();
		sql.append("select email from utenti where username = '");
		sql.append(username);
		sql.append("' and password = '");
		sql.append(password);
		sql.append("';");
		
		Debugger.log(sql.toString());
		
		Database db = new Database(context);
		ResultSet res = db.query(sql.toString());
		
		// TODO: Ottenere i dati dell'utente e salvarli da qualche parte
		
		return res.next();
	}
	
}
