package com.curso.ecommerce.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.curso.ecommerce.model.Servicio;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.IUsuarioService;
import com.curso.ecommerce.service.ServicioService;
import com.curso.ecommerce.service.UploadFileService;

@Controller
@RequestMapping("/servicios")
public class ServicioController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(ServicioController.class);
	
	@Autowired
	private ServicioService servicioService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private UploadFileService upload;
	
	@GetMapping("")
	public String show(Model model) {
		model.addAttribute("servicios", servicioService.findAll());
		return "servicios/show";
	}
	
	@GetMapping("/create")
	public String create() {
		return "servicios/create";
	}
	
	@PostMapping("/save")
	public String save(Servicio servicio, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
		LOGGER.info("Este es el objeto servicio {}",servicio);
		
		
		Usuario u= usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString() )).get();
		servicio.setUsuario(u);
		
		//imagen
		if (servicio.getId()==null) { // cuando se crea un producto
			String nombreImagen= upload.saveImage(file);
			servicio.setImagen(nombreImagen);
		}else {
			
		}
		
		servicioService.save(servicio);
		return "redirect:/servicios";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		Servicio servicio= new Servicio();
		Optional<Servicio> optionalServicio=servicioService.get(id);
		servicio= optionalServicio.get();
		
		LOGGER.info("Servicio buscado: {}",servicio);
		model.addAttribute("servicio", servicio);
		
		return "servicios/edit";
	}
	
	@PostMapping("/update")
	public String update(Servicio servicio, @RequestParam("img") MultipartFile file ) throws IOException {
		Servicio p= new Servicio();
		p=servicioService.get(servicio.getId()).get();
		
		if (file.isEmpty()) { // se edita el producto pero no se cambia la imagem
			
			servicio.setImagen(p.getImagen());
		}else {// cuando se edita tbn la imagen			
			//eliminar cuando no sea la imagen por defecto
			if (!p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			String nombreImagen= upload.saveImage(file);
			servicio.setImagen(nombreImagen);
		}
		servicio.setUsuario(p.getUsuario());
		servicioService.update(servicio);
		return "redirect:/servicios";
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		
		Servicio p = new Servicio();
		p=servicioService.get(id).get();
		
		//eliminar cuando no sea la imagen por defecto
		if (!p.getImagen().equals("default.jpg")) {
			upload.deleteImage(p.getImagen());
		}
		
		servicioService.delete(id);
		return "redirect:/productos";
	}
	
	
}
