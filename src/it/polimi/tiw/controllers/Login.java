package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.core.DatabaseConnection;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.debugger.Debugger;

import org.thymeleaf.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/Login")
public class Login extends HttpServlet {

private static final long serialVersionUID = 1L;
private Connection connection = null;
private TemplateEngine templateEngine;
	
	//Inizializzo il template engine
	public void init() throws ServletException {
		
		connection = DatabaseConnection.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
	
		String username = null;
		String password = null;
		
		try {
			username = request.getParameterValues("username")[0];
			password = request.getParameterValues("password")[0];
			//Verifica che ci siano tutti i camp
			if( username == null || username.isEmpty() ||   
				password == null || password.isEmpty() ) {
				
				//TODO: gestire il campo mancante
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Campi mancanti");
				Debugger.log("Campi mancanti");
				return;
			}
		} catch (Exception e) {
			
		}
		
		
				
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.checkUserCredential(username, password);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Non � possibile controllare le credenziali");
			return;
		}
		
		//Se l'utente esiste prepara il template engine e vai alla home
		//altimenti ritorna a index mostrando un messaggio di errore
		String path;
		if (user == null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Incorrect username or password");
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}
		
//		Boolean logged = false;
		
//		try {
//			logged = login(username, password, context);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		
//		if (logged)
//		{
//			out.println("Logged OK");
//		} 
//		else 
//		{
//			out.println("Logged NO");
//		}
		
	}
	
	public void destroy() {
		DatabaseConnection.destroyConnection(connection);
//		try {
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
//	Boolean login(String username, String password, ServletContext context) throws SQLException, ServletException
//	{
//		
//		StringBuilder sql = new StringBuilder();
//		sql.append("select email from utenti where username = '");
//		sql.append(username);
//		sql.append("' and password = '");
//		sql.append(password);
//		sql.append("';");
//		
//		Debugger.log(sql.toString());
//		
//		DatabaseConnection db = new DatabaseConnection(context);
//		ResultSet res = db.query(sql.toString());
//		
//		// TODO: Ottenere i dati dell'utente e salvarli da qualche parte
//		
//		return res.next();
//	}
	
	
}
