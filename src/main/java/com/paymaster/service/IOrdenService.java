package com.paymaster.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.paymaster.model.Orden;
import com.paymaster.model.Usuario;

public interface IOrdenService {
	List<Orden> findAll();
	Optional<Orden> findById(Integer id);
	Orden save (Orden orden);
	String generarNumeroOrden();
	List<Orden> findByUsuario (Usuario usuario);
	double calcularGananciasTotales();
	double calcularGananciasPorPeriodo(Date fechaInicio, Date fechaFin);
	//(ELIMINAR TODO ABAJO SI NO FUNCIONA)
	// Métodos para gestionar el pago
	public void actualizarMetodoPago(Integer ordenId, String metodoPago, String datosPago);
		// Implementa la lógica para actualizar el método de pago y los datos adicionales
	void actualizarEstadoPago(Integer ordenId, String estadoPago);

	List<Orden> obtenerOrdenesRecientes();
}
