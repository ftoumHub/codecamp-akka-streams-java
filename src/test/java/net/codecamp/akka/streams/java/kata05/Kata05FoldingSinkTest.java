package net.codecamp.akka.streams.java.kata05;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Kata05FoldingSinkTest {

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
    public void expectFoldingSinks()
            throws InterruptedException, ExecutionException, TimeoutException {
        final ActorSystem system = ActorSystem.create("Kata05FoldingSinkTest");
        final Materializer materializer = ActorMaterializer.create(system);

        final Sink<Integer, CompletionStage<Integer>> sinkA = Kata05FoldingSink.createFoldingSinkWithAddition();
        assertNotNull("sink must not be null", sinkA);

        assertResult(1, materializer, Source.range(1, 1), sinkA);
        assertResult(2, materializer, Source.range(2, 2), sinkA);
        assertResult(3, materializer, Source.range(3, 3), sinkA);

        assertResult(3, materializer, Source.range(1, 2), sinkA);
        assertResult(6, materializer, Source.range(1, 3), sinkA);
        assertResult(10, materializer, Source.range(1, 4), sinkA);
        assertResult(15, materializer, Source.range(1, 5), sinkA);
        assertResult(21, materializer, Source.range(1, 6), sinkA);
        assertResult(28, materializer, Source.range(1, 7), sinkA);

        assertResult(7, materializer, Source.range(3, 4), sinkA);
        assertResult(12, materializer, Source.range(3, 5), sinkA);
        assertResult(18, materializer, Source.range(3, 6), sinkA);

        final Sink<Integer, CompletionStage<Integer>> sinkM = Kata05FoldingSink.createFoldingSinkWithMultiplication();
        assertNotNull("sink must not be null", sinkM);

        assertResult(1, materializer, Source.range(1, 1), sinkM);
        assertResult(2, materializer, Source.range(2, 2), sinkM);
        assertResult(3, materializer, Source.range(3, 3), sinkM);

        assertResult(2, materializer, Source.range(1, 2), sinkM);
        assertResult(6, materializer, Source.range(1, 3), sinkM);
        assertResult(24, materializer, Source.range(1, 4), sinkM);
        assertResult(120, materializer, Source.range(1, 5), sinkM);
        assertResult(720, materializer, Source.range(1, 6), sinkM);
        assertResult(5040, materializer, Source.range(1, 7), sinkM);

        assertResult(12, materializer, Source.range(3, 4), sinkM);
        assertResult(60, materializer, Source.range(3, 5), sinkM);
        assertResult(360, materializer, Source.range(3, 6), sinkM);
    }

    private void assertResult(final Integer expected,
                              final Materializer materializer,
                              final Source<Integer, NotUsed> source,
                              final Sink<Integer, CompletionStage<Integer>> sink)
            throws InterruptedException, ExecutionException, TimeoutException {

        CompletionStage<Integer> completionStage = source.runWith(sink, materializer);

        CompletableFuture<Integer> completableFuture = completionStage.toCompletableFuture();
        Integer result = completableFuture.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", completableFuture.isDone());
        assertFalse("Completed exceptionally.", completableFuture.isCompletedExceptionally());
        assertFalse("Canceled.", completableFuture.isCancelled());
        assertEquals(expected, result);
    }

}
