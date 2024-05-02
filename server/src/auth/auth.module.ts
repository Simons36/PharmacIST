import { Module } from '@nestjs/common';
import { UserModule } from 'src/user/user.module';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { JwtModule } from '@nestjs/jwt';
import { AppConfigModule } from 'src/config/app-config.module';
import { JwtStrategy } from './strategy';

@Module({

    imports : [UserModule, AppConfigModule, JwtModule.register({})],
    controllers : [AuthController],
    providers : [AuthService, JwtStrategy]

})


export class AuthModule {}
