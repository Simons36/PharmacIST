import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class AppConfigService {
  private projectRootPath: string;

  constructor(private readonly configService: ConfigService) {
    this.projectRootPath = require('path').dirname(require.main.filename);
    //remove last folder
    const appDirSplit = this.projectRootPath.split('/');
    appDirSplit.pop();
    this.projectRootPath = appDirSplit.join('/');
  }

  get mongoHost(): string {
    return this.configService.get<string>('MONGO_HOST');
  }

  get mongoPort(): string {
    return this.configService.get<string>('MONGO_PORT');
  }

  get mongoDatabase(): string {
    return this.configService.get<string>('MONGO_DATABASE');
  }

  get mongoUsername(): string {
    return this.configService.get<string>('MONGO_USERNAME');
  }

  get mongoPassword(): string {
    return this.configService.get<string>('MONGO_PASSWORD');
  }

  get accessTokenSecretPath(): string {
    return this.configService.get<string>('ACCESS_TOKEN_SECRET_PATH');
  }

  get googleMapsApiKey(): string {
    return this.configService.get<string>('GOOGLE_MAPS_API_KEY');
  }

  get photosPath(): string {
    return this.projectRootPath + this.configService.get<string>('PHOTOS_FOLDER') + '/';
  }
}
