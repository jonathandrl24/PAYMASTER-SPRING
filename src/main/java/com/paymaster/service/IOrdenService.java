package com.paymaster.service;

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
}