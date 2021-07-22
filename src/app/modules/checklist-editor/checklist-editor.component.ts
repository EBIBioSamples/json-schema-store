import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {ChecklistFieldComponent} from "../checklist-field/checklist-field.component";
import {stringify} from "@angular/compiler/src/util";
import {StoreRoomService} from "../../service/storeroom/store-room.service";

@Component({
  selector: 'app-checklist-editor',
  templateUrl: './checklist-editor.component.html',
  styleUrls: ['./checklist-editor.component.scss']
})
export class ChecklistEditorComponent implements OnInit {
  public values;

  checklistForm = this.fb.group({
    schemaName: ['', Validators.required],
    schemaVersion: ['', Validators.required],
    schemaDomain: ['', Validators.required],
    schemaId: [{value: '', disabled: true}, Validators.required],
    schemaAccession: [{value: '', disabled: true}, Validators.required],
    schemaTitle: ['', Validators.required],
    schemaDescription: ['', Validators.required],
    metaSchema: ['', Validators.required],
    checklistFields: this.fb.array([
        this.fb.group(ChecklistFieldComponent.getChecklistFieldGroupTemplate())
    ])
  });

  constructor(private fb: FormBuilder, private storeroomClient: StoreRoomService) {
  }

  ngOnInit(): void {
    this.values = [];
  }

  get checklistFields() {
    return this.checklistForm.get('checklistFields') as FormArray;
  }

  getFormGroupFromIndex(i : number) {
    return this.checklistFields.get(String(i)) as FormGroup;
  }

  addField() {
    this.checklistFields.push(this.fb.group(ChecklistFieldComponent.getChecklistFieldGroupTemplate()));
  }

  onChangeSchemaIdFields() {
    const schemaDomain = this.checklistForm.get("schemaDomain").value;
    const schemaName = this.checklistForm.get("schemaName").value;
    const schemaVersion = this.checklistForm.get("schemaVersion").value;
    const schemaId =  `${schemaDomain}/${schemaName}/${schemaVersion}`
    this.checklistForm.patchValue({
      schemaId: schemaId
    });
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
    const checklistFields = [];
      this.checklistForm.get('checklistFields').value.forEach((formGroup: FormGroup, i: number) => {
        const checklistField = {}
        checklistField['name'] = formGroup['fieldName'];
        checklistField['description'] = formGroup['fieldDescription'];
        checklistField['type'] = formGroup['fieldType'];
        checklistField['format'] = formGroup['stringFormat'];
        checklistField['pattern'] = formGroup['stringPattern'];
        checklistField['minLength'] = formGroup['stringMinLength'];
        checklistField['maxLength'] = formGroup['stringMaxLength'];
        checklistField['min'] = formGroup['numberMin'];
        checklistField['max'] = formGroup['numberMax'];
        checklistField['multiples'] = formGroup['numberMultiples'];
        checklistField['enums'] = formGroup['enumList'];
        checklistField['constant'] = formGroup['const'];
        checklistField['required'] = formGroup['fieldRequired'];
        checklistField['recommended'] = formGroup['fieldRecommended'];

        checklistFields.push(checklistField);
      });

      const checklist = {
        schemaId: this.checklistForm.get("schemaId").value,
        accession: this.checklistForm.get("schemaAccession").value,
        title: this.checklistForm.get("schemaTitle").value,
        description: this.checklistForm.get("schemaDescription").value,
        metaSchema: this.checklistForm.get("metaSchema").value,
        fields: checklistFields
      };

      console.log(checklist);

      this.createChecklist(checklist);
  }

  createChecklist(checklist) {
    this.storeroomClient.createChecklist(checklist)
        .subscribe((response) => {
          console.log("Checklist created successfully")
        }, (error) => {
          console.log("Failed to create checklist")
        });
  }

  onSubmit() {
    // TODO: Use EventEmitter with form value
  }

}
