package org.example.service;

import org.example.dto.payment.PaymentRequestDto;
import org.example.dto.payment.PaymentResultDto;

public interface PaymentService {

    PaymentResultDto payForOrder(Long orderId);

}
