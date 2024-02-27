package org.fordes.subtitles.view.utils.submerge.subtitle.common;


import java.io.Serializable;
import java.time.LocalTime;

public class SubtitleTime implements TimedObject, Serializable {

    private static final long serialVersionUID = -2283115927128309201L;

    /**
     * Start Time of the Event, in 0:00:00:00 format ie. Hrs:Mins:Secs:hundredths. This is
     * the time elapsed during script playback at which the text will appear onscreen.
     */
    protected LocalTime start;

    /**
     * End Time of the Event, in 0:00:00:00 format ie. Hrs:Mins:Secs:hundredths. This is
     * the time elapsed during script playback at which the text will disappear offscreen.
     */
    protected LocalTime end;

    public SubtitleTime() {
    }

    public SubtitleTime(LocalTime start, LocalTime end) {

        super();
        this.start = start;
        this.end = end;
    }

    @Override
    public int compare(TimedObject o1, TimedObject o2) {

        return o1.compareTo(o2);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        TimedObject other = (TimedObject) obj;
        return compareTo(other) == 0;
    }

    @Override
    public int compareTo(TimedObject other) {

        int compare = this.start.compareTo(other.getStart());
        if (compare == 0) {
            compare = this.end.compareTo(other.getEnd());
        }
        return compare;
    }

    // ===================== getter and setter start =====================

    @Override
    public LocalTime getStart() {
        return this.start;
    }

    @Override
    public void setStart(LocalTime start) {
        this.start = start;
    }

    @Override
    public LocalTime getEnd() {
        return this.end;
    }

    @Override
    public void setEnd(LocalTime end) {
        this.end = end;
    }
}
