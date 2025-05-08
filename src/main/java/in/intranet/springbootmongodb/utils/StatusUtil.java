package in.intranet.springbootmongodb.utils;

import in.intranet.springbootmongodb.enums.Status;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class StatusUtil {

    public static Status calculateStatus(Date maturityDate, Date createdAt) {
        LocalDate maturityLocalDate = maturityDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate createdLocalDate = createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(createdLocalDate, maturityLocalDate);

        if (daysBetween < 0) {
            return Status.VENCIDO;
        } else if (daysBetween <= 5) {
            return Status.A_VENCER;
        } else {
            return Status.NO_PRAZO;
        }
    }
}
