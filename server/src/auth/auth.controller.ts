import { Body, Controller, Post, Req } from "@nestjs/common";
import { AuthService } from "./auth.service";
import { Request } from "express";
import { AuthLoginDto } from "./dto/auth-login.dto";

@Controller('auth')

export class AuthController{

    constructor(private authService: AuthService){}

    @Post('login')
    login(@Body('email') email : string, @Body('password') password : string){

        

        return this.authService.login(dto);
    }

    @Post('logout')
    logout(){
        return this.authService.logout();
    }

    @Post('register')
    register(){
        return this.authService.register();
    }
}