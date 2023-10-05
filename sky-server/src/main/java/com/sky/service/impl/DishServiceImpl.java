package com.sky.service.impl;

//import com.aliyuncs.kms.model.v20160120.ListKeysRequest;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
//import jdk.jshell.Snippet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    //删除菜品注入
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应口味
     *
     * @param dishDTO
     */
    @Transactional//事务注解
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO, dish);//拷贝数据

        //向菜品插入一条数据
        dishMapper.insert(dish);

        //获取insert语句生成的主键值
        Long dishID = dish.getId();


        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishID);
            });
            //向口味表插入n条数据
            dishFlavorMapper.inserBatch(flavors);
        }

    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除功能
     * @param ids
     */
    @Transactional//事务注解，保证数据的一致性
    public void deleBatch(List<Long> ids) {//先写注释明确逻辑

        //判断当前菜品是否能够删除---是否存在起售中的菜品？
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            //判断起售停售 1=起售 用常量类
            if(dish.getStatus() == StatusConstant.ENABLE){
                //当前菜品处于起售中，不能删除；抛异常
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断当前菜品是否能够删除---是否被套餐关联了
        List<Long> setmealIDs = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIDs != null && setmealIDs.size() > 0 ){
            //当前菜品被关联了，不能删除、
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中的菜品数据
        for (Long id : ids) {
            dishMapper.deleteById(id);


            //删除菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);


        }
    }
}
