import { IsNotEmpty, IsString, IsOptional, IsNumber } from "class-validator";

export class StockDto{
    @IsNotEmpty()
    @IsString()
    readonly pharmacyName: string;

    @IsNotEmpty()
    @IsString()
    readonly medicineName : string;

    @IsNotEmpty()
    readonly quantity: number;

}
