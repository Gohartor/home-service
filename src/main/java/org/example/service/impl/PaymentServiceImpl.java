package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.payment.PaymentRequestDto;
import org.example.dto.payment.PaymentResultDto;
import org.example.entity.Order;
import org.example.entity.Transaction;
import org.example.entity.User;
import org.example.entity.Wallet;
import org.example.entity.enumerator.OrderStatus;
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
import java.util.Optional;

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
    public PaymentResultDto payForOrder(Long orderId) {
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        User customer = order.getCustomer();


        if (!OrderStatus.COMPLETED.equals(order.getStatus()))
            return new PaymentResultDto(false, "Can not complete payment (order status invalid)");

        if (order.isPaid())
            return new PaymentResultDto(false, "Order is already paid");

        Wallet customerWallet = walletService.findByUser_Id(customer.getId())
                .orElseThrow(() -> new NotFoundException("Customer wallet not found"));

        Double amount = order.getTotalPrice();
        if (amount == null) {
            return new PaymentResultDto(false, "Order total price is not set.");
        }

        if (customerWallet.getBalance() < amount)
            return new PaymentResultDto(false, "Not enough amount");

        customerWallet.setBalance(customerWallet.getBalance() - amount);

        Transaction customerTransaction = new Transaction();
        customerTransaction.setAmount(-amount);
        customerTransaction.setType(TransactionType.PAYMENT);
        customerTransaction.setRelatedOrderId(order.getId());
        customerTransaction.setWallet(customerWallet);
        transactionService.save(customerTransaction);

        User expert = order.getExpert();
        if (expert == null)
            throw new NotFoundException("Order does not have an expert assigned");
        Wallet expertWallet = walletService.findByUser_Id(expert.getId())
                .orElseThrow(() -> new NotFoundException("Expert wallet not found"));

        double expertShare = Math.floor(amount * 0.7 * 100) / 100.0;
        expertWallet.setBalance(expertWallet.getBalance() + expertShare);

        Transaction expertTransaction = new Transaction();
        expertTransaction.setAmount(expertShare);
        expertTransaction.setType(TransactionType.PAYMENT);
        expertTransaction.setRelatedOrderId(order.getId());
        expertTransaction.setWallet(expertWallet);
        transactionService.save(expertTransaction);


        order.setPaid(true);

        applyLatePenaltyOrReward(order);

        walletService.save(customerWallet);
        walletService.save(expertWallet);
        orderService.save(order);

        return new PaymentResultDto(true, "Successfully paid, 70% transferred to expert.");
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
