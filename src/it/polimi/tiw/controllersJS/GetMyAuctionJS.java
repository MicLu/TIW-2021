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
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.utils.DatabaseConnection;

/**
 * Servlet implementation class GetMyAuction
 */
@WebServlet("/GetMyAuctionJS")
public class GetMyAuctionJS extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetMyAuctionJS() {
        super();
    }

    public void init() throws ServletException {
		connection = DatabaseConnection.getConnection(getServletContext());
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Gi� controllato il login dell'utente con filtro
		//Gi� controllate scadenza aste con filtro
		
		HttpSession session = request.getSession();
			
		User user = (User) session.getAttribute("user");
		
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
		
		
		String auctionsOpenJS = new Gson().toJson(auctionsOpen);
		String auctionsClosedJS = new Gson().toJson(auctionsClosed);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		response.getWriter().write(auctionsOpenJS);
		// TODO: Rendere parametrico e ritornare solo la lista di ate giusta (parametro tramite get probabilmente
		//response.getWriter().write(auctionsClosedJS);
		
		//TODO: aggiungere messaggio su mieAste.html quando la lista � vuota
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
