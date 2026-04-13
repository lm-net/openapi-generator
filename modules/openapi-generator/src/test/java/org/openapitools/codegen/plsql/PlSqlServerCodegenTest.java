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

package org.openapitools.codegen.plsql;

import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.languages.PlSqlServerCodegen;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class PlSqlServerCodegenTest {

    private PlSqlServerCodegen codegen;

    @BeforeMethod
    public void setUp() {
        codegen = new PlSqlServerCodegen();
        codegen.processOpts();
    }

    // -----------------------------------------------------------------------
    // Identifier helpers
    // -----------------------------------------------------------------------

    @Test
    public void testToOracleIdentifier_basicCamelCase() {
        Assert.assertEquals(codegen.toOracleIdentifier("petName"), "PET_NAME");
    }

    @Test
    public void testToOracleIdentifier_alreadySnakeCase() {
        Assert.assertEquals(codegen.toOracleIdentifier("pet_name"), "PET_NAME");
    }

    @Test
    public void testToOracleIdentifier_truncatesLongIdentifier() {
        String longName = "a".repeat(130);
        String result = codegen.toOracleIdentifier(longName);
        Assert.assertTrue(result.length() <= PlSqlServerCodegen.IDENTIFIER_MAX_LENGTH,
                "Identifier should be truncated to " + PlSqlServerCodegen.IDENTIFIER_MAX_LENGTH + " chars");
    }

    @Test
    public void testToModelName_uppercase() {
        Assert.assertEquals(codegen.toModelName("ApiResponse"), "API_RESPONSE");
    }

    @Test
    public void testToVarName_reservedWordPrefixed() {
        // 'date' is in the reserved-word list
        String result = codegen.toVarName("date");
        Assert.assertTrue(result.startsWith("p_"), "Reserved word var should be prefixed with p_");
    }

    // -----------------------------------------------------------------------
    // Object / collection type names
    // -----------------------------------------------------------------------

    @Test
    public void testToObjectTypeName_noSchema() {
        codegen.setSchema("");
        Assert.assertEquals(codegen.toObjectTypeName("Pet"), "PET_T");
    }

    @Test
    public void testToObjectTypeName_withSchema() {
        codegen.setSchema("my_schema");
        Assert.assertEquals(codegen.toObjectTypeName("Pet"), "MY_SCHEMA.PET_T");
    }

    @Test
    public void testToCollectionTypeName_noSchema() {
        codegen.setSchema("");
        Assert.assertEquals(codegen.toCollectionTypeName("Pet"), "PET_TAB_T");
    }

    @Test
    public void testToCollectionTypeName_withSchema() {
        codegen.setSchema("my_schema");
        Assert.assertEquals(codegen.toCollectionTypeName("Pet"), "MY_SCHEMA.PET_TAB_T");
    }

    // -----------------------------------------------------------------------
    // Type mapping assertions
    // -----------------------------------------------------------------------

    @Test
    public void testTypeMapping_string() {
        Assert.assertEquals(codegen.getTypeMapping().get("string"), "VARCHAR2");
    }

    @Test
    public void testTypeMapping_integer() {
        Assert.assertEquals(codegen.getTypeMapping().get("integer"), "NUMBER");
    }

    @Test
    public void testTypeMapping_long() {
        Assert.assertEquals(codegen.getTypeMapping().get("long"), "NUMBER");
    }

    @Test
    public void testTypeMapping_boolean() {
        Assert.assertEquals(codegen.getTypeMapping().get("boolean"), "VARCHAR2(1)");
    }

    @Test
    public void testTypeMapping_date() {
        Assert.assertEquals(codegen.getTypeMapping().get("date"), "DATE");
    }

    @Test
    public void testTypeMapping_datetime() {
        Assert.assertEquals(codegen.getTypeMapping().get("DateTime"), "TIMESTAMP");
    }

    @Test
    public void testTypeMapping_binary() {
        Assert.assertEquals(codegen.getTypeMapping().get("binary"), "BLOB");
    }

    @Test
    public void testTypeMapping_uuid() {
        Assert.assertEquals(codegen.getTypeMapping().get("UUID"), "VARCHAR2(36)");
    }

    // -----------------------------------------------------------------------
    // postProcessModelProperty — vendor extension population
    // -----------------------------------------------------------------------

    @Test
    public void testPostProcessModelProperty_boolean_setsYNFlag() {
        CodegenModel model = new CodegenModel();
        model.setName("TestModel");

        CodegenProperty prop = new CodegenProperty();
        prop.setBaseName("active");
        prop.isBoolean = true;
        prop.dataType  = "VARCHAR2(1)";

        codegen.postProcessModelProperty(model, prop);

        Map<String, Object> plsql = (Map<String, Object>) prop.getVendorExtensions().get(PlSqlServerCodegen.PLSQL_VENDOR_EXTENSION);
        Assert.assertNotNull(plsql);
        Assert.assertEquals(plsql.get("attrType"), "VARCHAR2(1)");
        Assert.assertEquals(plsql.get("isBoolean"), true);
    }

    @Test
    public void testPostProcessModelProperty_string_defaultVarchar4000() {
        CodegenModel model = new CodegenModel();
        model.setName("TestModel");

        CodegenProperty prop = new CodegenProperty();
        prop.setBaseName("description");
        prop.isString     = true;
        prop.dataType     = "VARCHAR2(4000)";
        prop.openApiType  = "string";

        codegen.postProcessModelProperty(model, prop);

        Map<String, Object> plsql = (Map<String, Object>) prop.getVendorExtensions().get(PlSqlServerCodegen.PLSQL_VENDOR_EXTENSION);
        Assert.assertNotNull(plsql);
        Assert.assertEquals(plsql.get("attrType"), "VARCHAR2(4000)");
    }

    @Test
    public void testPostProcessModelProperty_string_respectsMaxLength() {
        CodegenModel model = new CodegenModel();
        model.setName("TestModel");

        CodegenProperty prop = new CodegenProperty();
        prop.setBaseName("code");
        prop.isString    = true;
        prop.dataType    = "VARCHAR2(4000)";
        prop.openApiType = "string";
        prop.setMaxLength(50);

        codegen.postProcessModelProperty(model, prop);

        Map<String, Object> plsql = (Map<String, Object>) prop.getVendorExtensions().get(PlSqlServerCodegen.PLSQL_VENDOR_EXTENSION);
        Assert.assertNotNull(plsql);
        Assert.assertEquals(plsql.get("attrType"), "VARCHAR2(50)");
    }

    @Test
    public void testPostProcessModelProperty_enum_setsVarchar2WithComment() {
        CodegenModel model = new CodegenModel();
        model.setName("TestModel");

        CodegenProperty prop = new CodegenProperty();
        prop.setBaseName("status");
        prop.isEnum      = true;
        prop.dataType    = "VARCHAR2(4000)";
        prop.openApiType = "string";
        Map<String, Object> allowable = new HashMap<>();
        allowable.put("values", Arrays.asList("available", "pending", "sold"));
        prop.setAllowableValues(allowable);

        codegen.postProcessModelProperty(model, prop);

        Map<String, Object> plsql = (Map<String, Object>) prop.getVendorExtensions().get(PlSqlServerCodegen.PLSQL_VENDOR_EXTENSION);
        Assert.assertNotNull(plsql);
        Assert.assertEquals(plsql.get("attrType"), "VARCHAR2(4000)");
        Assert.assertEquals(plsql.get("isEnum"), true);
        Assert.assertNotNull(plsql.get("enumComment"));
        Assert.assertTrue(plsql.get("enumComment").toString().contains("available"));
        Assert.assertTrue(plsql.get("enumComment").toString().contains("sold"));
    }
}



