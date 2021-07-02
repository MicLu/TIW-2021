package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.el.lang.ELSupport;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.dao.OfferDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.DatabaseConnection;
import it.polimi.tiw.utils.Debugger;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.Offer;

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
		
		auctions.forEach(e->{
			
			Debugger.log("scadenza asta: " + e.getScadenza());
			
			Timestamp scadenzaAsta = e.getScadenza();
	        Timestamp now = new Timestamp(System.currentTimeMillis());

	        long milliseconds1 = scadenzaAsta.getTime();
	        long milliseconds2 = now.getTime();

	        long diff = (milliseconds1 - milliseconds2);
	        long diffSeconds = (long) Math.floor((diff % (1000 * 60)) / 1000);
	        long diffMinutes = (long) Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
	        long diffHours = (long) Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
	        long diffDays = (long) Math.floor(diff / (1000 * 60 * 60 * 24));
	        
	        String timeleft = (new StringBuilder())
					 .append(Long.toString(diffDays)).append("D ")
					 .append(Long.toString(diffHours)).append("H ")
					 .append(Long.toString(diffMinutes)).append("m ")
					 .append(Long.toString(diffSeconds)).append("s").toString();
	        
	        if(diff<0) {
				 e.setTimeLeft("Asta scaduta");
			 }else {
				 e.setTimeLeft(timeleft);
			 }
					 
		});
		
		ctx.setVariable("AvaiableAuctions", auctions);
		
		
		//Lista aste vinte
		List<Auction> winnedAuctions = new ArrayList<Auction>();
		List<Auction> closedAuctions = new ArrayList<Auction>();
		
		try {
			closedAuctions = auctionDAO.getClosedAuction();
		} catch (SQLException e1) {
			Debugger.log("Non trovo aste chiuse");
			e1.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get auctions");
			return;
		}
		
		closedAuctions.forEach(e->{
			List<Offer> offer = new ArrayList<Offer>();
			OfferDAO offerDAO = new OfferDAO(connection);
			try {
				offer = offerDAO.getOfferForItem(e.getIdAsta());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if(offer.size()>0) {
				try {
					UserDAO userDAO = new UserDAO(connection);
					//First element is the last offer
					Offer lastOffer = offer.get(0);
					User auctionWinner = userDAO.getUserByUsername(lastOffer.getOfferente());
					//sono il vincitoer
					if( (auctionWinner.getUsername()).equals(user.getUsername()) ) {
						winnedAuctions.add(e);
					}
				}
				catch (SQLException e2) {
					Debugger.log("Errore nella ricerca del vincitore dell'asta");
					e2.printStackTrace();
				}
				
			}
		});
		
		ctx.setVariable("winnedAuction", winnedAuctions);
		
		
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