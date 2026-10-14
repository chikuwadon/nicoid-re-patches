package e.e.a;
/** Preserve the original preference contract: 0 streams, 1 acquires a cache. */
final class CachePlaybackPolicy {static boolean acquire(String mode){return "1".equals(mode);}}
