package net.codecamp.akka.streams.java.kata12;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.javadsl.RunnableGraph;
import org.junit.Assert;
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

public class Kata12CSV2ListOfListsGraphTest {

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
    public void expectCSV2ListOfListsGraph() throws InterruptedException, ExecutionException, TimeoutException {
        final ActorSystem system = ActorSystem.create("Kata10BroadcastAndZipTest");
        final Materializer materializer = ActorMaterializer.create(system);

        Graph<ClosedShape, CompletionStage<List<List<String>>>> g
                = Kata12CSV2ListOfListsGraph.createCSV2ListOfListsGraph();
        assertNotNull("Graph must not be null.", g);

        CompletionStage<List<List<String>>> completionStage = RunnableGraph.fromGraph(g).run(materializer);

        CompletableFuture<List<List<String>>> completableFuture = completionStage.toCompletableFuture();
        List<List<String>> list = completableFuture.get(1, TimeUnit.SECONDS);
        assertTrue("Done.", completableFuture.isDone());
        assertFalse("Completed exceptionally.", completableFuture.isCompletedExceptionally());
        assertFalse("Cancelled.", completableFuture.isCancelled());

        assertNotNull(list);
        assertEquals(4, list.size());

        list.forEach(Assert::assertNotNull);
        list.forEach(l -> assertEquals(5, l.size()));

        List<String> uuids = Arrays.asList("548e841a-6d98-4591-b4f9-3cc6ec04776d",
                "d7256984-06bf-4bec-983c-428239dd0cce", "1d651905-0d0d-4c0c-ad93-49f893b209ca",
                "af544d18-b3fe-4ea6-a6bb-fcc3677b090c", "02afe543-721e-4d73-b62c-0d64fda4c9d5");

        List<String> timestamps = Arrays.asList("2017-01-05T10:00:00Z",
                "2017-01-05T10:15:00Z", "2017-01-05T10:30:00Z",
                "2017-01-05T10:45:00Z", "2017-01-05T11:00:00Z");

        List<String> degrees = Arrays.asList("20.0", "20.3", "20.5", "21.1", "20.7");

        List<String> units = Arrays.asList("C", "C", "C", "C", "C");

        assertTrue(list.get(0).equals(uuids));
        assertTrue(list.get(1).equals(timestamps));
        assertTrue(list.get(2).equals(degrees));
        assertTrue(list.get(3).equals(units));
    }

}
