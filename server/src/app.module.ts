import { Module } from '@nestjs/common';
import { UserModule } from './user/user.module';
import { AuthModule } from './auth/auth.module';
import { AuthController } from './auth/auth.controller';
import { AuthService } from './auth/auth.service';
import { AppConfigModule } from './config/app-config.module';
import { DatabaseModule } from './database/database.module';
import { AppConfigService } from './config/app-config.service';
import { UserService } from './user/user.service';
import { User, UserSchema } from './user/schemas/user.schema';
import { Mongoose } from 'mongoose';
import { MongooseModule } from '@nestjs/mongoose';
import { JwtModule } from '@nestjs/jwt';
import { PharmacyModule } from './pharmacy/pharmacy.module';

@Module({
  imports: [UserModule, AuthModule, AppConfigModule, DatabaseModule, JwtModule, MongooseModule.forFeature([{ name: User.name, schema: UserSchema }]), PharmacyModule],
  controllers: [AuthController],
  providers: [AuthService, AppConfigService, UserService],
})
export class AppModule {}
