import { Module } from '@nestjs/common';
import { PharmacyController } from './pharmacy.controller';
import { PharmacyService } from './pharmacy.service';
import { AppConfigModule } from 'src/config/app-config.module';
import { DatabaseModule } from 'src/database/database.module';
import { MongooseModule } from '@nestjs/mongoose';
import { Pharmacy, PharmacySchema } from './schemas/pharmacy.schema';
import { PharmacyVersion, PharmacyVersionSchema } from './schemas/pharmacy-version.schema';
import { UserModule } from 'src/user/user.module';

@Module({
  imports: [
    AppConfigModule,
    DatabaseModule,
    MongooseModule.forFeature([
      { name: Pharmacy.name, schema: PharmacySchema },
    ]),
    MongooseModule.forFeature([
      { name: PharmacyVersion.name, schema: PharmacyVersionSchema },
    ]),
    UserModule
  ],
  controllers: [PharmacyController],
  providers: [PharmacyService],
  exports: [PharmacyService]
})
export class PharmacyModule {}
