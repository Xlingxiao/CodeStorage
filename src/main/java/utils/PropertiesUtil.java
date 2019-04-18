package utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author: LX
 * @Date: 2019/4/1 19:29
 * @Version: 1.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class PropertiesUtil {

    private FileUtil util;

    public PropertiesUtil() {
        util = new FileUtil();
    }

    public PropertiesUtil(FileUtil fileUtil) {
        this.util = fileUtil;
    }

    /*根据相对路径获得properties文件*/
    public Properties getProperties(String relativePath) throws IOException {
        Properties prop = new Properties();
        prop.load(util.getRelativePath(relativePath));
        return prop;
    }

    @Test
    void main() throws IOException {
        Properties properties = getProperties("pulsar/pulsar.properties");
        System.out.println(properties.stringPropertyNames());
    }
}
