import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { AppConfigService } from './app-config.service';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true, // Make ConfigModule accessible everywhere
    }),
  ],
  providers: [AppConfigService],
  exports: [AppConfigService],
})

export class AppConfigModule {}
