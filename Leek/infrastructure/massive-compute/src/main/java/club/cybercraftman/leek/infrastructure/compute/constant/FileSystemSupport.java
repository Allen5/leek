package club.cybercraftman.leek.infrastructure.compute.constant;

import club.cybercraftman.leek.common.exception.LeekRuntimeException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 支持的文件系统类型
 */
@Getter
public enum FileSystemSupport {

    S3("s3", "s3://","S3协议"),
    FILE("file", "file://","本地文件"),
    HDFS("hdfs", "hdfs://", "HDFS文件系统"),

    ;

    FileSystemSupport(final String fs, final String protocol, final String description) {
        this.fs = fs;
        this.protocol = protocol;
        this.description = description;
    }

    private final String fs;

    private final String protocol;

    private final String description;

    public static FileSystemSupport parse(final String filepath) {
        Optional<FileSystemSupport> op = Arrays.stream(FileSystemSupport.values())
                .filter(f -> filepath.startsWith(f.protocol))
                .findAny();
        if ( op.isEmpty() ) {
            throw new LeekRuntimeException("不支持的文件系统类型. 文件路径: " + filepath);
        }
        return op.get();
    }

}
