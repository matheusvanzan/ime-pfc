package Pages;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;  
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class LoggedInHttpServlet extends HttpServlet {
    
    private HttpSession session;
    private String user;
    private String next;
    
    public LoggedInHttpServlet(){
        
    }
    
    protected void checkLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.session = request.getSession();
        this.next = request.getRequestURI();
        System.out.println("Next page: " + this.next);
        
        if (session.getAttribute("user") == null) {
            response.sendRedirect("login");
            this.session.setAttribute("next", this.next);
        } else {
            this.user = session.getAttribute("user").toString();
        }  
    }
    
    public String getUser(){
        return this.user;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkLogin(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkLogin(request, response);
    }
    
    protected boolean userHasCamada(HttpServletRequest request, String camada) {
        try {
            //Leio tudo do arquivo e coloco em uma String
            String path = this.getServletContext().getRealPath("/") + "WEB-INF/config-camadas.xml";
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            String encryptedContent = new String(encoded, "UTF-8");

            //Descriptografo
            String fileContent = (new Cripto()).decrypt(encryptedContent);

            //Leio como se fosse um xml
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(fileContent));
            Document doc = dBuilder.parse(is);
            NodeList nList = doc.getElementsByTagName("camada");
            
            System.out.println(fileContent);

            for (int i=0; i<nList.getLength(); i++) {
                Node nNode = nList.item(i);

                //  Loop nas camadas
                if(camada.equals(nNode.getAttributes().getNamedItem("value").getNodeValue())){
                    Element eElement = (Element) nNode;
                    NodeList nCamadas = eElement.getElementsByTagName("role");
                    for(int j=0; j<nCamadas.getLength(); j++) {
                        if(request.isUserInRole(nCamadas.item(j).getTextContent())) {
                            return true;
                        }
                    } 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
