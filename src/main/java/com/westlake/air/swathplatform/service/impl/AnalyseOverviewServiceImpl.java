package com.westlake.air.swathplatform.service.impl;

import com.westlake.air.swathplatform.constants.ResultCode;
import com.westlake.air.swathplatform.dao.AnalyseOverviewDAO;
import com.westlake.air.swathplatform.domain.ResultDO;
import com.westlake.air.swathplatform.domain.db.AnalyseOverviewDO;
import com.westlake.air.swathplatform.domain.query.AnalyseOverviewQuery;
import com.westlake.air.swathplatform.service.AnalyseOverviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by James Lu MiaoShan
 * Time: 2018-07-19 16:40
 */
@Service("analyseOverviewService")
public class AnalyseOverviewServiceImpl implements AnalyseOverviewService {

    public final Logger logger = LoggerFactory.getLogger(AnalyseOverviewServiceImpl.class);

    @Autowired
    AnalyseOverviewDAO analyseOverviewDAO;

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
            analyseOverviewDAO.insert(overviewDO);
            return ResultDO.build(overviewDO);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            ResultDO resultDO = new ResultDO(false);
            resultDO.setErrorResult(ResultCode.INSERT_ERROR.getCode(), e.getMessage());
            return resultDO;
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
            ResultDO resultDO = new ResultDO(false);
            resultDO.setErrorResult(ResultCode.DELETE_ERROR.getCode(), e.getMessage());
            return resultDO;
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
            ResultDO resultDO = new ResultDO(false);
            resultDO.setErrorResult(ResultCode.DELETE_ERROR.getCode(), e.getMessage());
            return resultDO;
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
            ResultDO resultDO = new ResultDO(false);
            resultDO.setErrorResult(ResultCode.QUERY_ERROR.getCode(), e.getMessage());
            return resultDO;
        }
    }
}
