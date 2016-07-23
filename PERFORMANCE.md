# Performance

Voxelwind is meant to be high-performance, but has unique constraints that are discussed in this document.

## UDP socket listening

Voxelwind uses Netty as the I/O library. Although Netty has high performance and can efficiently use multiple threads
using TCP, MCPE uses UDP, which requires special care for proper usage.

On recent versions of Linux, Mac OS X, FreeBSD and Solaris, `SO_REUSEPORT` is available to allow multiple threads to
bind to the same port. However, in Netty, this support is currently only available for Linux users.

Therefore, Voxelwind does not do much processing in its event loop except for decoding and encoding packets and queuing
them to be executed by other threads.