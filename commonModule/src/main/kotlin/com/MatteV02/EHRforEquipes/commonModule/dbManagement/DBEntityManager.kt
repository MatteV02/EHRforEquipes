package com.MatteV02.EHRforEquipes.commonModule.dbManagement

import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import com.MatteV02.EHRforEquipes.commonModule.entities.place.Place
import com.MatteV02.EHRforEquipes.commonModule.entities.visit.Visit

/**
 * Descrive una connessione ad un database contenente pazienti, posti e visite
 */
interface DBEntityManager {
    /**
     * Stabilisce una connessione con il database del tipo specificato
     *
     * @param connectionType Tipo di connessione da stabilire
     */
    fun dbConnection(connectionType: ConnectionTypes = ConnectionTypes.PERMANENT)

    /**
     * Inserisce un oggetto sul database
     *
     * @param o Oggetto da inserire
     */
    fun insert(o: Any)

    /**
     * Aggiorna un oggetto già presente sul database
     *
     * @param o Oggetto da aggiornare
     */
    fun update(o: Any)

    /**
     * Rimuove un oggetto preesistente dal database
     *
     * @param o Oggetto da rimuovere
     */
    fun remove(o: Any)

    /**
     * Ottiene la lista di tutti i pazienti dal database
     */
    fun getPatients(): List<Patient>

    /**
     * Ottiene la lista di tutti i posti dal database
     */
    fun getPlaces(): List<Place>

    /**
     * Ottiene la lista di tutte le visite presenti sul database
     */
    fun getVisits(): List<Visit>

    enum class ConnectionTypes {
        PERMANENT,
        TEMPORARY_FILE,
        VOLATILE
    }
}