# Encryption Utils
This is simple JavaFX GUI application for encryption utilities. This application has following features.

- Generate AES key
- Encrypt/Decrypt with AES algorithm
- Generate RSA key pair
- Encrypt/Decreyp with RSA algorithm

Note: For symmetric encryption/decryption, only `AES/GCM/NoPadding` method is supported. For asymmetric encryption/decryption, only `RSA/None/OAEPWITHSHA-384ANDMGF1PADDING` is supported.

## Screenshots
![Generate key pair](https://user-images.githubusercontent.com/4435371/127738788-77da331d-80f0-4416-8719-9aa591d041bd.png)

![RSA encryption](https://user-images.githubusercontent.com/4435371/127738798-46cefa81-c133-4a16-88c6-d3dfd4868873.png)

![AES cryption](https://user-images.githubusercontent.com/4435371/127738804-1963a43b-0ae3-432c-a0e6-190e1cd3fcde.png)

## Requirements
- JDK 16 or higher to build/run the application
- Maven 3.6.x to build the application

## Building the application
To build the application execute below command in the project root directory.

```cmd
mvn package
```

This command will create a directory with name `target` in the project root directory and a jar file named `encryption-utils-fat.jar` in the `target` directory. Only the jar file `encryption-utils-fat.jar` is needed, other files can be deleted. The jar file can be copied anywhere in the system. The generated jar file is fat-jar, hence it contains all the
dependencies withing itself.

## Running the application
To run the jar file, execute below command in the directory where ever the jar file has been moved to.

```cmd
javaw -jar encryption-utils-fat.jar
```

This command will start the application.

## Download the prebuilt Jar
You can download the prebuilt jar from the release section of Github.
