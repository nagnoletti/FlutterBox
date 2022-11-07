package com.github.nagnoletti.android.fluttermoduleintegrationlib

import android.content.Context
import android.content.Intent
import androidx.annotation.UiThread
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterActivity.CachedEngineIntentBuilder
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint
import java.util.*
import kotlin.reflect.KClass

/**
 * Tool to include a Flutter module within a native Android application with defaults to make it
 * easier and keep dependency to Flutter SDK enclosed.
 * Initialize it on app start with [FlutterBox.init].
 * You can perform engine warmup to avoid latency starting the Flutter app with
 * [FlutterBox.warmup].
 * Launch a [FlutterActivity] with the default [DartEntrypoint] with
 * [FlutterBox.launchDefaultActivityIntent] or extending [FlutterBoxActivity] and
 * [FlutterBoxFragment].
 */
object FlutterBox {

    private lateinit var engineGroup: FlutterEngineGroup

    internal var activeEngineData: ActiveEngineData? = null
        private set

    /**
     * Init [FlutterBox]
     * Initializes the [FlutterEngineGroup] containing engines to start the Flutter app.
     */
    @UiThread
    fun init(
        appContext: Context,
        dartVmArgs: Array<String>? = null,
        options: Options?
    ) {
        engineGroup = FlutterEngineGroup(appContext, dartVmArgs)
        options?.let { warmup(appContext, options) }
    }

    /**
     * Initializes the first engine for later use and smoother transition to the Flutter app
     * the first time it opens.
     */
    private fun warmup(
        appContext: Context,
        options: Options
    ) {
        maybeThrowNotInitialized()
        if (activeEngineData?.activatedOnWarmup == true) {
            throw Exception.AlreadyWarmedUp
        }

        val (id, engine) = newIdEnginePairFromGroup(appContext, options.route)
        activeEngineData = ActiveEngineData(id, true)
        FlutterEngineCache.getInstance().put(id, engine)
    }

    internal fun resetRoute(appContext: Context, route: String) {
        maybeThrowNotInitialized()
        if (activeEngineData != null) {
            generateAndCacheNewActiveEngine(appContext, route)
        }
    }

    /**
     * @throws [Exception.NotInitialized] if the [FlutterEngineGroup] used by [FlutterBox]
     * was initialized before this moment.
     */
    private fun maybeThrowNotInitialized() {
        if (!FlutterBox::engineGroup.isInitialized) {
            throw Exception.NotInitialized
        }
    }

    /**
     * Makes an intent to start the default [FlutterActivity] and launches it.
     */
    @UiThread
    fun launchDefaultActivityIntent(
        appContext: Context,
        initialRoute: String? = null
    ) = appContext.startActivity(
        makeActivityIntent(appContext, FlutterActivity::class, initialRoute)
    )

    /**
     * Creates a new [FlutterEngine] overwriting [activeEngineData] and caching it into
     * [FlutterEngineCache].
     * @returns id of the newly created [FlutterEngine].
     */
    private fun generateAndCacheNewActiveEngine(
        appContext: Context,
        initialRoute: String?
    ): String {
        val (id, engine) = newIdEnginePairFromGroup(appContext, initialRoute)
        activeEngineData = ActiveEngineData(id, false)
        FlutterEngineCache.getInstance().put(id, engine)
        return id
    }

    /**
     * Makes an intent to start a custom [FlutterActivity].
     * If the [FlutterEngineGroup] used by [FlutterBox] is not initialized an exception is
     * thrown.
     *
     * @return [Intent] to launch the activity running the Flutter module's app.
     */
    private fun makeActivityIntent(
        appContext: Context,
        activityClass: KClass<out FlutterActivity>,
        initialRoute: String?,
    ): Intent {
        maybeThrowNotInitialized()

        return when (val data = activeEngineData) {
            null -> {
                val id = generateAndCacheNewActiveEngine(appContext, initialRoute)
                CachedEngineIntentBuilder(activityClass.java, id).build(appContext)
            }
            else -> {
                when (/*val activeEngine = */FlutterEngineCache.getInstance().get(data.id)) {
                    null -> {
                        activeEngineData = null
                        makeActivityIntent(appContext, activityClass, initialRoute)
                    }
                    else -> {
                        if (initialRoute != null) {
                            activeEngineData = null
                            val intent = makeActivityIntent(appContext, activityClass, initialRoute)
                            // Remove engine from cache after creating a new one
                            FlutterEngineCache.getInstance().remove(data.id)
                            intent
                        } else {
                            val intent = CachedEngineIntentBuilder(
                                activityClass.java,
                                data.id
                            ).build(appContext)
                            intent
                        }
                    }
                }
            }
        }
    }

    /**
     * Create (and run) a new id-engine pair from [engineGroup].
     */
    private fun newIdEnginePairFromGroup(
        appContext: Context,
        initialRoute: String?
    ): Pair<String, FlutterEngine> {
        val newEngineId = UUID.randomUUID().toString()
        val newEngine = engineGroup.createAndRunEngine(
            appContext,
            DartEntrypoint.createDefault(),
            initialRoute
        )
        return newEngineId to newEngine
    }

    /**
     * Predictable exceptions that the [FlutterBox] can throw using it.
     */
    sealed class Exception(message: String) : kotlin.Exception(message) {

        /**
         * Use to signal that no one called [FlutterBox.init] before using
         * [FlutterBox].
         */
        object NotInitialized : Exception(
            "FlutterManager has not been initialized. Call ${FlutterBox::init} on FlutterManager to do so."
        )

        /**
         * Signal that warmup has already been performed
         */
        object AlreadyWarmedUp :
            Exception("Tried to call ${FlutterBox::warmup} but engine was already warmed up.")

    }

    data class ActiveEngineData(
        val id: String,
        val activatedOnWarmup: Boolean
    )

    data class Options(val route: String? = null)
}