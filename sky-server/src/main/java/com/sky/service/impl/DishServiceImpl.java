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

import javax.swing.plaf.basic.BasicListUI;
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
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//
//            //删除菜品关联的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//
//        }

        //根据菜品id集合批量删除菜品数据
        //sql:delete from dish id in (?,?,?)
        dishMapper.deleteByIds(ids);


        //根据菜品id集合批量删除关联的口味数据
        //sql:delete from dish_flavor dish_id in (?,?,?)
        dishFlavorMapper.deleteByDishIds(ids);


    }


    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id) {

        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);

        //根据菜品id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        //将查询到的数据封装到VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);//数据拷贝
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }


    /**
     * 根据id修改菜品基本信息和对应口味信息
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //修改菜品表基本信息
        dishMapper.update(dish);

            //删除原有的口味数据,先删除再插入
        dishFlavorMapper.deleteByDishId(dishDTO.getId());//删除原有口味数据

        List<DishFlavor> flavors=dishDTO.getFlavors();//重新插入口味数据
        //判断一下有口味数据，就遍历一下，重新设置dishID
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());//从DTO获取dishID
            });
            //向口味表插入n条数据
            dishFlavorMapper.inserBatch(flavors);
        }

    }
}
