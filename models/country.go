package models

type Country struct {
	ISO2       string      `gorm:"primaryKey;size:2"`
	Name       string      `gorm:"size:100;not null"`
	SwiftCodes []SwiftCode `gorm:"foreignKey:CountryID"`
}
