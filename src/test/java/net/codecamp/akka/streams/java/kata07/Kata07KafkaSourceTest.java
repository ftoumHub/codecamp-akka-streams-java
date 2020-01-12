package net.codecamp.akka.streams.java.kata07;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.kafka.ConsumerMessage;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import net.codecamp.akka.streams.java.model.FunResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.vavr.API.println;
import static net.codecamp.akka.streams.java.constants.Constants.KAFKA_CLIENT_PORT;
import static net.codecamp.akka.streams.java.constants.Constants.KAFKA_IP_OR_HOST;
import static net.codecamp.akka.streams.java.kata07.Kata07KafkaSource.system;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Kata07KafkaSourceTest {

    private static final String KATA07_RECORD = "kata07-record";
    private static final String KATA07_TOPIC = "kata07-topic";
    private static final int KATA07_PARTITION = 0;

    @Test
    public void expectKafkaSource() throws InterruptedException, ExecutionException, TimeoutException {
        final Materializer materializer = ActorMaterializer.create(Kata07KafkaSource.system);

        final Source<Pair<ConsumerMessage.CommittableMessage<String, String>, FunResult>, Consumer.Control> source
                = Kata07KafkaSource.createKafkaSource(s -> new FunResult(s + "_fun"));
        assertNotNull("source must not be null", source);

        // produce some
        produce(materializer, 10);
        // wait for production to really complete
        Thread.sleep(2000);

        // consume all elements currently in the topic
        {
            // flow to consume all elements
            final Flow<Pair<ConsumerMessage.CommittableMessage<String, String>, FunResult>, String, NotUsed> flow1
                    = Flow.fromFunction(p -> {
                p.first().committableOffset().commitJavadsl();
                return p.second().getResult();
            });
            final List<String> resultList1 = new ArrayList<>();
            final Sink<String, CompletionStage<Done>> sink1 = Sink.foreach(resultList1::add);

            final RunnableGraph<Pair<Consumer.Control, CompletionStage<Done>>> g1 = source.viaMat(flow1, Keep.left()).toMat(sink1, Keep.both());
            consume(materializer, g1, resultList1, 0, Integer.MAX_VALUE, false);
        }

        // produce new
        final int toProduce = 10;
        produce(materializer, 10);
        // wait for production to really complete
        Thread.sleep(2000);

        // consume just the first 5 new records
        {
            // flow to consume all elements
            final Flow<Pair<ConsumerMessage.CommittableMessage<String, String>, FunResult>, String, NotUsed> flow2
                    = Flow.fromFunction(p -> {
                p.first().committableOffset().commitJavadsl();
                return p.second().getResult();
            });
            // flow to consume only 5 elements
            final Flow<Pair<ConsumerMessage.CommittableMessage<String, String>, FunResult>, String, NotUsed> flowTake5
                    = flow2.take(5);
            final List<String> resultList2 = new ArrayList<>(5);
            final Sink<String, CompletionStage<Done>> sink2 = Sink.foreach(resultList2::add);
            final RunnableGraph<Pair<Consumer.Control, CompletionStage<Done>>> g2
                    = source.viaMat(flowTake5, Keep.left())
                            .toMat(sink2, Keep.both());
            consume(materializer, g2, resultList2, 0, 4, true);
        }

        // wait shortly in between consumptions
        Thread.sleep(500);

        // consume the rest of the new records from the stored offset to the latest
        {
            // flow to consume all elements
            final Flow<Pair<ConsumerMessage.CommittableMessage<String, String>, FunResult>, String, NotUsed> flow3
                    = Flow.fromFunction(p -> {
                p.first().committableOffset().commitJavadsl();
                return p.second().getResult();
            });
            final List<String> resultList3 = new ArrayList<>();
            final Sink<String, CompletionStage<Done>> sink3 = Sink.foreach(resultList3::add);
            final RunnableGraph<Pair<Consumer.Control, CompletionStage<Done>>> g3
                    = source.viaMat(flow3, Keep.left()).toMat(sink3, Keep.both());

            consume(materializer, g3, resultList3, 5, toProduce - 1, true);
        }
    }

    private static void produce(final Materializer materializer,
                                final int toProduce) throws InterruptedException, ExecutionException, TimeoutException {
        println("==> producing "+ 10 +" records");
        final Sink<ProducerRecord<String, String>, CompletionStage<Done>> producerSink
                = Producer.plainSink(createProducerSettings(system));

        // define records to be produced
        final Source<ProducerRecord<String, String>, NotUsed> producerRecordSource
                = Source.range(0, toProduce - 1)
                        .map(i -> KATA07_RECORD + "-" + i)
                        .map(v -> new ProducerRecord<>(KATA07_TOPIC, KATA07_PARTITION, Instant.now().toEpochMilli(), v, v));

        // start producing
        CompletableFuture<Done> producerFuture = producerRecordSource.runWith(producerSink, materializer).toCompletableFuture();

        // wait for production to complete
        Thread.sleep(2000);
        producerFuture.get(1, TimeUnit.SECONDS);

        println("<== producing done");
        assertTrue("Done.", producerFuture.isDone());
        assertFalse("Completed exceptionally.", producerFuture.isCompletedExceptionally());
        assertFalse("Canceled.", producerFuture.isCancelled());
    }

    private static void consume(final Materializer materializer,
                                final RunnableGraph<Pair<Consumer.Control, CompletionStage<Done>>> g,
                                final List<String> resultList,
                                final int fromIndex,
                                final int toIndex,
                                final boolean doAssert)
            throws InterruptedException, TimeoutException, ExecutionException {

        if (toIndex <= fromIndex) {
            throw new IllegalStateException("toIndex must be greater than fromIndex");
        }

        // start consumer
        Pair<Consumer.Control, CompletionStage<Done>> pair = g.run(materializer);

        // wait some time and shutdown the consumer
        Consumer.Control control = pair.first();
        Thread.sleep(3000);

        // check the consumer
        CompletionStage<Done> isShutdownCompletionStage = control.isShutdown();
        CompletableFuture<Done> isShutdownFuture = isShutdownCompletionStage.toCompletableFuture();
        boolean done = isShutdownFuture.isDone(); // this is already done when having taken 5
        assertFalse("Completed exceptionally.", isShutdownFuture.isCompletedExceptionally());
        assertFalse("Canceled.", isShutdownFuture.isCancelled());

        // if needed shutdown the consumer
        if (!done) {
            CompletionStage<Done> shutdownCompletionStage = control.shutdown();
            CompletableFuture<Done> shutdownFuture = shutdownCompletionStage.toCompletableFuture();
            shutdownFuture.get(5, TimeUnit.SECONDS);
            assertTrue("Done.", shutdownFuture.isDone());
            assertFalse("Completed exceptionally.", shutdownFuture.isCompletedExceptionally());
            assertFalse("Canceled.", shutdownFuture.isCancelled());
        }

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

        System.out.println("***********" + resultList);

        int index = fromIndex;
        if (doAssert) {
            assertEquals(1 + toIndex - fromIndex, resultList.size());

            for (String s : resultList) {
                String expectedEnd = "-" + index + "_fun";
                assertNotNull(s);
                assertTrue(s + " does not start with " + KATA07_RECORD, s.startsWith(KATA07_RECORD));
                assertTrue(s + "does not end with " + expectedEnd, s.endsWith(expectedEnd));
                index++;
            }
        }
    }

    private static ProducerSettings<String, String> createProducerSettings(final ActorSystem system) {
        return ProducerSettings.create(system, new StringSerializer(), new StringSerializer())
                .withBootstrapServers(KAFKA_IP_OR_HOST + ":" + KAFKA_CLIENT_PORT)
                .withParallelism(1)
                .withProperty("max.in.flight.requests.per.connection", "1")
                .withProperty("batch.size", "0");
    }

    private static ConsumerSettings<String, String> createConsumerSettings(final ActorSystem system,
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

}
