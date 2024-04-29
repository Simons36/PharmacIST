import { IsEmail, IsNotEmpty, IsString } from "class-validator";
import { CreateUserDto } from "src/user/dto/create-user.dto";

export class AuthRegisterDto{
    
    @IsString()
    @IsEmail()
    readonly email : string;

    @IsString()
    @IsNotEmpty()
    readonly username : string;

    @IsString()
    @IsNotEmpty()
    readonly password : string;
}

export function ConvertRegisterDtoToCreateUserDto(authRegisterDto : AuthRegisterDto, passwordHash :  string){
    const createUserDto : CreateUserDto = {
        username : authRegisterDto.username,
        email : authRegisterDto.email,
        password : passwordHash
    };

    return createUserDto;
}