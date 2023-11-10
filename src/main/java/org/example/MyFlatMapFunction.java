package org.example.util;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Arrays;
@Component
public class MyFlatMapFunction implements FlatMapFunction<String,String> {

    @Override
    public void flatMap(String s, Collector<String> collector) throws Exception {
        String[] arr = s.split(",");
        System.out.println(Arrays.toString(arr));
        JSONObject obj=new JSONObject();
        int i =1;
        String jsonkey;
        for (String input:arr) {
            jsonkey = "key"+i;
            obj.put(jsonkey,input);
            i++;
        }
        collector.collect(obj.toString());
    }
}
