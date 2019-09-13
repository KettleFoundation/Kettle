**:warning: Kettle is still __very raw__ and you may encounter issues in using it with your server. You have been warned!**

![Kettle](https://i.imgur.com/onGk0dK.png)

# Kettle

![](https://img.shields.io/badge/Minecraft%20Forge-1.12.2%20--%202838-orange.svg?style=for-the-badge)

### What's Kettle?

Kettle is the next link in the hybrid server chain:
**Kettle** :arrow_right: Contigo :arrow_right: Thermos :arrow_right: KCauldron :arrow_right: Cauldron :arrow_right: MCPC+

Kettle was initially concieved as a fork of Contigo, but after a few iterations got rid of Contigo's dependencies and can no longer be concidered it's fork, but a standalone hybrid server.

Kettle is dependent on custom Forge and Paper builds, meaning it can run both Craftbukkit/spigot-based plugins and forge-based mods.

We hope to eliminate all issues with craftbukkit forge servers. In the end, we envision a seamless, low lag Kettle experience with support for new 1.12+ versions of Minecraft.

## Deployment

### Installation

1. Download the server from the [**Releases** section](https://github.com/KettleFoundation/Kettle/releases) or our [**Build Server**](https://ci.openprocesses.ml/job/Kettle/)
2. Make a new folder for the server
3. Move the server to the folder and rename it to `kettle.jar`
4. Make a [launch script](https://gist.github.com/aolko/3b7a93107d162b21730c92e5236e3239)

## Build Instructions
- Clone Project
    - You can use an IDE or clone from a terminal
      - `git clone https://github.com/KettleFoundation/Kettle.git`
- Setup
    - `git submodule update --init --recursive`
- Build
    - Linux / Git Bash / MacOS
      - `./gradlew launch4j`
    - Windows
      - `.\gradlew.bat launch4j`

All builds are available in `build/distributions`

## Updating local repository

- Pull from source
    - `git pull origin`
- Reapply patches & build bianaries
    - Linux / Git Bash / MacOS
      - `./gradlew clean launch4j`
    - Windows
      - `./gradlew.bat clean launch4j`

## Chat

You are welcome to visit Kettle Discord server [here](https://discord.gg/RqDjbcM).

## Unstable/Test builds

For unstable/test builds you can check __this repository__, or our build server: https://ci.openprocesses.ml/job/Kettle
