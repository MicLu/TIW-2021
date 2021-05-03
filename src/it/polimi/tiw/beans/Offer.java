package it.polimi.tiw.beans;

import java.sql.Timestamp;

public class Offer {
	
	private String offerente;
	private Timestamp timestamp;
	private float valore;
	private int asta;
	
	private String offerent;
	
	public Offer() {
	}

	public String getOfferente() {
		return offerente;
	}

	public void setOfferente(String offerente) {
		this.offerente = offerente;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public float getValore() {
		return valore;
	}

	public void setValore(float valore) {
		this.valore = valore;
	}

	public int getAsta() {
		return asta;
	}

	public void setAsta(int asta) {
		this.asta = asta;
	}
	
	public void setOfferent(String offerent)
	{
		this.offerent = offerent;
	}
	
	public String getOfferent()
	{
		return offerent;
	}
	
	
	
	

}
