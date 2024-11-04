package club.cybecraftman.leek.common.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BasePageDTO {

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer count;

    public Integer getOffset() {
        if ( this.page == null || this.page <= 0 ) {
            return 0;
        }
        return (this.page - 1) * getLimit();
    }

    public Integer getLimit() {
        if ( this.count == null || this.count <= 0 ) {
            return 0;
        }
        return this.count;
    }

}
