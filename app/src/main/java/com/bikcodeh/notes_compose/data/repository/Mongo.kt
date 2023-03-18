package com.bikcodeh.notes_compose.data.repository

import com.bikcodeh.notes_compose.BuildConfig
import com.bikcodeh.notes_compose.domain.model.Diary
import com.bikcodeh.notes_compose.domain.repository.MongoRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration

object MongoDB: MongoRepository {

    private val app = App.create(BuildConfig.APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    override fun configureRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions { sub ->
                    add(
                        query = sub.query("ownerId == $0", user.id),
                        name = "diary"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }
}