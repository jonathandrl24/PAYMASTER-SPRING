package com.paymaster.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
	// generar orden
	@Override
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

	// Método para obtener las ganancias totales
	public double calcularGananciasTotales() {
		List<Orden> ordenesCompletadas = ordenRepository.findAll(); // podría filtrar por estado 'completado'
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

	@Override
	public List<Map<String, Object>> calcularGananciasDiarias(Date fechaInicio, Date fechaFin) {
		return ordenRepository.findGananciasDiarias(fechaInicio, fechaFin);
	}

	// Descargar Reporte Ordenes Excel
	public ByteArrayInputStream generarReporteExcel() throws IOException {
		String[] columnas = {"ID", "Total", "Número", "Fecha de Creación", "Moneda", "Método de Pago", "Descripción"};

		// Crear el workbook y la hoja
		Workbook workbook = new XSSFWorkbook();
		Sheet hoja = workbook.createSheet("Órdenes");

		// Crear el encabezado de la hoja
		Row encabezadoFila = hoja.createRow(0);
		for (int i = 0; i < columnas.length; i++) {
			Cell cell = encabezadoFila.createCell(i);
			cell.setCellValue(columnas[i]);
		}

		// Obtener todas las órdenes
		List<Orden> ordenes = ordenRepository.findAll();
		int filaIdx = 1;

		// Rellenar filas con datos de órdenes
		for (Orden orden : ordenes) {
			Row fila = hoja.createRow(filaIdx++);

			fila.createCell(0).setCellValue(orden.getId());
			fila.createCell(1).setCellValue(orden.getTotal());
			fila.createCell(2).setCellValue(orden.getNumero());
			fila.createCell(3).setCellValue(orden.getFechaCreacion() != null ? orden.getFechaCreacion().toString() : "Sin fecha"); // Verificar si es null
			fila.createCell(4).setCellValue(orden.getMoneda());
			fila.createCell(5).setCellValue(orden.getMethod());
			fila.createCell(6).setCellValue(orden.getDescripcion());
		}

		// Escribir datos a un ByteArrayOutputStream
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		workbook.close();

		return new ByteArrayInputStream(out.toByteArray());
	}


}

