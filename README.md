[![kotlin](https://img.shields.io/github/languages/top/bikcodeh/ToDoApp.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/) [![Android API](https://img.shields.io/badge/api-24%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=24)
# Notes compose

## :star: Features

- [x] Display notes
- [x] Display singular note
- [x] Edit or delete singular note
- [x] Delete all notes
- [x] Login
- [x] Filter
- [x] Local persistance
- [x] Dark theme
- [x] Modules
- [x] Upload images from gallery

:runner: For run the app just clone the repository and execute the app on Android Studio.
## Setup environment
#### Fill out in your local.properties the variables:

###### APP_ID = "Your custom app id from Mongo DB"
###### CLIENT_ID = "Your custom cliente id from Google cloud"

### :bookmark_tabs: Requirements to install the app
- Use phones with Android Api 24+

##### :open_file_folder: This application was developed using Kotlin and uses the following components:
- Kotlin
- Jetpack compose
- Coroutines
- Firebase storage
- Firebase authentication
- Mongo Db - Realm sync
- Clean architecture (Domain, Data, Presentation)
- Modules
- MVVM
- Repository pattern
- StateFlow
- Room database
- Flow
- Mutable State
- Jetpack navigation compose
- Dagger Hilt (Dependency injection)
- Coil (Load images)

## Structure per module


## :sun_with_face: Screenshots Light theme
 | Splash |     Sign In    |  Home  |   Write |  Write with Gallery |
 | :----: | :---------: | :-------: | :-----------: | :-----: |
 |<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/splash.png" align="left" height="300" width="1600">|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/sign_in.jpeg" align="left" height="300" width="1600">|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/home_with_gallery.png" align="left" height="300" width="1600">|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/write.png" align="left" height="300" width="1600">|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/write_with_images.png" align="left" height="300" width="1600"> |

| Drawer |   Empty  |   Sign out dialog | Delete single dialog  | Delete all dialog | 
| :-----------------:| :----------------------: | :----------------:| :----------------:| :----------------:|
<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/drawer.png" align="left" height="300" width="170">| <img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/empty.png" align="left" height="300" width="170"> |  <img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/sign_out_dialog.png" align="left" height="300" width="170"> | <img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/write_delete_dialog.png" align="left" height="300" width="170"> | <img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/delete_all_dialog.png" align="left" height="300" width="170"> |

| Date picker dialog | Date time dialog |
| :------------:| :---------------------:| 
|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/date_picker.png" align="left" height="300" width="170">|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/time_picker.png" align="left" height="300" width="170">|

## :new_moon_with_face: Screenshots Dark Mode

 | Splash |     Sign In    |  Home  |   Write |  Write with Gallery |
 | :----: | :---------: | :-------: | :-----------: | :-----: |
 |<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/splash_dark.jpeg" align="left" height="300" width="1600">|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/sign_in_dark.jpeg" align="left" height="300" width="1600">|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/home_with_gallery_dark.jpeg" align="left" height="300" width="1600">|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/write_dark.jpeg" align="left" height="300" width="1600">|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/write_with_images_dark.jpeg" align="left" height="300" width="1600"> |

| Drawer |   Empty  |   Sign out dialog | Delete single dialog  | Delete all dialog | 
| :-----------------:| :----------------------: | :----------------:| :----------------:| :----------------:|
<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/drawer_dark.jpeg" align="left" height="300" width="170">| <img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/empty_dark.jpeg" align="left" height="300" width="170"> |  <img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/sign_out_dialog_dark.jpeg" align="left" height="300" width="170"> | <img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/write_delete_dialog_dark.jpeg" align="left" height="300" width="170"> | <img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/delete_all_dialog_dark.jpeg" align="left" height="300" width="170"> |

| Date picker dialog | Date time dialog |
| :------------:| :---------------------:| 
|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/date_picker_dark.jpeg" align="left" height="300" width="170">|<img src="https://raw.githubusercontent.com/Bikcodeh/notes_compose/main/screenshots/time_picker_dark.jpeg" align="left" height="300" width="170">|


## :dart: Architecture

The application is built using Clean Architeture pattern based on [Architecture Components](https://developer.android.com/jetpack/guide#recommended-app-arch) on Android. The application is divided into three layers:

![Clean Arquitecture](https://devexperto.com/wp-content/uploads/2018/10/clean-architecture-own-layers.png)

- Domain: This layer contains the business logic of the application, here we define the data models and the use cases.
- Data: This layer contains the data layer of the application. It contains the database, network and the repository implementation.
- Presentation: This layer contains the presentation layer of the application.

## License

MIT

**Bikcodeh**
