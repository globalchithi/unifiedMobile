package com.vaxcare.unifiedhub.library.vaxjob.provider

import com.vaxcare.unifiedhub.core.common.provider.JobRunner

abstract class BaseVaxJobProvider : VaxJobProvider, JobRunner {
    private val map: MutableMap<String, Any?> = mutableMapOf()

    override fun withArguments(vararg args: Pair<String, Any?>): VaxJobProvider =
        apply {
            args.forEach { map[it.first] = it.second }
        }

    override fun runJob() {
        runJobWithArgs(map)
    }
}
