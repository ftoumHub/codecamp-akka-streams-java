package net.codecamp.akka.streams.java.kata08;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.junit.Test;

import java.util.ArrayList;
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

public class Kata08FlowTest {

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
    public void expectStringLengthFlow() throws InterruptedException, ExecutionException, TimeoutException {
        final ActorSystem system = ActorSystem.create("Kata08FlowTest");
        final Materializer materializer = ActorMaterializer.create(system);

        Flow<String, Integer, NotUsed> flow = Kata08Flow.createStringLengthFlow();
        assertNotNull("Flow must not be null.", flow);

        List<String> input = Arrays.asList("a", "ab", "abc", "abcd", "abcde", "t");
        Source<String, NotUsed> source = Source.<String>fromIterator(input::iterator);

        List<Integer> list = new ArrayList<>();
        Sink<Integer, CompletionStage<Done>> sink = Sink.foreach(list::add);

        RunnableGraph<CompletionStage<Done>> g
                = source.via(flow).toMat(sink, Keep.right());

        CompletionStage<Done> completionStage = g.run(materializer);
        CompletableFuture<Done> completableFuture = completionStage.toCompletableFuture();
        completableFuture.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", completableFuture.isDone());
        assertFalse("Completed exceptionally.", completableFuture.isCompletedExceptionally());
        assertFalse("Canceled.", completableFuture.isCancelled());
        assertEquals(6, list.size());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 1), list);
    }

}
