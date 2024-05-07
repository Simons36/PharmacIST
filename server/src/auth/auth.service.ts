import { HttpException, HttpStatus, Injectable, Logger } from '@nestjs/common';
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

  private readonly logger = new Logger(AuthService.name);

  async login(
    findLoginDto: AuthLoginDto,
  ): Promise<{ username: string; access_token: string }> {
    this.logger.log(
      'Received login request with email ' + findLoginDto.email + '.',
    );

    try {
      const user = await this.userService.findUser(findLoginDto.email);

      const isValidPassword = await argon.verify(
        user.password,
        findLoginDto.password,
      );

      if (!isValidPassword) {
        this.logger.error(
          'Login request with email ' +
            findLoginDto.email +
            ' rejected due to invalid password',
        );
        throw new HttpException('Wrong Password.', HttpStatus.UNAUTHORIZED);
      }

      const username = user.username;

      const payload = {
        sub: username,
      };

      // Sign the JWT token with custom options
      const accessToken = await this.jwtService.signAsync(payload, {
        expiresIn: '15m',
        secret: fs.readFileSync(
          this.configService.accessTokenSecretPath,
          'utf8',
        ),
      });

      // Return response with 200 status code
      this.logger.log(`Login of user '${username}' successful`);
      return {
        username,
        access_token: accessToken,
      };
    } catch (error) {
      if (error instanceof HttpException) {
        // Handle specific HttpException
        throw error; // Rethrow the HttpException to maintain HTTP status code
      } else {
        // Handle generic error
        this.logger.error(
          `Login failed due to unknown error: ${error.message}`,
        );
        throw new HttpException('Login failed', HttpStatus.UNAUTHORIZED);
      }
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
      throw new HttpException('Registration failed', HttpStatus.BAD_REQUEST);
    }
  }
}
