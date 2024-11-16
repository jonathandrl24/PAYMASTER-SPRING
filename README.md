# PAYMASTER-SPRING

Aplicación web spring boot Java maven- GESTION DE PAGOS Y PORTAL WEB PARA EMPRESA DE CONSTRUCCION - Proyecto SENA
- Nombres: Jonathan David Rodriguez Lozada
- ficha: (2758349) ANALISIS Y DESARROLLO DE SOFTWARE.
- Link Github: https://github.com/jonathandrl24/PAYMASTER-SPRING  

# Esta es una explicación sencilla que muestra la configuración predeterminada para ejecutar esta aplicación web con Thymeleaf, Bootstrap 5 Y Spring Boot.

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
- Mysql y workbench 
- Xamp control panel v3.3.0 (darle click al boton 'start' a Apache y a MySQL)
![image](https://github.com/user-attachments/assets/08d37dd0-54a2-4b4d-95f8-852755732350)
- (en workbench es necesario ejecutar este simple script: `CREATE DATABASE paymaster;` )
  ![image](https://github.com/user-attachments/assets/65db2ad1-b34a-4045-a616-8e2c177025a1)

- (OPCIONAL PARA API PAYPAL) tener una cuenta de paypal developer: https://www.paypal.com/signin?intent=developer&returnUri=https%3A%2F%2Fdeveloper.paypal.com%2Fdashboard%2F

# Pasos para ejecutar la aplicación:

1. En la terminal(cmd), ejecutar los siguientes 2 comandos:

   - `git clone https://github.com/jonathandrl24/PAYMASTER-SPRING`
   - `cd PAYMASTER-SPRING`

2. *IMPORTANTE SI SU PUERTO DE MYSQL NO ES EL 3306:
   - Ir a la carpeta (C:\Users\su_usuario\PAYMASTER-SPRING\src\main\resources)
   - dentro del archivo "application.properties" ubicar la siguiente linea: spring.datasource.url=jdbc:mysql://localhost:3306/paymaster
   - donde dice 3306 cambiarlo por el puerto que usted tiene configurado para MySQL, tambien cambiar el datasource.username y el datasource.password por los suyos
    ![image](https://github.com/user-attachments/assets/c0c5915f-cdc9-4891-9fc4-1a10524cb32d)
   - Dentro de la capeta "PAYMASTER-SPRING" Ejecutar el siguiente comando en la terminal `mvn clean install` 
   - (si se tiene el error "[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.1:compile (default-compile) on project
   PAYMASTER-SPRING-BOOT: Compilation failure
   [ERROR] /C:/Users/(su_usuario)/PAYMASTER-SPRING/src/main/java/com/paymaster/config/PayPalConfig.java:[16,8] class PaypalConfig is public, should be declared in a file named PaypalConfig.java" 
   Debera ir entonces a la siguiente ruta:"C:\Users\su_usuario\PAYMASTER-SPRING\src\main\java\com\paymaster\config"
   y cambiarle el nombre al archivo "PayPalConfig" a "PaypalConfig" y repetir el mvn clean install, por alguna 
   razon al clonar el repositorio se modifica el nombre de ese archivo)

   - (OPCIONAL API PAYPAL):
   - En la siguiente carpeta "C:\Users\(su_usuario)\PAYMASTER-SPRING\src\main\resources" Dentro del archivo "application.properties" , donde dice "paypal.client.id=" borre el client id escrito y escriba el suyo y tambien donde dice "paypal.client.secret=", borre el client secret que ya estaba escrito y copie y pegue el suyo del modo sandbox de la pagina de paypal developer que se encuentra en la seccion de apps & credentials: https://developer.paypal.com/home/
     ![hoal-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/4ce86135-1845-4d31-a638-315fcc473922)

     ![image](https://github.com/user-attachments/assets/a708c48b-c69a-4fc0-b5e1-4c3a2868cf95)

   - (PASO OPCIONAL PARA QUE EL USUARIO PUEDA ENVIAR MENSAJES AL CORREO DEL ADMINISTRADOR DESDE EL FORMULARIO DEL CONTACTO) primero es necesario configurar su cuenta de google para poder acceder a las contraseñas de aplicaciones
   ![contra-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/0342cc38-b0ee-4b58-b5eb-eb2ff1628a47)

   - Despues de eso simplemente hay que crear una contraseña de aplicacion y copiar y pegar esa contraseña en el archivo "application.properties" donde dice password, tambien cambiar el username por su correo electronico
  ![image](https://github.com/user-attachments/assets/3ebaac5a-fc4e-4bdd-8da5-432f68ce329f)

   - ir al archivo "HomeController.java" que esta dentro de la carpeta "controller" y cambiar los valores del correo electronico por el suyo:
     ![image](https://github.com/user-attachments/assets/1163c205-aa0c-481a-b159-6b4e78febf16)


3. para ejecutar la aplicación, ejecute el siguiente comando en la terminal:
   - `mvn spring-boot:run`

4. para entrar a la app, ir al navegador de su preferencia y escribir en el buscador esto:
   `localhost:8080`
   ![image](https://github.com/user-attachments/assets/5a8166ce-125a-49e2-90aa-a455acb15142)


6. (PARA ENTRAR A LA PAGINA DEL ADMIN) en el carrousel de imagenes, darle click al boton de registrarse
    - llenar el formulario de registro
    - en workbench o en localhost/phpmyadmin, editar el nuevo usuario en donde dice 'tipo' cambiarle el valor de 'USER' a 'ADMIN'
      ![image](https://github.com/user-attachments/assets/19fd665b-2766-4efe-a5d4-6a7f9b145e1a)

    - al recargar la pagina e iniciar sesion con esa cuenta, deberia entrar a la pagina del administrador
      ![image](https://github.com/user-attachments/assets/67cd15fc-ad2c-4519-8770-58a25465c5a1)
      ![image](https://github.com/user-attachments/assets/fbd6a1bb-d712-4055-8029-d2f7b1af893b)


    - (en esta pagina admin, usted podra: crear, editar y eliminar los servicios en la parte de gestionar servicios, ver los pagos hechos por los usuarios, ver los detalles de los pagos, ver los usuarios 
      registrados y descargar reporte de pagos en excel)

7. (CREAR UNA COMPRA)
    - registrarse
      ![image](https://github.com/user-attachments/assets/38d32197-7402-4fb6-b9c9-1bbc4167cbd8)

    - iniciar sesion con la cuenta creada
      ![image](https://github.com/user-attachments/assets/7a931ceb-6e6b-4ea5-b34a-ded71bf26535)

    - ir a servicios y escoger un servicio
      ![image](https://github.com/user-attachments/assets/7f4800ae-3921-4fe1-a370-a8f7b7a2200c)

    - darle click a contratar servicio, esto lo llevara al carrito
    - darle click a pagar con paypal (se deberia abrir una pestaña de paypal)
      ![image](https://github.com/user-attachments/assets/745415fb-7c7c-4bd0-970c-dcc847f9f5fc)

    - iniciar sesion en paypal con la cuenta que aparece en la pagina de paypal developer: https://www.paypal.com/signin?intent=developer&returnUri=https%3A%2F%2Fdeveloper.paypal.com%2Fdashboard%2F , esto esta en la seccion de testing tools donde dice sandbox accounts, darle click a la que termina en    
      '@personal.example.com', copiar y pegar el email y el password y darle click en iniciar sesion
      ![Grabacin2024-11-10175624-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/37e2a11b-b583-4752-bf7a-0ee753bb95d0)
      ![image](https://github.com/user-attachments/assets/9a23651b-4960-46e8-957c-734882bef9ef)

    - seguir los pasos, al final deberia salir una ventana que diga 'pago exitoso' y tanto en la base de datos como en las compras del usuario y en la pagina admin deberia salir el pago hecho.
      ![image](https://github.com/user-attachments/assets/9fd4bfb5-9959-4395-a08d-93506e694760)
      ![hola-ezgif com-video-to-gif-converter (1)](https://github.com/user-attachments/assets/09d90a47-a6f8-4875-ab02-cee435d7002f)
  

8. para detener la aplicación: 
   - dentro de la terminal, presione al mismo tiempo las teclas: control+c


