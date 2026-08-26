package com.streaming_app.ContentService.API.Configurations;

import com.streaming_app.ContentService.Application.Events.VideoEncodedEvent;
import com.streaming_app.ContentService.Application.Events.VideoUploadedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> consumerConfigs() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        props.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        props.put(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                false
        );

        props.put(
                ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,
                7200000
        );

        props.put(
                ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                1
        );

        return props;

    }

    /*
     * VideoEncodedEvent Consumer Factory
     */

    @Bean
    public ConsumerFactory<String, VideoEncodedEvent>
    videoEncodedConsumerFactory() {

        JacksonJsonDeserializer<VideoEncodedEvent> deserializer =
                new JacksonJsonDeserializer<>(
                        VideoEncodedEvent.class
                );

        deserializer.addTrustedPackages(
                "com.streaming_app.ContentService.Application.Events"
        );

        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<
            String,
            VideoEncodedEvent
            > videoEncodedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<
                String,
                VideoEncodedEvent
                >
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                videoEncodedConsumerFactory()
        );

        return factory;
    }

    /*
     * VideoUploadedEvent Consumer Factory
     */

    @Bean
    public ConsumerFactory<String, VideoUploadedEvent>
    videoUploadedConsumerFactory() {

        JacksonJsonDeserializer<VideoUploadedEvent> deserializer =
                new JacksonJsonDeserializer<>(
                        VideoUploadedEvent.class
                );

        deserializer.addTrustedPackages(
                "com.streaming_app.ContentService.Application.Events"
        );

        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<
            String,
            VideoUploadedEvent
            > videoUploadedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<
                String,
                VideoUploadedEvent
                >
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                videoUploadedConsumerFactory()
        );

        return factory;
    }
}
