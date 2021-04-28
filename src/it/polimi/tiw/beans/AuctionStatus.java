package it.polimi.tiw.beans;

public enum AuctionStatus {
	
	APERTA(0), 
	SCADUTA(1), 
	CHIUSA(2);
	
	private final int value;

	private AuctionStatus(int value) {
		this.value = value;
	}
	
	public static AuctionStatus getAuctionStatusByInt(int value) {
		
		AuctionStatus returnAuctionStatus = null;
		switch (value) {
		case 0:
			returnAuctionStatus = AuctionStatus.APERTA;
		case 1:
			returnAuctionStatus = AuctionStatus.SCADUTA;
		case 2:
			returnAuctionStatus = AuctionStatus.CHIUSA;
		}
		return returnAuctionStatus;
		
	}

	public int getValue() {
		return value;
	}
	
	

}