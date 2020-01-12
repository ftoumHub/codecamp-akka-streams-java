package net.codecamp.akka.streams.java.kata11;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.FramingTruncation;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static net.codecamp.akka.streams.java.kata11.Kata11WordCountFlow.createWordCountFlow;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Kata11WordCountFlowTest {

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
    public void expectWordCountFlow() throws InterruptedException, TimeoutException, ExecutionException {
        final ActorSystem system = ActorSystem.create("Kata11WordCountFlowTest");
        final Materializer materializer = ActorMaterializer.create(system);

        Source<String, CompletionStage<IOResult>> wordSource = createFile2WordsSource("src/main/resources/kata11.txt");

        Flow<String, Pair<String, Integer>, NotUsed> flow = createWordCountFlow();
        assertNotNull("Flow must not be null.", flow);

        Sink<Pair<String, Integer>, CompletionStage<List<Pair<String, Integer>>>> sink = Sink.seq();

        RunnableGraph<Pair<CompletionStage<IOResult>, CompletionStage<List<Pair<String, Integer>>>>> g
                = wordSource.viaMat(flow, Keep.left()).toMat(sink, Keep.both());

        Pair<CompletionStage<IOResult>, CompletionStage<List<Pair<String, Integer>>>> p = g.run(materializer);

        CompletableFuture<IOResult> completableFuture1 = p.first().toCompletableFuture();
        IOResult ioResult = completableFuture1.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", completableFuture1.isDone());
        assertFalse("Completed exceptionally.", completableFuture1.isCompletedExceptionally());
        assertFalse("Canceled.", completableFuture1.isCancelled());
        assertTrue(ioResult.wasSuccessful());

        CompletableFuture<List<Pair<String, Integer>>> completableFuture2 = p.second().toCompletableFuture();
        List<Pair<String, Integer>> list = completableFuture2.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", completableFuture2.isDone());
        assertFalse("Completed exceptionally.", completableFuture2.isCompletedExceptionally());
        assertFalse("Canceled.", completableFuture2.isCancelled());

        Pair<String, Integer> streams = new Pair<>("streams", 1);
        Pair<String, Integer> it = new Pair<>("it", 1);
        Pair<String, Integer> they = new Pair<>("they", 3);
        Pair<String, Integer> the = new Pair<>("the", 8);

        assertTrue(list.contains(streams));
        assertTrue(list.contains(it));
        assertTrue(list.contains(they));
        assertTrue(list.contains(the));
    }

    private Source<String, CompletionStage<IOResult>> createFile2WordsSource(final String filePath) {

        final Source<String, CompletionStage<IOResult>> wordSource;

        {
            final Source<ByteString, CompletionStage<IOResult>> fileSource
                    = FileIO.fromFile(new File(filePath))
                    .log("kata11-fileSource");

            final Flow<ByteString, String, NotUsed> lineFramingFlow
                    = Framing.delimiter(ByteString.fromString(System.lineSeparator()), 1000, FramingTruncation.ALLOW)
                    .map(ByteString::utf8String)
                    .log("kata11-lineFramingFlow");

            final Flow<String, String, NotUsed> wordFramingFlow
                    = Flow.<String, String[]>fromFunction(s -> s.split(" "))
                    .flatMapConcat(a -> Source.fromIterator(() -> Arrays.asList(a).iterator()))
                    .log("kata11-wordFramingFlow");

            wordSource = fileSource
                    .viaMat(lineFramingFlow, Keep.left())
                    .viaMat(wordFramingFlow, Keep.left())
                    .log("kata11-lineSource");
        }

        return wordSource
                .map(s -> s.replace(";", ""))
                .map(s -> s.replace(",", ""))
                .map(s -> s.replace(".", ""));
    }

}
