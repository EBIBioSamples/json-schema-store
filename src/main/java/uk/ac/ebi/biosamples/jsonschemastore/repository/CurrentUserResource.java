package uk.ac.ebi.biosamples.jsonschemastore.repository;

import uk.ac.ebi.biosamples.jsonschemastore.model.User;

public interface CurrentUserResource {

    User findCurrentUser();
}
