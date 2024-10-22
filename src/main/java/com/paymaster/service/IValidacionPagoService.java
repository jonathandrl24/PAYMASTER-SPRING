package com.paymaster.service;

import com.paymaster.model.MetodoPago;
import com.paymaster.model.Orden;

public interface IValidacionPagoService {
    boolean validarPago(Orden orden, MetodoPago metodoPago);
}
