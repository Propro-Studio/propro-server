package com.westlake.air.pecs.service.impl;

import com.westlake.air.pecs.constants.ResultCode;
import com.westlake.air.pecs.dao.AnalyseOverviewDAO;
import com.westlake.air.pecs.domain.ResultDO;
import com.westlake.air.pecs.domain.bean.analyse.ComparisonResult;
import com.westlake.air.pecs.domain.db.AnalyseOverviewDO;
import com.westlake.air.pecs.domain.db.simple.MatchedPeptide;
import com.westlake.air.pecs.domain.query.AnalyseOverviewQuery;
import com.westlake.air.pecs.service.AnalyseOverviewService;
import com.westlake.air.pecs.service.ScoresService;
import com.westlake.air.pecs.utils.SortUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by James Lu MiaoShan
 * Time: 2018-07-19 16:40
 */
@Service("analyseOverviewService")
public class AnalyseOverviewServiceImpl implements AnalyseOverviewService {

    public final Logger logger = LoggerFactory.getLogger(AnalyseOverviewServiceImpl.class);

    @Autowired
    AnalyseOverviewDAO analyseOverviewDAO;
    @Autowired
    ScoresService scoresService;

    @Override
    public List<AnalyseOverviewDO> getAllByExpId(String expId) {
        return analyseOverviewDAO.getAllByExperimentId(expId);
    }

    @Override
    public Long count(AnalyseOverviewQuery query) {
        return analyseOverviewDAO.count(query);
    }

    @Override
    public ResultDO<List<AnalyseOverviewDO>> getList(AnalyseOverviewQuery targetQuery) {
        List<AnalyseOverviewDO> dataList = analyseOverviewDAO.getList(targetQuery);
        long totalCount = analyseOverviewDAO.count(targetQuery);
        ResultDO<List<AnalyseOverviewDO>> resultDO = new ResultDO<>(true);
        resultDO.setModel(dataList);
        resultDO.setTotalNum(totalCount);
        resultDO.setPageSize(targetQuery.getPageSize());
        return resultDO;
    }

    @Override
    public ResultDO insert(AnalyseOverviewDO overviewDO) {
        try {
            overviewDO.setCreateDate(new Date());
            overviewDO.setLastModifiedDate(new Date());
            analyseOverviewDAO.insert(overviewDO);
            return ResultDO.build(overviewDO);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return ResultDO.buildError(ResultCode.INSERT_ERROR);
        }
    }

    @Override
    public ResultDO update(AnalyseOverviewDO overviewDO) {
        if (overviewDO.getId() == null || overviewDO.getId().isEmpty()) {
            return ResultDO.buildError(ResultCode.ID_CANNOT_BE_NULL_OR_ZERO);
        }

        try {
            overviewDO.setLastModifiedDate(new Date());
            analyseOverviewDAO.update(overviewDO);
            return ResultDO.build(overviewDO);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return ResultDO.buildError(ResultCode.INSERT_ERROR);
        }
    }

    @Override
    public ResultDO delete(String id) {
        if (id == null || id.isEmpty()) {
            return ResultDO.buildError(ResultCode.ID_CANNOT_BE_NULL_OR_ZERO);
        }
        try {
            analyseOverviewDAO.delete(id);
            return new ResultDO(true);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return ResultDO.buildError(ResultCode.DELETE_ERROR);
        }
    }

    @Override
    public ResultDO deleteAllByExpId(String expId) {
        if (expId == null || expId.isEmpty()) {
            return ResultDO.buildError(ResultCode.ID_CANNOT_BE_NULL_OR_ZERO);
        }
        try {
            analyseOverviewDAO.deleteAllByExperimentId(expId);
            return new ResultDO(true);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return ResultDO.buildError(ResultCode.DELETE_ERROR);
        }
    }

    @Override
    public ResultDO<AnalyseOverviewDO> getById(String id) {
        try {
            AnalyseOverviewDO analyseOverviewDO = analyseOverviewDAO.getById(id);
            if (analyseOverviewDO == null) {
                return ResultDO.buildError(ResultCode.OBJECT_NOT_EXISTED);
            } else {
                ResultDO<AnalyseOverviewDO> resultDO = new ResultDO<>(true);
                resultDO.setModel(analyseOverviewDO);
                return resultDO;
            }
        } catch (Exception e) {
            return ResultDO.buildError(ResultCode.QUERY_ERROR);
        }
    }

    @Override
    public ResultDO<AnalyseOverviewDO> getFirstByExpId(String expId) {
        try {
            AnalyseOverviewDO overviewDO = analyseOverviewDAO.getFirstByExperimentId(expId);
            if (overviewDO == null) {
                return ResultDO.buildError(ResultCode.OBJECT_NOT_EXISTED);
            } else {
                ResultDO<AnalyseOverviewDO> resultDO = new ResultDO<>(true);
                resultDO.setModel(overviewDO);
                return resultDO;
            }
        } catch (Exception e) {
            return ResultDO.buildError(ResultCode.QUERY_ERROR);
        }
    }

    @Override
    public ComparisonResult comparison(HashSet<String> overviewIds) {
        ComparisonResult resultMap = new ComparisonResult();

        HashMap<String, List<MatchedPeptide>> map = new HashMap<>();
        for (String overviewId : overviewIds) {
            AnalyseOverviewDO temp = analyseOverviewDAO.getById(overviewId);
            if (temp != null) {
                //如果MatchPeptideCount为空,则表明分析还没有完成,无法参与横向比对
                if (temp.getMatchedPeptideCount() != null) {
                    List<MatchedPeptide> peptides = scoresService.getAllMatchedPeptides(overviewId);
                    map.put(temp.getId(), peptides);
                }
            }
        }
        //所有的需要比对的肽段取并集
        HashSet<MatchedPeptide> totalPeptides = new HashSet<>();
        for(List<MatchedPeptide> peptides : map.values()){
            totalPeptides.addAll(peptides);
        }

        HashSet<MatchedPeptide> samePeptides = new HashSet<>();
        HashSet<MatchedPeptide> diffPeptides = new HashSet<>();
        HashMap<String, List<Boolean>> identifiesMap = new HashMap<>();


        return resultMap;
    }
}
