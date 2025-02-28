package models

type Bank struct {
	Name string `gorm:"primaryKey;size:100;not null"`
}
