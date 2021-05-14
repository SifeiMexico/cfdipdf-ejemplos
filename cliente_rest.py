import http.client
import json
import base64

server="api.sifei.com.mx"


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
       

        