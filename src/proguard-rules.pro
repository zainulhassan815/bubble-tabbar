# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.dreamers.bubbletabbar.BubbleTabBar {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'com/dreamers/bubbletabbar/repack'
-flattenpackagehierarchy
-dontpreverify
