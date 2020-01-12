package net.codecamp.akka.streams.java.kata12;

import akka.stream.ClosedShape;
import akka.stream.Graph;

import java.util.List;
import java.util.concurrent.CompletionStage;

class Kata12CSV2ListOfListsGraph {

    /**
     * Task: Create an akka.stream.Graph that has no unconnected inlets or outlets
     * and that processes the file kata12.csv in such a way that it produces
     * a List<List<String>> where each inner list contains the values of each
     * column in the CSV. The order of the columns in the CSV must be preserved
     * in the outer list. The structure of the graph is up to you.
     * <p/>
     * Tip: To create the necessary source just do something like in kata03.
     * <p/>
     * Reference: http://doc.akka.io/docs/akka/current/java/stream/stream-graphs.html
     * <p/>
     * Check: The kata is solved when the corresponding unit test is green.
     *
     * @return The flow.
     */
    static Graph<ClosedShape, CompletionStage<List<List<String>>>> createCSV2ListOfListsGraph() {
        return null;
    }

}
