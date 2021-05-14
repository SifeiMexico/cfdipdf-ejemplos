# ![Sifei](https://www.sifei.com.mx/web/image/res.company/1/logo?unique=38c7250)



#  RESTful Web Service CFDI PDF.
Este repositorio incluye ejemplos de solicitudes y respuesta de los endpoint(metodos) del WS de generacion de PDF de CFDI.

 
Pagina oficial de sifei: [Sifei](https://www.sifei.com.mx/) 

 

# Para mayor informacion tecnica referirte a:
Documentación: https://api.sifei.com.mx/cfdipdf/docs/index.html

# Listado de WS 

Actualmente se cuentan con los siguientes metodos
-  Método PDF


# Descripción Técnica
El presente WS está basado en una arquitectura tipo REST,  tanto la respuesta como la petición deben estar en formato JSON. Cada  método  tiene  su  propio  path  relativo a  la  URL  principal  de  consumo  y  en  general  comparten  una estructura  de  respuesta  basado en  JSON  genérica,  donde  únicamente  varía  el  atributo  data  según  el endpointconsumido.


# Requerimientos Mínimos

-  	Token valido y vigente para el consumo del servicio REST.
-  	Internet estable.


# Estándar

El **cuerpo** de  respuesta genérico para todos los endpoint del  WS es un JSON con los siguientes atributos:

| Código       | Tipo       |  Definición |
| ----------- | ----------- |------------- |
|status   | string | Indica el estado de la solicitud, sus valores posibles son: success, fail, error. <br /> Donde:<br /> •	success indica que el consumo fue exitoso <br /> •	fail indica algún error en el consumo(token invalido, falta de algún atributo o petición mal formada, para este caso ver el atributo code para mayor información, endpoint inexistente). <br />•	Error. Error genérico de aplicación.|
| data       | any       |  Contiene el resultado de la operación, si se trata de consultas, este campo contendrá el resultado de la búsqueda.   |
| code       | String       |  Si bien a través del campo status es posible saber si una solicitud se ejecutó la operación deseada, o bien si fue aceptada y validada, en el campo code se devuelve un código específico para cada tipo de operación, en general este campo contiene el código de error cuando status es diferente a success.   |
| message        | String       |  Cuando exista un error en la petición, codigo HTTP diferente de 200, junto con **code**, este campo contendrá el error verbal, es decir, una descripción del error.   |
| uuid        | String       |  En caso exitoso contiene el UUID del CFDI  |

Donde la estructura del campo **data** cambiara según el método consumido, todos los demás campos siempre serán del tipo definido en la tabla superior y mantienen su propósito y significado a través de todos los métodos.

# Métodos del API

## Método pdf(generar PDF)
Este método espera un XML de CFDI 3.3 y los colores del PDF, opcionalmente puede recibir un logo en base 64, para mayor informacion de este metodo ingresa a:

https://api.sifei.com.mx/cfdipdf/docs/index.html

### Request

El método y URL que forman la petición para la descarga se describe en la siguiente tabla

~~~HTTP
POST /cfdipdf/api/v2/pdf HTTP/1.1
Host: api.sifei.com.mx
User-Agent: curl/7.66.0
accept: application/json
Authorization: token_ejemplo
Content-Type: application/json
Content-Length: 126

{ "xml": "baseodificado64c..==",  "primaryColor": "#1599BF",  "secundaryColor": "#E7A201",  "logo": "3fa85f64-5717-4562-b3fc-2c963f66afa6"}' 
~~~

### Response (Respuesta)
A continuación se listan ejempos de respuestas.

#### Respuesta Acceso no autorizado 401


~~~HTTP
HTTP/1.1 401 Unauthorized
Server: Apache
Strict-Transport-Security: max-age=631138519; includeSubDomains
Expires: Thu, 19 Nov 1981 08:52:00 GMT
Cache-Control: no-store, no-cache, must-revalidate
Pragma: no-cache
Content-Length: 83
Content-Type: application/json

{"status":"fail","data":null,"code":"401","message":"Token invalido:token_ejemplo"}
~~~

#### Respuesta Erronea 400 (sin token)

~~~HTTP

HTTP/1.1 400 Bad Request
Server: Apache
Strict-Transport-Security: max-age=631138519; includeSubDomains
Expires: Thu, 19 Nov 1981 08:52:00 GMT
Cache-Control: no-store, no-cache, must-revalidate
Pragma: no-cache
Content-Length: 68
Connection: close
Content-Type: application/json

{"status":"fail","data":null,"code":"4001","message":"Falta Token "}

~~~


#### Response error 400 en request body

~~~HTTP
HTTP/1.1 400 Bad Request
Server: Apache
Strict-Transport-Security: max-age=631138519; includeSubDomains
Expires: Thu, 19 Nov 1981 08:52:00 GMT
Cache-Control: no-store, no-cache, must-revalidate
Pragma: no-cache
Content-Length: 170
Connection: close
Content-Type: application/json

{"status":"fail","data":null,"code":"4002","message":"Color tiene 6 caracteres y no cumple expresion hexadecimal valida. 6 Chacteres alfanumericos son necesarios:#FFFFF"}
~~~



# Ejemplos
A continuación se listan ejemplos de consumo en diversos lenguajes, **debes adaptarlo** incluyendo el token que tienes asignado, el XML de CFDI a enviar y los colores en formato hexadecimal.

El codigo HTTP debe ser **200** en caso de haberse generado el PDF exitosamente, "status" y "code" dentro del body de la respuesta son campos **auxiliares** que proveen de mayor informacion, el error esta en el campo **message** solo si el codigo HTTP es diferente de 200 y documentado en la referencia inferior.

Para mayor referencia:

Docs: http://api.sifei.com.mx/cfdipdf

## Ejemplo Java

Este ejemplo Java usa GSON como library para deserializar el JSON y modelarlo a la respuesta de la solicitud.

GSON:2.6.2

~~~JAVA
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
//clase response Model
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

~~~
## Ejemplo .NET
~~~ Csharp
using System.Net.Http;
using System.Net;
//clase modelo request
public class PDFRequest
{
    public string logo { get; set; }
    public string xml { get; set; }
    public string secundaryColor { get; set; }
    public string primaryColor { get; set; }
}
//clase modelo response
public class PDFResponse
{
    public string status { get; set; }
    public string code { get; set; }
    public string data { get; set; }
    public string message { get; set; }
	public string uuid { get; set; }
}
public class Program{
    public static  void Main(string[] args)
    {
        
        string token="TU_TOKEN";	
        string xmlPath="PATH_DE_XML";
        string pdfPath=xmlPath.Replace(".xml",".pdf");
        string xmlb64=Convert.ToBase64String(File.ReadAllBytes(xmlPath));
        var PDFRequest = new PDFRequest() { xml = xmlb64 ,primaryColor="#1599BF",secundaryColor="#E7A201"};
        using (var client = new HttpClient()){
            client.BaseAddress = new Uri("https://api.sifei.com.mx/cfdipdf/api/v2/");
            client.DefaultRequestHeaders.TryAddWithoutValidation("Authorization",token);
            var postTask = client.PostAsJsonAsync<PDFRequest>("pdf", PDFRequest);
            postTask.Wait();

            var result = postTask.Result;
            if (result.IsSuccessStatusCode)
            {

                var readTask = result.Content.ReadAsAsync<PDFResponse>();
                readTask.Wait();
                var createdPDFRequest = readTask.Result;

                Console.WriteLine("Extrayendo pdf de respuesta a archivo", createdPDFRequest.code, createdPDFRequest.status);

                byte[] pdfbytes=Convert.FromBase64String(createdPDFRequest.data);
                File.WriteAllBytes(pdfPath,pdfbytes);
                Console.WriteLine("PDF generado en:{0} de cfdi:{1}",pdfPath,createdPDFRequest.uuid);
            }
            else
            {
                Console.WriteLine("Error en peticion:{0}, description:{1}",(int)result.StatusCode,result.StatusCode);

                var readTask = result.Content.ReadAsAsync<PDFResponse>();
                readTask.Wait();

                var createdPDFRequest = readTask.Result;
                Console.WriteLine("Error exacto:[{0}]",createdPDFRequest.message);
                switch(result.StatusCode){
                    case HttpStatusCode.BadRequest:
                        break;
                    case HttpStatusCode.Unauthorized:
                        //aqui puedes agregar una alerta 
                        break;
                    case HttpStatusCode.NotFound:
                        break;
                    case HttpStatusCode.InternalServerError:
                        //error en el servidor
                        break;	
                    default:
                        break;	
                }

                
                Console.WriteLine(result.Content.ReadAsStringAsync().Result);
            }
        }
    }
  }
~~~

## Ejemplo Python
~~~python
import http.client
import json
import base64

#server="localhost"
server="api.sifei.com.mx"

#conn = http.client.HTTPSConnection(server)
conn = http.client.HTTPSConnection(server)

cfdiPath="cliente/XML_DEMO.xml"
logoPath='cliente/logo.jpg'
token='TU_TOKEN'
pdfPath=cfdiPath.replace('.xml','.pdf')
with open(cfdiPath,"rb") as cfdiFile:
    base64CFDI=base64.b64encode(cfdiFile.read())
with open(logoPath,"rb") as logoFile:
    base64Logo=base64.b64encode(logoFile.read())
#Preparamos la solicitud    
payload={
    'primaryColor':'#1599BF',
    'secundaryColor':'#E7A201',
    'logo': str(base64Logo.decode('utf-8')), 
    'xml': str(base64CFDI.decode('utf-8'))
}
headers = {
    'Content-Type': "application/json",
    'Authorization': token
}
print("Connectando")
conn.request("POST", "/cfdipdf/api/v2/pdf", json.dumps(payload), headers)

res = conn.getresponse()
data = res.read()
print(data)
resul=json.loads(data.decode("utf-8"))

print("httpcode:"+str(res.status))
# the code when it generates a pdf is 200.
if res.status != 200:
    print("Un error ocurrio, codigo:[{}], mensaje de error:[{}]".format(str(resul['code']),resul['message']))

    #400 invalid request
    #401. Wron token, invalid token, expired token
    #500 server error.

else:    
    print("PDF creado. Extrayendo respuesta")
    bPDF=base64.b64decode(resul['data'])
    #escribimos el PDF
    with open(pdfPath,'wb') as pdfFile:
        pdfFile.write(bPDF)
    print("PDF escrito en: {}".format(pdfPath))
       

        
~~~

## Ejemplo PHP

~~~php
<?php
declare(strict_types = 1);
$baseUrl=   "https://api.sifei.com.mx/cfdipdf/api/v2/";

#se prepara la URL relativa para el metodo(endpoint)
$urlMethod=$baseUrl."pdf";
echo "consumiendo(consuming):".$urlMethod."\n";
$token="TU_TOKEN";
#se preparan los datos como color, xml y logo.
$params =  [
    'primaryColor'=>'#1599BF',
    'secundaryColor'=>'#E7A201',
    'xml'=>  base64_encode(file_get_contents( __DIR__."/IngresoV2_DEMO.xml")),
    'logo'=> base64_encode(file_get_contents( __DIR__.'/logo.jpg')), 
];
$ch = curl_init();
curl_setopt_array($ch, 
array(
CURLOPT_URL            => $urlMethod,
CURLOPT_RETURNTRANSFER => true,
CURLOPT_POST           => 1,
CURLOPT_HTTPHEADER     => array(
                            'Content-Type:application/json',
                            'Authorization:'.$token, #token                            
                        ),
CURLOPT_POSTFIELDS     => json_encode($params)
));
$resultCurl = curl_exec($ch);
$httpcode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

if ($httpcode!=200) {
    if (!$resultCurl) {
        #Error de red, de resolucion de dominio,etc
        echo "Un error no controlado ocurrio";
        return;
    }
}else{
    echo "Codigo exitoso, recuperado PDF generado.(Successfull code, retrieving generated pdf from response.";
    $arrRes=json_decode($resultCurl, true);  

    #se escribe a archivo el PDF contenido en el atributo data de la respuesta.
    file_put_contents(__DIR__."/".$arrRes['uuid']. time().'.pdf', base64_decode($arrRes['data']));
    echo "creado";    
}
~~~
## Ejemplo nodejs

Nota: EL token nunca debe guardarse en el frontend, solo en el backend.

~~~js
const request = require('request');
const fs=require("fs")
var token="TU_TOKEN";
const sendCFDI=function(xmlB64,token){
    console.log(xmlB64)
    request.post({
        "headers": { 
            "content-type": "application/json",
            "Authorization": token
        },
        "url": "https://api.sifei.com.mx/cfdipdf/api/v2/pdf",
        "body": JSON.stringify({
            "xml": xmlB64,
            "primaryColor": "#1599BF",
            "secundaryColor": "#E7A201",
            "logo": ""
        })
    }, (error, response, body) => {
        if(error) {
            console.log("Error")
            return console.dir(error);
        }
        if(response.statusCode!=200){
            console.log("Error al consumir REST")
            var responseBodyObj=JSON.parse(body)
            console.dir(responseBodyObj);
            console.error(responseBodyObj.message);
            return;
        }
        //si fue exitoso puedes guardarlo a BD o escribirlo a archivo, etc.

        
        console.log("Ok")
    });
}
xmlPath="../IngresoV2_DEMO.xml";
var cfdi=fs.readFileSync(xmlPath,'utf8');
sendCFDI(Buffer.from(cfdi,'utf-8').toString('base64'),token)
~~~
# CENTRO DE SOPORTE TÉCNICO SIFEI
Acceso a recursos de Soporte Técnico de los productos y servicios de SIFEI, Preguntas Frecuentes, Manuales de Usuario, Manuales Técnicos, Notas Técnicas, entre otros 

# ATENCIÓN A INCIDENTES

La atención a incidentes se realizará mediante una herramienta de gestión de incidentes y la comunicación se realizará mediante correo electrónico.  

Correo Electrónico	helpdesk@sifei.com.mx


# HORARIO DE ATENCIÓN

El horario de atención a clientes y de Soporte Técnico para para preguntas, dudas o problemas de la aplicación es:
Lunes a viernes	De 09:00 a 19:00 hrs.

# PÁGINAS OFICIALES DE SIFEI

Sitio web	http://www.sifei.com.mx/
Facebook	http://www.facebook.com/SIFEIMexico
Twitter	http://twitter.com/SIFEIMexico
YouTube	http://www.youtube.com/SIFEIMexico
LinkedIn	http://www.linkedin.com/company/SIFEIMexico 

