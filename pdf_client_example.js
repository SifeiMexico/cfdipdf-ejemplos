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