package com.paymaster.controller;

import com.paymaster.service.IOrdenService;
import com.paymaster.service.IValidacionPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.paymaster.service.ValidacionPagoServiceImpl;


@Controller
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private IOrdenService ordenService;
    @Autowired
    private ValidacionPagoServiceImpl validacionService;

    @PostMapping("/procesarPago")
    public String procesarPago(@RequestParam("ordenId") Integer ordenId,
                               @RequestParam("metodoPago") String metodoPago,
                               @RequestParam("datosPago") String datosPago) {
        try {
            ordenService.actualizarMetodoPago(ordenId, metodoPago, datosPago);
            return "redirect:/ordenes"; // Redirige a la página de órdenes
        } catch (IllegalArgumentException e) {
            // Manejar la excepción de datos de pago inválidos
            return "redirect:/error";
        }
    }
}
