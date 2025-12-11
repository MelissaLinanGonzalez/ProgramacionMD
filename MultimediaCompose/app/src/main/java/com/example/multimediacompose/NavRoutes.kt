package com.example.multimediacompose

sealed class NavRoutes (val route: String){
    object Menu : NavRoutes("menu")
    object Audio : NavRoutes("audio")
    object Video : NavRoutes("video")
    object Camara : NavRoutes("camara")
    object Recorder : NavRoutes("recorder")
}