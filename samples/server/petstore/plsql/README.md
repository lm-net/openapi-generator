# PL/SQL Server — Petstore Sample

Generated from [petstore.yaml](../../../../modules/openapi-generator/src/test/resources/3_0/petstore.yaml)
using the `plsql-server` generator.

## Generated files

| File | Oracle object type | Collection type |
|---|---|---|
| `model/category.sql` | `CATEGORY_T` | `CATEGORY_TAB_T` |
| `model/tag.sql` | `TAG_T` | `TAG_TAB_T` |
| `model/pet.sql` | `PET_T` | `PET_TAB_T` |
| `model/api_response.sql` | `API_RESPONSE_T` | `API_RESPONSE_TAB_T` |
| `model/order.sql` | `ORDER_T` | `ORDER_TAB_T` |
| `model/user.sql` | `USER_T` | `USER_TAB_T` |

## Deployment order

Run the scripts in dependency order so referenced types exist before they are used:

```sql
@model/category.sql
@model/tag.sql
@model/pet.sql        -- references CATEGORY_T and TAG_TAB_T
@model/api_response.sql
@model/order.sql
@model/user.sql
```

## Re-generate

```bash
openapi-generator generate -c bin/configs/plsql-server-petstore.yaml
```

