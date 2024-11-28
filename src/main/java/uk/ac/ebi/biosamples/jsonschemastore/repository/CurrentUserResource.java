package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.biosamples.jsonschemastore.model.User;

public interface CurrentUserResource {

    User findCurrentUser();
}
