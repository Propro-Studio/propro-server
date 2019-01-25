package com.westlake.air.propro.domain.db;

import com.westlake.air.propro.domain.BaseDO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by James Lu MiaoShan
 * Time: 2018-06-04 21:16
 */
@Data
@Document(collection = "library")
public class LibraryDO extends BaseDO {

    public static Integer TYPE_STANDARD = 0;
    public static Integer TYPE_IRT = 1;

    private static final long serialVersionUID = -3258829839160856625L;

    @Id
    String id;

    @Indexed(unique = true)
    String name;

    String fatherId;

    //类似于PRM实验，需要在卷积中同时使用iRT库；
    Boolean needIrt = false;

    //0:标准库,1:iRT校准库
    Integer type;

    String description;

    //蛋白总数目
    Long proteinCount;
//
//    //真肽段数目
//    Long peptideCount;

    //肽段总数目
    Long totalCount;

    //真肽段片段总数目
    Long totalTargetCount;

    //为肽段片段总数目
    Long totalDecoyCount;

    Date createDate;

    Date lastModifiedDate;

    String creator = "Admin";

}
