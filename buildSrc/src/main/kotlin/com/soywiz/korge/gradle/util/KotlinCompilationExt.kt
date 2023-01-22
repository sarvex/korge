package com.soywiz.korge.gradle.util

import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.*

val KotlinCompilation<*>.allKotlinSourceSetsFix: kotlin.collections.Set<KotlinSourceSet>
    get() = this.dyn["allKotlinSourceSets"].casted()

val AbstractKotlinCompilation<*>.allKotlinSourceSetsFix: kotlin.collections.Set<KotlinSourceSet>
    get() = this.dyn["allKotlinSourceSets"].casted()
