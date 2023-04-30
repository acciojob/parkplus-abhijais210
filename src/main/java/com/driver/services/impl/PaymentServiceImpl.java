package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Reservation reservation = reservationRepository2.findById(reservationId).get();
        Payment payment = new Payment();
        //now check if it is a valid mode of transaction or not
        if(!mode.equalsIgnoreCase(String.valueOf(PaymentMode.CARD))
                && !mode.equalsIgnoreCase(String.valueOf(PaymentMode.UPI))
                && !mode.equalsIgnoreCase(String.valueOf(PaymentMode.CASH))){
            throw new Exception("Payment mode not detected");
        }

        //if amount sent insufficient
        int totalAmount = reservation.getSpot().getPricePerHour() * reservation.getNumberOfHours();
        if(amountSent < totalAmount)
            throw new Exception("Insufficient Amount");

        payment.setReservation(reservation);
        payment.setPaymentCompleted(true);
        payment.setPaymentMode(PaymentMode.valueOf(mode.toUpperCase()));

        reservation.setPayment(payment);

        return paymentRepository2.save(payment);
    }
}
