package com.thunisoft.znbq.bbq.service;

import com.thunisoft.znbq.bbq.constant.DatabaseTypeEnum;
import com.thunisoft.znbq.bbq.dao.NormalCodeDao;
import com.thunisoft.znbq.bbq.dao.NormalCodeSystemDao;
import com.thunisoft.znbq.bbq.dao.NormalCodeTypeDao;
import com.thunisoft.znbq.bbq.model.dto.NormalCode;
import com.thunisoft.znbq.bbq.model.dto.NormalCodeSystem;
import com.thunisoft.znbq.bbq.model.dto.NormalCodeType;
import com.thunisoft.znbq.bbq.model.po.NormalCodeImportFromDatabaseParam;
import com.thunisoft.znbq.bbq.model.vo.NormalCodeNode;
import com.thunisoft.znbq.bbq.model.vo.NormalCodeTree;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jtds.util.MD5Digest;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 单值代码导入服务
 *
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.service Barbecue
 * @date 2020/8/13 0013 13:50
 */
@Slf4j
@Service
public class NormalCodeImportService {

    @Autowired
    private NormalCodeDao normalCodeDao;

    @Autowired
    private NormalCodeTypeDao normalCodeTypeDao;

    @Autowired
    private NormalCodeSystemDao normalCodeSystemDao;

    public NormalCodeTree importFromFile(){
        return null;
    }

    public void importFromDatabase(NormalCodeImportFromDatabaseParam param) throws SQLException {
        DatabaseTypeEnum.codeOf(param.getDatabaseType()).load();
        try(Connection connection = DriverManager.getConnection(param.getDatabaseUrl(),
                param.getDatabaseUsername(), param.getDatabasePassword())){
            String encode = MD5Encoder.encode(param.getDatabaseUrl().getBytes());
            NormalCodeSystem system = new NormalCodeSystem();
            system.setName(param.getDatabaseUrl()).setCreateTime(new Date()).setDesc(encode);
            system = normalCodeSystemDao.save(system);
            List<NormalCodeType> types = executeSql(connection, param.getTypeSql(), this::getNormalCodeType);
            for (NormalCodeType type : types) {
                type.setSysId(system.getId());
            }
            types = normalCodeTypeDao.saveAll(types);

            Map<String, NormalCodeType> codeTypeMap = types.stream()
                    .collect(Collectors.toMap(NormalCodeType::getInnerKey, Function.identity(), (u, o) -> o));
            List<NormalCode> codes = executeSql(connection, param.getCodeSql(), r -> getNormalCode(r, codeTypeMap));
            for (NormalCode code : codes) {
                code.setSysId(system.getId());
            }
            normalCodeDao.saveAll(codes);
        }
    }

    private NormalCode getNormalCode(ResultSet r, Map<String, NormalCodeType> codeTypeMap) {
        try {
            int dmlx = r.getInt("n_bh_dmlx");
            int dm = r.getInt("n_dm");
            String mc = r.getString("c_mc");
            int sx = r.getInt("n_xssx");

            String codeType = Optional.ofNullable(codeTypeMap.get(String.valueOf(dmlx)))
                    .map(NormalCodeType::getId)
                    .orElse(String.valueOf(dmlx));

            return new NormalCode()
                    .setInnerKey(String.valueOf(dm))
                    .setOuterKey(String.valueOf(dm))
                    .setName(mc)
                    .setIdx(sx)
                    .setTypeId(codeType)
                    .setVersion(1);
        } catch (SQLException se) {
            log.warn(se.getMessage(), se);
            return null;
        }
    }

    public NormalCodeType getNormalCodeType(ResultSet r) {
        try {
            int bh = r.getInt("n_bh");
            String mc = r.getString("c_mc");
            return new NormalCodeType()
                    .setInnerKey(String.valueOf(bh))
                    .setOuterKey(String.valueOf(bh))
                    .setName(mc)
                    .setVersion(1);
        } catch (SQLException se) {
            log.warn(se.getMessage(), se);
            return null;
        }
    }

    private <T> List<T> executeSql(Connection connection, String sql, Function<ResultSet, T> resultReader) throws SQLException{
        List<T> results = new LinkedList<>();
        try(Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                T result = resultReader.apply(resultSet);
                if (result != null) {
                    results.add(result);
                }
            }
        }
        return results;
    }
}
