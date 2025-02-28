package main

import (
	"fmt"
	"swift-remitly-app/models"

	"github.com/gin-gonic/gin"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)

func main() {

	dsn := "root:qwertyuiop@tcp(127.0.0.1:3306)/remitly"

	db, err := gorm.Open(mysql.Open(dsn), &gorm.Config{})

	if err != nil {
		fmt.Println("Błąd połączenia z bazą danych:", err)
		return
	}

	err = db.AutoMigrate(&models.SwiftCode{}, &models.Country{}, &models.Bank{})

	if err != nil {
		fmt.Println("Bład migracji:", err)
		return
	}

	router := gin.Default()

	router.GET("/country/:iso2", func(c *gin.Context) {

		iso2 := c.Param("iso2")

		var country models.Country
		if err := db.Preload("SwiftCodes", func(db *gorm.DB) *gorm.DB {
			return db.Select("CODE", "Address", "IsHeadquarter", "BankID", "CountryID") // Wybieramy tylko wymagane pola
		}).Where("ISO2 = ?", iso2).First(&country).Error; err != nil {
			c.JSON(500, gin.H{"message": "Coudn't find country"})
			return
		}

		c.JSON(200, country)
	})

	router.Run("localhost:8080")
}
