package com.paymaster.service;

import java.util.List;
import java.util.Optional;

import com.paymaster.model.Servicio;

public interface ServicioService {
	public Servicio save(Servicio servicio);
	public Optional<Servicio> get(Integer id);
	public void update(Servicio servicio);
	public void delete(Integer id);
	public List<Servicio> findAll();
}
