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
		// Verificar si la orden ya tiene un ID (es decir, ya ha sido creada)
		if (orden.getId() == null) {
			orden.setNumero(ordenService.generarNumeroOrden());  // Generar número único para la orden
			orden = ordenService.save(orden);  // Guardar la orden y obtener el ID generado
			log.info("Orden creada con ID: {}", orden.getId());
		}

		// Detalle de la orden que representa el producto/servicio en el carrito
		DetalleOrden detalleOrden = new DetalleOrden();
		Servicio servicio = servicioService.get(id)
				.orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

		// Asignar el detalle a la orden
		detalleOrden.setServicio(servicio);
		detalleOrden.setPrecio(servicio.getPrecio());

		// Calcula el total del detalle de la orden basado en la cantidad
		double totalDetalle = servicio.getPrecio() * cantidad;
		detalleOrden.setTotal(totalDetalle);
		detalleOrden.setNombre(servicio.getNombre());

		// Validar si el producto ya está en el carrito
		boolean yaIngresado = detalles.stream()
				.anyMatch(det -> det.getServicio().getId().equals(servicio.getId()));

		if (!yaIngresado) {
			detalles.add(detalleOrden);
		}

		// Calcular el total de la orden
		double sumaTotal = detalles.stream().mapToDouble(DetalleOrden::getTotal).sum();
		orden.setTotal(sumaTotal);  // Asegúrate de que 'sumaTotal' sea un double bien formado

		// Mensajes de depuración
		log.info("Total del detalle: {}", totalDetalle);
		log.info("Suma total de la orden: {}", sumaTotal);
		log.info("ID de la orden: {}", orden.getId());

		// Actualizar el modelo
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";
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
	
	// guardar la orden
	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session ) {
		Date fechaCreacion = new Date();
		orden.setFechaCreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		
		//usuario
		Usuario usuario =usuarioService.findById( Integer.parseInt(session.getAttribute("idusuario").toString())  ).get();
		
		orden.setUsuario(usuario);
		ordenService.save(orden);
		
		//guardar detalles
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
