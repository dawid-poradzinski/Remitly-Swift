package dto

import "github.com/go-playground/validator"

type CountryDTO struct {
	ISO2     string               `json:"countryISO2" validate:"required"`
	Name     string               `json:"countryName" validate:"required"`
	Branches []SwiftCodeNastedDTO `json:"branches"`
}

func init() {
	validate = validator.New()
}

func ValidateCountryDTO(dto CountryDTO) error {
	return validate.Struct(dto)
}
