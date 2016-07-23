# Voxelwind

**Current version**: version 0.0.1 (_Layer of Fog_)

Voxelwind is an upcoming _Minecraft: Pocket Edition_ server project written in Java. It aims to provide a Java-first API,
provide high performance, high scalability and flexibility while being easy to use for causal users.

Voxelwind at the present moment aims to have a basic server implementation that drops players into a flatworld and can
run basic commands. In the future, Voxelwind will eventually have proper world loading support, a plugin API, and entity
support.

Voxelwind is licensed under the permissive **MIT** license. However, we would love if you submit improvements you make to
Voxelwind upstream so that the larger community can benefit from them.

## Special Thanks

* [MiNET](https://github.com/NiclasOlofsson/MiNET) was invaluable in helping me figure out some of the more difficult stuff.
* [yawkat's protocol documentation](https://confluence.yawk.at/display/PEPROTOCOL/pe-protocol-docs+Home) was also useful, especially for encryption.

## Requirements

To run Voxelwind, Java 8 is required. Additionally, it is strongly recommended you run Voxelwind on a recent 64-bit x86
Debian-based Linux distribution (Debian 8 or Ubuntu 16.04) for two reasons:

* Voxelwind can use multiple threads to listen to the same port on Linux (`SO_REUSEPORT`). **JDK 9 includes more native
support for SO_REUSPORT, so it will work on any modern Unix-like OS.**
* Voxelwind can take advantage of native compression support. (A solution needs to be sought for native encryption support.)

Voxelwind will work on other configurations outside 64-bit Debian/Ubuntu, but scalability and throughput will be affected.