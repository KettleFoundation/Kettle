**If you are looking for an updated version, please view the [KettleJava](https://github.com/KettleFoundation/Kettle/tree/KettleJava) branch**

**:warning: Kettle is still __raw__ and you may encounter issues with using it as your server software. This software is not yet ready for Production use. You have been warned!**

![Kettle](https://i.imgur.com/gkmTKvR.png)

# Kettle

![](https://img.shields.io/badge/Forge-1.12.2%20--%202838-orange.svg?style=for-the-badge)
![Jenkins](https://img.shields.io/jenkins/build/https/ci.openprocesses.ml/Kettle?label=Build&style=for-the-badge)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/KettleFoundation/Kettle?label=Latest%20Version&style=for-the-badge)

### What's Kettle?

Kettle is the next link in the hybrid server chain:
**Kettle** :arrow_right: Contigo :arrow_right: Thermos :arrow_right: KCauldron :arrow_right: Cauldron :arrow_right: MCPC+

Kettle was initially conceived as a fork of Contigo, but after a few iterations got rid of Contigo's dependencies and can no longer be concidered it's fork, but a standalone hybrid server.

Kettle is dependent on custom Forge and Paper builds, meaning it can run both Craftbukkit/spigot-based plugins and forge-based mods.

We hope to eliminate all issues with craftbukkit forge servers. In the end, we envision a seamless, low lag Kettle experience with support for new 1.12+ versions of Minecraft.

## Deployment

### Installation

1. Download the server from the [**Releases** section](https://github.com/KettleFoundation/Kettle/releases)
2. Make a new folder for the server
3. Move the server to the folder and rename it to `kettle.jar`
4. Make a [launch script](https://gist.github.com/aolko/3b7a93107d162b21730c92e5236e3239)

### Building the sources

All builds are available in `build/distributions`

- Clone the Project
  - You can use GitHub Desktop/GitKraken or clone using the terminal 
    - `git clone https://github.com/KettleFoundation/Kettle` 
  - Next you are gonna want to clone the submodule
    - `git submodule update --init --recursive` 
- Building
  - First you want to setup the project
    - `./gradlew setup`
  - After you have setup the project you are going to want to run the build
    - `./gradlew build`
  - Now go and get a drink this may take some time

## Chat

You are welcome to visit Kettle Discord server [here](https://discord.gg/RqDjbcM).

## More Kettle!

For unstable/test builds, you can check __[KettleDev](https://github.com/KettleFoundation/KettleDev)__!
For 1.14.4 builds, you can check __[KettleDev](https://github.com/KettleFoundation/Kettle-1.14.4)__!
