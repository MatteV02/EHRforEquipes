package entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.hibernate.annotations.processing.CheckHQL
import java.time.LocalDate
import java.util.*

@CheckHQL

@Entity
class Patient(
    var name: String = "",
    var gender: Gender = Gender.MALE,
    var dateOfBirth: LocalDate = LocalDate.now(),
    var placeOfBirth: String = "",
    var residence: String = "",
    var FC: String = "",
    var phoneNumber: String = "",
    var landlinePhoneNumber: String = "",
    var mail: String = "",
    var doctor: String = "",
    //@OneToMany val visits: MutableList<Visit> = mutableListOf()
) {

    @Id @GeneratedValue
    lateinit var id: UUID

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Patient

        if (name != other.name) return false
        if (gender != other.gender) return false
        if (dateOfBirth != other.dateOfBirth) return false
        if (placeOfBirth != other.placeOfBirth) return false
        if (residence != other.residence) return false
        if (FC != other.FC) return false
        if (phoneNumber != other.phoneNumber) return false
        if (landlinePhoneNumber != other.landlinePhoneNumber) return false
        if (mail != other.mail) return false
        if (doctor != other.doctor) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + dateOfBirth.hashCode()
        result = 31 * result + placeOfBirth.hashCode()
        result = 31 * result + residence.hashCode()
        result = 31 * result + FC.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + landlinePhoneNumber.hashCode()
        result = 31 * result + mail.hashCode()
        result = 31 * result + doctor.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    companion object {
        enum class Gender {
            MALE, FEMALE;
            override fun toString(): String {
                return when (this) {
                    MALE -> "M"
                    FEMALE -> "F"
                }
            }
        }
    }
}