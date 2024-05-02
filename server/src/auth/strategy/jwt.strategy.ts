import { PassportStrategy } from '@nestjs/passport';
import { Strategy, ExtractJwt } from 'passport-jwt';
import { AppConfigService } from 'src/config/app-config.service';
import * as fs from 'fs';

export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(private configService : AppConfigService){
    super({
        jwtFromRequest : ExtractJwt.fromAuthHeaderAsBearerToken(),
        secretOrKey : fs.readFileSync(configService.accessTokenSecretPath)
    })
  };
}
