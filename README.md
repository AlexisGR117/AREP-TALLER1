# TALLER 1: APLICACIONES DISTRIBUIDAS (HTTP, SOCKETS, HTML, JS,MAVEN, GIT)

Aplicación para consultar información de películas de cine. Recibe una frase de búsqueda del título y muestra los datos de la película correspondiente. Utiliza el API gratuito de https://www.omdbapi.com/ y un caché para evitar consultas repetidas.

## Comenzando

Estas instrucciones te ayudarán a obtener una copia del proyecto en funcionamiento en tu máquina local para fines de desarrollo y pruebas.

### Requisitos previos

- Kit de desarrollo de Java (JDK) versión 11 o posterior
- Herramienta de construcción Maven

### Instalando

1. Clona el repositorio:
    ```
    git clone https://github.com/AlexisGR117/AREP-TALLER1.git
    ```
2. Navega a la carpeta del proyecto:
    ```
    cd AREP-TALLER1
    ```
3. Construye el proyecto usando Maven:
    ```
    mvn clean install
    ```
4.  Ejecuta la aplicación:
    ```
    java -cp target/AREP-TALLER1-1.0-SNAPSHOT.jar edu.escuelaing.arem.ASE.app.MovieInfoServer
    ```
5. Abre el navegador web **Firefox** y accede a la aplicación en http://localhost:35000.
## Ejecutando las pruebas

Ejecuta las pruebas unitarias:
    ```
    mvn test
    ```

## Documentación

Para generar el Javadoc (se generará en la carpeta target/site):
```
mvn site
```

## Diseño

### Componentes clave:

- **Clase MovieInfoServer:** Maneja las solicitudes del cliente, obtiene datos de películas y genera respuestas HTML.
- **Interfaz MovieDataProvider:** Permite tener proveedores que proporcionen la información sobre películas como la API de OMDb (OMDbMovieDataProvider).
- **Caché:** Utiliza una ConcurrentHashMap para almacenar los datos de películas recuperados para mayor eficiencia.

### Extensibilidad

El diseño admite el uso de diferentes proveedores de datos de películas a través de la interfaz MovieDataProvider:

1. Cree una nueva clase que implemente MovieDataProvider para el proveedor deseado.
2. Instancia el nuevo proveedor en MovieInfoServer en lugar de OMDbMovieDataProvider.

## Construido con

- Java 11
- Maven

## Autores

* Jefer Alexis Gonzalez Romero
