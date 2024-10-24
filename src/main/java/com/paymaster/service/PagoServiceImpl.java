package com.paymaster.service;

import com.paymaster.model.MetodoPago;
import com.paymaster.model.Orden;
import com.paymaster.service.IValidacionPagoService;
import com.paypal.api.payments.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Service
public class PagoServiceImpl {

    private final IValidacionPagoService validacionTarjetaCreditoService;
    private final IValidacionPagoService validacionPayPalService;
    private final PaymentService paymentService;

    @Autowired
    public PagoServiceImpl(ValidacionTarjetaCreditoServiceImpl validacionTarjetaCreditoService,
                           ValidacionPayPalServiceImpl validacionPayPalService,
                           PaymentService paymentService) {
        this.validacionTarjetaCreditoService = validacionTarjetaCreditoService;
        this.validacionPayPalService = validacionPayPalService;
        this.paymentService = paymentService; // Inyecta PaymentService aquí
    }

    public boolean procesarPago(Orden orden, MetodoPago metodoPago) {
        if (metodoPago == MetodoPago.TARJETA_CREDITO) {
            return validacionTarjetaCreditoService.validarPago(orden, metodoPago);
        } else if (metodoPago == MetodoPago.PAYPAL) {
            // Aquí creamos el pago con PayPal antes de validarlo
            boolean isPagoCreado = crearPagoPayPal(orden);
            if (isPagoCreado) {
                return validacionPayPalService.validarPago(orden, metodoPago);
            } else {
                System.out.println("No se pudo crear el pago en PayPal.");
                return false;
            }
        }
        // Agregar más validaciones para otros métodos de pago
        return false;
    }

    private boolean crearPagoPayPal(Orden orden) {
        try {
            // Llamamos al servicio de creación de pago de PayPal
            Payment payment = paymentService.createPayment(orden, "http://localhost/cancel", "http://localhost/success");
            orden.setPaymentId(payment.getId());  // Asigna el paymentId a la orden
            return true;
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            return false;
        }
    }
}