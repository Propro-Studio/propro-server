package com.westlake.air.propro.controller;

import com.westlake.air.propro.algorithm.decoy.generator.ShuffleGenerator;
import com.westlake.air.propro.domain.ResultDO;
import com.westlake.air.propro.domain.db.LibraryDO;
import com.westlake.air.propro.domain.db.PeptideDO;
import com.westlake.air.propro.domain.query.PeptideQuery;
import com.westlake.air.propro.service.PeptideService;
import com.westlake.air.propro.utils.PermissionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.westlake.air.propro.constants.Constants.MAX_INSERT_RECORD_FOR_PEPTIDE;

/**
 * Created by James Lu MiaoShan
 * Time: 2018-06-19 16:03
 */
@Controller
@RequestMapping("decoy")
public class DecoyController extends BaseController {

    @Autowired
    ShuffleGenerator shuffleGenerator;
    @Autowired
    PeptideService peptideService;

    @RequestMapping(value = "/delete")
    String delete(Model model, @RequestParam(value = "id", required = true) String id) {

        LibraryDO library = libraryService.getById(id);
        PermissionUtil.check(library);

        peptideService.deleteAllDecoyByLibraryId(id);

        libraryService.countAndUpdateForLibrary(library);
        return "redirect:/library/detail/" + id;
    }

    @RequestMapping(value = "/generate")
    String generate(Model model,
                    @RequestParam(value = "id", required = true) String id) {

        LibraryDO library = libraryService.getById(id);
        PermissionUtil.check(library);

        logger.info("正在删除原有伪肽段");
        //删除原有的伪肽段
        peptideService.deleteAllDecoyByLibraryId(id);
        logger.info("原有伪肽段删除完毕");
        //计算原始肽段数目
        PeptideQuery query = new PeptideQuery();
        query.setIsDecoy(false);
        query.setLibraryId(id);
        long totalCount = peptideService.count(query);
        int totalPage = (int) (totalCount / MAX_INSERT_RECORD_FOR_PEPTIDE) + 1;
        query.setPageSize(MAX_INSERT_RECORD_FOR_PEPTIDE);
        int countForInsert = 0;
        for (int i = 1; i <= totalPage; i++) {
            query.setPageNo(i);
            ResultDO<List<PeptideDO>> resultDO = peptideService.getList(query);
            List<PeptideDO> list = shuffleGenerator.generate(resultDO.getModel());
            ResultDO resultTmp = peptideService.insertAll(list, false);
            if (resultTmp.isSuccess()) {
                countForInsert += list.size();
                logger.info("插入新生成伪肽段" + countForInsert + "条");
            }
        }

        libraryService.countAndUpdateForLibrary(library);

        return "redirect:/library/detail/" + id;
    }
}
