package net.codecamp.akka.streams.java.kata05;

import akka.stream.javadsl.Sink;

import java.util.concurrent.CompletionStage;

class Kata05FoldingSink {

    /**
     * Task: Create a akka.stream.javadsl.Sink that consumes integers,
     * folds over them using addition and returns the aggregated result,
     * i.e. the aggregated sum of all the consumed integers.
     * <p/>
     * Tip: Use the appropriate factory method to be found in akka.stream.javadsl.Sink to create the sink.
     * <p/>
     * Reference: http://doc.akka.io/docs/akka/current/java/stream/stream-flows-and-basics.html
     * <p/>
     * Check: The kata is solved when the corresponding unit test is green.
     *
     * @return The source.
     */
    static Sink<Integer, CompletionStage<Integer>> createFoldingSinkWithAddition() {
        return null;
    }

    /**
     * Task: Create a akka.stream.javadsl.Sink that consumes integers,
     * folds over them using multiplication and returns the aggregated result,
     * i.e. the aggregated product of all the consumed integers.
     * <p/>
     * Tip: Use the appropriate factory method to be found in akka.stream.javadsl.Sink to create the sink.
     * <p/>
     * Reference: http://doc.akka.io/docs/akka/current/java/stream/stream-flows-and-basics.html
     * <p/>
     * Check: The kata is solved when the corresponding unit test is green.
     *
     * @return The sink.
     */
    static Sink<Integer, CompletionStage<Integer>> createFoldingSinkWithMultiplication() {
        return null;
    }

}
