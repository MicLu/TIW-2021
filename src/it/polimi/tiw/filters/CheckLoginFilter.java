package it.polimi.tiw.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.utils.Debugger;

public class CheckLoginFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		Debugger.log("FILTRO controllo login utente sessione");
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		String loginpath = req.getServletContext().getContextPath() + "/index.html";
		
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = req.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			res.sendRedirect(loginpath);
			return;
		}
		
		chain.doFilter(request, response);
		
	}

}
