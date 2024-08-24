package com.MatteV02.EHRforEquipes.commonModule.dbManagement

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.MatteV02.EHRforEquipes.commonModule.dbManagement.DBEntityManager.ConnectionTypes
import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import com.MatteV02.EHRforEquipes.commonModule.entities.place.Place
import com.MatteV02.EHRforEquipes.commonModule.entities.visit.Exam
import com.MatteV02.EHRforEquipes.commonModule.entities.visit.Visit
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.UUID
import javax.sql.DataSource
import kotlin.random.Random
import kotlin.random.nextInt

object SQLDBEntityManager : DBEntityManager {
    var dataSource: DataSource? = null

    object Defaults {
        fun permanentConfiguration() : HikariConfig {
            val hikariConfig = HikariConfig()
            hikariConfig.driverClassName = "org.h2.Driver"
            hikariConfig.jdbcUrl = "jdbc:h2:./dbSQL"
            hikariConfig.maximumPoolSize = 20

            return hikariConfig
        }

        fun volatileConfiguration() : HikariConfig {
            val hikariConfig = HikariConfig()
            hikariConfig.driverClassName = "org.h2.Driver"
            hikariConfig.jdbcUrl = "jdbc:h2:mem:test${Random.nextInt(0..1000)}"
            hikariConfig.maximumPoolSize = 20

            return hikariConfig
        }

        fun temporaryFileConfiguration() : HikariConfig {
            val hikariConfig = HikariConfig()
            hikariConfig.driverClassName = "org.h2.Driver"
            hikariConfig.jdbcUrl = "jdbc:h2:" + System.getProperty("java.io.tmpdir") + "/db" + Random.nextInt(0..Int.MAX_VALUE)
            hikariConfig.maximumPoolSize = 20

            return hikariConfig
        }
    }

    override fun dbConnection(connectionType: ConnectionTypes) {
        val configuration = when (connectionType) {
            ConnectionTypes.PERMANENT -> Defaults.permanentConfiguration()
            ConnectionTypes.VOLATILE -> Defaults.volatileConfiguration()
            ConnectionTypes.TEMPORARY_FILE -> Defaults.temporaryFileConfiguration()
        }

        dataSource = HikariDataSource(configuration)

        dataSource?.connection?.use { connection: Connection ->
            connection.createStatement().use { statement: Statement ->
                statement.addBatch(
                    "CREATE TABLE IF NOT EXISTS Patient(" +
                            "id VARCHAR PRIMARY KEY, " +
                            "name VARCHAR, " +
                            "gender INTEGER, " +
                            "dateOfBirth DATE, " +
                            "placeOfBirth VARCHAR, " +
                            "residence VARCHAR, " +
                            "FC VARCHAR, " +
                            "phoneNumber VARCHAR, " +
                            "landlinePhoneNumber VARCHAR, " +
                            "mail VARCHAR, " +
                            "doctor VARCHAR" +
                            ")"
                )
                statement.addBatch(
                    "CREATE TABLE IF NOT EXISTS Place(" +
                            "id VARCHAR PRIMARY KEY, " +
                            "name VARCHAR, " +
                            "city VARCHAR" +
                            ")"
                )
                statement.addBatch(
                    "CREATE TABLE IF NOT EXISTS Visit(" +
                            "id VARCHAR PRIMARY KEY, " +
                            "date DATE, " +
                            "patientID VARCHAR NULL, " +
                            "placeID VARCHAR NULL, " +
                            "specialist VARCHAR, " +
                            "type INT, " +
                            "anamnesis VARCHAR, " +
                            "objectiveExam VARCHAR, " +
                            "indications VARCHAR, " +
                            "nextSteps VARCHAR, " +
                            "letterText VARCHAR, " +
                            "FOREIGN KEY (patientID) REFERENCES Patient(id), " +
                            "FOREIGN KEY (placeID) REFERENCES Place(id)" +
                            ")"
                )
                statement.addBatch(
                    "CREATE TABLE IF NOT EXISTS Exam(" +
                            "id VARCHAR PRIMARY KEY, " +
                            "date DATE, " +
                            "type INT, " +
                            "description VARCHAR, " +
                            "diagnosis VARCHAR, " +
                            "visitID VARCHAR, " +
                            "FOREIGN KEY (visitID) REFERENCES Visit(id)" +
                            ")"
                )

                statement.executeBatch()
            }
        } ?: throw DBEntityManagerNotConnectedException()
    }

