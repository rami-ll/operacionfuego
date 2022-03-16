Este proyecto expone un servicio rest con distintos recuros mediante un tomcat embebido provisto springboot framework.

## Los recursos expuestos son:

---

**POST** -> /topsecret

Espera dentro del body la informacion correspondiente a los satelites, estos deben estar configurados en el archivo application.properties como se detalla mas adelante.

De no recibir canditidad suficiente de satelites, satelites invalidos o informacion insuficiente en el mensaje retornara error indicando la causa.

body: 

Formato:

````json
{
  "satellites": [
    {
      "name": "",
      "distance":"",
      "message": []
    }
  ]
}
````

Ejemplo:  
  
```json
{
    "satellites" :[
        {"name":"kenobi",
        "distance":100.0,
        "message":["este","","","mensaje",""]
        },
      {
        "name":"skywalker",
        "distance":115.5,
        "message":["","es","","","secreto"]
      },
      {
        "name":"sato",
        "distance":142.7,
        "message":["","","un","",""]
      }
    ]
}
````
response:

Status 200

Formato:
````json
{
    "position": {
        "x": "coordX",
        "y": "coordY"
    },
    "message": "mensaje"
}
````

Ejemplo:
````json
{
    "position": {
        "x": -58.31525258657543,
        "y": -69.55141837490909
    },
    "message": "este es un mensaje secreto"
}
````


---

**POST** -> /topsecret_split/{nombre_satelite}

Se espera como parametro alguno de los satelites configurados
de lo contrario retornara un error

body: 
````json
 {
    "distance":142.7,
         "message":["","","un","",""]
 }
````
response:

Status 200

No body

---

**GET** -> /topsecret_split

La respuesta es igual a la del recurso topsecret. Para que este devuelva una respuesta correcta se debera haber cargado previamente mediante el recurso anterior la cantidad minima de satelites necesarios e informacion suficiente para descifrar el mensaje. Asi como en el otro recurso en caso de ser insuficiente retornara error indicando la causa.


body: *none*

response:

Status 200

Formato:
````json
{
    "position": {
        "x": "coordX",
        "y": "coordY"
    },
    "message": "mensaje"
}
````

Ejemplo:
````json
{
    "position": {
        "x": -58.31525258657543,
        "y": -69.55141837490909
    },
    "message": "este es un mensaje secreto"
}
````

---

En caso de error la respuesta sera un codigo 404 con el siguente body:

````json
{
  "error": "mensaje_error",
  "status": "404"
}
````

---

## Informacion para ejecucion:

Para levantar la aplicacion descargar el contenido del repositorio y ejecutar el comando sobre el root paht **mvn clean install**.

Posteriormente ejecutar el archivo jar generado.

*Actualmente se encuentra configurada para correr sobre el puerto 8443 con protocolo https
para esto es necesitario generar un certificado y configurarlo en el archivo application.properties*
ver mas info sobre como configurarlo [aqui](https://www.thomasvitale.com/https-spring-boot-ssl-certificate/)

Existe una catidad de satelites configurables desde elarchivo application.properties los cuales son necesarios
para el correcto funcionamiento de la app. Configuracion actual (solo a modo ejemplo, se puede usar cualquiera deseada
siempre que sea compatible con la funcionalidad de la libreria trilateration):

*satellites=kenobi,skywalker,sato*

*satellite.kenobi=-500.0,-200.0*

*satellite.skywalker=100.0,-100.0*

*satellite.sato=500.0,100.0*

Para las los calculos de la posicion del emisor en base a las posiciones de los satelites y las distancias provistas
como input se utilizo la libreria trilateration que provee esta funcionalidad ([Maven central](https://mvnrepository.com/artifact/com.lemmingapex.trilateration/trilateration/1.0.2))
El codigo fuente de la misma es publico, puede ser descargado y testeado desde este repositorio:
[GitHub](https://github.com/lemmingapex/trilateration)
