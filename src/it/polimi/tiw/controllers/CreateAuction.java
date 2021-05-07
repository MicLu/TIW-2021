package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Article;
import it.polimi.tiw.core.DatabaseConnection;
import it.polimi.tiw.dao.ArticleDAO;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.debugger.Debugger;
import it.polimi.tiw.beans.User;

/**
 * Servlet implementation class CreateAuction
 */
@WebServlet("/CreateAuction")
public class CreateAuction extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public CreateAuction() {
        super();
    }

    public void init() throws ServletException {
		connection = DatabaseConnection.getConnection(getServletContext());
	}
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		boolean badRequest = false;
		
		String nomeArticolo = null;
		String descrizioneArticolo = null;
		String immagineArticolo = null;
		
		Float prezzo_startAsta = null;
		Integer rialzo_minAsta = null;
		Timestamp scadenzaAsta = null;
		
		
		try {
			nomeArticolo = request.getParameter("nomeArticolo");
			descrizioneArticolo = request.getParameter("descrizioneArticolo");
			immagineArticolo = request.getParameter("immagineArticolo");
			
			prezzo_startAsta = Float.parseFloat(request.getParameter("prezzo_startAsta"));
			rialzo_minAsta = Integer.parseInt(request.getParameter("rialzo_minAsta"));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			Date date = (Date) sdf.parse(request.getParameter("scadenzaAsta"));
									
			scadenzaAsta = new Timestamp(date.getTime());

			//Controllo campi vuoti
			badRequest = nomeArticolo.isEmpty() || descrizioneArticolo.isEmpty() || /*immagineArticolo.isEmpty() || */
					prezzo_startAsta == null || rialzo_minAsta == null || scadenzaAsta == null;
			//Controllo data precedente
			badRequest = scadenzaAsta.before(new Timestamp(System.currentTimeMillis()));
			
		} catch (NumberFormatException | NullPointerException | ParseException e) {
			badRequest = true;
			e.printStackTrace();
		}
		
		if (badRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		
		User user = (User) session.getAttribute("user");
		
		//Crea un nuovo articolo
		ArticleDAO articleDAO = new ArticleDAO(connection);
		Article newArticle = null; 
		
		//TODO: blocca update db se una delle due query fallisce?
		
		try {
			articleDAO.createArticle(nomeArticolo, descrizioneArticolo, immagineArticolo);
			newArticle = articleDAO.getLastInsertArticle();
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile creare l'articolo");
			e.printStackTrace();
		}
		Debugger.log("Articolo creato");
		
		//Crea una nuova asta
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		try {
			auctionDAO.createAuction(prezzo_startAsta, rialzo_minAsta, scadenzaAsta, newArticle.getIdArticolo(), user.getUsername());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile creare l'asta");
			e.printStackTrace();
		}
		Debugger.log("Asta creata");
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetMyAuction";
		response.sendRedirect(path);

		
		
	}
	
	public void destroy() {
		DatabaseConnection.destroyConnection(connection);
	}

}
