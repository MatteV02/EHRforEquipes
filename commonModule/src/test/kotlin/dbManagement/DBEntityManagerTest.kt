package dbManagement

import dbManagement.DBEntityManager.ConnectionTypes
import entities.patient.Patient
import entities.place.Place
import entities.visit.Exam
import entities.visit.Visit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import kotlin.random.Random
import kotlin.random.nextInt


class DBEntityManagerTest : FunSpec({

    val dbEntityManager: DBEntityManager = SQLDBEntityManager

    context("patient") {
        test("insert and get") {

            dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)

            val p1 = Patient.testPatientRandom(1)

            dbEntityManager.insert(p1)
            dbEntityManager.getPatients() shouldBe listOf(p1)

            val p2to10 = (2..10).map { Patient.testPatientRandom(it) }
            p2to10.forEach { p ->
                dbEntityManager.insert(p)
            }

            val expectedList = mutableListOf<Patient>()
            expectedList.add(p1)
            expectedList.addAll(p2to10)

            dbEntityManager.getPatients() shouldBe expectedList
        }

        test("update and get") {
            dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)

            val p1 = Patient.testPatientRandom(1)

            dbEntityManager.insert(p1)

            p1.testPatientRandomChange(1)
            dbEntityManager.update(p1)

            dbEntityManager.getPatients() shouldBe listOf(p1)

            val p2to10 = (2..10).map { Patient.testPatientRandom(it) }
            p2to10.forEachIndexed { i, p ->
                dbEntityManager.insert(p)

                p.testPatientRandomChange(i + 2)
                dbEntityManager.update(p)
            }

            val expectedList = mutableListOf<Patient>()
            expectedList.add(p1)
            expectedList.addAll(p2to10)

            dbEntityManager.getPatients() shouldBe expectedList
        }

        test("delete and get") {
            dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)

            val p1 = Patient.testPatientRandom(1)
            dbEntityManager.insert(p1)

            dbEntityManager.remove(p1)

            dbEntityManager.getPatients() shouldBe emptyList()

            val p1to10 = (1..10).map { Patient.testPatientRandom(it) }.toMutableList()
            p1to10.forEach { dbEntityManager.insert(it) }

            val iterator = p1to10.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                iterator.remove()
                dbEntityManager.remove(p)

                dbEntityManager.getPatients() shouldBe p1to10
            }
        }
    }

    context("place") {
        test("insert and get") {
            dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)

            val p1 = Place.testPlaceRandom(1)
            dbEntityManager.insert(p1)

            dbEntityManager.getPlaces() shouldBe listOf(p1)

            val p2to10 = (2..10).map { Place.testPlaceRandom(it) }
            p2to10.forEach { dbEntityManager.insert(it) }

            val expectedPlaces = mutableListOf<Place>()
            expectedPlaces.add(p1)
            expectedPlaces.addAll(p2to10)

            dbEntityManager.getPlaces() shouldBe expectedPlaces
        }

        test("update and get") {
            dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)

            val p1 = Place.testPlaceRandom(1)
            dbEntityManager.insert(p1)

            p1.testPlaceRandomChange(1)
            dbEntityManager.update(p1)

            dbEntityManager.getPlaces() shouldBe listOf(p1)

            val p2to10 = (2..10).map { Place.testPlaceRandom(it) }
            p2to10.forEachIndexed { i, p ->
                dbEntityManager.insert(p)

                p.testPlaceRandomChange(i + 2)
                dbEntityManager.update(p)
            }

            val expectedPlaces = mutableListOf<Place>()
            expectedPlaces.add(p1)
            expectedPlaces.addAll(p2to10)

            dbEntityManager.getPlaces() shouldBe expectedPlaces
        }

        test("remove and get") {
            dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)

            val p1 = Place.testPlaceRandom(1)
            dbEntityManager.insert(p1)

            dbEntityManager.remove(p1)

            dbEntityManager.getPlaces() shouldBe emptyList()

            val p1to10 = (1..10).map { Place.testPlaceRandom(it) }.toMutableList()
            p1to10.forEach { dbEntityManager.insert(it) }

            val iterator = p1to10.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                iterator.remove()
                dbEntityManager.remove(p)

                dbEntityManager.getPlaces() shouldBe p1to10
            }
        }
    }

    context("visit") {
        test("insert and get") {
            dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)

            val v1 = Visit.testVisitRandom()
            dbEntityManager.insert(v1)

            dbEntityManager.getVisits() shouldBe listOf(v1)

            val v2to10 = (2..10).map { Visit.testVisitRandom() }
            v2to10.forEach { dbEntityManager.insert(it) }

            val expectedVisits = mutableListOf<Visit>()
            expectedVisits.add(v1)
            expectedVisits.addAll(v2to10)

            dbEntityManager.getVisits() shouldBe expectedVisits
        }

        test("update and get") {
            dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)

            val v1 = Visit.testVisitRandom()
            dbEntityManager.insert(v1)

            v1.testVisitRandomChange()
            dbEntityManager.update(v1)

            dbEntityManager.getVisits() shouldBe listOf(v1)

            val v2to10 = (2..10).map { Visit.testVisitRandom() }
            v2to10.forEach { dbEntityManager.insert(it) }

            val expectedVisits = mutableListOf<Visit>()
            expectedVisits.add(v1)
            expectedVisits.addAll(v2to10)

            dbEntityManager.getVisits() shouldBe expectedVisits
        }

        test("remove and get") {
            dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)

            val v1 = Visit.testVisitRandom()
            dbEntityManager.insert(v1)

            dbEntityManager.remove(v1)

            dbEntityManager.getVisits() shouldBe emptyList()

            val v1to10 = (1..10).map { Visit.testVisitRandom() }.toMutableList()
            v1to10.forEach { dbEntityManager.insert(it) }

            val iterator = v1to10.iterator()
            while (iterator.hasNext()) {
                val v = iterator.next()
                iterator.remove()
                dbEntityManager.remove(v)

                dbEntityManager.getVisits() shouldBe v1to10
            }
        }
    }

    context("patient, place visit") {
        lateinit var patient1: Patient
        lateinit var patient2: Patient
        lateinit var patient3: Patient
        lateinit var patient4: Patient
        lateinit var patient5: Patient
        lateinit var patients: MutableList<Patient>

        lateinit var place1: Place
        lateinit var place2: Place
        lateinit var place3: Place
        lateinit var place4: Place
        lateinit var place5: Place
        lateinit var places: MutableList<Place>

        lateinit var visit1: Visit
        lateinit var visit2: Visit
        lateinit var visit3: Visit
        lateinit var visit4: Visit
        lateinit var visit5: Visit
        lateinit var visits: MutableList<Visit>

        beforeEach {
            patient1 = Patient.testPatientRandom(1)
            patient2 = Patient.testPatientRandom(2)
            patient3 = Patient.testPatientRandom(3)
            patient4 = Patient.testPatientRandom(4)
            patient5 = Patient.testPatientRandom(5)
            patients = mutableListOf(patient1, patient2, patient3, patient4, patient5)

            place1 = Place.testPlaceRandom(1)
            place2 = Place.testPlaceRandom(2)
            place3 = Place.testPlaceRandom(3)
            place4 = Place.testPlaceRandom(4)
            place5 = Place.testPlaceRandom(5)
            places = mutableListOf(place1, place2, place3, place4, place5)

            visit1 = Visit.testVisitRandom()
            visit2 = Visit.testVisitRandom()
            visit3 = Visit.testVisitRandom()
            visit4 = Visit.testVisitRandom()
            visit5 = Visit.testVisitRandom()
            visits = mutableListOf(visit1, visit2, visit3, visit4, visit5)

            /*
            visit1 -> patient1, place1
            visit2 -> patient1, place2
            visit3 -> patient2, place1
            visit4 -> patient3, null
            visit5 -> null    , place3
             */
            visit1.patient = patient1
            visit1.place = place1

            visit2.patient = patient1
            visit2.place = place2

            visit3.patient = patient2
            visit3.place = place1

            visit4.patient = patient3

            visit5.place = place3

            dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)
            val elements = listOf(patients, places, visits)
            elements.forEach { list ->
                list.forEach { o ->
                    dbEntityManager.insert(o)
                }
            }
        }

        test("visit changes patient") {
            /*
            visit1 -> patient5, place1
             */
            visit1.patient = patient5
            dbEntityManager.update(visit1)

            dbEntityManager.getVisits() shouldBe visits
            dbEntityManager.getPlaces() shouldBe places
            dbEntityManager.getPatients() shouldBe patients
        }

        test("visit changes place") {
            /*
            visit2 -> patient1, place5
             */
            visit2.place = place5
            dbEntityManager.update(visit2)

            dbEntityManager.getVisits() shouldBe visits
            dbEntityManager.getPlaces() shouldBe places
            dbEntityManager.getPatients() shouldBe patients
        }

        test("remove patient with visits") {
            /*
            remove patient1 -> remove visit1, visit2
             */
            patients.remove(patient1)
            visits.remove(visit1)
            visits.remove(visit2)
            dbEntityManager.remove(patient1)

            dbEntityManager.getVisits() shouldBe visits
            dbEntityManager.getPlaces() shouldBe places
            dbEntityManager.getPatients() shouldBe patients
        }

        test("remove place with visits") {
            /*
            remove place1 -> remove visit1, visit3
             */
            places.remove(place1)
            visits.remove(visit1)
            visits.remove(visit3)
            dbEntityManager.remove(place1)

            dbEntityManager.getVisits() shouldBe visits
            dbEntityManager.getPlaces() shouldBe places
            dbEntityManager.getPatients() shouldBe patients
        }
    }
})

