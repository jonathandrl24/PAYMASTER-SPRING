package com.paymaster.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymaster.model.Orden;
import com.paymaster.model.Usuario;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Integer> {
	List<Orden> findByUsuario (Usuario usuario);
	List<Orden> findByFechaCreacionBetween(Date fechaInicio, Date fechaFin);
	// Obtener las órdenes más recientes
	List<Orden> findTop5ByOrderByFechaCreacionDesc();
}
