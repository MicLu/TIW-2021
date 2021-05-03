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

import it.polimi.tiw.beans.Offer;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.core.DatabaseConnection;
import it.polimi.tiw.dao.OfferDAO;
import it.polimi.tiw.debugger.Debugger;

@WebServlet("/MakeOffer")
public class MakeOffer extends HttpServlet
{
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	

	public MakeOffer()
	{
		super();
	}
	
	public void init() throws ServletException
	{
		ServletContext servletContext = getServletContext();
		connection = DatabaseConnection.getConnection(servletContext);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Leggo i parametri e creo l'offerta
		String loginpath = getServletContext().getContextPath() + "/index.html";
		
		Offer offer = new Offer();
		
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		User user = (User) session.getAttribute("user");
		
		OfferDAO offerDAO = new OfferDAO(connection);
		
		String offerente = null;
		int timestamp = 0;
		float valore = 0;
		int asta = 0;
		
		try {
			
			timestamp = 500;
			valore = Float.parseFloat(request.getParameterValues("valore")[0]);
			asta = Integer.parseInt(request.getParameterValues("asta")[0]);
			
			if (valore == 0 || asta == 0 || timestamp == 0 || offerente == null)
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Campi mancanti");
				Debugger.log("Campi mancanti");
				return;
			}
			
			offer.setAsta(asta);
			offer.setOfferent(offerente);
			offer.setTimestamp(timestamp);
			offer.setValore(valore);
			
			offerDAO.makeOffer(offer);
			
			
		}catch (SQLException e)
		{
			
		} 
		catch (Exception e)
		{
			
		} 
		
		
		
		
	}
}
