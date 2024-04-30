import { Body, Controller, Post, Req } from "@nestjs/common";
import { AuthService } from "./auth.service";
import { AuthLoginDto, AuthRegisterDto } from "./dto";

@Controller('auth')

export class AuthController{

    constructor(private authService: AuthService){}

    @Post('login')
    login(@Body() loginDto : AuthLoginDto){

        

        return this.authService.login(loginDto);
    }

    @Post('logout')
    logout(){
        return this.authService.logout();
    }

    @Post('register')
    register(@Body() registerDto : AuthRegisterDto){
        
        return this.authService.register(registerDto);
        
    }
}