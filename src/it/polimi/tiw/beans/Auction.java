package it.polimi.tiw.beans;

public class Auction {
	
	private int idAsta;
	private float prezzo_start;
	private int rialzo_min;
	private int scadenza;
	private int articolo;
	private String proprietario;
	private AuctionStatus auctionStatus;
	
	public Auction() {
	}

	public int getIdAsta() {
		return idAsta;
	}

	public void setIdAsta(int idAsta) {
		this.idAsta = idAsta;
	}

	public float getPrezzo_start() {
		return prezzo_start;
	}

	public void setPrezzo_start(float prezzo_start) {
		this.prezzo_start = prezzo_start;
	}

	public int getRialzo_min() {
		return rialzo_min;
	}

	public void setRialzo_min(int rialzo_min) {
		this.rialzo_min = rialzo_min;
	}

	public int getScadenza() {
		return scadenza;
	}

	public void setScadenza(int scadenza) {
		this.scadenza = scadenza;
	}

	public int getArticolo() {
		return articolo;
	}

	public void setArticolo(int articolo) {
		this.articolo = articolo;
	}

	public String getProprietario() {
		return proprietario;
	}

	public void setProprietario(String proprietario) {
		this.proprietario = proprietario;
	}

	public AuctionStatus getAuctionStatus() {
		return auctionStatus;
	}

	public void setAuctionStatus(AuctionStatus auctionStatus) {
		this.auctionStatus = auctionStatus;
	}
	
	
	
	
	
	

}
