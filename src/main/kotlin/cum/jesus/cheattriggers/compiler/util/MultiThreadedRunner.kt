package cum.jesus.cheattriggers.compiler.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object MultiThreadedRunner {
    private var threadPool: ExecutorService? = null

    fun addTask(task: () -> Unit) {
        if (threadPool == null) return
        threadPool!!.submit(Task(task))
    }

    fun shutdown() {
        if (threadPool == null) return
        threadPool!!.shutdown()
    }

    fun waitForFinish() {
        if (threadPool == null) return
        threadPool!!.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    }

    fun new(threadAmount: Int) {
        if (threadPool != null) destroy()
        threadPool = Executors.newFixedThreadPool(threadAmount)
    }

    fun destroy() {
        if (threadPool == null) return
        shutdown()
        waitForFinish()

        threadPool = null
    }

    private class Task(val task: () -> Unit) : Runnable {
        override fun run() {
            task()
        }
    }
}