private fun Visit.testVisitRandomChange() {
    date = getRandomLocalDate()
    specialist = getRandomString(16)
    type = Visit.Companion.VisitType.entries.random()
    anamnesis = getRandomString(100)
    objectiveExam = getRandomString(100)
    indications = getRandomString(100)
    nextSteps = getRandomString(100)
    letterText = getRandomString(500)

    val iterator = exams.iterator()
    while (iterator.hasNext()) {
        val exam = iterator.next()

        if (Random.nextBoolean()) {
            exam.testExamRandomChange()
        } else {
            iterator.remove()
        }
    }
}

private fun Exam.testExamRandomChange() {
    date = getRandomLocalDate()
    type = Exam.Companion.ExamType.entries.random()
    description = getRandomString(50)
    diagnosis = getRandomString(100)
}

private fun Visit.Companion.testVisitRandom(): Visit {
    val visit = Visit(
        date = getRandomLocalDate(),
        specialist = getRandomString(15),
        type = Visit.Companion.VisitType.entries.random(),
        anamnesis = getRandomString(100),
        objectiveExam = getRandomString(100),
        indications = getRandomString(100),
        nextSteps = getRandomString(100),
        letterText = getRandomString(500)
    )

    for (i in 0..Random.nextInt(0..4)) {
        visit.addExam(Exam.testExamRandom())
    }

    return visit
}

