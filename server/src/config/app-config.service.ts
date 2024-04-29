import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class AppConfigService {
  constructor(private readonly configService: ConfigService) {}

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
}
