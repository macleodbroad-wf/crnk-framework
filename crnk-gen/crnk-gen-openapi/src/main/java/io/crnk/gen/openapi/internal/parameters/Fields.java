package io.crnk.gen.openapi.internal.parameters;

import io.crnk.meta.model.MetaElement;
import io.crnk.meta.model.resource.MetaResource;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;

import static java.util.stream.Collectors.joining;

public class Fields extends AbstractParameterGenerator {

  public Fields(MetaResource metaResource) {
    super(metaResource);
  }

  public Parameter parameter() {
    return new Parameter()
        .name("fields")
        .description(metaResource.getResourceType() + " fields to include (csv)")
        .in("query")
        .schema(new StringSchema()
            ._default(
                metaResource
                    .getAttributes()
                    .stream()
                    .map(MetaElement::getName)
                    .collect(joining(","))));
  }
}
