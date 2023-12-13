# Ejemplo Spring REST  (DAGSS-2023)

Ejemplo de endpoints REST con Spring-MVC (implementación directa)

Construido sobre proyecto [ejemplo-spring-23](https://github.com/esei-si-dagss/ejemplo-spring-23)

**Nota:** En los equipos de laboratorio, es conveniente establecer la variable de entorno JAVA_PATH, para que el comando `mvn` (Maven) compile y ejecute los proyectos siempre con el mismo JDK. 

```sh 
export JAVA_HOME=/usr/lib/jvm/jdk-20 

export PATH=$JAVA_HOME/bin:$PATH 
```

## Estructura del proyecto
El proyecto resultante sigue una organización relativamente habitual en proyectos Spring. 
En este caso, al tratarse de una aplicación muy sencilla es más compleja de lo necesario y sufre de cierta "_sobreingeniería_".

* Paquete **entidades**: incluye las entidades del dominio
* Paquete **daos**: incluye la definición de los `interfaces` _xxxDAO_ que heredan de `JpaRepository<E,K>` y aportan las operaciones CRUD (_create_, _read_, _update_, _delete_) sobre las entidades del dominio (se inyectarán _proxies_ con las implementaciones de estos interfaces cuando se requiera su inyección con `@Autowired`, que implícitamente estarían marcadas con `@Repository`)
* Paquete **servicios**: incluye los _servicios_ que encapsulan operaciones de la lógica de la aplicación
    - Las operaciones se definen en los `interfaces` _xxxService_ y se implementan en las clases _xxxServiceImpl_, marcadas con `@Service`
    - Los métodos de los _xxxServiceImpl_ delegan las operaciones en los _DAO_ necesarios/correspondientes, inyectados con `@Autowired` (no es imprescindible una correspondencia directa con las entidades presente en el dominio, ver como ejemplo el caso de `ArticuloServiceImpl`)
    - Las operaciones de _xxxService_ cumplen dos funciones complementarias:
       1. Ocultan y coordinan la operación de otros objetos _Service_ (`@Service`) o _DAO_ (`@Repository`), siguiendo un patrón _Facade_ (Fachada) que oculta a la capa superior la interacción entre esos objetos
       2. Algunos de sus métodos están marcados con `@Transactional` delimitando operaciones trasaccionales que serán invocadas desde la capa superior. Se asegura así que las modificaciones realizadas en estos métodos (y en los métodos de otros componentes invocados por estos) se ejecutan todas o ninguna, mantieniedo las propiedades ACID 
* Paquete **controladores**: incluye las clases marcadas con `@RestController` que aportan los métodos que son mapeados a las peticiones HTTP que conforman el API REST de la aplicación
    - Cada _xxxController_ está anotado con`@RestController` y delega en uno o más _yyyService_ (inyectados con `@Autowired`) la implementación de la lógica que corresponda (no es imprescindible una correspondencia directa con las entidades presente en el dominio)
    - Todas la URIs del API REST se ubican bajo la URI base `api`
    - Los controladores están anotados con `@CrossOrigin(origins = "*")` para permitir accesos al API REST desde aplicaciones Javascript (ver https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-cors)
    - La estructura del API REST definido sigue la estructura típica de las **aplicaciones _RESTFul_**
      - Se define un _endpoint_ REST para cada Recurso (~ Entidad) relevante para la aplicación, la **URI base** de cada recurso se identifica con el **nombre del recurso/entidad en plural** (ver anotaciones `@RequestMapping` en cada Controller)
      - Las peticiones **HTTP GET** sobre la **URI base** de cada Recurso se usan para realizar **búsquedas** con diferentes parámetros, devolviendo una lista de entidades codificadas como un array JSON
      - [[Las peticiones **HTTP POST** sobre la **URI base** de cada Recurso se usan para **crear nuevos recursos/entidades**. Los atributos del recurso/entidad a crear se pasan en el cuerpo de la petición POST codificados como objetos JSON. La respuesta HTTP generada contiene la URI del nuevo recurso/entidad creado en el parámetro `Location`.
      - Las peticiones **HTTP GET** sobre la URI base de cada Recurso seguida del **fragmento de path `/{id}` ** (mapeado como argumento del método con `@PathVariable`) se usan para **acceder** al recurso/entidad identificado **por la clave primaria** indicada. El recurso/entidad se devuelve como un objeto JSON en el cuerpo de la respuesta HTTP.
      - Las peticiones **HTTP DELETE** sobre la URI base de cada Recurso seguida del **fragmento de path `/{id}` ** (mapeado como argumento del método con `@PathVariable`) se usan para **eliminar** el recurso/entidad identificado **por la clave primaria** indicada.
      - Las peticiones **HTTP PUT** sobre la URI base de cada Recurso seguida del **fragmento de path `/{id}` ** (mapeado como argumento del método con `@PathVariable`) se usan para **modificar** los atributos del recurso/entidad identificado **por la clave primaria** indicada. Los nuevos valores de los atriutos del recurso/entidad se envian como un objeto JSON en el cuerpo de la petición HTTP PUT.
      - En los casos donde sea relevante, las **relaciones de un recurso** dado con otros recursos/entidades se mapean en **URIs anidadas** bajo el fragmento de path `/{id}`, usando el nombre en plural del recurso/entidad  al otro lado de la relación (por ejemplo `/api/almacenes/1/articulos`). Sobre esos recursos anidados pueden aplicarse peticiones GET (listado de recursos relacionados), POST (establecimiento de la relación con un nuevo recurso relacionado), DELETE sobre un `/{idRelacionado}` (eliminación de la relación con el recurso relacionado identificado por su clave primaria) o GET sobre un `/{idRelacionado}` (acceso al recurso relacionado identificado por su clave primaria).
* Paquete **controladores.excepciones**: ejemplo de gestión de excepciones en Spring MVC
   - Declara 2 excepciones propias de la aplicación (`ResourceNotFoundException` y `WrongParameterException`) que heredan de `RuntimeException`
   - Declara un _manejador de excepciones_ global, `GlobalExceptionHandler`, anotado con `@RestControllerAdvice`
      - Genera las respuestas HTTP para peticiones que provoquen excepciones 
      - Captura y maneja `ResourceNotFoundException`, `WrongParameterException`,  `MethodArgumentNotValidException` [fallos de Bean Validation en los parámetros anotados con `@Valid`] y `Exception` [respuesta genérica ante excepciones]
      - Cada respuesta a excepciones tiene un código `HttpStatus` de respuesta propio e incorpora en el cuerspo de la respuesta  datos adicionales encapsulando un objeto [ProblemDetail](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html)

## Añadidos
* En `pom.xml`:
    * Añadida dependencia con _starter_ para Spring MVC (`spring-boot-starter-web`) 
    * Añadida dependencia con _starter_ para Spring Validation (`spring-boot-starter-validation`) [Implementación de Spring de la especificación Bean Validation, permite usar la anotación `@Valid` para validar los atributos de los objetos JSON recibidos en las peticiones HTTP del API REST] 
* En paquete `entidades`: 
	* Añadida anotación `@JsonIgnore` en relación `@ManyToOne` de `LineaPedido` hacia `Pedido` para evitar ciclos en la serialziación JSON de los pedidos.
* Creado paquete `services` con:
	* `ClienteService` y `ClienteServiceImpl.java` 	
	* `ArticuloService` y `ArtculoServiceImpl.java` (gestiona Articulos y Familias)	
	* `AlmacenService` y `AlmacenServiceImpl.java`  (gestiona Almacenes y ArticuloAlmane [=stocks])
	* `PedidoService` y `PedidoServiceImpl`	
* Creado paquete `controllers` con:
   * `ClienteController`: API REST para cliente (en `http://localhost:8080/api/clientes`)
   * `ArticuloController`: API REST para artículos (en `http://localhost:8080/api/articulos`)
   * `FamiliaController`: API REST para familias (en `http://localhost:8080/api/familias`)
   * `PedidoController`: API REST para pedidos (en `http://localhost:8080/api/pedidos`)
   * `AlmacenController`: API REST para almacenes y stock de artículos en almacén (en `http://localhost:8080/api/almacenes`) 
   

## Ejecución del proyecto

En Spring Tool Suite: Proyecto 'pedidos-spring' `[botón derecho] > Run As > Spring Boot App`

Desde línea de comandos:
```sh
mvn spring-boot:run
```

**Nota**: 

* El .jar resultante ejecuta su propio contenedor de Servlet Apache Tomcat embebido, deplegando la aplicación en http://localhos:8080/api
* Se puede acceder desde el navegador o mediante  `curl -v` a las URIs del API
   - http://localhost/8080/clientes
   - http://localhost/8080/clientes/1111111A 
   - http://localhost/8080/articulos/3
   - http://localhost/8080/almacenes/1/articulos
   - http://localhost/8080/almacenes/1/articulos/2/stock


### EXTRA: SpringDoc OpenAPI (generador de documentación on-line OpenAPI/Swagger) [no es parte de Spring]

1. Descomentar en el `pom.xml` las  dependencias que activan el proyecto `springdoc-openapi`
   - Detalles en https://springdoc.org/

2. Ejecutar de nuevo el proyecto (con `mvn spring-boot:run`) y acceder con un navegador a la URL http://localhost:8080/swagger-ui.html para ver la documentación autogenerada para el API REST.



## Añadidos (13/12/2023) 

Añadida una versión del API REST usando Spring HATEOAS (https://docs.spring.io/spring-hateoas/docs/current/reference/html/) 

* Añadida al `pom.xml` del proyecto la dependencia `spring-boot-starter-hateoas` (ofrece `EntityModel`, `Link`, etc)  
* Añadido el paquete `controladores.hateoas` con las implementaciones de los endpoint HATEOAS disponibles en las URL bajo `http://localhost:8080/api/v2`. 
* Añadidos en ese paquete las versiones modificadas de los controladores: `AlmacenControllerV2.java`, `ArticuloControllerV2.java`,  `ClienteControllerV2.java`, `FamiliaControllerV2.java`, `PedidoControllerV2.java` 
     - Todos ellos replican los endpoints de la versión anterior, devolviendo objetos `EntityModel<Entidad>` en lugar de devolver directamente las entidades correspondientes. 
     - Estos `EntityModel<Entidad>` funcionan como especia de DTOs (_Data Transfer Objects_) encapsulando la/las entidad/entidades devueltas y añadiéndolo mediante objetos `Link` hipernelaces a otros re
     cursos/entidades relacionados. 
     - En el ejemplo no se usan hiperenlaces HATEOAS al 100%, limitandose a vincularlo a las entidades simples. En el caso de colecciones no se usa `CollectionModel` sino que se devuelven directamente listas de `EntityModel<E>` 

Añadido un ejemplo de documentación de APIs REST usando _SpringDOC OpenAPI_ (https://springdoc.org/ y https://www.openapis.org/) 
* Añadida al `pom.xml` del proyecto la dependencia `springdoc-openapi-ui`  
* Añadidas en `ClienteControllerV2.java` (y parcialmente en  `AlmacenControllerV2.java`) las anotaciones para vincular metadatos y generar la documentación automática de los _endpoints_. 
* La documentación on-line generada y el interfaz de pruebas _SwaggerUI_ estará disponible en http://localhost:8080/swagger-ui.html   
* La definición OpenAPI 3.x de los _endpoints_ definidos estará disponible en http://localhost:8080/v3/api-docs (formato JSON) y en http://localhost:8080/v3/api-docs.yml (formato YAML,ver https://spec. openapis.org/oas/latest.html, editable en https://editor.swagger.io/)
