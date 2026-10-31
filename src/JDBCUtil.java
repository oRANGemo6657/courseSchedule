import com.alibaba.druid.pool.DruidDataSourceFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtil {
    private static DataSource ds;
    static{
        try{
            Properties prop = new Properties();
            InputStream is = JDBCUtil.class.getClassLoader().getResourceAsStream("druid.properties");
            prop.load(is);
            ds = DruidDataSourceFactory.createDataSource(prop);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static Connection getConn() throws SQLException{
        return ds.getConnection();
    }
}
