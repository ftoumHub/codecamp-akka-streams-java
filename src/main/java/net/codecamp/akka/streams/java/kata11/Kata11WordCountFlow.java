package net.codecamp.akka.streams.java.kata11;

import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.javadsl.Flow;

class Kata11WordCountFlow {

    /**
     * Task: Create a akka.stream.javadsl.Flow for word count, i.e. with input type String
     * and output type Pair<String, Integer> that counts the number of occurrences of each String.
     * <p/>
     * Reference: http://doc.akka.io/docs/akka/current/java/stream/stream-cookbook.html
     * <p/>
     * Check: The kata is solved when the corresponding unit test is green.
     *
     * @return The flow.
     */
    static Flow<String, Pair<String, Integer>, NotUsed> createWordCountFlow() {
        return null;
    }

}
