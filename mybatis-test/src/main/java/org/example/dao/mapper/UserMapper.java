package org.example.dao.mapper;
import org.apache.ibatis.annotations.Select;
import org.example.dao.ListDistinctAgeAndNameResult;
import org.example.dao.UserNameAndAgePO;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.User;

@Mapper
public interface UserMapper {


    List<Long> findIdByNameAndAge(@Param("name")String name,@Param("age")Integer age);


    UserNameAndAgePO getNameAndAgeById(@Param("id")Long id);

    List<Integer> listDistinctAge();


    List<ListDistinctAgeAndNameResult> listDistinctAgeAndName();


    int insertSelective(User user);



    User updateById(Long id);


    int updateById(@Param("updated")User updated,@Param("id")Long id);

    @Select("select * from user")
    List<User> selectAll();

}
