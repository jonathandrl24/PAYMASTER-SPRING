# PAYMASTER-SPRING

Aplicación web spring boot Java maven- GESTION DE PAGOS Y PORTAL WEB PARA EMPRESA DE CONSTRUCCION - Proyecto SENA
- Nombres: Jonathan David Rodriguez Lozada
- ficha: (2758349) ANALISIS Y DESARROLLO DE SOFTWARE.
- Link Github: https://github.com/jonathandrl24/PAYMASTER-SPRING  

# Esta es una explicación sencilla que muestra la configuración predeterminada para ejecutar esta aplicación web con Thymeleaf Y Spring Boot.

# Primeros pasos:
Estas instrucciones le permitirán obtener una copia del proyecto en funcionamiento en su máquina local para fines de desarrollo y prueba.

# Requisitos previos:
Debe tener instalado en su máquina:
- JDK 11
- IDE Compatible: (IntelliJ IDEA (recomendado): Incluye soporte nativo para Spring Boot.
  Eclipse con Spring Tools Suite (STS): Si usas Eclipse, asegúrate de que tener el plugin STS instalado.
  Visual Studio Code: Con extensiones de Java y Spring Boot.
  NetBeans: También puede usarse, pero IntelliJ IDEA y Eclipse son más populares para Spring Boot.)
- Apache Maven 3 o posterior
- Spring Boot (Aunque no necesita instalarse por separado, el IDE debe poder manejar proyectos de Spring Boot. Maven descargará todas las dependencias necesarias.)
- apache tomcat
- Git
- Mysql y workbench (en workbench es necesario ejecutar este simple script: `CREATE DATABASE paymaster;` )
- Xamp control panel v3.3.0 (darle click a 'start' en Apache y en MySQL)
![image](https://github.com/user-attachments/assets/08d37dd0-54a2-4b4d-95f8-852755732350)

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
   - Dentro del application.properties , donde dice "paypal.client.id=" borre el client id escrito y escriba el suyo del modo sandbox del paypal developer que se encuentra en la seccion de apps & credentials
   - como en el paso anterior, donde dice "paypal.client.secret=", borre el client secret que ya estaba escrito y escriba el suyo 


3. para ejecutar la aplicación, escriba:
   - `mvn spring-boot:run`

4. para entrar a la app, ir al navegador de su preferencia y escribir en el buscador esto:
   `localhost:8080`

5. (PARA ENTRAR A LA PAGINA DEL ADMIN) en el carrousel de imagenes, darle click al boton de registrarse
    - llenar el formulario de registro
    - en workbench o en localhost/phpmyadmin, editar el nuevo usuario en donde dice 'tipo' cambiarle el valor de 'USER' a 'ADMIN'
    - al recargar la pagina e iniciar sesion con esa cuenta, deberia entrar a la pagina del administrador
    - (en esta pagina admin, usted podra: crear, editar y eliminar los servicios en la parte de gestionar servicios, ver los pagos hechos por los usuarios, ver los detalles de los pagos, ver los usuarios 
      registrados y descargar reporte de pagos en excel)

6. (CREAR UNA COMPRA)
    - registrarse
    - iniciar sesion con la cuenta creada
    - ir a servicios y escoger un servicio 
    - darle click a contratar servicio, esto lo llevara al carrito
    - darle click a pagar con paypal (se deberia abrir una pestaña de paypal)
    - iniciar sesion en paypal con la cuenta que aparece en la pagina de paypal developer, esto esta en la seccion de testing tools donde dice sandbox accounts, darle click a la que termina en    
      '@personal.example.com', copiar y pegar el email y el password y darle en continuar
    - seguir los pasos, al final deberia salir una ventana que diga 'pago exitoso' y tanto en la base de datos como en las compras del usuario y en la pagina admin deberia salir el pago hecho.

7. para detener la aplicación: 
   - dentro de la terminal, presione al mismo tiempo las teclas: control+c


