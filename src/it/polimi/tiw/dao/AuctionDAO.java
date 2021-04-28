package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.AuctionStatus;

public class AuctionDAO {
	
	private Connection connection;

	public AuctionDAO(Connection connection) {
		this.connection = connection;
	}
	
	//Restituisce lista delle aste create dal proprietario, non ancora chiuse 
	public List<Auction> getSellingNotClosedAuction(String owner) throws SQLException{
		
		List<Auction> auctions = new ArrayList<Auction>();
		
		//FIXME: controllare ordinamento aste ascs/desc
		String query = "SELECT * FROM asta WHERE proprietario = ? AND NOT(stato = CHIUSA) ORDER BY scadenza ASC";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, owner);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					Auction auction = new Auction();
					auction.setIdAsta(result.getInt("idAsta"));
					auction.setPrezzo_start(result.getFloat("prezzo_start"));
					auction.setRialzo_min(result.getInt("rialzo_min"));
					auction.setScadenza(result.getInt("scadenza"));
					auction.setArticolo(result.getInt("articolo"));
					auction.setProprietario(result.getString("proprietario"));

					//TODO: controllare se setta bene i valori
					auction.setAuctionStatus( AuctionStatus.valueOf(result.getString("stato")) );	
					
					auctions.add(auction);
				}
			}
		}
		
		return auctions;
		
	}
	
	//Restituisce lista delle aste create dal proprietario, già chiuse
	//TODO: possibile unire questo metodo con il precedente usando un parametro che condiziona la costruzione della query
	public List<Auction> getSellingClosedAuction(String owner) throws SQLException{
		
		List<Auction> auctions = new ArrayList<Auction>();
		
		//FIXME: controllare ordinamento aste ascs/desc
		String query = "SELECT * FROM asta WHERE proprietario = ? AND (stato = CHIUSA) ORDER BY scadenza ASC";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, owner);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					Auction auction = new Auction();
					auction.setIdAsta(result.getInt("idAsta"));
					auction.setPrezzo_start(result.getFloat("prezzo_start"));
					auction.setRialzo_min(result.getInt("rialzo_min"));
					auction.setScadenza(result.getInt("scadenza"));
					auction.setArticolo(result.getInt("articolo"));
					auction.setProprietario(result.getString("proprietario"));

					//TODO: controllare se setta bene i valori
					auction.setAuctionStatus( AuctionStatus.valueOf(result.getString("stato")) );	
					
					auctions.add(auction);
				}
			}
		}
		
		return auctions;
		
	}
	
	public List<Auction> getAvaibleAuction(String buyer) throws SQLException{
		
		List<Auction> auctions = new ArrayList<Auction>();
		
		//FIXME: controllare ordinamento aste ascs/desc
		String query = "SELECT * FROM asta WHERE NOT(proprietario = ?) AND (stato = APERTA) ORDER BY scadenza ASC";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, buyer);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					Auction auction = new Auction();
					auction.setIdAsta(result.getInt("idAsta"));
					auction.setPrezzo_start(result.getFloat("prezzo_start"));
					auction.setRialzo_min(result.getInt("rialzo_min"));
					auction.setScadenza(result.getInt("scadenza"));
					auction.setArticolo(result.getInt("articolo"));
					auction.setProprietario(result.getString("proprietario"));

					//TODO: controllare se setta bene i valori
					auction.setAuctionStatus( AuctionStatus.valueOf(result.getString("stato")) );	
					
					auctions.add(auction);
				}
			}
		}
		
		return auctions;
		
	}
	
	public List<Auction> getAvaibleAuctionByKeyword(String buyer, String keyword) throws SQLException{
		
		List<Auction> auctions = new ArrayList<Auction>();
		
		//FIXME: controllare ordinamento aste ascs/desc
		String query = "SELECT * FROM asta WHERE NOT(proprietario = ?) AND (stato = APERTA) AND () ORDER BY scadenza ASC";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, buyer);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					
					Auction auction = new Auction();
					auction.setIdAsta(result.getInt("idAsta"));
					auction.setPrezzo_start(result.getFloat("prezzo_start"));
					auction.setRialzo_min(result.getInt("rialzo_min"));
					auction.setScadenza(result.getInt("scadenza"));
					auction.setArticolo(result.getInt("articolo"));
					auction.setProprietario(result.getString("proprietario"));

					//TODO: controllare se setta bene i valori
					auction.setAuctionStatus( AuctionStatus.valueOf(result.getString("stato")) );	
					
					auctions.add(auction);
				}
			}
		}
		
		return auctions;
		
	}
	
	public List<Auction> getClosedAuction(){
		return null;
		
	}
	

}
