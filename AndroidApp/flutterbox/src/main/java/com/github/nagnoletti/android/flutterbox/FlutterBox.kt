package com.github.nagnoletti.android.flutterbox

import android.content.Context
import com.github.nagnoletti.android.flutterbox.FlutterBox.initialize
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor
import java.util.*

/**
 * FlutterBox
 * Initialize it (preferably on app start) with [FlutterBox.initialize].
 * Get an engine ID for your screen (FlutterActivity or FlutterFragment) with [FlutterBox.getScreenEngineID].
 */
object FlutterBox {
    private val className: String get() = FlutterBox::javaClass.name

    /**
     * Engine ID used to identify the engine used for warm up.
     */
    private const val initializerEngineID = "FlutterBox.initializerEngineID"

    /**
     * Screen ID used to
     */
    private const val initializerScreenID = "FlutterBox.initializerScreenID"

    /**
     * FlutterEngineGroup used to instantiate and run Flutter sharing resources between engines for
     * quicker Flutter screen transitions.
     */
    private var engineGroup: FlutterEngineGroup? = null

    /**
     * Map storing screenID-engineID pairs to get an engineID every time a screen requires it based
     * on its ID.
     */
    private val assignedEngineIDs = mutableMapOf<String, String>()

    /**
     * Initialization method creating the [engineGroup] and running an engine to warmup resources
     * for later engine startups.
     */
    fun initialize(context: Context) {
        checkMultipleInitializationsError {
            val eg = FlutterEngineGroup(context)
            engineGroup = eg
            eg.initialize(context)
        }
    }

    /**
     * Get the engine ID assigned to the activity or fragment with a [screenID].
     * Provide [opts] to configure the engine and the environment of the Flutter app running on it.
     * Provide [engineID] to optionally assign a specific ID for the engine.
     * If none is provided a random UUID is assigned instead.
     */
    fun getScreenEngineID(
        context: Context,
        screenID: String,
        engineID: String? = null,
        opts: Options? = null
    ): String = checkNotInitializedError {
        assignedEngineIDs[screenID] ?: cacheAndRunNewEngineToGetID(
            context,
            screenID,
            engineID,
            opts
        )
    }

    /**
     * Destroy the engine assigned to the activity or fragment with a [screenID].
     */
    fun releaseEngineForScreenID(screenID: String) {
        val engineID = assignedEngineIDs[screenID]
        engineID?.let { eid ->
            assignedEngineIDs.remove(key = screenID)
            FlutterEngineCache.getInstance().get(eid)?.let {
                FlutterEngineCache.getInstance().remove(eid)
                it.destroy()
            }
        }
    }

    /**
     * Checks if [engineGroup] is not initialized.
     */
    private fun <T> checkNotInitializedError(then: FlutterEngineGroup.() -> T): T {
        val eg = engineGroup
        eg ?: throw Error.NotInitialized
        return eg.then()
    }

    /**
     * Checks if [engineGroup] and initialized engine were already initialized.
     */
    private fun <T> checkMultipleInitializationsError(then: () -> T): T {
        val eg = engineGroup
        return if (eg != null) {
            val engine = FlutterEngineCache.getInstance().get(initializerEngineID)
            if (engine != null) {
                throw Error.MultipleInitializations
            } else {
                throw Error.Unexpected
            }
        } else {
            then()
        }
    }

    /**
     * Checks if the [engineID] used for engine configuration is already being used for a running
     * engine.
     */
    private fun checkEngineIDAlreadyUsedError(engineID: String) {
        if (FlutterEngineCache.getInstance().get(engineID) != null) {
            throw Error.EngineIDAlreadyUsed(engineID)
        }
    }

    /**
     * Initializes and runs the initializer engine to warm up resources for later engine startups.
     */
    private fun FlutterEngineGroup.initialize(context: Context) = cacheAndRunNewEngineToGetID(
        context,
        screenID = initializerScreenID,
        engineID = initializerEngineID
    )

    /**
     * Creates and runs a new engine with the provided [opts].
     * If no engineID is provided, a random UUID is used instead.
     * A link between the [screenID] and the [engineID] is stored into [assignedEngineIDs] to make
     * possible to retrieve an engineID starting from the screenID of the screen requesting it.
     * If the engineID is equal to [initializerEngineID] no screenID-engineID link is stored.
     */
    private fun FlutterEngineGroup.cacheAndRunNewEngineToGetID(
        context: Context,
        screenID: String,
        engineID: String? = null,
        opts: Options? = null
    ): String {
        engineID?.let { checkEngineIDAlreadyUsedError(it) }

        val options = FlutterEngineGroup.Options(context).apply {
            dartEntrypoint = DartExecutor.DartEntrypoint.createDefault()
            dartEntrypointArgs = opts?.arguments ?: listOf()
            initialRoute = opts?.initialRoute
        }

        val engine = createAndRunEngine(options)
        val newEngineID = engineID ?: UUID.randomUUID().toString()

        if (screenID != initializerScreenID) {
            assignedEngineIDs[screenID] = newEngineID
        }

        FlutterEngineCache.getInstance().put(newEngineID, engine)
        return newEngineID
    }

    /**
     * Options to configure an engine and run the Flutter app on the main entrypoint.
     * Override [initialRoute] to initial route of the Flutter app.
     * Override [arguments] that will be passed to the Flutter entrypoint "main" function.
     */
    open class Options {
        open val initialRoute: String? = null
        open val arguments: List<String>? = null
    }

    /**
     * Set of errors that FlutterBox may throw.
     */
    sealed class Error : java.lang.Exception() {

        object MultipleInitializations : Error() {
            override val message: String = "Initialize $className only once."
        }

        object NotInitialized : Error() {
            override val message: String = "$className should be initialized before using it. " +
                    "Call $className.${FlutterBox::initialize.name} before opening a Flutter screen."
        }

        data class EngineIDAlreadyUsed(val engineID: String) : Error() {
            override val message: String =
                "Engine ID \"$engineID\" already used. Make sure to always provide distinct engine IDs."
        }

        object Unexpected : Error() {
            override val message: String = "Unexpected error."
        }
    }
}