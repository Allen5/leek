package club.cybecraftman.leek.common.constant.finance.future;

import lombok.Getter;

@Getter
public enum ProductCategory {

    未分类(0, "未分类"),
    能源化工(1000, "能源化工"),
    // TODO: 待整理添加

    ;

    ProductCategory(final Integer category, final String description) {
        this.category = category;
        this.description = description;
    }

    private Integer category;

    private String description;

}
