import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {JsonEditorComponent, JsonEditorOptions} from 'ang-jsoneditor';
import {StoreRoomService} from '../../service/storeroom/store-room.service';
import {ActivatedRoute, Router} from '@angular/router';
import {IError} from 'ang-jsoneditor/jsoneditor/jsoneditoroptions';
import {MatSnackBar} from '@angular/material/snack-bar';
import {JsonConverterService} from '../../service/json-converter/json-converter.service';

@Component({
    selector: 'app-editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.scss']
})
export class EditorComponent implements OnInit, OnDestroy {
    public editorOptions: JsonEditorOptions;
    public editorOptions2: JsonEditorOptions;
    public data: any;
    @ViewChild(JsonEditorComponent, {static: false}) editor: JsonEditorComponent;
    @ViewChild(JsonEditorComponent, {static: false}) editorMetaSchema: JsonEditorComponent;
    public isUpdate: boolean;
    public isMetaSchemaViewerDisable = false;
    private jsonSchema: any;
    private metaSchema: any;

    constructor(private storeroomClient: StoreRoomService, private route: ActivatedRoute, private snackBar: MatSnackBar,
                private router: Router, private jsonConverterService: JsonConverterService) {
        this.editorOptions = new JsonEditorOptions();
        this.editorOptions2 = new JsonEditorOptions();
        this.editorOptions.modes = ['code', 'text', 'tree', 'view'];
        this.editorOptions2.mode = 'view';
        this.editorOptions.onChange = () => this.jsonSchema = this.editor.get();
        // this.editorOptions.onValidate = () => this.validateSchema();
        this.editorOptions.navigationBar = true;
        // tslint:disable-next-line:max-line-length
        this.editorOptions.schema = { $schema: 'http://json-schema.org/draft-07/schema#', $ref: '#/definitions/Welcome', definitions: { Welcome: { type: 'object', additionalProperties: false, properties: { $schema: { type: 'string', format: 'uri', 'qt-uri-protocols': [ 'http' ] }, $id: { type: 'string', format: 'uri', 'qt-uri-protocols': [ 'https' ], 'qt-uri-extensions': [ '.0' ] }, title: { type: 'string' }, description: { type: 'string' }, type: { type: 'string' }, meta: { $ref: '#/definitions/Meta' }, properties: { $ref: '#/definitions/Properties' }, required: { type: 'array', items: { type: 'string' } }, oneof: { type: 'array', items: { $ref: '#/definitions/Oneof' } }, additionalProperties: { type: 'boolean' }, examples: { type: 'array', items: { $ref: '#/definitions/WelcomeExample' } } }, required: [ '$id', '$schema', 'additionalProperties', 'description', 'examples', 'meta', 'oneof', 'properties', 'required', 'title', 'type' ], title: 'Welcome' }, WelcomeExample: { type: 'object', additionalProperties: false, properties: { term: { $ref: '#/definitions/ClassOfOnset' }, classOfOnset: { $ref: '#/definitions/ClassOfOnset' } }, required: [ 'classOfOnset', 'term' ], title: 'WelcomeExample' }, Meta: { type: 'object', additionalProperties: false, properties: { contributors: { type: 'array', items: { $ref: '#/definitions/Contributor' } }, provenance: { type: 'array', items: { $ref: '#/definitions/Contributor' } }, used_by: { type: 'array', items: { $ref: '#/definitions/Contributor' } }, sb_status: { type: 'string' } }, required: [ 'contributors', 'provenance', 'sb_status', 'used_by' ], title: 'Meta' }, Contributor: { type: 'object', additionalProperties: false, properties: { description: { type: 'string' }, id: { type: 'string', 'qt-uri-protocols': [ 'https' ], 'qt-uri-extensions': [ '.rst' ] } }, required: [ 'description' ], title: 'Contributor' }, Oneof: { type: 'object', additionalProperties: false, properties: { properties: { type: 'array', items: { type: 'string' } } }, required: [ 'properties' ], title: 'Oneof' }, Properties: { type: 'object', additionalProperties: false, properties: { term: { $ref: '#/definitions/Term' }, ageOfOnset: { $ref: '#/definitions/AgeOfOnset' }, ageRangeOfOnset: { $ref: '#/definitions/AgeRangeOfOnset' }, classOfOnset: { $ref: '#/definitions/PropertiesClassOfOnset' }, diseaseStage: { $ref: '#/definitions/DiseaseStage' }, tnmFinding: { $ref: '#/definitions/DiseaseStage' } }, required: [ 'ageOfOnset', 'ageRangeOfOnset', 'classOfOnset', 'diseaseStage', 'term', 'tnmFinding' ], title: 'Properties' }, AgeOfOnset: { type: 'object', additionalProperties: false, properties: { allof: { type: 'array', items: { $ref: '#/definitions/AgeOfOnsetAllof' } } }, required: [ 'allof' ], title: 'AgeOfOnset' }, AgeOfOnsetAllof: { type: 'object', additionalProperties: false, properties: { $ref: { type: 'string', format: 'uri', 'qt-uri-protocols': [ 'https' ], 'qt-uri-extensions': [ '.json' ] }, description: { type: 'string' }, examples: { type: 'array', items: { $ref: '#/definitions/Start' } } }, required: [], title: 'AgeOfOnsetAllof' }, Start: { type: 'object', additionalProperties: false, properties: { age: { type: 'string' } }, required: [ 'age' ], title: 'Start' }, AgeRangeOfOnset: { type: 'object', additionalProperties: false, properties: { description: { type: 'string' }, $ref: { type: 'string', format: 'uri', 'qt-uri-protocols': [ 'https' ], 'qt-uri-extensions': [ '.json' ] }, examples: { type: 'array', items: { $ref: '#/definitions/AgeRangeOfOnsetExample' } } }, required: [ '$ref', 'description', 'examples' ], title: 'AgeRangeOfOnset' }, AgeRangeOfOnsetExample: { type: 'object', additionalProperties: false, properties: { start: { $ref: '#/definitions/Start' } }, required: [ 'start' ], title: 'AgeRangeOfOnsetExample' }, PropertiesClassOfOnset: { type: 'object', additionalProperties: false, properties: { description: { type: 'string' }, $ref: { type: 'string', format: 'uri', 'qt-uri-protocols': [ 'https' ], 'qt-uri-extensions': [ '.json' ] }, examples: { type: 'array', items: { $ref: '#/definitions/ClassOfOnset' } } }, required: [ '$ref', 'description', 'examples' ], title: 'PropertiesClassOfOnset' }, DiseaseStage: { type: 'object', additionalProperties: false, properties: { description: { type: 'string' }, type: { type: 'string' }, items: { $ref: '#/definitions/Items' }, examples: { type: 'array', items: { type: 'array', items: { $ref: '#/definitions/ClassOfOnset' } } } }, required: [ 'description', 'examples', 'items', 'type' ], title: 'DiseaseStage' }, ClassOfOnset: { type: 'object', additionalProperties: false, properties: { id: { type: 'string' }, label: { type: 'string' } }, required: [ 'id', 'label' ], title: 'ClassOfOnset' }, Items: { type: 'object', additionalProperties: false, properties: { $ref: { type: 'string', format: 'uri', 'qt-uri-protocols': [ 'https' ], 'qt-uri-extensions': [ '.json' ] } }, required: [ '$ref' ], title: 'Items' }, Term: { type: 'object', additionalProperties: false, properties: { allof: { type: 'array', items: { $ref: '#/definitions/TermAllof' } } }, required: [ 'allof' ], title: 'Term' }, TermAllof: { type: 'object', additionalProperties: false, properties: { $ref: { type: 'string', format: 'uri', 'qt-uri-protocols': [ 'https' ], 'qt-uri-extensions': [ '.json' ] }, description: { type: 'string' }, examples: { type: 'array', items: { $ref: '#/definitions/AllofExample' } } }, required: [], title: 'TermAllof' }, AllofExample: { type: 'object', additionalProperties: false, properties: { id: { type: 'string' } }, required: [ 'id' ], title: 'AllofExample' } } };
        // tslint:disable-next-line:max-line-length
        this.data = { $schema: 'http://json-schema.org/draft-07/schema#', $id: 'https://schemablocks.org/schemas/xxxxx', title: 'title', description: 'description', type: 'object', meta: { contributors: [ { description: 'GA4GH Data Working Group' }, { description: 'Jules Jacobsen', id: 'orcid:0000-0002-3265-15918' } ], provenance: [ { description: 'description', id: 'https://github.com/phenopackets/phenopacket-schema/xxxxxx' } ], used_by: [ { description: 'Phenopackets', id: 'https://github.com/phenopackets/phenopacket-schema/xxxx' } ], sb_status: 'implemented' }, properties: { term: { allof: [ { $ref: 'https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/OntologyClass.json' }, { description: 'The identifier of this disease\ne.g. MONDO:0007043, OMIM:101600, Orphanet:710, DOID:14705 (note these are all equivalent)\n', examples: [ { id: 'MONDO:0007043' } ] } ] } }, required: [ 'term' ], additionalProperties: false, examples: [ { term: { id: 'MONDO:0007043', label: 'Pfeiffer syndrome' } } ] };
    }

