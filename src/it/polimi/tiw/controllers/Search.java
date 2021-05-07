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

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.core.DatabaseConnection;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.debugger.Debugger;

/**
 * Servlet implementation class Search
 */
@WebServlet("/Search")
public class Search extends HttpServlet {

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
       
    public Search() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		User user = (User) session.getAttribute("user");
		
		// Redirect to the Home page
		String path = "/templates/home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		//Set user's name to display in the home page
		ctx.setVariable("HelloName", user.getNome() + " " + user.getCognome());
		
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		List<Auction> auctions = new ArrayList<Auction>();
		
		//Get the list of avaiable auction for the logged user
		try {
			auctions = auctionDAO.getAvaibleAuctionByKeyword(user.getUsername(), request.getParameter("keywordSearch")); 
		} catch (SQLException e) {
//			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get auctions");
			//TODO: gestire caso lista vuota
			if(auctions.isEmpty()) {
				Debugger.log("Lista aste vuota");
			}
//			return;
		}
		
		Debugger.log("Cercata la parola: " + request.getParameter("keywordSearch"));
		
		ctx.setVariable("AvaiableAuctions", auctions);
		
		templateEngine.process(path, ctx, response.getWriter());
		
	}
}
