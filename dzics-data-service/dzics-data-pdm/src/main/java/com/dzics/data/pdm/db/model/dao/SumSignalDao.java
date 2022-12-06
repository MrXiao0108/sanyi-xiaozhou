package com.dzics.data.pdm.db.model.dao;

import lombok.Data;

/**
 * @Classname SumSignal
 * @Description 描述
 * @Date 2022/3/1 15:45
 * @Created by NeverEnd
 */
@Data
public class SumSignalDao {
    private Long nowNum;
    private Long roughNum;
    private Long qualifiedNum;
    private Long badnessNum;
}
