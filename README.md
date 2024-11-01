# PAYMASTER-SPRING

Aplicación web spring boot Java maven- GESTION DE PAGOS EMPRESA DE CONSTRUCCION - Proyecto

# Esta es una explicación sencilla que muestra la configuración predeterminada para ejecutar esta aplicación web con Thymeleaf Y Spring Boot.

# Primeros pasos:
Estas instrucciones le permitirán obtener una copia del proyecto en funcionamiento en su máquina local para fines de desarrollo y prueba.

# Requisitos previos:
Debe tener instalado en su máquina:
- JDK 
- apache tomcat
- Maven 3 o posterior
- Git
- Mysql y workbench (en workbench hay que ejecutar un simple script: "CREATE DATABASE paymaster;")
- Xamp control panel v3.3.0 (darle click a 'start' en Apache y en MySQL)

- (OPCIONAL PARA API PAYPAL) tener una cuenta de paypal developer

# Pasos para ejecutar la aplicación:

1. En la terminal(cmd), escriba los siguientes 3 comandos:

   - `git clone https://github.com/jonathandrl24/PAYMASTER-SPRING`
   - `cd PAYMASTER-SPRING`
   - `mvn clean install` 
   - (si se tiene el error "[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:compile (default-compile) on project
   PAYMASTER-SPRING-BOOT: Compilation failure
   [ERROR] /C:/Users/jonathan/PAYMASTER-SPRING/src/main/java/com/paymaster/config/PayPalConfig.java:[16,8] class PaypalConfig is public, should be declared in a file named PaypalConfig.java" 
   Debera ir entonces a la siguiente ruta:"C:\Users\su_usuario\PAYMASTER-SPRING\src\main\java\com\paymaster\config"
   y cambiarle el nombre al archivo "PayPalConfig" a "PaypalConfig" y repetir el mvn clean install, por alguna 
   razon al clonar el repositorio se modifica el nombre de ese archivo)

2. *IMPORTANTE SI SU PUERTO DE MYSQL NO ES EL 3306:
   - Ir a la carpeta (C:\Users\su_usuario\PAYMASTER-SPRING\src\main\resources)
   - dentro del archivo "application.properties" ubicar la siguiente linea: spring.datasource.url=jdbc:mysql://localhost:3306/paymaster
   - donde dice 3306 cambiarlo por el puerto que usted tiene configurado para MySQL

   - (OPCIONAL API PAYPAL):
   - Dentro del application.properties , donde dice "paypal.client.id=" borre el client id escrito y escriba el suyo del modo sandbox del paypal developer
   - como en el paso anterior, donde dice "paypal.client.secret=", borre el client secret que ya estaba escrito y escriba el suyo 


3. para ejecutar la aplicación, escriba:
   - `mvn spring-boot:run`

4. para entrar a la app, ir al navegador de su preferencia y escribir en el buscador esto:
   `localhost:8080`

5. (PARA ENTRAR A LA PAGINA DEL ADMIN) en el carrousel de imagenes, darle click al boton de registrarse
    - llenar el formulario de registro
    - en workbench o en localhost/phpmyadmin, editar el nuevo usuario en donde dice 'tipo' cambiarle el valor de 'USER' a 'ADMIN'
    - al recargar la pagina e iniciar sesion con esa cuenta, deberia entrar a la pagina del administrador

6. para detener la aplicación: 
   - dentro de la terminal, presione al mismo tiempo las teclas: control+c


