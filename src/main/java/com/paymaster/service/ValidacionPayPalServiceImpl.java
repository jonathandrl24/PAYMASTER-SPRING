package com.paymaster.service;

import com.paymaster.model.MetodoPago;
import com.paymaster.model.Orden;
import com.paymaster.service.IValidacionPagoService;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidacionPayPalServiceImpl implements IValidacionPagoService {

    @Autowired
    private APIContext apiContext;

    @Override
    public boolean validarPago(Orden orden, MetodoPago metodoPago) {
        if (metodoPago == MetodoPago.PAYPAL) {
            System.out.println("Validando pago con PayPal...");
            try {
                // Obtener el ID del pago que se realizó (esto debe ser pasado a este método)
                String paymentId = orden.getPaymentId(); // Asumiendo que la orden tiene un campo para el ID del pago

                // Crear un PaymentExecution con el ID del pago
                PaymentExecution paymentExecution = new PaymentExecution();
                paymentExecution.setPayerId(orden.getPayerId()); // Payer ID debe ser parte de la orden

                // Obtener el pago desde PayPal
                Payment payment = Payment.get(apiContext, paymentId);

                // Ejecutar el pago
                Payment executedPayment = payment.execute(apiContext, paymentExecution);

                // Verificar el estado del pago
                if ("approved".equals(executedPayment.getState())) {
                    System.out.println("Pago validado con éxito.");
                    return true;
                } else {
                    System.out.println("El pago no fue aprobado.");
                }
            } catch (PayPalRESTException e) {
                e.printStackTrace();
                System.out.println("Error al validar el pago: " + e.getMessage());
            }
        }
        return false;
    }
}
