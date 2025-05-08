package in.intranet.springbootmongodb.utils;

import in.intranet.springbootmongodb.enums.Status;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CertificateUtils {

    public static Status calculateStatus(Date maturityDate, Date createdAt) {
        if (maturityDate == null || createdAt == null) {
            return Status.NO_PRAZO;
        }

        LocalDate createdDate = createdAt.toInstant().atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();
        LocalDate maturityLocalDate = maturityDate.toInstant().atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();

        if (createdDate.isAfter(maturityLocalDate)) {
            return Status.VENCIDO;
        } else if (!createdDate.isAfter(maturityLocalDate.minusDays(5))) {
            return Status.NO_PRAZO;
        } else {
            return Status.A_VENCER;
        }
    }
}
