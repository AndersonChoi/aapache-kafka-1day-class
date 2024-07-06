package com.dvwy.steams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import org.apache.kafka.streams.kstream.KTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KTablePrint {
    private final static Logger logger = LoggerFactory.getLogger(KTablePrint.class);

    private static String APPLICATION_NAME = "streams-application";
    private static String BOOTSTRAP_SERVERS = "my-kafka:9092";
    private static String WEATHER_TOPIC = "weather";

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KTable<String, String> weatherTable = builder.table(WEATHER_TOPIC, Materialized.as(WEATHER_TOPIC));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            try {
                ReadOnlyKeyValueStore<String, String> keyValueStore =
                        streams.store(StoreQueryParameters.fromNameAndType(WEATHER_TOPIC,
                                QueryableStoreTypes.keyValueStore()));
                logger.info("====weather KTable====");
                keyValueStore.all().forEachRemaining(entry -> logger.info(entry.key +"|"+ entry.value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 5, 1, TimeUnit.SECONDS);
    }
}
// $ bin/kafka-console-producer.sh --bootstrap-server my-kafka:9092 --topic weather --property "parse.key=true" --property "key.separator=:"
// >seoul:13
// >pusan:10
// >la:23