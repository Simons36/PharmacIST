import { Body, Controller, Get, HttpCode, Param, Post, Req, UseGuards } from '@nestjs/common';
import { UserService } from './user.service';
import { AuthGuard } from '@nestjs/passport';
import { Request } from 'express';

interface AuthenticatedRequest extends Request {
  user: { username: string };
}

@Controller('user')
export class UserController {
  constructor(private userService: UserService) {}

  @UseGuards(AuthGuard('jwt'))
  @Post('favorite/add/:pharmacyName')
  @HttpCode(200)
  async addFavoritePharmacy(
    @Param('pharmacyName') pharmacyName: string,
    @Req() req: AuthenticatedRequest,
  ) {
    return await this.userService.addFavoritePharmacy(
      pharmacyName,
      req.user.username,
    );
  }

  @UseGuards(AuthGuard('jwt'))
  @Post('favorite/remove/:pharmacyName')
  @HttpCode(200)
  async removeFavoritePharmacy(
    @Param('pharmacyName') pharmacyName: string,
    @Req() req: AuthenticatedRequest,
  ) {
    return await this.userService.removeFavoritePharmacy(
      pharmacyName,
      req.user.username,
    );
  }

  @UseGuards(AuthGuard('jwt'))
  @Get('favorite')
  @HttpCode(200)
  async getFavoritePharmacies(@Req() req: AuthenticatedRequest) {
    return await this.userService.getFavoritePharmacies(req.user.username);
  }
}
