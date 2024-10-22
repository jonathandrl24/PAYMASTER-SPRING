package com.paymaster.service;

import com.paymaster.model.MetodoPago;
import com.paymaster.model.Orden;
import com.paymaster.service.IValidacionPagoService;
import org.springframework.stereotype.Service;

@Service
public class ValidacionTarjetaCreditoServiceImpl implements IValidacionPagoService {

    @Override
    public boolean validarPago(Orden orden, MetodoPago metodoPago) {
        // Lógica de validación para tarjetas de crédito
        if (metodoPago == MetodoPago.TARJETA_CREDITO) {
            // Aquí se puede implementar la lógica de validación real
            System.out.println("Validando pago con tarjeta de crédito...");
            // Validar número de tarjeta, fecha de expiración, etc.
            return true; // Retorna true si la validación es exitosa
        }
        return false;
    }
}
