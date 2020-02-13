package net.codecamp.akka.streams.java.kata06;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.concurrent.CompletionStage;

import static net.codecamp.akka.streams.java.constants.Constants.KAFKA_CLIENT_PORT;
import static net.codecamp.akka.streams.java.constants.Constants.KAFKA_IP_OR_HOST;

class Kata06KafkaSink {

    final static ActorSystem system = ActorSystem.create("Kata06KafkaSink");

    /**
     * Task: Create a akka.stream.javadsl.Sink that writes each element
     * to the Kafka topic named kata06-topic to the partition 0.
     * <p/>
     * Tip: See the README.md for additional instructions regarding Kafka.
     * <p/>
     * Tip: To start Kafka execute in the project root directory: docker-compose up -d
     * <p/>
     * Tip: For the Kafka client port see in the project root directory: docker-compose-old.yml
     * <p/>
     * Tip: Use the library Akka Streams Kafka. The dependency is already present in this project.
     * <p/>
     * Tip: Use {@link net.codecamp.akka.streams.java.constants.Constants#KAFKA_IP_OR_HOST}
     * and {@link net.codecamp.akka.streams.java.constants.Constants#KAFKA_CLIENT_PORT}
     * where KAFKA_IP_OR_HOST must be the current IP of your machine if you use Docker for Mac
     * and localhost if you use Linux or Windows.
     * <p/>
     * Tip: To provide producer settings see: http://doc.akka.io/docs/akka-stream-kafka/0.13/producer.html#settings
     * <p/>
     * Reference: http://doc.akka.io/docs/akka-stream-kafka/0.13/producer.html#producer-as-a-sink
     * <p/>
     * Check: The kata is solved when the corresponding unit test is green.
     *
     * @return The sink.
     */
    static Sink<ProducerRecord<String, String>, CompletionStage<Done>> createKafkaSink() {

        final ProducerSettings<String, String> producerSettings = ProducerSettings
                .create(system, new StringSerializer(), new StringSerializer())
                .withBootstrapServers(KAFKA_IP_OR_HOST + ":" + KAFKA_CLIENT_PORT);

        return Producer.plainSink(producerSettings);

        //return Sink.foreach(v -> new ProducerRecord<String, String>("kata06-topic", "Record:"+v));
    }

}
