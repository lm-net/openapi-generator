---
title: Documentation for the plsql-server Generator
---

## METADATA

| Property | Value | Notes |
| -------- | ----- | ----- |
| generator name | plsql-server | pass this to the generate command after -g |
| generator stability | BETA | |
| generator type | SERVER | |
| generator language | PL/SQL | |
| generator default templating engine | mustache | |
| helpTxt | Generates Oracle PL/SQL object types (TYPE … AS OBJECT) from the OpenAPI model definitions. | |

## CONFIG OPTIONS
These options may be applied as additional-properties (cli) or configOptions (plugins).
Refer to [configuration docs](https://openapi-generator.tech/docs/configuration) for more details.

| Option | Description | Values | Default |
| ------ | ----------- | ------ | ------- |
| schemaName | Oracle schema/owner prefix prepended to all generated type names (e.g. `MY_SCHEMA`). Leave empty to omit the prefix. | | |

## IMPORT MAPPING

| Type/Alias | Imports |
| ---------- | ------- |

## INSTANTIATION TYPES

| Type/Alias | Instantiated By |
| ---------- | --------------- |

## LANGUAGE PRIMITIVES

<ul class="column-ul">
<li>BLOB</li>
<li>CLOB</li>
<li>DATE</li>
<li>NUMBER</li>
<li>NUMBER(1)</li>
<li>NUMBER(10)</li>
<li>NUMBER(19)</li>
<li>TIMESTAMP</li>
<li>VARCHAR2(36)</li>
<li>VARCHAR2(4000)</li>
</ul>

## RESERVED WORDS

<ul class="column-ul">
<li>access</li>
<li>add</li>
<li>all</li>
<li>alter</li>
<li>and</li>
<li>any</li>
<li>as</li>
<li>asc</li>
<li>audit</li>
<li>between</li>
<li>by</li>
<li>char</li>
<li>check</li>
<li>cluster</li>
<li>column</li>
<li>comment</li>
<li>compress</li>
<li>connect</li>
<li>create</li>
<li>current</li>
<li>date</li>
<li>decimal</li>
<li>default</li>
<li>delete</li>
<li>desc</li>
<li>distinct</li>
<li>drop</li>
<li>else</li>
<li>exclusive</li>
<li>exists</li>
<li>file</li>
<li>float</li>
<li>for</li>
<li>from</li>
<li>grant</li>
<li>group</li>
<li>having</li>
<li>identified</li>
<li>immediate</li>
<li>in</li>
<li>increment</li>
<li>index</li>
<li>initial</li>
<li>insert</li>
<li>integer</li>
<li>intersect</li>
<li>into</li>
<li>is</li>
<li>level</li>
<li>like</li>
<li>lock</li>
<li>long</li>
<li>maxextents</li>
<li>minus</li>
<li>mlslabel</li>
<li>mode</li>
<li>modify</li>
<li>noaudit</li>
<li>nocompress</li>
<li>not</li>
<li>nowait</li>
<li>null</li>
<li>number</li>
<li>of</li>
<li>offline</li>
<li>on</li>
<li>online</li>
<li>option</li>
<li>or</li>
<li>order</li>
<li>pctfree</li>
<li>prior</li>
<li>privileges</li>
<li>public</li>
<li>raw</li>
<li>rename</li>
<li>resource</li>
<li>revoke</li>
<li>row</li>
<li>rowid</li>
<li>rownum</li>
<li>rows</li>
<li>select</li>
<li>session</li>
<li>set</li>
<li>share</li>
<li>size</li>
<li>smallint</li>
<li>start</li>
<li>successful</li>
<li>synonym</li>
<li>sysdate</li>
<li>table</li>
<li>then</li>
<li>to</li>
<li>trigger</li>
<li>uid</li>
<li>union</li>
<li>unique</li>
<li>update</li>
<li>user</li>
<li>validate</li>
<li>values</li>
<li>varchar</li>
<li>varchar2</li>
<li>view</li>
<li>whenever</li>
<li>where</li>
<li>with</li>
</ul>

## FEATURE SET

### Client Modification Feature
| Name | Supported | Defined By |
| ---- | --------- | ---------- |

### Data Type Feature
| Name | Supported | Defined By |
| ---- | --------- | ---------- |
| Array | ✓ | OAS2,OAS3 |
| Enum | ✓ | OAS2,OAS3 |
| Map | ✓ | OAS2,OAS3 |
| Object | ✓ | OAS2,OAS3 |
| Primitives | ✓ | OAS2,OAS3 |

### Documentation Feature
| Name | Supported | Defined By |
| ---- | --------- | ---------- |
| Readme | ✓ | CODEGEN |

### Global Feature
| Name | Supported | Defined By |
| ---- | --------- | ---------- |
| Callbacks | ✗ | OAS3 |
| LinkObjects | ✗ | OAS3 |
| ParameterStyling | ✗ | OAS3 |
| XMLStructureDefinitions | ✗ | OAS2,OAS3 |

### Schema Support Feature
| Name | Supported | Defined By |
| ---- | --------- | ---------- |
| Polymorphism | ✗ | OAS2,OAS3 |

