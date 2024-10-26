package com.paymaster.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.paymaster.model.Orden;
import com.paymaster.model.Usuario;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Integer> {
	List<Orden> findByUsuario (Usuario usuario);
	List<Orden> findByFechaCreacionBetween(Date fechaInicio, Date fechaFin);
	// Obtener las órdenes más recientes
	List<Orden> findTop5ByOrderByFechaCreacionDesc();
	@Query("SELECT new map(FUNCTION('DATE', o.fechaCreacion) as fecha, SUM(o.total) as ganancia) " +
			"FROM Orden o WHERE o.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
			"GROUP BY FUNCTION('DATE', o.fechaCreacion) ORDER BY FUNCTION('DATE', o.fechaCreacion)")
	List<Map<String, Object>> findGananciasDiarias(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);
}
