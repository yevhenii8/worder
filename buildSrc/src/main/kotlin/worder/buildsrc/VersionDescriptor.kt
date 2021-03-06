package worder.buildsrc

class VersionDescriptor(
        var majorVersion: Int,
        var minorVersion: Int,
        var buildNumber: Int,
        var isSnapshot: Boolean = false
) {
    companion object {
        fun fromString(worderVersion: String): VersionDescriptor {
            val matchRes = "(\\d+).(\\d+).(\\d+)(-SNAPSHOT)?".toRegex().matchEntire(worderVersion)
                    ?: error("Can't parse version: $worderVersion")

            return VersionDescriptor(
                    majorVersion = matchRes.groupValues[1].toInt(),
                    minorVersion = matchRes.groupValues[2].toInt(),
                    buildNumber = matchRes.groupValues[3].toInt(),
                    isSnapshot = worderVersion.endsWith("-SNAPSHOT")
            )
        }
    }


    override fun toString() = "$majorVersion.$minorVersion.$buildNumber${if (isSnapshot) "-SNAPSHOT" else ""}"
}
