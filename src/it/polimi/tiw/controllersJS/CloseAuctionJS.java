package it.polimi.tiw.controllersJS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.utils.DatabaseConnection;
import it.polimi.tiw.utils.Debugger;

/**
 * Servlet implementation class CloseAuction
 */
@WebServlet("/CloseAuctionJS")
@MultipartConfig
public class CloseAuctionJS extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public CloseAuctionJS() {
        super();
    }
    
    public void init() throws ServletException {
		connection = DatabaseConnection.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		//AuctionId
		Integer auctionId = null;
		
		try {
			auctionId = Integer.parseInt(request.getParameter("auctionId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print("Incorrect param values");
			
			Debugger.log("parametri sbagliati");
			return;
		}
		
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		
		try {
			int res = auctionDAO.closeMyAuction(auctionId, user.getUsername());
			
			if(res==1) {
				Debugger.log("Chiusa asta con id " + auctionId);
			}else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				//response.getWriter().print("Non � possibile chiudere questa asta");
				return;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			//response.getWriter().print("Non � possibile chiudere questa ascdcdvcdvta");
			return;
			
		}
		
		//TODO: in js non serve fare redirect? 
		//response.sendRedirect(getServletContext().getContextPath() + "/GetArticleDetails?auctionId=" + auctionId);
		
		response.getWriter().write("1");

	}


}
