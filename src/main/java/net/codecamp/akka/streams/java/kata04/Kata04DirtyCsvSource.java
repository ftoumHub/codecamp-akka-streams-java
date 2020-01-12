package net.codecamp.akka.streams.java.kata04;

import akka.NotUsed;
import akka.stream.IOResult;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import net.codecamp.akka.streams.java.model.TemperatureReading;

import java.util.concurrent.CompletionStage;

class Kata04DirtyCsvSource {

    /**
     * Task: Create a akka.stream.javadsl.Source that emits line by line of kata04.csv file.
     * <p/>
     * Reference: See kata03.
     *
     * @return The source.
     */
    static Source<String, CompletionStage<IOResult>> createSourceOfLinesInFileKata04Csv() {
        return null;
    }

    /**
     * Task: This is just like kata 03, but this time the CSV contains lines with data problems.
     * The flow must not stop the stream with a failure when a line with a data problem is encountered.
     * Instead, each line that fails to be transformed into a {@link TemperatureReading} must be skipped.
     * <p/>
     * Reference: http://doc.akka.io/docs/akka/current/java/stream/stream-error.html
     * <p/>
     * Check: The kata is solved when the corresponding unit test is green.
     *
     * @return The source.
     */
    static Flow<String, TemperatureReading, NotUsed> createRobustFlowOfStringToTemperatureReading() {
        return null;
    }

}
