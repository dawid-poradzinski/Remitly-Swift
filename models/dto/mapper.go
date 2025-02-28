package dto

import models "swift-remitly-app/models/sql"

func MapToSwiftCodeMainDTO(swiftCode models.SwiftCode) SwiftCodeMainDTO {

	bankName := ""
	if swiftCode.Bank != nil {
		bankName = swiftCode.Bank.Name
	}

	countryISO2 := ""
	countryName := ""
	if swiftCode.CountryID != nil {
		countryISO2 = *swiftCode.CountryID
		countryName = swiftCode.Country.Name
	}

	return SwiftCodeMainDTO{
		Address:       swiftCode.Address,
		BankName:      bankName,
		CountryISO2:   countryISO2,
		CountryName:   countryName,
		IsHeadquarter: swiftCode.IsHeadquarter,
		SwiftCode:     swiftCode.CODE,
		Branches:      MapToSwiftCodenastedDTOList(swiftCode.Branches),
	}
}

func MapToSwiftCodenastedDTOList(branches []models.SwiftCode) []SwiftCodeNastedDTO {
	var branchDTOs []SwiftCodeNastedDTO
	for _, branch := range branches {
		branchDTOs = append(branchDTOs, MapToSwiftCodeNastedDTO(branch))
	}
	return branchDTOs
}

func MapToSwiftCodeNastedDTO(swiftCode models.SwiftCode) SwiftCodeNastedDTO {
	dto := SwiftCodeNastedDTO{
		Address:       swiftCode.Address,
		BankName:      *swiftCode.BankID,
		CountryISO2:   *swiftCode.CountryID,
		IsHeadquarter: swiftCode.IsHeadquarter,
		SwiftCode:     swiftCode.CODE,
	}

	return dto
}
