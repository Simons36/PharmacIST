import { Module } from '@nestjs/common';
import { UserModule } from 'src/user/user.module';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { JwtModule } from '@nestjs/jwt';

@Module({

    imports : [UserModule, JwtModule.register({})],
    controllers : [AuthController],
    providers : [AuthService]

})


export class AuthModule {}