    ngOnInit(): void {
        this.router.routeReuseStrategy.shouldReuseRoute = () =>  false;
        if (this.route.snapshot.queryParamMap && this.route.snapshot.queryParamMap.has('$id')) {
            this.isUpdate = true;
            this.getSchemaBlock();
        }
        this.getMetaSchema();
    }

    ngOnDestroy(): void {
        this.data = null;
    }

    getMetaSchema(): void {
        this.storeroomClient.getMetaSchema().subscribe((response) => {
            this.metaSchema = response;
        });
    }

    getSchemaBlock(): void {
        this.route
            .queryParams
            .subscribe(params => {
                this.editorOptions.mode = 'view';
                this.storeroomClient.getSchemaBlockById(params.$id)
                    .subscribe((response) => {
                        this.data = response;
                        this.jsonSchema = this.data;
                    });
            });
    }

    createJsonSchema(): void {
        this.storeroomClient.createJsonSchema(this.editor.get())
            .subscribe((response) => {
                console.log(response);
                this.openSnackBar('Created Successfully!', {duration: 5000, panelClass: 'snackbar-success'});
            }, (error) => {
                this.openSnackBar(error, {panelClass: 'snackbar-error'}, 'Close');
            });
    }

    updateJsonSchema(): void {
        this.storeroomClient.updateSchemaBlock(this.editor.get())
            .subscribe((response) => {
                console.log(response);
                this.openSnackBar('Updated Successfully!', {duration: 5000, panelClass: 'snackbar-success'});
            }, (error) => {
                this.openSnackBar(error, {panelClass: 'snackbar-error'}, 'Close');
            });
    }

