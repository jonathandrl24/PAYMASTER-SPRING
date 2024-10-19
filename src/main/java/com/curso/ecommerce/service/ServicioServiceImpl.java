package com.curso.ecommerce.service;

import java.util.List;
import java.util.Optional;

import com.curso.ecommerce.repository.IServicioRepository;
import com.curso.ecommerce.model.Servicio;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ServicioServiceImpl implements ServicioService{
	
	@Autowired
	private IServicioRepository servicioRepository;

	@Override
	public Servicio save(Servicio servicio) {
		return servicioRepository.save(servicio);
	}

	@Override
	public Optional<Servicio> get(Integer id) {
		return servicioRepository.findById(id);
	}

	@Override
	public void update(Servicio servicio) {
		servicioRepository.save(servicio);
	}

	@Override
	public void delete(Integer id) {
		servicioRepository.deleteById(id);
	}

	@Override
	public List<Servicio> findAll() {
		return servicioRepository.findAll();
	}

}
