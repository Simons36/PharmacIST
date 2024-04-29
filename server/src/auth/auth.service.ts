import { Injectable } from "@nestjs/common";
import { Request } from "express";


@Injectable()
export class AuthService {

    login(requestBody : Request){
        return requestBody;
    }

    logout(){

    }

    register(){

    }
}