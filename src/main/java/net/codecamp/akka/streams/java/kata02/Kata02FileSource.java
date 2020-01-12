package net.codecamp.akka.streams.java.kata02;

import akka.NotUsed;
import akka.kafka.ProducerMessage;
import akka.stream.IOResult;
import akka.stream.javadsl.*;
import akka.util.ByteString;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.File;
import java.util.concurrent.CompletionStage;

class Kata02FileSource {

    /**
     * Task: Create a akka.stream.javadsl.Source that emits line by line of kata02.txt file.
     * <p/>
     * Tip: The direct relative path to the file is usually src/main/resources/kata02.txt when using an IDE.
     * <p/>
     * Tip: http://doc.akka.io/docs/akka/current/java/stream/stream-cookbook.html#Logging_elements_of_a_stream
     * <p/>
     * Reference: http://doc.akka.io/docs/akka/current/java/stream/stream-io.html#Streaming_File_IO
     * <p/>
     * Check: The kata is solved when the corresponding unit test is green.
     *
     * @return The source.
     */

    private static Flow<ByteString, ByteString, NotUsed> lineDelimiter =
            Framing.delimiter(ByteString.fromString("\n"), 10000, FramingTruncation.ALLOW);

    static Source<String, CompletionStage<IOResult>> createSourceOfLinesInFileKata02Txt() {
        return FileIO.fromFile(new File("./src/main/resources/kata02.txt"))
                .via(lineDelimiter)
                .map(ByteString::utf8String);
    }

}
