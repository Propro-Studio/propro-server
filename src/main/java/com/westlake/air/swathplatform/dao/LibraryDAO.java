package com.westlake.air.swathplatform.dao;

import com.westlake.air.swathplatform.domain.db.LibraryDO;
import com.westlake.air.swathplatform.domain.db.TransitionDO;
import com.westlake.air.swathplatform.domain.query.LibraryQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by James Lu MiaoShan
 * Time: 2018-06-13 13:16
 */
@Service
public class LibraryDAO {

    public static String CollectionName = "library";
    @Autowired
    MongoTemplate mongoTemplate;

    public long count(LibraryQuery query) {
        return mongoTemplate.count(buildQueryWithoutPage(query), LibraryDO.class);
    }

    public List<LibraryDO> getList(LibraryQuery query) {
        return mongoTemplate.find(buildQuery(query), LibraryDO.class, CollectionName);
    }

    public LibraryDO getById(String id) {
        return mongoTemplate.findById(id, LibraryDO.class, CollectionName);
    }

    public LibraryDO getByName(String name) {
        LibraryQuery query = new LibraryQuery();
        query.setName(name);
        return mongoTemplate.findOne(buildQuery(query), LibraryDO.class, CollectionName);
    }

    public LibraryDO insert(LibraryDO libraryDO) {
        mongoTemplate.insert(libraryDO, CollectionName);
        return libraryDO;
    }

    public LibraryDO update(LibraryDO libraryDO) {
        mongoTemplate.save(libraryDO, CollectionName);
        return libraryDO;
    }

    public List<LibraryDO> insertAll(List<LibraryDO> libraries) {
        mongoTemplate.insert(libraries, CollectionName);
        return libraries;
    }

    public void delete(String id) {
        mongoTemplate.remove(id, CollectionName);
    }

    public void deleteAllByLibraryId(String libraryId) {
        Query query = new Query(where("libraryId").is(libraryId));
        mongoTemplate.remove(query, TransitionDO.class, CollectionName);
    }

    private Query buildQuery(LibraryQuery libraryQuery) {
        Query query = new Query();
        if (libraryQuery.getId() != null) {
            query.addCriteria(where("id").is(libraryQuery.getId()));
        }
        if (libraryQuery.getName() != null) {
            //加个i支持不区分大小写的模糊查询
            query.addCriteria(where("name").regex(libraryQuery.getName(),"i"));
        }
        query.skip((libraryQuery.getPageNo()-1) * libraryQuery.getPageSize());
        query.limit(libraryQuery.getPageSize());
        query.with(new Sort(libraryQuery.getOrderBy(), libraryQuery.getSortColumn()));
        return query;
    }

    private Query buildQueryWithoutPage(LibraryQuery libraryQuery) {
        Query query = new Query();
        if (libraryQuery.getId() != null) {
            query.addCriteria(where("id").is(libraryQuery.getId()));
        }
        if (libraryQuery.getName() != null) {
            query.addCriteria(where("name").regex(libraryQuery.getName()));
        }
        return query;
    }
}
