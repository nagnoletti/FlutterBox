package com.github.nagnoletti.android.flutterbox

import android.content.Context
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor
import java.util.*

/**
 * FlutterBox
 */
object FlutterBox {
    private val className: String get() = FlutterBox::javaClass.name

    private const val initializerEngineID = "FlutterBox.initializerEngineID"
    private const val initializerScreenID = "FlutterBox.initializerScreenID"

    private var engineGroup: FlutterEngineGroup? = null

    private val assignedEngineIDs = mutableMapOf<String, String>()

    fun initialize(context: Context) {
        checkMultipleInitializationsError {
            val eg = FlutterEngineGroup(context)
            engineGroup = eg
            eg.initialize(context)
        }
    }

    fun getOwnOrNewEngineID(
        context: Context,
        screenID: String,
        engineID: String? = null,
        opts: Options? = null
    ): String = checkNotInitializedError {
        cacheAndRunNewEngineToGetID(context, screenID, engineID, opts)
    }

    private fun <T> checkNotInitializedError(then: FlutterEngineGroup.() -> T): T {
        val eg = engineGroup
        eg ?: throw Error.NotInitialized
        return eg.then()
    }

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

    private fun checkEngineIDAlreadyUsedError(engineID: String) {
        if (FlutterEngineCache.getInstance().get(engineID) != null) {
            throw Error.EngineIDAlreadyUsed(engineID)
        }
    }

    private fun FlutterEngineGroup.initialize(context: Context) = cacheAndRunNewEngineToGetID(
        context,
        screenID = initializerScreenID,
        engineID = initializerEngineID
    )

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

    open class Options {
        open val initialRoute: String? = null
        open val arguments: List<String>? = null
    }

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