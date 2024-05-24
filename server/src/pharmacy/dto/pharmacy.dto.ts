import { IsDefined, IsNotEmpty, ValidateIf } from "class-validator";

export class PharmacyDto {

    @IsNotEmpty()
    readonly name: string;
    
    @ValidateIf(o => (!o.latitude || !o.longitude) || o.address)
    readonly address: string;
    
    @ValidateIf(o => !o.address || (o.latitude && o.longitude))
    readonly latitude: number;
    @ValidateIf(o => !o.address || (o.latitude && o.longitude))
    readonly longitude: number;

    readonly photoPath: string;

}