import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { UserService } from 'src/user/user.service';
import * as argon from 'argon2';
import * as fs from 'fs';
import { AuthLoginDto, AuthRegisterDto } from './dto';
import { JwtService } from '@nestjs/jwt';
import { AppConfigService } from 'src/config/app-config.service';

@Injectable()
export class AuthService {
  constructor(
    private userService: UserService,
    private jwtService: JwtService,
    private configService: AppConfigService,
  ) {}

  async login(
    findLoginDto: AuthLoginDto,
  ): Promise<{ username: string; access_token: string }> {
    try {
      const user = await this.userService.findUser(findLoginDto.email);

      const isValidPassword = await argon.verify(
        (await user).password,
        findLoginDto.password,
      );

      if (!isValidPassword) {
        throw new HttpException(`Wrong Password.`, HttpStatus.UNAUTHORIZED);
      }

      const username = (await user).username;

      const payload = {
        sub: username,
      };

      return {
        username: username,
        access_token: await this.jwtService.signAsync(payload, {
          expiresIn: '15m',
          secret: fs.readFileSync(this.configService.accessTokenSecretPath),
        }),
      };
    } catch (error) {
      return error;
    }
  }

  logout() {}

  async register(registerDto: AuthRegisterDto) {
    try {
      // Hash password
      const passwordHash = await argon.hash(registerDto.password);

      // Call userService.createUser with the createUserDto
      return await this.userService.createUser({
        email: registerDto.email,
        username: registerDto.username,
        password: passwordHash,
      });
    } catch (error) {
      return error;
    }
  }
}
