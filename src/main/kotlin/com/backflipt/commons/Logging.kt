package com.backflipt.commons

import mu.KLogger
import mu.KotlinLogging

/**
 * Creates logger for calling class
 *
 * @return returns the KLogger
 */
fun getLogger(): KLogger {
    return getLogger(Thread.currentThread().stackTrace[2].className)
}

/**
 * Creates logger for given name
 *
 * @param name name for the logger
 * @return returns KLogger with given name
 */
fun getLogger(name: String) = KotlinLogging.logger(name)
