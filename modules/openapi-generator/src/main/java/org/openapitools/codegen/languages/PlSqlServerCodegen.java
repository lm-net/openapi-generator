/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openapitools.codegen.languages;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.JsonSchema;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.codegen.*;
import org.openapitools.codegen.meta.GeneratorMetadata;
import org.openapitools.codegen.meta.Stability;
import org.openapitools.codegen.meta.features.*;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.ModelsMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class PlSqlServerCodegen extends DefaultCodegen implements CodegenConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(PlSqlServerCodegen.class);

    /**
     * Extensions for OpenAPI
     */
    public static final String VENDOR_EXTENSION_PLSQL_DATA_TYPE = "x-plsql-data-type";
    public static final String VENDOR_EXTENSION_DATA_TYPE_AND_SIZE = "x-data-type-and-size";
    public static final String VENDOR_EXTENSION_IS_JSON = "x-is-json";
    public static final String VENDOR_EXTENSION_IS_CLOB = "x-is-clob";

    /**
     * Options
     */
    public static final String SCHEMA = "schema";
    public static final String DOUBLE_QUOTE_PROPERTIES = "doubleQuoteProperties";
    public static final String DOUBLE_QUOTE_MODELS = "doubleQuoteModels";
    public static final String FILE_NAME_PREFIX = "fileNamePrefix";
    public static final String FILE_NAME_SUFFIX = "fileNameSuffix";
    public static final String DEFAULT_TIME_ZONE = "defaultTimeZone";
    public static final String VALIDATION_ERROR_CODE = "validationErrorCode";

    /**
     * Constants
     */
    public static final int IDENTIFIER_MAX_LENGTH = 128;

    /**
     * Optional Oracle schema/owner prefix (e.g. "MY_SCHEMA").
     * When set, all generated type names are prefixed with "{schemaName}.".
     */
    @Getter
    @Setter
    protected String schema = null;
    @Getter
    @Setter
    protected boolean doubleQuoteProperties = true;
    @Getter
    @Setter
    protected boolean doubleQuoteModels = true;
    @Getter
    @Setter
    protected String fileNamePrefix = null;
    @Getter
    @Setter
    protected String fileNameSuffix = null;
    @Getter
    @Setter
    protected String defaultTimeZone = "UTC";
    @Getter
    @Setter
    protected int validationErrorCode = -20001;

    private final Set<String> modelsToBeDeleted = new HashSet<>();
    private final Map<String, String> modelsToBeRenamed = new HashMap<>();

    /**
     * Constructor
     */
    public PlSqlServerCodegen() {
        super();

        generatorMetadata = GeneratorMetadata.newBuilder(generatorMetadata)
                .stability(Stability.BETA)
                .build();

        modifyFeatureSet(features -> features
                .includeDocumentationFeatures(DocumentationFeature.Readme)
                .wireFormatFeatures(EnumSet.noneOf(WireFormatFeature.class))
                .securityFeatures(EnumSet.noneOf(SecurityFeature.class))
                .excludeGlobalFeatures(
                        GlobalFeature.XMLStructureDefinitions,
                        GlobalFeature.Callbacks,
                        GlobalFeature.LinkObjects,
                        GlobalFeature.ParameterStyling)
                .excludeSchemaSupportFeatures(
                        SchemaSupportFeature.Polymorphism)
                .clientModificationFeatures(EnumSet.noneOf(ClientModificationFeature.class)));

        importMapping.clear();

        outputFolder = "generated-code" + File.separator + "plsql-server";
        modelPackage = "model";
        embeddedTemplateDir = templateDir = "plsql-server";

        // Only model files — API stubs deferred
        modelTemplateFiles.put("model.mustache", ".sql");
        apiTemplateFiles.clear();
        apiDocTemplateFiles.clear();
        modelDocTemplateFiles.clear();
        apiTestTemplateFiles.clear();
        modelTestTemplateFiles.clear();

        // Oracle PL/SQL type mappings
        typeMapping.clear();
        typeMapping.put("string", "VARCHAR2");
        typeMapping.put("integer", "INTEGER");
        typeMapping.put("int", "INTEGER");
        typeMapping.put("long", "NUMBER");
        typeMapping.put("number", "NUMBER");
        typeMapping.put("float", "FLOAT");
        typeMapping.put("double", "NUMBER");
        typeMapping.put("boolean", "BOOLEAN");
        typeMapping.put("date", "DATE");
        typeMapping.put("DateTime", "TIMESTAMP");
        typeMapping.put("binary", "BLOB");
        typeMapping.put("byte", "BLOB");
        typeMapping.put("ByteArray", "BLOB");
        typeMapping.put("file", "BLOB");
        typeMapping.put("UUID", "VARCHAR2");
        typeMapping.put("URI", "VARCHAR2");
        typeMapping.put("BigDecimal", "NUMBER");

        languageSpecificPrimitives = new HashSet<>(Arrays.asList(
                "VARCHAR2", "NUMBER", "INTEGER", "FLOAT", "DATE", "TIMESTAMP", "BOOLEAN", "BLOB", "CLOB", "JSON_OBJECT_T"));

        // Oracle 21c reserved words
        // https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/Oracle-SQL-Reserved-Words.html
        setReservedWordsLowerCase(Arrays.asList(
                "access", "add", "all", "alter", "and", "any", "as", "asc",
                "audit", "between", "by", "char", "check", "cluster", "column",
                "comment", "compress", "connect", "create", "current", "date",
                "decimal", "default", "delete", "desc", "distinct", "drop",
                "else", "exclusive", "exists", "file", "float", "for", "from",
                "grant", "group", "having", "identified", "immediate", "in",
                "increment", "index", "initial", "insert", "integer", "intersect",
                "into", "is", "level", "like", "lock", "long", "maxextents",
                "minus", "mlslabel", "mode", "modify", "noaudit", "nocompress",
                "not", "nowait", "null", "number", "of", "offline", "on",
                "online", "option", "or", "order", "pctfree", "prior",
                "privileges", "public", "raw", "rename", "resource", "revoke",
                "row", "rowid", "rownum", "rows", "select", "session", "set",
                "share", "size", "smallint", "start", "successful", "synonym",
                "sysdate", "table", "then", "to", "trigger", "uid", "union",
                "unique", "update", "user", "validate", "values", "varchar",
                "varchar2", "view", "whenever", "where", "with"
        ));

        cliOptions.clear();
        cliOptions.add(CliOption.newString(SCHEMA, "Oracle schema/owner prefix for all generated type names (e.g. 'MY_SCHEMA'). Leave empty to omit the prefix.", schema));
        cliOptions.add(CliOption.newBoolean(DOUBLE_QUOTE_PROPERTIES, "Indicator if attributes are generated with double quotes (case-sensitive). Defaults to true.", doubleQuoteProperties));
        cliOptions.add(CliOption.newBoolean(DOUBLE_QUOTE_MODELS, "Indicator if types are generated with double quotes (case-sensitive). Defaults to true.", doubleQuoteModels));
        cliOptions.add(CliOption.newString(FILE_NAME_PREFIX, "Optional prefix prepended to every generated model file name (e.g. 'api_' → 'api_pet.sql'). Defaults to no prefix.", fileNamePrefix));
        cliOptions.add(CliOption.newString(FILE_NAME_SUFFIX, "Optional suffix appended to every generated model file name (e.g. '_type' → 'pet_type.sql'). Defaults to no suffix.", fileNameSuffix));
        cliOptions.add(CliOption.newString(DEFAULT_TIME_ZONE, "Default time zone used in TIMESTAMP WITH TIME ZONE literals (e.g. 'UTC', 'Europe/Prague'). Defaults to 'UTC'.", defaultTimeZone));
        cliOptions.add(new CliOption(VALIDATION_ERROR_CODE, "Oracle error code raised by validation procedures (must be in range -20999..-20000). Defaults to -20001.").defaultValue(String.valueOf(validationErrorCode)));
    }

    /**
     * Exposes the internal type-mapping table for testing.
     */
    public Map<String, String> getTypeMapping() {
        return typeMapping;
    }

    // -------------------------------------------------------------------------
    // CodegenConfig identity
    // -------------------------------------------------------------------------

    @Override
    public CodegenType getTag() {
        return CodegenType.SERVER;
    }

    @Override
    public String getName() {
        return "plsql-server";
    }

    @Override
    public String getHelp() {
        return "Generates Oracle PL/SQL object types (TYPE … AS OBJECT) from the OpenAPI model definitions.";
    }

    @Override
    public GeneratorLanguage generatorLanguage() {
        return GeneratorLanguage.PLSQL;
    }

    // -------------------------------------------------------------------------
    // Options processing
    // -------------------------------------------------------------------------

    @Override
    public void processOpts() {
        super.processOpts();

        convertPropertyToStringAndWriteBack(SCHEMA, this::setSchema);
        convertPropertyToBooleanAndWriteBack(DOUBLE_QUOTE_PROPERTIES, this::setDoubleQuoteProperties);
        convertPropertyToBooleanAndWriteBack(DOUBLE_QUOTE_MODELS, this::setDoubleQuoteModels);
        convertPropertyToStringAndWriteBack(FILE_NAME_PREFIX, this::setFileNamePrefix);
        convertPropertyToStringAndWriteBack(FILE_NAME_SUFFIX, this::setFileNameSuffix);
        convertPropertyToStringAndWriteBack(DEFAULT_TIME_ZONE, this::setDefaultTimeZone);
        if (additionalProperties.containsKey(VALIDATION_ERROR_CODE)) {
            validationErrorCode = Integer.parseInt(additionalProperties.get(VALIDATION_ERROR_CODE).toString());
        }

        additionalProperties.put(SCHEMA, schema != null ? schema + "." : null);
        additionalProperties.put(DOUBLE_QUOTE_PROPERTIES, doubleQuoteProperties ? "\"" : null);
        additionalProperties.put(DOUBLE_QUOTE_MODELS, doubleQuoteModels ? "\"" : null);
        additionalProperties.put(DEFAULT_TIME_ZONE, defaultTimeZone);
        additionalProperties.put(VALIDATION_ERROR_CODE, validationErrorCode);

        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
    }


    // -------------------------------------------------------------------------
    // Escaping
    // -------------------------------------------------------------------------

    @Override
    public String escapeQuotationMark(String input) {
        return input.replace("'", "''");
    }

    @Override
    public String escapeUnsafeCharacters(String input) {
        return input.replace("*/", "*_/").replace("/*", "/_*");
    }

    @Override
    public String escapeReservedWord(String name) {
        return doubleQuoteProperties ? name : "_" + name;
    }


    // -------------------------------------------------------------------------
    // Identifier helpers
    // -------------------------------------------------------------------------

    /**
     * Converts a name to a valid Oracle identifier:
     * truncated to {@value #IDENTIFIER_MAX_LENGTH} chars.
     */
    public String toOracleIdentifier(String name) {
        if (name == null || name.length() <= IDENTIFIER_MAX_LENGTH) {
            return name;
        } else {
            LOGGER.warn("Identifier '{}' exceeds {} characters and will be truncated.", name, IDENTIFIER_MAX_LENGTH);
            return name.substring(0, IDENTIFIER_MAX_LENGTH);
        }
    }

    @Override
    public String toModelName(String name) {
        String identifier = toOracleIdentifier(name);

        if (isReservedWord(identifier)) {
            return escapeReservedWord(identifier);
        } else {
            return identifier;
        }
    }

    @Override
    public String toModelFilename(String name) {
        String prefix = fileNamePrefix != null ? fileNamePrefix : "";
        String suffix = fileNameSuffix != null ? fileNameSuffix : "";
        return prefix + toOracleIdentifier(name) + suffix;
    }

    @Override
    public String toVarName(String name) {
        return toModelName(name);
    }

