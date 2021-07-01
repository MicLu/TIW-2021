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
import it.polimi.tiw.utils.DatabaseConnection;
import it.polimi.tiw.utils.Debugger;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Auction;

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

	        }else {
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