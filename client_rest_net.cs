using System.Net.Http;
using System.Net;

public class PDFRequest
{
    public string logo { get; set; }
    public string xml { get; set; }
    public string secundaryColor { get; set; }
    public string primaryColor { get; set; }
}
public class PDFResponse
{
    public string status { get; set; }
    public string code { get; set; }
    public string data { get; set; }
    public string message { get; set; }
	public string uuid { get; set; }
}
public class Program
    {
        public static  void Main(string[] args)
        {
            
			string token="TU_TOKEN";	
			string xmlPath="IngresoV2_DEMO.xml";
			string pdfPath=xmlPath.Replace(".xml",".pdf");
			string xmlb64=Convert.ToBase64String(File.ReadAllBytes(xmlPath));
			var PDFRequest = new PDFRequest() { xml = xmlb64 ,primaryColor="#1599BF",secundaryColor="#E7A201"};
		   using (var client = new HttpClient())
		            {
		           	client.BaseAddress = new Uri("https://api.sifei.com.mx/cfdipdf/api/v2/");
					client.DefaultRequestHeaders.TryAddWithoutValidation("Authorization",token);
		            var postTask = client.PostAsJsonAsync<PDFRequest>("pdf", PDFRequest);
		            postTask.Wait();

		            var result = postTask.Result;
		            if (result.IsSuccessStatusCode)
		            {

		                var readTask = result.Content.ReadAsAsync<PDFResponse>();
		                readTask.Wait();

		                var insertedPDFRequest = readTask.Result;

		                Console.WriteLine("Extrayendo pdf de respuesta a archivo", insertedPDFRequest.code, insertedPDFRequest.status);

						byte[] pdfbytes=Convert.FromBase64String(insertedPDFRequest.data);
						File.WriteAllBytes(pdfPath,pdfbytes);
						Console.WriteLine("PDF generado en:{0} de cfdi:{1}",pdfPath,insertedPDFRequest.uuid);
		            }
		            else
		            {
						Console.WriteLine("Error en peticion:{0}, description:{1}",(int)result.StatusCode,result.StatusCode);

						var readTask = result.Content.ReadAsAsync<PDFResponse>();
		                readTask.Wait();

		                var insertedPDFRequest = readTask.Result;
						Console.WriteLine("Error exacto:[{0}]",insertedPDFRequest.message);
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
