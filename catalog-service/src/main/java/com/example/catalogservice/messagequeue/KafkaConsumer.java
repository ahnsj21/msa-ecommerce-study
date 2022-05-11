package com.example.catalogservice.messagequeue;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.jpa.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CatalogRepository catalogRepository;

    /**
     * topic listen
     * @param kafkaMessage
     */
    @KafkaListener(topics = "example-order-topic")
    @Transactional
    public void updateQty(String kafkaMessage) {
        log.info("kafka message : -> {}", kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String productId = (String) map.get("productId");
        int qty = (Integer) map.get("qty");
        CatalogEntity catalogEntity = catalogRepository.findByProductId(productId);
        if (catalogEntity != null) {
            catalogEntity.setStock(catalogEntity.getStock() - qty);
        }
    }
}
