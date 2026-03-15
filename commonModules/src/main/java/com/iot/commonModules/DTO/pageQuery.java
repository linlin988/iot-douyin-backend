package com.iot.commonModules.DTO;


import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询实体")
public class pageQuery {


    @Schema(description = "页码",defaultValue = "1")
    private Long pageNo;
    @Schema(description = "页大小",defaultValue = "10")
    private Long pageSize;
    @Schema(description = "排序字段")
    private String sortBy;
    @Schema(description = "是否升序")
    private Boolean isAsc;


    //前端 pageQuery 转换为 MybatisPlus Page（可以进行查询）
    
    public <T> Page<T> toMpPage(OrderItem... orders){
        // 1.分页条件 - 处理 null 值，提供默认值
        long pageNum = pageNo != null ? pageNo : 1L;  // 默认第 1 页
        long pageSizeVal = pageSize != null ? pageSize : 10L;  // 默认每页 10 条
            
        Page<T> p = Page.of(pageNum, pageSizeVal);
        // 2.排序条件
        // 2.1.先看前端有没有传排序字段
        if (sortBy != null) {
            OrderItem orderItem = new OrderItem();
            orderItem.setColumn(sortBy);
            orderItem.setAsc(isAsc);
            p.addOrder(orderItem);
            return p;
        }
        // 2.2.再看有没有手动指定排序字段
        if(orders != null){
            p.addOrder(orders);
        }
        return p;
    }

    public <T> Page<T> toMpPage(String defaultSortBy, boolean isAsc){
        return this.toMpPage(new OrderItem()
                .setColumn(defaultSortBy)
                .setAsc(isAsc));
    }

    public <T> Page<T> toMpPageDefaultSortByCreateTimeDesc() {
        return toMpPage("create_time", false);
    }

    public <T> Page<T> toMpPageDefaultSortByUpdateTimeDesc() {
        return toMpPage("update_time", false);
    }

}
