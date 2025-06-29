package com.sync.sprout.infrastructure.insurance

import com.sync.sprout.domain.command.EnrollCommand
import com.sync.sprout.domain.model.InsuranceModel
import com.sync.sprout.infrastructure.insurance.request.EnrollRequest
import com.sync.sprout.support.enum.PolicyStatus
import org.springframework.stereotype.Component


@Component
class InsuranceClient {

    fun checkCountry(
        country: String,
    ): Boolean {
        TODO("call [GET] https://kakao-insurance-api.com/v1/subscribable/countries/{country-code}")
    }

    fun getPolicyStatus(
        policyNumber: String,
    ): PolicyStatus {
        TODO("call [GET] https://kakao-insurance-api.com/v1/policies/{policy-number}")
    }

    fun enroll(
        command: EnrollCommand
    ): String {
        val request = EnrollRequest.of(command = command)
        TODO("call [POST] https://kakao-insurance-api.com/v1/insurance with EnrollRequest")
    }

    fun getInsurance(
        policyNumber: String,
    ): InsuranceModel {
        TODO("call [GET] https://kakao-insurance-api.com/v1/insurance")
    }
}