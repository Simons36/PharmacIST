import { Module } from '@nestjs/common';
import { UserModule } from './user/user.module';
import { AuthModule } from './auth/auth.module';
import { AuthController } from './auth/auth.controller';
import { AuthService } from './auth/auth.service';
import { AppConfigModule } from './config/app-config.module';
import { DatabaseModule } from './database/database.module';
import { AppConfigService } from './config/app-config.service';

@Module({
  imports: [UserModule, AuthModule, AppConfigModule, DatabaseModule],
  controllers: [AuthController],
  providers: [AuthService, AppConfigService],
})
export class AppModule {}
