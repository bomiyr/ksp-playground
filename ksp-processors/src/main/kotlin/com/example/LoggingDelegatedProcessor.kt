package com.example

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated

class LoggingDelegatedProcessor(
    val delegate: SymbolProcessor,
    environment: SymbolProcessorEnvironment,
) : SymbolProcessor {

    val logger = environment.logger
    val tag = delegate::class.qualifiedName ?: ""
    private var roundCount: Int = 0


    override fun finish() {
        logger.warn("$tag: finish")
        delegate.finish()
        logger.warn("$tag: finish completed")
    }

    override fun onError() {
        logger.warn("$tag: onError")
        delegate.onError()
        logger.warn("$tag: onError completed")
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("$tag: process (round ${++roundCount})")
        val postpone = delegate.process(resolver)
        logger.warn("$tag: process completed (round $roundCount). Postpone: $postpone")
        return postpone
    }
}