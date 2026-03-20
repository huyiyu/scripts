package org.example.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.entity.Company;

@Mapper
public interface CompanyMapper {

    @Select("select * from company where id = #{id}}")
    Company getCompanyById(String id);


}
