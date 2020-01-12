package net.codecamp.akka.streams.java.kata01;

import akka.NotUsed;
import akka.stream.javadsl.Source;

class Kata01NumberSource {

    /**
     * Task: Create a akka.stream.javadsl.Source that emits the first ten natural numbers before completing.
     * <p/>
     * Tip: Use the appropriate factory method to be found in akka.stream.javadsl.Source to create the source.
     * <p/>
     * Reference: http://doc.akka.io/docs/akka/current/java/stream/stream-flows-and-basics.html
     * <p/>
     * Check: The kata is solved when the corresponding unit test is green.
     *
     * @return The source.
     */
    static Source<Integer, NotUsed> createSourceOfNaturalNumbers1to10() {
        return Source.range(1, 10);
    }

}
