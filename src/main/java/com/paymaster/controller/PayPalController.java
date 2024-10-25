package com.paymaster.controller;

import com.paymaster.model.Orden;
import com.paymaster.service.OrdenServiceImpl;
import com.paymaster.service.PaymentService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/paypal")
public class PayPalController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrdenServiceImpl ordenService; // Inyecta el servicio para manejar las órdenes

    // URL donde el usuario iniciará el proceso de pago
    @PostMapping("/pago")
    public String pagar(Model model, @RequestParam("ordenId") Long ordenId) {
        // Obtiene la orden desde la base de datos usando el servicio
        // Convertir el Long a Integer antes de pasar al servicio
        Optional<Orden> optionalOrden = ordenService.findById(ordenId.intValue());
        if (!optionalOrden.isPresent()) {
            model.addAttribute("error", "Orden no encontrada");
            return "error";
        }

        Orden orden = optionalOrden.get();

        if (orden == null) {
            model.addAttribute("error", "Orden no encontrada");
            return "error"; // Manejo de error si la orden no existe
        }

        try {
            // Crear el pago con PayPal
            Payment payment = paymentService.createPayment(
                    orden,
                    "http://localhost:8080/paypal/cancelar",
                    "http://localhost:8080/paypal/confirmar"
            );

            // Redirigir a PayPal para aprobar el pago
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return "redirect:" + link.getHref(); // Redirige a la URL de PayPal para aprobar el pago
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al procesar el pago: " + e.getMessage());
            return "error";
        }

        return "redirect:/"; // Si algo falla, redirige a la página principal
    }

    // URL de éxito donde PayPal redirige después de aprobar el pago
    @GetMapping("/confirmar")
    public String confirmarPago(Model model, @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paymentService.executePayment(paymentId, payerId);
            if ("approved".equals(payment.getState())) {
                // El pago fue aprobado
                model.addAttribute("mensaje", "Pago realizado con éxito!");
                return "resultado_pago_exitoso"; // Muestra la vista de éxito
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al confirmar el pago: " + e.getMessage());
        }

        return "error"; // Si algo falla, muestra la vista de error
    }

    // URL de cancelación donde PayPal redirige si el usuario cancela el pago
    @GetMapping("/cancelar")
    public String cancelarPago(Model model) {
        model.addAttribute("mensaje", "El pago ha sido cancelado.");
        return "resultado_pago_cancelado"; // Muestra la vista de cancelación
    }
}
