package worder.buildsrc

class WorderVersionDescriptor(
        var majorVersion: Int,
        var minorVersion: Int,
        var buildNumber: Int,
        var isSnapshot: Boolean = false
) {
    companion object {
        fun parseWorderVersion(version: String): WorderVersionDescriptor {
            val matchRes = "(\\d+).(\\d+).(\\d+)".toRegex().matchEntire(version)
                    ?: error("Can't parse version: $version")

            return WorderVersionDescriptor(
                    majorVersion = matchRes.groupValues[0].toInt(),
                    minorVersion = matchRes.groupValues[1].toInt(),
                    buildNumber = matchRes.groupValues[2].toInt(),
                    isSnapshot = version.endsWith("-SNAPSHOT")
            )
        }
    }


    override fun toString() = "$majorVersion.$minorVersion.$buildNumber${if (isSnapshot) "-SNAPSHOT" else ""}"
}
