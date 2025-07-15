package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.payment.PaymentRequestDto;
import org.example.dto.payment.PaymentResultDto;
import org.example.entity.Order;
import org.example.entity.Transaction;
import org.example.entity.User;
import org.example.entity.Wallet;
import org.example.entity.enumerator.ServiceStatus;
import org.example.entity.enumerator.TransactionType;
import org.example.mapper.TransactionMapper;
import org.example.mapper.WalletMapper;
import org.example.service.OrderService;
import org.example.service.PaymentService;
import org.example.service.TransactionService;
import org.example.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.Duration;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final WalletService walletService;
    private final TransactionService transactionService;
    private final OrderService orderService;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;

    @Override
    public PaymentResultDto payForOrder(Long userId, PaymentRequestDto paymentRequest) {

        Order order = orderService.findById(paymentRequest.orderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getCustomer().getId().equals(userId))
            return new PaymentResultDto(false, "you do not own the order");
        if (!order.getStatus().equals(ServiceStatus.COMPLETED))
            return new PaymentResultDto(false, "can not complete payment (not order found)");
        if (order.isPaid())
            return new PaymentResultDto(false, "cost this order is paid");

        Wallet wallet = walletService.findById(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found"));


        Double amount = order.getTotalPrice();
        if (wallet.getBalance() < amount)
            return new PaymentResultDto(false, "not enough amount");


        wallet.setBalance(wallet.getBalance() - amount);
        Transaction transaction = new Transaction();
        transaction.setAmount(-amount);
        transaction.setType(TransactionType.PAYMENT);
        transaction.setRelatedOrderId(order.getId());
        transaction.setWallet(wallet);
        transactionService.save(transaction);


        order.setStatus(ServiceStatus.COMPLETED);
        order.setPaid(true);

        applyLatePenaltyOrReward(order);


        walletService.save(wallet);
        orderService.save(order);

        return new PaymentResultDto(true, "successfully paid");
    }


    private void applyLatePenaltyOrReward(Order order) {
        ZonedDateTime expected = order.getExpectedDoneAt();
        ZonedDateTime actual = order.getDoneAt();
        if (expected != null && actual != null && actual.isAfter(expected)) {
            long hoursLate = Duration.between(expected, actual).toHours();
            if (hoursLate > 0) {

                User expert = order.getExpert();
                expert.addNegativeScore(hoursLate);

            }
        }

    }
}
