package it.unicam.cs.asdl2526.es3sol;

/**
 * Una prenotazione riguarda una certa aula per un certo time slot.
 *
 * @author Luca Tesei
 *
 */
public class Prenotazione implements Comparable<Prenotazione> {

    String aula, docente, motivo;
    TimeSlot orario;
    /**
     * Costruisce una prenotazione.
     *
     * @param aula
     *                     l'aula a cui la prenotazione si riferisce
     * @param timeSlot
     *                     il time slot della prenotazione
     * @param docente
     *                     il nome del docente che ha prenotato l'aula
     * @param motivo
     *                     il motivo della prenotazione
     * @throws NullPointerException
     *                                  se uno qualsiasi degli oggetti passati è
     *                                  null
     */
    public Prenotazione(String aula, TimeSlot timeSlot, String docente, String motivo) {
        if(aula == null || timeSlot == null || docente == null || motivo == null)
            throw new NullPointerException("Nessuno dei parametri può essere null");

        this.aula = aula;
        this.orario = timeSlot;
        this.docente = docente;
        this.motivo = motivo;
    }

    /**
     * @return the aula
     */
    public String getAula() {
        return aula;
    }

    /**
     * @return the timeSlot
     */
    public TimeSlot getTimeSlot() {
        return orario;
    }

    /**
     * @return the docente
     */
    public String getDocente() {
        return docente;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param docente
     *                    the docente to set
     */
    public void setDocente(String docente) {
        this.docente = docente;
    }

    /**
     * @param motivo
     *                   the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /*
     * Due prenotazioni sono uguali se hanno la stessa aula e lo stesso time
     * slot. Il docente e il motivo possono cambiare senza influire
     * sull'uguaglianza.
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(!(obj instanceof Prenotazione))
            return false;

        Prenotazione prenotazione = (Prenotazione) obj;
        return aula.equals(prenotazione.getAula()) && orario.equals(prenotazione.getTimeSlot());
    }

    /*
     * L'hashcode di una prenotazione si calcola a partire dai due campi usati
     * per equals.
     */
    @Override
    public int hashCode() {
        int prime = 17;
        int result = 1;
        result = prime * result + aula.hashCode();
        result = prime * result + orario.hashCode();

        return result;
    }

    /*
     * Una prenotazione precede un altra in base all'ordine dei time slot. Se
     * due prenotazioni hanno lo stesso time slot allora una precede l'altra in
     * base all'ordine tra le aule.
     */
    @Override
    public int compareTo(Prenotazione o) {
        int cmp = this.orario.compareTo(o.orario);
        if(cmp != 0)
            return cmp;

        return aula.compareToIgnoreCase(o.aula);
    }

    @Override
    public String toString() {
        return "Prenotazione in aula:" + aula + ", orario: " + orario + ", docente:" + docente + ", motivo:" + motivo + ".";
    }

}
