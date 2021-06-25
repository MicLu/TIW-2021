package it.polimi.tiw.controllersJS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.AuctionStatus;
import it.polimi.tiw.beans.Offer;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.dao.OfferDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.DatabaseConnection;
import it.polimi.tiw.utils.Debugger;

/**
 * Servlet implementation class GetAuctionDetails
 */
@WebServlet("/GetArticleDetailsJS")
public class GetArticleDetailsJS extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetArticleDetailsJS() {
        super();
    }
    
    public void init() throws ServletException {
		connection = DatabaseConnection.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Già controllato il login dell'utente con filtro
		//Già controllate scadenza aste con filtro
		
		HttpSession session = request.getSession();
		
		User loggedUser = (User) session.getAttribute("user");
		
		//Get auctionId
		Integer auctionId = null;
		
		try {
			auctionId = Integer.parseInt(request.getParameter("auctionId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param values");
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
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().print("Resource not found");
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

//		//Redirect to the Article details page
//		String path = "/templates/dettaglio.html";
//		ServletContext servletContext = getServletContext();
//		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		List<Object> myResponse = new ArrayList<>();
		
		myResponse.add(auction);
		myResponse.add(user);
		myResponse.add(offers);
		//String auctionJS = new Gson().toJson(auction);
		//String auctionOwnerJS = new Gson().toJson(user);
		//String offersJS = new Gson().toJson(offers);
		
		Debugger.log("Offerte per questo articolo: " + offers.size());
		
		myResponse.add(loggedUser.getUsername());
		myResponse.add(auctionId);
		myResponse.add(minim);
		//String logged_usernameJS = new Gson().toJson(loggedUser.getUsername());
		//String auctionIdJS = new Gson().toJson(auctionId);
		//String minimJS = new Gson().toJson(minim);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		//response.getWriter().write(auctionJS);
		//response.getWriter().write(auctionOwnerJS);
		//response.getWriter().write(offersJS);
		//response.getWriter().write(logged_usernameJS);
		//response.getWriter().write(auctionIdJS);
		//response.getWriter().write(minimJS);
		
		//Send auctionWinner'infos to template engine
		User auctionWinner = null;
		if(auction.getAuctionStatus()==AuctionStatus.CHIUSA) {
			if(offers.size()>0) {
				try {
					//First element is the last offer
					Offer lastOffer = offers.get(0);
					auctionWinner = userDAO.getUserByUsername(lastOffer.getOfferente());
				}
				catch (SQLException e) {
					Debugger.log("Errore nella ricerca del vincitore dell'asta");
					e.printStackTrace();
				}
				
			}
			
			myResponse.add(auctionWinner);
			//String auctionWinnerJS = new Gson().toJson(auctionWinner);

			String myResponseJS = new Gson().toJson(myResponse);
			response.getWriter().write(myResponseJS);
		}
		
		
		
		
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
