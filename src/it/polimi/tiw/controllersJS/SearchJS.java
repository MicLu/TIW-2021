package it.polimi.tiw.controllersJS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.utils.DatabaseConnection;
import it.polimi.tiw.utils.Debugger;

/**
 * Servlet implementation class Search
 */
@WebServlet("/SearchJS")
public class SearchJS extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public void init() throws ServletException {
		
		connection = DatabaseConnection.getConnection(getServletContext());
	}
       
    public SearchJS() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		//Già controllato il login dell'utente con filtro
		
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		List<Auction> auctions = new ArrayList<Auction>();
		
		//Get the list of avaiable auction for the logged user
		try {
			auctions = auctionDAO.getAvaibleAuctionByKeyword(user.getUsername(), request.getParameter("keywordSearch")); 
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to get auctions");
			return;
		}
		
		Debugger.log("Cercata la parola: " + request.getParameter("keywordSearch"));
		
		if(auctions.isEmpty()) {
			Debugger.log("Nessuna asta corrispondente alla parola cercata");
			
		}
		
		String auctionsJS = new Gson().toJson(auctions);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(auctionsJS);
		
	}
}
