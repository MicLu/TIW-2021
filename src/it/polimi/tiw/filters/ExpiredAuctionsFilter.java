package it.polimi.tiw.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import it.polimi.tiw.core.DatabaseConnection;
import it.polimi.tiw.dao.AuctionDAO;
import it.polimi.tiw.debugger.Debugger;

public class ExpiredAuctionsFilter implements Filter {

	public ExpiredAuctionsFilter()
	{
		super();
	}
	
	private FilterConfig config;

	@Override
	public void init(FilterConfig config) {
	    this.config = config;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		ServletContext servletContext = config.getServletContext();
		Connection connection = DatabaseConnection.getConnection(servletContext);
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
		AuctionDAO auctionDAO = new AuctionDAO(connection);
		
		Debugger.log("FILTRO controllo scadenza aste - in data: " + now.toString());
		auctionDAO.updateExpiredAuctionStatus(now);
		
		chain.doFilter(request, response);
	}

}