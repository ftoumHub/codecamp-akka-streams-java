package net.codecamp.akka.streams.java.kata10;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.javadsl.RunnableGraph;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Kata10BroadcastAndZipTest {

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
    public void expectResultOfGraphWithFanOutAndFanIn() throws InterruptedException, ExecutionException, TimeoutException {
        final ActorSystem system = ActorSystem.create("Kata10BroadcastAndZipTest");
        final Materializer materializer = ActorMaterializer.create(system);

        Graph<ClosedShape, CompletionStage<List<Integer>>> g = Kata10BroadcastAndZip.createGraphWithFanInAndFanOut();
        assertNotNull("Graph must not be null.", g);

        CompletionStage<List<Integer>> completionStage = RunnableGraph.fromGraph(g).run(materializer);

        CompletableFuture<List<Integer>> completableFuture = completionStage.toCompletableFuture();
        List<Integer> list = completableFuture.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", completableFuture.isDone());
        assertFalse("Completed exceptionally.", completableFuture.isCompletedExceptionally());
        assertFalse("Cancelled.", completableFuture.isCancelled());

        assertEquals(Arrays.asList(7, 11, 15, 19, 23, 27, 31, 35, 39, 43), list);

        System.out.println(list);
    }

}
