package net.codecamp.akka.streams.java.kata08;

import akka.NotUsed;
import akka.stream.javadsl.Flow;

class Kata08Flow {

    /**
     * Task: Create a simple akka.stream.javadsl.Flow that transforms each string into an integer
     * which is the length of the string.
     * <p/>
     * Tip: An akka.stream.javadsl.Flow is a specialization of akka.stream.FlowShape
     * and always has exactly one input and one output.
     * <p/>
     * Tip: Look at the API of akka.stream.javadsl.Flow and consider to use an available factory method.
     * <p/>
     * Reference: http://doc.akka.io/docs/akka/current/java/stream/stream-composition.html
     * <p/>
     * Check: The kata is solved when the corresponding unit test is green.
     *
     * @return The flow.
     */
    static Flow<String, Integer, NotUsed> createStringLengthFlow() {
        return null;
    }

}
