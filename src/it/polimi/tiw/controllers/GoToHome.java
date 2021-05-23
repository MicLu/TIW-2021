package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.core.DatabaseConnection;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.debugger.Debugger;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Auction;

/**
 * Servlet implementation class GoToHome
 */
@WebServlet("/Home")
public class GoToHome extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public GoToHome() {
        super();
    }

    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = DatabaseConnection.getConnection(getServletContext());
	}
    
        
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		//Già controllato il login dell'utente con filtro
		//Già controllate scadenza aste con filtro
		
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		// Redirect to the Home page
		String path = "/templates/home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		List<Auction> auctions = new ArrayList<Auction>();
		
		//Get the list of avaiable auction for the logged user
		try {
			auctions = auctionDAO.getAvaibleAuction(user.getUsername()); 
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get auctions");
			return;
		}
		
		if(auctions.isEmpty()) {
			Debugger.log("Lista aste consultabili vuota");
		}
		
		ctx.setVariable("AvaiableAuctions", auctions);
		
		
		templateEngine.process(path, ctx, response.getWriter());
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}
	
	public void destroy() {
		DatabaseConnection.destroyConnection(connection);
//		try {
//			DatabaseConnection.destroyConnection(connection);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	

}