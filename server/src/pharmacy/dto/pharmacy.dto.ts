import { IsNotEmpty } from "class-validator";

export class PharmacyDto {

    @IsNotEmpty()
    readonly name: string;
    
    readonly address: string;
    
    @IsNotEmpty()
    readonly latitude: number;
    @IsNotEmpty()
    readonly longitude: number;
    
    readonly photo : Uint8Array;
    readonly photoExtension : string;
}