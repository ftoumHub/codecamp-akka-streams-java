package net.codecamp.akka.streams.java.kata10;

import akka.NotUsed;
import akka.stream.ClosedShape;
import akka.stream.Graph;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.List;
import java.util.concurrent.CompletionStage;

class Kata10BroadcastAndZip {

    /**
     * Task: Create an akka.stream.Graph that has no unconnected inlets or outlets
     * anywhere and that looks like the following:
     * <ul>
     * <li>It starts with source {@link #createSource()}</li>.
     * <li>It forks via a broadcasting uniform fan out into three branches.</li>.
     * <li>Branch 0 goes via {@link #createFlow0_0()}
     * and then {@link #createFlow0_1()}.</li>.
     * <li>Branch 1 just goes via {@link #createFlow1_0()}.</li>.
     * <li>Branch 2 goes via {@link #createFlow2_0()}
     * and {@link #createFlow2_1()}
     * and then {@link #createFlow2_2()}.</li>.
     * <li>It joins the three branches again via a zipping fan in
     * that simply uses addition to combine each three integers by calculating their sum.</li>.
     * <li>The outlet of the fan in is connected to the sink {@link #createSink()}.</li>.
     * </ul>
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
     * @return The graph.
     */
    static Graph<ClosedShape, CompletionStage<List<Integer>>> createGraphWithFanInAndFanOut() {
        return null;
    }

    private static Source<Integer, NotUsed> createSource() {
        return Source.range(1, 10);
    }

    private static Flow<Integer, Integer, NotUsed> createFlow0_0() {
        return Flow.fromFunction(i -> i - 1);
    }

    private static Flow<Integer, Integer, NotUsed> createFlow0_1() {
        return Flow.fromFunction(i -> i - 2);
    }

    private static Flow<Integer, Integer, NotUsed> createFlow1_0() {
        return Flow.fromFunction(i -> i * 2);
    }

    private static Flow<Integer, Integer, NotUsed> createFlow2_0() {
        return Flow.fromFunction(i -> i + 1);
    }

    private static Flow<Integer, Integer, NotUsed> createFlow2_1() {
        return Flow.fromFunction(i -> i + 2);
    }

    private static Flow<Integer, Integer, NotUsed> createFlow2_2() {
        return Flow.fromFunction(i -> i + 3);
    }

    private static Sink<Integer, CompletionStage<List<Integer>>> createSink() {
        return Sink.seq();
    }

}
