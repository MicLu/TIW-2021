package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.core.DatabaseConnection;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.debugger.Debugger;

/**
 * Servlet implementation class CloseAuction
 */
@WebServlet("/CloseAuction")
public class CloseAuction extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public CloseAuction() {
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
		
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		//AuctionId
		Integer auctionId = null;
		
		try {
			auctionId = Integer.parseInt(request.getParameter("auctionId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			Debugger.log("parametri sbagliati");
			return;
		}
		
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		
		try {
			int res = auctionDAO.closeMyAuction(auctionId, user.getUsername());
			
			if(res==1) {
				Debugger.log("Chiusa asta con id " + auctionId);
			}else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Non è possibile chiudere questa asta");
				return;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Non è possibile chiudere l'asta");
			return;
			
		}
		
		response.sendRedirect(getServletContext().getContextPath() + "/GetArticleDetails?auctionId=" + auctionId);

	}


}
