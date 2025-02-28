package models

import (
	"gorm.io/gorm"
)

type SwiftCode struct {
	Code            string `gorm:"primaryKey;size:11"`
	Address         string `gorm:"size:255"`
	IsHeadquarter   bool   `gorm:"not null"`
	CountryID       *string
	Country         Country `gorm:"foreignKey:CountryID;references:ISO2"`
	BankName        *string
	Bank            Bank        `gorm:"foreignKey:BankName;references:Name"`
	ParentSwiftCode *string     `gorm:"size:11;default:NULL"`
	ParentSwift     *SwiftCode  `gorm:"foreignKey:ParentSwiftCode;references:Code"`
	Branches        []SwiftCode `gorm:"foreignKey:ParentSwiftCode"`
}

func (sc *SwiftCode) AddConnectionsToBranch(db *gorm.DB) error {

	if !sc.IsHeadquarter {

		var parent SwiftCode

		if err := db.Where("Code = ?", sc.Code[:8]+"XXX").First(&parent).Error; err == nil {

			sc.ParentSwiftCode = &parent.Code

		}

	}

	return nil

}

func (sc *SwiftCode) AddConnectionsToheadquarter(db *gorm.DB) error {

	if sc.IsHeadquarter {

		if err := db.Model(&SwiftCode{}).Where("Code LIKE ? AND parent_swift_code is NULL", sc.Code[:8]+"%").Update("parent_swift_code", sc.Code).Error; err != nil {

		}

	}

	return nil

}

func (sc *SwiftCode) RemoveConnections(db *gorm.DB) error {

	if sc.IsHeadquarter {

		if err := db.Model(&SwiftCode{}).Where("parent_swift_code = ?", sc.Code).Update("parent_swift_code", nil).Error; err != nil {

		}

	}

	return nil
}
