import { IsNotEmpty, IsString, IsOptional, IsNumber } from "class-validator";

export class MedicineQuantityDto{
    readonly name : string;

    readonly quantity : number;

}
