import { IsNotEmpty, IsString, IsOptional, IsNumber } from "class-validator";

export class AddMedicineDto{
    @IsNotEmpty()
    @IsString()
    readonly name: string;

    @IsNotEmpty()
    @IsString()
    readonly pharmacyName : string;

    @IsNotEmpty()
    readonly quantity: number;

    @IsNotEmpty()
    @IsString()
    readonly purpose: string;

    @IsOptional()
    @IsString()
    readonly photoPath?: string;
}
