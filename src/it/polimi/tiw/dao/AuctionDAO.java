package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Article;
import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.AuctionStatus;
import it.polimi.tiw.debugger.Debugger;

public class AuctionDAO {
	
	private Connection connection;

	public AuctionDAO(Connection connection) {
		this.connection = connection;
	}
	
	//Restituisce lista delle aste create dal proprietario, non ancora chiuse 
	public List<Auction> getSellingNotClosedAuction(String owner) throws SQLException{
		
		List<Auction> auctions = new ArrayList<Auction>();
		
		//FIXME: controllare ordinamento aste ascs/desc
		String query = "SELECT * FROM asta JOIN articolo on articolo = idarticolo WHERE proprietario = ? AND NOT(stato = 'CHIUSA') ORDER BY scadenza ASC";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, owner);
			auctions = getAuctionList(pstatement);
			
		}
		
		return auctions;
		
	}
	
	//Restituisce lista delle aste create dal proprietario, gi� chiuse
	//TODO: possibile unire questo metodo con il precedente usando un parametro che condiziona la costruzione della query
	public List<Auction> getSellingClosedAuction(String owner) throws SQLException{
		
		List<Auction> auctions = new ArrayList<Auction>();
		
		//FIXME: controllare ordinamento aste ascs/desc
		String query = "SELECT * FROM asta JOIN articolo on articolo = idarticolo WHERE proprietario = ? AND (stato = 'CHIUSA') ORDER BY scadenza ASC";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, owner);
			auctions = getAuctionList(pstatement);
		}
		
		return auctions;
		
	}
	
	public List<Auction> getAvaibleAuction(String buyer) throws SQLException{
		
		List<Auction> auctionList = new ArrayList<Auction>();
		
		//FIXME: controllare ordinamento aste ascs/desc
		String query = "SELECT * FROM asta JOIN articolo on articolo = idarticolo WHERE NOT(proprietario = ?) AND (stato = 'APERTA') ORDER BY scadenza ASC";
		
		try (PreparedStatement stm = connection.prepareStatement(query))
		{
			stm.setString(1, buyer);
			auctionList = getAuctionList(stm);
		}
		
		return auctionList;
		
		
	}
	
	public Auction getAuctionById(int auctionId) throws SQLException
	{
		List<Auction> auctions = new ArrayList<Auction>();
		Auction test = null;
		String query = "SELECT * FROM asta JOIN articolo on articolo = idarticolo WHERE (idasta = ?) ORDER BY scadenza ASC";
		
		try (PreparedStatement stm = connection.prepareStatement(query))
		{
			stm.setInt(1, auctionId);
			auctions = getAuctionList(stm);
			Debugger.log(Integer.toString(auctions.size()));
		}
		
		return auctions.get(0);
	}
	
	public List<Auction> getAvaibleAuctionByKeyword(String buyer, String keyword) throws SQLException{
		
		List<Auction> auctions = new ArrayList<Auction>();
		
		//FIXME: controllare ordinamento aste ascs/desc
		//FIXME: controllare funzionamento ricerca per keyword
		String query = "SELECT * FROM asta JOIN articolo on articolo = idarticolo WHERE NOT(proprietario = ?) AND (stato = 'APERTA') AND (nome LIKE '%?%' OR descrizione LIKE '%?%') ORDER BY scadenza ASC";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) 
		{
			pstatement.setString(1, buyer);
			auctions = getAuctionList(pstatement);
		}
		
		
		return auctions;
		
	}
	
	
	public List<Auction> getAllMyAuction(String owner) throws SQLException{
		
		List<Auction> auctions = new ArrayList<Auction>();
		
		//FIXME: controllare ordinamento aste ascs/desc
		String query = "SELECT * FROM asta JOIN articolo on articolo = idarticolo WHERE proprietario = ? ORDER BY scadenza ASC";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, owner);
			auctions = getAuctionList(pstatement);
		}
		
		return auctions;
		
	}
	
	
	public List<Auction> getClosedAuction(){
		return null;
		
	}
	
	public List<Auction> getAuctionList(PreparedStatement pstatement) throws SQLException
	{
		List<Auction> auctions = new ArrayList<Auction>();
		
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					Auction auction = new Auction();
					Article article = new Article();
					
					auction.setIdAsta(result.getInt("idAsta"));
					auction.setPrezzo_start(result.getFloat("prezzo_start"));
					auction.setRialzo_min(result.getInt("rialzo_min"));
					auction.setScadenza(result.getTimestamp("scadenza"));
					auction.setArticolo(result.getInt("articolo"));
					auction.setProprietario(result.getString("proprietario"));
					
					//TODO: controllare se setta bene i valori
					auction.setAuctionStatus( AuctionStatus.valueOf(result.getString("stato")) );
					
					article.setIdArticolo(result.getInt("articolo"));
					article.setNome(result.getString("nome"));
					article.setDescrizione(result.getString("descrizione"));
					article.setImmagine(result.getString("immagine"));
					
					
					auction.setArticle(article);

					auctions.add(auction);
				}
			}
		
		return auctions;
	}

	public void updateAuctionPrezzoStart(float price, int id) throws SQLException {
		
		String query = "update asta set prezzo_start = ? where idasta = ?";
		PreparedStatement pstatement = connection.prepareStatement(query);
		pstatement.setString(1, Float.toString(price));
		pstatement.setString(2, Integer.toString(id));
		
		int result = pstatement.executeUpdate();
		Debugger.log("Modificate "+result+" righe");
	}
	
	public void updateAuctionStatus(int id, AuctionStatus status)
	{
		
	}
	
	public void createAuction(float prezzo_start, int rialzo_min, Timestamp scadenza, int articolo, String proprietario) throws SQLException {
		
		String query = "INSERT INTO asta (prezzo_start, rialzo_min, scadenza, articolo, proprietario) VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setFloat(1, prezzo_start);
			pstatement.setInt(2, rialzo_min);
			pstatement.setTimestamp(3, scadenza);
			pstatement.setInt(4, articolo);
			pstatement.setString(5, proprietario);
			
			pstatement.executeUpdate();
		}
		
	}
	

}
