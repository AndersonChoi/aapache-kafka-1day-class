package com.dvwy.consumer.util;

import com.dvwy.producer.util.CustomProducerCallback;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CustomOffsetCommitCallback implements OffsetCommitCallback {
    private final static Logger logger = LoggerFactory.getLogger(CustomOffsetCommitCallback.class);

    @Override
    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
        if (e != null) {
            logger.error("Commit failed for offsets {}", offsets, e);
        } else {
            logger.info("Commit succeeded : {}", offsets);
        }
    }
}
