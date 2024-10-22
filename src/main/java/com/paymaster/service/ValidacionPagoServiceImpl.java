package com.paymaster.service;

import com.paymaster.model.MetodoPago;
import org.springframework.stereotype.Service;

@Service
public class ValidacionPagoServiceImpl {

    public boolean validarDatosPago(MetodoPago metodoPago, String datosPago) {
        switch (metodoPago) {
            case TARJETA_CREDITO:
            case TARJETA_DEBITO:
                return validarTarjeta(datosPago);

            case TRANSFERENCIA_BANCARIA:
                return validarTransferencia(datosPago);

            case PAYPAL:
                return validarPayPal(datosPago);

            default:
                throw new IllegalArgumentException("Método de pago no válido");
        }
    }

    private boolean validarTarjeta(String numeroTarjeta) {
        // Validación básica de tarjeta de crédito/débito
        return numeroTarjeta != null && numeroTarjeta.matches("\\d{16}");
    }

    private boolean validarTransferencia(String numeroCuenta) {
        // Validación básica de transferencia bancaria
        return numeroCuenta != null && numeroCuenta.matches("\\d{10,12}");
    }

    private boolean validarPayPal(String emailPayPal) {
        // Validación de una cuenta PayPal (simple validación de correo electrónico)
        return emailPayPal != null && emailPayPal.contains("@");
    }
}
