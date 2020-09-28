package com.thunisoft.znbq.bbq.smd;

import com.thunisoft.znbq.bbq.smd.diff.CompareResult;
import com.thunisoft.znbq.bbq.smd.diff.SmdVersionBase;
import com.thunisoft.znbq.bbq.smd.diff.VersionComparator;
import com.thunisoft.znbq.bbq.smd.gen.SmdFileGen;
import com.thunisoft.znbq.bbq.smd.model.DatabaseInfo;
import com.thunisoft.znbq.bbq.smd.model.DatabaseSnapshot;
import com.thunisoft.znbq.bbq.smd.model.TableFilter;
import com.thunisoft.znbq.bbq.smd.sql.SqlGenerator;
import com.thunisoft.znbq.bbq.util.ZipUtil;
import lombok.experimental.UtilityClass;
import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * @author <a href="mailto:wuzhao-1@thunisoft.com>Zhao.Wu</a>
 * @description com.thunisoft.znbq.bbq.smd Barbecue
 * @date 2020/9/28 0028 21:46
 */
@UtilityClass
public class SmdApi {
    public DatabaseSnapshot createSmd(DatabaseSnapshot snapshot, DatabaseInfo databaseInfo, List<TableFilter> filters) throws SQLException, ClassNotFoundException {
        String s = SmdFileGen.buildSmd(snapshot, databaseInfo, filters);
        snapshot.setSmdPath(s);
        return snapshot;
    }

    public DatabaseSnapshot generateScript(DatabaseSnapshot previous, DatabaseSnapshot target) {
        SmdVersionBase np = new SmdVersionBase(previous.getSmdPath());
        SmdVersionBase t3 = new SmdVersionBase(target.getSmdPath());
        CompareResult compare = VersionComparator.compare(np, t3);
        SqlGenerator sqlGenerator = SqlGenerator.getInstance(target.getDatabaseType());
        String path = compare.generateScript(sqlGenerator);
        target.setScriptPath(path);
        return target;
    }

    public void downloadSmd(DatabaseSnapshot snapshot, OutputStream outputStream) throws IOException {
        InputStream zip = ZipUtil.zip(new File(snapshot.getSmdPath()));
        IOUtils.copy(zip, outputStream);
    }

    public void downloadScript(DatabaseSnapshot snapshot, OutputStream outputStream) throws IOException {
        InputStream zip = ZipUtil.zip(new File(snapshot.getScriptPath()));
        IOUtils.copy(zip, outputStream);
    }
}
