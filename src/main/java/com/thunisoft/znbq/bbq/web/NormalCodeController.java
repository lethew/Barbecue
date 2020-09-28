package com.thunisoft.znbq.bbq.web;

import com.thunisoft.znbq.bbq.model.po.NormalCodeImportFromDatabaseParam;
import com.thunisoft.znbq.bbq.model.vo.NormalCodeTree;
import com.thunisoft.znbq.bbq.service.NormalCodeImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

/**
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.web Barbecue
 * @date 2020/8/13 0013 14:09
 */
@Controller
@RequestMapping("/code")
public class NormalCodeController {
    @Autowired
    private NormalCodeImportService normalCodeImportService;
    public ResponseEntity<NormalCodeTree> importFromFile() {
        return null;
    }
    @PostMapping("/import/database")
    public ResponseEntity<NormalCodeTree> importFromDatabase(NormalCodeImportFromDatabaseParam param) throws SQLException {
        normalCodeImportService.importFromDatabase(param);
        return null;
    }


}
