import { Body, Controller, Post, Req } from "@nestjs/common";
import { AuthService } from "./auth.service";
import { AuthLoginDto, AuthRegisterDto } from "./dto";

@Controller('auth')

export class AuthController{

    constructor(private authService: AuthService){}

    @Post('login')
    async login(@Body() loginDto : AuthLoginDto){

        const debug = await this.authService.login(loginDto);
        console.log(debug);

        return debug;
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