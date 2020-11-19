# Worder

## Project structure
1. **worder-gui** - the guest of honor. A GUI application that uses a [My Dictionary](https://play.google.com/store/apps/details?id=com.swotwords.lite) backup file in order to replace word translations with definitions and examples from Internet-dictionaries, i.e. update a word. Additionally, supports inserting new words from plain text files in the mode in which every line is a separate word or phrase. It doesn't work completely automatically. For every word you will be given a choice of definitions and examples. You will have to choose the appropriate ones or enter custom ones. The graphical and command line interfaces are there at your service.
   ```
   hell (ад) -> hell (an extremely unpleasant or difficult place, situation, or experience)
   ```
   - [Kotlin](https://kotlinlang.org/)
   - [TornadoFX](https://github.com/edvin/tornadofx)
   - [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
   - [SQLite JDBC](https://github.com/xerial/sqlite-jdbc)
   - [Exposed](https://github.com/JetBrains/Exposed)
2. **worder-launcher** - a lightweight GUI application that has zero dependencies list and provides hassle-free running of *worder-gui*. Can be used as `jar file + local JRE 15` or with installation via native installers (`.exe for Windows and .deb for Linux are available`). The installers contain packed java image and therefore don't need JVM to be installed.
   - [Java 15](https://jdk.java.net/15/)
   - [Java Swing](https://en.wikipedia.org/wiki/Swing_(Java))
3. **worder-commons** - common build logic that is used by both *buildSrc* and *worder-launcher*. Has zero dependencies list as well that forces using `Java Serialization` instead of e.g. JSON.
   - [Java 15](https://jdk.java.net/15/)
   - [Java Serialization](https://www.tutorialspoint.com/java/java_serialization.htm)
4. **bash-utils** - contains two bash scripts that were used at some time during development. 
   - *generateFileStamps.sh* - was used for initial generation of source stamps. The problem it was solving stems from the fact that Java Standard Lib doesn't contain methods to access file creation time, and it's kind of [difficult](https://unix.stackexchange.com/questions/24441/get-file-created-creation-time) to obtain one in Ubuntu.
   - *binlog.sh* - was used in order to investigate how exactly looks java-command with which gradle runs *worder-gui*. It allows to log all requests to any arbitrary executable just before passing them to it. You can "listen" to several executable files simultaneously.
5. **buildSrc** - a special gradle subproject. Contains custom build logic for worder.
    - *AssembleExecutableTask.kt* - uses `jpackage` from JDK 15 to pack (the launcher + JRE image) into native executable.
    - *DeployAppTask.kt* - deploys *worder-gui* to the remote [Bintray repository](https://bintray.com/evgen8) or custom local file system directory. The launcher uses Bintray repository as a default source for updates-check.
    - *UpdateFileStampsTask.kt* - provides auto-stamp for source files that contains creation time, last modification time, count of compilations.
    - *UpdateVersionTask.kt* - puts a build number in application version.

## How to run Worder ?

- [x] Using JRE 15 and the launcher: `java -jar worder-launcher-1.0.238.jar`
- [x] Install `worder-launcher-1.0.238.exe` on Windows or `worder-launcher-1.0.238.deb` on Linux. Run via an application icon.
- [x] Using Gradle 6.7.1+ or Gradle wrapper (gradlew). Wrapper will download gradle by itself otherwise you have to download Gradle by yourself.
  ```
  // Bash
  cd worder-gui
  ./gradlew run
  
  // CMD
  cd worder-gui
  gradlew.bat run
  ```

## Screenshots

![Alt text](/screenshots/worder-launcher.png?raw=true "Worder Launcher")
