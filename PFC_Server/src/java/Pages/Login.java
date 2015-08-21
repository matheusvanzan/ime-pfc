package Pages;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("   <title>Simple Login Page</title>");            
            out.println("</head>");
            out.println("<body>");
            
            HttpSession session = request.getSession();
            if (session.getAttribute("user") == null) {
                out.println("   <form action=\"login\" method=\"POST\">");
                out.println("       <p>Simple Login Page</p>");
                out.println("       User: ");
                out.println("       <input type=\"text\" name=\"username\"></input>");
                out.println("       Pass: ");
                out.println("       <input type=\"password\" name=\"password\"></input>");
                out.println("       <input type=\"submit\" value=\"Go\"></input>");
                out.println("   </form>");
            } else {
                out.println("<p>Usuario: " + session.getAttribute("user") + "</p>");
            }  
            
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String username = request.getParameter("username"); 
        String password = request.getParameter("password");
        
        try {
            request.login(username, password);
        } catch(ServletException ex) {
            System.out.println(ex.toString());
            response.sendRedirect("erro");
        } finally {
            HttpSession session = request.getSession();
            session.setAttribute("user", request.getUserPrincipal());
            //session.setMaxInactiveInterval(15*60);
            
            if (session.getAttribute("next") == null) {
                response.sendRedirect("index.html");
            } else {
                response.sendRedirect(session.getAttribute("next").toString());
                session.removeAttribute("next");
            }  
        }
    }
}
