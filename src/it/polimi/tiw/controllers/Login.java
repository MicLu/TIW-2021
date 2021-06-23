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
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.DatabaseConnection;

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
		
		//Già controllate scadenza aste con filtro
	
		String username = null;
		String password = null;
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String path;
		
		try {
			username = request.getParameter("username");
			password = request.getParameter("password");
			//Verifica che ci siano tutti i camp
			if( username == null || username.isEmpty() ||   
				password == null || password.isEmpty() ) {
				
				path = "/index.html";
				ctx.setVariable("errorMsg", "Campi mancanti");
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Errore di autenticazione");
		}
		
		
				
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.checkUserCredential(username, password);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Non è possibile controllare le credenziali");
			return;
		}
		
		//Se l'utente esiste prepara il template engine e vai alla home
		//altimenti ritorna a index mostrando un messaggio di errore
		if (user == null) {
			path = "/index.html";
			ctx.setVariable("errorMsg", "Incorrect username or password");
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}
		
	}
	
	public void destroy() {
		DatabaseConnection.destroyConnection(connection);
//		try {
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	
}