    override fun insert(o: Any) {
        when (o) {
            is Patient -> o.insert()
            is Place -> o.insert()
            is Visit -> o.insert()
        }
    }

    private fun Patient.insert() {
        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "INSERT INTO Patient(" +
                        "id, " +
                        "name, " +
                        "gender, " +
                        "dateOfBirth, " +
                        "placeOfBirth, " +
                        "residence, " +
                        "FC, " +
                        "phoneNumber, " +
                        "landlinePhoneNumber, " +
                        "mail, " +
                        "doctor" +
                        ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())
                preparedStatement.setString(2, name)
                preparedStatement.setInt(3, gender.ordinal)
                preparedStatement.setDate(4, Date.valueOf(dateOfBirth))
                preparedStatement.setString(5, placeOfBirth)
                preparedStatement.setString(6, residence)
                preparedStatement.setString(7, FC)
                preparedStatement.setString(8, phoneNumber)
                preparedStatement.setString(9, landlinePhoneNumber)
                preparedStatement.setString(10, mail)
                preparedStatement.setString(11, doctor)

                preparedStatement.executeUpdate()
            }
        } ?: throw DBEntityManagerNotConnectedException()
    }

    private fun Place.insert() {
        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "INSERT INTO Place(" +
                        "id, " +
                        "name, " +
                        "city" +
                        ") VALUES (?, ?, ?)"
            ) .use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())
                preparedStatement.setString(2, name)
                preparedStatement.setString(3, city)

                preparedStatement.executeUpdate()
            }
        } ?: throw DBEntityManagerNotConnectedException()
    }

    private fun Visit.insert() {
        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "INSERT INTO Visit(" +
                        "id, " +
                        "date, " +
                        "patientID, " +
                        "placeID, " +
                        "specialist, " +
                        "type, " +
                        "anamnesis, " +
                        "objectiveExam, " +
                        "indications, " +
                        "nextSteps, " +
                        "letterText" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())
                preparedStatement.setDate(2, Date.valueOf(date))
                preparedStatement.setString(3, patient?.id?.toString())
                preparedStatement.setString(4, place?.id?.toString())
                preparedStatement.setString(5, specialist)
                preparedStatement.setInt(6, type.ordinal)
                preparedStatement.setString(7, anamnesis)
                preparedStatement.setString(8, objectiveExam)
                preparedStatement.setString(9, indications)
                preparedStatement.setString(10, nextSteps)
                preparedStatement.setString(11, letterText)

                preparedStatement.executeUpdate()

                exams.forEach { it.insert(visit = this) }
            }

        } ?: throw DBEntityManagerNotConnectedException()
    }

    private fun Exam.insert(visit: Visit) {
        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "INSERT INTO Exam(" +
                        "id, " +
                        "date, " +
                        "type, " +
                        "description, " +
                        "diagnosis, " +
                        "visitID" +
                        ") VALUES (?, ?, ?, ?, ?, ?)"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id(visit))
                preparedStatement.setDate(2, Date.valueOf(date))
                preparedStatement.setInt(3, type.ordinal)
                preparedStatement.setString(4, description)
                preparedStatement.setString(5, diagnosis)
                preparedStatement.setString(6, visit.id.toString())

                preparedStatement.executeUpdate()
            }
        } ?: throw DBEntityManagerNotConnectedException()
    }

    private fun Exam.id(visit : Visit): String = date.toString() + type.name + description + diagnosis + visit.id.toString()

    override fun update(o: Any) {
        when (o) {
            is Patient -> o.update()
            is Place -> o.update()
            is Visit -> o.update()
        }
    }

    private fun Patient.update() {
        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "UPDATE Patient SET " +
                        "name = ?, " +
                        "gender = ?, " +
                        "dateOfBirth = ?, " +
                        "placeOfBirth = ?, " +
                        "residence = ?, " +
                        "FC = ?, " +
                        "phoneNumber = ?, " +
                        "landlinePhoneNumber = ?, " +
                        "mail = ?, " +
                        "doctor = ? " +
                        "WHERE id = ?;"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, name)
                preparedStatement.setInt(2, gender.ordinal)
                preparedStatement.setDate(3, Date.valueOf(dateOfBirth))
                preparedStatement.setString(4, placeOfBirth)
                preparedStatement.setString(5, residence)
                preparedStatement.setString(6, FC)
                preparedStatement.setString(7, phoneNumber)
                preparedStatement.setString(8, landlinePhoneNumber)
                preparedStatement.setString(9, mail)
                preparedStatement.setString(10, doctor)

                preparedStatement.setString(11, id.toString())

                preparedStatement.executeUpdate()
            }
        } ?: throw DBEntityManagerNotConnectedException()
    }

    private fun Place.update() {
        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "UPDATE Place SET " +
                        "name = ?, " +
                        "city = ? " +
                        "WHERE id = ?"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, name)
                preparedStatement.setString(2, city)

                preparedStatement.setString(3, id.toString())

                preparedStatement.executeUpdate()
            }
        } ?: throw DBEntityManagerNotConnectedException()
    }

    private fun Visit.update() {
        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "UPDATE Visit SET " +
                        "date = ?, " +
                        "patientID = ?, " +
                        "placeID = ?, " +
                        "specialist = ?, " +
                        "type = ?, " +
                        "anamnesis = ?, " +
                        "objectiveExam = ?, " +
                        "indications = ?, " +
                        "nextSteps = ?, " +
                        "letterText = ? " +
                        "WHERE id = ?"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setDate(1, Date.valueOf(date))
                preparedStatement.setString(2, patient?.id?.toString())
                preparedStatement.setString(3, place?.id?.toString())
                preparedStatement.setString(4, specialist)
                preparedStatement.setInt(5, type.ordinal)
                preparedStatement.setString(6, anamnesis)
                preparedStatement.setString(7, objectiveExam)
                preparedStatement.setString(8, indications)
                preparedStatement.setString(9, nextSteps)
                preparedStatement.setString(10, letterText)

                preparedStatement.setString(11, id.toString())

                preparedStatement.executeUpdate()
            }

            exams.forEach { exam: Exam ->
                connection.prepareStatement(
                    "SELECT * FROM Exam WHERE id = ?",
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE
                ).use { preparedStatement: PreparedStatement ->
                    preparedStatement.setString(1, exam.id(visit = this))

                    val resultSet = preparedStatement.executeQuery()

                    if (!resultSet.next()) {
                        exam.insert(visit = this)
                    } else {
                        do {
                            resultSet.updateDate("date", Date.valueOf(exam.date))
                            resultSet.updateInt("type", exam.type.ordinal)
                            resultSet.updateString("description", exam.description)
                            resultSet.updateString("diagnosis", exam.diagnosis)

                            resultSet.updateRow()
                        } while (resultSet.next())
                    }
                }
            }

            var notInTemplate = ""

            for (i in 1..exams.size) {
                notInTemplate += "?, "
            }
            notInTemplate = notInTemplate.removeSuffix(", ")

            connection.prepareStatement(
                "SELECT * FROM Exam WHERE " +
                        "visitID = ? AND " +
                        "id NOT IN (" +
                        notInTemplate +
                        ")",
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())

                exams.forEachIndexed { index, exam ->
                    preparedStatement.setString(index + 2, exam.id(visit = this))
                }

                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next()) {
                    resultSet.deleteRow()
                }
            }
        } ?: throw DBEntityManagerNotConnectedException()
    }

    override fun remove(o: Any) {
        when (o) {
            is Patient -> o.remove()
            is Place -> o.remove()
            is Visit -> o.remove()
        }
    }

    private fun Patient.remove() {
        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "DELETE FROM Exam WHERE visitID IN (" +
                        "SELECT id FROM visit WHERE " +
                            "patientID = ?" +
                        ")"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())

                preparedStatement.executeUpdate()
            }

            connection.prepareStatement(
                "DELETE FROM Visit WHERE patientID = ?"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())
                preparedStatement.executeUpdate()
            }

            connection.prepareStatement(
                "DELETE FROM Patient WHERE id = ?"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())

                preparedStatement.executeUpdate()
            }
        } ?: throw DBEntityManagerNotConnectedException()
    }

    private fun Place.remove() {
        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "DELETE FROM Exam WHERE visitID IN (" +
                        "SELECT id FROM visit WHERE " +
                        "placeID = ?" +
                        ")"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())

                preparedStatement.executeUpdate()
            }

            connection.prepareStatement(
                "DELETE FROM Visit WHERE placeID = ?"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())

                preparedStatement.executeUpdate()
            }

            connection.prepareStatement(
                "DELETE FROM Place WHERE id = ?"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())

                preparedStatement.executeUpdate()
            }
        } ?: throw DBEntityManagerNotConnectedException()
    }

    private fun Visit.remove() {
        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "DELETE FROM Exam WHERE visitID = ?"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())

                preparedStatement.executeUpdate()
            }

            connection.prepareStatement(
                "DELETE FROM Visit WHERE id = ?"
            ).use { preparedStatement: PreparedStatement ->
                preparedStatement.setString(1, id.toString())

                preparedStatement.executeUpdate()
            }
        }
    }

    override fun getPatients(): List<Patient> {
        val patients = mutableListOf<Patient>()

        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "SELECT * FROM Patient"
            ).use { preparedStatement: PreparedStatement ->
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next()) {
                    val patient = Patient(
                        resultSet.getString("name"),
                        Patient.Companion.Gender.entries[resultSet.getInt("gender")],
                        resultSet.getDate("dateOfBirth").toLocalDate(),
                        resultSet.getString("placeOfBirth"),
                        resultSet.getString("residence"),
                        resultSet.getString("FC"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("landlinePhoneNumber"),
                        resultSet.getString("mail"),
                        resultSet.getString("doctor")
                    )

                    patient.id = UUID.fromString(resultSet.getString("id"))

                    patients.add(patient)
                }
            }
        } ?: throw DBEntityManagerNotConnectedException()

        return patients
    }

    override fun getPlaces(): List<Place> {
        val places = mutableListOf<Place>()

        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "SELECT * FROM Place"
            ).use { preparedStatement: PreparedStatement ->
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next()) {
                    val place = Place(
                        resultSet.getString("name"),
                        resultSet.getString("city")
                    )
                    place.id = UUID.fromString(resultSet.getString("id"))

                    places.add(place)
                }
            }
        } ?: throw DBEntityManagerNotConnectedException()

        return places
    }

    override fun getVisits(): List<Visit> {
        val visits = mutableListOf<Visit>()

        dataSource?.connection?.use { connection: Connection ->
            connection.prepareStatement(
                "SELECT * FROM Visit"
            ).use { preparedStatement: PreparedStatement ->
                val resultSet = preparedStatement.executeQuery()

                while (resultSet.next()) {
                    val visit = Visit()

                    visit.id = UUID.fromString(resultSet.getString("id"))
                    visit.date = resultSet.getDate("date").toLocalDate()
                    visit.specialist = resultSet.getString("specialist")
                    visit.type = Visit.Companion.VisitType.entries[resultSet.getInt("type")]
                    visit.anamnesis = resultSet.getString("anamnesis")
                    visit.objectiveExam = resultSet.getString("objectiveExam")
                    visit.indications = resultSet.getString("indications")
                    visit.nextSteps = resultSet.getString("nextSteps")
                    visit.letterText = resultSet.getString("letterText")

                    visit.patient = getPatients().firstOrNull { p -> p.id.toString() == resultSet.getString("patientID") }

                    visit.place = getPlaces().firstOrNull { p -> p.id.toString() == resultSet.getString("placeID") }

                    connection.prepareStatement(
                        "SELECT * FROM Exam WHERE visitID = ?"
                    ).use { examStatement: PreparedStatement ->
                        examStatement.setString(1, visit.id.toString())
                        val examSet = examStatement.executeQuery()

                        while (examSet.next()) {
                            val exam = Exam(
                                examSet.getDate("date").toLocalDate(),
                                Exam.Companion.ExamType.entries[examSet.getInt("type")],
                                examSet.getString("description"),
                                examSet.getString("diagnosis")
                            )

                            visit.exams.add(exam)
                        }
                    }

                    visits.add(visit)
                }
            }
        } ?: throw DBEntityManagerNotConnectedException()

        return visits
    }
}