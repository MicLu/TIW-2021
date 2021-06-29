package it.polimi.tiw.controllersJS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.DatabaseConnection;
import it.polimi.tiw.utils.Debugger;

/**
 * Servlet implementation class LoginJS
 */
@WebServlet("/LoginJS")
@MultipartConfig
public class LoginJS extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public void init() throws ServletException {
		connection = DatabaseConnection.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		//TODO: Gi� controllate scadenza aste con filtro
	
		String username = null;
		String password = null;
		
		try {
			username = request.getParameter("username");
			password = request.getParameter("password");
			
			Debugger.log("username: " + username);
			Debugger.log("password: " + password);
			
			//Verifica che ci siano tutti i camp
			if( username == null || username.isEmpty() ||   
				password == null || password.isEmpty() ) {
				
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Credenziali mancanti");
				return;
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Errore Di autenticazione");
		}
		
		
				
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.checkUserCredential(username, password);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Non � possibile controllare le credenziali");
			return;
		}
		
		//Se l'utente esiste imposta l'attributo user e va alla home
		//altimenti ritorna a index mostrando un messaggio di errore
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Incorrect username or password");
		} else {
			request.getSession().setAttribute("user", user);
			response.setStatus(HttpServletResponse.SC_OK);
			//response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(username);
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
