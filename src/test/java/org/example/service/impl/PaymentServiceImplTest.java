package org.example.service.impl;

import org.example.dto.payment.PaymentResultDto;
import org.example.entity.Order;
import org.example.entity.Transaction;
import org.example.entity.User;
import org.example.entity.Wallet;
import org.example.entity.enumerator.OrderStatus;
import org.example.mapper.TransactionMapper;
import org.example.mapper.WalletMapper;
import org.example.service.OrderService;
import org.example.service.TransactionService;
import org.example.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    WalletService walletService;
    @Mock
    TransactionService transactionService;
    @Mock
    OrderService orderService;
    @Mock
    WalletMapper walletMapper;
    @Mock
    TransactionMapper transactionMapper;

    @InjectMocks
    PaymentServiceImpl paymentService;

    @Test
    void payForOrder_successfulFlow() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.COMPLETED);
        order.setPaid(false);
        order.setTotalPrice(100.0);
        User customer = new User(); customer.setId(10L);
        Wallet customerWallet = new Wallet(); customerWallet.setBalance(200.0);
        User expert = new User(); expert.setId(20L);
        Wallet expertWallet = new Wallet(); expertWallet.setBalance(50.0);

        order.setCustomer(customer);
        order.setExpert(expert);

        when(orderService.findById(1L)).thenReturn(Optional.of(order));
        when(walletService.findByUser_Id(10L)).thenReturn(Optional.of(customerWallet));
        when(walletService.findByUser_Id(20L)).thenReturn(Optional.of(expertWallet));

        // Act
        PaymentResultDto result = paymentService.payForOrder(1L);

        // Assert
        assertTrue(result.success());
        assertEquals("Successfully paid, 70% transferred to expert.", result.message());
        assertEquals(100.0, 200.0 - customerWallet.getBalance(), 0.001);
        assertEquals(50.0 + 70.0, expertWallet.getBalance(), 0.001);

        verify(transactionService, times(2)).save(any(Transaction.class));
        verify(walletService, times(1)).save(customerWallet);
        verify(walletService, times(1)).save(expertWallet);
        verify(orderService).save(order);
        assertTrue(order.isPaid());
    }

    @Test
    void payForOrder_orderNotFound() {
        when(orderService.findById(5L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> paymentService.payForOrder(5L));
    }

    @Test
    void payForOrder_invalidStatus() {
        Order o = new Order(); o.setStatus(OrderStatus.PENDING_PROPOSAL); o.setCustomer(new User());
        when(orderService.findById(1L)).thenReturn(Optional.of(o));
        PaymentResultDto r = paymentService.payForOrder(1L);
        assertFalse(r.success());
        assertTrue(r.message().contains("status invalid"));
    }

    @Test
    void payForOrder_alreadyPaid() {
        Order o = new Order(); o.setStatus(OrderStatus.COMPLETED); o.setPaid(true); o.setCustomer(new User());
        when(orderService.findById(1L)).thenReturn(Optional.of(o));
        PaymentResultDto r = paymentService.payForOrder(1L);
        assertFalse(r.success());
        assertTrue(r.message().contains("already paid"));
    }

    @Test
    void payForOrder_customerWalletNotFound() {
        Order o = new Order(); o.setStatus(OrderStatus.COMPLETED); o.setCustomer(new User());
        when(orderService.findById(1L)).thenReturn(Optional.of(o));
        when(walletService.findByUser_Id(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> paymentService.payForOrder(1L));
    }

    @Test
    void payForOrder_totalPriceNull() {
        Order o = new Order(); o.setStatus(OrderStatus.COMPLETED); o.setCustomer(new User());
        o.setTotalPrice(null);
        when(orderService.findById(1L)).thenReturn(Optional.of(o));
        when(walletService.findByUser_Id(any())).thenReturn(Optional.of(new Wallet()));
        PaymentResultDto r = paymentService.payForOrder(1L);
        assertFalse(r.success());
        assertTrue(r.message().contains("total price is not set"));
    }

    @Test
    void payForOrder_notEnoughAmount() {
        Order o = new Order(); o.setStatus(OrderStatus.COMPLETED); o.setTotalPrice(100.0);
        User c = new User(); c.setId(11L);
        o.setCustomer(c);
        Wallet w = new Wallet(); w.setBalance(10.0);
        when(orderService.findById(1L)).thenReturn(Optional.of(o));
        when(walletService.findByUser_Id(11L)).thenReturn(Optional.of(w));
        PaymentResultDto r = paymentService.payForOrder(1L);
        assertFalse(r.success());
        assertTrue(r.message().contains("Not enough amount"));
    }

    @Test
    void payForOrder_noExpertAssigned() {
        Order o = new Order(); o.setStatus(OrderStatus.COMPLETED); o.setTotalPrice(50.0);
        User c = new User(); c.setId(11L);
        o.setCustomer(c);
        Wallet w = new Wallet(); w.setBalance(100.0);
        when(orderService.findById(any())).thenReturn(Optional.of(o));
        when(walletService.findByUser_Id(11L)).thenReturn(Optional.of(w));
        assertThrows(NotFoundException.class, () -> paymentService.payForOrder(1L));
    }

    @Test
    void payForOrder_expertWalletNotFound() {
        Order o = new Order(); o.setStatus(OrderStatus.COMPLETED); o.setTotalPrice(50.0);
        User c = new User(); c.setId(11L); o.setCustomer(c);
        User e = new User(); e.setId(22L); o.setExpert(e);
        Wallet wc = new Wallet(); wc.setBalance(100.0);
        when(orderService.findById(any())).thenReturn(Optional.of(o));
        when(walletService.findByUser_Id(11L)).thenReturn(Optional.of(wc));
        when(walletService.findByUser_Id(22L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> paymentService.payForOrder(1L));
    }
}
