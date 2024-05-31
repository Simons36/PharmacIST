// database/database.module.ts

import { Module, Logger } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { AppConfigModule } from '../config/app-config.module';
import { AppConfigService } from '../config/app-config.service';
import { UserModule } from 'src/user/user.module';

@Module({
  imports: [
    MongooseModule.forRootAsync({
      imports: [AppConfigModule],
      useFactory: async (configService: AppConfigService) => {
        const uri = `mongodb://${configService.mongoUsername}${configService.mongoPassword}${configService.mongoHost}:${configService.mongoPort}/`;
        const options = {
        };

        // Log MongoDB URI and options for debugging
        Logger.log(`Connecting to MongoDB at ${uri}`, 'DatabaseModule');
        Logger.log('MongoDB options:', JSON.stringify(options, null, 2), 'DatabaseModule');

        return {
          uri,
          ...options,
        };
      },
      inject: [AppConfigService],
    }),
  ],
})
export class DatabaseModule {}
