package it.polimi.tiw;

import java.io.IOException;
import org.thymeleaf.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

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
	private TemplateEngine tempEngine;
	
	private Database db;
	
	public LoginTest() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException
    {
    	ServletContext context = getServletContext();
    	ServletContextTemplateResolver tempResolver = new ServletContextTemplateResolver(context);
    	tempResolver.setTemplateMode(TemplateMode.HTML);
    	this.tempEngine = new TemplateEngine();
    	this.tempEngine.setTemplateResolver(tempResolver);
    	tempResolver.setSuffix(".html");
    	
    	db = new Database(context);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.setContentType("text/html");
		

    	String path = "/Templates/index.html";
    	ServletContext contect = getServletContext();
    	
    	final WebContext ctx = new WebContext(request, response, contect, request.getLocale());
    	tempEngine.process(path, ctx, response.getWriter());
		
		PrintWriter out = response.getWriter();
		
		
		/*
		out.println("<html>");
		out.println("<form action = 'Login' method = 'GET'>");
		out.println("<p>Username");
		out.println("<input type = 'text' name = 'username'>");
		out.println("<p>Password");
		out.println("<input type = 'password' name = 'password'>");
		out.println("<input type = 'submit' value = 'login'>");
		out.println("</form>");
		out.println("</html>");
		*/
	}


	
}
