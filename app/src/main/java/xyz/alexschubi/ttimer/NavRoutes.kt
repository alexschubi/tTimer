package xyz.alexschubi.ttimer

sealed class NavRoutes(val route: String) {
    object Screen1: NavRoutes("screen1")
}