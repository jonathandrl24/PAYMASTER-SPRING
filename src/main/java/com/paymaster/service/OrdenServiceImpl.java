package com.paymaster.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymaster.model.Orden;
import com.paymaster.model.Usuario;
import com.paymaster.repository.IOrdenRepository;

@Service
public class OrdenServiceImpl implements IOrdenService {
	
	@Autowired
	private IOrdenRepository ordenRepository;

	@Override
	public Orden save(Orden orden) {
		return ordenRepository.save(orden);
	}

	@Override
	public List<Orden> findAll() {
		return ordenRepository.findAll();
	}
	// 0000010
	public String generarNumeroOrden() {
		int numero=0;
		String numeroConcatenado="";
		
		List<Orden> ordenes = findAll();
		
		List<Integer> numeros= new ArrayList<Integer>();
		
		ordenes.stream().forEach(o -> numeros.add( Integer.parseInt( o.getNumero())));
		
		if (ordenes.isEmpty()) {
			numero=1;
		}else {
			numero= numeros.stream().max(Integer::compare).get();
			numero++;
		}
		
		if (numero<10) { //0000001000
			numeroConcatenado="000000000"+String.valueOf(numero);
		}else if(numero<100) {
			numeroConcatenado="00000000"+String.valueOf(numero);
		}else if(numero<1000) {
			numeroConcatenado="0000000"+String.valueOf(numero);
		}else if(numero<10000) {
			numeroConcatenado="0000000"+String.valueOf(numero);
		}		
		
		return numeroConcatenado;
	}

	@Override
	public List<Orden> findByUsuario(Usuario usuario) {
		return ordenRepository.findByUsuario(usuario);
	}

	@Override
	public Optional<Orden> findById(Integer id) {
		return ordenRepository.findById(id);
	}

	//(Eliminar todo abajo si sale mal)
	// Método para obtener las ganancias totales
	public double calcularGananciasTotales() {
		List<Orden> ordenesCompletadas = ordenRepository.findAll(); // Dependiendo de tu implementación, podrías filtrar por estado 'completado'
		return ordenesCompletadas.stream()
				.mapToDouble(Orden::getTotal) // Sumando el total de cada orden
				.sum();
	}

	// Método para obtener las ganancias por un período (opcional)
	public double calcularGananciasPorPeriodo(Date fechaInicio, Date fechaFin) {
		List<Orden> ordenesEnPeriodo = ordenRepository.findByFechaCreacionBetween(fechaInicio, fechaFin);
		return ordenesEnPeriodo.stream()
				.mapToDouble(Orden::getTotal)
				.sum();
	}

	public List<Orden> obtenerOrdenesRecientes() {
		// Llama al método del repositorio para obtener las órdenes más recientes
		return ordenRepository.findTop5ByOrderByFechaCreacionDesc();
	}

}
