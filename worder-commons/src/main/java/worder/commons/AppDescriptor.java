package worder.commons;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AppDescriptor implements Serializable {
    private static final SerializationUtil<AppDescriptor> serializator = new SerializationUtil<>();
    transient private List<Artifact> allArtifacts;
    private final String name = "WorderAppDescriptor-" + System.getProperty("os.name");
    private final String generated = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));

    private final String appVersion;
    private final String appMainClass;
    private final String usedModules;
    private final List<String> envArguments;
    private final List<Artifact> modulePath;
    private final List<Artifact> classPath;
    private final long version;


    private AppDescriptor(
            String appVersion,
            String appMainClass,
            String usedModules,
            List<String> envArguments,
            List<File> modulePath,
            List<File> classPath,
            long version
    ) {
        this.appVersion = appVersion;
        this.appMainClass = appMainClass;
        this.usedModules = usedModules;
        this.envArguments = envArguments;
        this.modulePath = modulePath.stream().map(Artifact::new).collect(Collectors.toList());
        this.classPath = classPath.stream().map(Artifact::new).collect(Collectors.toList());
        this.version = version;
    }


    public List<Artifact> getModulePath() {
        return modulePath;
    }

    public List<Artifact> getClassPath() {
        return classPath;
    }

    public List<Artifact> getAllArtifacts() {
        if (allArtifacts == null) {
            allArtifacts = new ArrayList<>(classPath);
            allArtifacts.addAll(modulePath);
        }

        return allArtifacts;
    }

    public byte[] toByteArray() {
        try {
            return serializator.serialize(this);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public long getVersion() {
        return version;
    }


    public static class Artifact implements Serializable {
        transient private final File file;
        private final String name;
        private final long size;


        Artifact(File file) {
            this.file = file;
            name = "artifacts/" + file.getName();
            size = file.length();
        }


        public File getFile() {
            return file;
        }

        public String getName() {
            return name;
        }

        public long getSize() {
            return size;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Artifact artifact = (Artifact) o;

            if (size != artifact.size) return false;
            return name.equals(artifact.name);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (int) (size ^ (size >>> 32));
            return result;
        }
    }

    public static class Builder {
        private String appVersion;
        private String appMainClass;
        private String usedModules;
        private List<String> envArguments;
        private List<File> modulePath;
        private List<File> classPath;
        private Long version;


        public AppDescriptor build() {
            Objects.requireNonNull(appVersion, "'appVersion' should be specified!");
            Objects.requireNonNull(appMainClass, "'appMainClass' should be specified!");
            Objects.requireNonNull(usedModules, "'usedModules' should be specified!");
            Objects.requireNonNull(envArguments, "'envArguments' should be specified!");
            Objects.requireNonNull(modulePath, "'modulePath' should be specified!");
            Objects.requireNonNull(classPath, "'classPath' should be specified!");
            Objects.requireNonNull(version, "'version' should be specified!");

            return new AppDescriptor(
                    appVersion,
                    appMainClass,
                    usedModules,
                    envArguments,
                    modulePath,
                    classPath,
                    version
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

        public Builder modulePath(List<File> modulePath) {
            this.modulePath = modulePath;
            return this;
        }

        public Builder classPath(List<File> classPath) {
            this.classPath = classPath;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }
    }


    public static AppDescriptor fromByteArray(byte[] bytes) {
        try {
            return serializator.deserialize(bytes);
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static String getCalculatedName() {
        return "WorderAppDescriptor-" + System.getProperty("os.name");
    }
}