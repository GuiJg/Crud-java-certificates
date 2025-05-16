package in.intranet.springbootmongodb.service;

import in.intranet.springbootmongodb.dto.CertificateImportDto;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelParserService {

    public List<CertificateImportDto> parseExcel(MultipartFile file) throws Exception {
        List<CertificateImportDto> list = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Ignora o cabeçalho
            Row row = sheet.getRow(i);
            if (row == null) continue;

            CertificateImportDto dto = new CertificateImportDto();
            dto.setFile(getCellValue(row.getCell(0)));
            dto.setCompany(getCellValue(row.getCell(1)));
            dto.setCode(getCellValue(row.getCell(2)));
            dto.setCpfCnpj(getCellValue(row.getCell(3)));
            dto.setMunicipality(getCellValue(row.getCell(4)));
            dto.setUf(getCellValue(row.getCell(5)));
            dto.setType(getCellValue(row.getCell(6)));
            dto.setPassword(getCellValue(row.getCell(7)));

            // Leitura robusta de maturityDate
            Cell dateCell = row.getCell(8);
            if (dateCell != null) {
                switch (dateCell.getCellType()) {
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(dateCell)) {
                            dto.setMaturityDate(dateCell.getDateCellValue());
                        } else {
                            dto.setMaturityDate(DateUtil.getJavaDate(dateCell.getNumericCellValue()));
                        }
                        break;
                    case STRING:
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            dto.setMaturityDate(sdf.parse(dateCell.getStringCellValue().trim()));
                        } catch (Exception e) {
                            throw new RuntimeException("Formato de data inválido na linha " + (i + 1));
                        }
                        break;
                    default:
                        throw new RuntimeException("Tipo de célula de data inválido na linha " + (i + 1));
                }
            }

            list.add(dto);
        }

        workbook.close();
        return list;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("dd/MM/yyyy").format(cell.getDateCellValue());
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return cell.toString().trim();
        }
    }
}