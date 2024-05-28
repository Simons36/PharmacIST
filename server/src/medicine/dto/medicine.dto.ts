import { IsNotEmpty, IsString } from "class-validator";

export class MedicineDto{
    @IsNotEmpty()
    @IsString()
    readonly name : string;

    readonly photoPath : string;
}