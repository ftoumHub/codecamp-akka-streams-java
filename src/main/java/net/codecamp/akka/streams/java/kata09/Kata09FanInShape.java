package net.codecamp.akka.streams.java.kata09;

import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.FanInShape2;
import akka.stream.Graph;

class Kata09FanInShape {

    /**
     * Task: Create a fan in shape as a raw unconnected graph
     * that can combine a stream of strings
     * and a stream of integers into a stream of pairs.
     * <p/>
     * Tip: To define processing pipelines it is sufficient to use:
     * <ul>
     * <li>akka.stream.javadsl.Source</li>
     * <li>akka.stream.javadsl.Flow</li>
     * <li>akka.stream.javadsl.Sink</li>
     * </ul>
     * To define processing graphs with patterns such as forks, fan out, balancing,
     * joins or fan in it is necessary to work with the abstractions Shape or AbstractShape
     * and their sub-classes in order to create an akka.stream.Graph.
     * For more information see the reference documentation linked below.
     * <p/>
     * Reference: http://doc.akka.io/docs/akka/current/java/stream/stream-graphs.html
     * <p/>
     * Check: The kata is solved when the corresponding unit test is green.
     *
     * @return The flow.
     */
    static Graph<FanInShape2<String, Integer, Pair<String, Integer>>, NotUsed> createFanInShape2() {
        return null;
    }

}
