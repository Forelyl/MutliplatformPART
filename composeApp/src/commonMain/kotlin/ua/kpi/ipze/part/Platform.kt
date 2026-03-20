package ua.kpi.ipze.part

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform