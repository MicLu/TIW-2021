package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import it.polimi.tiw.beans.AuctionStatus;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.utils.DatabaseConnection;
import it.polimi.tiw.utils.Debugger;

/**
 * Servlet implementation class GetMyAuction
 */
@WebServlet("/GetMyAuction")
public class GetMyAuction extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public GetMyAuction() {
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
		
		//Già controllato il login dell'utente con filtro
		//Già controllate scadenza aste con filtro
		
		HttpSession session = request.getSession();
			
		User user = (User) session.getAttribute("user");
	
		String path = "/templates/mieAste.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		List<Auction> auctions = new ArrayList<Auction>();
		
		List<Auction> auctionsOpen = new ArrayList<Auction>();
		List<Auction> auctionsClosed = new ArrayList<Auction>();
		
		
		
		//Get the list of all auction for the logged user
		
		try {
			auctions = auctionDAO.getAllMyAuction(user.getUsername());
			
			for (Auction auction : auctions)
			{
				Debugger.log("scadenza asta: " + auction.getScadenza());
				
				Timestamp scadenzaAsta = auction.getScadenza();
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
		        	auction.setTimeLeft("Asta scaduta");
				 }else {
					 auction.setTimeLeft(timeleft);
				 }
		        
				if (auction.getAuctionStatus() == AuctionStatus.CHIUSA)
				{
					auctionsClosed.add(auction);
				} else
				{
					auctionsOpen.add(auction);
				} 
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		ctx.setVariable("MyAuctionO", auctionsOpen);
		ctx.setVariable("MyAuctionC", auctionsClosed);
		
		templateEngine.process(path, ctx, response.getWriter());
		
		//TODO: aggiungere messaggio su mieAste.html quando la lista è vuota
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