private fun Exam.Companion.testExamRandom(): Exam {
    return Exam(
        date = getRandomLocalDate(),
        type = Exam.Companion.ExamType.entries.random(),
        description = getRandomString(50),
        diagnosis = getRandomString(100)
    )
}

private fun Place.testPlaceRandomChange(progressive: Int) {
    name = "PlaceChanged$progressive"
    city = getRandomString(17)
}

private fun Place.Companion.testPlaceRandom(progressive: Int): Place {
    return Place(
        name = "Place$progressive",
        city = getRandomString(15)
    )
}

private fun Patient.Companion.testPatientRandom(progressive : Int): Patient {
    return Patient(
        name = "Patient${progressive}",
        gender = Patient.Companion.Gender.entries.random(),
        dateOfBirth = getRandomLocalDate(),
        placeOfBirth = getRandomString(8),
        residence = getRandomString(20),
        FC = getRandomString(12),
        phoneNumber = getRandomPhoneNumber(),
        landlinePhoneNumber = getRandomPhoneNumber(),
        mail = getRandomMail(8),
        doctor = getRandomString(15)
    )
}

private fun Patient.testPatientRandomChange(progressive: Int) {
    name = "PatientChanged$progressive"
    gender = Patient.Companion.Gender.entries.random()
    dateOfBirth = getRandomLocalDate()
    placeOfBirth = getRandomString(8)
    residence = getRandomString(20)
    FC = getRandomString(12)
    phoneNumber = getRandomPhoneNumber()
    landlinePhoneNumber = getRandomPhoneNumber()
    mail = getRandomMail(10)
    doctor = getRandomString(20)
}

private fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

private fun getRandomLocalDate() = LocalDate.of((1960..2015).random(), (1..12).random(), (1..28).random())

private fun getRandomPhoneNumber() = (1..10).map { (0..9).random() }.joinToString("")

private fun getRandomMail(length: Int) = getRandomString(length) + "@gmail.com"
