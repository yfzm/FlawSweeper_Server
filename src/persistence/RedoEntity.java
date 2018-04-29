package persistence;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "redo", schema = "flaw_sweeper", catalog = "")
public class RedoEntity {
    private int redoId;
    private String answer;
    private Timestamp redoTime;

    @Id
    @Column(name = "redo_id", nullable = false)
    public int getRedoId() {
        return redoId;
    }

    public void setRedoId(int redoId) {
        this.redoId = redoId;
    }

    @Basic
    @Column(name = "answer", nullable = true, length = 128)
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Basic
    @Column(name = "redo_time", nullable = false)
    public Timestamp getRedoTime() {
        return redoTime;
    }

    public void setRedoTime(Timestamp redoTime) {
        this.redoTime = redoTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedoEntity that = (RedoEntity) o;
        return redoId == that.redoId &&
                Objects.equals(answer, that.answer) &&
                Objects.equals(redoTime, that.redoTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(redoId, answer, redoTime);
    }
}
