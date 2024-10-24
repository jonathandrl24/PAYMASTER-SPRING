package com.paymaster.service;

import com.paymaster.model.Orden;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private APIContext apiContext;

    // Método para crear un pago de PayPal
    public Payment createPayment(Orden orden, String cancelUrl, String successUrl) throws PayPalRESTException {
        // 1. Configurar la cantidad del pago
        Amount amount = new Amount();
        amount.setCurrency("USD");  // Aquí puedes usar la moneda que prefieras
        amount.setTotal(String.format("%.2f", orden.getTotal()));  // Importe total del pago

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
        return payment.create(apiContext);  // Crea el pago usando PayPal API
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
