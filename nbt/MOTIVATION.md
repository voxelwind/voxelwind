# Why write your own NBT library?

This revolves around two main issues:

* **Code quality**: current NBT libraries for Java lack proper unit tests, especially for decoding and encoding NBT.
* **MCPE needs**: MCPE 0.16 has a new NBT encoding format (fun!) that makes major changes.

`voxelwind-nbt` has unit tests over decoding and encoding NBT and supports the new NBT encoding that 0.16 uses. I (Tux)
still do not understand why Mojang decided to break their own standards.