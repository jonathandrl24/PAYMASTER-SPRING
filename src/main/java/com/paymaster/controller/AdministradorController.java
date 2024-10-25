package com.paymaster.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.paymaster.model.Orden;
import com.paymaster.model.Servicio;
import com.paymaster.service.IOrdenService;
import com.paymaster.service.IUsuarioService;
import com.paymaster.service.ServicioService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

	@Autowired
	private ServicioService servicioService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordensService;

	private Logger logg= LoggerFactory.getLogger(AdministradorController.class);

	@GetMapping("")
	public String home(Model model) {

		List<Servicio> servicios = servicioService.findAll();
		model.addAttribute("servicios", servicios);


		return "administrador/home";
	}
	
	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios", usuarioService.findAll());
		return "administrador/usuarios";
	}
	
	@GetMapping("/ordenes")
	public String ordenes(Model model) {
		model.addAttribute("ordenes", ordensService.findAll());
		return "administrador/ordenes";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalle(Model model, @PathVariable Integer id) {
		logg.info("Id de la orden {}",id);
		Orden orden= ordensService.findById(id).get();
		
		model.addAttribute("detalles", orden.getDetalle());
		
		return "administrador/detalleorden";
	}

	// IMPLEMENTACION DASHBOARD
	// Endpoint para obtener ganancias totales
	@GetMapping("/ganancias")
	public ResponseEntity<Double> obtenerGananciasTotales() {
		double ganancias = ordensService.calcularGananciasTotales();
		return ResponseEntity.ok(ganancias);
	}

	// Endpoint para obtener ganancias por un período
	@GetMapping("/ganancias/periodo")
	public ResponseEntity<Double> obtenerGananciasPorPeriodo(@RequestParam("inicio") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
															 @RequestParam("fin") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
		double ganancias = ordensService.calcularGananciasPorPeriodo(fechaInicio, fechaFin);
		return ResponseEntity.ok(ganancias);
	}

	@GetMapping("/dashboard")
	public String getDashboard(Model model) {
		double gananciasTotales = ordensService.calcularGananciasTotales();
		List<Orden> ordenesRecientes = ordensService.obtenerOrdenesRecientes();

		// Pasar los datos al modelo para que sean utilizados en la vista
		model.addAttribute("gananciasTotales", gananciasTotales);
		model.addAttribute("ordenesRecientes", ordenesRecientes);

		return "administrador/dashboard"; // la vista estará en templates/administrador/dashboard.html
	}

}
