package it.polimi.tiw.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

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
@MultipartConfig
public class CreateAuction extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private String imgFolder = "";
       
    public CreateAuction() {
        super();
    }

    public void init() throws ServletException {
		connection = DatabaseConnection.getConnection(getServletContext());
		imgFolder = getServletContext().getInitParameter("imgFolder");
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
		
		Part filePart = request.getPart("immagineArticolo");
		if (filePart == null || filePart.getSize() <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file in request!");
			return;
		}
		
		String contentType = filePart.getContentType();
		
		if (!contentType.startsWith("image")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File format not permitted");
			return;
		}
		
		String fileName = Paths.get(System.currentTimeMillis() + filePart.getSubmittedFileName()).getFileName().toString();
		
		String outPath = imgFolder + fileName;
		
		File file = new File(outPath);
		
		try (InputStream fileContent = filePart.getInputStream()) {
			// TODO: WHAT HAPPENS IF A FILE WITH THE SAME NAME ALREADY EXISTS?
			// you could override it, send an error or 
			// rename it, for example, if I need to upload images to an album, and for each image I also save other data, I could save the image as {image_id}.jpg using the id of the db

			Files.copy(fileContent, file.toPath());
			Debugger.log("Immage upload OK");
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving file");
		}
		
		try {
			nomeArticolo = request.getParameter("nomeArticolo");
			descrizioneArticolo = request.getParameter("descrizioneArticolo");
			//immagineArticolo = request.getParameter("immagineArticolo");
			
			prezzo_startAsta = Float.parseFloat(request.getParameter("prezzo_startAsta"));
			rialzo_minAsta = Integer.parseInt(request.getParameter("rialzo_minAsta"));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			Date date = (Date) sdf.parse(request.getParameter("scadenzaAsta"));
									
			scadenzaAsta = new Timestamp(date.getTime());
			
			
            
            immagineArticolo = URLEncoder.encode(fileName, "utf-8");
            

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
