package com.MatteV02.EHRforEquipes.commonModule.entities.visit

import com.MatteV02.EHRforEquipes.commonModule.entities.place.Place
import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
class Visit(
    var date: LocalDate = LocalDate.now(),
    @ManyToOne var patient: Patient? = null,
    @ManyToOne var place: Place? = null,
    var specialist: String = "",
    var type: VisitType = VisitType.NORMAL_VISIT,
    var anamnesis: String = "",
    var objectiveExam: String = "",
    @ElementCollection  val exams: MutableList<Exam> = mutableListOf(),
    var indications: String = "",
    var nextSteps: String = "",
    @Column(length = 1000)
    var letterText: String = ""
) {
    @Id
    var id : UUID = UUID.randomUUID()

    fun addExam(exam: Exam) {
        exams.add(exam)
    }

    fun removeExam(exam: Exam) {
        exams.remove(exam)
    }

    companion object {
        enum class VisitType {
            NORMAL_VISIT,
            CHECKUP_VISIT;
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Visit

        if (date != other.date) return false
        if (patient != other.patient) return false
        if (place != other.place) return false
        if (specialist != other.specialist) return false
        if (type != other.type) return false
        if (anamnesis != other.anamnesis) return false
        if (objectiveExam != other.objectiveExam) return false
        exams.forEachIndexed { i, exam ->
            if (exam != other.exams[i]) return false
        }
        if (indications != other.indications) return false
        if (nextSteps != other.nextSteps) return false
        if (letterText != other.letterText) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + (patient?.hashCode() ?: 0)
        result = 31 * result + (place?.hashCode() ?: 0)
        result = 31 * result + specialist.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + anamnesis.hashCode()
        result = 31 * result + objectiveExam.hashCode()
        result = 31 * result + exams.hashCode()
        result = 31 * result + indications.hashCode()
        result = 31 * result + nextSteps.hashCode()
        result = 31 * result + letterText.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}