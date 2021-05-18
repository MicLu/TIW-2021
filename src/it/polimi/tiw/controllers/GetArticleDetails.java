package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import it.polimi.tiw.beans.Offer;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.core.DatabaseConnection;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.dao.OfferDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.debugger.Debugger;

/**
 * Servlet implementation class GetAuctionDetails
 */
@WebServlet("/GetArticleDetails")
public class GetArticleDetails extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public GetArticleDetails() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";

		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		User loggedUser = (User) session.getAttribute("user");
		
		//Get auctionId
		Integer auctionId = null;
		
		try {
			auctionId = Integer.parseInt(request.getParameter("auctionId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			Debugger.log("parametri sbagliati");
			return;
		}
		
		//Chech if a auction with that id exists for that user 
		//ArticleDAO articleDAO = new ArticleDAO(connection);
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		Auction auction = null;
		
		
		OfferDAO offerDAO = new OfferDAO(connection);
		List<Offer> offers = null;
		
		//Article article = null;
		
		try {
			Debugger.log("Auc id: " + auctionId);
			//article = articleDAO.getArticleByAuctionId(auctionId);
			auction = auctionDAO.getAuctionById(auctionId);
			
			
			if (auction == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			} else 
			{
				offers = offerDAO.getOfferForItem(auction.getIdAsta());
			}
		} catch (SQLException e) {
			Debugger.log("Sql exception nella ricerca dell'articolo");
			e.printStackTrace();
			return;
		}
		
		//Get info about auction's owner
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.getUserByUsername(auction.getProprietario());
		} catch (SQLException e) {
			Debugger.log("Errore nella ricerca del proprietario dell'asta");
			e.printStackTrace();
		}
		
		
		float minim = auction.getPrezzo_start() + auction.getRialzo_min();
		//Redirect to the Article details page
		String path = "/templates/dettaglio.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("article", auction);
		ctx.setVariable("auctionOwner", user);
		ctx.setVariable("offers", offers);
		Debugger.log("Offerte per questo articolo: " + offers.size());
		ctx.setVariable("logged_username", loggedUser.getUsername());
		ctx.setVariable("auctionId", auctionId);
		ctx.setVariable("minim", minim);
		templateEngine.process(path, ctx, response.getWriter());
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}
	
	public void destroy() {
		DatabaseConnection.destroyConnection(connection);
//		try {
			DatabaseConnection.destroyConnection(connection);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}

}
