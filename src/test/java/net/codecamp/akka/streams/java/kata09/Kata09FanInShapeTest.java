package net.codecamp.akka.streams.java.kata09;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.FanInShape2;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.SourceShape;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Kata09FanInShapeTest {

    /*
    . Please do not read this test code while solving any of the katas! Spoiler warning!
    .
    . Just run it with right-click on the test class name above.
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    .
    */

    @Test
    public void expectFanInShape2() throws InterruptedException {
        final ActorSystem system = ActorSystem.create("Kata09FanInShapeTest");
        final Materializer materializer = ActorMaterializer.create(system);

        Graph<FanInShape2<String, Integer, Pair<String, Integer>>, NotUsed> fanInShape2Graph
                = Kata09FanInShape.createFanInShape2();
        assertNotNull("fanInShape2Graph must not be null.", fanInShape2Graph);


        Source<String, NotUsed> stringSource = Source.fromIterator(Arrays.asList("1", "2", "3")::iterator);
        Source<Integer, NotUsed> intSource = Source.fromIterator(Arrays.asList(1, 2, 3)::iterator);
        List<Pair<String, Integer>> list = new ArrayList<>();
        Sink<Pair<String, Integer>, CompletionStage<Done>> sink = Sink.foreach(list::add);

        final Graph<ClosedShape, CompletionStage<Done>> g = GraphDSL.create(sink, (b, s) -> {
            final SourceShape<String> hashtagSourceShape = b.add(stringSource);
            final SourceShape<Integer> numberSourceShape = b.add(intSource);
            final FanInShape2<String, Integer, Pair<String, Integer>> zipShape = b.add(fanInShape2Graph);

            b.from(hashtagSourceShape).toInlet(zipShape.in0());
            b.from(numberSourceShape).toInlet(zipShape.in1());

            b.from(zipShape.out()).to(s);

            return ClosedShape.getInstance();
        });

        CompletionStage<Done> completionStage = RunnableGraph.fromGraph(g).run(materializer);
        CompletableFuture<Done> completableFuture = completionStage.toCompletableFuture();
        Thread.sleep(100);
        assertTrue("Done.", completableFuture.isDone());
        assertFalse("Completed exceptionally.", completableFuture.isCompletedExceptionally());
        assertFalse("Cancelled.", completableFuture.isCancelled());

        Integer counter = 1;
        for (Pair<String, Integer> pair : list) {
            assertEquals(String.valueOf(counter), pair.first());
            assertEquals(counter, pair.second());
            counter++;
        }
    }

}
