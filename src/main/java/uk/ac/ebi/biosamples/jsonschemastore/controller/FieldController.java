package uk.ac.ebi.biosamples.jsonschemastore.controller;

import org.springframework.hateoas.server.ExposesResourceFor;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController()
@ExposesResourceFor(Field.class)
public class FieldController {
}
