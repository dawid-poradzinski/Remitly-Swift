package dto

import "github.com/go-playground/validator"

type SwiftCodeMainDTO struct {
	Address       string                `json:"address"`
	BankName      string                `json:"bankName" validate:"required"`
	CountryISO2   string                `json:"countryISO2" validate:"required,len=2"`
	CountryName   string                `json:"countryName" validate:"required"`
	IsHeadquarter *bool                 `json:"isHeadquarter" validate:"required"`
	SwiftCode     string                `json:"swiftCode" validate:"required,min=11,max=11"`
	Branches      *[]SwiftCodeNastedDTO `json:"branches,omitempty"`
}

var validate *validator.Validate

func init() {
	validate = validator.New()
}

func ValidateSwiftCodeMainDTO(dto interface{}) error {

	err := validate.Struct(dto)
	if err != nil {
		return err
	}

	return nil
}
