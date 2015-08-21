package Pages;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Camadas extends LoggedInHttpServlet {
    
    String[] camadas;

    public Camadas() {
        this.camadas = new String[] {"atracoes", "bairros", "comites", "competicoes", "bombeiros", "delegacias", "policiais", "hoteis"};
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            if(request.isUserInRole("administrador")){
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Camadas</title>");            
                out.println("</head>");
                out.println("<body>");
                out.println("   <p>Página destinada aos Administradores para definir a relação entre Camdas e Grupos/Funções</p>");
                out.println("   Current User = " + this.getUser() + "<br><br>");

                //Leio tudo do arquivo e coloco em uma String
                String path = this.getServletContext().getRealPath("/") + "WEB-INF/config-camadas.xml";
                
                //Se ja existe
                File f = new File(path);
                System.out.println(path);
                if (f.exists() && !f.isDirectory()) {
                    System.out.println("File already exists");
                    
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

                    //Camada
                    for (int i=0; i<nList.getLength(); i++) {
                        Node nNode = nList.item(i);
                        String camada = nNode.getAttributes().getNamedItem("value").getNodeValue();
                        Element eElement = (Element) nNode;
                        NodeList nCamadas = eElement.getElementsByTagName("role");

                        //Roles da Camada
                        String roles = "";
                        for(int j=0; j<nCamadas.getLength(); j++) {
                            roles += nCamadas.item(j).getTextContent();
                        }

                        out.println("<form action=\"camadas\" method=\"POST\">");

                        out.println("<p>" + camada + "</p>");
                        out.println("<textarea rows=\"5\" cols=\"50\""
                                + "id=\"camada_" + Integer.toString(i) + "\" "
                                + "name=\"" + camada + "\">"
                                + roles
                                + "</textarea>");
                    }
                } else {
                    System.out.println("File dont exist");
                    
                    int i = 0;
                    for (String camada:this.camadas){
                        out.println("<form action=\"camadas\" method=\"POST\">");

                        out.println("<p>" + camada + "</p>");
                        out.println("<textarea rows=\"5\" cols=\"50\""
                                + "id=\"camada_" + Integer.toString(i) + "\" "
                                + "name=\"" + camada + "\">"
                                + "</textarea>");
                        i++;
                    }
                }
                
                out.println("<br>");
                out.println("<input type=\"submit\" value=\"Salvar\"></input>");
                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
                
            } else {
                response.sendRedirect("error");
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        String path = this.getServletContext().getRealPath("/") + "WEB-INF/config-camadas.xml";
        
        try {      
            //Codigo ainda dependente da lista de camadas
            String[] camadas = this.camadas;
            
            //Crio o conteudo do arquivo
            String fileContent = "<config>\n"; 
            for(String camada:camadas) {
                fileContent += "<camada value=\"" + camada + "\">\n";
                String[] roles = request.getParameter(camada).split("\n");
                for(String role:roles){
                    fileContent += "<role>" + role + "</role>\n";
                }
                fileContent += "</camada>\n";
            }
            fileContent += "</config>";
            
            //Criptografo e salvo
            String encryptedContent = (new Cripto()).encrypt(fileContent);
            PrintWriter outFile = new PrintWriter(path);
            outFile.println(encryptedContent);
            outFile.close();
            
            response.sendRedirect("camadas");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
}
