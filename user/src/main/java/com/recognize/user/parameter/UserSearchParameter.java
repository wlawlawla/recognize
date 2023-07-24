package com.recognize.user.parameter;

import lombok.Data;

import java.util.List;

@Data
public class UserSearchParameter {

    /**
     * 搜索关键字 (userName fullName nickName)
     */
    private String searchText;

    /**
     * 部门id
     */
    private List<Long> departmentIds;


}
