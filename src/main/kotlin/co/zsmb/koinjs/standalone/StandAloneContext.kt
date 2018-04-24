package co.zsmb.koinjs.standalone

import co.zsmb.koinjs.ContextCallback
import co.zsmb.koinjs.Koin
import co.zsmb.koinjs.KoinContext
import co.zsmb.koinjs.core.bean.BeanRegistry
import co.zsmb.koinjs.core.instance.InstanceFactory
import co.zsmb.koinjs.core.property.PropertyRegistry
import co.zsmb.koinjs.dsl.module.Module
import co.zsmb.koinjs.error.AlreadyStartedException

/**
 * Koin agnostic context support
 * @author - Arnaud GIULIANI
 */
object StandAloneContext {

    private var isStarted = false

    /**
     * Koin Context
     */
    lateinit var koinContext: StandAloneKoinContext

    /**
     * Load Koin modules - whether Koin is already started or not
     * allow late module definition load (e.g: libraries ...)
     *
     * @param modules : List of Module
     */
    fun loadKoinModules(vararg modules: Module): Koin = synchronized(this) {
        createContextIfNeeded()
        return KoinContext().build(modules.toList())
    }

    /**
     * Load Koin modules - whether Koin is already started or not
     * allow late module definition load (e.g: libraries ...)
     *
     * @param modules : List of Module
     */
    fun loadKoinModules(modules: List<Module>): Koin = synchronized(this) {
        createContextIfNeeded()
        return KoinContext().build(modules)
    }

    /**
     * Get koin context
     */
    private fun KoinContext() = Koin(koinContext as KoinContext)

    /**
     * Create Koin context if needed :)
     */
    private fun createContextIfNeeded() = synchronized(this) {
        if (!isStarted) {
            Koin.logger.log("[context] create")
            val beanRegistry = BeanRegistry()
            val propertyResolver = PropertyRegistry()
            val instanceFactory = InstanceFactory()
            koinContext = KoinContext(beanRegistry, propertyResolver, instanceFactory)
            isStarted = true
        }
    }

    /**
     * Register Context callbacks
     * @see ContextCallback - Context CallBack
     */
    fun registerContextCallBack(contextCallback: ContextCallback) {
        Koin.logger.log("[context] callback registering with $contextCallback")
        KoinContext().koinContext.contextCallback = contextCallback
    }

    /**
     * Load Koin properties - whether Koin is already started or not
     * Will look at koin.properties file
     *
     * @param extraProperties - additional properties
     */
    fun loadProperties(
            extraProperties: Map<String, Any> = HashMap()
    ): Koin = synchronized(this) {
        createContextIfNeeded()

        val koin = KoinContext()

        if (extraProperties.isNotEmpty()) {
            Koin.logger.log("[properties] load extras properties : ${extraProperties.size}")
            koin.bindAdditionalProperties(extraProperties)
        }

        return koin
    }

    /**
     * Koin starter function to load modules and extraProperties
     * Throw AlreadyStartedException if already started
     * @param list : Modules
     * @param extraProperties - extra extraProperties
     */
    fun startKoin(
            list: List<Module>,
            extraProperties: Map<String, Any> = HashMap()
    ): Koin {
        if (isStarted) {
            throw AlreadyStartedException("Koin is already started. Run startKoin only once or use loadKoinModules")
        }
        createContextIfNeeded()
        loadKoinModules(list)
        loadProperties(extraProperties)
        return KoinContext()
    }

    /**
     * Close actual Koin context
     */
    fun closeKoin() = synchronized(this) {
        if (isStarted) {
            // Close all
            (koinContext as KoinContext).close()
            isStarted = false
        }
    }
}

/**
 * Stand alone Koin context
 */
interface StandAloneKoinContext