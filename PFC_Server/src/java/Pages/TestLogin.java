package Pages;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestLogin extends LoggedInHttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestLogin</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("   <p>Servlet TestLogin at " + request.getContextPath() + "</p>");
            out.println("   Current User = " + this.getUser() + "<br><br>");
            
            // Teste de roles
            if(request.isUserInRole("administrador")){
                out.println("   Usuario pertence a role 'administrador'<br><br>");
            }
            if(request.isUserInRole("tatico")){
                out.println("   Usuario pertence a role 'tatico'<br><br>");
            }
            if(request.isUserInRole("operacional")){
                out.println("   Usuario pertence a role 'operacional'<br><br>");
            }
            
            //Teste de Camadas
            if(this.userHasCamada(request, "hoteis")){
                out.println("   Usuario possui acesso a camada hoteis<br><br>");
            }
            if(this.userHasCamada(request, "policiais")){
                out.println("   Usuario possui acesso a camada policiais<br><br>");
            }
            if(this.userHasCamada(request, "bombeiros")){
                out.println("   Usuario possui acesso a bombeiros<br><br>");
            }
            
            if(request.isUserInRole("administrador")){
                out.println("<br><br>   <a href=\"camadas\">Configurar Camadas");
            }
           
            out.println("<br><br>   <a href=\"logout\">Logout</a>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
        
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        
        processRequest(request, response);
    }
}
