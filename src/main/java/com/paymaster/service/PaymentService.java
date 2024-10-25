package com.paymaster.service;

import com.paymaster.model.Orden;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private APIContext apiContext;
    @Autowired
    private PaymentService paymentService;

    // Método para crear un pago de PayPal
    public Payment createPayment(Orden orden, String cancelUrl, String successUrl) throws PayPalRESTException {
        // 1. Configurar la cantidad del pago
        Amount amount = new Amount();
        amount.setCurrency("USD");  // Asegúrate de que la moneda sea correcta

        // Verifica que el total no sea nulo y sea positivo
        Double total = orden.getTotal(); // Asegúrate de que esto devuelva un Double
        if (total == null || total < 0) {
            throw new IllegalArgumentException("El total de la orden no puede ser nulo o negativo.");
        }

        // Formatea el total con dos decimales
        String totalFormatted = String.format("%.3f", total);
        System.out.println("Total formateado: " + totalFormatted); // Depuración
        amount.setTotal(totalFormatted);  // Importe total del pago

        // 2. Crear la transacción
        Transaction transaction = new Transaction();
        transaction.setDescription("Compra de servicios de construcción");  // Descripción del pago
        transaction.setAmount(amount);

        // 3. Agregar la transacción a la lista
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        // 4. Configurar el pagador (PayPal en este caso)
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        // 5. Configurar las URLs de redirección
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);  // URL para cancelar el pago
        redirectUrls.setReturnUrl(successUrl);  // URL de retorno después del éxito del pago

        // 6. Crear el objeto Payment y configurarlo
        Payment payment = new Payment();
        payment.setIntent("sale");  // "sale" indica que el pago es una venta directa
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);

        // 7. Crear el pago en PayPal
        try {
            return payment.create(apiContext);  // Crear el pago usando PayPal API
        } catch (PayPalRESTException e) {
            // Manejar errores de PayPal aquí
            System.err.println("Error al crear el pago: " + e.getMessage());
            throw e;
        }
    }




    // Método para ejecutar el pago después de la aprobación del usuario
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        // Obtener el pago usando el ID
        Payment payment = Payment.get(apiContext, paymentId);

        // Crear un PaymentExecution con el ID del pagador (payerId)
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        // Ejecutar el pago
        return payment.execute(apiContext, paymentExecution);
    }
}
