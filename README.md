**:warning: Kettle is still __very raw__ and you may encounter issues in using it with your server. You have been warned!**

**:warning::warning: Experimental branch. Expect everything to be broken.**

![Kettle](https://i.imgur.com/kHmTLzI.png)

# Kettle

![](https://img.shields.io/badge/Kettle%20Red-Pre--alpha-red.svg?style=for-the-badge)

## What is KettleRed?

*<font size="1">(heads up: some of the features described may not be available from the start)</font>*

![](https://i.imgur.com/PJn696Y.png)

**KettleRed** is a new experimental branch of Kettle built with JRuby and utilizing Java classes and bindings.
**KettleRed**, like **KettleJava**, still relies on Forge and Paper, and yet, introduces a new Scripting Engine (which is a part of the core).

![](https://i.imgur.com/bvOywvh.png)

Scripting Engine runs scripts and packages, rather than plain plugins or mods.

Scripting Engine runs with Forge and Paper and uses their APIs to allow creation of scripts and packages utilizing features from both Forge and Paper.

*Heads up: Scripting Enigne requires a ScriptLoader forge mod on the client for Forge api scripts/packages to work.*

### How it works

*Heads up: stub image*
![](https://cdn.discordapp.com/attachments/545889689245319188/606597256321302722/unknown.png)

Scripting engine loads up scripts and packages on the server, while client connects to the server Scripting Engine checks for the ScriptLoader and if it's enabled/installed lets client in, all scripts utilizing the forge api are loaded and executed via ScriptLoader on joining.

## Deployment

### We'll update this section later

## Chat

You are welcome to visit Kettle Discord server [here](https://discord.gg/RqDjbcM).

## Unstable/Test builds

For unstable/test builds you can check **releases** section for the KettleRed releases.
