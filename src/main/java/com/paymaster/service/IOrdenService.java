package com.paymaster.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
	List<Orden> obtenerOrdenesRecientes();

	List<Map<String, Object>> calcularGananciasDiarias(Date fechaInicio, Date fechaFin);
	// Método para generar reporte en Excel
	ByteArrayInputStream generarReporteExcel() throws IOException;
}
