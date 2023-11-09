package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.example.pojo.Employee;
import org.springframework.beans.factory.annotation.Value;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    @Value("${spring.kafka.topic.name}")
    public static String topicName;
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String path = "src\\main\\resources\\file";

          // Deprecated approach
//        TextInputFormat textInputFormat = new TextInputFormat(new Path(path));
//        DataStream<String> stream = env.readFile(textInputFormat,path);
//        System.out.println(topicName);

        final FileSource<String> source =
                FileSource.forRecordStreamFormat(new TextLineInputFormat(),new Path(path))
                        .build();
        final DataStream<String> stream =
        env.fromSource(source, WatermarkStrategy.noWatermarks(), "file-source");
        stream.print();

//        KafkaSink<String> sink = KafkaSink.<String>builder()
//                .setBootstrapServers("localhost:9092")
//                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
//                        .setTopic("device_info")
//                        .setValueSerializationSchema(new SimpleStringSchema())
//                        .build()
//                )
//                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
//
//                .build();
//        stream.sinkTo(sink);
        env.execute();
    }
}