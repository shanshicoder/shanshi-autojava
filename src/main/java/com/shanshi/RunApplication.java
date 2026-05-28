package com.shanshi;

import com.shanshi.bean.TableInfo;
import com.shanshi.builder.*;
import com.shanshi.constant.PropertiesConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RunApplication {
    private static final Logger log = LoggerFactory.getLogger(RunApplication.class);

    public static void main(String[] args) {
        List<TableInfo> tables = BuildTable.getTables();

        log.info("{}", PropertiesConstant.PATH_PACKAGE_BASE);
        log.info("{}", PropertiesConstant.PATH_PO);
        log.info("{}",PropertiesConstant.PATH_QUERY);
        log.info("{}",PropertiesConstant.PACKAGE_QUERY_RELATIVE);
        log.info("{}",PropertiesConstant.PACKAGE_PO_RELATIVE);

        BuildBaseClass.execute();
        for (TableInfo table : tables) {
            BuildPo.execute(table);
            BuildQuery.execute(table);
            BuildMapper.execute(table);
            BuildMapperXml.execute(table);
            BuildService.execute(table);
            BuildServiceImpl.execute(table);
            BuildController.execute(table);
        }
    }
}
