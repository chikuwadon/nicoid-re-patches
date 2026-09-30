# Popup pinch host regression checks

From the repository root (JDK required):

```sh
javac -d /tmp/nicoid-pinch-tests $(find porting/tests/stubs -name '*.java') \
  extensions/extension/src/main/java/e/e/a/PopupPinchGeometry.java \
  extensions/extension/src/main/java/e/e/a/PopupPinchLayout.java \
  porting/tests/PopupPinchGeometryTest.java porting/tests/PopupPinchLayoutTest.java
java -cp /tmp/nicoid-pinch-tests PopupPinchGeometryTest
java -cp /tmp/nicoid-pinch-tests PopupPinchLayoutTest
```

These test stubs must never be packaged in the Android extension. They check calculations
and event routing, not Android's real ViewGroup dispatch or WindowManager implementation.
On a device verify pinching over video/buttons/seek bar, corner drag, single-finger movement,
tap after release, size restoration on reopening, portrait/landscape, and popup-to-normal transfer.
