package utils;

import java.io.IOException;

/**
 * @Author: LX
 * @Date: 2019/4/18 18:13
 * @Version: 1.0
 */
@SuppressWarnings("WeakerAccess")
public class Utils {
    public static final DataUtil DATA_UTIL = new DataUtil();
    public static final FileUtil FILE_UTIL = new FileUtil();
    public static ZKUtil ZK_UTIL;
    public static KafkaUtil KAFKA_UTIL;
    public static PulsarUtil PULSAR_UTIL;
    public static PropertiesUtil PROPERTIES_UTIL;

    static {
        try {
            PROPERTIES_UTIL = new PropertiesUtil(FILE_UTIL);
            ZK_UTIL = new ZKUtil(PROPERTIES_UTIL);
            KAFKA_UTIL = new KafkaUtil(ZK_UTIL, PROPERTIES_UTIL);
            PULSAR_UTIL = new PulsarUtil(PROPERTIES_UTIL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
