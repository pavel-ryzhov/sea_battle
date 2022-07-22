package com.example.sea_battle.utils

import com.example.sea_battle.entities.Task
import java.io.BufferedOutputStream
import java.net.Socket
import java.util.concurrent.locks.ReentrantLock

class SpecialBufferedWriter(socket: Socket) {
    companion object {
        private val reentrantLock = ReentrantLock()
    }

    private val writer = BufferedOutputStream(socket.getOutputStream())

    fun writeString(string: String) {
        reentrantLock.lock()
        try {
            writer.write("$string###".toByteArray())
        }finally {
            reentrantLock.unlock()
        }
    }

    fun writeStringAndFlush(string: String) {
        reentrantLock.lock()
        try {
            writer.write("$string###".toByteArray())
            writer.flush()
        }finally {
            reentrantLock.unlock()
        }
    }

    fun writeTask(task: Task) {
        reentrantLock.lock()
        try {
            writer.apply {
                write("${task.tag}\n".toByteArray())
                write(task.data)
                write("###".toByteArray())
                flush()
            }
        }finally {
            reentrantLock.unlock()
        }
    }
}