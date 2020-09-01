import { Injectable } from '@angular/core';
import * as jsonexport from 'jsonexport/dist';

@Injectable({
  providedIn: 'root'
})
export class JsonConverterService {

  constructor() { }

  public jsonToCSV(jsonObject: object): any{
    jsonexport(jsonObject, (err, csv) => {
      if (err) {
        return console.error(err);
      }
      console.log(csv);
      return csv;
    });
  }
}
