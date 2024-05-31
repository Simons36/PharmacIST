import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { User, UserDocument } from './schemas/user.schema';
import { CreateUserDto } from './dto/create-user.dto';
import { UserDto } from './dto/user.dto';

@Injectable()
export class UserService {
  constructor(
    @InjectModel(User.name) private readonly userModel: Model<UserDocument>,
  ) {}

  // called on register
  async createUser(createUserDto: CreateUserDto): Promise<void> {
    try {
      const newUser = new this.userModel(createUserDto);
      await newUser.save();
      
    } catch (error) {
      if (error.code === 11000 || error.code === 11001) {
        // Duplicate key error (MongoDB error code for duplicate key)
        if (error.keyPattern.email) {
          throw new HttpException(
            {
              message: `'${createUserDto.email}' is already in use.`,
              field: 'email',
            },
            HttpStatus.CONFLICT,
          );
        } else if (error.keyPattern.username) {
          throw new HttpException(
            {
              message: `'${createUserDto.username}' is already in use.`,
              field: 'username',
            },
            HttpStatus.CONFLICT,
          );
        } else {
          throw new Error('Duplicate key error');
        }
      }
      throw error; // Rethrow other errors
    }
  }

  async findUser(emailOrUsername: string): Promise<UserDto> {
    try {
      // Find user by email or username
      const user = await this.userModel
        .findOne({
          $or: [{ email: emailOrUsername }, { username: emailOrUsername }],
        })
        .exec();

      if (!user) {
        throw new HttpException('User not found', HttpStatus.NOT_FOUND);
      }

      // Construct and return UserDto
      const userDto: UserDto = {
        username: user.username,
        email: user.email,
        password: user.password,
        favoritePharmacies: user.favoritePharmacies,
        // Add other properties from user as needed
      };

      return userDto;
    } catch (error) {
      throw error; // Rethrow any errors
    }
  }

  async addFavoritePharmacy(pharmacyName: string, username: string) {
    try {
      // Find user by username
      const user = await this.userModel.findOne({ username }).exec();

      if (!user) {
        throw new HttpException('User not found', HttpStatus.NOT_FOUND);
      }

      // Add pharmacy to favorites
      user.favoritePharmacies.push(pharmacyName);
      await user.save();
    } catch (error) {
      throw new HttpException(
        'Error adding favorite pharmacy',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }

  async removeFavoritePharmacy(pharmacyName: string, username: string) {
    try {
      // Find user by username
      const user = await this.userModel
        .findOne({
          username,
        })
        .exec();

      if (!user) {
        throw new HttpException('User not found', HttpStatus.NOT_FOUND);
      }

      // Remove pharmacy from favorites
      user.favoritePharmacies = user.favoritePharmacies.filter(
        (pharmacy) => pharmacy !== pharmacyName,
      );
      await user.save();
    } catch (error) {
      throw new HttpException(
        'Error removing favorite pharmacy',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }

  async getFavoritePharmacies(username: string): Promise<string[]> {
    try {
      // Find user by username
      const user = await this.userModel.findOne({ username }).exec();

      if (!user) {
        throw new HttpException('User not found', HttpStatus.NOT_FOUND);
      }

      // Return the list of favorite pharmacies
      return user.favoritePharmacies;
    } catch (error) {
      throw new HttpException(
        'Error retrieving favorite pharmacies',
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }
  
}
