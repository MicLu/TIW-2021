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
import it.polimi.tiw.beans.AuctionStatus;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.core.DatabaseConnection;
import it.polimi.tiw.dao.AuctionDAO;

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
		
		// If the user is not logged in (not present in session) redirect to the login
			String loginpath = getServletContext().getContextPath() + "/index.html";
			HttpSession session = request.getSession();
			if (session.isNew() || session.getAttribute("user") == null) {
				response.sendRedirect(loginpath);
				return;
			}
			
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
