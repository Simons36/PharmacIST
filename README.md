# PharmacIST

## To run the project

- **Server:**
  - First, make sure you have docker engine or docker desktop running in yout computer
  - Then, in a command line, go to into folder server/
  - There, type the command `./start.sh` (NOTE: make sure that the script is executable)
- **App:**
  - You have to make sure that the url used to connect to the server is correct. This URL is defined in the file `/client/app/src/main/res/raw/config.properties` **IF:**
    - **You are running emulator:** the value in "api_url" should be `http://10.0.2.2:3000`
    - **If you are using your phone with wireless debugging:** the value in "api_url" will depend on the ip of the machine running the backend. You will have to discover your local ip (through ifconfig command, for example), and set it in this file. Let's say the local IP of the computer running the backend is `192.168.1.92`. Then, the "api_url value should be: `http://192.168.1.92:3000`"