//    @Override
//    public String toParamName(String name) {
//        return toVarName(name);
//    }

    // -------------------------------------------------------------------------
    // Type resolution helpers
    // -------------------------------------------------------------------------

    private String doubleQuoteIdentifier(String identifier) {
        return "\"" + identifier + "\"";
    }

    private String removeDoubleQuotes(String identifier) {
        return identifier.replaceAll("\"", "");
    }

    private String doubleQuotePropertyIfNecessary(String identifier) {
        return doubleQuoteProperties ? doubleQuoteIdentifier(removeDoubleQuotes(identifier)) : identifier;
    }

    private String doubleQuoteModelIfNecessary(String identifier) {
        return doubleQuoteModels ? doubleQuoteIdentifier(removeDoubleQuotes(identifier)) : identifier;
    }

    private boolean isPrimitiveDataType(String dataType) {
        return languageSpecificPrimitives.contains(dataType.toUpperCase(Locale.ROOT));
    }

    private String getArrayDataType(CodegenProperty items) {
        if (items.isModel) {
            // arrays of complex types reference TABLE OF type
            return items.getDataType() + "Array";
        } else if (items.isDateTime) {
            return "dateTimeArray";
        } else if (items.isDate) {
            return "dateArray";
        } else if (items.isNumber) {
            return "numberArray";
        } else if (items.isString && items.getMaxLength() != null) {
            return "string" + items.getMaxLength().toString() + "Array";
        } else if (items.isString && items.getMaxLength() == null) {
            return "string4000Array";
        } else {
            return "anydataArray";
        }
    }

    private String getArrayItemDataType(CodegenProperty items) {
        if (items.isModel) {
            // arrays of complex types reference TABLE OF type
            return items.getDataType();
        } else if (items.isDateTime) {
            return "TIMESTAMP";
        } else if (items.isDate) {
            return "DATE";
        } else if (items.isNumber) {
            return "NUMBER";
        } else if (items.isString && items.getMaxLength() != null) {
            return "VARCHAR2(" + items.getMaxLength().toString() + ")";
        } else if (items.isString && items.getMaxLength() == null) {
            return "VARCHAR2(4000)";
        } else {
            return "SYS.ANYDATA";
        }
    }

    /**
     * Property helpers
     */
    private String getPropertyDataType(CodegenProperty property) {
        if (property.getVendorExtensions().containsKey(VENDOR_EXTENSION_PLSQL_DATA_TYPE)) {
            String dataType = property.getVendorExtensions().get(VENDOR_EXTENSION_PLSQL_DATA_TYPE).toString();
            return isPrimitiveDataType(dataType) ? dataType.toUpperCase(Locale.ROOT) : dataType;
        } else if (modelsToBeRenamed.containsKey(property.getDataType())) {
            return modelsToBeRenamed.get(property.getDataType());
        } else if (property.isArray) {
            // arrays of complex types reference TABLE OF type
            return getArrayDataType(property.getItems());
        } else {
            return isPrimitiveDataType(property.getDataType()) ? property.getDataType().toUpperCase(Locale.ROOT) : property.getDataType();
        }
    }

    private String getPropertyDataTypeAndSize(CodegenProperty property) {
        if (property.isLong) {
            return "NUMBER(19,0)";
        } else if (property.isUuid) {
            return "VARCHAR2(36)";
        } else if (property.isEnum) {
            String enumWithMaxLenght = ((List<String>) property.getAllowableValues().get("values")).stream().max(Comparator.comparingInt(String::length)).orElse(null);
            if (enumWithMaxLenght != null && enumWithMaxLenght.length() <= 4000) {
                return "VARCHAR2(" + enumWithMaxLenght.length() + ")";
            } else {
                return "VARCHAR2(4000)";
            }
        } else if (property.isString && property.getMaxLength() != null && property.getMaxLength() <= 4000) {
            return "VARCHAR2(" + property.getMaxLength().toString() + ")";
        } else if (property.isString) {
            return "VARCHAR2(4000)";
        } else {
            return property.getDataType();
        }
    }

    /**
     * Model helpers
     */
    private String getModelDataType(CodegenModel model) {
        if (model.getVendorExtensions().containsKey(VENDOR_EXTENSION_PLSQL_DATA_TYPE)) {
            String dataType = model.getVendorExtensions().get(VENDOR_EXTENSION_PLSQL_DATA_TYPE).toString();
            return isPrimitiveDataType(dataType) ? dataType.toUpperCase(Locale.ROOT) : dataType;
        } else {
            return model.getDataType();
        }
    }


    // -------------------------------------------------------------------------
    // Model post-processing
    // -------------------------------------------------------------------------

    @Override
    public void preprocessOpenAPI(OpenAPI openAPI) {
        super.preprocessOpenAPI(openAPI);

        LOGGER.info("Preprocessing OpenAPI specification: applying global vendor extensions and resolving $ref references for all models.");
    }

    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        // set data type and format
        // data type from vendor extension x-plsql.dataType used
        property.setDataType(getPropertyDataType(property));
        property.getVendorExtensions().put(VENDOR_EXTENSION_DATA_TYPE_AND_SIZE, getPropertyDataTypeAndSize(property));

        // correct item name and data type in array
        if (property.isArray && property.getItems().isModel) {
            property.getItems().setDataType(getArrayItemDataType(property.getItems()));
        }

        // correct default values
        if (property.getDefaultValue().equals("null")) {
            property.setDefaultValue(null);
        }

        // set primitive types
        if (isPrimitiveDataType(property.getDataType())) {
            property.setIsPrimitiveType(true);
            property.setIsModel(false);
            property.setIsArray(false);
        } else  {
            property.setIsPrimitiveType(false);
        }

        // set isClob indicator
        if (property.getDataType().equals("CLOB")) {
            property.setIsString(true);
            property.getVendorExtensions().put(VENDOR_EXTENSION_IS_CLOB, true);
        }

        // set isJson indicator
        if (property.getDataType().equals("JSON_OBJECT_T")) {
            property.getVendorExtensions().put(VENDOR_EXTENSION_IS_JSON, true);
        }
    }

    @Override
    public ModelsMap postProcessModels(ModelsMap objs) {
        for (ModelMap mo : objs.getModels()) {
            CodegenModel model = mo.getModel();

            // set model data types
            model.setDataType(getModelDataType(model));

            if (isPrimitiveDataType(model.getDataType())) {
                modelsToBeDeleted.add(model.getName());
            }
        }

        return objs;
    }

    // -------------------------------------------------------------------------
    // Topological sort of models (dependency order for Oracle type creation)
    // -------------------------------------------------------------------------

    /**
     * Helper for topological sort — mirrors AbstractAdaCodegen.ModelDepend.
     */
 /*   private static class ModelDepend implements Comparable<ModelDepend> {
        final List<String> depend;
        final ModelMap model;
        final String name;

        ModelDepend(ModelMap model, List<String> depend, String name) {
            this.model = model;
            this.depend = depend;
            this.name = name;
        }

        @Override
        public int compareTo(ModelDepend other) {
            if (depend != null && depend.contains(other.name)) return 1;
            if (other.depend != null && other.depend.contains(name)) return -1;
            int mySize = depend == null ? 0 : depend.size();
            int otherSize = other.depend == null ? 0 : other.depend.size();
            if (mySize != otherSize) return mySize - otherSize;
            return name.compareTo(other.name);
        }
    }*/
    @Override
    public Map<String, ModelsMap> updateAllModels(Map<String, ModelsMap> objs) {
        objs = super.updateAllModels(objs);

        // delete unnecessary models
        modelsToBeDeleted.forEach(objs::remove);

        // arrays has to be generated as separate models
        Map<String, ModelsMap> modelsToBeAdded = new HashMap<>();
        objs.forEach((key, value) -> {
            for (ModelMap mo : value.getModels()) {
                CodegenModel model = mo.getModel();

                model.getVars().stream().filter(property -> property.isArray).forEach(property -> {
                    CodegenModel arrayModel = new CodegenModel();
                    arrayModel.setName(getArrayDataType(property.getItems()));
                    arrayModel.setDataType(getArrayItemDataType(property.getItems()));
                    arrayModel.setIsArray(true);

                    ModelMap modelMap = new ModelMap();
                    modelMap.setModel(arrayModel);
                    ModelsMap modelsMap = new ModelsMap();
                    modelsMap.put("models", List.of(modelMap));
                    modelsMap.put(CodegenConstants.MUSTACHE_PARENT_CONTEXT, this);

                    modelsToBeAdded.put(arrayModel.getName(), modelsMap);
                    LOGGER.info("Property '{}' in model '{}' is an array and will be generated as separate model.", property.getName(), model.getName());
                });
            }
        });

        // oneOfs with discriminator are generated via polymorphism
        objs.forEach((key, value) -> {
            for (ModelMap mo : value.getModels()) {
                CodegenModel model = mo.getModel();

                if (model.getComposedSchemas() != null && model.getComposedSchemas().getOneOf() != null && !model.getComposedSchemas().getOneOf().isEmpty() && model.getDiscriminator() != null) {
                    model.getComposedSchemas().getOneOf().forEach(oneOfSchema -> {
                        CodegenModel childModel = new CodegenModel();
                        childModel.setName(model.getName() + "_" + oneOfSchema.getDataType());
                        childModel.setDataType("object");
                        childModel.setParentModel(model);
                        childModel.setParent(model.getName());

                        ModelMap modelMap = new ModelMap();
                        modelMap.setModel(childModel);
                        ModelsMap modelsMap = new ModelsMap();
                        modelsMap.setModels(Collections.singletonList(modelMap));

                        modelsToBeAdded.put(childModel.getName(), modelsMap);
                        LOGGER.info("Model '{}' is a oneOf with discriminator and will be generated via polymorphism.", childModel.getName());
                    });

                }
            }
        });
        objs.putAll(modelsToBeAdded);

        return objs;
    }

    @Override
    public Map<String, ModelsMap> postProcessAllModels(Map<String, ModelsMap> objs) {
        objs = super.postProcessAllModels(objs);

        return objs;
    }
}