# CinderLib

An [Architectury API](https://github.com/architectury/architectury-api) multi-loader mod for Minecraft `26.1.2`,
targeting **Fabric** and **NeoForge**.

## Project layout

- `common/` — shared code, using only Minecraft and Architectury API classes.
- `fabric/` — Fabric-specific entrypoint and glue code.
- `neoforge/` — NeoForge-specific entrypoint and glue code.

## Building

```sh
./gradlew build
```

Loader jars are output to `fabric/build/libs/` and `neoforge/build/libs/`.

## Running in dev

```sh
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

## Versions

| Component       | Version               |
|------------------|------------------------|
| Minecraft        | 26.1.2                 |
| Architectury API | 20.1.15                |
| Fabric Loader    | 0.19.5                 |
| Fabric API       | 0.155.3+26.1.2         |
| NeoForge         | 26.1.2.109             |
| Loom             | 1.17.493               |
| Java             | 25                      |

> These pin the latest known-compatible releases for Minecraft 26.1.2 as of this project's creation.
> Bump them in `gradle.properties` / `build.gradle` as newer builds land.
