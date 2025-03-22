package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/order-service")
public class OrderController {
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;
    private final OrderService orderService;
    private final Environment env;

    @GetMapping("/health_check")
    public String sataus(HttpServletRequest request){
        return String.format("It's Working in Order Service on Port %s", request.getServerPort());
    }
    
    // 주문 생성
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails){
        log.info("Before add orders data");
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = modelMapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        // jpa
        OrderDto createDto = orderService.createOrder(orderDto);
        ResponseOrder returnValue = modelMapper.map(createDto, ResponseOrder.class);

        /*  CircuitBreaker, 모니터링 테스트를 위해 주석 처리!
        // kafka
        // - orderId, totalPrice 입력!
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());
        ResponseOrder returnValue = modelMapper.map(orderDto, ResponseOrder.class);
        */
        // Send an order to the Kafka
        kafkaProducer.send("example-order-topic", orderDto);
        // orderProducer.send("orders", orderDto);

        log.info("After add orders data");

        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
    
    // 주문 목록 요청
    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getUserOrders(@PathVariable("userId") String userId){
        log.info("Before retrieved orders data");
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });
        log.info("After retrieved orders data");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
