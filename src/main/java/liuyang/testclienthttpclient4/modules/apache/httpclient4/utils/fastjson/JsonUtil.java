package liuyang.testclienthttpclient4.modules.apache.httpclient4.utils.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.LinkedList;

/**
 * 预订制特性的Fastjson工具类
 *
 * @author liuyang(wx)
 * @since 2022/5/25
 */
public class JsonUtil {
    private static LinkedList<SerializerFeature> serializerFeatures;
    static {
        serializerFeatures = new LinkedList<>();
        //serializerFeatures.add(SerializerFeature.PrettyFormat);
        serializerFeatures.add(SerializerFeature.WriteMapNullValue);
        serializerFeatures.add(SerializerFeature.DisableCircularReferenceDetect);
        // 日期
        serializerFeatures.add(SerializerFeature.WriteDateUseDateFormat);
        // 加之前：{"bd":14632796719163961436738196341296219,"d":1.1,"id":1,"info":"foo 中文！","name":"liuyang","nullProperty":null,"testDate":1651112665061,"testJSR310Date":"2022-04-28T10:24:25.067745900"}
        // 加之后：{"bd":14632796719163961436738196341296219,"d":1.1,"id":1,"info":"foo 中文！","name":"liuyang","nullProperty":null,"testDate":"2022-04-28 10:27:07","testJSR310Date":"2022-04-28 10:27:07"}
    }

    public static String toJSONString(Object obj) {
        if (null == obj) return "";// 这个策略待调试协商。Fastjson
        return JSON.toJSONString(obj, serializerFeatures.toArray(new SerializerFeature[]{}));
    }

    public static <T> T parseObject(String jsonPrimitive, Class<T> clazz) {
        return JSON.parseObject(jsonPrimitive, clazz);
    }
}
