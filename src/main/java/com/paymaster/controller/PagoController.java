package com.paymaster.controller;

import com.paymaster.model.MetodoPago;
import com.paymaster.model.Orden;
import com.paymaster.service.IOrdenService;
import com.paymaster.service.IValidacionPagoService;
import com.paymaster.service.PagoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.paymaster.service.ValidacionPagoServiceImpl;

import java.util.Optional;


@Controller
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private IOrdenService ordenService;
    @Autowired
    private ValidacionPagoServiceImpl validacionService;
    @Autowired
    private PagoServiceImpl pagoServiceImpl;

    @PostMapping("/procesarPago")
    public String procesarPago(@RequestParam("orderId") Long orderId,
                               @RequestParam("metodoPago") String metodoPago,
                               @RequestParam("amount") Double amount) {
        // Convertir el Long a Integer si es necesario
        Integer orderIdInteger = Math.toIntExact(orderId);

        // Obtener la orden del servicio
        Orden orden = ordenService.findById(orderIdInteger)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        // Procesar el pago
        if (metodoPago.equals("PAYPAL")) {
            boolean pagoExitoso = pagoServiceImpl.procesarPago(orden, MetodoPago.PAYPAL);

            if (pagoExitoso) {
                return "redirect:/confirmacionPago";
            } else {
                return "redirect:/errorPago";
            }
        }
        return "redirect:/errorPago";
    }
}

