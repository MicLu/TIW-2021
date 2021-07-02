package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Auction;
import it.polimi.tiw.beans.Offer;
import it.polimi.tiw.utils.Debugger;

public class OfferDAO {
	
	private Connection connection;

	public OfferDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Offer> getOfferForItem(int item) throws SQLException
	{
		List<Offer> offers = new ArrayList<Offer>();
		
		//String query = "SELECT asta.idasta as id, utenti.nome as uNome, utenti.cognome as uCognome, offerta.timestamp as time, offerta.valore as val FROM articolo JOIN asta ON articolo.idarticolo = asta.articolo JOIN offerta ON offerta.asta = asta.idasta JOIN utenti ON offerta.offerente = utenti.username WHERE articolo.idarticolo = ? ORDER BY offerta.valore DESC LIMIT 10";
		String query = "SELECT offerta.valore as val, asta.idasta as id, utenti.nome as unome, utenti.cognome as ucognome, utenti.username as username, timestamp from offerta join asta on offerta.asta = asta.idasta join utenti on utenti.username = offerta.offerente where asta.idasta = ? order by offerta.valore desc limit 10";
		try (PreparedStatement pstatement = connection.prepareStatement(query);)
		{
			pstatement.setString(1, Integer.toString(item));
			offers = getOfferList(pstatement);
		}
		
		return offers;
	}
	
	public List<Offer> getOfferList(PreparedStatement pstatement) throws SQLException
	{
		List<Offer> offers = new ArrayList<Offer>();
		
		try(ResultSet result = pstatement.executeQuery())
		{
			while(result.next())
			{
				Offer offer = new Offer();
				String offerent = result.getString("unome") + " " + result.getString("ucognome");
//				Debugger.log(offerent);
				offer.setOfferenteCompleto(offerent);
				offer.setOfferente(result.getString("username"));
				offer.setValore(result.getFloat("val"));
				offer.setAsta(Integer.parseInt(result.getString("id")));
				offer.setTimestamp(result.getTimestamp("timestamp"));
				
				offers.add(offer);
			}
		}
		
		return offers;
	}
	
	public boolean makeOffer(Offer offer) throws SQLException
	{
	
		
//		String validatorQuery = "select * from asta where idasta = ? AND stato = 'APERTA' AND proprietario != ?";
		String validatorQuery = "select * from asta join articolo on asta.articolo = articolo.idarticolo where idasta = ? AND stato = 'APERTA' AND proprietario != ?";
		PreparedStatement validStm = connection.prepareStatement(validatorQuery);
		validStm.setString(1, Integer.toString(offer.getAsta()));
		validStm.setString(2, offer.getOfferente());
		
		AuctionDAO acDAO = new AuctionDAO(connection);
		List<Auction> auctions = acDAO.getAuctionList(validStm);
		
		Debugger.log("Aste trovate con Offer: " + auctions.size());
		
		if (auctions.size() <= 0 || auctions.isEmpty())
		{
			return false;
			
		} else {
		
			String query = "insert into offerta (offerente, timestamp, valore, asta) values (?,?,?,?)";
			PreparedStatement pstatement = connection.prepareStatement(query);
			
			pstatement.setString(1, offer.getOfferente());
			pstatement.setString(2, offer.getTimestamp().toString());
			pstatement.setString(3, Float.toString(offer.getValore()));
			pstatement.setString(4, Integer.toString(offer.getAsta()));
			
			int result = pstatement.executeUpdate();
			
			Debugger.log("Inserite "+result+" nuove righe");
				
			return true;
		}
		
	}
	

}
