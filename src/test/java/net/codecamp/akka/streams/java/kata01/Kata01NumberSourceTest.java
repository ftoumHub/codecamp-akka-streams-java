package net.codecamp.akka.streams.java.kata01;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
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

public class Kata01NumberSourceTest {

    @Test
    public void expectSourceOfNaturalNumbers1to10() throws InterruptedException, ExecutionException, TimeoutException {
        final ActorSystem system = ActorSystem.create("Kata01NumberSourceTest");
        final Materializer materializer = ActorMaterializer.create(system);

        Source<Integer, NotUsed> source = Kata01NumberSource.createSourceOfNaturalNumbers1to10();
        assertNotNull("Source must not be null.", source);

        List<Integer> list = new ArrayList<>();
        Sink<Integer, CompletionStage<Done>> sink = Sink.foreach(list::add);

        CompletionStage<Done> completionStage = source.runWith(sink, materializer);
        CompletableFuture<Done> completableFuture = completionStage.toCompletableFuture();
        completableFuture.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", completableFuture.isDone());
        assertFalse("Completed exceptionally.", completableFuture.isCompletedExceptionally());
        assertFalse("Canceled.", completableFuture.isCancelled());
        assertEquals(10, list.size());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), list);
    }

}
