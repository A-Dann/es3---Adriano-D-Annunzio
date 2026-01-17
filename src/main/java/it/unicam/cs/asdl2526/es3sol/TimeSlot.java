/**
 *
 */
package it.unicam.cs.asdl2526.es3sol;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Un time slot è un intervallo di tempo continuo che può essere associato ad
 * una prenotazione. Gli oggetti della classe sono immutabili. Non sono ammessi
 * time slot che iniziano e finiscono nello stesso istante.
 *
 * @author Luca Tesei
 *
 */
public class TimeSlot implements Comparable<TimeSlot> {

    /**
     * Rappresenta la soglia di tolleranza da considerare nella sovrapposizione
     * di due Time Slot. Se si sovrappongono per un numero di minuti minore o
     * uguale a questa soglia allora NON vengono considerati sovrapposti.
     */
    public static final int MINUTES_OF_TOLERANCE_FOR_OVERLAPPING = 5;

    private final GregorianCalendar start;

    private final GregorianCalendar stop;

    /**
     * Crea un time slot tra due istanti di inizio e fine
     *
     * @param start
     *                  inizio del time slot
     * @param stop
     *                  fine del time slot
     * @throws NullPointerException
     *                                      se uno dei due istanti, start o
     *                                      stop, è null
     * @throws IllegalArgumentException
     *                                      se start è uguale o successivo a
     *                                      stop
     */
    public TimeSlot(GregorianCalendar start, GregorianCalendar stop) {
        if(start == null || stop == null)
            throw new NullPointerException("Start and stop non possono essere null");
        if(start.equals(stop) || stop.before(start))
            throw new IllegalArgumentException("Start deve iniziare prima di stop");

        this.start = start;
        this.stop = stop;
    }

    /**
     * @return the start
     */
    public GregorianCalendar getStart() {
        return start;
    }

    /**
     * @return the stop
     */
    public GregorianCalendar getStop() {
        return stop;
    }

    /*
     * Un time slot è uguale a un altro se rappresenta esattamente lo stesso
     * intervallo di tempo, cioè se inizia nello stesso istante e termina nello
     * stesso istante.
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(!(obj instanceof TimeSlot))
            return false;

        TimeSlot other = (TimeSlot) obj;
        if(start.equals(other.getStart()) && stop.equals(other.getStop()))
            return true;
        else
            return false;
    }

    /*
     * Il codice hash associato a un timeslot viene calcolato a partire dei due
     * istanti di inizio e fine, in accordo con i campi usati per il metodo
     * equals.
     */
    @Override
    public int hashCode() {
        int prime = 17;
        int result = 1;
        result = prime * result + start.hashCode();
        result = prime * result + stop.hashCode();

        return result;
    }

    /*
     * Un time slot precede un altro se inizia prima. Se due time slot iniziano
     * nello stesso momento quello che finisce prima precede l'altro. Se hanno
     * stesso inizio e stessa fine sono uguali, in compatibilità con equals.
     */
    @Override
    public int compareTo(TimeSlot o) {
        if(o == null)
            throw new NullPointerException("Il time slot non può essere nullo");
        int cmp = start.compareTo(o.getStart());
        if(cmp != 0)
            return cmp;
        else
            cmp = stop.compareTo(o.getStop());
        return cmp;
    }

