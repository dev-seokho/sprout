package com.sync.sprout.application

import com.sync.sprout.domain.command.CheckAvailabilityCommand
import com.sync.sprout.domain.command.CheckCountriesCommand
import com.sync.sprout.domain.command.EnrollCommand
import com.sync.sprout.infrastructure.insurance.InsuranceClient
import com.sync.sprout.infrastructure.notification.NotificationClient
import com.sync.sprout.support.enum.PolicyStatus
import com.sync.sprout.support.util.coroutine.getOrThrow
import com.sync.sprout.support.util.coroutine.pmap
import com.sync.sprout.support.util.coroutine.withAsync
import com.sync.sprout.support.util.coroutine.withBlocking
import com.sync.sprout.support.util.coroutine.withLaunch
import kotlinx.coroutines.Dispatchers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class EnrollService(
    private val insuranceClient: InsuranceClient,
    private val notificationClient: NotificationClient
){
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun checkCountries(
        command: CheckCountriesCommand
    ): Boolean {
        return withBlocking(Dispatchers.IO) {
            command.countries.pmap {
                insuranceClient.checkCountry(country = it)
            }.getOrThrow()
                .all { it }
        }
    }

    fun checkAvailability(
        command: CheckAvailabilityCommand
    ): Boolean {
        return try {
            withBlocking(Dispatchers.IO) {
                val deferredCheckCountry = withAsync {
                    command.countries.pmap { insuranceClient.checkCountry(country = it) }
                        .getOrThrow()
                        .all { it }
                }

                val deferredPolicyStatus = withAsync {
                    insuranceClient.getPolicyStatus(policyNumber = command.policyNumber)
                }

                val checkCountry = deferredCheckCountry.await()
                val policyStatus = deferredPolicyStatus.await()

                when {
                    policyStatus != PolicyStatus.ACTIVE && checkCountry -> true
                    else -> false
                }
            }

        } catch (exception: Exception) {
            logger.warn("Failed to check availability", exception.message)
            false
        }
    }

    fun enroll(
        command: EnrollCommand
    ) {
        runCatching {
            insuranceClient.enroll(command = command)
        }.onFailure { exception ->
            logger.error("Failed to enroll", exception.message)
        }.onSuccess {
            val insurance = insuranceClient.getInsurance(policyNumber = it)

            withBlocking(Dispatchers.IO) {
                withLaunch {
                    notificationClient.sendEnrollEmail(insurance = insurance)
                }

                withLaunch {
                    notificationClient.sendEnrollMessage(insurance = insurance)
                }
            }
        }.getOrThrow()
    }
}