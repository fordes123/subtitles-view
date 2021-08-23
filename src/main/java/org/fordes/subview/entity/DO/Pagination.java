package org.fordes.subview.entity.DO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象
 *
 * @author fordes on 2021/1/5
 */
@Data
public class Pagination<T> {

    /**
     * 当前页
     */
    private Integer pageIndex = 1;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 总记录数，默认为0
     */
//    private Integer totalCount;

    /**
     * 每页数据
     */
    private List<T> rows;

    /**
     * 当前所处层级
     */
    private int level;

    /**
     * 构造对象赋值
     */
    public Pagination(Integer pageIndex, Integer pageSize, Integer totalPage, List<T> rows) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;

        if(totalPage != null){
            this.totalPage = totalPage;
        }else{
            this.totalPage = 1;
        }
        this.rows = rows;
    }

    /**
     * 无参构造
     */
    public Pagination(){
        this.pageIndex = 0;
        this.pageSize = 0;
        this.totalPage = 0;
        this.rows = new ArrayList<>();
//        this.totalCount = 0;
    }
}
