package entities.place

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
class Place(
    var name: String = "",
    var city: String = ""
) {

    @Id
    var id : UUID = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Place

        if (name != other.name) return false
        if (city != other.city) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    companion object
}