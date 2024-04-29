import { Injectable } from "@nestjs/common";
import { UserService } from "src/user/user.service";
import { AuthLoginDto, AuthRegisterDto, ConvertRegisterDtoToCreateUserDto } from "./dto/index";
import * as argon from 'argon2';

@Injectable()
export class AuthService {

    constructor(private userService : UserService) {}

    login(authLoginDto : AuthLoginDto){

        //first we need to hash the password
        const passwordHash = argon.hash(authLoginDto.password);

        //return this.userService.;
    }

    logout(){

    }

    async register(registerDto : AuthRegisterDto){
        try {
            // Hash password
            const passwordHash = await argon.hash(registerDto.password);

            // Create user DTO with hashed password
            const createUserDto = ConvertRegisterDtoToCreateUserDto(registerDto, passwordHash);

            // Call userService.createUser with the createUserDto
            return await this.userService.createUser(createUserDto);
        } catch (error) {
            return error;
        }
    }
}