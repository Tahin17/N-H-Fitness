package com.aegisfit.app.domain.usecase.biometrics

import com.aegisfit.app.domain.model.AsymmetryResult
import com.aegisfit.app.domain.model.AsymmetrySeverity
import com.aegisfit.app.domain.model.BodyMeasurement
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

class DetectAsymmetryUseCase @Inject constructor() {
    operator fun invoke(measurement: BodyMeasurement): List<AsymmetryResult> {
        val results = mutableListOf<AsymmetryResult>()

        fun checkPair(bodyPart: String, left: Double?, right: Double?) {
            if (left != null && right != null) {
                val differenceCm = abs(left - right)
                val denominator = max(left, right)
                if (!left.isFinite() || !right.isFinite() || denominator <= 0.0) return
                val differencePercent = (differenceCm / denominator) * 100.0
                val severity = when {
                    differencePercent <= 3.0 -> AsymmetrySeverity.Normal
                    differencePercent <= 8.0 -> AsymmetrySeverity.Warning
                    else -> AsymmetrySeverity.Alert
                }
                results.add(
                    AsymmetryResult(
                        bodyPart = bodyPart,
                        leftCm = left,
                        rightCm = right,
                        differenceCm = differenceCm,
                        differencePercent = differencePercent,
                        severity = severity
                    )
                )
            }
        }

        checkPair("Bicep", measurement.leftBicepCm, measurement.rightBicepCm)
        checkPair("Forearm", measurement.leftForearmCm, measurement.rightForearmCm)
        checkPair("Quad", measurement.leftQuadCm, measurement.rightQuadCm)
        checkPair("Calf", measurement.leftCalfCm, measurement.rightCalfCm)

        return results
    }
}
