package it.polimi.tiw;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import it.polimi.tiw.core.Database;

@WebServlet("/")
public class LoginTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public LoginTest() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException
    {
    	
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
		
		ServletContext context = getServletContext();
		
		out.println("<html>");
		out.println("<form action = 'Login' method = 'GET'>");
		out.println("<p>Username");
		out.println("<input type = 'text' name = 'username'>");
		out.println("<p>Password");
		out.println("<input type = 'password' name = 'password'>");
		out.println("<input type = 'submit' value = 'login'>");
		out.println("</form>");
		out.println("</html>");
	}


	
}
