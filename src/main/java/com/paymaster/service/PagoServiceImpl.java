package com.paymaster.service;

import com.paymaster.model.MetodoPago;
import com.paymaster.model.Orden;
import com.paymaster.service.IValidacionPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagoServiceImpl {

    private final IValidacionPagoService validacionTarjetaCreditoService;
    private final IValidacionPagoService validacionPayPalService;

    @Autowired
    public PagoServiceImpl(ValidacionTarjetaCreditoServiceImpl validacionTarjetaCreditoService,
                           ValidacionPayPalServiceImpl validacionPayPalService) {
        this.validacionTarjetaCreditoService = validacionTarjetaCreditoService;
        this.validacionPayPalService = validacionPayPalService;
    }

    public boolean procesarPago(Orden orden, MetodoPago metodoPago) {
        if (metodoPago == MetodoPago.TARJETA_CREDITO) {
            return validacionTarjetaCreditoService.validarPago(orden, metodoPago);
        } else if (metodoPago == MetodoPago.PAYPAL) {
            return validacionPayPalService.validarPago(orden, metodoPago);
        }
        // Agregar más validaciones para otros métodos de pago
        return false;
    }
}
