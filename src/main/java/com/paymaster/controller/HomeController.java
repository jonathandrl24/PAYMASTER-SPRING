package com.paymaster.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

	public static final String SUCCESS_URL = "pay/success";
	public static final String CANCEL_URL = "pay/cancel";


	// para almacenar los detalles de la orden
	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

	// datos de la orden
	Orden orden = new Orden();

	@GetMapping("")
	public String home(Model model, HttpSession session) {
		
		log.info("Sesion del usuario: {}", session.getAttribute("idusuario"));
		
		model.addAttribute("servicios", servicioService.findAll());
		
		//session
		model.addAttribute("sesion", session.getAttribute("idusuario"));

		return "usuario/home";
	}

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


	// paypal pagar
	@PostMapping("/pay")
	public String payment(@ModelAttribute("orden") Orden orden) {
		// Establecer valores predeterminados si son nulos
		if (orden.getMetodo() == null) {
			orden.setMetodo("paypal");
		}
		if (orden.getIntent() == null) {
			orden.setIntent("sale");  // O el valor que necesites
		}
		if (orden.getDescripcion() == null) {
			orden.setDescripcion("Pago por servicio."); // Valor predeterminado
		}

		try {
			Payment payment = service.createPayment(
					orden.getTotal(),
					orden.getMoneda(),
					orden.getMetodo(),
					orden.getIntent(),
					orden.getDescripcion(),
					"http://localhost:8080/payment/cancel",
					"http://localhost:8080/payment/success"
			);

			// Verificar el enlace de aprobación y redirigir
			for (Links link : payment.getLinks()) {
				if (link.getRel().equals("approval_url")) {
					return "redirect:" + link.getHref();
				}
			}
		} catch (PayPalRESTException e) {
			e.printStackTrace();  // Esto ayuda a detectar el error específico
		}

		// Redirigir a la página principal en caso de error
		return "redirect:/";
	}


	// cancelar pago paypal
	@GetMapping(value = CANCEL_URL)
	public String cancelPay() {
		return "cancel";
	}
	//pago exitoso
	@GetMapping(value = SUCCESS_URL)
	public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
		try {
			Payment payment = service.executePayment(paymentId, payerId);
			System.out.println(payment.toJSON());
			if (payment.getState().equals("approved")) {
				return "success";
			}
		} catch (PayPalRESTException e) {
			System.out.println(e.getMessage());
		}
		return "redirect:/";
	}


	// URL de éxito del pago paypal
	@GetMapping("/payment/success")
	public String paymentSuccess(@RequestParam("paymentId") String paymentId,
								 @RequestParam("PayerID") String payerId,
								 Model model) {
		try {
			// Completar el pago con PayPal
			Payment payment = service.executePayment(paymentId, payerId);

			// Si el pago es aprobado
			if ("approved".equals(payment.getState())) {
				// Generar número único para la orden y guardarla en la base de datos
				orden.setNumero(ordenService.generarNumeroOrden());
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

				// Confirmar al usuario
				model.addAttribute("mensaje", "Pago completado y orden generada correctamente.");
				return "usuario/confirmacionPago";
			}
		} catch (PayPalRESTException e) {
			e.printStackTrace();
			model.addAttribute("mensaje", "Error al procesar el pago.");
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
	
	// guardar(generar)la orden
	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session ) {
		Date fechaCreacion = new Date();
		orden.setFechaCreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		
		//usuario
		Usuario usuario =usuarioService.findById( Integer.parseInt(session.getAttribute("idusuario").toString())  ).get();
		
		orden.setUsuario(usuario);
		ordenService.save(orden);
		
		//guardar detalles de la orden
		for (DetalleOrden dt:detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}
		
		///limpiar lista y orden
		orden = new Orden();
		detalles.clear();
		
		return "redirect:/";
	}
	//Funcionalidad para buscar un servicio por nombre
	@PostMapping("/search")
	public String searchProduct(@RequestParam String nombre, Model model) {
		log.info("Nombre del servicio: {}", nombre);
		List<Servicio> servicios= servicioService.findAll().stream().filter( p -> p.getNombre().contains(nombre)).collect(Collectors.toList());
		model.addAttribute("servicios", servicios);
		return "usuario/home";
	}

}
