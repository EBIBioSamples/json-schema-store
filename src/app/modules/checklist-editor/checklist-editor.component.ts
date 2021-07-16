import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {ChecklistFieldComponent} from "../checklist-field/checklist-field.component";
import {stringify} from "@angular/compiler/src/util";

@Component({
  selector: 'app-checklist-editor',
  templateUrl: './checklist-editor.component.html',
  styleUrls: ['./checklist-editor.component.scss']
})
export class ChecklistEditorComponent implements OnInit, AfterViewInit {
  public values;
  // @ViewChild(ChecklistFieldComponent) checklistFieldComponent: ChecklistFieldComponent;

  checklistForm = this.fb.group({
    schemaId: ['', Validators.required],
    checklistFields: this.fb.array([
        this.fb.group({
          fieldName:[''],
          fieldType:[''],
          stringPattern:[''],
          stringFormat:[''],
          numberMax:[''],
          numberMin:['']
        })
    ])
  });

  constructor(private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.values = [];
  }

  ngAfterViewInit() {
    // this.checklistFields.push(this.checklistFieldComponent.checklistFieldGroup);
    // this.checklistFieldComponent.checklistFieldGroup.setParent(this.checklistFields);
  }

  get checklistFields() {
    return this.checklistForm.get('checklistFields') as FormArray;
  }

  getFormGroupFromIndex(i : number) {
    return this.checklistFields.get(String(i)) as FormGroup;
  }

  addField() {
    this.checklistFields.push(this.fb.group({
      fieldName:[''],
      fieldType:[''],
      stringPattern:[''],
      stringFormat:[''],
      numberMax:[''],
      numberMin:['']
    }));
  }

  updateChecklist() {
    this.checklistForm.patchValue({
      schemaId: 'www.ebi/schema/simple/1.0.0',
      checklistFields: [
        {
          fieldName: 'organism',
          fieldType: 'string',
          stringPattern: 'NCBI:*',
          stringFormat: '',
          numberMax: 0,
          numberMin: 0
        }
      ]
    })
  }

  onSubmitChecklist() {
      this.checklistForm.get('checklistFields').value.forEach((formGroup: FormGroup, i: number) => {
        console.log(formGroup['fieldName']);

        const checklistField = {}
        checklistField['fieldName'] = formGroup['fieldName'];
        checklistField['fieldType'] = formGroup['fieldType'];
        checklistField['stringPattern'] = formGroup['stringPattern'];
        checklistField['stringFormat'] = formGroup['stringFormat'];
        checklistField['numberMax'] = formGroup['numberMax'];
        checklistField['numberMin'] = formGroup['numberMin'];

        console.log(checklistField);
      });


  }

  onSubmit() {
    // TODO: Use EventEmitter with form value
  }

  onChecklistTypeChange(type) {

  }

}
