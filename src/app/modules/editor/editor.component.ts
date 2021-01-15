import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {JsonEditorComponent, JsonEditorOptions} from 'ang-jsoneditor';
import {StoreRoomService} from '../../service/storeroom/store-room.service';
import {ActivatedRoute, Router} from '@angular/router';
import {IError} from 'ang-jsoneditor/jsoneditor/jsoneditoroptions';
import {MatSnackBar} from '@angular/material/snack-bar';
import {META_SCHEMA, INIT_SCHEMA} from '../../dto/mock_json'

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
    public isMetaSchemaViewerDisable = true;
    private jsonSchema: any;
    private metaSchema: any;

    constructor(private storeroomClient: StoreRoomService, private route: ActivatedRoute, private snackBar: MatSnackBar,
                private router: Router) {
        this.editorOptions = new JsonEditorOptions();
        this.editorOptions2 = new JsonEditorOptions();
        this.editorOptions.modes = ['code', 'text', 'tree', 'view', 'form'];
        this.editorOptions2.mode = 'view';
        this.editorOptions.onChange = () => this.jsonSchema = this.editor.get();
        // this.editorOptions.onValidate = () => this.validateSchema();
        this.editorOptions.navigationBar = true;
        this.editorOptions.schema = {};
        this.data = INIT_SCHEMA;
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
