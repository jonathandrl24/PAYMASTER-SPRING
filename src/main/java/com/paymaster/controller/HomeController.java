package com.paymaster.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import com.paymaster.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.paymaster.service.PaypalService;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.paymaster.model.DetalleOrden;
import com.paymaster.model.Orden;
import com.paymaster.model.Servicio;
import com.paymaster.model.Usuario;


@Controller
@RequestMapping("/")
public class HomeController {

	private final Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private ServicioService servicioService;
	
	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;
	
	@Autowired
	private IDetalleOrdenService detalleOrdenService;

	@Autowired
	PaypalService service;

	@Autowired
	private JavaMailSender mailSender;

	public static final String SUCCESS_URL = "pay/success";
	public static final String CANCEL_URL = "pay/cancel";


	// para almacenar los detalles de la orden
	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

	// datos de la orden
	Orden orden = new Orden();
	// Pagina home con los datos de servicios y sesion de usuario
	@GetMapping("")
	public String home(Model model, HttpSession session) {
		
		log.info("Sesion del usuario: {}", session.getAttribute("idusuario"));
		
		model.addAttribute("servicios", servicioService.findAll());
		
		//session
		model.addAttribute("sesion", session.getAttribute("idusuario"));

		return "usuario/home";
	}
	//Ver servicios
	@GetMapping("serviciohome/{id}")
	public String servicioHome(@PathVariable Integer id, Model model) {
		log.info("Id servicio enviado como parámetro {}", id);
		Servicio servicio = new Servicio();
		Optional<Servicio> servicioOptional = servicioService.get(id);
		servicio = servicioOptional.get();

		model.addAttribute("servicio", servicio);

		return "usuario/serviciohome";
	}
	//Añadir al carrito
	@PostMapping("/cart")
	public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model) {
		DetalleOrden detalleOrden = new DetalleOrden();
		Servicio servicio = servicioService.get(id)
				.orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

		detalleOrden.setServicio(servicio);
		detalleOrden.setPrecio(servicio.getPrecio());
		double totalDetalle = servicio.getPrecio() * cantidad;
		detalleOrden.setTotal(totalDetalle);
		detalleOrden.setNombre(servicio.getNombre());

		boolean yaIngresado = detalles.stream()
				.anyMatch(det -> det.getServicio().getId().equals(servicio.getId()));

		if (!yaIngresado) {
			detalles.add(detalleOrden);
		}

		double sumaTotal = detalles.stream().mapToDouble(DetalleOrden::getTotal).sum();
		orden.setTotal(sumaTotal);

		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}


	// Fucionalidad pagar con paypal
	@PostMapping("/pay")
	public String payment(@ModelAttribute("orden") Orden orden) {
		if (orden.getMethod() == null || orden.getMethod().isEmpty()) {
			orden.setMethod("paypal");
		}
		if (orden.getIntent() == null || orden.getIntent().isEmpty()) {
			orden.setIntent("sale");
		}
		if (orden.getDescripcion() == null) {
			orden.setDescripcion("Pago por servicio.");
		}

		// Formatea y convierte el total con el punto decimal
		String totalFormatted = String.format(Locale.US, "%.2f", orden.getTotal());
		Double totalDouble = Double.parseDouble(totalFormatted);

		try {
			Payment payment = service.createPayment(
					totalDouble,
					orden.getMoneda(),
					orden.getMethod(),
					orden.getIntent(),
					orden.getDescripcion(),
					"http://localhost:8080/payment/cancel",
					"http://localhost:8080/payment/success"
			);

			for (Links link : payment.getLinks()) {
				if (link.getRel().equals("approval_url")) {
					return "redirect:" + link.getHref();
				}
			}
		} catch (PayPalRESTException e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:/";
	}


	// cancelar pago paypal
	@GetMapping("/payment/cancel")
	public String cancelPay() {
		return "usuario/cancel";
	}
	//pago exitoso paypal
	@GetMapping("/payment/success")
	public String paymentSuccess(@RequestParam("paymentId") String paymentId,
								 @RequestParam("PayerID") String payerId,
								 HttpSession session,
								 Model model) {
		try {
			// Completar el pago con PayPal
			Payment payment = service.executePayment(paymentId, payerId);
			System.out.println(payment.toJSON());

			// Si el pago es aprobado
			if ("approved".equals(payment.getState())) {
				// Verificar que el atributo idusuario no sea nulo, la sesion debe estar iniciada para pagar
				if (session.getAttribute("idusuario") == null) {
					model.addAttribute("mensaje", "Usuario no autenticado.");
					return "usuario/login";  // Redirigir a la página de inicio de sesión
				}

				// Obtener el usuario de la sesión
				Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).orElse(null);
				if (usuario == null) {
					model.addAttribute("mensaje", "No se encontró el usuario.");
					return "redirect:/";
				}
				// Calcular el total de la orden
				double total = 0.0;
				for (DetalleOrden detalle : detalles) {
					total += detalle.getPrecio();
				}
				String moneda = "USD";
				String method = "paypal";
				String intent = "venta";
				String descripcion = "Compra de servicio de construcción";

				// Generar número único para la orden y guardar la orden en la base de datos
				orden.setNumero(ordenService.generarNumeroOrden());
				orden.setFechaCreacion(new Date());
				orden.setUsuario(usuario); // Asignar el usuario a la orden
				orden.setTotal(total);
				orden.setMoneda(moneda);
				orden.setMethod(method);
				orden.setIntent(intent);
				orden.setDescripcion(descripcion);

				orden = ordenService.save(orden);  // Guardar la orden en la base de datos
				log.info("Orden creada con ID: {}", orden.getId());

				// Asignar detalles de la orden a la orden y guardarlos en la base de datos
				for (DetalleOrden detalle : detalles) {
					detalle.setOrden(orden);  // Asociar el detalle a la orden
					detalleOrdenService.save(detalle);  // Guardar cada detalle en la base de datos
				}

				// Limpiar carrito y detalles
				detalles.clear();
				orden = new Orden();

				// Confirmar al usuario pago exitoso
				model.addAttribute("mensaje", "Pago completado y orden generada correctamente.");
				model.addAttribute("orden", orden);
				return "usuario/success";  // Redirige a una página de confirmación de pago
			}
		} catch (PayPalRESTException e) {
			e.printStackTrace();
			model.addAttribute("mensaje", "Error al procesar el pago."); // confirmar pago cancelado
		}

		return "redirect:/";
	}


	// quitar un servicio del carrito
	@GetMapping("/delete/cart/{id}")
	public String deleteServicioCart(@PathVariable Integer id, Model model) {

		// lista nueva de servicios
		List<DetalleOrden> ordenesNueva = new ArrayList<DetalleOrden>();

		for (DetalleOrden detalleOrden : detalles) {
			if (detalleOrden.getServicio().getId() != id) {
				ordenesNueva.add(detalleOrden);
			}
		}

		// poner la nueva lista con los servicios restantes
		detalles = ordenesNueva;

		double sumaTotal = 0;
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}


	//resumen de la orden
	@GetMapping("/getCart")
	public String getCart(Model model, HttpSession session) {
		
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		
		//sesion
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		return "/usuario/carrito";
	}
	//resumen de orden
	@GetMapping("/order")
	public String order(Model model, HttpSession session) {
		
		Usuario usuario =usuarioService.findById( Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("usuario", usuario);
		
		return "usuario/resumenorden";
	}
	//mostrar resumen orden
	@GetMapping("/resumenorden")
	public String mostrarResumenOrden(HttpSession session, Model model) {
		// Obtener el usuario a partir de la sesión
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		if (idUsuario != null) {
			Usuario usuario = usuarioService.findById(idUsuario).orElse(null);
			model.addAttribute("usuario", usuario);

		}
		// Agregar otros datos necesarios como la lista de detalles y la orden
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/resumenorden";
	}

	//Funcionalidad para buscar un servicio por nombre
	@PostMapping("/search")
	public String searchProduct(@RequestParam String nombre, Model model) {
		log.info("Nombre del servicio: {}", nombre);
		List<Servicio> servicios= servicioService.findAll().stream().filter( p -> p.getNombre().contains(nombre)).collect(Collectors.toList());
		model.addAttribute("servicios", servicios);
		return "usuario/home";
	}

	//funcionalidad enviar mensaje a correo del admin
	@PostMapping("/contacto/enviar")
	@ResponseBody
	public String enviarMensaje(@RequestParam String nombre,
								@RequestParam String email,
								@RequestParam String mensaje) {

		try {
			SimpleMailMessage emailMensaje = new SimpleMailMessage();
			emailMensaje.setFrom("jonathandrl2403@gmail.com");
			emailMensaje.setTo("jonathandrl2403@gmail.com");
			emailMensaje.setSubject("Nuevo mensaje de contacto");
			emailMensaje.setText("Nombre: " + nombre + "\nEmail: " + email + "\nMensaje: " + mensaje);

			mailSender.send(emailMensaje);

			return "Mensaje enviado exitosamente.";
		} catch (Exception e) {
			return "Error al enviar el mensaje.";
		}
	}

	@GetMapping("/home")
	public String homeDefault() {
		return "usuario/home";
	}
}


