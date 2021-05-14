import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import com.google.gson.Gson;
import java.text.MessageFormat;

public class PDF {
    
    public static Boolean sendCFDI(String url,String rfc,String cfdiB64,String token,String logo){
    
        
        String requestBody="{\"xml\":\""+cfdiB64+"\",\"primaryColor\":\"#1599BF\",\"secundaryColor\":\"#E7A201\",\"logo\":"+logo+" }"; //expected JSON
        try {
            HttpClient client = HttpClient.newHttpClient();
            
            byte[] sampleData = "Sample request body".getBytes();
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.sifei.com.mx/cfdipdf/api/v2/pdf"))
            .headers("Content-Type", "application/json","Header1","Value 1")
            .headers("Authorization", token)
            .POST(BodyPublishers.ofString(requestBody))
            .build();

            PDFResponse pdfResponse = null;
            HttpResponse<String> response= client.send(request,BodyHandlers.ofString());

            //checamos si la respuesta fue exitosa:
            if(response.statusCode() != 200){
                System.err.println("Error al consumir WS:"+response.statusCode());
                System.err.println(response.body());
                pdfResponse = new Gson().fromJson(response.body(), PDFResponse.class);
                System.err.println(pdfResponse);

                //si requeremos obtener el mensaje del error completo:
                System.err.println(pdfResponse.message);

                switch (response.statusCode()) {
                    case 400://error en petiicon

                        break;
                    case 401://no authorize invalid token
                    //logica para alertar sobre token invalido, o  expiracion de token.
                        break;
                    case 405:
                        //invalid method
                        break;
                    case 500:
                        //server error. 
                        break;
                    default:
                        break;
                }
                return false;
            }
            //si es exitosa  entonces:
            System.out.println("Se genero el pdf");
            //System.out.println(response.body()); //si quiere ver la respuesta raw descomenta esta linea.
            System.out.println("Parseando pdfResponseeto/Parsing pdfResponseect");
            pdfResponse = new Gson().fromJson(response.body(), PDFResponse.class);

            System.out.println(pdfResponse);
            return true;//Solo si status="success"
            
        } catch (InterruptedException ex) {
            System.err.println("Exception:"+ex.toString());  

        	return false;
            
        } 
       
        catch (IOException ex) {
        	System.err.println("Exception:"+ex.toString());  
            return false;
        }     
    }   

}

class PDFResponse{  
  //Campo auxiliar
  //success|error| fail
  public String status;

   
  public String data;

  //Mensaje del error
  public String message;     

  public String code;

  public String uuid;

  public String toString(){
    return MessageFormat.format("status: {0}. code: {1}. message:{2}, data:{3},uuid:{4} ", this.status,this.code,this.message,this.data,this.uuid);
  }
}