    /**
     * Determina il numero di minuti di sovrapposizione tra questo timeslot e
     * quello passato.
     *
     * @param o
     *              il time slot da confrontare con questo
     * @return il numero di minuti di sovrapposizione tra questo time slot e
     *         quello passato, oppure -1 se non c'è sovrapposizione. Se questo
     *         time slot finisce esattamente al millisecondo dove inizia il time
     *         slot <code>o</code> non c'è sovrapposizione, così come se questo
     *         time slot inizia esattamente al millisecondo in cui finisce il
     *         time slot <code>o</code>. In questi ultimi due casi il risultato
     *         deve essere -1 e non 0. Nel caso in cui la sovrapposizione non è
     *         di un numero esatto di minuti, cioè ci sono secondi e
     *         millisecondi che avanzano, il numero dei minuti di
     *         sovrapposizione da restituire deve essere arrotondato per difetto
     * @throws NullPointerException
     *                                      se il time slot passato è nullo
     * @throws IllegalArgumentException
     *                                      se i minuti di sovrapposizione
     *                                      superano Integer.MAX_VALUE
     */
    public int getMinutesOfOverlappingWith(TimeSlot o) {
        if(o == null)
            throw new NullPointerException("il time slot è nullo");

        int cmp1 = start.compareTo(o.getStart());
        int cmp2 = start.compareTo(o.getStop());
        int cmp3 = stop.compareTo(o.getStart());
        int cmp4 = stop.compareTo(o.getStop());
        long sovrapposizioneInMillisecondi;

        if(cmp1 < 0 && cmp3 > 0 && cmp4 < 0){
            //this.start - o.start - this-stop - o.stop
            sovrapposizioneInMillisecondi = stop.getTimeInMillis() - o.getStart().getTimeInMillis();
            return computeMinutes(sovrapposizioneInMillisecondi);
        }

        if(cmp1 < 0 && cmp4 > 0){
            //this.start - o.start - o.stop - this-stop
            sovrapposizioneInMillisecondi = o.stop.getTimeInMillis() - o.getStart().getTimeInMillis();
            return computeMinutes(sovrapposizioneInMillisecondi);
        }

        if(cmp1 > 0 && cmp2 < 0 && cmp4 > 0) {
            //o.start - this.start - o.stop - this.stop
            sovrapposizioneInMillisecondi = o.getStop().getTimeInMillis() - start.getTimeInMillis();
            return computeMinutes(sovrapposizioneInMillisecondi);
        }

        if(cmp1 > 0 && cmp4 < 0){
            //o.start - this.start - this.stop - o.stop
            sovrapposizioneInMillisecondi = stop.getTimeInMillis() - start.getTimeInMillis();
            return computeMinutes(sovrapposizioneInMillisecondi);
        }

        return -1;
    }

    /*
     * Calcola il numero di minuti di sovrapposizione a partire dai millisecondi
     * facendo il troncamento. Gestisce il caso particolare in cui il numero di
     * millisecondi passati è 0. Lancia IllegalArgumentException se il numero di
     * minuti è troppo grande per un int.
     */
    private int computeMinutes(long overlappingMilliseconds) {
        if(overlappingMilliseconds == 0)
            return -1;

        long minutiArrotondati = overlappingMilliseconds / 60000;
        if(minutiArrotondati > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Il numero di minuti è troppo grande da gestire.");

        return (int)minutiArrotondati;
    }

    /**
     * Determina se questo time slot si sovrappone a un altro time slot dato,
     * considerando la soglia di tolleranza.
     *
     * @param o
     *              il time slot che viene passato per il controllo di
     *              sovrapposizione
     * @return true se questo time slot si sovrappone per più (strettamente) di
     *         MINUTES_OF_TOLERANCE_FOR_OVERLAPPING minuti a quello passato
     * @throws NullPointerException
     *                                  se il time slot passato è nullo
     */
    public boolean overlapsWith(TimeSlot o) {
        if(o == null)
            throw new NullPointerException("time slot nullo");

        int minSovrapposti = this.getMinutesOfOverlappingWith(o);
        if(minSovrapposti == -1)
            return false;
        if(minSovrapposti > MINUTES_OF_TOLERANCE_FOR_OVERLAPPING)
            return true;
        else
            return false;
    }

    /*
     * Ridefinisce il modo in cui viene reso un TimeSlot con una String.
     *
     * Esempio 1, stringa da restituire: "[4/11/2019 11.0 - 4/11/2019 13.0]"
     *
     * Esempio 2, stringa da restituire: "[10/11/2019 11.15 - 10/11/2019 23.45]"
     *
     * I secondi e i millisecondi eventuali non vengono scritti.
     */
    @Override
    public String toString() {
        return "[" + start.get(Calendar.DAY_OF_MONTH) + "/"
                + (start.get(Calendar.MONTH) + 1) + "/"
                + start.get(Calendar.YEAR) + " "
                + start.get(Calendar.HOUR_OF_DAY) + "."
                + start.get(Calendar.MINUTE) + " - "
                + stop.get(Calendar.DAY_OF_MONTH) + "/"
                + (stop.get(Calendar.MONTH) + 1) + "/" + stop.get(Calendar.YEAR)
                + " " + stop.get(Calendar.HOUR_OF_DAY) + "."
                + stop.get(Calendar.MINUTE) + "]";
    }

}