package club.cybercraftman.leek.infrastructure.compute.job;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class JobSelector {

    @Autowired
    private List<AbstractEtlJob> jobs;

    public AbstractEtlJob findJob(final String functionId, final String masterUrl) {
        Optional<AbstractEtlJob> op = jobs.stream().filter(j -> j.getId().equals(functionId)).findAny();
        if ( op.isEmpty() ) {
            throw new LeekRuntimeException("不存在id为: " + functionId + "任务");
        }
        AbstractEtlJob job = op.get();
        if ( null != masterUrl ) {
            job.setMasterUrl(masterUrl);
        }
        return job;
    }

}
