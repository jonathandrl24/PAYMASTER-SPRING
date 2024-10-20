package com.paymaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymaster.model.Servicio;

@Repository
public interface IServicioRepository extends JpaRepository<Servicio, Integer> {

}
