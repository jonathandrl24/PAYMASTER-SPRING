package com.paymaster.service;

import com.paymaster.model.MetodoPago;
import com.paymaster.model.Orden;
import com.paymaster.service.IValidacionPagoService;
import org.springframework.stereotype.Service;

@Service
public class ValidacionPayPalServiceImpl implements IValidacionPagoService {

    @Override
    public boolean validarPago(Orden orden, MetodoPago metodoPago) {
        // Lógica de validación para PayPal
        if (metodoPago == MetodoPago.PAYPAL) {
            System.out.println("Validando pago con PayPal...");
            // Implementar la lógica específica de PayPal
            return true; // Retorna true si la validación es exitosa
        }
        return false;
    }
}
