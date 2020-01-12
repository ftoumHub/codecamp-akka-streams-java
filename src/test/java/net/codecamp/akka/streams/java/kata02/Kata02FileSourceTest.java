package net.codecamp.akka.streams.java.kata02;

import akka.Done;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
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

public class Kata02FileSourceTest {

    @Test
    public void expectSourceOfLinesInFileKata02Txt() throws InterruptedException, ExecutionException, TimeoutException {
        final ActorSystem system = ActorSystem.create("Kata02FileSourceTest");
        final Materializer materializer = ActorMaterializer.create(system);

        Source<String, CompletionStage<IOResult>> source = Kata02FileSource.createSourceOfLinesInFileKata02Txt();
        assertNotNull("Source must not be null.", source);

        List<String> list = new ArrayList<>();
        Sink<String, CompletionStage<Done>> sink = Sink.foreach(list::add);
        RunnableGraph<Pair<CompletionStage<IOResult>, CompletionStage<Done>>> g = source.toMat(sink, Keep.both());

        Pair<CompletionStage<IOResult>, CompletionStage<Done>> p = g.run(materializer);

        CompletableFuture<IOResult> completableFuture1 = p.first().toCompletableFuture();
        IOResult ioResult = completableFuture1.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", completableFuture1.isDone());
        assertFalse("Completed exceptionally.", completableFuture1.isCompletedExceptionally());
        assertFalse("Canceled.", completableFuture1.isCancelled());
        assertTrue(ioResult.wasSuccessful());

        CompletableFuture<Done> completableFuture2 = p.second().toCompletableFuture();
        completableFuture2.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", completableFuture2.isDone());
        assertFalse("Completed exceptionally.", completableFuture2.isCompletedExceptionally());
        assertFalse("Canceled.", completableFuture2.isCancelled());
        assertEquals(10, list.size());
        assertEquals(Arrays.asList("line one", "this line is the second line", "the third line",
                "line four", "line five", "line six", "line seven", "line eight", "line nine", "last line"), list);
    }

}
