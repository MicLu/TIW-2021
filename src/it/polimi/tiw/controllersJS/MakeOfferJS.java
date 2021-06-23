package it.polimi.tiw.controllersJS;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Offer;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.dao.OfferDAO;
import it.polimi.tiw.utils.DatabaseConnection;
import it.polimi.tiw.utils.Debugger;

@WebServlet("/MakeOfferJS")
@MultipartConfig
public class MakeOfferJS extends HttpServlet
{
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	

	public MakeOfferJS()
	{
		super();
	}
	
	public void init() throws ServletException
	{
		ServletContext servletContext = getServletContext();
		connection = DatabaseConnection.getConnection(servletContext);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//Già controllato il login dell'utente con filtro
		//Già controllate scadenza aste con filtro
		
		Offer offer = new Offer();
		
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		OfferDAO offerDAO = new OfferDAO(connection);
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		
		String offerente = null;
		int timestamp = 0;
		float valore = 0;
		int asta = 0;
		float rialzo_min = 0;
		float corrente = 0;

		// Leggo i parametri e creo l'offerta
		try {
			
			timestamp = 500;
			valore = Float.parseFloat(request.getParameterValues("valore")[0]);
			asta = Integer.parseInt(request.getParameterValues("asta")[0]);
			rialzo_min = Float.parseFloat(request.getParameterValues("min")[0]);
			corrente = Float.parseFloat(request.getParameterValues("corrente")[0]);
			offerente = request.getParameterValues("offerente")[0];
			
			
			if (valore == 0 || asta == 0 || timestamp == 0 || offerente == null)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Campi mancanti");
				//FIXME
				//response.sendRedirect(getServletContext().getContextPath() + "/GetArticleDetails?auctionId="+asta+"&error="+1);
				Debugger.log("Campi mancanti");
				return;
			}
			
			if (valore < rialzo_min + corrente)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Offerta minore dell'offerta minima");
				
				Debugger.log("Offerta minore dell'offerta minima");
				//FIXME
				//response.sendRedirect(getServletContext().getContextPath() + "/GetArticleDetails?auctionId="+asta+"&error="+2);
				return;
			}
			
			offer.setAsta(asta);
			offer.setOfferente(offerente);
			offer.setTimestamp(new Timestamp(System.currentTimeMillis()));
			offer.setValore(valore);
			
			boolean offerOk = offerDAO.makeOffer(offer);
			
			
			if (offerOk) {
				auctionDAO.updateAuctionPrezzoStart(valore, asta);
				//FIXME
				//response.sendRedirect(getServletContext().getContextPath() + "/GetArticleDetails?auctionId="+asta);
			} else 
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Spiacente l'asta è scaduta, o hai provato a offrire alla tua asta");
			}
			
			
		}catch (SQLException e)
		{
			Debugger.log("SQL exception");
			e.printStackTrace();
		} 
		catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Input non valido");
			Debugger.log("Input non valido");
			return;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} 
		
		
		
		
	}
}
