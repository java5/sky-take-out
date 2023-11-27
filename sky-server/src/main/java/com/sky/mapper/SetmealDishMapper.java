package com.sky.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//加上mapper注解
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    //select setmeal id from setmeal dish where dish_id in (1,2,3,4)
    //写到xml映射文件中
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

}
