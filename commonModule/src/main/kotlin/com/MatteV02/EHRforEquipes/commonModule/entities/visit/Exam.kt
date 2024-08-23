package com.MatteV02.EHRforEquipes.commonModule.entities.visit

import jakarta.persistence.Embeddable
import java.time.LocalDate

@Embeddable
class Exam(
    var date: LocalDate = LocalDate.now(),
    var type: ExamType = ExamType.TYPE1,
    var description: String = "",
    var diagnosis: String = ""
) {
    companion object {
        enum class ExamType {
            TYPE1,
            TYPE2;
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Exam

        if (date != other.date) return false
        if (type != other.type) return false
        if (description != other.description) return false
        if (diagnosis != other.diagnosis) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + diagnosis.hashCode()
        return result
    }
}