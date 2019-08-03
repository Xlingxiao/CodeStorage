package dataDistribution.mybatis.mapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * @Author: LX
 * @Date: 2019/4/25 13:46
 * @Version: 1.0
 */
public class MapperFactory {
    //mybatis的配置文件
    private static final String resource = "spring-mybatis.xml";
    private final SqlSession session;

    /*初始化Mapper工厂*/
    public MapperFactory() throws IOException {
        //使用MyBatis提供的Resources类加载mybatis的配置文件（它也加载关联的映射文件）
        Reader reader = Resources.getResourceAsReader(resource);
        //构建sqlSession的工厂
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        session = sessionFactory.openSession();
    }

    /*获得MessageStatusMapper*/
    @SuppressWarnings("unchecked")
    public MessageStatusMapper getMessageMapper(Class var1) {
        //获得mapper对象
        return (MessageStatusMapper) session.getMapper(var1);
    }

    /*关闭session*/
    public void closeSession() {
        session.close();
    }
}
