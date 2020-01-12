package net.codecamp.akka.streams.java.kata06;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static net.codecamp.akka.streams.java.constants.Constants.KAFKA_CLIENT_PORT;
import static net.codecamp.akka.streams.java.constants.Constants.KAFKA_IP_OR_HOST;
import static net.codecamp.akka.streams.java.kata06.Kata06KafkaSink.system;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Kata06KafkaSinkTest {

    private static final String KATA06_RECORD = "kata06-record";
    private static final String KATA06_TOPIC = "kata06-topic";
    private static final int KATA06_PARTITION = 0;

    TopicPartition createTopicPartitionKata06() {
        return new TopicPartition(KATA06_TOPIC, KATA06_PARTITION);
    }

    @Test
    public void expectKafkaSink() throws InterruptedException, ExecutionException, TimeoutException {
        final Materializer materializer = ActorMaterializer.create(system);

        // determine current and next offset by looking into what is currently in the topic
        final int currentOffset = determineLatestOffsetByReadingAllRecords(system, materializer);
        final int nextOffset = currentOffset + 1;

        // obtain sink under test
        final Sink<ProducerRecord<String, String>, CompletionStage<Done>> producerSink
                = Kata06KafkaSink.createKafkaSink();
        assertNotNull("sink must not be null", producerSink);

        // define records to be produced
        final Source<ProducerRecord<String, String>, NotUsed> producerRecordSource = Source.range(nextOffset, nextOffset + 9)
                .map(i -> KATA06_RECORD + "-" + i)
                .map(v -> new ProducerRecord<>(KATA06_TOPIC, KATA06_PARTITION, Instant.now().toEpochMilli(), v, v));

        // start producing
        CompletionStage<Done> producerCompletitionStage = producerRecordSource.runWith(producerSink, materializer);

        // wait a bit
        Thread.sleep(200);

        // define the consumer
        Source<ConsumerRecord<String, String>, Consumer.Control> consumerSource
                = Consumer.plainSource(createConsumerSettings(system, "kata06-assert-consumergroup", "none", true),
                Subscriptions.assignmentWithOffset(createTopicPartitionKata06(), nextOffset));
        List<ConsumerRecord<String, String>> list = new ArrayList<>();
        Sink<ConsumerRecord<String, String>, CompletionStage<Done>> consumerSink
                = Sink.foreach(list::add);
        RunnableGraph<Pair<Consumer.Control, CompletionStage<Done>>> g = consumerSource.toMat(consumerSink, Keep.both());

        // start consuming
        Pair<Consumer.Control, CompletionStage<Done>> pair = g.run(materializer);

        // wait for production to complete
        CompletableFuture<Done> producerFuture = producerCompletitionStage.toCompletableFuture();
        producerFuture.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", producerFuture.isDone());
        assertFalse("Completed exceptionally.", producerFuture.isCompletedExceptionally());
        assertFalse("Canceled.", producerFuture.isCancelled());

        // check that consumer is still running
        Consumer.Control control = pair.first();
        CompletionStage<Done> controlIsShutDownCompletionStage = control.isShutdown();
        CompletableFuture<Done> controlIsShutDownFuture = controlIsShutDownCompletionStage.toCompletableFuture();
        assertFalse("Done.", controlIsShutDownFuture.isDone());
        assertFalse("Completed exceptionally.", controlIsShutDownFuture.isCompletedExceptionally());
        assertFalse("Canceled.", controlIsShutDownFuture.isCancelled());

        // wait a bit
        Thread.sleep(200);

        // shutdown consumer
        CompletionStage<Done> controlShutDownCompletionStage = control.shutdown();
        CompletableFuture<Done> controlShutDownFuture = controlShutDownCompletionStage.toCompletableFuture();
        controlShutDownFuture.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", controlShutDownFuture.isDone());
        assertFalse("Completed exceptionally.", controlShutDownFuture.isCompletedExceptionally());
        assertFalse("Canceled.", controlShutDownFuture.isCancelled());

        // assert that consumer finishes
        CompletionStage<Done> consumerSinkCompletionStage = pair.second();
        CompletableFuture<Done> consumerSinkFuture = consumerSinkCompletionStage.toCompletableFuture();
        consumerSinkFuture.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", consumerSinkFuture.isDone());
        assertFalse("Completed exceptionally.", consumerSinkFuture.isCompletedExceptionally());
        assertFalse("Canceled.", consumerSinkFuture.isCancelled());

        // assert consumed result
        assertEquals(createExpectedList(nextOffset, nextOffset + 9), extractValues(list));
    }

    private ConsumerSettings<String, String> createConsumerSettings(final ActorSystem system,
                                                                    final String consumerGroupId,
                                                                    final String autoOffsetResetConfig,
                                                                    final boolean autoCommit) {
        ConsumerSettings<String, String> settings
                = ConsumerSettings.create(system, new StringDeserializer(), new StringDeserializer())
                .withBootstrapServers(KAFKA_IP_OR_HOST + ":" + KAFKA_CLIENT_PORT)
                .withGroupId(consumerGroupId)
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetConfig)
                .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(autoCommit));

        if (autoCommit) {
            settings.withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5");
        }
        return settings;
    }

    private List<String> createExpectedList(final Integer from, final Integer to) {
        if (null == from || null == to || from > to) {
            throw new IllegalArgumentException("illegal range");
        }
        List<String> list = new ArrayList<>(to - from + 1);
        for (int i = from; i <= to; i++) {
            list.add(KATA06_RECORD + "-" + i);
        }
        return list;
    }

    private List<String> extractValues(final List<ConsumerRecord<String, String>> list) {
        return list.stream().map(ConsumerRecord::value).collect(Collectors.toList());
    }

    private Integer determineLatestOffsetByReadingAllRecords(final ActorSystem system,
                                                             final Materializer materializer)
            throws InterruptedException, ExecutionException, TimeoutException {
        // define consumer
        Source<ConsumerRecord<String, String>, Consumer.Control> consumerSource
                = Consumer.plainSource(createConsumerSettings(system, "kata06-getoffset-consumergroup", "earliest", false),
                Subscriptions.assignment(createTopicPartitionKata06()));
        List<ConsumerRecord<String, String>> list = new ArrayList<>();
        Sink<ConsumerRecord<String, String>, CompletionStage<Done>> consumerSink
                = Sink.foreach(list::add);
        RunnableGraph<Pair<Consumer.Control, CompletionStage<Done>>> g = consumerSource.toMat(consumerSink, Keep.both());

        // start consumer
        Pair<Consumer.Control, CompletionStage<Done>> pair = g.run(materializer);

        // wait some time and shutdown the consumer
        Consumer.Control control = pair.first();
        Thread.sleep(2000);

        // assert that consumer is still running
        CompletionStage<Done> isShutdownCompletionStage = control.isShutdown();
        CompletableFuture<Done> isShutdownFuture = isShutdownCompletionStage.toCompletableFuture();
        assertFalse("Done.", isShutdownFuture.isDone());
        assertFalse("Completed exceptionally.", isShutdownFuture.isCompletedExceptionally());
        assertFalse("Canceled.", isShutdownFuture.isCancelled());

        // shutdown the consumer
        CompletionStage<Done> shutdownCompletionStage = control.shutdown();
        CompletableFuture<Done> shutdownFuture = shutdownCompletionStage.toCompletableFuture();
        shutdownFuture.get(2, TimeUnit.SECONDS);
        assertTrue("Done.", shutdownFuture.isDone());
        assertFalse("Completed exceptionally.", shutdownFuture.isCompletedExceptionally());
        assertFalse("Canceled.", shutdownFuture.isCancelled());

        // assert that consumer is finished
        CompletionStage<Done> completionStage = pair.second();
        CompletableFuture<Done> completableFuture = completionStage.toCompletableFuture();
        completableFuture.get(2, TimeUnit.SECONDS);
        assertTrue("Done.", completableFuture.isDone());
        assertFalse("Completed exceptionally.", completableFuture.isCompletedExceptionally());
        assertFalse("Canceled.", completableFuture.isCancelled());

        // assert that consumer has shutdown
        CompletionStage<Done> isShutdownCompletionStage2 = control.isShutdown();
        CompletableFuture<Done> isShutdownFuture2 = isShutdownCompletionStage2.toCompletableFuture();
        assertTrue("Done.", isShutdownFuture2.isDone());
        assertFalse("Completed exceptionally.", isShutdownFuture2.isCompletedExceptionally());
        assertFalse("Canceled.", isShutdownFuture2.isCancelled());

        System.out.println(list);

        return list.size() - 1;
    }

}
