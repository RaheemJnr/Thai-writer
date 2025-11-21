package com.rjnr.thaiwrter.utils

import android.util.Log

class TelemetryLogger {
    fun logEvent(name: String, attributes: Map<String, Any?> = emptyMap()) {
        Log.d("Telemetry", buildMessage(name, attributes))
    }

    fun logError(
            name: String,
            throwable: Throwable? = null,
            attributes: Map<String, Any?> = emptyMap()
    ) {
        Log.e("Telemetry", buildMessage(name, attributes), throwable)
    }

    private fun buildMessage(name: String, attributes: Map<String, Any?>): String {
        if (attributes.isEmpty()) return name
        val payload = attributes.entries.joinToString { "${it.key}=${it.value}" }
        return "$name | $payload"
    }
}
