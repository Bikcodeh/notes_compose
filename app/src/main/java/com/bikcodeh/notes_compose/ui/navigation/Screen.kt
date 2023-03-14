package com.bikcodeh.notes_compose.ui.navigation

sealed class Screen(val route: String) {
    object Authentication: Screen(route = "authentication_screen")
    object Home: Screen(route = "home_screen")
    object Write: Screen(route = "write_screen?diaryId={diaryId}") {
        const val WRITE_ARG_KEY = "diaryId"
        fun passDiaryId(diaryId: String): String {
            return "write_screen?$WRITE_ARG_KEY=$diaryId"
        }
    }
}
