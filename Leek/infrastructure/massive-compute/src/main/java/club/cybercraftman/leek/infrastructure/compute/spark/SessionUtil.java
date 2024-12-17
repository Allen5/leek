package club.cybercraftman.leek.infrastructure.compute.spark;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.SparkSession;
import org.springframework.util.CollectionUtils;

@Slf4j
public class SessionUtil {

    private static final SessionUtil INSTANCE = new SessionUtil();

    @Getter
    private SparkSession session;

    private SessionUtil() {
    }

    public static SessionUtil getInstance() {
        return INSTANCE;
    }

    public SparkSession get() {
        if ( null == this.session ) {
            log.error("spark session is not initialized. please invoke init first!");
            throw new LeekRuntimeException("spark session is not initialized. please invoke init first!");
        }
        return this.session;
    }

    public SparkSession init(final String appName, final String sparkMaster, SessionParam param) {
        if ( null != this.session ) {
            log.warn("spark session is already initialized. ignore params with appName: {}. sparkMaster: {} and params: {}",
                    appName, sparkMaster, param);
            return this.session;
        }
        SparkSession.Builder builder = SparkSession.builder();
        builder.appName(appName);
        builder.master(sparkMaster);
        if ( null != param && !CollectionUtils.isEmpty(param.getParams()) ) {
            builder.config(param.getParams());
        }
        this.session = builder.getOrCreate();
        return this.session;
    }

    public void release() {
        if ( null == this.session ) {
            return ;
        }
        this.session.close();
        this.session = null;
    }


}
