package worder.commons;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppDescriptor implements Serializable {
    private final String generated = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    private final String name = obtainNameForCurrentOS();
    private final String appVersion;
    private final String appMainClass;
    private final String usedModules;
    private final List<String> envArguments;
    private final List<Artifact> artifacts;


    private AppDescriptor(
            String appVersion,
            String appMainClass,
            String usedModules,
            List<String> envArguments,
            List<Artifact> artifacts
    ) {
        this.appVersion = appVersion;
        this.appMainClass = appMainClass;
        this.usedModules = usedModules;
        this.envArguments = envArguments;
        this.artifacts = artifacts;
    }


    public static AppDescriptor fromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
        if (bytes == null)
            return null;

        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(input);
        AppDescriptor obj = (AppDescriptor) ois.readObject();
        ois.close();

        return obj;
    }

    public static String obtainNameForCurrentOS() {
        return "WorderAppDescriptor-" + OS.getCurrentOS();
    }


    public String getGenerated() {
        return generated;
    }

    public String getName() {
        return name;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppMainClass() {
        return appMainClass;
    }

    public String getUsedModules() {
        return usedModules;
    }

    public List<String> getEnvArguments() {
        return envArguments;
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(output);
        oos.writeObject(this);
        output.close();

        return output.toByteArray();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppDescriptor that = (AppDescriptor) o;

        if (!generated.equals(that.generated)) return false;
        if (!name.equals(that.name)) return false;
        if (!appVersion.equals(that.appVersion)) return false;
        if (!appMainClass.equals(that.appMainClass)) return false;
        if (!usedModules.equals(that.usedModules)) return false;
        if (!envArguments.equals(that.envArguments)) return false;
        return artifacts.equals(that.artifacts);
    }

    @Override
    public int hashCode() {
        int result = generated.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + appVersion.hashCode();
        result = 31 * result + appMainClass.hashCode();
        result = 31 * result + usedModules.hashCode();
        result = 31 * result + envArguments.hashCode();
        result = 31 * result + artifacts.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AppDescriptor{" +
                "generated='" + generated + '\'' +
                ", name='" + name + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", appMainClass='" + appMainClass + '\'' +
                ", usedModules='" + usedModules + '\'' +
                ", envArguments=" + envArguments +
                ", artifacts=" + artifacts +
                '}';
    }

    public static class Artifact implements Serializable {
        transient private final Path pathToFile;
        private final String name;
        private final Type type;


        private Artifact(Path pathToFile, Type type) {
            this.pathToFile = pathToFile;
            this.name = pathToFile.getFileName().toString();
            this.type = type;
        }


        public Path getPathToFile() {
            return pathToFile;
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Artifact artifact = (Artifact) o;

            if (!name.equals(artifact.name)) return false;
            return type == artifact.type;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Artifact{" +
                    "pathToFile=" + pathToFile +
                    ", name='" + name + '\'' +
                    ", type=" + type +
                    '}';
        }

        public enum Type {
            CLASSPATH, MODULEPATH
        }
    }

    public static class Builder {
        private String appVersion;
        private String appMainClass;
        private String usedModules;
        private List<String> envArguments;
        private List<Path> classPath;
        private List<Path> modulePath;


        public AppDescriptor build() {
            Objects.requireNonNull(appVersion, "'appVersion' should be specified!");
            Objects.requireNonNull(appMainClass, "'appMainClass' should be specified!");
            Objects.requireNonNull(usedModules, "'usedModules' should be specified!");
            Objects.requireNonNull(envArguments, "'envArguments' should be specified!");
            Objects.requireNonNull(classPath, "'classPath' should be specified!");
            Objects.requireNonNull(modulePath, "'modulePath' should be specified!");

            var artifacts = Stream.concat(
                    classPath.stream().map(it -> new Artifact(it, Artifact.Type.CLASSPATH)),
                    modulePath.stream().map(it -> new Artifact(it, Artifact.Type.MODULEPATH))
            ).collect(Collectors.toList());


            return new AppDescriptor(
                    appVersion,
                    appMainClass,
                    usedModules,
                    envArguments,
                    artifacts
            );
        }


        public Builder appVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public Builder appMainClass(String appMainClass) {
            this.appMainClass = appMainClass;
            return this;
        }

        public Builder usedModules(String usedModules) {
            this.usedModules = usedModules;
            return this;
        }

        public Builder envArguments(List<String> envArguments) {
            this.envArguments = envArguments;
            return this;
        }

        public Builder modulePath(List<Path> modulePath) {
            this.modulePath = modulePath;
            return this;
        }

        public Builder classPath(List<Path> classPath) {
            this.classPath = classPath;
            return this;
        }
    }
}
