package services

import (
	"errors"
	"mime/multipart"
	"strings"
	"swift-remitly-app/models/dto"

	"github.com/xuri/excelize/v2"
)

func ValidateExcelFile(mime string) error {

	if mime != "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" {
		return errors.New("invalid file type. expected .xlsx")
	}

	return nil
}

func ReadExcelData(file multipart.File) ([]dto.SwiftCodeMainDTO, error) {

	excel, err := excelize.OpenReader(file)

	if err != nil {
		return nil, errors.New("failed to open excel")
	}

	defer excel.Close()

	sheetName := excel.GetSheetName(0)

	if sheetName == "" {
		return nil, errors.New("sheet not found")
	}

	rows, err := excel.GetRows(sheetName)

	if err != nil {
		return nil, errors.New("failed to get rows")
	}

	defer excel.Close()

	var swiftCodeList []dto.SwiftCodeMainDTO

	for i, row := range rows {

		if i == 0 {
			continue
		}

		var swiftCode dto.SwiftCodeMainDTO

		swiftCode.CountryISO2 = row[0]
		swiftCode.SwiftCode = row[1]

		swiftCode.IsHeadquarter = new(bool)
		*swiftCode.IsHeadquarter = strings.HasSuffix(swiftCode.SwiftCode, "XXX")

		swiftCode.BankName = row[3]
		swiftCode.Address = row[4]
		swiftCode.CountryName = row[6]

		swiftCodeList = append(swiftCodeList, swiftCode)
	}

	return swiftCodeList, nil

}
