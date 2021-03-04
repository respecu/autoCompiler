package ohrae.biz

import java.io.BufferedReader

class Executor {
    var isRunning = false

    private var tasks: ArrayList<Task> = ArrayList()
    private var index: Int = -1

    fun run(command: Command) {
        val thread = CommandTask(command)
        tasks.add(thread)
        next()
    }

    fun run(runnable: Runnable) {
        val thread = RunnableTask(runnable)
        tasks.add(thread)
        next()
    }

    fun next() {
        synchronized(tasks) {
            if (index == -1 || !tasks[index].isRunning()) {
                if (index < tasks.size - 1) {
                    tasks[++index]._start()
                    isRunning = true
                } else {
                    isRunning = false
                }
            }
        }
    }

    fun clear() {
        synchronized(tasks) {
            if (index >= 0 && tasks[index].isRunning()) {
                tasks[index]._join()
            }
            tasks.clear()
        }
    }

    interface Task {
        fun isRunning(): Boolean
        fun _start()
        fun _join()
    }

    inner class CommandTask(val command: Command) : Thread(), Task {
        var running: Boolean = false
        var reader: BufferedReader? = null
        var line: String? = null

        override fun run() {
            running = true;
            reader = command.start()?.bufferedReader()
            println("$ANSI_GREEN${command.string}$ANSI_RESET")
            line = reader?.readLine()
            val reader = reader ?: return
            while (line != null) {
                line?.let {
                    if (it.isNotEmpty()) {
                        println("$ANSI_YELLOW${it}$ANSI_RESET")
                    }
                }
                line = reader.readLine()
            }
            running = false
            next()
        }

        override fun isRunning(): Boolean {
            return running
        }

        override fun _start() {
            start()
        }

        override fun _join() {
            join()
        }
    }

    inner class RunnableTask(val runnable: Runnable) : Thread(), Task {
        var running: Boolean = false

        override fun run() {
            running = true
            runnable.run()
            running = false
            next()
        }

        override fun isRunning(): Boolean {
            return running
        }

        override fun _start() {
            start()
        }

        override fun _join() {
            join()
        }
    }
}