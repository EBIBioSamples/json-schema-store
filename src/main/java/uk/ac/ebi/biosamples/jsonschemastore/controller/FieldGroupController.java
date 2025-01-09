package uk.ac.ebi.biosamples.jsonschemastore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.QueryParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.biosamples.jsonschemastore.model.FieldGroup;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldGroupRepository;

import java.util.List;

@RepositoryRestController
@RequiredArgsConstructor
public class FieldGroupController {
  private final FieldGroupRepository fieldGroupRepository;
  private final PagedResourcesAssembler<FieldGroup> pagedResourcesAssembler;

  @GetMapping("/fieldGroups/search/findByExample")
  @ResponseBody
  public PagedModel<EntityModel<FieldGroup>> findByExample(@ModelAttribute FieldGroup fieldGroup, Pageable pageable) {
    ExampleMatcher matcher = ExampleMatcher.matching()
        .withIgnoreNullValues()
        .withIgnorePaths("usedBySchemas")
        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
    Example<FieldGroup> example = Example.of(fieldGroup, matcher);

    Page<FieldGroup> fieldPage = fieldGroupRepository.findAll(example, pageable);
    return pagedResourcesAssembler.toModel(fieldPage);
  }

}
