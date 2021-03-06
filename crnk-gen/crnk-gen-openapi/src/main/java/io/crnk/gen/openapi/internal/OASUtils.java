package io.crnk.gen.openapi.internal;

import io.crnk.gen.openapi.internal.schemas.ResourceReference;
import io.crnk.meta.model.MetaArrayType;
import io.crnk.meta.model.MetaAttribute;
import io.crnk.meta.model.MetaCollectionType;
import io.crnk.meta.model.MetaElement;
import io.crnk.meta.model.MetaEnumType;
import io.crnk.meta.model.MetaLiteral;
import io.crnk.meta.model.MetaMapType;
import io.crnk.meta.model.MetaSetType;
import io.crnk.meta.model.MetaType;
import io.crnk.meta.model.resource.MetaJsonObject;
import io.crnk.meta.model.resource.MetaResource;
import io.crnk.meta.model.resource.MetaResourceField;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ByteArraySchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public class OASUtils {

  public static Schema transformMetaResourceField(MetaType metaType) {
    if (metaType instanceof MetaResource) {
      return new ResourceReference((MetaResource) metaType).$ref();
    } else if (metaType instanceof MetaCollectionType) {
      return new ArraySchema()
          .items(transformMetaResourceField(metaType.getElementType()))
          .uniqueItems(metaType instanceof MetaSetType);
    } else if (metaType instanceof MetaArrayType) {
      return new ArraySchema()
          .items(transformMetaResourceField(metaType.getElementType()))
          .uniqueItems(false);
    } else if (metaType instanceof MetaJsonObject) {
      ObjectSchema objectSchema = new ObjectSchema();
      for (MetaElement child : metaType.getChildren()) {
        if (child instanceof MetaAttribute) {
          MetaAttribute metaAttribute = (MetaAttribute) child;
          objectSchema.addProperties(child.getName(), transformMetaResourceField(metaAttribute.getType()));
        }
      }
      return objectSchema;
    } else if (metaType.getName().equals("boolean")) {
      return new BooleanSchema();
    } else if (metaType.getName().equals("byte")) {
      return new ByteArraySchema();
    } else if (metaType.getName().equals("date")) {
      return new DateSchema();
    } else if (metaType.getName().equals("offsetDateTime")) {
      return new DateTimeSchema();
    } else if (metaType.getName().equals("localDate")) {
      return new DateSchema();
    } else if (metaType.getName().equals("localDateTime")) {
      return new DateTimeSchema();
    } else if (metaType.getName().equals("double")) {
      return new NumberSchema().format("double");
    } else if (metaType.getName().equals("float")) {
      return new NumberSchema().format("float");
    } else if (metaType.getName().equals("integer")) {
      return new IntegerSchema().format("int32");
    } else if (metaType.getName().equals("json")) {
      return new ObjectSchema();
    } else if (metaType.getName().equals("json.object")) {
      return new ObjectSchema();
    } else if (metaType.getName().equals("json.array")) {
      return new ArraySchema().items(new Schema());
    } else if (metaType.getName().equals("long")) {
      return new IntegerSchema().format("int64");
    } else if (metaType.getName().equals("object")) {
      return new ObjectSchema();
    } else if (metaType.getName().equals("short")) {
      return new IntegerSchema().minimum(BigDecimal.valueOf(Short.MIN_VALUE)).maximum(BigDecimal.valueOf(Short.MAX_VALUE));
    } else if (metaType.getName().equals("string")) {
      return new StringSchema();
    } else if (metaType.getName().equals("uuid")) {
      return new UUIDSchema();
    } else if (metaType instanceof MetaMapType) {

      return transformMetaResourceField(metaType.getElementType());
    } else if (metaType instanceof MetaEnumType) {
      Schema enumSchema = new StringSchema();
      for (MetaElement child : metaType.getChildren()) {
        if (child instanceof MetaLiteral) {
          enumSchema.addEnumItemObject(child.getName());
        } else {
          return new ObjectSchema().additionalProperties(true);
        }
      }
      return enumSchema;
    } else {
      return new Schema().type(metaType.getElementType().getName());
    }
  }

  @SafeVarargs
  static Map<String, ApiResponse> mergeApiResponses(Map<String, ApiResponse>... maps) {
    Map<String, ApiResponse> merged = new TreeMap<>();
    for (Map<String, ApiResponse> map : maps) {
      merged.putAll(map);
    }
    return merged;
  }


  static Operation mergeOperations(Operation newOperation, Operation existingOperation) {
    if (existingOperation == null) {
      return newOperation;
    }

    if (existingOperation.getOperationId() != null) {
      newOperation.setOperationId(existingOperation.getOperationId());
    }

    if (existingOperation.getSummary() != null) {
      newOperation.setSummary(existingOperation.getSummary());
    }

    if (existingOperation.getDescription() != null) {
      newOperation.setDescription(existingOperation.getDescription());
    }

    if (existingOperation.getExtensions() != null) {
      newOperation.setExtensions(existingOperation.getExtensions());
    }

    return newOperation;
  }

  public static boolean oneToMany(MetaResourceField metaResourceField) {
    return metaResourceField.getType().isCollection() || metaResourceField.getType().isMap();
  }
}
