package dto

import models "swift-remitly-app/models/sql"

func MapToSwiftCodeMainDTO(swiftCode models.SwiftCode) SwiftCodeMainDTO {

	var branchesPtr *[]SwiftCodeNastedDTO
	if swiftCode.IsHeadquarter {
		branches := MapToSwiftCodeNastedDTOList(swiftCode.Branches)
		branchesPtr = &branches
	} else {
		branchesPtr = nil
	}

	return SwiftCodeMainDTO{
		Address:       swiftCode.Address,
		BankName:      *swiftCode.BankName,
		CountryISO2:   *swiftCode.CountryID,
		CountryName:   swiftCode.Country.Name,
		IsHeadquarter: &swiftCode.IsHeadquarter,
		SwiftCode:     swiftCode.Code,
		Branches:      branchesPtr,
	}
}

func MapToSwiftCodeNastedDTOList(branches []models.SwiftCode) []SwiftCodeNastedDTO {
	if len(branches) == 0 {
		return []SwiftCodeNastedDTO{}
	}
	var branchDTOs []SwiftCodeNastedDTO
	for _, branch := range branches {
		branchDTOs = append(branchDTOs, MapToSwiftCodeNastedDTO(branch))
	}
	return branchDTOs
}

func MapToSwiftCodeNastedDTO(swiftCode models.SwiftCode) SwiftCodeNastedDTO {
	dto := SwiftCodeNastedDTO{
		Address:       swiftCode.Address,
		BankName:      *swiftCode.BankName,
		CountryISO2:   *swiftCode.CountryID,
		IsHeadquarter: swiftCode.IsHeadquarter,
		SwiftCode:     swiftCode.Code,
	}

	return dto
}

func MapToCountryDTO(country models.Country) CountryDTO {

	return CountryDTO{
		ISO2:     country.ISO2,
		Name:     country.Name,
		Branches: MapToSwiftCodeNastedDTOList(country.SwiftCodes),
	}

}

func MapToSwiftCodeModel(dto SwiftCodeMainDTO) models.SwiftCode {

	var countryID *string

	if dto.CountryISO2 != "" {
		countryID = &dto.CountryISO2
	}

	var bankName *string

	if dto.BankName != "" {
		bankName = &dto.BankName
	}

	return models.SwiftCode{
		Code:          dto.SwiftCode,
		Address:       dto.Address,
		IsHeadquarter: *dto.IsHeadquarter,
		BankName:      bankName,
		CountryID:     countryID,
	}

}
