package com.bank.eventstore;

import com.bank.domain.event.CashDepositedEvent;
import com.bank.domain.event.CashWithdrawnEvent;
import com.bank.domain.event.Event;
import com.bank.domain.event.FeeChargedEvent;
import com.bank.domain.event.InterestAccruedEvent;
import com.bank.domain.event.LoanPaymentEvent;
import com.bank.domain.event.MasterAccountChangedEvent;
import com.bank.domain.event.ProductClosedEvent;
import com.bank.domain.event.ProductOpenedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class EventSerializer {

    private static final int MAX_EVENT_SIZE_BYTES = 1_048_576;
    private final ObjectMapper objectMapper;

    public EventSerializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.deactivateDefaultTyping();
    }

    public String serialize(Event event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            if (json.getBytes(StandardCharsets.UTF_8).length > MAX_EVENT_SIZE_BYTES)
                throw new SecurityException("Событие превышает максимальный размер");
            JsonNode node = objectMapper.readTree(json);
            ((ObjectNode) node).put("@type", event.getClass().getSimpleName());
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации", e);
        }
    }

    public Event deserialize(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            String type = node.get("@type").asText();
            // Удаляем @type перед десериализацией в конкретный класс
            ((ObjectNode) node).remove("@type");
            String cleanJson = objectMapper.writeValueAsString(node);
            return switch (type) {
                case "CashDepositedEvent" -> objectMapper.readValue(cleanJson, CashDepositedEvent.class);
                case "CashWithdrawnEvent" -> objectMapper.readValue(cleanJson, CashWithdrawnEvent.class);
                case "InterestAccruedEvent" -> objectMapper.readValue(cleanJson, InterestAccruedEvent.class);
                case "FeeChargedEvent" -> objectMapper.readValue(cleanJson, FeeChargedEvent.class);
                case "ProductOpenedEvent" -> objectMapper.readValue(cleanJson, ProductOpenedEvent.class);
                case "ProductClosedEvent" -> objectMapper.readValue(cleanJson, ProductClosedEvent.class);
                case "LoanPaymentEvent" -> objectMapper.readValue(cleanJson, LoanPaymentEvent.class);
                case "MasterAccountChangedEvent" -> objectMapper.readValue(cleanJson, MasterAccountChangedEvent.class);
                default -> throw new SecurityException("Неизвестный тип: " + type);
            };
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка десериализации", e);
        }
    }

    public String serializeToString(Event event) {
    return serialize(event);
}
}