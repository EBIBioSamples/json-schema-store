import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {JsonEditorComponent, JsonEditorOptions} from 'ang-jsoneditor';
import {StoreRoomService} from '../../service/storeroom/store-room.service';
import {ActivatedRoute, Router} from '@angular/router';
import {IError} from 'ang-jsoneditor/jsoneditor/jsoneditoroptions';
import {MatSnackBar} from '@angular/material/snack-bar';
import {META_SCHEMA, INIT_SCHEMA} from '../../dto/mock_json'
import {HttpErrorResponse} from "@angular/common/http";

@Component({
    selector: 'app-editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.scss']
})
export class EditorComponent implements OnInit, OnDestroy {
    public jsonEditorOptions: JsonEditorOptions;
    public metaEditorOptions: JsonEditorOptions;
    @ViewChild(JsonEditorComponent, {static: false}) editor: JsonEditorComponent;
    @ViewChild(JsonEditorComponent, {static: false}) editorMetaSchema: JsonEditorComponent;
    public isUpdate: boolean;
    public isMetaSchemaViewerDisable = true;

    public editorJsonSchema: object;
    public jsonSchema: object;
    public metaSchema: object;
    public metaSchemas: Map<string, object>;
    public metaSchemaSelectList: [string, string][];

    constructor(private storeroomClient: StoreRoomService, private route: ActivatedRoute, private snackBar: MatSnackBar,
                private router: Router) {
        this.jsonEditorOptions = new JsonEditorOptions();
        this.jsonEditorOptions.modes = ['code', 'text', 'tree', 'view', 'form'];
        this.jsonEditorOptions.onChange = () => this.jsonSchema["schema"] = this.editor.get();
        // this.editorOptions.onValidate = () => this.validateSchema();
        this.jsonEditorOptions.navigationBar = true;
        this.jsonEditorOptions.schema = {};

        this.metaEditorOptions = new JsonEditorOptions();
        this.metaEditorOptions.mode = 'view';

        this.metaSchemas = new Map<string, object>();
        this.metaSchemaSelectList = [];
    }

    ngOnInit(): void {
        this.router.routeReuseStrategy.shouldReuseRoute = () =>  false;
        if (this.route.snapshot.queryParamMap && this.route.snapshot.queryParamMap.has('$id')) {
            this.isUpdate = true;
            this.getJsonSchema();
        } else {
            this.editorJsonSchema = INIT_SCHEMA;
            this.jsonSchema = {};
            this.jsonSchema["schema"] = this.editorJsonSchema;
        }
        this.getAllMetaSchemas();
    }

    ngOnDestroy(): void {
        this.jsonSchema = null;
    }

    getAllMetaSchemas(): void {
        this.storeroomClient.getMetaSchema().subscribe((response) => {
            for (let schema of response['_embedded']['schemas']) {
                this.metaSchemas.set(schema['id'], schema)
                this.metaSchemaSelectList.push([schema['id'], schema['name']])
            }
            this.metaSchema = response['_embedded']['schemas'][0]['schema'];
        });
    }

    getJsonSchema(): void {
        this.route
            .queryParams
            .subscribe(params => {
                this.jsonEditorOptions.mode = 'view';
                this.storeroomClient.getSchema(params.$id)
                    .subscribe((response) => {
                        this.jsonSchema = response;
                        this.editorJsonSchema = this.jsonSchema['schema'];
                    });
            });
    }

    createJsonSchema(): void {
        const schema = this.editor.get();
        const request = {
            metaSchema: this.metaSchema["$id"],
            schema: schema
        };
        this.storeroomClient.createSchema(request)
            .subscribe((response) => {
                this.openSnackBar('Created Successfully!', {duration: 5000, panelClass: 'snackbar-success'});
            }, (error) => {
                this.openSnackBar(error, {panelClass: 'snackbar-error'}, 'Close');
            });
    }

    updateJsonSchema(): void {
        const schema = this.editor.get();
        const request = {
            accession: this.jsonSchema["accession"],
            metaSchema: this.metaSchema["$id"],
            schema: schema
        };
        this.storeroomClient.updateSchema(request)
            .subscribe((response) => {
                this.jsonSchema = response;
                this.editorJsonSchema = this.jsonSchema['schema'];
                this.openSnackBar('Updated Successfully!', {duration: 5000, panelClass: 'snackbar-success'});
            }, (error) => {
                this.openSnackBar(error, {panelClass: 'snackbar-error'}, 'Close');
            });
    }

    deleteJsonSchema(): void {
        const schema = this.editor.get();
        this.storeroomClient.deleteSchema(schema["$id"])
            .subscribe((response) => {
                this.openSnackBar('Deleted Successfully!', {duration: 5000, panelClass: 'snackbar-success'});
            }, (error) => {
                this.openSnackBar(error, {panelClass: 'snackbar-error'}, 'Close');
            });
    }

    public onClickMetaSchemaViewer(): void {
        this.isMetaSchemaViewerDisable = !this.isMetaSchemaViewerDisable;
    }

    private validateSchema(): IError[] {
        let error: IError[] = [];
        if (this.jsonSchema['schema']) {
            this.storeroomClient.validateSchema(this.jsonSchema['schema'])
                .subscribe((response) => {
                    error = response.validationErrors;
                    return error;
                });
        }
        return error;
    }

    onMetaSchemaSelect(value: string) {
        this.metaSchema = this.metaSchemas.get(value)['schema'];
    }

    private openSnackBar(message: string, config: object = {duration: 5000}, action: string = ''): void {
        const snackBarRef = this.snackBar.open(message, action, config);
        snackBarRef.onAction().subscribe(() => {
            snackBarRef.dismiss();
        });
    }
}
