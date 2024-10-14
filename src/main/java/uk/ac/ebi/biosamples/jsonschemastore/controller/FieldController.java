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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;

@RepositoryRestController
@RequiredArgsConstructor
public class FieldController {
  private final FieldRepository fieldRepository;
  private final PagedResourcesAssembler<Field> pagedResourcesAssembler;

  @GetMapping("/fields/search/findByExample")
  @ResponseBody
  public PagedModel<EntityModel<Field>> findByExample(@ModelAttribute Field field, Pageable pageable) {
    ExampleMatcher matcher = ExampleMatcher.matching()
        .withIgnoreNullValues()
        .withIgnorePaths("usedBySchemas")
        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
    Example<Field> example = Example.of(field, matcher);

    Page<Field> fieldPage = fieldRepository.findAll(example, pageable);
    return pagedResourcesAssembler.toModel(fieldPage);
  }
}
