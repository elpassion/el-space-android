package pl.elpassion.elspace.common

object CurrentTimeProvider : Provider<Long>({ System.currentTimeMillis() })