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
@WebServlet("/GetAvailableAuctionJS")
public class GetAvailableAuctionJS extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetAvailableAuctionJS() {
        super();
    }

    public void init() throws ServletException {
		connection = DatabaseConnection.getConnection(getServletContext());
	}
    
        
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		//Gi� controllato il login dell'utente con filtro
		//Gi� controllate scadenza aste con filtro
		
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		Debugger.log(user.toString());
		
		Debugger.log(user.getUsername());
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		List<Auction> auctions = new ArrayList<Auction>();
		
		//Get the list of avaiable auction for the logged user
		try {
			Debugger.log("Lista aste ricevuta da JS: " + request.getParameter("list"));
			 
		 	String[] visitedAuctionString = request.getParameter("list").replaceAll("\\[", "").replaceAll("\\]", "").split(",");
			 
		 	if (visitedAuctionString[0].equals("null")){
	            //come prima lista di tutte le aste
		 		Debugger.log("No cookie -> Lista normale di tutte le aste");
	            auctions = auctionDAO.getAvaibleAuction(user.getUsername());

	        }else if (visitedAuctionString[0].equals("")){
	            //lista vuota
	        	Debugger.log("Cookie, ma nessua asta vista -> Lista vuota");
	        	//Non hai visualizzato nessuna asta

	        } else if (visitedAuctionString[0].equals("win"))
	        {
	        	// Ritorno le aste vinte
	        	Debugger.log("Richieste aste vinte");
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
	    		
	    		auctions = winnedAuctions;
	        }
	        
	        else {
	            //lista aste visitate
	        	Debugger.log("Cookie, aste vistate -> Lista aste");
	        	List<Integer> visitedAuctionList = new ArrayList<Integer>();
			    for (int i = 0; i < visitedAuctionString.length ; i++) {
			    	visitedAuctionList.add(Integer.parseInt(visitedAuctionString[i]));
			    }
			    
			    //List<Auction> visitedAuctionToShow = new ArrayList<Auction>();
			    //visitedAuctionToShow = auctionDAO.getAvaibleAuction(user.getUsername());
			    
			    //visitedAuctionToShow.removeIf( n->visitedAuctionList.contains(n.getIdAsta()) );
			    
			    List<Auction> visitedAuctionToShow = new ArrayList<Auction>();

			    visitedAuctionList.forEach(e -> {
					try {
						visitedAuctionToShow.add(auctionDAO.getAuctionById(e));
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				});
			    
			    auctions = visitedAuctionToShow;

	        }

			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to get auctions");
			return;
		}
		
		if(auctions.isEmpty()) {
			Debugger.log("Lista aste consultabili vuota");
		}
		
		String auctionsJS = new Gson().toJson(auctions);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(auctionsJS);
		
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