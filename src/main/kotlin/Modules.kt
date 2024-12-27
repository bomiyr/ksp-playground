package com.example

@Module
class ExistingModule

@AutoModule
class SimpleAutoModule

@GenerateAutoModule
class ComplexAutoModule

@Component(
    [ExistingModule::class, GeneratedModule::class]
)
interface SourceComponent

