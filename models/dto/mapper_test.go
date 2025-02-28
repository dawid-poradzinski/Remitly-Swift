package dto

import (
	models "swift-remitly-app/models/sql"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestMapToSwiftCodeMainDTO(t *testing.T) {
	t.Run("should map headquarters with branches", func(t *testing.T) {
		// Given
		bankName := "Test Bank"
		countryID := "PL"
		isHeadquarter := true
		swiftCode := models.SwiftCode{
			Code:          "TESTPL12",
			Address:       "Test Address 1",
			IsHeadquarter: isHeadquarter,
			BankName:      &bankName,
			CountryID:     &countryID,
			Country: models.Country{
				Name: "Poland",
			},
			Branches: []models.SwiftCode{
				{
					Code:          "TESTPL13",
					Address:       "Branch Address",
					IsHeadquarter: false,
					BankName:      &bankName,
					CountryID:     &countryID,
				},
			},
		}

		// When
		result := MapToSwiftCodeMainDTO(swiftCode)

		// Then
		assert.Equal(t, swiftCode.Address, result.Address)
		assert.Equal(t, *swiftCode.BankName, result.BankName)
		assert.Equal(t, *swiftCode.CountryID, result.CountryISO2)
		assert.Equal(t, swiftCode.Country.Name, result.CountryName)
		assert.Equal(t, swiftCode.IsHeadquarter, *result.IsHeadquarter)
		assert.Equal(t, swiftCode.Code, result.SwiftCode)
		assert.NotNil(t, result.Branches)
		assert.Len(t, *result.Branches, 1)
	})

	t.Run("should map non-headquarters without branches", func(t *testing.T) {
		// Given
		bankName := "Test Bank"
		countryID := "PL"
		isHeadquarter := false
		swiftCode := models.SwiftCode{
			Code:          "TESTPL12",
			Address:       "Test Address 1",
			IsHeadquarter: isHeadquarter,
			BankName:      &bankName,
			CountryID:     &countryID,
			Country: models.Country{
				Name: "Poland",
			},
		}

		// When
		result := MapToSwiftCodeMainDTO(swiftCode)

		// Then
		assert.Equal(t, swiftCode.Address, result.Address)
		assert.Equal(t, *swiftCode.BankName, result.BankName)
		assert.Equal(t, *swiftCode.CountryID, result.CountryISO2)
		assert.Equal(t, swiftCode.Country.Name, result.CountryName)
		assert.Equal(t, swiftCode.IsHeadquarter, *result.IsHeadquarter)
		assert.Equal(t, swiftCode.Code, result.SwiftCode)
		assert.Nil(t, result.Branches)
	})
}
