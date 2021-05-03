package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.xdevapi.Statement;

import it.polimi.tiw.beans.Offer;
import it.polimi.tiw.debugger.Debugger;

public class OfferDAO {
	
	private Connection connection;

	public OfferDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Offer> getOfferForItem(int item) throws SQLException
	{
		List<Offer> offers = new ArrayList<Offer>();
		
		String query = "SELECT asta.idasta as id, utenti.nome as uNome, utenti.cognome as uCognome, offerta.timestamp as time, offerta.valore as val FROM articolo JOIN asta ON articolo.idarticolo = asta.articolo JOIN offerta ON offerta.asta = asta.idasta JOIN utenti ON offerta.offerente = utenti.username WHERE articolo.idarticolo = ? ORDER BY offerta.valore DESC LIMIT 10";
		
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
				String offerent = result.getString("uNome") + " " + result.getString("uCognome");
				Debugger.log(offerent);
				
				offer.setOfferent(offerent);
				offer.setTimestamp(result.getInt("time"));
				offer.setValore(result.getFloat("val"));
				offer.setAsta(Integer.parseInt(result.getString("id")));
				
				offers.add(offer);
			}
		}
		
		return offers;
	}
	
	public void makeOffer(Offer offer) throws SQLException
	{
		String query = "insert into offerta (offerente, timestamp, valore, asta) values (?,?,?,?)";
		PreparedStatement pstatement = connection.prepareStatement(query);
		
		pstatement.setString(1, offer.getOfferent());
		pstatement.setString(2, Integer.toString(offer.getTimestamp()));
		pstatement.setString(3, Float.toString(offer.getValore()));
		pstatement.setString(4, Integer.toString(offer.getAsta()));
		
		int result = pstatement.executeUpdate();
		
		Debugger.log("Inserite "+result+" nuove righe");
			
			
		
	}
	

}
