package com.example

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Module(val subModules: Array<KClass<*>> = [])

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class GenerateAutoModule

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class AutoModule

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Component(val modules: Array<KClass<*>> = [])