    deleteJsonSchema(): void {
        this.jsonSchema = this.editor.get();
        this.storeroomClient.deleteSchemaBlock(this.jsonSchema.$id)
            .subscribe((response) => {
                this.openSnackBar('Deleted Successfully!', {duration: 5000, panelClass: 'snackbar-success'});
            }, (error) => {
                this.openSnackBar(error, {panelClass: 'snackbar-error'}, 'Close');
            });
    }

    public onClickMetaSchemaViewer(): void {
        this.isMetaSchemaViewerDisable = !this.isMetaSchemaViewerDisable;
    }

    public downloadAs(format: string): void {
        if (format === 'JSON') {
            this.jsonConverterService.jsonToJSONAndDownload(this.editor.get());
        } else if (format === 'XML') {
            this.jsonConverterService.jsonToXMLAndDownload(this.editor.get());
        }
    }

    private validateSchema(): IError[] {
        console.log('start validating scehma!');
        console.log(this.jsonSchema);
        let error: IError[] = [];
        if (this.jsonSchema) {
            this.storeroomClient.validateSchema(this.jsonSchema)
                .subscribe((response) => {
                    console.log(response);
                    error = response.validationErrors;
                    return error;
                });
        }
        return error;
    }

    private openSnackBar(message: string, config: object = {duration: 5000}, action: string = ''): void {
        const snackBarRef = this.snackBar.open(message, action, config);
        snackBarRef.onAction().subscribe(() => {
            snackBarRef.dismiss();
        });
    }
}
