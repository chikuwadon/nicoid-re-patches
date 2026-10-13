package e.e.a;class CacheFolders {static String videoId(String name){return name.matches("sm[0-9]+\\..*")?name.substring(0,name.indexOf('.')):null;}}
