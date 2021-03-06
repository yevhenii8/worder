# Worder

## Project structure
1. **worder-gui** - the guest of honor. A GUI application that uses a [My Dictionary](https://play.google.com/store/apps/details?id=com.swotwords.lite) backup file in order to replace word translations with definitions and examples from the Internet-dictionaries, i.e. update a word. Additionally, supports inserting new words from plain text files in the mode in which every line is a separate word or phrase. It doesn't work completely automatically. For every word you will be given a choice of definitions and examples. You will have to choose the appropriate ones or enter custom ones. Both graphical and command line interfaces are there at your service.
   ```
   hell (ад) -> hell (an extremely unpleasant or difficult place, situation, or experience)
   ```
   - [Kotlin](https://kotlinlang.org/)
   - [TornadoFX](https://github.com/edvin/tornadofx)
   - [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
   - [SQLite JDBC](https://github.com/xerial/sqlite-jdbc)
   - [Exposed](https://github.com/JetBrains/Exposed)
2. **worder-launcher** - a lightweight GUI application that has zero dependencies list and provides hassle-free running of *worder-gui*. Can be used as `jar file + local JRE 15` or with an installation via native installers (`.exe for Windows and .deb for Linux are available`). The installers contain packed java image and therefore don't need JVM to be installed.
   - [Java](https://jdk.java.net/15/)
   - [Java Swing](https://en.wikipedia.org/wiki/Swing_(Java))
3. **worder-commons** - common build logic that is used by both *buildSrc* and *worder-launcher*. It has zero dependencies list as well which, however, forced me to use `Java Serialization` instead of e.g. JSON or even more powerful [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization).
   - [Java](https://jdk.java.net/15/)
   - [Java Serialization](https://www.tutorialspoint.com/java/java_serialization.htm)
4. **bash-utils** - contains several bash scripts that were used at some point of the development. 
   - *generateFileStamps.sh* - was used for initial generation of source stamps. The problem it was solving stems from the fact that Java Standard Lib doesn't contain methods to access file creation time, and it's kind of [difficult](https://unix.stackexchange.com/questions/24441/get-file-created-creation-time) to obtain one in Ubuntu.
   - *binlog* - was used in order to investigate how exactly looks java-command with which gradle runs *worder-gui*. It allows to log all requests to any arbitrary executable just before passing them along. You can "listen" to several executable files simultaneously.
   - *linker* - was used by a first version of worder (CLI). It could use MTP of local-ftp for auto-delivery of a backup file to worder app and returning it back to phone. As for now it is deprecated, but planned to be implemented in Kotlin and merged with *worder-gui*.
5. **buildSrc** - a special gradle subproject. Contains some custom build logic for `worder-gui` and `worder-launcher`.
    - *AssembleExecutableTask.kt* - uses `jpackage` from JDK 15 to pack the launcher and JRE image into a native executable.
    - *DeployAppTask.kt* - deploys *worder-gui* to the remote [Bintray repository](https://bintray.com/evgen8) or to a specified local directory. The launcher uses Bintray repository as a default source for updates-check.
    - *DeployDemoTask.kt* - deploys demo-files to the remote [Bintray repository](https://bintray.com/evgen8) or to a specified local directory. The launcher uses Bintray repository as a default source of demo-files. Basically, the trick with the demo was written in haste, don't judge strictly about how it works.
    - *UpdateFileStampsTask.kt* - provides auto-stamp for source files that contains creation time, last modification time and count of compilations.
    - *UpdateVersionTask.kt* - puts a build number in an application version.

## How to run Worder ?

- [x] On Windows or Linux: using JRE 15 and the launcher: `java -jar worder-launcher_v1.0.272.jar`.
- [x] On Windows or Linux: install `worder-launcher_v1.0.272.exe` or `worder-launcher_v1.0.272.deb`. Run via an application icon.
- [x] Any other OS, including MacOS: use Gradle 6.7.1+ or Gradle wrapper (gradlew) to build the application from the sources. Gradle Wrapper will download gradle by itself. Otherwise, you will have to do that by yourself.
  ```
  // Bash
  cd worder-gui
  ./gradlew run
  
  // CMD
  cd worder-gui
  gradlew.bat run
  ```
  
## Have launched. What's next ?

There are two options how to look at it at work.  
First one. Usual use model.

   1. If you are not a 'My Dictionary' Android-app's user, you can [download and install](https://play.google.com/store/apps/details?id=com.swotwords.lite) it.
   2. Open it, select english as a language to learn and russian as a native one.
   3. You will see five default words. It's enough.
   4. Go to a menu -> `Settings` -> `Create database backup in a file`.
   5. Send it to your PC and then feed it to the Database Tab of Worder.
   6. Now you can upgrade present words and add new ones.
   7. After some actions, you can disconnect from the database, push it back to phone and restore from it.
    
Second one. There's a Demo mode if you just want to see how it looks and perform what's called 'smoke-test'. You just need to pass an argument to the launcher, ways how to do that are listed below.
   1. On Windows or Linux: using JRE 15 and the launcher: `java -jar worder-launcher_v1.0.272.jar --demo`.
   2. On Windows: after installation, open shortcut properties of worder's icon and add argument `--demo` to the `Target` field.
   3. On Linux: after installation, go to `/usr/share/applications`, open `worder-launcher-Worder_Launcher.desktop` and add `--demo` argument to the `Exec` line.

Additionally, with command line you can run
   - `java -jar worder-launcher_v1.0.272.jar --help` to see all launcher arguments.
   - `java -jar worder-launcher_v1.0.272.jar --args=--help` to see all worder arguments.

## Screenshots

### Worder Launcher
![worder-launcher.png](/screenshots/launcher.png?raw=true "Worder Launcher")

### Worder GUI
![worder-gui-disconnected-db.png](/screenshots/gui-disconnected.png?raw=true "Worder GUI - disconnected DB")
![worder-gui-connected-db.png](/screenshots/gui-connected.png?raw=true "Worder GUI - connected DB")
![worder-gui-update.png](/screenshots/gui-update-tab.png?raw=true "Worder GUI - Update Tab")
![worder-gui-insert.png](/screenshots/gui-insert-tab.png?raw=true "Worder GUI - Insert Tab")
![worder-gui-naked.png](/screenshots/gui-naked.png?raw=true "Worder GUI - No Active Tab")

## Remarks

1. Again, the launcher can be used only on Linux or Windows. On Mac you can run Worder only through [compiling it from the sources](#how-to-run-worder-).
3. It was primarily developed and fully-tested on Ubuntu\Linux for Ubuntu\Linux and FHD. Good look is **not guaranteed** on other resolutions or systems.
4. Project doesn't contain unit tests for test purposes. That's why they're disabled for the `gradle build` task.
5. Only smoke testing have been performed on Windows.
6. You can upload the launcher from the [Releases](https://github.com/yevhenii8/worder/releases) section.
