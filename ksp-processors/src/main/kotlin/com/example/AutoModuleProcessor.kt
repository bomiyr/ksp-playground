package com.example

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile

class AutoModuleProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        LoggingDelegatedProcessor(AutoModuleProcessor(environment), environment)
}

class AutoModuleProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val logger = environment.logger
    private val codeGenerator = environment.codeGenerator

    private val classes = mutableSetOf<String>()
    private val dependencies = mutableSetOf<KSFile>()

    private var rounds = 0

    override fun process(resolver: Resolver): List<KSAnnotated> {
        rounds++
        val symbols = resolver.getSymbolsWithAnnotation(requireNotNull(AutoModule::class.qualifiedName))

        val classDeclarations = symbols.filterIsInstance<KSClassDeclaration>()

        dependencies.addAll(
            classDeclarations.mapNotNull { it.containingFile }
        )
        val classNames = classDeclarations.map { requireNotNull(it.qualifiedName).asString() }
        logger.warn("AutoModuleProcessor: ${classNames.joinToString()}")
        classes.addAll(classNames)

        if (rounds == 2) {
            generateFile()
        }

        return emptyList()
    }

    override fun finish() {
//        generateFile()
    }

    private fun generateFile() {
        logger.warn("AutoModuleProcessor: generateFile")
        val deps = Dependencies(true, *dependencies.toTypedArray())
        codeGenerator.createNewFile(deps, "com.example", "GeneratedModule", "kt")
            .use {
                val writer = it.writer()
                writer.write(
                    """
                        package com.example
                        
                        @Module(
                            [
                                ${classes.joinToString { "$it::class" }}
                            ]
                        )
                        class GeneratedModule
                    """.trimIndent()
                )
                writer.flush()
            }

    }
}
