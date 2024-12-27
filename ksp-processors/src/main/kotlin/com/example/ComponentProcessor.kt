package com.example

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate

class ComponentProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        LoggingDelegatedProcessor(ComponentProcessor(environment), environment)
}

class ComponentProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(requireNotNull(Component::class.qualifiedName))
        val postpone = mutableListOf<KSClassDeclaration>()
        symbols.filterIsInstance<KSClassDeclaration>().forEach { cls ->
            if (!cls.validate()) {
                logger.warn("ComponentProcessor: ${cls.qualifiedName?.asString()} is not valid")
                postpone.add(cls)
            } else {
                val annotation = cls.annotations.first { it.shortName.getShortName() == "Component" }
                val modules = annotation.arguments.first { it.name?.getShortName() == "modules" }
                val modulesDeclarations = (modules.value as List<KSType?>)
                    .mapNotNull { it?.declaration }
                    .filterIsInstance<KSClassDeclaration>()
                generate(cls, modulesDeclarations)
            }
        }
        return postpone
    }

    private fun generate(cls: KSClassDeclaration, modulesDeclarations: List<KSClassDeclaration>) {
        val deps = Dependencies(false, requireNotNull(cls.containingFile))
        val className = "Generated${cls.simpleName.asString()}"
        codeGenerator.createNewFile(deps, "com.example", className, "kt")
            .use { outputStream ->
                val writer = outputStream.writer()
                writer.write(
                    """
                        package com.example
                        
                        class $className
                        // generated for ${modulesDeclarations.joinToString { requireNotNull(it.qualifiedName).asString() }}
                    """.trimIndent()
                )
                writer.flush()
            }
    }
}
