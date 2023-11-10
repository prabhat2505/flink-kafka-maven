package org.example;


import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.example.util.MyFlatMapFunction;
import java.io.FileInputStream;
import java.util.Properties;




// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    private static String bootstarpServer;
    private static String topic;
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/application.properties"));
        bootstarpServer = properties.getProperty("spring.kafka.producer.bootstrap-servers");
        topic = properties.getProperty("spring.kafka.topic.name");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String path = "src\\main\\resources\\file";

        System.out.println(properties.getProperty("spring.kafka.producer.bootstrap-servers"));


          // Deprecated approach
//        TextInputFormat textInputFormat = new TextInputFormat(new Path(path));
//        DataStream<String> stream = env.readFile(textInputFormat,path);


        final FileSource<String> source =
                FileSource.forRecordStreamFormat(new TextLineInputFormat(),new Path(path))
                        .build();
        final DataStream<String> stream =
        env.fromSource(source, WatermarkStrategy.noWatermarks(), "file-source");
        //stream.print();
        stream.flatMap(new MyFlatMapFunction()).print();
        //CloseableIterator<String> data = stream.executeAndCollect();//not used but we can iterate data

        // Passing stream to kafka
        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers(bootstarpServer)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(topic)
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)

                .build();
        stream.sinkTo(sink);
        env.execute();
    }
}
