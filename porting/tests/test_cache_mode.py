"""Exercise the production cache-mode gate with storage/service test doubles."""
import pathlib,subprocess,sys,tempfile
classes,android,ecj=map(pathlib.Path,sys.argv[1:])
fixtures=pathlib.Path(__file__).with_name("cache-mode-fixtures")
with tempfile.TemporaryDirectory() as temp:
 out=pathlib.Path(temp);deps=f"{classes}:{android}"
 subprocess.run(["java","-jar",str(ecj),"-1.8","-warn:none","-cp",deps,"-d",str(out),*map(str,fixtures.rglob("*.java"))],check=True)
 subprocess.run(["java","-cp",f"{out}:{deps}","e.e.a.CacheModeTest",str(out/"files")],check=True)
