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
			auctions = auctionDAO.getAvaibleAuction(user.getUsername()); 
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