package net.codecamp.akka.streams.java.kata03;

import akka.NotUsed;
import akka.stream.IOResult;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import net.codecamp.akka.streams.java.model.TemperatureReading;

import java.util.concurrent.CompletionStage;

class Kata03CsvSource {

    /**
     * Task: Create a akka.stream.javadsl.Source that emits line by line of kata03.csv file.
     * <p/>
     * Reference: See kata02.
     *
     * @return The source.
     */
    static Source<String, CompletionStage<IOResult>> createSourceOfLinesInFileKata03Csv() {
        return null;
    }

    /**
     * Task: Create a akka.stream.javadsl.Flow that accepts a string
     * and transforms (i.e. parses) it into a {@link TemperatureReading}.
     * In the unit test the inlet of this flow will be connected to the outlet of the source
     * created by {@link Kata03CsvSource#createSourceOfLinesInFileKata03Csv()}.
     * Thus the flow needs to expect and treat the string to be in the format of a line in the given CSV file.
     * <p/>
     * Reference: http://doc.akka.io/docs/akka/current/java/stream/stream-flows-and-basics.html#Defining_sources__sinks_and_flows
     * <p/>
     * Check: The kata is solved when the corresponding unit test is green.
     *
     * @return The source.
     */
    static Flow<String, TemperatureReading, NotUsed> createFlowOfStringToTemperatureReading() {
        return null;
    }

}
