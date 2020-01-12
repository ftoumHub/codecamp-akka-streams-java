package net.codecamp.akka.streams.java.kata04;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import net.codecamp.akka.streams.java.model.TemperatureReading;
import net.codecamp.akka.streams.java.model.TemperatureUnit;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
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

public class Kata04DirtyCsvSourceTest {

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
    public void expectRobustFlowOfStringToTemperatureReading() throws InterruptedException, ExecutionException, TimeoutException {
        final ActorSystem system = ActorSystem.create("Kata04DirtyCsvSourceTest");
        final Materializer materializer = ActorMaterializer.create(system);

        Source<String, CompletionStage<IOResult>> source = Kata04DirtyCsvSource.createSourceOfLinesInFileKata04Csv();
        assertNotNull("Source must not be null.", source);

        Flow<String, TemperatureReading, NotUsed> flow = Kata04DirtyCsvSource.createRobustFlowOfStringToTemperatureReading();
        assertNotNull("Flow must not be null.", flow);

        List<TemperatureReading> list = new ArrayList<>();
        Sink<TemperatureReading, CompletionStage<Done>> sink = Sink.foreach(list::add);
        RunnableGraph<Pair<CompletionStage<IOResult>, CompletionStage<Done>>> g
                = source.viaMat(flow, Keep.left()).toMat(sink, Keep.both());

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
        assertEquals(5, list.size());
        assertEquals(createExpectedList(), list);
    }

    private List<TemperatureReading> createExpectedList() {
        final List<TemperatureReading> expectedList = new ArrayList<>();
        expectedList.add(new TemperatureReading("548e841a-6d98-4591-b4f9-3cc6ec04776d",
                Instant.parse("2017-01-05T09:00:00Z"), 20.0F, TemperatureUnit.DEGREES_CELSIUS));
        expectedList.add(new TemperatureReading("d7256984-06bf-4bec-983c-428239dd0cce",
                Instant.parse("2017-01-05T09:30:00Z"), 20.3F, TemperatureUnit.DEGREES_CELSIUS));
        expectedList.add(new TemperatureReading("1d651905-0d0d-4c0c-ad93-49f893b209ca",
                Instant.parse("2017-01-05T10:00:00Z"), 20.5F, TemperatureUnit.DEGREES_CELSIUS));
        expectedList.add(new TemperatureReading("af544d18-b3fe-4ea6-a6bb-fcc3677b090c",
                Instant.parse("2017-01-05T10:30:00Z"), 21.1F, TemperatureUnit.DEGREES_CELSIUS));
        expectedList.add(new TemperatureReading("02afe543-721e-4d73-b62c-0d64fda4c9d5",
                Instant.parse("2017-01-05T11:00:00Z"), 20.7F, TemperatureUnit.DEGREES_CELSIUS));
        return expectedList;
    }

}
