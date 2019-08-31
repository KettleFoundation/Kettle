**:warning: This is a VERY alpha version of the KettleJava mixin mod**

![Kettle](https://i.imgur.com/gkmTKvR.png)

# Kettle

[![Forge Version](https://img.shields.io/badge/Forge-1.12.2%20--%202844-orange?style=flat-square)](http://github.com/MinecraftForge/MinecraftForge/tree/1.12.x)
[![MixinGradle Version](https://img.shields.io/badge/MixinGradle-0.6--SNAPSHOT-yellow?style=flat-square)](http://github.com/SpongePowered/MixinGradle)
[![Mixin Version](https://img.shields.io/badge/Mixin%20Version-Custom-yellow?style=flat-square)](https://github.com/KettleFoundation/KettleMixin)

### What's Kettle?

Kettle is the new implementation of Forge + Paper, but in mod form.

Kettle was initially concieved as a fork of Contigo, but after a few iterations got rid of Contigo's dependencies and can no longer be concidered it's fork, but a standalone hybrid server. Now with this new upgrade, it's not even considered a server, as it can be run as a simple mod, much like SpongeForge.

Kettle is dependent on custom Forge and Paper builds, meaning it can run both Craftbukkit/spigot-based plugins and forge-based mods.

We hope to eliminate all issues with craftbukkit forge servers. In the end, we envision a seamless, low lag Kettle experience with support for more mods and plugins that modify NMS or OBC code.

## Deployment

### Installation

1. Download the server from our [**Build Server**](https://ci.openprocesses.ml/job/Kettle-Mixins/)
2. Add jar file to the /mods/ folder of your server
3. Lauch your forge server as normal
4. Viola, you can now add your plugins to the generated folder, as if it's a spigot server.

## Build Instructions
- Clone Project
    - You can use an IDE or clone from a terminal
      - `git clone https://github.com/KettleFoundation/Kettle.git`
- Build
    - Linux / Git Bash / MacOS
      - `./gradlew build`
    - Windows
      - `gradlew build`

All builds are available in `build/libs/`

## Updating local repository

- Pull from source
    - `git pull origin`
- Reapply patches & build bianaries
    - Linux / Git Bash / MacOS
      - `./gradlew clean build`
    - Windows
      - `gradlew.bat clean build`

## Chat

You are welcome to visit Kettle Discord server [here](https://discord.gg/RqDjbcM).

## Unstable/Test builds

For unstable/test builds you can check __our build server__: https://ci.openprocesses.ml/job/Kettle-Mixins/
