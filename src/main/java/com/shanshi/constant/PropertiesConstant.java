package com.shanshi.constant;

import com.shanshi.utils.PropertiesUtils;

public class PropertiesConstant {
    public static final String PROJECT_AUTHOR = PropertiesUtils.getProperty("project.author");


    public static final Boolean IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getProperty("ignore.table.prefix"));
    public static final String BEAN_PARAM_SUFFIX = PropertiesUtils.getProperty("bean.param.suffix");
    public static final String BEAN_PROPERTY_SUFFIX_FUZZY = PropertiesUtils.getProperty("bean.property.suffix.fuzzy");
    public static final String BEAN_PROPERTY_SUFFIX_TIME_START = PropertiesUtils.getProperty("bean.property.suffix.time.start");
    public static final String BEAN_PROPERTY_SUFFIX_TIME_END = PropertiesUtils.getProperty("bean.property.suffix.time.end");

    public static final String MAPPER_SUFFIX = PropertiesUtils.getProperty("mapper.suffix");
    public static final String PATH_JAVA = PropertiesUtils.getProperty("path.java");
    public static final String PATH_RESOURCES = PropertiesUtils.getProperty("path.resources");


    public static final String PACKAGE_BASE = PropertiesUtils.getProperty("package.base");
    public static final String PACKAGE_ENTITY_PO = PropertiesUtils.getProperty("package.entity.po");
    public static final String PACKAGE_ENTITY_QUERY = PropertiesUtils.getProperty("package.entity.query");
    public static final String PACKAGE_UTILS = PropertiesUtils.getProperty("package.utils");
    public static final String PACKAGE_ENUMS = PropertiesUtils.getProperty("package.enums");
    public static final String PACKAGE_MAPPER = PropertiesUtils.getProperty("package.mapper");
    public static final String PACKAGE_SERVICE = PropertiesUtils.getProperty("package.service");

    public static final String PACKAGE_EXCEPTION = PropertiesUtils.getProperty("package.exception");
    public static final String PACKAGE_CONTROLLER = PropertiesUtils.getProperty("package.controller");
    public static final String PACKAGE_SERVICE_IMPL = PropertiesUtils.getProperty("package.service.impl");
    public static final String PACKAGE_ENTITY_VO = PropertiesUtils.getProperty("package.entity.vo");
    public static final String PACKAGE_ENTITY_DTO = PropertiesUtils.getProperty("package.entity.dto");

    public static final String PACKAGE_PO_RELATIVE = PACKAGE_BASE + "." + PACKAGE_ENTITY_PO;
    public static final String PACKAGE_QUERY_RELATIVE = PACKAGE_BASE + "." + PACKAGE_ENTITY_QUERY;
    public static final String PACKAGE_ENTITY_VO_RELATIVE = PACKAGE_BASE + "." + PACKAGE_ENTITY_VO;
    public static final String PACKAGE_ENTITY_DTO_RELATIVE = PACKAGE_BASE + "." + PACKAGE_ENTITY_DTO;
    public static final String PACKAGE_UTILS_RELATIVE = PACKAGE_BASE + "." + PACKAGE_UTILS;
    public static final String PACKAGE_ENUMS_RELATIVE = PACKAGE_BASE + "." + PACKAGE_ENUMS;
    public static final String PACKAGE_MAPPER_RELATIVE = PACKAGE_BASE + "." + PACKAGE_MAPPER;
    public static final String PACKAGE_SERVICE_RELATIVE = PACKAGE_BASE + "." + PACKAGE_SERVICE;
    public static final String PACKAGE_EXCEPTION_RELATIVE = PACKAGE_BASE + "." + PACKAGE_EXCEPTION;
    public static final String PACKAGE_CONTROLLER_RELATIVE = PACKAGE_BASE + "." + PACKAGE_CONTROLLER;
    public static final String PACKAGE_SERVICE_IMPL_RELATIVE = PACKAGE_BASE + "." + PACKAGE_SERVICE + "." + PACKAGE_SERVICE_IMPL;

    public static final String PATH_PACKAGE_BASE = PATH_JAVA + "/" + PACKAGE_BASE.replace(".","/");
    public static final String PATH_RESOURCE_BASE = PATH_RESOURCES + "/" + PACKAGE_BASE.replace(".","/");
    public static final String PATH_PO = PATH_PACKAGE_BASE + "/" + PACKAGE_ENTITY_PO.replace(".","/");
    public static final String PATH_QUERY = PATH_PACKAGE_BASE + "/" + PACKAGE_ENTITY_QUERY.replace(".","/");
    public static final String PATH_UTILS = PATH_PACKAGE_BASE + "/" + PACKAGE_UTILS.replace(".","/");
    public static final String PATH_ENUMS = PATH_PACKAGE_BASE + "/" + PACKAGE_ENUMS.replace(".","/");
    public static final String PATH_MAPPER = PATH_PACKAGE_BASE + "/" + PACKAGE_MAPPER.replace(".","/");
    public static final String PATH_MAPPER_XML = PATH_RESOURCE_BASE + "/" + PACKAGE_MAPPER.replace(".","/");
    public static final String PATH_SERVICE = PATH_PACKAGE_BASE + "/" + PACKAGE_SERVICE.replace(".","/");

    public static final String PATH_EXCEPTION = PATH_PACKAGE_BASE + "/" + PACKAGE_EXCEPTION.replace(".","/");
    public static final String PATH_CONTROLLER = PATH_PACKAGE_BASE + "/" + PACKAGE_CONTROLLER.replace(".","/");
    public static final String PATH_SERVICE_IMPL = PATH_PACKAGE_BASE + "/" + PACKAGE_SERVICE + "/" + PACKAGE_SERVICE_IMPL;
    public static final String PATH_VO = PATH_PACKAGE_BASE + "/" + PACKAGE_ENTITY_VO.replace(".","/");
    public static final String PATH_DTO = PATH_PACKAGE_BASE + "/" + PACKAGE_ENTITY_DTO.replace(".","/");


    public static final String IGNORE_BEAN_TOJSON_FIELD = PropertiesUtils.getProperty("ignore.bean.tojson.field");
    public static final String IGNORE_BEAN_TOJSON_EXPRESSION = PropertiesUtils.getProperty("ignore.bean.tojson.expression");
    public static final String IGNORE_BEAN_TOJSON_CLASS = PropertiesUtils.getProperty("ignore.bean.tojson.class");


    public static final String BEAN_DATE_SERIALIZE_EXPRESSION = PropertiesUtils.getProperty("bean.date.serialize.expression");
    public static final String BEAN_DATE_SERIALIZE_CLASS = PropertiesUtils.getProperty("bean.date.serialize.class");
    public static final String BEAN_DATE_DESERIALIZE_EXPRESSION = PropertiesUtils.getProperty("bean.date.deserialize.expression");
    public static final String BEAN_DATE_DESERIALIZE_CLASS = PropertiesUtils.getProperty("bean.date.deserialize.class");
}
