package com.example

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class ModuleProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        LoggingDelegatedProcessor(GenerateAutoModuleProcessor(environment), environment)
}

class GenerateAutoModuleProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val codeGenerator = environment.codeGenerator

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(requireNotNull(GenerateAutoModule::class.qualifiedName))

        val postpone = mutableListOf<KSClassDeclaration>()
        symbols.filterIsInstance<KSClassDeclaration>().forEach { cls ->
            generate(cls)
        }
        return postpone
    }

    private fun generate(cls: KSClassDeclaration) {
        val deps = Dependencies(false, requireNotNull(cls.containingFile))
        val className = "Generated${cls.simpleName.asString()}"
        codeGenerator.createNewFile(deps, "com.example", className, "kt")
            .use {
                val writer = it.writer()
                writer.write(
                    """
                        package com.example
                        
                        @Module
                        @AutoModule
                        class $className
                    """.trimIndent()
                )
                writer.flush()
            }

    }
}
