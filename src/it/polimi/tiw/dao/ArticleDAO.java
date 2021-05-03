package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.Article;

public class ArticleDAO {
	
	private Connection connection;

	public ArticleDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Article getArticleByAuctionId(int auctionId) throws SQLException {
		
		Article article = null;
		
		String query = "SELECT * FROM asta JOIN articolo on articolo = idarticolo WHERE idasta = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, auctionId);
			try (ResultSet result = pstatement.executeQuery();) {
					
				if (result.next())
				{
					article = new Article();
					
					article.setIdArticolo(result.getInt("idarticolo"));
					article.setNome(result.getString("nome"));
					article.setDescrizione(result.getString("descrizione"));
					article.setImmagine(result.getString("immagine"));
				}

			} 
		} 
		
		return article;
		
	}
	
	

}
