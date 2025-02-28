package models

type SwiftCode struct {
	CODE            string `gorm:"primaryKey;size:11"`
	Address         string `gorm:"size:255"`
	IsHeadquarter   bool   `gorm:"not null"`
	CountryID       *string
	Country         Country `gorm:"foreignKey:CountryID;references:ISO2"`
	BankID          *string
	Bank            *Bank       `gorm:"foreignKey:BankID;references:Name"`
	ParentSwiftCode *string     `gorm:"size:11"`
	ParentSwift     *SwiftCode  `gorm:"foreignKey:ParentSwiftCode"`
	Branches        []SwiftCode `gorm:"foreignKey:ParentSwiftCode"`
}
