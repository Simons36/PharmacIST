import { HttpException, HttpStatus, Injectable } from "@nestjs/common";
import { UserService } from "src/user/user.service";
import * as argon from 'argon2';
import { AuthLoginDto, AuthRegisterDto } from "./dto";

@Injectable()
export class AuthService {

    constructor(private userService : UserService) {}

    async login(findLoginDto : AuthLoginDto){


        try{

            const user = this.userService.findUser(findLoginDto.email);

            const isValidPassword = await argon.verify((await user).password, findLoginDto.password)

            if(!isValidPassword){
                throw new HttpException(
                    `Wrong Password.`,
                    HttpStatus.UNAUTHORIZED,
                );
            }

            return {msg : "Login successful!"};

        }catch(error){

            return error;

        }


    }

    logout(){

    }

    async register(registerDto : AuthRegisterDto){
        try {
            // Hash password
            const passwordHash = await argon.hash(registerDto.password);

            

            // Call userService.createUser with the createUserDto
            return await this.userService.createUser({
                email : registerDto.email,
                username : registerDto.username,
                password : passwordHash
            });

        } catch (error) {
            return error;
        }
    }
}