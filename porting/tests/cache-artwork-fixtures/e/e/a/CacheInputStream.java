package e.e.a;
/** Artwork decoding is isolated from provider I/O, tested by the storage suites. */
public final class CacheInputStream extends java.io.FileInputStream {
    public CacheInputStream(java.io.File file)throws java.io.FileNotFoundException{super(file);}
}
