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