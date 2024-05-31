import { IsNotEmpty, IsString, IsOptional } from "class-validator";

export class MedicineDto{
    @IsNotEmpty()
    @IsString()
    readonly name: string;

    @IsOptional()
    @IsString()
    readonly photoPath?: string;
}
