package com.streaming_app.EncodingService.API.Configuration;

//import com.azure.core.util.serializer.JsonSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import com.streaming_app.EncodingService.Application.Events.VideoEncodedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, VideoEncodedEvent> producerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, VideoEncodedEvent> kafkaTemplate(
            ProducerFactory<String, VideoEncodedEvent> producerFactory){

        return new KafkaTemplate<>(producerFactory);
    }

    // Published when encoding is complete
    // Streaming-Service consumes this
    @Bean
    public NewTopic videoEncodedTopic() {

        return new NewTopic(
                "video.encoded",
                3,
                (short) 1
        );
    }
}
