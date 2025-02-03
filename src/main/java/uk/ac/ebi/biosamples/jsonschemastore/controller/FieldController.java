package uk.ac.ebi.biosamples.jsonschemastore.controller;

import lombok.RequiredArgsConstructor;
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
import uk.ac.ebi.biosamples.jsonschemastore.service.FieldService;

import java.util.List;

@RepositoryRestController
@RequiredArgsConstructor
public class FieldController {
  private final FieldRepository fieldRepository;
  private final FieldService fieldService;
  private final PagedResourcesAssembler<Field> pagedResourcesAssembler;

  @GetMapping("/fields/search/findByExample")
  @ResponseBody
  public PagedModel<EntityModel<Field>>
    findByExample(@ModelAttribute Field field,
                  Pageable pageable) {
    Page<Field> fieldPage = fieldService.findByExample(field, pageable);
    return pagedResourcesAssembler.toModel(fieldPage);
  }

  @GetMapping("/fields/search/findAttributeValues")
  @ResponseBody
  public List<FieldRepository.AttributeResult> findDistinctAndSortByAttributeName(String attributeName){
    return fieldRepository.findAttributeValues(attributeName);
  }
}
